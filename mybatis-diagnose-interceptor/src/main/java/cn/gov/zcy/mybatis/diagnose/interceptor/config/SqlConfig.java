package cn.gov.zcy.mybatis.diagnose.interceptor.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：fafa
 * @date ：Created in 2020/5/14 4:28 下午
 */
@Data
@Configuration
public class SqlConfig {

    /**
     * sql执行时间阈值
     */
    @Value("${sql.threshold.execute.max.time:500}")
    private Integer maxExecuteTime;

    /**
     * sql执行返回数量阈值
     */
    @Value("${sql.threshold.execute.max.count:1000}")
    private Integer maxExecuteCount;
}
