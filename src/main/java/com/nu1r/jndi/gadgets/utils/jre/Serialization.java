package com.nu1r.jndi.gadgets.utils.jre;


import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Serialization {

	private List<Data> objects = new ArrayList<Data>();

	private Object handle;

	private TCBlockData blockData;

	public Serialization() {
		try {
			ObjectOutputStream output = new ObjectOutputStream(new ByteOutputStream());
			Field              f      = output.getClass().getDeclaredField("handles");
			f.setAccessible(true);
			this.handle = f.get(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addObject(Object obj) throws Exception {
		addObject(obj, false);
	}

	public void addObject(Object obj, boolean block) throws Exception {
		if (obj instanceof TCObject) {
			TCObject tco = (TCObject) obj;
			if (tco.size() == 0)
				throw new Exception("no class_desc/data in TCObject");
		}
		this.objects.add(new Data(block, obj));
	}

	public void write(String path) throws Exception {
		write(new File(path));
	}

	public void write(File path) throws Exception {
		write(new FileOutputStream(path));
	}

	public void write(OutputStream o) throws Exception {
		if (this.objects.size() == 0)
			throw new Exception("no objects in serialization");
		DataOutputStream out = new DataOutputStream(o);
		out.writeShort(-21267);
		out.writeShort(5);
		HandleContainer handles = new HandleContainer(this.handle);
		for (Data data : this.objects) {
			if (!data.block)
				writeBlockData(out, handles);
			Object obj = data.data;
			if (obj instanceof SerializedElement) {
				((SerializedElement) obj).write(out, handles);
				continue;
			}
			treatObject(out, obj, handles, data.block);
		}
		writeBlockData(out, handles);
		out.close();
	}

	protected void writeBlockData(DataOutputStream out, HandleContainer handles) throws Exception {
		if (this.blockData != null) {
			this.blockData.write(out, handles);
			this.blockData = null;
		}
	}

	public void treatObject(DataOutputStream out, Object obj, HandleContainer handles, boolean blockData) throws Exception {
		if (blockData) {
			if (this.blockData == null)
				this.blockData = new TCBlockData();
			this.blockData.append(obj);
			return;
		}
		writeBlockData(out, handles);
		if (obj instanceof Byte) {
			out.writeByte(((Byte) obj).byteValue());
		} else if (obj instanceof Short) {
			out.writeShort(((Short) obj).shortValue());
		} else if (obj instanceof Integer) {
			out.writeInt(((Integer) obj).intValue());
		} else if (obj instanceof Long) {
			out.writeLong(((Long) obj).longValue());
		} else if (obj instanceof Float) {
			out.writeFloat(((Float) obj).floatValue());
		} else if (obj instanceof Double) {
			out.writeDouble(((Double) obj).doubleValue());
		} else if (obj instanceof Character) {
			out.writeChar(((Character) obj).charValue());
		} else if (obj instanceof String || obj instanceof TCString) {
			TCString s = (obj instanceof TCString) ? (TCString) obj : TCString.getInstance(obj.toString());
			s.write(out, handles);
		} else if (obj instanceof TCObject) {
			TCObject o = (TCObject) obj;
			o.write(out, handles);
		} else {
			ByteArrayOutputStream byteout = new ByteArrayOutputStream();
			ObjectOutputStream    objout  = getPatchedOutputStream(byteout);
			TCJavaObject          o       = new TCJavaObject(obj, byteout, objout);
			o.write(out, handles);
		}
	}

	private static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
		Field f = obj.getClass().getDeclaredField(fieldName);
		f.setAccessible(true);
		if (Modifier.isFinal(f.getModifiers())) {
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & 0xFFFFFFEF);
		}
		f.set(obj, value);
	}

	private ObjectOutputStream getPatchedOutputStream(ByteArrayOutputStream out) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		setFieldValue(oos, "handles", this.handle);
		return oos;
	}

	private class Data {

		private boolean block;

		private Object data;

		public Data(boolean block, Object data) {
			this.block = block;
			this.data = data;
		}
	}
}
