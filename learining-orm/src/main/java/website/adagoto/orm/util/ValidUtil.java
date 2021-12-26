package website.adagoto.orm.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: orm
 * @Package website.adagoto.orm.util
 * @Description: TODO
 * @date Date : 2021年07月10日 17:04
 */
public class ValidUtil {

    private ValidUtil() {
    }

    /**
     * 判断是否是NuLL
     * @param t
     * @param <T>
     * @return
     */
    public static <T> boolean isNull(T t) {
        return null == t;
    }

    /**
     * 判空
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(T t) {
        if (null == t) {
            return true;
        }
        if (t instanceof String) {
            return ((String) t).isEmpty();
        }
        if (t instanceof Collection) {
            return ((Collection) t).isEmpty();
        }
        if (t instanceof Map) {
            return ((Map) t).isEmpty();
        }
        if (t.getClass().isArray()) {
            return ((Object[]) t).length == 0;
        }
        return false;
    }
}
