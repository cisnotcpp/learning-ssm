package website.adagoto.demo.dao.impl;

import website.adagoto.demo.pojo.User;
import website.adagoto.framework.annotation.Component;
import website.adagoto.demo.dao.UserDao;
import website.adagoto.framework.support.JdbcUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.demo.dao
 * @Description: TODO
 * @date Date : 2021年07月03日 18:28
 */
@Component
public class UserDaoImpl implements UserDao {

    private static final String USER_NAME = "root";
    private static final String PASSWORD = "331107";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=UTC";

    public UserDaoImpl() {
        try {
            Class.forName(DRIVER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> query(String name) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            String sql = "select ID, NAME ,PASSWORD from USER where NAME = ? ";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();
            List<User> userList = new ArrayList<>();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getString(1));
                user.setName(resultSet.getString(2));
                user.setPassword(resultSet.getString(3));
                userList.add(user);
            }
            return userList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.close(connection, preparedStatement, resultSet);
        }
        return null;
    }
}
