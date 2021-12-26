package website.adagoto.framework.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import website.adagoto.framework.aop.intercept.MethodInvocation;
import website.adagoto.framework.aop.support.AdvisedSupport;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop
 * @Description: TODO
 * @date Date : 2021年07月04日 12:41
 */
public class CglibAopProxy implements AopProxy, MethodInterceptor {

    private AdvisedSupport support;

    public CglibAopProxy(AdvisedSupport support) {
        this.support = support;
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        // 设置enhancer对象的父类
        enhancer.setSuperclass(support.getTargetClass());
        // 设置enhancer的回调对象
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return getProxy();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = support.getInterceptorsAndDynamicInterceptionAdvice(method, this.support.getTargetClass());
        MethodInvocation methodInvocation = new MethodInvocation(o, support.getTarget(), method, objects, support.getTargetClass(), interceptorsAndDynamicMethodMatchers);
        return methodInvocation.proceed();
    }
}
