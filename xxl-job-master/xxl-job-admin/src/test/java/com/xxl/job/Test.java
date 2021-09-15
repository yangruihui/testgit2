package com.xxl.job;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import sun.security.provider.MD5;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) throws JSONException, UnsupportedEncodingException, NoSuchAlgorithmException {
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

        /*int y, m, d, h, mi, s;
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

        StringBuilder ls = new StringBuilder();
        System.out.println("".contentEquals(ls));

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        String time = sf.format(c.getTime());
        System.out.println(time);

        String gf = "123456789*-OFD";
        System.out.println(gf.substring(0,gf.length()-4));

        getIndexOf(gf,"2",1);

        String ll = "1234567";
        System.out.println(ll.contains("2"));


        String iy = "123456789";
        System.out.println(Integer.parseInt(iy.substring(iy.length() - 2)));

        StringBuffer dsd = new StringBuffer(iy);
        System.out.println(dsd);

        String text = "{\"claimPhone\":\"13111175955\",\"policyNo\":\"P10032021001701000063\",\"claimReason\":\"测试\",\"claimName\":\"测试姓名\"}";
        byte[] textByte = text.getBytes("UTF-8");
        Base64 base64 = new Base64();
        String encodedText = base64.encodeToString(textByte);
        System.out.println(encodedText);
        String AppId = "tb15b75d1862da1d48";
        String requestId = "7bf041b160df45e1b8e8f25382cb4748";
        String slat = "923456";
        String timeStamp = "1625533924";
        String fgfg = AppId + requestId + slat + timeStamp + encodedText;
        String result = "";
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte b[] = md.digest(fgfg.getBytes());
        StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < b.length; i++) {
            if (Integer.toHexString(0xFF & b[i]).length() == 1)
                buf.append("0").append(Integer.toHexString(0xFF & b[i]));
            else
                buf.append(Integer.toHexString(0xFF & b[i]));
        }
        result = buf.toString();//转换成字符串
        System.out.println("MD5(" + fgfg + ",32) = " + result);//输出32位16进制字符串
        System.out.println("MD5(" + fgfg + ",16) = " + buf.toString().substring(8, 24));//输出16位16进制字符串

        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sim.format(new Date(1622625533929L)));

        List<String> list1 = new ArrayList<>();
        System.out.println(list1.size());

        String llslsl = "{\"policyNo\":\"P16112021001701007469\",\"backStatus\":\"2\"}";
        String sas = "BNsnz73E7id3x441nsVYMXdrn1sOMKZKqAJixBSr0IqjPrtwTl0eIUf/t4nhW4Kloo+hngkpDOuaOUdTtCMq/v/mJys9HtnkvRTnCM+O6e/BZIcTN8fLSnHW0lkezy4g/qXmh1jOHxvfgO4Q2r4sawYX8Mdz53WcbrGn8zEyzD78zy3s2FoDFpfhOgJSnpKk9lAiI1xjOSFdHMGV/3Mb4ZYp73kV7QVJfnGtbj/0PKz+vsTe/RmEuzx1Or/Y6nzFxEftihGky1ib2qB1VWUIDFJ+XFAy+Opp9i6F/KVy0w==";
        JsonParser jsonParser =  new JsonParser();
        JsonElement jsonElement = jsonParser.parse(llslsl);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        System.out.println(jsonObject);
        Gson gson = new Gson();
        JsonObject test = gson.fromJson(llslsl, JsonObject.class);
        String testStr = test.toString();
        System.out.println(testStr);


        String timeStamp111 = String.valueOf(new Date().getTime());
        System.out.println(timeStamp111);
        System.out.println(sim.format(new Date(1628061907033L)));*/

        /*String sssss = "reportLossFee \n remark \n damageAddress \n reportorName \n reportorNumber \n createTime \n attachFlag \n claimId \n resourceType \n applyNo \n claimerName \n claimerCode \n agentEmail \n receiveAccount \n receiveAccountName \n receiveBankName \n receiveBankNo \n requestDate \n fileUrl";
        System.out.println(sssss.toUpperCase(Locale.US));*/

        String llslsl = "{\n" +
                " \"amount\": \"30000\",\n" +
                " \"receive_account_name\": null,\n" +
                " \"guarantee_no\": \"P16162021001701022179\",\n" +
                " \"agent_email\": \"\",\n" +
                " \"apply_no\": \"06645629\",\n" +
                " \"agent_phone\": \"13978562898\",\n" +
                " \"receive_bank_name\": null,\n" +
                " \"claim_reason\": \"测试索赔，请设置索赔结果\",\n" +
                " \"receive_account\": null,\n" +
                " \"claimer_code\": \"KDJFL2342343553243\",\n" +
                " \"claim_evidence_url\": \"http://219.142.102.242/gtm/static/glodon.gtm.deposit.demo/files/timg.jpg\",\n" +
                " \"claimer_name\": \"牛大阿叔带领一群年轻小伙\",\n" +
                " \"receive_bank_no\": \"308100005336\"\n" +
                "}";
        String sas = "BNsnz73E7id3x441nsVYMXdrn1sOMKZKqAJixBSr0IqjPrtwTl0eIUf/t4nhW4Kloo+hngkpDOuaOUdTtCMq/v/mJys9HtnkvRTnCM+O6e/BZIcTN8fLSnHW0lkezy4g/qXmh1jOHxvfgO4Q2r4sawYX8Mdz53WcbrGn8zEyzD78zy3s2FoDFpfhOgJSnpKk9lAiI1xjOSFdHMGV/3Mb4ZYp73kV7QVJfnGtbj/0PKz+vsTe/RmEuzx1Or/Y6nzFxEftihGky1ib2qB1VWUIDFJ+XFAy+Opp9i6F/KVy0w==";
        JsonParser jsonParser =  new JsonParser();
        JsonElement jsonElement = jsonParser.parse(llslsl);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        System.out.println(jsonObject);
        Gson gson = new Gson();
        JsonObject test = gson.fromJson(llslsl, JsonObject.class);
        String testStr = test.toString();
        System.out.println(testStr);

        String s = "businessType";
        System.out.println(s.toUpperCase());
    }

    /**
     * @param data 指定字符串
     * @param str 需要定位的特殊字符或者字符串
     * @param num   第n次出现
     * @return  第n次出现的位置索引
     */
    public static int getIndexOf(String data,String str,int num){
        Pattern pattern = Pattern.compile(str);
        Matcher findMatcher = pattern.matcher(data);
        //标记遍历字符串的位置
        int indexNum=0;
        while(findMatcher.find()) {
            indexNum++;
            if(indexNum==num){
                break;
            }
        }
        System.out.println("字符或者字符串"+str+"第"+num+"次出现的位置为："+findMatcher.start());
        return findMatcher.start();
    }

}
