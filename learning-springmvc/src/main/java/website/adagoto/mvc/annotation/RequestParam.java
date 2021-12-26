package website.adagoto.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: mvc
 * @Package website.adagoto.mvc.annotation
 * @Description: TODO
 * @date Date : 2021年06月20日 10:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface RequestParam {
    String value() default "";
}
