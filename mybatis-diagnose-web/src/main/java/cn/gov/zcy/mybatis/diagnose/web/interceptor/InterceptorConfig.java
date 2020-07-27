package cn.gov.zcy.mybatis.diagnose.web.interceptor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author ：fafa
 * @date ：Created in 2019-11-28 17:24
 */
@Configuration
public class InterceptorConfig {

    @Bean
    public FilterRegistrationBean servletRegistrationBean() {
        RequestWrapperFilter requestWrapperFilter = new RequestWrapperFilter();
        FilterRegistrationBean<RequestWrapperFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(requestWrapperFilter);
        bean.setName("requestWrapperFilter");
        bean.addUrlPatterns("/*");
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bean;
    }
}
