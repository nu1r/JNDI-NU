package com.nu1r.jndi.gadgets.utils.jre;

import java.io.DataOutputStream;

public class TCNull implements SerializedElement {

	public void write(DataOutputStream out, HandleContainer handles) throws Exception {
		out.writeByte(112);
	}
}
