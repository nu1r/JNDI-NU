package com.qi4l.jndi.gadgets.utils.handle;

import com.qi4l.jndi.gadgets.Config.Config;
import javassist.CtClass;
import javassist.CtField;

public class ClassFieldHandler {
    public static void insertField(CtClass ctClass, String fieldName, String fieldCode) throws Exception {
        ctClass.defrost();
        try {
            CtField field = ctClass.getDeclaredField(fieldName);
            ctClass.removeField(field);
        } catch (javassist.NotFoundException ignored) {
        }
        ctClass.addField(CtField.make(fieldCode, ctClass));
    }


    /**
     * 将 Field String 转一层，实际用处不大
     *
     * @param target Field String 值
     * @return 替换后的 String
     */
    public static String converString(String target) {
        if (Config.IS_OBSCURE) {
            StringBuilder result = new StringBuilder("new String(new byte[]{");
            byte[]        bytes  = target.getBytes();
            for (int i = 0; i < bytes.length; i++) {
                result.append(bytes[i]).append(",");
            }
            return result.substring(0, result.length() - 1) + "})";
        }

        return "\"" + target + "\"";
    }

    /**
     * 只有在原 Class 已经有此 Field 的情况下才加入
     *
     * @param ctClass   CtClass
     * @param fieldName field 名称
     * @param fieldCode field 代码
     * @throws Exception 抛出异常
     */
    public static void insertFieldIfExists(CtClass ctClass, String fieldName, String fieldCode) throws Exception {
        ctClass.defrost();
        try {
            CtField field = ctClass.getDeclaredField(fieldName);
            ctClass.removeField(field);
            ctClass.addField(CtField.make(fieldCode, ctClass));
        } catch (javassist.NotFoundException ignored) {
        }
    }
}
