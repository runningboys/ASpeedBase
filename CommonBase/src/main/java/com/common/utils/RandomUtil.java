package com.common.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 随机工具类
 *
 * @author LiuFeng
 * @data 2021/11/22 11:11
 */
public class RandomUtil {

    /**
     * 生成可重复的随机数
     *
     * @param min    最小值
     * @param max    最大值
     * @param length 生成长度
     * @return
     */
    public static List<Integer> generateRepeatableRandomNum(int min, int max, int length) {
        // 边界处理
        if (min > max) {
            throw new IllegalArgumentException();
        }

        int range = max - min + 1;
        Random random = new Random();
        List<Integer> numList = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            int value = random.nextInt(range) + min;
            numList.add(value);
        }

        return numList;
    }

    /**
     * 生成不重复的随机数
     *
     * @param min    最小值
     * @param max    最大值
     * @param length 生成长度
     * @return
     */
    public static List<Integer> generateNoRepeatRandomNum(int min, int max, int length) {
        int range = max - min + 1;

        // 边界处理
        if (min > max || length > range) {
            throw new IllegalArgumentException();
        }

        Random random = new Random();
        Set<Integer> numSet = new HashSet<>(length);
        List<Integer> numList = new ArrayList<>(length);

        // 生成数占比高于70%时，采用全范围逐个移除
        // 原因：占比较低时，随机值重复概率低；占比很高时，随机值重复概率将以指数级上升！
        if (length > range * 7 / 10) {
            // 加入全范围值
            for (int i = min; i <= max; i++) {
                numSet.add(i);
            }

            // 随机移除达到目标长度
            while (numSet.size() > length) {
                int value = random.nextInt(range) + min;
                numSet.remove(value);
            }

            // 加入list并打乱顺序
            numList.addAll(numSet);
            disturbRawList(numList);
            return numList;
        }

        // 生成数占比较低时，采用逐个生成
        for (int i = 0; i < length; i++) {
            // 随机不重复值
            while (true) {
                int value = random.nextInt(range) + min;
                if (!numSet.contains(value)) {
                    numSet.add(value);
                    numList.add(value);
                    break;
                }
            }
        }

        return numList;
    }


    /**
     * 扰乱集合数据（返回生成的新集合，原集合不变）
     *
     * @param dataList
     * @return
     */
    public static <T> List<T> disturbNewList(List<T> dataList) {
        return disturbRawList(new ArrayList<>(dataList));
    }

    /**
     * 扰乱原始集合数据
     *
     * @param dataList
     * @return
     */
    public static <T> List<T> disturbRawList(List<T> dataList) {
        Random random = new Random();
        int length = dataList.size();
        for (int i = 0; i < length; i++) {
            T value = dataList.get(i);
            int index = random.nextInt(length);
            dataList.set(i, dataList.get(index));
            dataList.set(index, value);
        }

        return dataList;
    }
}
