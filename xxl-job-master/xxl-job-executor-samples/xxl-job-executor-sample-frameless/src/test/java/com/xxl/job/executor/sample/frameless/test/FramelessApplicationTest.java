package com.xxl.job.executor.sample.frameless.test;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class FramelessApplicationTest {

    @Test
    public void test(){
        Map<String, Object> map = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(o1.compareToIgnoreCase(o2), 0);
            }
        }) ;

        Map<String, Object> map1 = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        map.put("AAA", "AAAAAAAAAA");
        map.put("aaa", "aaaaaaaaaa");
        map.put("MMM", "MMMMMMMMMMM");
        map.put("mmm", "mmmmmmmmmmm");
        map.put("SSS", "SSSSSSSSSSSSS");
        map.put("sss", "ssssssssssss");
        map.put("AaA", "哈哈哈");
        for (Map.Entry<String, Object> et : map.entrySet()) {
            System.out.println("遍历map：--" + et.getKey() + "----" + et.getValue());
        }
        System.out.println(map.get("aaa"));
        System.out.println(map.get("mmm"));
        System.out.println(map.get("sss"));
        System.out.println(map.get("Aaa"));

    }

}
