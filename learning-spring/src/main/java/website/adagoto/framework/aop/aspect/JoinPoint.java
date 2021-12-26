package website.adagoto.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop.aspect
 * @Description: 切入点
 * @date Date : 2021年07月04日 10:42
 */
public interface JoinPoint {
    /**
     * 知道切入那个方法
     * @return
     */
    Method getMethod();

    /**
     * 获取该方法的参数列表
     */
    Object[] getArguments();

    /**
     * 获取该方法的实例对象
     */
    Object getThis();

    /**
     * 添加自定义属性
     * @param key
     * @param value
     */
    void setUserAttribute(String key, Object value);

    /**
     * 获取用户设置的属性
     * @param key
     * @return
     */
    Object getUserAttribute(String key);
}
