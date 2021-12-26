package website.adagoto.framework.aop.support;

import website.adagoto.framework.aop.CglibAopProxy;
import website.adagoto.framework.aop.JdkDynamicAopProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learning-spring-core
 * @Package website.adagoto.framework.aop.support
 * @Description: Aop代理的工具
 * @date Date : 2021年07月06日 22:31
 */
public class AopUtils {

    /**
     * 获取代理的原对象
     *
     * @param proxy
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getTarget(Object proxy) {
        try {
            if (isCglibAopProxy(proxy)) {
                return getCglibAopProxy(proxy);
            }
            if (isJdkDynamicAopProxy(proxy)) {
                return getJdkDynamicAopProxyObject(proxy);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return proxy;
    }

    /**
     * 获取cglib的代理对象
     *
     * @param proxy
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getCglibAopProxy(Object proxy) throws NoSuchFieldException, IllegalAccessException {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        CglibAopProxy aopProxy = (CglibAopProxy) h.get(proxy);
        Field support = aopProxy.getClass().getDeclaredField("support");
        support.setAccessible(true);
        return ((AdvisedSupport) support.get(aopProxy)).getTarget();
    }

    /**
     * 获取jdk的动态代理的原对象
     *
     * @param proxy
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getJdkDynamicAopProxyObject(Object proxy) throws NoSuchFieldException, IllegalAccessException {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        JdkDynamicAopProxy aopProxy = (JdkDynamicAopProxy) h.get(proxy);
        Field support = aopProxy.getClass().getDeclaredField("support");
        support.setAccessible(true);
        return ((AdvisedSupport) support.get(aopProxy)).getTarget();
    }

    /**
     * 判断是否是动态代理
     *
     * @param object
     * @return
     */
    public static boolean isProxyClass(Object object) {
        return isCglibAopProxy(object) || isJdkDynamicAopProxy(object);
    }

    /**
     * 判断是否是Cglib的动态代理
     *
     * @param object
     * @return
     */
    public static boolean isCglibAopProxy(Object object) {
        return object.getClass().getName().contains("$$");
    }


    /**
     * 判断是否是Jdk动态代理
     *
     * @param object
     * @return
     */
    public static boolean isJdkDynamicAopProxy(Object object) {
        return Proxy.isProxyClass(object.getClass());
    }
}
