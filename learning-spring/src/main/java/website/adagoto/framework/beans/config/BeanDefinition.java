package website.adagoto.framework.beans.config;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.config
 * @Description: TODO
 * @date Date : 2021年06月26日 15:51
 */
public class BeanDefinition {
    /**
     * bean的全限定类名称
     */
    private String beanClassName;
    /**
     * 是否延迟加载
     */
    private boolean lazyInit = false;
    /**
     * 保存beanName, 就是IOC中的存储的key
     */
    private String factoryBeanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
