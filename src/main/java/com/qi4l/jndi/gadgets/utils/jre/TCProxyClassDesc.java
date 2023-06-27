package com.qi4l.jndi.gadgets.utils.jre;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TCProxyClassDesc extends TCClassDesc implements SerializedElement {
    private List<Class> interfaces = (List) new ArrayList<Class<?>>();

    public TCProxyClassDesc addInterface(Class cls) {
        this.interfaces.add(cls);
        return this;
    }

    public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {
        out.writeByte(125);
        out.writeInt(this.interfaces.size());
        for (Class intf : this.interfaces)
            out.writeUTF(intf.getName());
        out.writeByte(120);
    }
}
