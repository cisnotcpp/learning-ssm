package website.adagoto.framework.aop;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.aop
 * @Description: 代理工厂顶级接口
 * @date Date : 2021年07月04日 12:40
 */
public interface AopProxy {
    /**
     * 获得一个代理对象
     * @return
     */
    Object getProxy();
    /**
     * 通过自定义加载器获取一个代理对象
     * @param classLoader
     * @return
     */
    Object getProxy(ClassLoader classLoader);
}
