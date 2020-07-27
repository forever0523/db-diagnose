package cn.gov.zcy.mybatis.diagnose.interceptor.factories;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：fafa
 * @date ：Created in 2020/6/3 10:25 上午
 */
@Configuration
@ComponentScan(
        basePackages = {"cn.gov.zcy.mybatis.diagnose.interceptor"}
)
public class MybatisDiagnoseConfig {
}
