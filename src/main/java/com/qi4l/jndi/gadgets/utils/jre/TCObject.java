package com.qi4l.jndi.gadgets.utils.jre;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TCObject extends ReferencableObject implements SerializedElement {
    private Serialization ser;

    private List<ClassDescData> descData;

    public TCObject(Serialization ser) {
        this.descData = new ArrayList<ClassDescData>();
        this.ser = ser;
    }

    public int size() {
        return this.descData.size();
    }

    public TCObject addClassDescData(TCClassDesc desc, ObjectData data) throws Exception {
        return addClassDescData(desc, data, false);
    }

    public TCObject addClassDescData(TCClassDesc desc, ObjectData data, boolean ignoreEquality) throws Exception {
        if (!ignoreEquality &&
                desc.getFieldsCount() != data.size())
            throw new Exception("not enough fields/data, fields count: " + desc.getFieldsCount() + ", data count: " + data.size());
        data.setSer(this.ser);
        this.descData.add(new ClassDescData(desc, data));
        return this;
    }

    protected void writeHeader(DataOutputStream out, HandleContainer handles) throws Exception {
        out.writeByte(115);
    }

    protected void writeClassDescs(DataOutputStream out, HandleContainer handles) throws Exception {
        try {
            for (int i = 0; i < this.descData.size(); i++) {
                ClassDescData d = this.descData.get(i);
                d.getDesc().write(out, handles);
            }
            out.writeByte(112);
        } catch (Exception e) {
            if (!e.getMessage().equals("stop"))
                e.printStackTrace();
        }
    }

    protected void writeClassData(DataOutputStream out, HandleContainer handles) throws Exception {
        for (int i = this.descData.size() - 1; i >= 0; i--) {
            ClassDescData d = this.descData.get(i);
            d.getData().write(out, handles);
            if (d.getDesc().hasWriteObject()) {
                this.ser.writeBlockData(out, handles);
                out.writeByte(120);
            }
        }
    }

    public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {
        writeHeader(out, handles);
        writeClassDescs(out, handles);
        handles.putHandle(getHandleObject());
        writeClassData(out, handles);
    }

    private static class ClassDescData {

        private TCClassDesc desc;

        private TCObject.ObjectData data;

        public ClassDescData(TCClassDesc desc, TCObject.ObjectData data) {
            this.desc = desc;
            this.data = data;
        }

        public TCClassDesc getDesc() {
            return this.desc;
        }

        public TCObject.ObjectData getData() {
            return this.data;
        }
    }

    public static class ObjectData implements SerializedElement {

        private class Data {

            private boolean block;

            private Object data;

            public Data(boolean block, Object data) {
                this.block = block;
                this.data = data;
            }
        }

        private List<Data> data = new ArrayList<Data>();

        private Serialization ser;

        public void setSer(Serialization ser) {
            this.ser = ser;
        }

        public int size() {
            return this.data.size();
        }

        public void write(DataOutputStream out, HandleContainer handles) throws Exception {
            for (Data d : this.data)
                this.ser.treatObject(out, d.data, handles, d.block);
        }

        public ObjectData addData(Object obj) {
            addData(obj, false);
            return this;
        }

        public ObjectData addData(Object obj, boolean block) {
            this.data.add(new Data(block, obj));
            return this;
        }
    }
}
