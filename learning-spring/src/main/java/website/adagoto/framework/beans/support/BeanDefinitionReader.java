package website.adagoto.framework.beans.support;

import website.adagoto.framework.beans.config.BeanDefinition;
import website.adagoto.framework.support.StreamUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.beans.support
 * @Description: TODO
 * @date Date : 2021年06月26日 16:04
 */
public class BeanDefinitionReader {
    /**
     * 存放扫苗到的全限定名称
     */
    private List<String> registyBeanClasses = new ArrayList<String>();

    private Properties config = new Properties();

    private static final String SCAN_PACKAGE = "scanPackage";

    public BeanDefinitionReader(String... configLocations) {
        String configLocation = configLocations[0].replace("classpath:", "");
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream(configLocation);
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(is);
        }

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    /**
     * 扫描包
     *
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File f : classPath.listFiles()) {
            if (f.isDirectory()) {
                doScanner(scanPackage + "." + f.getName());
            } else {
                if (!f.getName().endsWith(".class")) {
                    continue;
                }
                StringBuilder classNameBuilder = new StringBuilder();
                classNameBuilder.append(scanPackage).append(".").append(f.getName().replace(".class", ""));
                this.registyBeanClasses.add(classNameBuilder.toString());
            }
        }
    }

    /**
     * 将配置文件转换成BeanDefinition对象
     *
     * @return
     */
    public List<BeanDefinition> loadBeanDefinition() {
        List<BeanDefinition> beanDefinitions = new ArrayList<BeanDefinition>();
        for (String className : registyBeanClasses) {
            try {
                Class<?> beanClass = Class.forName(className);
                //是接口跳过
                if (beanClass.isInterface()) {
                    continue;
                }
                beanDefinitions.add(doCreateBeanDefinition(toLowerFristCase(beanClass.getSimpleName()), beanClass.getName()));
                //将clazz的父接口全部注册
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> c : interfaces) {
                    beanDefinitions.add(doCreateBeanDefinition(toLowerFristCase(c.getName()), beanClass.getName()));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return beanDefinitions;
    }

    /**
     * 将配置信息转换成BeanDefinition
     *
     * @param factoryBeanName
     * @param beanClassName
     * @return
     */
    private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    private String toLowerFristCase(String str) {
        char[] chars = str.toCharArray();
        if (Character.isUpperCase(chars[0])) {
            chars[0] = Character.toLowerCase(chars[0]);
        }
        return String.valueOf(chars);
    }

    /**
     * 获取配置
     *
     * @return
     */
    public Properties getConfig() {
        return config;
    }
}
