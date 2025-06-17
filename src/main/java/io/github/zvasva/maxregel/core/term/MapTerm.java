package io.github.zvasva.maxregel.core.term;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.github.zvasva.maxregel.core.process.MaxRegelException.requireNonNullArg;


/**
 * A term backed by a {@link Map}.
 */
public class MapTerm extends AbstractTerm {

    private final Map<String, ?> map;
    private int hash = Integer.MAX_VALUE;

    public MapTerm(Map<String, ?> map) {
        this.map = requireNonNullArg(map, "map");
    }

    /**
     * Create a term with key-value pairs.
     * E.g. o("key_1", 1, "key_2", 2)
     *
     * @param kvs interleaved key-values
     * @return a new map
     */
    public static MapTerm of(Object... kvs) {
        if (kvs == null) {
            return new MapTerm(Map.of());
        }
        LinkedHashMap<String, Object> m = new LinkedHashMap<>(kvs.length / 2);
        for (int i = 0; i < kvs.length - 1; i += 2) {
            m.put(kvs[i].toString(), kvs[i + 1]);
        }
        return new MapTerm(m);
    }

    @Override
    public boolean has(String key) {
        return map.containsKey(key);
    }

    @Override
    public Object get(String key) {
        return key == null ? null : map.get(key);
    }

    @Override
    public List<String> keys() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public int hashCode() {
        if(hash == Integer.MAX_VALUE)
            this.hash = Terms.hashCode(this); // cache
        return hash;
    }
}
