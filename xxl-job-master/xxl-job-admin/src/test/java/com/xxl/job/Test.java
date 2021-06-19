package com.xxl.job;

import ch.qos.logback.core.net.SyslogOutputStream;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Test {
    public static void main(String[] args) {
       /* String s = "1/2";
        System.out.println(s.contains("/"));
        String[] s1 = s.split("/");
        for (String s2 : s1){
            System.out.println(s2);
        }
        StringBuffer stringBuffer = new StringBuffer("1,2,3,4,5,");
        System.out.println(stringBuffer.lastIndexOf(","));
//        stringBuffer.delete(stringBuffer.lastIndexOf(","),stringBuffer.length());
        stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));
        System.out.println(stringBuffer);
        System.out.println(stringBuffer.toString());
        String ww = "12W";
        System.out.println(ww.substring(0,ww.lastIndexOf("W")));
        String sss = "2021-2022";

        String sssss = "RelatedInformation";
        System.out.println(sssss.toUpperCase(Locale.US));

        String ssssss = null;
        System.out.println(ssssss);

        String sssssss = "\n\nsdsdsds";
        System.out.println(sssssss.split("\n\n")[0] + sssssss.split("\n\n")[1]);
        String js = "triggerDayCountSuc";
        System.out.println(js.toUpperCase());

        Date date = new Date(1617354000000L);
        System.out.println(date);

        String sssssssss = "month-1";
        System.out.println(sssssssss.substring(5));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,0);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));

        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusMonths(0);
        System.out.println(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println(localDateTime.with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println(localDateTime.with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        LocalDate localDate = LocalDate.now();
        System.out.println(localDate.toString());
        System.out.println("-----------------------------------------------------------------------------------------------");


        Calendar calendar1 = Calendar.getInstance();
        // 设置时间,当前时间不用设置
        calendar1.set(Calendar.YEAR, Integer.parseInt("2018"));
        calendar1.set(Calendar.MONTH, Integer.parseInt("2"));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd ").format(calendar1.getTime()));

        calendar1.add(Calendar.MONTH, 3);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd ").format(calendar1.getTime()));

        calendar1.set(Calendar.DAY_OF_MONTH,12);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd ").format(calendar1.getTime()));
        calendar1.set(Calendar.DAY_OF_MONTH,0);

        System.out.println(calendar1.getActualMaximum(Calendar.DATE));
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd ").format(calendar1.getTime()));

        calendar1.set(Calendar.DATE, calendar1.getActualMaximum(Calendar.DATE));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd ").format(calendar1.getTime()));

        
        Map<String, String> map = new HashMap<>();
        map.put("111","sss");
        map.put("222","fff");
        for (String key : map.keySet()) {
            System.out.println(key);
        }
        System.out.println(map.size());
        System.out.println(map.get("111"));

        ConcurrentHashMap<String, String> map1 = new ConcurrentHashMap<>();
        map1.put("111","aaa");
        map1.put("null","");

        String te = "123s";
        int i =  te.indexOf('s');
        System.out.println(i);

        String re = null;
        int ee = Integer.parseInt(re);*/

        int y, m, d, h, mi, s;
        Calendar cal = Calendar.getInstance();
        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DATE);
        String startDate = y + "-" + (m + 1) + "-" + d + " 00:00:00";
        Date startdate = null;
        Date nowdate = null;
        GregorianCalendar now = new GregorianCalendar();
        SimpleDateFormat fmtrq = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = fmtrq.format(now.getTime());

        System.out.println(startDate);
        System.out.println(nowDate);

        Map<String, String> map1 = new HashMap<>();
        map1.put("1","1");
        map1.put("2","2");
        map1.put("3","3");
        Map<String, Map<String,String>> map2 = new HashMap<>();
        map2.put("A",map1);
        Map<String, Map<String, Map<String,String>>> map3 = new HashMap<>();
        map3.put("B",map2);
        List<Map<String, Map<String, Map<String,String>>>> list1 = new LinkedList<>();
        list1.add(map3);
        List<List<Map<String, Map<String, Map<String,String>>>>> list2 = new LinkedList<>();
        list2.add(list1);
        System.out.println(list2);

    }
}
