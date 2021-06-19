#
# XXL-JOB v2.3.1-SNAPSHOT
# Copyright (c) 2015-present, xuxueli.

CREATE database if NOT EXISTS xxl_job default character set utf8mb4 collate utf8mb4_unicode_ci;
use xxl_job;

SET NAMES utf8mb4;

CREATE TABLE xxl_job_info (
  id number(11) NOT NULL,
  job_group number(11) NOT NULL,
  job_desc varchar2(255) NOT NULL,
  add_time date DEFAULT NULL,
  update_time date DEFAULT NULL,
  author varchar2(64) DEFAULT NULL,
  alarm_email varchar2(255) DEFAULT NULL,
  schedule_type varchar2(50) NOT NULL,
  schedule_conf varchar2(128) DEFAULT NULL,
  misfire_strategy varchar2(50) NOT NULL ,
  executor_route_strategy varchar2(50) DEFAULT NULL,
  executor_handler varchar2(255) DEFAULT NULL,
  executor_param varchar2(512) DEFAULT NULL,
  executor_block_strategy varchar2(50) DEFAULT NULL,
  executor_timeout number(11) NOT NULL,
  executor_fail_retry_count number(11) NOT NULL,
  glue_type varchar2(50) NOT NULL,
  glue_source clob,
  glue_remark varchar2(128) DEFAULT NULL,
  glue_updatetime date DEFAULT NULL,
  child_jobid varchar2(255) DEFAULT NULL,
  trigger_status varchar2(4) NOT NULL,
  trigger_last_time number(13) NOT NULL,
  trigger_next_time number(13) NOT NULL,
  PRIMARY KEY (id)
)

comment on column xxl_job_info.job_group is '执行器主键ID';
comment on column xxl_job_info.author is '作者';
comment on column xxl_job_info.alarm_email is '报警邮件';
comment on column xxl_job_info.schedule_type is '调度类型';
comment on column xxl_job_info.schedule_conf is '调度配置，值含义取决于调度类型';
comment on column xxl_job_info.misfire_strategy is '调度过期策略';
comment on column xxl_job_info.executor_route_strategy is '执行器路由策略';
comment on column xxl_job_info.executor_handler is '执行器任务handler';
comment on column xxl_job_info.executor_param is '执行器任务参数';
comment on column xxl_job_info.executor_block_strategy is '阻塞处理策略';
comment on column xxl_job_info.executor_timeout is '任务执行超时时间，单位秒';
comment on column xxl_job_info.executor_fail_retry_count is '失败重试次数';
comment on column xxl_job_info.glue_type is 'GLUE类型';
comment on column xxl_job_info.glue_source is 'GLUE源代码';
comment on column xxl_job_info.glue_remark is 'GLUE备注';
comment on column xxl_job_info.glue_updatetime is 'GLUE更新时间';
comment on column xxl_job_info.child_jobid is '子任务ID，多个逗号分隔';
comment on column xxl_job_info.trigger_status is '调度状态：0-停止，1-运行';
comment on column xxl_job_info.trigger_last_time is '上次调度时间';
comment on column xxl_job_info.trigger_next_time is '下次调度时间';
CREATE TABLE xxl_job_log (
  id number(20) NOT NULL,
  job_group number(11) NOT NULL,
  job_id number(11) NOT NULL,
  executor_address varchar2(255) DEFAULT NULL,
  executor_handler varchar2(255) DEFAULT NULL,
  executor_param varchar2(512) DEFAULT NULL,
  executor_sharding_param varchar2(20) DEFAULT NULL,
  executor_fail_retry_count number(11) NOT NULL,
  trigger_time date DEFAULT NULL,
  trigger_code number(11) NOT NULL,
  trigger_msg clob,
  handle_time date DEFAULT NULL,
  handle_code number(11) NOT NULL,
  handle_msg clob,
  alarm_status number(4) NOT NULL,
  PRIMARY KEY (id)
) 

comment on column xxl_job_log.job_group is '执行器主键ID';
comment on column xxl_job_log.job_id is '任务，主键ID';
comment on column xxl_job_log.executor_address is '执行器地址，本次执行的地址';
comment on column xxl_job_log.executor_handler is '执行器任务handler';
comment on column xxl_job_log.executor_param is '执行器任务参数';
comment on column xxl_job_log.executor_sharding_param is '执行器任务分片参数，格式如 1/2';
comment on column xxl_job_log.executor_fail_retry_count is '失败重试次数';
comment on column xxl_job_log.trigger_time is '调度-时间';
comment on column xxl_job_log.trigger_code is '调度-结果';
comment on column xxl_job_log.trigger_msg is '调度-日志';
comment on column xxl_job_log.handle_time is '执行-时间';
comment on column xxl_job_log.handle_code is '执行-状态';
comment on column xxl_job_log.handle_msg is '执行-日志';
comment on column xxl_job_log.alarm_status is '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败';

CREATE TABLE xxl_job_log_report (
  id number(11) NOT NULL,
  trigger_day date DEFAULT NULL COMMENT 调度-时间,
  running_count number(11) NOT NULL,
  suc_count number(11) NOT NULL,
  fail_count number(11) NOT NULL,
  update_time date DEFAULT NULL,
  PRIMARY KEY (id)
);

comment on column xxl_job_log_report.trigger_day is '调度-时间';
comment on column xxl_job_log_report.running_count is '运行中-日志数量';
comment on column xxl_job_log_report.suc_count is '执行成功-日志数量';
comment on column xxl_job_log_report.fail_count is '执行失败-日志数量';
	


CREATE TABLE xxl_job_registry (
  id number(11) NOT NULL,
  registry_group varchar2(50) NOT NULL,
  registry_key varchar2(255) NOT NULL,
  registry_value varchar2(255) NOT NULL,
  update_time date DEFAULT NULL,
  PRIMARY KEY (id),
) 



CREATE TABLE xxl_job_group (
  id number(11) NOT NULL,
  app_name varchar2(64) NOT NULL,
  title varchar2(12) NOT NULL,
  address_type number(4) NOT NULL,
  address_list varchar2(2000),
  update_time date DEFAULT NULL,
  PRIMARY KEY (id)
);


comment on column xxl_job_group.app_name is '执行器AppName';
comment on column xxl_job_group.title is '执行器名称';
comment on column xxl_job_group.address_type is '执行器地址类型：0=自动注册、1=手动录入';
comment on column xxl_job_group.address_list is '执行器地址列表，多地址逗号分隔';

CREATE TABLE xxl_job_user (
  id number(11) NOT NULL,
  username varchar2(50) NOT NULL,
  password varchar2(50) NOT NULL,
  role number(4) NOT NULL,
  permission varchar2(255) DEFAULT NULL,
  PRIMARY KEY (id)
);
comment on column xxl_job_user.username is '账号';
comment on column xxl_job_user.password is '密码';
comment on column xxl_job_user.role is '角色：0-普通用户、1-管理员';
comment on column xxl_job_user.permission is '权限：执行器ID列表，多个逗号分割',


CREATE TABLE xxl_job_lock (
  lock_name varchar2(50) NOT NULL,
  PRIMARY KEY (lock_name)
);
comment on column xxl_job_lock.lock_name is '锁名称';

INSERT INTO xxl_job_group(id, app_name, title, address_type, address_list, update_time) VALUES (1, xxl-job-executor-sample, 示例执行器, 0, NULL, 2018-11-03 22:21:31 );
INSERT INTO xxl_job_info(id, job_group, job_desc, add_time, update_time, author, alarm_email, schedule_type, schedule_conf, misfire_strategy, executor_route_strategy, executor_handler, executor_param, executor_block_strategy, executor_timeout, executor_fail_retry_count, glue_type, glue_remark, glue_updatetime) VALUES (1, 1, 测试任务1, 2018-11-03 22:21:31, 2018-11-03 22:21:31, XXL, CRON, 0 0 0 * * ? *, DO_NOTHING, FIRST, demoJobHandler,SERIAL_EXECUTION, 0, 0, BEAN, , GLUE代码初始化, 2018-11-03 22:21:31 );
INSERT INTO xxl_job_user(id, username, password, role, permission) VALUES (1, admin, e10adc3949ba59abbe56e057f20f883e, 1, NULL);
INSERT INTO xxl_job_lock ( lock_name) VALUES ( schedule_lock);

commit;

