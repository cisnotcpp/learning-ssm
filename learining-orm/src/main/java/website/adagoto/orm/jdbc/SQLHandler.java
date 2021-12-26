package website.adagoto.orm.jdbc;

import lombok.Data;
import website.adagoto.orm.constant.SQLType;
import website.adagoto.orm.handler.ParameterHandler;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.jdbc
 * @Description: sql处理器
 * @date Date : 2021年07月17日 9:44
 */
@Data
public class SQLHandler implements Serializable {
    /**
     * sqlText
     */
    private SQL sql;
    /**
     * 实参列表
     */
    private Object[] params;
    /**
     * 执行的方法
     */
    private Method method;
    /**
     * SQL类型
     */
    private SQLType type;

    public SQLHandler(SQL sql, SQLType type, Method method, Object[] params) {
        this.sql = sql;
        this.params = params;
        this.method = method;
        this.type = type;
    }

    public SQLHandler(SQL sql, SQLType type, Method method, List<Object> paramList) {
        this(sql, type, method, paramList.toArray());
    }

    /**
     * 执行
     *
     * @return
     */
    public Object execute(DataSource dataSource) {
        ParameterHandler handler = new ParameterHandler(this);
        SQLCommand sqlCommand = handler.generateSQLCommand();
        return SQLRunner.getInstance().execute(sqlCommand, dataSource);
    }
}
