package website.adagoto.orm.handler;

import website.adagoto.orm.jdbc.SQLCommand;
import website.adagoto.orm.jdbc.SQLHandler;
import website.adagoto.orm.mapping.ClassMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.handler
 * @Description: 入参处理器
 * @date Date : 2021年07月17日 20:18
 */
public class ParameterHandler {
    /**
     * 存储解析的参数
     */
    private Map<String, Object> parameterMap = new HashMap<>();
    /**
     * sql处理器
     */
    private SQLHandler sqlHandler;

    public ParameterHandler(SQLHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
        parseParameters();
    }

    /**
     * 解析参数
     */
    private void parseParameters() {
        Parameter[] parameters = this.sqlHandler.getMethod().getParameters();
        Object[] values = this.sqlHandler.getParams();
        for (int i = 0, len = parameters.length; i < len; i++) {
            Object value = values[i];
            if (ClassMapping.isBaseType(value.getClass())) {
                parameterMap.put(parameters[i].getName(), value);
            } else {
                parseObjectParameter(parameters[i], value);
            }
        }
    }

    /**
     * 解析对象类型
     *
     * @param parameter
     * @param paramObj
     */
    public void parseObjectParameter(Parameter parameter, Object paramObj) {
        if (parameter.getType() != paramObj.getClass()) {
            return;
        }
        Field[] fields = paramObj.getClass().getDeclaredFields();
        String prefix = parameter.getName();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String key = prefix + "." + field.getName();
                Object value = field.get(paramObj);
                parameterMap.put(key, value);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成sqlCommand
     *
     * @return
     */
    public SQLCommand generateSQLCommand() {
        Pattern pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
        String sqlText = this.sqlHandler.getSql().getSqlText();
        Matcher matcher = pattern.matcher(sqlText);
        List<Object> parameterList = new ArrayList<>();
        while (matcher.find()) {
            String paramName = matcher.group();
            paramName = paramName.substring("#{".length(), paramName.length() - 1);
            parameterList.add(parameterMap.get(paramName));
            sqlText = sqlText.replaceFirst(REGEX, "?");
            matcher = pattern.matcher(sqlText);
        }
        return new SQLCommand(sqlText, parameterList.toArray(), sqlHandler.getType());
    }

    /**
     * #{}的正则表达式
     */
    private static final String REGEX = "#\\{.+?}";

}
