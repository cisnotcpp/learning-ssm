package website.adagoto.orm.annotations;

import java.lang.annotation.*;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: orm
 * @Package website.adagot.orm.configuration.annotations
 * @Description: 查询的
 * @date Date : 2021年07月10日 16:15
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Select {
    /**
     * 返回一个sql
     * @return
     */
    String[] value();
}
