package website.adagoto.orm.jdbc;

import lombok.Data;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.jdbc
 * @Description: 抽象的Sql
 * @date Date : 2021年07月11日 21:11
 */
@Data
public abstract class AbstractSQL {
    /**
     * 抽象的SQL语句
     */
    protected String sqlText;

}
