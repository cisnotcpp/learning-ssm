package website.adagoto.orm.mapping;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.mapping
 * @Description: 类型映射
 * @date Date : 2021年07月17日 12:17
 */
public class ClassMapping {
    static final Set<Class<?>> SUPPORT_BASE_TYPE = new HashSet<>();

    static {
        Class<?>[] classes = {
                boolean.class, Boolean.class,
                short.class, Short.class,
                int.class, Integer.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class,
                String.class,
                Date.class,
                Timestamp.class,
                BigDecimal.class
        };
        SUPPORT_BASE_TYPE.addAll(Arrays.asList(classes));
    }

    /**
     * 是否是基础类型
     *
     * @param clazz
     * @return
     */
    public static boolean isBaseType(Class<?> clazz) {
        return clazz.isEnum() || SUPPORT_BASE_TYPE.contains(clazz);
    }

}
