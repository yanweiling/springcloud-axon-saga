package com.ywl.study.proxy;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ROUTE_TYPE;

public class HttpMethodZuulFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    /*是否启用过滤器的条件*/
    @Override
    public boolean shouldFilter() {
        //当前缀是order，请求是get的时候启动filter，并转发到order-query上面去
        RequestContext context=RequestContext.getCurrentContext();
        HttpServletRequest request=context.getRequest();
        String method=request.getMethod();
        String uri=request.getRequestURI();
        return "GET".equals(method.toUpperCase()) && uri.startsWith("/order");
    }

    @Override
    public Object run() {
        RequestContext context=RequestContext.getCurrentContext();
        context.set("serviceId","order-query");
        context.setRouteHost(null);
        return null;
    }
}
