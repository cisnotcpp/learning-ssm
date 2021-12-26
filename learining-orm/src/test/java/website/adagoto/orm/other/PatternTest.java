package website.adagoto.orm.other;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: learining-orm
 * @Package website.adagoto.orm.other
 * @Description: TODO
 * @date Date : 2021年07月17日 20:56
 */
public class PatternTest {

    @Test
    public void patternTest() {
        String regex = "#\\{.+?}";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        String data = "update USER set NAME = #{user.name} , PASSWORD = #{user.password} where ID = #{user.id}";
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            String paramName = matcher.group();
            paramName = paramName.substring("#{".length(), paramName.length() - 1);
            System.out.println(paramName);
            data = data.replaceFirst(regex, "?");
            matcher = pattern.matcher(data);
        }
        System.out.println(data);
    }
}
