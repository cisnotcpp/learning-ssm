package website.adagoto.framework.core;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.core
 * @Description: TODO
 * @date Date : 2021年06月26日 15:47
 */
public interface BeanFactory {
    /**
     * 根据beanName从IOC容器中获取一个bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;

    /**
     *
     * 根据beanClass从IOC中获取一个Bean
     * @param beanClass
     * @return
     * @throws Exception
     */
    Object getBean(Class<?> beanClass) throws Exception;
}
