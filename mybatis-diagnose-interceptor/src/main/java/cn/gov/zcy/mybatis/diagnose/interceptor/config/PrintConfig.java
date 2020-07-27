package cn.gov.zcy.mybatis.diagnose.interceptor.config;

import cn.gov.zcy.mybatis.diagnose.interceptor.common.SqlThreadLocal;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.google.common.base.Strings;
import com.googlecode.aviator.AviatorEvaluator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author ：fafa
 * @date ：Created in 2020/6/2 11:56 上午
 */
@Data
@Configuration
@Slf4j
public class PrintConfig {

    /**
     * 是否开启打印SQL功能
     */
    @Value("${sql.threshold.execute.print.sql:false}")
    private Boolean printSqlSwitch;

    /**
     * 打印SQL的前缀
     */
    @Value("${sql.threshold.execute.print.prefix:}")
    private String printSqlPrefix;

    /**
     * 打印SQL
     */
    @Value("${sql.threshold.execute.print.times:1}")
    private Integer printTimes;

    /**
     * 类型
     * dubbo/rest
     */
    @Value("${sql.threshold.execute.print.printType:}")
    private String printType;

    /**
     * rest请求的url
     */
    @Value("${sql.threshold.execute.print.restUrlOrDubboClassMethod:}")
    private String restUrlOrDubboClassMethod;

    /**
     * 请求的operatorId
     */
    @Value("${sql.threshold.execute.print.restOperatorId:}")
    private Long restOperatorId;

    /**
     * 规则表达式
     * dubbo/rest公用
     */
    @Value("${sql.threshold.execute.print.paramCheck:true}")
    private Boolean paramCheck;

    /**
     * 规则表达式
     * dubbo/rest公用
     */
    @Value("${sql.threshold.execute.print.expression:}")
    private String expression;

    /**
     * 条件表达式的校验
     * @param env
     * @return
     */
    public Boolean checkExpression(Map<String, Object> env){
        if(!paramCheck){
            return Boolean.TRUE;
        }

        if(CollectionUtils.isEmpty(env)){
            return Boolean.FALSE;
        }

        if(Strings.isNullOrEmpty(expression)){
            return Boolean.FALSE;
        }

        Object result = null;
        try {
            result = AviatorEvaluator.execute(expression,env,true);
        } catch (Exception e) {
            log.error("[checkExpressionError]:{},{},{}",expression,env,e);
            return Boolean.FALSE;
        }

        if(result != null && result instanceof Boolean){
            return (Boolean) result;
        }

        return Boolean.FALSE;
    }

    @ApolloConfigChangeListener
    private void changeHandler(ConfigChangeEvent changeEvent) {
        if (changeEvent.isChanged("sql.threshold.execute.print.times")) {
            ConfigChange configChange = changeEvent.getChange("sql.threshold.execute.print.times");
            SqlThreadLocal.setPrintTimes(Integer.valueOf(configChange.getNewValue()));
        }
    }

    public static void main(String[] args) {
        JSONObject jsonObject = JSON.parseObject("{\n" +
                "  \"val\": {\n" +
                "    \"val\": \"3.9\"\n" +
                "  }\n" +
                "}");

        Object result = AviatorEvaluator.execute("(val.val == \"3.9\" )", jsonObject);

        System.out.println(result instanceof Boolean);
    }
}
