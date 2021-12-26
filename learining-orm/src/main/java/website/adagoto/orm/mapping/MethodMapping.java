package website.adagoto.orm.mapping;

import lombok.Data;
import website.adagoto.orm.constant.SQLType;
import website.adagoto.orm.jdbc.SQL;
import website.adagoto.orm.util.ValidUtil;

import java.util.Locale;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.mapping
 * @Description: mapper文件中方法级别的映射
 * @date Date : 2021年07月12日 21:23
 */
@Data
public class MethodMapping {
    /**
     * SQL
     */
    private SQL sql;
    /**
     * 参数对象类型
     */
    private String parameterType;
    /**
     * 参数类型
     */
    private Class<?> parameterClass;
    /**
     * 返回结果类型
     */
    private String resultType;
    /**
     * 返回结果类型
     */
    private Class<?> resultClass;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 标签名称
     */
    private SQLType type;

    /**
     * 每个方法的创建必须有个ID
     *
     * @param methodName
     */
    public MethodMapping(String methodName, SQL sql) {
        this.methodName = methodName;
        this.sql = sql;
    }

    public void setParameterType(String parameterType) {
        if (ValidUtil.isEmpty(parameterType)) {
            return;
        }
        this.parameterType = parameterType;
        try {
            this.parameterClass = Class.forName(parameterType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setResultType(String resultType) {
        if (ValidUtil.isEmpty(resultType)) {
            return;
        }
        this.resultType = resultType;
        try {
            this.resultClass = Class.forName(resultType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setType(SQLType type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = SQLType.valueOf(type.toUpperCase(Locale.ENGLISH));
    }
}
