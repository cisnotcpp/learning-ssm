package website.adagoto.demo.service;

import website.adagoto.demo.pojo.User;

import java.util.List;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.demo.service
 * @Description: TODO
 * @date Date : 2021年07月03日 14:39
 */
public interface QueryService {
    /**
     * 查询
     * @param name
     * @return
     */
    public List<User> query(String name);
}
