package website.adagoto.orm.jdbc;

import website.adagoto.orm.util.CloseableUtil;
import website.adagoto.orm.util.ValidUtil;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.*;
import java.util.*;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.jdbc
 * @Description: SQL执行者
 * @date Date : 2021年07月17日 20:20
 */
public class SQLRunner {

    private SQLRunner() {

    }

    /**
     * 执行sql
     *
     * @param sqlCommand
     * @param dataSource
     * @return
     */
    public Object execute(SQLCommand sqlCommand, DataSource dataSource) {
        switch (sqlCommand.getType()) {
            case UPDATE:
            case DELETE:
            case INSERT:
                return executeModify(sqlCommand, dataSource);
            case SELECT:
                return executeQuery(sqlCommand, dataSource);
        }
        return null;
    }

    /**
     * 执行查询
     *
     * @param sqlCommand
     * @param dataSource
     * @return
     */
    public List<Map<String, Object>> executeQuery(SQLCommand sqlCommand, DataSource dataSource) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = initPreparedStatement(connection, sqlCommand);
            resultSet = statement.executeQuery();
            return getResultMapList(resultSet);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            CloseableUtil.close(resultSet, statement, connection);
        }
        return null;
    }

    /**
     * 初始化PreparedStatement
     *
     * @param connection
     * @param sqlCommand
     * @return
     * @throws SQLException
     */
    private PreparedStatement initPreparedStatement(Connection connection, SQLCommand sqlCommand) throws SQLException {
        if (ValidUtil.isNull(connection)) {
            throw new SQLException("Connection is null");
        }
        PreparedStatement statement = connection.prepareStatement(sqlCommand.getPrepareSql());
        Object[] params = sqlCommand.getParams();
        if (!ValidUtil.isEmpty(params)) {
            for (int i = 0, len = sqlCommand.getParams().length; i < len; i++) {
                statement.setObject(i + 1, params[i]);
            }
        }
        return statement;
    }

    /***
     * 执行插入
     * @param sqlCommand
     * @param dataSource
     * @return
     */
    public Integer executeModify(SQLCommand sqlCommand, DataSource dataSource) {
        Connection connection = null;
        PreparedStatement statement = null;
        Integer result = 0;
        try {
            connection = dataSource.getConnection();
            statement = initPreparedStatement(connection, sqlCommand);
            return statement.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            isCommit(connection);
            CloseableUtil.close(statement, connection);
        }
        return result;
    }

    /**
     * 判断连接是否开启事务
     */
    private void isCommit(Connection connection) {
        try {
            if (!ValidUtil.isNull(connection) && !connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException se) {
            rollback(connection);
            se.printStackTrace();
        }
    }

    /**
     * 回滚
     *
     * @param connection
     */
    private void rollback(Connection connection) {
        try {
            if (!ValidUtil.isNull(connection)) {
                connection.rollback();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * 将ResultSet转换成List<Map<String, Object>>
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private static List<Map<String, Object>> getResultMapList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (rs.next()) {
            Map<String, Object> rowMap = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                rowMap.put(metaData.getColumnName(i).toUpperCase(Locale.ENGLISH), rs.getObject(i));
            }
            resultList.add(rowMap);
        }
        return resultList;
    }

    public static final SQLRunner getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static SQLRunner INSTANCE = new SQLRunner();
    }
}
