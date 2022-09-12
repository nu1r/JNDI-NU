package com.nu1r.jndi.gadgets.utils.jre;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class TCBlockData implements SerializedElement {

	private ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

	private DataOutputStream out = new DataOutputStream(this.byteOut);

	public void append(Object data) throws Exception {
		if (data instanceof Integer) {
			this.out.writeInt(((Integer) data).intValue());
		} else if (data instanceof Short) {
			this.out.writeShort(((Short) data).shortValue());
		} else if (data instanceof Long) {
			this.out.writeLong(((Long) data).longValue());
		} else if (data instanceof Byte) {
			this.out.writeByte(((Byte) data).byteValue());
		} else if (data instanceof Character) {
			this.out.writeChar(((Character) data).charValue());
		} else if (data instanceof char[]) {
			this.out.writeChars(new String((char[]) data));
		} else if (data instanceof String) {
			this.out.writeUTF((String) data);
		} else if (data instanceof Float) {
			this.out.writeFloat(((Float) data).floatValue());
		} else if (data instanceof Double) {
			this.out.writeDouble(((Double) data).doubleValue());
		} else if (data instanceof Boolean) {
			this.out.writeBoolean(((Boolean) data).booleanValue());
		}
	}

	public void write(DataOutputStream out, HandleContainer handles) throws Exception {
		out.writeByte(119);
		byte[] data = this.byteOut.toByteArray();
		out.writeByte(data.length);
		out.write(data);
		this.byteOut.close();
		this.out.close();
	}
}
