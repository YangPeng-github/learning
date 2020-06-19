package com.pengyang.learning.filter;

import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器filter
 */
@Order(2)
@WebFilter(filterName = "AuthFilter",urlPatterns = "/*", initParams = @WebInitParam(name = "url",value = "/login,/logout"))
public class AuthFilter implements Filter {
    private String filterName;
    private String [] urls;

    /**
     * 初始化
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //和我们编写的Servlet程序一样，Filter的创建和销毁由WEB服务器负责。
        // web 应用程序启动时，web 服务器将创建Filter 的实例对象，并调用其init方法，读取web.xml配置，完成对象的初始化功能.
        // 从而为后续的用户请求作好拦截的准备工作（filter对象只会创建一次，init方法也只会执行一次）。
        // 开发人员通过init方法的参数，可获得代表当前filter配置信息的FilterConfig对象
        this.filterName = filterConfig.getFilterName();
        this.urls = filterConfig.getInitParameter("url") == null? null : filterConfig.getInitParameter("url").split(",");
        System.out.println("初始化过滤器："+filterName+"，过滤白名单："+filterConfig.getInitParameter("url"));
    }

    /**
     * 拦截请求
     * @param request
     * @param response
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String url = httpServletRequest.getRequestURI();
        if (isWhiteUrl(url)) {
            filterChain.doFilter(request,response);
            System.out.println("请求在白名单中，不过滤："+url);
        }else {
            if (!isLogin(httpServletRequest)) {
                System.out.println("没有登录过或者账号密码错误，跳转到登录界面");
                httpServletResponse.sendRedirect("/login");
                return;
            } else {
                System.out.println("已经登录，进行下一步");
                filterChain.doFilter(request, response);
            }
        }


    }

    /**
     * 是否在白名单中
     * @param url
     * @return
     */
    private Boolean isWhiteUrl(String url) {
        Boolean isWhiteUrl = false;
        if (!StringUtils.isEmpty(url) && urls != null) {
            for (String u : urls) {
                if (u.contains(url)) {
                    isWhiteUrl = true;
                    break;
                }
            }
        }
        return isWhiteUrl;
    }

    /**
     * 是否登录
     * @param httpServletRequest
     * @return
     */
    private Boolean isLogin(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getParameter("token");
        if (!StringUtils.isEmpty(token)) {
            if (token.equals("yangpeng")) {
                System.out.println("yangpeng 已经登陆");
                return true;
            }
        }
        return false;
    }

    /**
     * 销毁
     */
    @Override
    public void destroy() {
        //Filter对象创建后会驻留在内存，当web应用移除或服务器停止时才销毁。
        // 在Web容器卸载 Filter 对象之前被调用。该方法在Filter的生命周期中仅执行一次。
        // 在这个方法中，可以释放过滤器使用的资源。
        System.out.println("销毁过滤器："+filterName);
        this.filterName = null;
        this.urls = null;
    }
}
