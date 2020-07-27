package cn.gov.zcy.mybatis.diagnose.interceptor.common;

import com.google.common.base.Strings;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：fafa
 * @date ：Created in 2019-11-28 17:08
 */
public class SqlThreadLocal {

    private static final AtomicInteger printTimes = new AtomicInteger(1);

    private static final ThreadLocal<String> SQL_PRINT_PREFIX = new ThreadLocal<>();

    public static String getPrintPrefix(){
        return SQL_PRINT_PREFIX.get();
    }

    public static void setPrintPrefix(String prefix){
        if(Strings.isNullOrEmpty(prefix)){
            return;
        }

        SQL_PRINT_PREFIX.set(prefix);
    }

    public static void clearPrintPrefix(){
        SQL_PRINT_PREFIX.remove();
    }

    public static Boolean canPrint(){
        // 9999表示，不限制打印次数
        if(printTimes.get() == 9999){
            return Boolean.TRUE;
        }

        int times = printTimes.decrementAndGet();

        if(times >= 0){
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public static void setPrintTimes(Integer times){
        printTimes.set(times);
    }
}
