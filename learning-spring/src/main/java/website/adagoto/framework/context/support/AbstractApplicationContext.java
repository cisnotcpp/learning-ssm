package website.adagoto.framework.context.support;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.context.support
 * @Description: TODO
 * @date Date : 2021年06月26日 15:57
 */
public abstract class AbstractApplicationContext {
    /**
     * 子类重写
     * @throws Exception
     */
    protected abstract void refresh() throws Exception;
}
