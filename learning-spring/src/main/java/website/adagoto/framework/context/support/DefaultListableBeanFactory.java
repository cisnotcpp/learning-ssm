package website.adagoto.framework.context.support;

import website.adagoto.framework.beans.config.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.context.support
 * @Description: TODO
 * @date Date : 2021年06月26日 15:58
 */
public abstract class DefaultListableBeanFactory extends  AbstractApplicationContext {
    /**
     * 存储注册信息 BeanDefinition
     */
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
}
