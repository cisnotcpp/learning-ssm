package website.adagoto.orm.annotations;

import java.lang.annotation.*;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: orm
 * @Package website.adagoto.orm.annotations
 * @Description: 更新语句
 * @date Date : 2021年07月10日 17:09
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Update {
    /**
     * 返回sql语句
     *
     * @return
     */
    String[] value();

}
