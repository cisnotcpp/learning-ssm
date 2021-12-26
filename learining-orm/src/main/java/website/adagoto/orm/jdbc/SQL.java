package website.adagoto.orm.jdbc;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.jdbc
 * @Description: 存放sql的
 * @date Date : 2021年07月17日 9:42
 */
public class SQL extends AbstractSQL {

    public SQL(String sql) {
        this.sqlText = sql;
    }
}
