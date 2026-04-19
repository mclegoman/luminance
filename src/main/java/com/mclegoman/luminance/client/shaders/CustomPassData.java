package com.mclegoman.luminance.client.shaders;

import java.util.HashMap;
import java.util.Map;

public interface CustomPassData {
    CustomPassData copy();

    class CustomPassDataMap<K,V> extends HashMap<K,V> implements CustomPassData {
        public CustomPassDataMap(int initialCapacity) {
            super(initialCapacity);
        }

        public CustomPassDataMap(Map<? extends K, ? extends V> m) {
            super(m);
        }

        @Override
        public CustomPassData copy() {
            return new CustomPassDataMap<>(this);
        }
    }
}
