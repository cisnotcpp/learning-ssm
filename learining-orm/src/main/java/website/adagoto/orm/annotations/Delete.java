package website.adagoto.orm.annotations;

import java.lang.annotation.*;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: orm
 * @Package website.adagoto.orm.annotations
 * @Description: 删除语句
 * @date Date : 2021年07月10日 17:11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Delete {
    /**
     * 删除语句
     *
     * @return
     */
    String[] value();
}
