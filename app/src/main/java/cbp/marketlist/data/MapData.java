package cbp.marketlist.data;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * 通用数据容器，专门存放Key-Value数据。对此类中存放的数据进行遍历时有确切顺序，可以与for语句一起工作。
 *
 * @author cbp
 */
public final class MapData implements Iterable<Entry<Key<?>, Object>> {
    private LinkedHashMap<Key<?>, Object> innerData = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <V> V get(Key<V> key) {
        return (V) innerData.get(key);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(Key<V> key, V defaultValue) {
        V v = (V) innerData.get(key);
        return v != null ? v : defaultValue;
    }

    public <V> MapData set(Key<V> key, V value) {
        innerData.put(key, value);
        return this;
    }

    /**
     * 将参数MapData的所有数据添加到当前MapData中
     *
     * @param fromMap
     * @return MapData
     */
    public <V> MapData putAll(MapData fromMap) {
        for (Entry<Key<?>, Object> kv : fromMap) {
            innerData.put(kv.getKey(), kv.getValue());
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <V> V remove(Key<V> key) {
        return (V) innerData.remove(key);
    }

    @Override
    public Iterator<Entry<Key<?>, Object>> iterator() {
        return innerData.entrySet().iterator();
    }

    /**
     * 返回键key在数据列表中对应的位置，从0开始。若没有找到对应的key，返回-1;
     *
     * @return
     */
    public int indexOf(Key<?> key) {
        int index = -1;
        Iterator<Entry<Key<?>, Object>> itor = innerData.entrySet().iterator();
        for (int i = 0; itor.hasNext(); i++) {
            Entry<Key<?>, Object> entry = itor.next();
            if (entry.getKey() == key) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 数据个数
     *
     * @return
     */
    public int size() {
        return innerData.size();
    }

    /**
     * 清空
     */
    public void clear() {
        innerData.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        MapData clone = new MapData();
        clone.innerData = (LinkedHashMap<Key<?>, Object>) innerData.clone();
        return clone;
    }

    @Override
    public String toString() {
        return innerData.toString();
    }
}
