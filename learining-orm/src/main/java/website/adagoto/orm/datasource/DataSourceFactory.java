package website.adagoto.orm.datasource;

import javax.sql.DataSource;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.datasource
 * @Description: 数据源工厂
 * @date Date : 2021年07月11日 15:41
 */
public interface DataSourceFactory {
    /**
     * 获取数据池
     *
     * @return
     */
    DataSource getDataSource();
}
