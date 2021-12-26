package website.adagoto.framework.aop;

import website.adagoto.framework.aop.intercept.MethodInvocation;
import website.adagoto.framework.aop.support.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop
 * @Description: JDK动态代理
 * @date Date : 2021年07月04日 12:42
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private AdvisedSupport support;

    public JdkDynamicAopProxy(AdvisedSupport support) {
        this.support = support;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = support.getInterceptorsAndDynamicInterceptionAdvice(method, this.support.getTargetClass());
        MethodInvocation methodInvocation = new MethodInvocation(proxy, support.getTarget(), method, args, support.getTargetClass(), interceptorsAndDynamicMethodMatchers);
        return methodInvocation.proceed();
    }

    @Override
    public Object getProxy() {
        return getProxy(support.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, support.getTargetClass().getInterfaces(), this);
    }

}
