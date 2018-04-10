package cbp.marketlist.key;

import cbp.marketlist.data.Key;
import cbp.marketlist.parse.Parser;

/**
 * <pre>
 * 字段。是与解析器绑定的Key。
 * </pre>
 *
 * @param <JavaType> 字段要被存储的类型
 * @param <P>        用于JavaType的解析器
 * @author cbp
 */

public final class Field<JavaType, P extends Parser<? super JavaType>> extends Key<JavaType> {
    //字段解析器
    private final P parser;
    private final String stringValue;

    public Field(String name, P parser) {
        super(name);
        if (parser == null) {
            throw new NullPointerException("field must have a parser!(" + name + ")");
        }
        this.parser = parser;
        this.stringValue = "F." + name + ":" + parser.getClass().getSimpleName() + "";
    }

    public final P getParser() {
        return parser;
    }

    @Override
    public final String toString() {
        return stringValue;
    }

    //工厂，定义一个field实例
    public static <T, P extends Parser<? super T>> Field<T, P> define(String name, P parser) {
        return new Field<T, P>(name, parser);
    }
}

