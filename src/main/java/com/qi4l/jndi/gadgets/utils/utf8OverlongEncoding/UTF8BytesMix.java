package com.qi4l.jndi.gadgets.utils.utf8OverlongEncoding;

public class UTF8BytesMix {
    public static byte[] resultBytes = new byte[0];
    public static byte[] originalBytes = new byte[0];

    // 加密字节位数
    public static int type = 2; //3

    // 原 byte[] 坐标
    public static int index = 0;

    final static byte TC_CLASSDESC = (byte) 0x72;
    final static byte TC_PROXYCLASSDESC = (byte) 0x7d;
    final static byte TC_STRING = (byte) 0x74;
    final static byte TC_REFERENCE = (byte) 0x71;
    final static byte TC_LONGSTRING = (byte) 0x7C;
    final static byte TC_ARRAY = (byte) 0x75;
    final static byte TC_ENDBLOCKDATA = (byte) 0x78;
    final static byte TC_NULL = (byte) 0x70;


    final static byte Byte = (byte) 0x42;
    final static byte Char = (byte) 0x43;
    final static byte Double = (byte) 0x44;
    final static byte Float = (byte) 0x46;
    final static byte Integer = (byte) 0x49;
    final static byte Long = (byte) 0x4a;
    final static byte Object_L = (byte) 0x4c;
    final static byte Short = (byte) 0x53;
    final static byte Boolean = (byte) 0x5a;
    final static byte Array = (byte) 0x5b;


    public UTF8BytesMix(byte[] originalBytes) {
        this.originalBytes = originalBytes;
    }

    public static byte[] builder() {
        while (index < originalBytes.length) {
            byte b = originalBytes[index];
            byteAdd(b);

            if (b == TC_CLASSDESC) {
                changeTC_CLASSDESC();
            } else if (b == TC_PROXYCLASSDESC) {
                changeTC_PROXYCLASSDESC();
            } else if (b == TC_STRING) {
                changeTC_STRING();
            }

            index++;
        }
        return resultBytes;
    }

    public static void changeTC_PROXYCLASSDESC() {
        int interfaceCount = ((originalBytes[index + 1] & 0xFF) << 24) |
                ((originalBytes[index + 2] & 0xFF) << 16) |
                ((originalBytes[index + 3] & 0xFF) << 8) |
                (originalBytes[index + 4] & 0xFF);
        if (interfaceCount > 0xff || interfaceCount < 0x00)
            return;

        for (int i = 0; i < 4; i++) {
            byteAdd(originalBytes[index + 1]);
            index++;
        }

        int length = ((originalBytes[index + 1] & 0xFF) << 8) | (originalBytes[index + 2] & 0xFF);
        byte[] originalValue = new byte[length];
        System.arraycopy(originalBytes, index + 3, originalValue, 0, length);
        index += 3 + length;

        encode(originalValue, type);
        index--;
    }


    public static boolean changeTC_CLASSDESC() {
        /**
         * 类信息
         */
        boolean isTC_CLASSDESC = changeTC_STRING();
        if (!isTC_CLASSDESC) {
            return false;
        }
        index++;

        /**
         * SerialVersionUID + ClassDescFlags
         */
        byte[] serialVersionUID = new byte[9];
        System.arraycopy(originalBytes, index, serialVersionUID, 0, 9);
        for (int i = 0; i < serialVersionUID.length; i++) {
            byteAdd(serialVersionUID[i]);
        }
        index += 9;

        /**
         * FieldCount
         */
        byte[] fieldCount = new byte[2];
        System.arraycopy(originalBytes, index, fieldCount, 0, 2);
        for (int i = 0; i < fieldCount.length; i++) {
            byteAdd(fieldCount[i]);
        }
        int fieldCounts = ((fieldCount[0] & 0xFF) << 8) | (fieldCount[1] & 0xFF);
        index += 2;

        for (int i = 0; i < fieldCounts; i++) {
            boolean isFiledOver = false;

            /**
             * FieldName
             */
            if (originalBytes[index] == Byte
                    || originalBytes[index] == Char
                    || originalBytes[index] == Double
                    || originalBytes[index] == Float
                    || originalBytes[index] == Integer
                    || originalBytes[index] == Long
                    || originalBytes[index] == Object_L
                    || originalBytes[index] == Short
                    || originalBytes[index] == Boolean
                    || originalBytes[index] == Array) {
                // Object
                byteAdd(originalBytes[index]);
                index++;

                int fieldLength = ((originalBytes[index] & 0xFF) << 8) | (originalBytes[index + 1] & 0xFF);
                byte[] originalFieldName = new byte[fieldLength];
                System.arraycopy(originalBytes, index + 2, originalFieldName, 0, fieldLength);
                index += 2 + fieldLength;
                encode(originalFieldName, type);
            }

            /**
             * Class Name
             *
             * 也规避了这种情况
             *          Index 0:
             *           Integer - I - 0x49
             *           @FieldName
             *             @Length - 4 - 0x00 04
             *             @Value - size - 0x73 69 7a 65
             */
            // TC_STRING 0x74
            if (originalBytes[index] == TC_STRING) {

                byteAdd(originalBytes[index]);
                index++;

                int classLength = ((originalBytes[index] & 0xFF) << 8) | (originalBytes[index + 1] & 0xFF);
                byte[] originalClassName = new byte[classLength];
                System.arraycopy(originalBytes, index + 2, originalClassName, 0, classLength);
                index += 2 + classLength;
                encode(originalClassName, type);
                isFiledOver = true;
            } else if (originalBytes[index] == TC_REFERENCE) {
                /**
                 * Index 0:
                 * Object - L - 0x4c
                 * @FieldName
                 * @Length - 9 - 0x00 09
                 * @Value - decorated - 0x64 65 63 6f 72 61 74 65 64
                 * @ClassName
                 *         TC_REFERENCE - 0x71
                 * @Handler - 8257537 - 0x00 7e 00 01
                 */
                byte[] reference = new byte[5];
                System.arraycopy(originalBytes, index, reference, 0, 5);
                for (int j = 0; j < reference.length; j++) {
                    byteAdd(reference[j]);
                }
                index += 5;
                isFiledOver = true;
            }

            // todo 看看其他可能未识别到的类型
//            if(i < fieldCounts - 1 && !isFiledOver) {
//                while (true) {
//                    if (!isField(originalBytes, index)) {
//                        byteAdd(originalBytes[index]);
//                        index++;
//                    } else {
//                        break;
//                    }
//                }
//            }

        }

        // 循环需要
        index--;
        return true;
    }

    public static boolean changeTC_STRING() {
        int length = ((originalBytes[index + 1] & 0xFF) << 8) | (originalBytes[index + 2] & 0xFF);
        // 溢出
        if (length > 0xff || length < 0x00)
            return false;

        // 原始内容
        byte[] originalValue = new byte[length];
        System.arraycopy(originalBytes, index + 3, originalValue, 0, length);
        // 非全部可见字符，可能存在的报错，不继续执行
        if (!isByteVisible(originalValue)) {
            return false;
        }

        index += 3 + length;
        encode(originalValue, type);

        index--;
        return true;
    }


    public static boolean isField(byte[] checkBytes, int index) {
        if (!(checkBytes[index] == Byte
                || checkBytes[index] == Char
                || checkBytes[index] == Double
                || checkBytes[index] == Float
                || checkBytes[index] == Integer
                || checkBytes[index] == Long
                || checkBytes[index] == Object_L
                || checkBytes[index] == Short
                || checkBytes[index] == Boolean
                || checkBytes[index] == Array)) {
            return false;
        }

        int length = ((checkBytes[index + 1] & 0xFF) << 8) | (checkBytes[index + 2] & 0xFF);
        if (length > 0xff || length < 0x00)
            return false;
        byte[] lengthBytes = new byte[length];
        try {
            System.arraycopy(checkBytes, index + 3, lengthBytes, 0, length);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 加密
     *
     * @return
     */
    public static void encode(byte[] originalValue, int type) {
        if (type == 3) {
            // 3 byte format: 1110xxxx 10xxxxxx 10xxxxxx
            int newLength = originalValue.length * 3;

            byteAdd((byte) ((newLength >> 8) & 0xFF));
            byteAdd((byte) (newLength & 0xFF));

            for (int i = 0; i < originalValue.length; i++) {
                char c = (char) originalValue[i];
                byteAdd((byte) (0xE0 | ((c >> 12) & 0x0F)));
                byteAdd((byte) (0x80 | ((c >> 6) & 0x3F)));
                byteAdd((byte) (0x80 | ((c >> 0) & 0x3F)));
            }

        } else {
            // 2 byte format: 110xxxxx 10xxxxxx
            int newLength = originalValue.length * 2;

            byteAdd((byte) ((newLength >> 8) & 0xFF));
            byteAdd((byte) (newLength & 0xFF));

            for (int i = 0; i < originalValue.length; i++) {
                char c = (char) originalValue[i];
                byteAdd((byte) (0xC0 | ((c >> 6) & 0x1F)));
                byteAdd((byte) (0x80 | ((c >> 0) & 0x3F)));
            }
        }


    }

    /**
     * 判断字节是否在可见字符的 ASCII 范围内
     *
     * @param bytes
     * @return
     */
    public static boolean isByteVisible(byte[] bytes) {
        for (byte b : bytes) {
            if (b < 32 || b > 126) {
                return false;
            }
        }
        return true;
    }

    public static void byteAdd(byte b) {
        byte[] newBytes = new byte[resultBytes.length + 1];
        System.arraycopy(resultBytes, 0, newBytes, 0, resultBytes.length);
        newBytes[resultBytes.length] = b;
        resultBytes = newBytes;
    }
}
