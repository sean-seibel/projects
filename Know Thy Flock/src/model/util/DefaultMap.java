package model.util;

import java.util.HashMap;

public class DefaultMap<K,V> extends HashMap<K,V> {

  protected V defaultValue;

  public DefaultMap(V def) {
    defaultValue = def;
  }

  @Override
  public V get(Object key) {
    return getOrDefault(key, defaultValue);
  }
}
