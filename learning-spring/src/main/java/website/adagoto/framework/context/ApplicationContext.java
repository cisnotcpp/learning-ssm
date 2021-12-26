package website.adagoto.framework.context;

import website.adagoto.framework.annotation.Autowired;
import website.adagoto.framework.annotation.Component;
import website.adagoto.framework.annotation.Controller;
import website.adagoto.framework.annotation.Service;
import website.adagoto.framework.aop.AopConfig;
import website.adagoto.framework.aop.AopProxy;
import website.adagoto.framework.aop.CglibAopProxy;
import website.adagoto.framework.aop.JdkDynamicAopProxy;
import website.adagoto.framework.aop.support.AdvisedSupport;
import website.adagoto.framework.aop.support.AopUtils;
import website.adagoto.framework.beans.BeanWrapper;
import website.adagoto.framework.beans.config.BeanDefinition;
import website.adagoto.framework.beans.config.BeanPostProcessor;
import website.adagoto.framework.beans.support.BeanDefinitionReader;
import website.adagoto.framework.context.support.DefaultListableBeanFactory;
import website.adagoto.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.context.support
 * @Description: IoC，DI,MVC,AOP
 * @date Date : 2021年06月26日 16:01
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    /**
     * 配置文件路径
     */
    private String[] configLocations;
    /**
     * 读取配置
     */
    private BeanDefinitionReader reader;
    /**
     * 单例的IoC容器缓存
     */
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();
    /**
     * 通用的Ioc容器
     */
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void refresh() throws Exception {
        // 定位配置文件
        reader = new BeanDefinitionReader(this.configLocations);
        // 加载配置并扫描成相关的类，把类的信息封装成框架的BeanDefinition
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinition();
        // 注册到容器中
        doRegisterBeanDefinitions(beanDefinitions);
        //自动注入
        doAutowired();
    }

    /**
     * 注册bean
     *
     * @param beanDefinitions
     */
    private void doRegisterBeanDefinitions(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition definition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(definition.getFactoryBeanName())) {
//                throw new Exception("The \"" + definition.getFactoryBeanName() + "\" is exist! ");
                continue;
            }
            super.beanDefinitionMap.put(definition.getFactoryBeanName(), definition);
        }
    }

    /**
     * 对非延时加载的自动初始化
     */
    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanDefinitionEntry.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 依赖注入，从这里开始读取BeanDefinition的信息
     * 然后通过反射机制创建一个实例并返回
     * Spring 做法是将最原始的对象进行一次包赚再返回
     *
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        //获取注册信息
        BeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        try {
            //生成通知事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
            //实例化原始对象
            Object instance = instantiateBean(beanDefinition);
            if (null == instance) {
                return null;
            }
            //前置通知
            beanPostProcessor.postProssessBeforeInitialization(instance, beanName);
            //加入实例缓存
            this.factoryBeanInstanceCache.put(beanName, new BeanWrapper(instance));
            //后置通知
            beanPostProcessor.postProessAfterInitialization(instance, beanName);
            // 注入依赖
            populateBean(instance);
            return factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            //第一次失败了后，再次调用的时候会重新注入依赖
            e.printStackTrace();
        }
        return null;
    }

    private void populateBean(Object instance) {
        if (AopUtils.isProxyClass(instance)) {
            instance = AopUtils.getTarget(instance);
        }
        Class<?> clazz = instance.getClass();
        if (!isAnnotationPresents(clazz)) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(Autowired.class) && null != field.get(instance)) {
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);
                String autowiredBeanName = autowired.value().trim();
                if ("".equals(autowiredBeanName)) {
                    autowiredBeanName = field.getType().getName();
                }
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
            }
        }
    }

    /**
     * 实例化Bean
     *
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(BeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        try {
            Object instance = null;
            if (this.factoryBeanObjectCache.containsKey(className)) {
                instance = this.factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                if (isAnnotationPresents(clazz)) {
                    instance = clazz.newInstance();
                    //实例Aop
                    AdvisedSupport support = instantiationAopConfig(beanDefinition);
                    support.setTarget(instance);
                    support.setTargetClass(clazz);
                    if (support.pointCutMatch()) {
                        instance = createProxy(support).getProxy();
                    }
                    this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
                }
            }
            return instance;
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
        } catch (IllegalAccessException ie) {
            ie.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否是支持的注解
     *
     * @param clazz
     * @return
     */
    private boolean isAnnotationPresents(Class<?> clazz) {
        return clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class) || clazz.isAnnotationPresent(Component.class);
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    /**
     * 创建代理
     *
     * @param support
     * @return
     */
    private AopProxy createProxy(AdvisedSupport support) {
        Class targetClass = support.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new JdkDynamicAopProxy(support);
        }
        return new CglibAopProxy(support);
    }

    /**
     * 实例化Aop的配置
     *
     * @param beanDefinition
     * @return
     */
    private AdvisedSupport instantiationAopConfig(BeanDefinition beanDefinition) {
        AopConfig config = new AopConfig();
        config.setPointCut(reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(config);
    }

    /**
     * 获取Bean的注册名称
     *
     * @return
     */
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    /**
     * 获取注册bean的个数
     *
     * @return
     */
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }

}
