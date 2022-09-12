package com.nu1r.jndi.gadgets.utils.jre;

import java.io.DataOutputStream;

public abstract class ReferencableObject {

	public Object getHandleObject() {
		return this;
	}

	public void write(DataOutputStream out, HandleContainer handles) throws Exception {
		if (handles.getHandle(getHandleObject()) != -1) {
			TCReference reference = new TCReference(handles.getHandle(getHandleObject()));
			reference.write(out, handles);
		} else {
			doWrite(out, handles);
			handles.putHandle(getHandleObject());
		}
	}

	public abstract void doWrite(DataOutputStream paramDataOutputStream, com.nu1r.jndi.gadgets.utils.jre.HandleContainer paramHandleContainer) throws Exception;
}
