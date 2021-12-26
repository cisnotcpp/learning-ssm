package website.adagoto.framework.webmvc;

import website.adagoto.framework.support.StreamUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: framework
 * @Package website.adagoto.framework.webmvc
 * @Description: TODO
 * @date Date : 2021年06月29日 20:48
 */
public class View {

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public View(File viewFile) {
        this.viewFile = viewFile;
    }

    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        StringBuffer buffer = new StringBuffer();
        RandomAccessFile randomFile = new RandomAccessFile(this.viewFile, "r");
        try {
            String line = null;
            while (null != (line = randomFile.readLine())) {
                line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
                Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String paramName = matcher.group();
                    paramName = paramName.replaceAll("￥\\{|\\}", "");
                    Object paramValue = model.get(paramName);
                    if (null == paramValue) {
                        continue;
                    }
                    line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);
                }
                buffer.append(line);
            }
        } finally {
            StreamUtil.close(randomFile);
        }
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(buffer.toString());
    }

    /**
     * 处理特殊字符
     *
     * @param str
     * @return
     */
    public static String makeStringForRegExp(String str) {
        String exp = "*+|{}()^$[]?,.&";
        str = str.replace("\\", "\\\\");
        for (Character c : exp.toCharArray()) {
            str = str.replace(String.valueOf(c), String.format("\\%s", c));
        }
        return str;
    }

}
