package website.adagoto.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: mvc
 * @Package website.adagoto.mvc.annotation
 * @Description: TODO
 * @date Date : 2021年06月20日 10:35
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Autowired {
    String value() default "";
}
