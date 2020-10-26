package com.common.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义LRU缓存
 * 描述：基于双向循环链表和散列表实现的LRU缓存
 * 时间复杂度：get和put都为O(1)
 *
 * @author LiuFeng
 * @data 2020/2/20 10:10
 */
public class CustomLRUCache<K, V> implements ICache<K, V> {

    // 缓存键的最大数量
    private int keyMaxNum;

    // 双向循环链表头结点
    private Node<K> head;

    // 关联key和结点数据
    private Map<K, Node<K>> nodeMap = new ConcurrentHashMap<>();

    // 缓存数据
    private Map<K, V> cacheMap = new ConcurrentHashMap<>();

    /**
     * 构造方法
     *
     * @param keyMaxNum 缓存键的最大数量
     */
    public CustomLRUCache(int keyMaxNum) {
        this.keyMaxNum = keyMaxNum;
    }


    /**
     * 存入缓存
     *
     * @param key
     * @param value
     */
    @Override
    public synchronized void put(K key, V value) {
        // 超出缓存容量、且无此缓存数据时，移除尾结点的key
        if (cacheMap.size() >= keyMaxNum && !cacheMap.containsKey(key)) {
            Node<K> tail = head.prev;
            remove(tail.item);
        }

        // 缓存数据
        cacheMap.put(key, value);

        Node<K> node = nodeMap.get(key);
        if (node != null) {
            // 当前结点是头结点时，就不更新结点位置
            if (node == head) {
                return;
            }

            // 存在前驱结点时，将前驱结点next指针指向后继结点
            if (node.prev != node) {
                node.prev.next = node.next;
            }

            // 存在后继结点时，将后继结点prev指针指向前驱结点
            if (node.next != node) {
                node.next.prev = node.prev;
            }
        } else {
            node = new Node<>(key, null, null);
        }

        // 头结点为空时，此时为链表无数据，让第一个结点前后指针都指向自己
        if (head == null) {
            node.prev = node;
            node.next = node;
        } else {
            // 头结点的前驱结点即tail尾结点
            Node<K> tail = head.prev;

            // 修改当前结点前后指针
            node.prev = tail;
            node.next = tail.next;

            // 修改head头结点prev指针，指向新的头结点
            tail.next.prev = node;

            // 修改tail尾结点next指针，指向新的头结点
            tail.next = node;
        }

        // 保存最新头结点数据
        head = node;
        nodeMap.put(key, node);
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    @Override
    public synchronized V get(K key) {
        Node<K> node = nodeMap.get(key);
        if (node != null && node != head) {
            // 存在前驱结点时，将前驱结点next指针指向后继结点
            if (node.prev != node) {
                node.prev.next = node.next;
            }

            // 存在后继结点时，将后继结点prev指针指向前驱结点
            if (node.next != node) {
                node.next.prev = node.prev;
            }

            // 头结点的前驱结点即tail尾结点
            Node<K> tail = head.prev;

            // 修改当前结点前后指针
            node.prev = tail;
            node.next = tail.next;

            // 修改head头结点prev指针，指向新的头结点
            tail.next.prev = node;

            // 修改tail尾结点next指针，指向新的头结点
            tail.next = node;

            // 保存最新头结点数据
            head = node;
            nodeMap.put(key, node);
        }

        return cacheMap.get(key);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    @Override
    public synchronized void remove(K key) {
        Node<K> node = nodeMap.get(key);
        if (node != null) {
            // 存在前驱结点时，将前驱结点next指针指向后继结点
            if (node.prev != node) {
                node.prev.next = node.next;
            }

            // 存在后继结点时，将后继结点prev指针指向前驱结点
            if (node.next != node) {
                node.next.prev = node.prev;
            }

            // 移除的是头结点时
            if (node == head) {
                // 链表仅包含一个结点时，head置空，否则指向后继结点
                if (node == head.next) {
                    head = null;
                } else {
                    head = head.next;
                }
            }

            nodeMap.remove(key);
            cacheMap.remove(key);
        }
    }

    /**
     * 缓存中是否存在
     *
     * @param key
     * @return
     */
    @Override
    public synchronized boolean containsKey(K key) {
        return cacheMap.containsKey(key);
    }

    /**
     * 判断是否为空
     *
     * @return
     */
    @Override
    public synchronized boolean isEmpty() {
        return cacheMap.isEmpty();
    }

    /**
     * 清空缓存
     */
    @Override
    public synchronized void clear() {
        head = null;
        nodeMap.clear();
        cacheMap.clear();
    }

    /**
     * 双向链表结点
     *
     * @param <T>
     */
    private class Node<T> {
        T item;
        Node<T> prev;
        Node<T> next;

        Node(T element, Node<T> prev, Node<T> next) {
            this.item = element;
            this.prev = prev;
            this.next = next;
        }
    }
}
