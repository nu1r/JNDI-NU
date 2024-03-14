package com.qi4l.jndi.gadgets.utils.jre;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCClassDesc extends ReferencableObject implements SerializedElement {
    private String className;

    private long serialVersionUID;

    private byte classDescFlags;

    private List<Field> fields = new ArrayList<Field>();

    protected TCClassDesc() {
    }

    public TCClassDesc(String className) throws Exception {
        this(className, -1L, (byte) 0);
    }

    public TCClassDesc(String className, long serialVersionUID) throws Exception {
        this(className, serialVersionUID, (byte) 0);
    }

    public TCClassDesc(String className, byte classDescFlags) throws Exception {
        this(className, -1L, classDescFlags);
    }

    public TCClassDesc(String className, long serialVersionUID, byte classDescFlags) throws Exception {
        this.className = className;
        this.serialVersionUID = (serialVersionUID != -1L) ? serialVersionUID : getSerialVersionUID();
        this.classDescFlags = (classDescFlags != 0) ? classDescFlags : getClassDescFlags();
    }

    public int getFieldsCount() {
        return this.fields.size();
    }

    private long getSerialVersionUID() throws Exception {
        Class<?>                cls = Class.forName(this.className);
        java.lang.reflect.Field f   = cls.getDeclaredField("serialVersionUID");
        if (f == null)
            return -1L;
        f.setAccessible(true);
        return Long.valueOf(f.get((Object) null).toString()).longValue();
    }

    private byte getClassDescFlags() throws Exception {
        Class<?> cls = Class.forName(this.className);
        byte     b   = 0;
        if (Serializable.class.isAssignableFrom(cls))
            b = (byte) (b | 0x2);
        try {
            if (cls.getDeclaredMethod("writeObject", new Class[]{ObjectOutputStream.class}) != null)
                b = (byte) (b | 0x1);
        } catch (Exception exception) {
        }
        return b;
    }

    public boolean hasWriteObject() {
        return ((this.classDescFlags & 0x1) != 0);
    }

    public TCClassDesc addField(Field field) {
        this.fields.add(field);
        return this;
    }

    public void write(DataOutputStream out, HandleContainer handles) throws Exception {
        if (handles.getHandle(this) != -1) {
            TCReference reference = new TCReference(handles.getHandle(this));
            reference.write(out, handles);
            throw new Exception("stop");
        }
        super.write(out, handles);
    }

    public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {
        out.writeByte(114);
        out.writeUTF(this.className);
        out.writeLong(this.serialVersionUID);
        out.writeByte(this.classDescFlags);
        out.writeShort(this.fields.size());
        handles.putHandle(getHandleObject());
        for (Field field : this.fields)
            field.write(out, handles);
        out.writeByte(120);
    }

    public static class Field implements SerializedElement {

        private String name;

        private Class type;

        public Field(String name, Class type) {
            this.name = name;
            this.type = type;
        }

        private byte getTypeByte() throws Exception {
            Map<Class<?>, Byte> bytes = new HashMap<Class<?>, Byte>();
            bytes.put(byte.class, Byte.valueOf((byte) 66));
            bytes.put(char.class, Byte.valueOf((byte) 67));
            bytes.put(double.class, Byte.valueOf((byte) 68));
            bytes.put(float.class, Byte.valueOf((byte) 70));
            bytes.put(int.class, Byte.valueOf((byte) 73));
            bytes.put(long.class, Byte.valueOf((byte) 74));
            bytes.put(short.class, Byte.valueOf((byte) 83));
            bytes.put(boolean.class, Byte.valueOf((byte) 90));
            Byte b = bytes.get(this.type);
            if (b == null)
                b = Byte.valueOf((byte) 76);
            return b.byteValue();
        }

        public void write(DataOutputStream out, HandleContainer handles) throws Exception {
            byte b = getTypeByte();
            out.writeByte(b);
            out.writeUTF(this.name);
            if (b == 76) {
                TCString s = TCString.getInstance((char) b + this.type.getName().replace('.', '/') + ";");
                s.write(out, handles);
            }
        }
    }
}
