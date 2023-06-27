package com.qi4l.jndi.gadgets.annotation;

import com.qi4l.jndi.gadgets.utils.Reflections;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependencies {


    String[] value() default {};

    public static class Utils {

        public static String[] getDependencies(AnnotatedElement annotated) {
            Dependencies deps = annotated.getAnnotation(Dependencies.class);
            if (deps != null && deps.value() != null) {
                return deps.value();
            } else {
                try {
                    Class  name = Class.forName(Reflections.getFieldValue(annotated, "name").toString());
                    Method m    = name.getDeclaredMethod("getDependencies");
                    m.setAccessible(true);
                    return (String[]) m.invoke(null);
                } catch (Exception ignored) {
                    return new String[0];
                }
            }
        }

        public static String[] getDependenciesSimple(AnnotatedElement annotated) {
            String[] deps   = getDependencies(annotated);
            String[] simple = new String[deps.length];
            for (int i = 0; i < simple.length; i++) {
                simple[i] = deps[i].split(":", 2)[1];
            }
            return simple;
        }
    }
}
