package website.adagoto.orm.configuration.support;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import website.adagoto.orm.datasource.DataSourceFactory;
import website.adagoto.orm.handler.MethodHandler;
import website.adagoto.orm.mapping.MapperMapping;
import website.adagoto.orm.util.ValidUtil;

import java.lang.reflect.Method;
import java.net.URL;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.configuration.support
 * @Description: 读取mapper配置的操作
 * @date Date : 2021年07月17日 12:33
 */
public class MapperReader {
    private MapperReader() {

    }

    /**
     * 读取配置文件
     *
     * @param mapperLocation
     * @return
     */
    public MapperMapping getMapping(String mapperLocation, DataSourceFactory factory) {
        try {
            Element mapperNode = getRoot(mapperLocation);
            String fullClassName = mapperNode.attribute("namespace").getValue();
            //加接口
            Class<?> clazz = Class.forName(fullClassName);
            MapperMapping mapping = new MapperMapping(clazz, factory);
            fullMapperMapping(mapping, clazz, mapperNode);
            return mapping;
        } catch (DocumentException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 填充
     *
     * @param mapperMapping
     * @param clazz
     * @param rootNode
     */
    public void fullMapperMapping(MapperMapping mapperMapping, Class<?> clazz, Element rootNode) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Element methodNode = (Element) rootNode.selectSingleNode(String.format("./*[@id='%s']", methodName));
            if (ValidUtil.isNull(methodNode)) {
                continue;
            }
            String parameterType = methodNode.attributeValue("parameterType");
            String resultType = methodNode.attributeValue("resultType");
            MethodHandler handler = new MethodHandler(methodName, methodNode.getTextTrim(), parameterType, resultType, methodNode.getName());
            mapperMapping.putMapping(method, handler);
        }
    }

    /**
     * 获取根节点
     *
     * @param mapperLocation
     * @return
     * @throws DocumentException
     */
    private Element getRoot(String mapperLocation) throws DocumentException {
        URL mapperURL = this.getClass().getClassLoader().getResource(mapperLocation);
        SAXReader saxReader = new SAXReader();
        Document mapperDocument = saxReader.read(mapperURL);
        return mapperDocument.getRootElement();
    }

    public static MapperReader getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static MapperReader INSTANCE = new MapperReader();
    }
}
