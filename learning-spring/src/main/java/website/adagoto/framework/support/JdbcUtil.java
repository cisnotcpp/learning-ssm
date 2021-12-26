package website.adagoto.framework.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.support
 * @Description: TODO
 * @date Date : 2021年07月03日 18:46
 */
public class JdbcUtil {

    public static final void close(Connection conn , Statement stmt , ResultSet set){
        try{
            if (null != set){
                set.close();
            }
            if (null != stmt){
                stmt.close();
            }
            if (null != conn){
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
