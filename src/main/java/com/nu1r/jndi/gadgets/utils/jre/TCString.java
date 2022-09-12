package com.nu1r.jndi.gadgets.utils.jre;

import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

public class TCString extends ReferencableObject implements SerializedElement {

	public Object getHandleObject() {
		return this.content;
	}

	private static Map<String, TCString> instances = new HashMap<String, TCString>();

	private String content;

	private TCString(String content) {
		this.content = content;
	}

	public static TCString getInstance(String content) {
		TCString ins = instances.get(content);
		if (ins != null)
			return ins;
		ins = new TCString(content);
		instances.put(content, ins);
		return ins;
	}

	public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {
		out.writeByte(116);
		out.writeUTF(this.content);
	}

	public static void main(String[] args) throws Exception {
	}
}
