package cbp.marketlist.data;

/**
 * Key用于存取数据时作为键
 *
 * @param <ValueType> 与key对应的值的数据类型
 * @author cbp
 */

public class Key<ValueType> {
    //具有含义的说明性文字
    private final String name;
    private final String stringValue;

    protected Key(String name) {
        this.name = name;
        this.stringValue = "K." + name;
    }

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    //定义一个Key实例
    public static <T> Key<T> define(String name) {
        return new Key<>(name);
    }
}

