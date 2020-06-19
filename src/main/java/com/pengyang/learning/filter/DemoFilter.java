package com.pengyang.learning.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

/**
 * @ServletComponentScan ：在SpringBootApplication上使用@ServletComponentScan注解后，
 * Servlet、Filter、Listener可以直接通过@WebServlet、@WebFilter、@WebListener注解自动注册，无需其他代码。
 */
@Order(1)//过滤器的顺序
@WebFilter(filterName = "DemoFilter",urlPatterns = "/*",initParams = @WebInitParam(name = "charset",value = "utf-8"))
public class DemoFilter implements Filter {
    private String filterName;
    private String charset;
    public void destroy() {
        System.out.println("销毁过滤器："+filterName);
        this.filterName = null;
        this.charset = null;
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        req.setCharacterEncoding(charset);
        resp.setCharacterEncoding(charset);
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {
        this.filterName = config.getFilterName();
        this.charset = config.getInitParameter("charset");
        System.out.println("字符集编码："+charset);
        System.out.println("初始化过滤器："+filterName);

    }

}
