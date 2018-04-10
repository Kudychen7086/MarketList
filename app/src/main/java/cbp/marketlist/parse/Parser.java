package cbp.marketlist.parse;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 字节流解析类的抽象父类
 *
 * @param <JavaType> 支持的java类型
 * @author cbp
 */
public abstract class Parser<JavaType> {

    /**
     * 反序列化字节数据到JavaType
     *
     * @param in InputStream
     */
    public final JavaType readStream(InputStream in) {
        try {
            return onReadStream(in);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    /**
     * 序列化JavaType对象
     *
     * @param obj
     */
    public final void writeStream(JavaType obj, OutputStream out) {
        try {
            onWriteStream(obj, out);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    /**
     * 反序列化到JavaType，子类实现具体工作
     *
     * @param in
     * @throws Exception
     */
    protected abstract JavaType onReadStream(InputStream in) throws Exception;

    /**
     * 序列化JavaType对象，子类实现具体工作
     *
     * @param obj
     * @param out TODO
     * @throws Exception
     */
    protected abstract void onWriteStream(JavaType obj, OutputStream out) throws Exception;

}