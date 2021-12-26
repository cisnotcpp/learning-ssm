package website.adagoto.framework.annotation;

import java.lang.annotation.*;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.annotation
 * @Description: TODO
 * @date Date : 2021年06月26日 15:46
 */
@Target({ElementType.PARAMETER})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String value() default "";
}
