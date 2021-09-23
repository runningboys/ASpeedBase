package com.common.skin.attr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * 皮肤属性的数据构造者
 *
 * @author LiuFeng
 * @data 2021/8/24 18:17
 */
public class SkinAttrBuilder {
    private static final int MAX_POOL_SIZE = 6;
    private final Map<String, String> attrMap = new HashMap<>();
    private final static Queue<SkinAttrBuilder> pools = new LinkedList<>();

    private SkinAttrBuilder() {}

    /**
     * 获取复用池对象
     *
     * @return
     */
    public static SkinAttrBuilder obtain() {
        if (!pools.isEmpty()) {
            return pools.poll();
        }

        return new SkinAttrBuilder();
    }

    /**
     * 保存皮肤属性
     *
     * @param attrType
     * @param resId
     * @return
     */
    public SkinAttrBuilder saveAttr(SkinAttrType attrType, int resId) {
        String attrName = SkinAttrSupport.getSkinEntryName(resId);
        if (attrName != null) {
            attrMap.put(attrType.type, attrName);
        }
        return this;
    }

    /**
     * 保存皮肤属性
     *
     * @param attrType
     * @param attrValueName
     * @return
     */
    public SkinAttrBuilder saveAttr(SkinAttrType attrType, String attrValueName) {
        attrMap.put(attrType.type, attrValueName);
        return this;
    }

    /**
     * 保存皮肤属性
     *
     * @param skinAttrs
     * @return
     */
    public SkinAttrBuilder saveAttrs(List<SkinAttr> skinAttrs) {
        for (SkinAttr attr : skinAttrs) {
            attrMap.put(attr.attrType.type, attr.attrValueName);
        }
        return this;
    }

    /**
     * 将属性构建为指定格式
     *
     * @return
     */
    public String build() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : attrMap.entrySet()) {
            String type = entry.getKey();
            String value = entry.getValue();
            builder.append(type);
            builder.append(":");
            builder.append(value);
            builder.append("|");
        }

        return builder.toString();
    }

    /**
     * 判断属性是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return attrMap.isEmpty();
    }

    /**
     * 清空属性
     */
    public void clear() {
        attrMap.clear();
    }

    /**
     * 回收对象
     */
    public synchronized void recycle() {
        clear();
        if (pools.size() < MAX_POOL_SIZE) {
            pools.offer(this);
        }
    }
}
