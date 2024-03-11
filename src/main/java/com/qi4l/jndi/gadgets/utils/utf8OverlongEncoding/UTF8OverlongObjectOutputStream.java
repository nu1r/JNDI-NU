package com.qi4l.jndi.gadgets.utils.utf8OverlongEncoding;

import com.qi4l.jndi.gadgets.utils.Reflections;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class UTF8OverlongObjectOutputStream extends ObjectOutputStream {
    public static HashMap<Character, int[]> map = new HashMap<Character, int[]>() {{
        put('.', new int[]{0xc0, 0xae});
        put(';', new int[]{0xc0, 0xbb});
        put('$', new int[]{0xc0, 0xa4});
        put('[', new int[]{0xc1, 0x9b});
        put(']', new int[]{0xc1, 0x9d});
        put('a', new int[]{0xc1, 0xa1});
        put('b', new int[]{0xc1, 0xa2});
        put('c', new int[]{0xc1, 0xa3});
        put('d', new int[]{0xc1, 0xa4});
        put('e', new int[]{0xc1, 0xa5});
        put('f', new int[]{0xc1, 0xa6});
        put('g', new int[]{0xc1, 0xa7});
        put('h', new int[]{0xc1, 0xa8});
        put('i', new int[]{0xc1, 0xa9});
        put('j', new int[]{0xc1, 0xaa});
        put('k', new int[]{0xc1, 0xab});
        put('l', new int[]{0xc1, 0xac});
        put('m', new int[]{0xc1, 0xad});
        put('n', new int[]{0xc1, 0xae});
        put('o', new int[]{0xc1, 0xaf}); // 0x6f
        put('p', new int[]{0xc1, 0xb0});
        put('q', new int[]{0xc1, 0xb1});
        put('r', new int[]{0xc1, 0xb2});
        put('s', new int[]{0xc1, 0xb3});
        put('t', new int[]{0xc1, 0xb4});
        put('u', new int[]{0xc1, 0xb5});
        put('v', new int[]{0xc1, 0xb6});
        put('w', new int[]{0xc1, 0xb7});
        put('x', new int[]{0xc1, 0xb8});
        put('y', new int[]{0xc1, 0xb9});
        put('z', new int[]{0xc1, 0xba});
        put('A', new int[]{0xc1, 0x81});
        put('B', new int[]{0xc1, 0x82});
        put('C', new int[]{0xc1, 0x83});
        put('D', new int[]{0xc1, 0x84});
        put('E', new int[]{0xc1, 0x85});
        put('F', new int[]{0xc1, 0x86});
        put('G', new int[]{0xc1, 0x87});
        put('H', new int[]{0xc1, 0x88});
        put('I', new int[]{0xc1, 0x89});
        put('J', new int[]{0xc1, 0x8a});
        put('K', new int[]{0xc1, 0x8b});
        put('L', new int[]{0xc1, 0x8c});
        put('M', new int[]{0xc1, 0x8d});
        put('N', new int[]{0xc1, 0x8e});
        put('O', new int[]{0xc1, 0x8f});
        put('P', new int[]{0xc1, 0x90});
        put('Q', new int[]{0xc1, 0x91});
        put('R', new int[]{0xc1, 0x92});
        put('S', new int[]{0xc1, 0x93});
        put('T', new int[]{0xc1, 0x94});
        put('U', new int[]{0xc1, 0x95});
        put('V', new int[]{0xc1, 0x96});
        put('W', new int[]{0xc1, 0x97});
        put('X', new int[]{0xc1, 0x98});
        put('Y', new int[]{0xc1, 0x99});
        put('Z', new int[]{0xc1, 0x9a});
    }};

    public UTF8OverlongObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeClassDescriptor(ObjectStreamClass desc) {
        try {
            String name = desc.getName();
            writeShort(name.length() * 2);
            try {
                for (int i = 0; i < name.length(); i++) {
                    char s = name.charAt(i);
                    write(map.get(s)[0]);
                    write(map.get(s)[1]);
                }
            } catch (Exception e) {

            }
            writeLong(desc.getSerialVersionUID());
            byte flags = 0;
            if ((Boolean) Reflections.getFieldValue(desc, "externalizable")) {
                flags |= ObjectStreamConstants.SC_EXTERNALIZABLE;
                Field protocolField = ObjectOutputStream.class.getDeclaredField("protocol");
                protocolField.setAccessible(true);
                int protocol = (Integer) protocolField.get(this);
                if (protocol != ObjectStreamConstants.PROTOCOL_VERSION_1) {
                    flags |= ObjectStreamConstants.SC_BLOCK_DATA;
                }
            } else if ((Boolean) Reflections.getFieldValue(desc, "serializable")) {
                flags |= ObjectStreamConstants.SC_SERIALIZABLE;
            }
            if ((Boolean) Reflections.getFieldValue(desc, "hasWriteObjectData")) {
                flags |= ObjectStreamConstants.SC_WRITE_METHOD;
            }
            if ((Boolean) Reflections.getFieldValue(desc, "isEnum")) {
                flags |= ObjectStreamConstants.SC_ENUM;
            }
            writeByte(flags);
            ObjectStreamField[] fields = (ObjectStreamField[]) Reflections.getFieldValue(desc, "fields");
            writeShort(fields.length);
            for (int i = 0; i < fields.length; i++) {
                ObjectStreamField f = fields[i];
                writeByte(f.getTypeCode());
                writeUTF(f.getName());
                if (!f.isPrimitive()) {
                    Method writeTypeString = ObjectOutputStream.class.getDeclaredMethod("writeTypeString", String.class);
                    writeTypeString.setAccessible(true);
                    writeTypeString.invoke(this, f.getTypeString());
//                    writeTypeString(f.getTypeString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
