package cn.gov.zcy.mybatis.diagnose.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author ：fafa
 * @date ：Created in 2020/6/3 2:17 下午
 */
@Configuration
@ComponentScan(
        basePackages = {"cn.gov.zcy.mybatis.diagnose.web.interceptor"}
)
public class SqlInterceptorFactories extends WebMvcConfigurerAdapter {

    @Autowired
    private SqlPrintInterceptor sqlPrintInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sqlPrintInterceptor).order(1000);
    }
}
