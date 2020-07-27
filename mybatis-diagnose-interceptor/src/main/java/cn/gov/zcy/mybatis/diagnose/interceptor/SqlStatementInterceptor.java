package cn.gov.zcy.mybatis.diagnose.interceptor;

import cn.gov.zcy.mybatis.diagnose.interceptor.common.SqlThreadLocal;
import cn.gov.zcy.mybatis.diagnose.interceptor.config.SqlConfig;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * @author ：fafa
 * @date ：Created in 2019-06-25 16:24
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
@Slf4j
@Component
public class SqlStatementInterceptor implements Interceptor {
    @SuppressWarnings("unused")
    private Properties properties;

    @Autowired
    private SqlConfig sqlConfig;

    @Override
    public Object intercept(Invocation arg0) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) arg0.getArgs()[0];
        Object parameter = null;
        if (arg0.getArgs().length > 1) {
            parameter = arg0.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();

        Object returnValue;
        long start = System.currentTimeMillis();
        returnValue = arg0.proceed();
        long end = System.currentTimeMillis();

        int count = 0;
        long time = (end - start);

        if(returnValue != null && returnValue instanceof List){
            count = ((List) returnValue).size();
        }

        String prefix = SqlThreadLocal.getPrintPrefix();
        String sql = null;
        if(!Strings.isNullOrEmpty(prefix)){
            sql = getSql(configuration, boundSql, sqlId, time);
            log.warn("[{}][sqlPrint]:time:{} ,count: {} ,sql: {}",prefix,time,count,sql);
        }

        if (time > sqlConfig.getMaxExecuteTime() || count > sqlConfig.getMaxExecuteCount()) {
            sql = Strings.isNullOrEmpty(sql) ? getSql(configuration, boundSql, sqlId, time) : sql;
            log.warn("[slowSql][bigResult]: time:{} , count:{} ,sql : {}",time,count,sql);
        }

        return returnValue;
    }

    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId, long time) {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder();
        str.append(sqlId);
        str.append(":");
        str.append(sql);
        return str.toString();
    }

    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    @Override
    public void setProperties(Properties arg0) {
        this.properties = arg0;
    }
}
