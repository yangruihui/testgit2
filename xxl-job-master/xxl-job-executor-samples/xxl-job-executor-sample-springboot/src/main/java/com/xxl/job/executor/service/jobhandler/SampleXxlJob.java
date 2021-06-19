package com.xxl.job.executor.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import net.sf.json.JSONObject;
import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
public class SampleXxlJob {
    private static Logger logger = LoggerFactory.getLogger(SampleXxlJob.class);

    /**
     * 执行数据库存储过程的JobHandler
     * @throws Exception
     */
    @XxlJob("JDBCJobHandler")
    public void testJDBCHandler() throws Exception {
        XxlJobHelper.log("testJDBCHandler start...");
        System.out.println("testJDBCHandler start...");

        //获取请求参数  数据库配置信息+执行sql
        String param = XxlJobHelper.getJobParam();
        System.out.println(param);
        if (param == null || param.trim().length() == 0) {
            XxlJobHelper.log("param[" + param + "] invalid.");
            XxlJobHelper.handleFail();
            return;
        }

        String[] params = param.split("\n\n");
        //执行sql
        String[] paramSql = new String[0];
        if (!"".equals(params[0]) && params[0].length() != 0) {
            paramSql = params[0].split("\n");
        }
        if (paramSql.length == 0) {
            XxlJobHelper.log("param[" + paramSql + "] invalid.");
            XxlJobHelper.handleFail();
            return;
        }

        //数据库配置信息
        String[] paramDatabase = new String[4];
        if (!"".equals(params[1]) && params[1].length() != 0) {
            paramDatabase = params[1].split("\n");
        }

        //执行存储过程的sql
        String sql = null;
        boolean isProcedure = true;
        if (paramSql[0].startsWith("call")) {
            sql = "{" + paramSql[0] + "}";
        } else {
            sql = paramSql[0];
            isProcedure = false;
        }

        Connection connection = null;
        //执行存储过程
        CallableStatement call= null;
        //执行sql语句
        PreparedStatement preparedStatement = null;
        try {
            Long start = System.currentTimeMillis();
            List<Object> resultList = new ArrayList<>();
            //创建数据库连接
            connection = getConnection(paramDatabase);
            if (connection == null) {
                System.out.println("数据库连接失败！");
                XxlJobHelper.handleFail();
                XxlJobHelper.log("数据库连接失败");
                return;
            }
            if (isProcedure) {
                call = connection.prepareCall(sql);

                //整理请求参数-存储过程
                Map<Integer,String> outMap = reorganizeParams(call, paramSql);

                //执行
                call.execute();

                //整理返回结果
                String resultString;
                int resultInt;
                Date resultDate;
                for (Integer key : outMap.keySet()) {
                    if ("String".equals(outMap.get(key))) {
                        resultString = call.getString(key);
                        resultList.add(resultString);
                    }
                    if ("int".equals(outMap.get(key))) {
                        resultInt = call.getInt(key);
                        resultList.add(resultInt);
                    }
                    if ("date".equals(outMap.get(key))){
                        resultDate = call.getTimestamp(key);
                        resultList.add(resultDate);
                    }
                }
            } else {
                preparedStatement = connection.prepareStatement(sql);
                int count = 0;
                for (int i = 0;i < sql.length();i ++) {
                    if (sql.charAt(i) == '?') {
                        count ++;
                    }
                }

                //执行sql语句时替换sql中的参数
                reorganizeSql(preparedStatement, count, paramSql);
                //执行
                preparedStatement.execute();

            }
            Long end = System.currentTimeMillis();
            System.out.println("耗时" + (end - start)/1000 + "秒");
            System.out.println(resultList.toString());
        } catch (Exception e) {
            e.printStackTrace();
            XxlJobHelper.log(e);
            XxlJobHelper.handleFail();
        } finally {
            //关闭数据库相关资源
            if (isProcedure) {
                release(connection, call, null);
            } else {
                release(connection, preparedStatement, null);
            }
        }
    }

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        XxlJobHelper.log("XXL-JOB, Hello World.");

        for (int i = 0; i < 5; i++) {
            XxlJobHelper.log("beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }
        // default success
    }


    /**
     * 2、分片广播任务
     */
    @XxlJob("shardingJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        XxlJobHelper.log("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);

        // 业务逻辑
        for (int i = 0; i < shardTotal; i++) {
            if (i == shardIndex) {
                XxlJobHelper.log("第 {} 片, 命中分片开始处理", i);
            } else {
                XxlJobHelper.log("第 {} 片, 忽略", i);
            }
        }

    }


    /**
     * 3、命令行任务
     */
    @XxlJob("commandJobHandler")
    public void commandJobHandler() throws Exception {
        String command = XxlJobHelper.getJobParam();
        int exitValue = -1;

        BufferedReader bufferedReader = null;
        try {
            // command process
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            //Process process = Runtime.getRuntime().exec(command);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(process.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));

            // command log
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                XxlJobHelper.log(line);
            }

            // command exit
            process.waitFor();
            exitValue = process.exitValue();
        } catch (Exception e) {
            XxlJobHelper.log(e);
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        if (exitValue == 0) {
            // default success
        } else {
            XxlJobHelper.handleFail("command exit value(" + exitValue + ") is failed");
        }

    }


    /**
     * 4、跨平台Http任务
     * 参数示例：
     * "url: http://www.baidu.com\n" +
     * "method: get\n" +
     * "data: content\n";
     */
    @XxlJob("httpJobHandler")
    public void httpJobHandler() throws Exception {

        // param parse
        String param = XxlJobHelper.getJobParam();
        if (param == null || param.trim().length() == 0) {
            XxlJobHelper.log("param[" + param + "] invalid.");

            XxlJobHelper.handleFail();
            return;
        }

        String[] httpParams = param.split("\n");
        String url = null;
        String method = null;
        String data = null;
        for (String httpParam : httpParams) {
            if (httpParam.startsWith("url:")) {
                url = httpParam.substring(httpParam.indexOf("url:") + 4).trim();
            }
            if (httpParam.startsWith("method:")) {
                method = httpParam.substring(httpParam.indexOf("method:") + 7).trim().toUpperCase();
            }
            if (httpParam.startsWith("data:")) {
                data = httpParam.substring(httpParam.indexOf("data:") + 5).trim();
            }
        }

        // param valid
        if (url == null || url.trim().length() == 0) {
            XxlJobHelper.log("url[" + url + "] invalid.");

            XxlJobHelper.handleFail();
            return;
        }
        if (method == null || !Arrays.asList("GET", "POST").contains(method)) {
            XxlJobHelper.log("method[" + method + "] invalid.");

            XxlJobHelper.handleFail();
            return;
        }
        boolean isPostMethod = method.equals("POST");

        // request
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            // connection
            URL realUrl = new URL(url);
            connection = (HttpURLConnection) realUrl.openConnection();

            // connection setting
            connection.setRequestMethod(method);
            connection.setDoOutput(isPostMethod);
            connection.setDoInput(true);
            connection.setUseCaches(false);
//            connection.setReadTimeout(5 * 1000);
//            connection.setConnectTimeout(3 * 1000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");

            // do connection
            connection.connect();

            // data
            if (isPostMethod && data != null && data.trim().length() > 0) {
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(data.getBytes("UTF-8"));
                dataOutputStream.flush();
                dataOutputStream.close();
            }

            // valid StatusCode
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                throw new RuntimeException("Http Request StatusCode(" + statusCode + ") Invalid.");
            }

            // result
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            String responseMsg = result.toString();

            JSONObject jsonObject = JSONObject.fromObject(responseMsg);
            if (!"SUCCESS".equals(jsonObject.getString("status"))) {
                XxlJobHelper.handleFail(jsonObject.getString("message"));
            }
            XxlJobHelper.log(responseMsg);

            return;
        } catch (Exception e) {
            XxlJobHelper.log(e);

            XxlJobHelper.handleFail();
            return;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                XxlJobHelper.log(e2);
            }
        }

    }

    /**
     * 5、生命周期任务示例：任务初始化与销毁时，支持自定义相关逻辑；
     */
    @XxlJob(value = "demoJobHandler2", init = "init", destroy = "destroy")
    public void demoJobHandler2() throws Exception {
        XxlJobHelper.log("XXL-JOB, Hello World.");
    }

    public void init() {
        logger.info("init");
    }

    public void destroy() {
        logger.info("destory");
    }

    /**
     * 创建数据库连接
     * @return
     */
    private Connection getConnection(String[] paramDatabase) {
        //获取数据库连接属性,
        String driver = null;
        String url = null;
        String username = null;
        String password = null;
        for (String param : paramDatabase) {
            if (param.startsWith("driver:")) {
                driver = param.substring(param.indexOf("driver:") + 7).trim();
            }
            if (param.startsWith("url:")) {
                url = param.substring(param.indexOf("url:") + 4).trim();
            }
            if (param.startsWith("username:")) {
                username = param.substring(param.indexOf("username:") + 9).trim();
            }
            if (param.startsWith("password:")) {
                password = param.substring(param.indexOf("password:") + 9).trim();
            }
        }

        //加载数据库驱动
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.out.println("找不到驱动程序类 ，加载驱动失败！");
            e.printStackTrace();
            XxlJobHelper.handleFail();
        }

        //创建数据库连接
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("数据库连接成功！");
            return connection;
        } catch (SQLException e) {
            System.out.println("数据库连接失败！");
            e.printStackTrace();
            XxlJobHelper.handleFail();
        }
        return null;
    }

    /**
     * 1、整理请求参数到存储过程执行sql语句中，in
     * 2、声明返回参数，out
     * @param call
     * @param paramSql
     * @return
     */
    private Map<Integer, String> reorganizeParams(CallableStatement call,String[] paramSql) {
        Map<Integer, String> outMap = new HashMap<>();
        try{
            for (int i = 1; i < paramSql.length; i ++) {
                String[] paramType = paramSql[i].split(":");
                if (paramType[0] != null && paramType[0].length() > 0){
                    //对于in参数进行赋值
                    if ("in".equals(paramType[0])) {
                        String[] dataTypeIn = new String[0];
                        //分割参数类型与参数
                        if (paramType[1] != null && paramType[1].length() > 0) {
                            dataTypeIn = paramType[1].split("/");
                        }
                        if ("String".equals(dataTypeIn[0])) {
                            if (dataTypeIn[1].contains("now")) {
                                if (dataTypeIn[1].endsWith("now")) {
                                    Date date = new Date();
                                    call.setString(i, new SimpleDateFormat("yyyy-MM-dd").format(date));
                                } else {
                                    if (!dataTypeIn[1].contains("&")) {
                                        //现在的前几天或后几天
                                        String changeTime = dataTypeIn[1].substring(3);
                                        LocalDateTime localDateTime = LocalDateTime.now();
                                        localDateTime = localDateTime.plusDays(Integer.parseInt(changeTime));
                                        call.setString(i, localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                    } else {
                                        String[] dateParams = dataTypeIn[1].split("&");
                                        LocalDateTime localDateTime = LocalDateTime.now();
                                        //现在的前几天或后几天
                                        if (dateParams.length > 0 && !dateParams[0].endsWith("now")) {
                                            String changeDay = dateParams[0].substring(3);
                                            localDateTime = localDateTime.plusDays(Integer.parseInt(changeDay));
                                        }
                                        //现在的前几月或后几月
                                        if (dateParams.length > 1) {
                                            String changeMonth = dateParams[1].substring(5);
                                            localDateTime = localDateTime.plusMonths(Integer.parseInt(changeMonth));
                                        }
                                        //当前月的第一天或最后一天
                                        if (dateParams.length > 2) {
                                            if ("first".equals(dateParams[2])) {
                                                localDateTime = localDateTime.with(TemporalAdjusters.firstDayOfMonth());
                                            } else if ("last".equals(dateParams[2])) {
                                                localDateTime = localDateTime.with(TemporalAdjusters.lastDayOfMonth());
                                            }
                                        }
                                        call.setString(i,localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                    }
                                }
                            } else {
                                call.setString(i, dataTypeIn[1]);
                            }
                        }
                        if ("int".equals(dataTypeIn[0])) {
                            call.setInt(i, Integer.parseInt(dataTypeIn[1]));
                        }
                        if ("date".equals(dataTypeIn[0])) {
                            if (dataTypeIn[1].endsWith("now")) {
                                call.setTimestamp(i, new Timestamp(System.currentTimeMillis()));
                            } else {
                                if (!dataTypeIn[1].contains("&")) {
                                    //现在的前几天或后几天
                                    String changeTime = dataTypeIn[1].substring(3);
                                    LocalDateTime localDateTime = LocalDateTime.now();
                                    localDateTime = localDateTime.plusDays(Integer.parseInt(changeTime));
                                    call.setTimestamp(i, Timestamp.valueOf(localDateTime));
                                } else {
                                    String[] dateParams = dataTypeIn[1].split("&");
                                    LocalDateTime localDateTime = LocalDateTime.now();
                                    //现在的前几天或后几天
                                    if (dateParams.length > 0 && !dateParams[0].endsWith("now")) {
                                        String changeDay = dateParams[0].substring(3);
                                        localDateTime = localDateTime.plusDays(Integer.parseInt(changeDay));
                                    }
                                    //现在的前几月或后几月
                                    if (dateParams.length > 1) {
                                        String changeMonth = dateParams[1].substring(5);
                                        localDateTime = localDateTime.plusMonths(Integer.parseInt(changeMonth));
                                    }
                                    //当前月的第一天或最后一天
                                    if (dateParams.length > 2) {
                                        if ("first".equals(dateParams[2])) {
                                            localDateTime = localDateTime.with(TemporalAdjusters.firstDayOfMonth());
                                        } else if ("last".equals(dateParams[2])) {
                                            localDateTime = localDateTime.with(TemporalAdjusters.lastDayOfMonth());
                                        }
                                    }
                                    call.setTimestamp(i,Timestamp.valueOf(localDateTime));
                                }
                            }
                        }
                    }
                    //对于out参数进行声明
                    if ("out".equals(paramType[0])) {
                        String[] dataTypeOut = new String[0];
                        if (paramType[1] != null && paramType[1].length() > 0) {
                            dataTypeOut = paramType[1].split("/");
                        }
                        if ("String".equals(dataTypeOut[0])) {
                            call.registerOutParameter(i, OracleTypes.VARCHAR);
                            outMap.put(i, "String");
                        }
                        if ("int".equals(dataTypeOut[0])) {
                            call.registerOutParameter(i, OracleTypes.NUMBER);
                            outMap.put(i, "int");
                        }
                        if ("date".equals(dataTypeOut[0])) {
                            call.registerOutParameter(i, OracleTypes.DATE);
                            outMap.put(i, "date");
                        }
                    }
                }
            }
            return outMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * 执行sql语句时替换sql中的参数
     * @param preparedStatement
     * @param count
     * @param paramSql
     */
    private void reorganizeSql(PreparedStatement preparedStatement, int count, String[] paramSql) {
        try {
            for (int i = 1; i <= count; i++) {
                String[] paramType = paramSql[i].split("/");
                if ("String".equals(paramType[0])) {
                    if (paramType[1] != null && paramType[1].contains("now")) {
                        Date date = new Date();
                        preparedStatement.setString(i, new SimpleDateFormat("yyyy-MM-dd").format(date));
                    } else {
                        preparedStatement.setString(i, paramType[1]);
                    }
                }
                if ("int".equals(paramType[0])) {
                    preparedStatement.setInt(i, Integer.parseInt(paramType[1]));
                }
                if ("date".equals(paramType[0])) {
                    if (paramType[1].endsWith("now")) {
                        preparedStatement.setTimestamp(i, new Timestamp(System.currentTimeMillis()));
                    } else {
                        //现在的前几天或后几天
                        String changeTime = paramType[1].substring(3);
                        preparedStatement.setTimestamp(i, new Timestamp(System.currentTimeMillis() + (Integer.parseInt(changeTime) * (60 * 60 * 24 * 1000))));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭数据库相关资源
     * @param conn 数据库连接对象
     * @param stat 声明
     * @param rs 结果集
     */
    private void release(Connection conn, Statement stat, ResultSet rs) {
        //结果集
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //声明
        if(stat != null){
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //数据库连接
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
