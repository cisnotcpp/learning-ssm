package website.adagoto.framework.aop.aspect;

import website.adagoto.framework.aop.intercept.MethodInterceptor;
import website.adagoto.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop.aspect
 * @Description: TODO
 * @date Date : 2021年07月04日 11:36
 */
public class MethodBeforeAdvice extends AbstractAspectJAdvice implements Advice , MethodInterceptor {

    private JoinPoint joinPoint;

    public MethodBeforeAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(this.joinPoint, null, null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
