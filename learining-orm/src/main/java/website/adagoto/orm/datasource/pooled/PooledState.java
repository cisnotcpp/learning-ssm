package website.adagoto.orm.datasource.pooled;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.datasource.pooled
 * @Description: 连接池的状态
 * @date Date : 2021年07月11日 15:51
 */
public class PooledState {
    /**
     * 空闲连接
     */
    protected final List<PooledConnection> idleConnections = new ArrayList<>();
    /**
     * 工作连接
     */
    protected final List<PooledConnection> activeConnections = new ArrayList<>();
    /**
     * 连接池
     */
    protected PooledDataSource dataSource;

    public PooledState(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }


}
