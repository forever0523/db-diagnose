package cn.gov.zcy.mybatis.diagnose.web.interceptor;

import cn.gov.zcy.mybatis.diagnose.interceptor.common.SqlThreadLocal;
import cn.gov.zcy.mybatis.diagnose.interceptor.config.PrintConfig;
import cn.gov.zcy.paas.user.dto.Operator;
import cn.gov.zcy.paas.web.auth.util.UserUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * @author ：fafa
 * @date ：Created in 2019-11-28 17:20
 */
@Slf4j
@Component
public class SqlPrintInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private PrintConfig printConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(!printConfig.getPrintSqlSwitch() || !"rest".equals(printConfig.getPrintType())){
            return true;
        }

        if(Strings.isNullOrEmpty(printConfig.getRestUrlOrDubboClassMethod()) || !printConfig.getRestUrlOrDubboClassMethod().equals(request.getRequestURI())){
            return true;
        }

        Operator operator = UserUtil.getCurrentOperator();
        if(printConfig.getRestOperatorId() != null && operator != null && printConfig.getRestOperatorId().equals(operator.getId())
                && (Strings.isNullOrEmpty(printConfig.getExpression()) || !printConfig.getParamCheck())){

            if(!SqlThreadLocal.canPrint()){
                return true;
            }

            SqlThreadLocal.setPrintPrefix(printConfig.getPrintSqlPrefix());
            return true;
        }

        if(printConfig.getRestOperatorId() != null &&
                ((operator != null && printConfig.getRestOperatorId().equals(operator.getId())) || Long.valueOf(-1).equals(printConfig.getRestOperatorId()))
                && !Strings.isNullOrEmpty(printConfig.getExpression())){

            Map<String, Object> env = new HashMap<>();

            if(request instanceof RequestWrapper){
                String json = ((RequestWrapper) request).getBody();
                if(!Strings.isNullOrEmpty(json)){
                    try {
                        JSONObject jsonObject = JSON.parseObject(json);
                        env.putAll(jsonObject);
                    } catch (Exception e) {
                        log.error("[RequestWrapperBodyJSONError]:{},{}",json,e);
                    }
                }
            }

            Map<String, String[]> requestParameterMap = request.getParameterMap();
            if(requestParameterMap != null ){
                for(String key : requestParameterMap.keySet()){
                    env.put(key,requestParameterMap.get(key) == null || requestParameterMap.get(key).length == 0 ? null : requestParameterMap.get(key)[0]);
                }
            }

            if(printConfig.checkExpression(env)){

                if(!SqlThreadLocal.canPrint()){
                    return true;
                }

                SqlThreadLocal.setPrintPrefix(printConfig.getPrintSqlPrefix());
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SqlThreadLocal.clearPrintPrefix();
    }
}
