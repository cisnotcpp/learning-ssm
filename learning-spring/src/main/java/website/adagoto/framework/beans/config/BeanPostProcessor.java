package website.adagoto.framework.beans.config;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.beans.config
 * @Description: TODO
 * @date Date : 2021年06月26日 16:59
 */
public class BeanPostProcessor {
    /**
     * 在Bean初始化之前提供回调
     *
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProssessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    /**
     * 在bean初始化之后回调
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
