package com.qi4l.jndi.gadgets;

import com.qi4l.jndi.LdapServer;
import com.qi4l.jndi.enumtypes.PayloadType;
import com.qi4l.jndi.gadgets.utils.JavaVersion;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public interface ObjectPayload<T> {
    /*
     * return armed payload object to be serialized that will execute specified
     * command on deserialization
     */
    public T getObject(PayloadType type, String... param) throws Exception;

    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isAnnInvHUniversalMethodImpl();
    }

    public static class Utils {

        // get payload classes by classpath scanning
        public static Set<Class<? extends ObjectPayload>> getPayloadClasses() {
            final Reflections                         reflections  = new Reflections(ObjectPayload.class.getPackage().getName());
            final Set<Class<? extends ObjectPayload>> payloadTypes = reflections.getSubTypesOf(ObjectPayload.class);
            for (Iterator<Class<? extends ObjectPayload>> iterator = payloadTypes.iterator(); iterator.hasNext(); ) {
                Class<? extends ObjectPayload> pc = iterator.next();
                if (pc.isInterface() || Modifier.isAbstract(pc.getModifiers())) {
                    iterator.remove();
                }
            }
            return payloadTypes;
        }


        @SuppressWarnings("unchecked")
        public static Class<? extends ObjectPayload> getPayloadClass(final String className) {
            Class<? extends ObjectPayload> clazz = null;
            try {
                clazz = (Class<? extends ObjectPayload>) Class.forName(className);
            } catch (Exception ignored) {
            }
            if (clazz == null) {
                try {
                    return clazz = (Class<? extends ObjectPayload>) Class
                            .forName(LdapServer.class.getPackage().getName() + ".gadgets." + className);
                } catch (Exception ignored) {
                }
            }
            if (clazz != null && !ObjectPayload.class.isAssignableFrom(clazz)) {
                clazz = null;
            }
            return clazz;
        }

        @SuppressWarnings("unchecked")
        public static void releasePayload(ObjectPayload payload, Object object) throws Exception {
            if (payload instanceof ReleaseableObjectPayload) {
                ((ReleaseableObjectPayload) payload).release(object);
            }
        }


        public static void releasePayload(String payloadType, Object payloadObject) {
            final Class<? extends ObjectPayload> payloadClass = getPayloadClass(payloadType);
            if (payloadClass == null || !ObjectPayload.class.isAssignableFrom(payloadClass)) {
                throw new IllegalArgumentException("Invalid payload type '" + payloadType + "'");

            }

            try {
                final ObjectPayload payload = payloadClass.newInstance();
                releasePayload(payload, payloadObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //生成随机字符
        public static String generateRandomString(int length) {
            String        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder sb         = new StringBuilder();

            Random random = new Random();
            for (int i = 0; i < length; i++) {
                int  index      = random.nextInt(characters.length());
                char randomChar = characters.charAt(index);
                sb.append(randomChar);
            }

            return sb.toString();
        }
    }
}
