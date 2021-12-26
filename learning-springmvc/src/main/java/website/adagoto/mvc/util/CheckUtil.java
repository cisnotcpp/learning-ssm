package website.adagoto.mvc.util;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: mvc
 * @Package website.adagoto.mvc.util
 * @Description: TODO
 * @date Date : 2021年06月20日 15:10
 */
public final class CheckUtil {

    private CheckUtil(){}

    /**
     * 判断是否为空
     * @param t
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(T t){
        if (null == t){
            return true;
        }
        if (t instanceof String){
            return String.valueOf(t).trim().length() == 0;
        }else if (t instanceof Collection){
            return ((Collection) t).isEmpty();
        }else if (t instanceof Map){
            return ((Map) t).isEmpty();
        }else if (t.getClass().isArray()){
            return ((Object[])t).length == 0;
        }
        return false;
    }


}
