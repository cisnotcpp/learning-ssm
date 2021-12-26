package website.adagoto.orm.jdbc;

import lombok.Data;
import website.adagoto.orm.constant.SQLType;

import java.io.Serializable;
import java.util.List;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.jdbc
 * @Description: SQL指令
 * @date Date : 2021年07月17日 9:56
 */
@Data
public class SQLCommand implements Serializable {
    /**
     * 预编译的sql
     */
    private String prepareSql;
    /**
     * sql对应的参数
     */
    private Object[] params;
    /**
     * SQL的类型
     */
    private SQLType type;

    public SQLCommand(String prepareSql, List<Object> objects, SQLType type) {
        this.prepareSql = prepareSql;
        this.params = objects.toArray();
        this.type = type;
    }

    public SQLCommand(String prepareSql, Object[] objects, SQLType type) {
        this.prepareSql = prepareSql;
        this.params = objects;
        this.type = type;
    }


}
