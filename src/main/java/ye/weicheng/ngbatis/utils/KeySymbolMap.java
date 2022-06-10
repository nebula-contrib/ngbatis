// Copyright 2022-present Weicheng Ye. All rights reserved.
// Use of this source code is governed by a MIT-style license that can be
// found in the LICENSE file.
package ye.weicheng.ngbatis.utils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 键名含 . 运算符的地图，将 key 根据 . 运算符进行切割，形成嵌套的 Map 对象。<br>
 *     主要用途在于可以通过 . 表达式深层次获取 表达式所指数据对象，不至于造成数据凌乱
 *
 * @author yeweicheng
 * <br>Now is history!
 */
public class KeySymbolMap extends HashMap<String, Object>{

    private final Map<String, Object> map;
    private String split;

    public KeySymbolMap(Map<String, Object> map, String split ){
        if (map == null) {
            throw new IllegalArgumentException("map is null.");
        }
        this.map = map;
        this.split = split;
        recursionConstructor();
    }

    public Map<String, Object> getOneDMap() {
        return getOneDMap( this.split );
    }

    public Map<String, Object> getOneDMap( String split ) {
        return getOneDMap( "", split );
    }

    private Map<String, Object> getOneDMap( String prefix, String split ) {
        Map<String, Object> oneDMap = new HashMap<>();
        for ( Entry<String, Object> entry : this.map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String newKey = isBlank( prefix ) ? key : ( prefix + split  + key );
            if( value instanceof KeySymbolMap ) {
                ((KeySymbolMap) value).putOneDMap( oneDMap, newKey, split );
            } else if ( value instanceof Collection ) {
                int i = 0;
                for( KeySymbolMap mapInColl : ( Collection<KeySymbolMap>)value ) {
                    String newKeyInColl = newKey + split + i;
                    mapInColl.putOneDMap( oneDMap, newKeyInColl, split );
                    i ++;
                }
            } else {
                oneDMap.put( newKey, value );
            }
        }
        return oneDMap;
    }

    private void putOneDMap( Map<String, Object> result, String prefix, String split  ) {
        Map<String, Object> oneDMapInner = this.getOneDMap(prefix, split);
        result.putAll( oneDMapInner );
    }

    public void recursionConstructor() {
        for( Entry me : map.entrySet() ) {
            Object value = me.getValue();
            if( value instanceof Map && ! (value instanceof KeySymbolMap) ) {
                me.setValue( new KeySymbolMap( (Map)value, this.split ) );
            } else if( value instanceof List ) {
                int size = ((List) value).size();
                for( int i = 0 ; i < size; i ++ ) {
                    Object o = ((List) value).get(i);
                    if( o instanceof Map ) {
                        KeySymbolMap element = new KeySymbolMap((Map<String, Object>) o, this.split);
                        ((List) value).set( i , element);
                    }
                }
            }
        }
    }

    public Object get( Object keyDot ) {
        if( keyDot == null ) return null;
        String[] split = keyDot.toString().split(this.split);
        Queue<String> queue = new ConcurrentLinkedQueue( Arrays.asList( split ) );
        Object o = this.get(queue, null);
        return o;
    }


    public Object get( Queue<String> queue, String collectionInner ) {
        if( queue.size() != 0 ) {
            String currentKey = collectionInner == null ? queue.poll() : collectionInner;
            boolean isLeaf = queue.size() == 0;

            Object data = map.get( currentKey );

            if( isLeaf) {
                return data;
            }

            if( data instanceof KeySymbolMap ) {
                return ((KeySymbolMap) data).get( queue, null );
            } else if( data instanceof Collection ) {
                Iterator iterator = ((Collection) data).iterator();
                String collectionInnerKey = queue.poll();
                List value = new ArrayList();
                while ( iterator.hasNext() ) {
                    Object next = iterator.next();
                    if( next instanceof KeySymbolMap ) {
                        Object o = ((KeySymbolMap) next).get(queue, collectionInnerKey);
                        value.add( o );
                    }
                }
                return value;
            }
            return data;
        }
        return map.get( collectionInner );
    }

    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    public KeySymbolMap fluentPut(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
        map.putAll(m);
    }

    public KeySymbolMap fluentPutAll(Map<? extends String, ? extends Object> m) {
        map.putAll(m);
        return this;
    }

    public void clear() {
        map.clear();
    }

    public KeySymbolMap fluentClear() {
        map.clear();
        return this;
    }

    public Object remove(String key) {
        return map.remove(key);
    }

    public KeySymbolMap fluentRemove(String key) {
        map.remove(key);
        return this;
    }

    public Object remove(Object key) {
        return map.remove(key);
    }

    public KeySymbolMap fluentRemove(Object key) {
        map.remove(key);
        return this;
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Collection<Object> values() {
        return map.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }
    
}
