package cn.gov.zcy.mybatis.diagnose.web.interceptor;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * @author ：fafa
 * @date ：Created in 2020/6/2 4:07 下午
 */
@Slf4j
public class RequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        RequestWrapper requestWrapper = null;
        try {
            HttpServletRequest req = (HttpServletRequest)servletRequest;
            requestWrapper = new RequestWrapper(req);
        }catch (Exception e){
            log.warn("RequestWrapperFilter Error:", e);
        }

        filterChain.doFilter((Objects.isNull(requestWrapper) ? servletRequest : requestWrapper), servletResponse);
    }

    @Override
    public void destroy() {

    }
}
