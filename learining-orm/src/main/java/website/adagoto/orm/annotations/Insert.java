package website.adagoto.orm.annotations;

import java.lang.annotation.*;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: orm
 * @Package website.adagoto.orm.annotations
 * @Description: 插入语句
 * @date Date : 2021年07月10日 17:10
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Insert {
    /**
     * 插入语句
     *
     * @return
     */
    String[] value();
}
