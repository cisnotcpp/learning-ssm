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
 * @date Date : 2021年07月18日 10:22
 */
public class ParamTest {

    @Test
    public void mapUnderscoreToCamelCaseTest() {
        String str = "ASSET_TYPE_CODE";
        str = str.toLowerCase();
        final StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("_(\\w)");
        Matcher m = p.matcher(str);
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        System.out.println(sb.toString());
    }

    @Test
    public void mapCamelCaseToUnderscoreTest() {
        String str = "assetTypeCode";
        final StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("[A-Z]+");
        Matcher m = p.matcher(str);
        while (m.find()) {
            m.appendReplacement(sb,"_" +  m.group(0).toLowerCase());
        }
        m.appendTail(sb);
        System.out.println(sb.toString());
    }
}
