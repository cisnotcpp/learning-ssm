package website.adagoto.framework.support;

import java.util.Collection;
import java.util.Map;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.support
 * @Description: TODO
 * @date Date : 2021年06月29日 20:51
 */
public final class CommonUtil {

    private CommonUtil() {

    }

    /**
     * 判空处理
     *
     * @param t
     * @param <T>
     * @return
     */
    public static final <T> boolean isEmpty(T t) {
        if (null == t) {
            return true;
        }
        if (t instanceof String) {
            return ((String) t).trim().length() == 0;
        } else if (t instanceof Collection) {
            return ((Collection) t).isEmpty();
        } else if (t instanceof Map) {
            return ((Map) t).isEmpty();
        } else if (t.getClass().isArray()) {
            return ((Object[])t).length == 0;
        }
        return false;
    }
}
