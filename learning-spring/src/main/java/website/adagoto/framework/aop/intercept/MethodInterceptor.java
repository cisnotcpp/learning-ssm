package website.adagoto.framework.aop.intercept;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop.aspect
 * @Description: 方法拦截器
 * @date Date : 2021年07月04日 10:45
 */
public interface MethodInterceptor {
    /**
     *方法拦截器调用
     * @param mi
     * @return
     * @throws Throwable
     */
    Object invoke(MethodInvocation mi) throws Throwable;
}
