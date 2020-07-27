package cn.gov.zcy.mybatis.diagnose.dubbo.interceptor;

import cn.gov.zcy.mybatis.diagnose.interceptor.common.SqlThreadLocal;
import cn.gov.zcy.mybatis.diagnose.interceptor.config.PrintConfig;
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：fafa
 * @date ：Created in 2020/6/2 4:28 下午
 */
@Activate(group = Constants.PROVIDER)
@Slf4j
public class MybatisDubboFilter implements Filter {

    private PrintConfig printConfig;

    public void setPrintConfig(PrintConfig printConfig) {
        this.printConfig = printConfig;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        if(printConfig.getPrintSqlSwitch() && "dubbo".equals(printConfig.getPrintType())){

            String classMethodName = invoker.getInterface().getName() + "." + invocation.getMethodName();

            if(classMethodName.equals(printConfig.getRestUrlOrDubboClassMethod())){

                if(!printConfig.getParamCheck()){
                    if(SqlThreadLocal.canPrint()){
                        SqlThreadLocal.setPrintPrefix(printConfig.getPrintSqlPrefix());
                    }
                }else{
                    try {
                        Object[] args = invocation.getArguments();

                        if(args != null && args.length > 0){

                            Map<String,Object> env = new HashMap<>();
                            for(int i=0;i<args.length;i++){

                                try {
                                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(args[i]));
                                    env.put("arg" + i,jsonObject);
                                } catch (Exception e) {
                                    log.warn("[MybatisDubboFilterParamError]:{}",e);
                                    env.put("arg" + i,args[i]);
                                }
                            }

                            if(printConfig.checkExpression(env) && SqlThreadLocal.canPrint()){
                                SqlThreadLocal.setPrintPrefix(printConfig.getPrintSqlPrefix());
                            }
                        }
                    } catch (Exception e) {
                        log.error("[MybatisDubboFilterParamError]:{}",e);
                    }
                }
            }
        }

        try {
            Result result = invoker.invoke(invocation);
            return result;
        } finally {
            SqlThreadLocal.clearPrintPrefix();
        }
    }
}
