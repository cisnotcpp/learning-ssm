package website.adagoto.framework.aop.aspect;

import jdk.nashorn.internal.scripts.JO;
import website.adagoto.framework.aop.intercept.MethodInterceptor;
import website.adagoto.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop.aspect
 * @Description: TODO
 * @date Date : 2021年07月04日 12:12
 */
public class MethodAfterAdvice extends AbstractAspectJAdvice implements Advice, MethodInterceptor {

    private JoinPoint joinPoint;

    public MethodAfterAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }


    public void after(Method method, Object[] arguments, Object target) throws Throwable {
        invokeAdviceMethod(joinPoint, null, null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        this.after(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
