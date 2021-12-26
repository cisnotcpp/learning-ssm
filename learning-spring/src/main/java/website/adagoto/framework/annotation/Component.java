package website.adagoto.framework.annotation;

import java.lang.annotation.*;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.annotation
 * @Description: TODO
 * @date Date : 2021年06月26日 17:41
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
@Inherited
public @interface Component {
    String value() default "";
}
