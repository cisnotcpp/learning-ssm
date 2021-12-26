package website.adagoto.orm.handler;

import website.adagoto.orm.exception.IllegalException;
import website.adagoto.orm.jdbc.SQL;
import website.adagoto.orm.jdbc.SQLHandler;
import website.adagoto.orm.mapping.MethodMapping;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm
 * @Description: 方法处理器
 * @date Date : 2021年07月17日 8:52
 */
public class MethodHandler {
    /**
     *
     */
    private MethodMapping methodMapping;

    public MethodHandler(MethodMapping methodMapping) {
        this.methodMapping = methodMapping;
    }

    /**
     * @param methodName    方法名称
     * @param sqlStatements sql语句
     * @param parameterType 参数全限定类型
     * @param resultType    结果类型
     */
    public MethodHandler(String methodName, String sqlStatements, String parameterType, String resultType, String sqlType) {
        this.methodMapping = new MethodMapping(methodName, new SQL(sqlStatements));
        this.methodMapping.setResultType(resultType);
        this.methodMapping.setParameterType(parameterType);
        this.methodMapping.setType(sqlType);
    }

    /**
     * 处理方法的逻辑
     *
     * @param args
     */
    public Object handle(Method method, Object[] args, DataSource dataSource) throws IllegalException {
        SQLHandler handler = new SQLHandler(methodMapping.getSql(), methodMapping.getType(), method, args);
        Object result = handler.execute(dataSource);
        ResultHandler resultHandler = new ResultHandler(method.getReturnType(), method.getGenericReturnType(), this.methodMapping.getResultClass(), result);
        return resultHandler.wrap();
    }
}
