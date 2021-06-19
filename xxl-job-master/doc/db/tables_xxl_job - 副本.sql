drop sequence XXL_JOB_GROUP_ID_SEQ;
drop sequence XXL_JOB_INFO_ID_SEQ;
drop sequence XXL_JOB_LOG_GLUE_ID_SEQ;
drop sequence XXL_JOB_LOG_ID_SEQ;
drop sequence XXL_JOB_REGISTRY_ID_SEQ;


--Create sequence
create sequence ETL.XXL_JOB_GROUP_ID_SEQ
minvalue 1
maxvalue 999999999999
start with 1
increment by 1
cache 20
cycle;

--Create sequence
create sequence ETL.XXL_JOB_INFO_ID_SEQ
minvalue 1
maxvalue 999999999999
start with 1
increment by 1
cache 20
cycle;

--Create sequence
create sequence ETL.XXL_JOB_LOG_GLUE_ID_SEQ
minvalue 1
maxvalue 999999999999
start with 1
increment by 1
cache 20
cycle;

--Create sequence
create sequence ETL.XXL_JOB_LOG_ID_SEQ
minvalue 1
maxvalue 999999999999
start with 1
increment by 1
cache 20
cycle;

--Create sequence
create sequence ETL.XXL_JOB_REGISTRY_ID_SEQ
minvalue 1
maxvalue 999999999999
start with 1
increment by 1
cache 20
cycle;


--有外键的表先删除
DROP TABLE XXL_JOB_QRTZ_BLOB_TRIGGERS        ;
DROP TABLE XXL_JOB_QRTZ_SIMPROP_TRIGGERS     ;
DROP TABLE XXL_JOB_QRTZ_CRON_TRIGGERS        ;
DROP TABLE XXL_JOB_QRTZ_SIMPLE_TRIGGERS      ;
DROP TABLE XXL_JOB_QRTZ_TRIGGERS             ;
DROP TABLE XXL_JOB_QRTZ_JOB_DETAILS          ;

DROP TABLE XXL_JOB_QRTZ_TRIGGER_GROUP        ;
DROP TABLE XXL_JOB_QRTZ_TRIGGER_INFO         ;
DROP TABLE XXL_JOB_QRTZ_TRIGGER_LOG          ;
DROP TABLE XXL_JOB_QRTZ_TRIGGER_LOGGLUE      ;
DROP TABLE XXL_JOB_QRTZ_TRIGGER_REGISTRY     ;
DROP TABLE XXL_JOB_QRTZ_CALENDARS            ;
DROP TABLE XXL_JOB_QRTZ_FIRED_TRIGGERS       ;
DROP TABLE XXL_JOB_QRTZ_LOCKS                ;
DROP TABLE XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS  ;
DROP TABLE XXL_JOB_QRTZ_SCHEDULER_STATE      ;

CREATE TABLE XXL_JOB_QRTZ_JOB_DETAILS
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    JOB_NAME  VARCHAR2(400) NOT NULL,
    JOB_GROUP VARCHAR2(400) NOT NULL,
    DESCRIPTION VARCHAR2(500) NULL,
    JOB_CLASS_NAME   VARCHAR2(500) NOT NULL,
    IS_DURABLE VARCHAR2(2) NOT NULL,
    IS_NONCONCURRENT VARCHAR2(2) NOT NULL,
    IS_UPDATE_DATA VARCHAR2(2) NOT NULL,
    REQUESTS_RECOVERY VARCHAR2(2) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE XXL_JOB_QRTZ_TRIGGERS
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    TRIGGER_NAME VARCHAR2(400) NOT NULL,
    TRIGGER_GROUP VARCHAR2(400) NOT NULL,
    JOB_NAME  VARCHAR2(400) NOT NULL,
    JOB_GROUP VARCHAR2(400) NOT NULL,
    DESCRIPTION VARCHAR2(500) NULL,
    NEXT_FIRE_TIME NUMBER(20,0) NULL,
    PREV_FIRE_TIME NUMBER(20,0) NULL,
    PRIORITY NUMBER(10,0) NULL,
    TRIGGER_STATE VARCHAR2(32) NOT NULL,
    TRIGGER_TYPE VARCHAR2(16) NOT NULL,
    START_TIME NUMBER(20,0) NOT NULL,
    END_TIME NUMBER(20,0) NULL,
    CALENDAR_NAME VARCHAR2(400) NULL,
    MISFIRE_INSTR NUMBER(5,0) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
        REFERENCES XXL_JOB_QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE XXL_JOB_QRTZ_SIMPLE_TRIGGERS
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    TRIGGER_NAME VARCHAR2(400) NOT NULL,
    TRIGGER_GROUP VARCHAR2(400) NOT NULL,
    REPEAT_COUNT NUMBER(20,0) NOT NULL,
    REPEAT_INTERVAL NUMBER(20,0) NOT NULL,
    TIMES_TRIGGERED NUMBER(20,0) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES XXL_JOB_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE XXL_JOB_QRTZ_CRON_TRIGGERS
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    TRIGGER_NAME VARCHAR2(400) NOT NULL,
    TRIGGER_GROUP VARCHAR2(400) NOT NULL,
    CRON_EXPRESSION VARCHAR2(400) NOT NULL,
    TIME_ZONE_ID VARCHAR2(160),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES XXL_JOB_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE XXL_JOB_QRTZ_SIMPROP_TRIGGERS
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    TRIGGER_NAME VARCHAR2(400) NOT NULL,
    TRIGGER_GROUP VARCHAR2(400) NOT NULL,
    STR_PROP_1 VARCHAR2(1024) NULL,
    STR_PROP_2 VARCHAR2(1024) NULL,
    STR_PROP_3 VARCHAR2(1024) NULL,
    INT_PROP_1 NUMBER(10,0) NULL,
    INT_PROP_2 NUMBER(10,0) NULL,
    LONG_PROP_1 NUMBER(20,0) NULL,
    LONG_PROP_2 NUMBER(20,0) NULL,
    DEC_PROP_1 NUMBER(13,4) NULL,
    DEC_PROP_2 NUMBER(13,4) NULL,
    BOOL_PROP_1 VARCHAR2(2) NULL,
    BOOL_PROP_2 VARCHAR2(2) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES XXL_JOB_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE XXL_JOB_QRTZ_BLOB_TRIGGERS
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    TRIGGER_NAME VARCHAR2(400) NOT NULL,
    TRIGGER_GROUP VARCHAR2(400) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES XXL_JOB_QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE XXL_JOB_QRTZ_CALENDARS
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    CALENDAR_NAME  VARCHAR2(400) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

CREATE TABLE XXL_JOB_QRTZ_PAUSED_TRIGGER_GRPS
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    TRIGGER_GROUP  VARCHAR2(400) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE XXL_JOB_QRTZ_FIRED_TRIGGERS
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    ENTRY_ID VARCHAR2(200) NOT NULL,
    TRIGGER_NAME VARCHAR2(400) NOT NULL,
    TRIGGER_GROUP VARCHAR2(400) NOT NULL,
    INSTANCE_NAME VARCHAR2(400) NOT NULL,
    FIRED_TIME NUMBER(20,0) NOT NULL,
    SCHED_TIME NUMBER(20,0) NOT NULL,
    PRIORITY NUMBER(10,0) NOT NULL,
    STATE VARCHAR2(32) NOT NULL,
    JOB_NAME VARCHAR2(400) NULL,
    JOB_GROUP VARCHAR2(400) NULL,
    IS_NONCONCURRENT VARCHAR2(2) NULL,
    REQUESTS_RECOVERY VARCHAR2(2) NULL,
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE XXL_JOB_QRTZ_SCHEDULER_STATE
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    INSTANCE_NAME VARCHAR2(400) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(20,0) NOT NULL,
    CHECKIN_INTERVAL NUMBER(20,0) NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE XXL_JOB_QRTZ_LOCKS
  (
    SCHED_NAME VARCHAR2(240) NOT NULL,
    LOCK_NAME  VARCHAR2(80) NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);



CREATE TABLE XXL_JOB_QRTZ_TRIGGER_INFO (
  id NUMBER(10,0) NOT NULL,
  job_group NUMBER(10,0) NOT NULL,
  job_cron varchar2(256) NOT NULL,
  job_desc varchar2(512) NOT NULL,
  add_time date DEFAULT NULL,
  update_time date DEFAULT NULL,
  author varchar2(128) DEFAULT NULL,
  alarm_email varchar2(1024) DEFAULT NULL,
  executor_route_strategy varchar2(100) DEFAULT NULL,
  executor_handler varchar2(512) DEFAULT NULL ,
  executor_param varchar2(1024) DEFAULT NULL ,
  executor_block_strategy varchar2(100) DEFAULT NULL,
  executor_timeout NUMBER(10,0) DEFAULT 0 NOT NULL  ,
  executor_fail_retry_count NUMBER(10,0)  DEFAULT 0 NOT NULL,
  glue_type varchar2(100) NOT NULL,
  glue_source CLOB ,
  glue_remark varchar2(256) DEFAULT NULL,
  glue_updatetime date DEFAULT NULL ,
  child_jobid varchar2(512) DEFAULT NULL,
  PRIMARY KEY (id)
) ;
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.job_group is '执行器主键ID';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.job_cron is '任务执行CRON';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.author is '作者';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.alarm_email is '报警邮件';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.executor_route_strategy is '执行器路由策略';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.executor_handler is '执行器任务handler';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.executor_param is '执行器任务参数';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.executor_block_strategy is '阻塞处理策略';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.executor_timeout is '任务执行超时时间，单位秒';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.executor_fail_retry_count is '失败重试次数';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.glue_type is 'GLUE类型';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.glue_source is 'GLUE源代码';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.glue_remark is 'GLUE备注';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.glue_updatetime is 'GLUE更新时间';
comment on column XXL_JOB_QRTZ_TRIGGER_INFO.child_jobid is '子任务ID，多个逗号分隔';

CREATE TABLE XXL_JOB_QRTZ_TRIGGER_LOG (
  id NUMBER(10,0) NOT NULL ,
  job_group NUMBER(10,0) NOT NULL ,
  job_id NUMBER(10,0) NOT NULL ,
  executor_address varchar2(512) DEFAULT NULL ,
  executor_handler varchar2(512) DEFAULT NULL ,
  executor_param varchar2(1024) DEFAULT NULL,
  executor_sharding_param varchar2(40) DEFAULT NULL ,
  executor_fail_retry_count NUMBER(10,0) DEFAULT 0 NOT NULL ,
  trigger_time date DEFAULT NULL ,
  trigger_code NUMBER(10,0) NOT NULL ,
  trigger_msg CLOB ,
  handle_time date DEFAULT NULL ,
  handle_code NUMBER(10,0) NOT NULL ,
  handle_msg CLOB ,
  alarm_status NUMBER(3,0) DEFAULT 0 NOT NULL,
  PRIMARY KEY (id)
) ;
create index I_trigger_time on XXL_JOB_QRTZ_TRIGGER_LOG (trigger_time);
create index I_handle_code on XXL_JOB_QRTZ_TRIGGER_LOG (handle_code);
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.job_group is '执行器主键ID';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.job_id is '任务，主键ID';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.executor_address is '执行器地址，本次执行的地址';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.executor_handler is '执行器任务handler';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.executor_param is '执行器任务参数';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.executor_sharding_param is '执行器任务分片参数，格式如 1/2';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.executor_fail_retry_count is '失败重试次数';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.trigger_time is '调度-时间';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.trigger_code is '调度-结果';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.trigger_msg is '调度-日志';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.handle_time is '执行-时间';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.handle_code is '执行-状态';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.handle_msg is '执行-日志';
comment on column XXL_JOB_QRTZ_TRIGGER_LOG.alarm_status is '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败';



CREATE TABLE XXL_JOB_QRTZ_TRIGGER_LOGGLUE (
  id NUMBER(10,0) NOT NULL,
  job_id NUMBER(10,0) NOT NULL,
  glue_type varchar2(100) DEFAULT NULL,
  glue_source CLOB ,
  glue_remark varchar2(256) NOT NULL,
  add_time timestamp DEFAULT NULL NULL ,
  update_time timestamp DEFAULT CURRENT_TIMESTAMP  NULL,
  PRIMARY KEY (id)
) ;
comment on column XXL_JOB_QRTZ_TRIGGER_LOGGLUE.job_id is '任务，主键ID';
comment on column XXL_JOB_QRTZ_TRIGGER_LOGGLUE.glue_type is 'GLUE类型';
comment on column XXL_JOB_QRTZ_TRIGGER_LOGGLUE.glue_source is 'GLUE源代码';
comment on column XXL_JOB_QRTZ_TRIGGER_LOGGLUE.glue_remark is 'GLUE备注';

CREATE TABLE XXL_JOB_QRTZ_TRIGGER_REGISTRY (
  id NUMBER(10,0) NOT NULL,
  registry_group varchar2(512) NOT NULL,
  registry_key varchar2(512) NOT NULL,
  registry_value varchar2(512) NOT NULL,
  update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL ,
  PRIMARY KEY (id)
) ;

CREATE TABLE XXL_JOB_QRTZ_TRIGGER_GROUP (
  id NUMBER(10,0) NOT NULL,
  app_name varchar2(128) NOT NULL ,
  title varchar2(24) NOT NULL ,
  "ORDER" NUMBER(3,0)  DEFAULT 0 NOT NULL,
  address_type NUMBER(3,0) DEFAULT 0 NOT NULL ,
  address_list varchar2(1024) DEFAULT NULL ,
  PRIMARY KEY (id)
) ;
comment on column XXL_JOB_QRTZ_TRIGGER_GROUP.app_name is '执行器AppName';
comment on column XXL_JOB_QRTZ_TRIGGER_GROUP.title is '执行器名称';
comment on column XXL_JOB_QRTZ_TRIGGER_GROUP."ORDER" is '排序';
comment on column XXL_JOB_QRTZ_TRIGGER_GROUP.address_type is '执行器地址类型：0=自动注册、1=手动录入';
comment on column XXL_JOB_QRTZ_TRIGGER_GROUP.address_list is '执行器地址列表，多地址逗号分隔';



INSERT INTO XXL_JOB_QRTZ_TRIGGER_GROUP(id, app_name, title, "ORDER", address_type, address_list) VALUES (XXL_JOB_GROUP_ID_SEQ.nextval, 'xxl-job-executor-sample', '示例执行器', 1, 0, NULL);
INSERT INTO XXL_JOB_QRTZ_TRIGGER_INFO(id, job_group, job_cron, job_desc, add_time, update_time, author, alarm_email, executor_route_strategy, executor_handler, executor_param, executor_block_strategy, executor_timeout, executor_fail_retry_count, glue_type, glue_source, glue_remark, glue_updatetime, child_jobid)
VALUES (XXL_JOB_INFO_ID_SEQ.nextval, 1, '0 0 0 * * ? *', '测试任务1', sysdate, sysdate, 'XXL', '', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', 's', 'GLUE代码初始化', sysdate, '');

commit;
