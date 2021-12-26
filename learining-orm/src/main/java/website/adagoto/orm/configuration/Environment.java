package website.adagoto.orm.configuration;

import website.adagoto.orm.configuration.support.ConfigurationReader;
import website.adagoto.orm.configuration.support.MapperReader;
import website.adagoto.orm.datasource.DataSourceFactory;
import website.adagoto.orm.datasource.pooled.PooledDataSourceFactory;
import website.adagoto.orm.mapping.MapperMapping;
import website.adagoto.orm.util.ValidUtil;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.configuration
 * @Description: 环境
 * @date Date : 2021年07月12日 20:27
 */
public class Environment implements Serializable {

    private DataSourceFactory factory;

    private ConfigurationReader reader;
    /**
     * 存放配置文件的
     */
    private Map<String, MapperMapping> mapperMappings = new ConcurrentHashMap<>();

    /**
     * Orm框架的上下问环境
     *
     * @param configLocation
     */
    public Environment(String... configLocation) {
        this.reader = new ConfigurationReader(configLocation[0]);
        initDataSourceFactory(getConfiguration());
    }

    /**
     * 获取Mapper映射方法
     *
     * @return
     */
    public MapperMapping getMapperMapping(Class<?> clazz) {
        return getMapperMapping(clazz.getName());
    }

    /**
     * 全限定名称
     *
     * @param className
     * @return
     */
    public MapperMapping getMapperMapping(String className) {
        if (ValidUtil.isEmpty(mapperMappings)) {
            registerMappers(this.reader.loadMappers());
        }
        return mapperMappings.get(className);
    }

    /**
     * 将Mapper的地址全部注册一遍
     *
     * @param mapperPaths
     */
    private void registerMappers(List<String> mapperPaths) {
        if (ValidUtil.isEmpty(mapperPaths)) {
            return;
        }
        for (String mapperLocation : mapperPaths) {
            MapperMapping mapping = MapperReader.getInstance().getMapping(mapperLocation, factory);
            mapperMappings.put(mapping.getClazz().getName(), mapping);
        }
    }

    /**
     * 初始化数据源
     */
    private void initDataSourceFactory(Configuration configuration) {
        this.factory = new PooledDataSourceFactory(configuration);
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public DataSource getDataSource() {
        return this.factory.getDataSource();
    }

    /**
     * 获取配置
     *
     * @return
     */
    public Configuration getConfiguration() {
        return this.reader.getConfiguration();
    }


}
