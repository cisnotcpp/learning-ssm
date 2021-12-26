package website.adagoto.orm.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: orm
 * @Package website.adagoto.orm.util
 * @Description: 流工具
 * @date Date : 2021年07月10日 16:39
 */
public final class CloseableUtil {
    private CloseableUtil() {
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void close(Closeable... closeables) {
        if (null == closeables || closeables.length == 0) {
            return;
        }
        for (Closeable closeable : closeables) {
            try {
                if (null != closeable) {
                    closeable.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void close(AutoCloseable... closeables) {
        if (null == closeables || closeables.length == 0) {
            return;
        }
        for (AutoCloseable closeable : closeables) {
            try {
                if (null != closeable) {
                    closeable.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
