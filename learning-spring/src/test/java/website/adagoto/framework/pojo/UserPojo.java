package website.adagoto.framework.pojo;

import com.alibaba.fastjson.JSON;
import website.adagoto.framework.annotation.Component;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework
 * @Description: TODO
 * @date Date : 2021年06月26日 17:48
 */
@Component
public class UserPojo {
    private String username = "lgy";
    private String password = "123456";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


}
