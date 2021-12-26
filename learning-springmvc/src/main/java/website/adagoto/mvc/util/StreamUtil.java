package website.adagoto.mvc.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author : adagoto
 * @version V1.0.0
 * @Project: mvc
 * @Package website.adagoto.mvc.util
 * @Description: TODO
 * @date Date : 2021年06月20日 14:58
 */
public final class StreamUtil {
    private StreamUtil(){

    }

    /**
     * 关闭流
     * @param closeables
     */
    public static void close(Closeable...closeables){
        if (null == closeables || closeables.length == 0){
            return;
        }
        for (Closeable closeable : closeables){
            try{
                if (null != closeable){
                    closeable.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
