package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.cron.CronExpression;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLogReport;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.scheduler.MisfireStrategyEnum;
import com.xxl.job.admin.core.scheduler.ScheduleTypeEnum;
import com.xxl.job.admin.core.thread.JobScheduleHelper;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.*;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

/**
 * core job action for xxl-job
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class XxlJobServiceImpl implements XxlJobService {
	private static Logger logger = LoggerFactory.getLogger(XxlJobServiceImpl.class);

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobLogDao xxlJobLogDao;
	@Resource
	private XxlJobLogGlueDao xxlJobLogGlueDao;
	@Resource
	private XxlJobLogReportDao xxlJobLogReportDao;
	
	@Override
	public Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {

		// page list

		List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
		int list_count = xxlJobInfoDao.pageListCount(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);

		//对查询出的结果集中调度配置进行转码
		scheduleConfTochinese(list);

		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
	    maps.put("recordsTotal", list_count);		// 总记录数
	    maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
	    maps.put("data", list);  					// 分页列表
		return maps;
	}

	@Override
	public ReturnT<String> add(XxlJobInfo jobInfo) {

		// valid base
		XxlJobGroup group = xxlJobGroupDao.load(jobInfo.getJobGroup());
		if (group == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_choose")+I18nUtil.getString("jobinfo_field_jobgroup")) );
		}
		if (jobInfo.getJobDesc()==null || jobInfo.getJobDesc().trim().length()==0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_jobdesc")) );
		}
		if (jobInfo.getAuthor()==null || jobInfo.getAuthor().trim().length()==0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_author")) );
		}

		// valid trigger
		ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
		if (scheduleTypeEnum == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
		}
		if (scheduleTypeEnum == ScheduleTypeEnum.CRON) {
			if (jobInfo.getScheduleConf()==null || !CronExpression.isValidExpression(jobInfo.getScheduleConf())) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, "Cron"+I18nUtil.getString("system_unvalid"));
			}
		} else if (scheduleTypeEnum == ScheduleTypeEnum.FIX_RATE/* || scheduleTypeEnum == ScheduleTypeEnum.FIX_DELAY*/) {
			if (jobInfo.getScheduleConf() == null) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")) );
			}
			try {
				int fixSecond = Integer.valueOf(jobInfo.getScheduleConf());
				if (fixSecond < 1) {
					return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
				}
			} catch (Exception e) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
			}
		}

		// valid job
		if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_gluetype")+I18nUtil.getString("system_unvalid")) );
		}
		if (GlueTypeEnum.BEAN==GlueTypeEnum.match(jobInfo.getGlueType()) && (jobInfo.getExecutorHandler()==null || jobInfo.getExecutorHandler().trim().length()==0) ) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+"JobHandler") );
		}
		// 》fix "\r" in shell
		if (GlueTypeEnum.GLUE_SHELL==GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource()!=null) {
			jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
		}

		// valid advanced
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorRouteStrategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("misfire_strategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorBlockStrategy")+I18nUtil.getString("system_unvalid")) );
		}

		// 》ChildJobId valid
		if (jobInfo.getChildJobId()!=null && jobInfo.getChildJobId().trim().length()>0) {
			String[] childJobIds = jobInfo.getChildJobId().split(",");
			for (String childJobIdItem: childJobIds) {
				if (childJobIdItem!=null && childJobIdItem.trim().length()>0 && isNumeric(childJobIdItem)) {
					XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.parseInt(childJobIdItem));
					if (childJobInfo==null) {
						return new ReturnT<String>(ReturnT.FAIL_CODE,
								MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_not_found")), childJobIdItem));
					}
				} else {
					return new ReturnT<String>(ReturnT.FAIL_CODE,
							MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_unvalid")), childJobIdItem));
				}
			}

			// join , avoid "xxx,,"
			String temp = "";
			for (String item:childJobIds) {
				temp += item + ",";
			}
			temp = temp.substring(0, temp.length()-1);

			jobInfo.setChildJobId(temp);
		}

		// add in db
		jobInfo.setAddTime(new Date());
		jobInfo.setUpdateTime(new Date());
		jobInfo.setGlueUpdatetime(new Date());
		xxlJobInfoDao.save(jobInfo);
		if (jobInfo.getId() < 1) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_add")+I18nUtil.getString("system_fail")) );
		}

		return new ReturnT<String>(String.valueOf(jobInfo.getId()));
	}

	private boolean isNumeric(String str){
		try {
			int result = Integer.valueOf(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public ReturnT<String> update(XxlJobInfo jobInfo) {

		// valid base
		if (jobInfo.getJobDesc()==null || jobInfo.getJobDesc().trim().length()==0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_jobdesc")) );
		}
		if (jobInfo.getAuthor()==null || jobInfo.getAuthor().trim().length()==0) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_author")) );
		}

		// valid trigger
		ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
		if (scheduleTypeEnum == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
		}
		if (scheduleTypeEnum == ScheduleTypeEnum.CRON) {
			if (jobInfo.getScheduleConf()==null || !CronExpression.isValidExpression(jobInfo.getScheduleConf())) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, "Cron"+I18nUtil.getString("system_unvalid") );
			}
		} else if (scheduleTypeEnum == ScheduleTypeEnum.FIX_RATE /*|| scheduleTypeEnum == ScheduleTypeEnum.FIX_DELAY*/) {
			if (jobInfo.getScheduleConf() == null) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
			}
			try {
				int fixSecond = Integer.valueOf(jobInfo.getScheduleConf());
				if (fixSecond < 1) {
					return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
				}
			} catch (Exception e) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
			}
		}

		// valid advanced
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorRouteStrategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("misfire_strategy")+I18nUtil.getString("system_unvalid")) );
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_executorBlockStrategy")+I18nUtil.getString("system_unvalid")) );
		}

		// 》ChildJobId valid
		if (jobInfo.getChildJobId()!=null && jobInfo.getChildJobId().trim().length()>0) {
			String[] childJobIds = jobInfo.getChildJobId().split(",");
			for (String childJobIdItem: childJobIds) {
				if (childJobIdItem!=null && childJobIdItem.trim().length()>0 && isNumeric(childJobIdItem)) {
					XxlJobInfo childJobInfo = xxlJobInfoDao.loadById(Integer.parseInt(childJobIdItem));
					if (childJobInfo==null) {
						return new ReturnT<String>(ReturnT.FAIL_CODE,
								MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_not_found")), childJobIdItem));
					}
				} else {
					return new ReturnT<String>(ReturnT.FAIL_CODE,
							MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_unvalid")), childJobIdItem));
				}
			}

			// join , avoid "xxx,,"
			String temp = "";
			for (String item:childJobIds) {
				temp += item + ",";
			}
			temp = temp.substring(0, temp.length()-1);

			jobInfo.setChildJobId(temp);
		}

		// group valid
		XxlJobGroup jobGroup = xxlJobGroupDao.load(jobInfo.getJobGroup());
		if (jobGroup == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_jobgroup")+I18nUtil.getString("system_unvalid")) );
		}

		// stage job info
		XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(jobInfo.getId());
		if (exists_jobInfo == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_field_id")+I18nUtil.getString("system_not_found")) );
		}

		// next trigger time (5s后生效，避开预读周期)
		long nextTriggerTime = exists_jobInfo.getTriggerNextTime();
		boolean scheduleDataNotChanged = jobInfo.getScheduleType().equals(exists_jobInfo.getScheduleType()) && jobInfo.getScheduleConf().equals(exists_jobInfo.getScheduleConf());
		if (exists_jobInfo.getTriggerStatus() == 1 && !scheduleDataNotChanged) {
			try {
				Date nextValidTime = JobScheduleHelper.generateNextValidTime(jobInfo, new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
				if (nextValidTime == null) {
					return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
				}
				nextTriggerTime = nextValidTime.getTime();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
			}
		}

		exists_jobInfo.setJobGroup(jobInfo.getJobGroup());
		exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
		exists_jobInfo.setAuthor(jobInfo.getAuthor());
		exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
		exists_jobInfo.setScheduleType(jobInfo.getScheduleType());
		exists_jobInfo.setScheduleConf(jobInfo.getScheduleConf());
		exists_jobInfo.setMisfireStrategy(jobInfo.getMisfireStrategy());
		exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
		exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler());
		exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
		exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
		exists_jobInfo.setExecutorTimeout(jobInfo.getExecutorTimeout());
		exists_jobInfo.setExecutorFailRetryCount(jobInfo.getExecutorFailRetryCount());
		exists_jobInfo.setChildJobId(jobInfo.getChildJobId());
		exists_jobInfo.setTriggerNextTime(nextTriggerTime);

		exists_jobInfo.setUpdateTime(new Date());
        xxlJobInfoDao.update(exists_jobInfo);


		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> remove(int id) {
		XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);
		if (xxlJobInfo == null) {
			return ReturnT.SUCCESS;
		}

		xxlJobInfoDao.delete(id);
		xxlJobLogDao.delete(id);
		xxlJobLogGlueDao.deleteByJobId(id);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> start(int id) {
		XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);

		// valid
		ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(xxlJobInfo.getScheduleType(), ScheduleTypeEnum.NONE);
		if (ScheduleTypeEnum.NONE == scheduleTypeEnum) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type_none_limit_start")) );
		}

		// next trigger time (5s后生效，避开预读周期)
		long nextTriggerTime = 0;
		try {
			Date nextValidTime = JobScheduleHelper.generateNextValidTime(xxlJobInfo, new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
			if (nextValidTime == null) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
			}
			nextTriggerTime = nextValidTime.getTime();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) );
		}

		xxlJobInfo.setTriggerStatus(1);
//		xxlJobInfo.setTriggerLastTime(0);
		xxlJobInfo.setTriggerNextTime(nextTriggerTime);

		xxlJobInfo.setUpdateTime(new Date());
		xxlJobInfoDao.update(xxlJobInfo);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> stop(int id) {
        XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(id);

		xxlJobInfo.setTriggerStatus(0);
//		xxlJobInfo.setTriggerLastTime(0);
		xxlJobInfo.setTriggerNextTime(0);

		xxlJobInfo.setUpdateTime(new Date());
		xxlJobInfoDao.update(xxlJobInfo);
		return ReturnT.SUCCESS;
	}

	@Override
	public Map<String, Object> dashboardInfo() {

		int jobInfoCount = xxlJobInfoDao.findAllCount();
		int jobLogCount = 0;
		int jobLogSuccessCount = 0;
		XxlJobLogReport xxlJobLogReport = xxlJobLogReportDao.queryLogReportTotal();
		if (xxlJobLogReport != null) {
			jobLogCount = xxlJobLogReport.getRunningCount() + xxlJobLogReport.getSucCount() + xxlJobLogReport.getFailCount();
			jobLogSuccessCount = xxlJobLogReport.getSucCount();
		}

		// executor count
		Set<String> executorAddressSet = new HashSet<String>();
		List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();

		if (groupList!=null && !groupList.isEmpty()) {
			for (XxlJobGroup group: groupList) {
				if (group.getRegistryList()!=null && !group.getRegistryList().isEmpty()) {
					executorAddressSet.addAll(group.getRegistryList());
				}
			}
		}

		int executorCount = executorAddressSet.size();

		Map<String, Object> dashboardMap = new HashMap<String, Object>();
		dashboardMap.put("jobInfoCount", jobInfoCount);
		dashboardMap.put("jobLogCount", jobLogCount);
		dashboardMap.put("jobLogSuccessCount", jobLogSuccessCount);
		dashboardMap.put("executorCount", executorCount);
		return dashboardMap;
	}

	@Override
	public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {

		// process
		List<String> triggerDayList = new ArrayList<String>();
		List<Integer> triggerDayCountRunningList = new ArrayList<Integer>();
		List<Integer> triggerDayCountSucList = new ArrayList<Integer>();
		List<Integer> triggerDayCountFailList = new ArrayList<Integer>();
		int triggerCountRunningTotal = 0;
		int triggerCountSucTotal = 0;
		int triggerCountFailTotal = 0;

		List<XxlJobLogReport> logReportList = xxlJobLogReportDao.queryLogReport(startDate, endDate);

		if (logReportList!=null && logReportList.size()>0) {
			for (XxlJobLogReport item: logReportList) {
				String day = DateUtil.formatDate(item.getTriggerDay());
				int triggerDayCountRunning = item.getRunningCount();
				int triggerDayCountSuc = item.getSucCount();
				int triggerDayCountFail = item.getFailCount();

				triggerDayList.add(day);
				triggerDayCountRunningList.add(triggerDayCountRunning);
				triggerDayCountSucList.add(triggerDayCountSuc);
				triggerDayCountFailList.add(triggerDayCountFail);

				triggerCountRunningTotal += triggerDayCountRunning;
				triggerCountSucTotal += triggerDayCountSuc;
				triggerCountFailTotal += triggerDayCountFail;
			}
		} else {
			for (int i = -6; i <= 0; i++) {
				triggerDayList.add(DateUtil.formatDate(DateUtil.addDays(new Date(), i)));
				triggerDayCountRunningList.add(0);
				triggerDayCountSucList.add(0);
				triggerDayCountFailList.add(0);
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("triggerDayList", triggerDayList);
		result.put("triggerDayCountRunningList", triggerDayCountRunningList);
		result.put("triggerDayCountSucList", triggerDayCountSucList);
		result.put("triggerDayCountFailList", triggerDayCountFailList);

		result.put("triggerCountRunningTotal", triggerCountRunningTotal);
		result.put("triggerCountSucTotal", triggerCountSucTotal);
		result.put("triggerCountFailTotal", triggerCountFailTotal);

		return new ReturnT<Map<String, Object>>(result);
	}

	/**
	 * 对查询出的结果集中调度配置进行转码
	 * @param list 查询出的XxlJobInfo对象集合
	 */
	private void scheduleConfTochinese(List<XxlJobInfo> list) {
		for (XxlJobInfo xxlJobInfo : list) {
			if (xxlJobInfo.getScheduleType() != null && !"".equals(xxlJobInfo.getScheduleType())) {
				if ("NONE".equals(xxlJobInfo.getScheduleType())) {
					xxlJobInfo.setScheduleTypeChinese("无");
				} else if ("FIX_RATE".equals(xxlJobInfo.getScheduleType())) {
					xxlJobInfo.setScheduleTypeChinese("固定速度");
					xxlJobInfo.setScheduleConfChinese("每" + xxlJobInfo.getScheduleConf() + "秒触发");
				} else if ("CRON".equals(xxlJobInfo.getScheduleType())) {
					xxlJobInfo.setScheduleTypeChinese("CRON");
					StringBuffer scheduleConf = new StringBuffer();
					String[] scheduleConfs = xxlJobInfo.getScheduleConf().split("\\s+");
					//年
					if (scheduleConfs.length == 7){
						if ("*".equals(scheduleConfs[6])) {
							scheduleConf.append("每年");
						} else {
							String[] scheduleConfYear = scheduleConfs[6].split("-");
							scheduleConf.append(scheduleConfYear[0]).append("年-").append(scheduleConfYear[1]).append("年");
						}
					}

					if (scheduleConfs.length == 6) {
						scheduleConf.append("每年");
					}

					//月
					if (!"*".equals(scheduleConfs[4])) {
						if (scheduleConfs[4].contains("-")) {
							String[] scheduleConfMonthTo = scheduleConfs[4].split("-");
							scheduleConf.append(scheduleConfMonthTo[0]).append("月-").append(scheduleConfMonthTo[1]).append("月");
						}
						if (scheduleConfs[4].contains("/")) {
							String[] scheduleConfMonth = scheduleConfs[4].split("/");
							scheduleConf.append("的").append(scheduleConfMonth[0]).append("月开始，每").append(scheduleConfMonth[1]).append("个月");
							scheduleConf.append("的");
						}
						if (scheduleConfs[4].contains(",")) {
							String[] scheduleConfMonthAssign = scheduleConfs[4].split(",");
							scheduleConf.append("(");
							for (String s : scheduleConfMonthAssign) {
								scheduleConf.append(s).append("月、");
							}
							if ('、' == (scheduleConf.charAt(scheduleConf.length() - 1))) {
								scheduleConf.deleteCharAt(scheduleConf.lastIndexOf("、"));
							}
							scheduleConf.append(")");
						} else {
							scheduleConf.append(scheduleConfs[4]).append("月");
						}
					} else {
						scheduleConf.append("每个月");
					}

					if (("*".equals(scheduleConfs[5]) || "?".equals(scheduleConfs[5]))
							&& ("*".equals(scheduleConfs[3]) || "?".equals(scheduleConfs[3]))) {
						scheduleConf.append("每天");
					}

					//周
					if (!"*".equals(scheduleConfs[5]) && !"?".equals(scheduleConfs[5])) {
						//用于星期的转换
						String[] week = {"日","一","二","三","四","五","六"};
						if (scheduleConfs[5].contains("-")) {
							String[] scheduleConfWeekTo = scheduleConfs[5].split("-");
							scheduleConf.append("星期").append(week[Integer.parseInt(scheduleConfWeekTo[0]) - 1])
									.append("-星期").append(week[Integer.parseInt(scheduleConfWeekTo[1]) - 1]).append("的");
						}
						if (scheduleConfs[5].contains("/")) {
							String[] scheduleConfWeek = scheduleConfs[5].split("/");
							scheduleConf.append("第").append(scheduleConfWeek[0]).append("周的星期").append(week[Integer.parseInt(scheduleConfWeek[1]) - 1]);
						}
						if (scheduleConfs[5].contains("L")) {
							scheduleConf.append("最后一个星期").append(week[Integer.parseInt(scheduleConfs[5].substring(0,1)) - 1]).append("的");
						}
						if (scheduleConfs[5].contains(",")) {
							String[] scheduleConfWeekAssign = scheduleConfs[5].split(",");
							scheduleConf.append("(");
							for (String s : scheduleConfWeekAssign) {
								scheduleConf.append("星期").append(week[Integer.parseInt(s) - 1]).append("、");
							}
							if ('、' == (scheduleConf.charAt(scheduleConf.length() - 1))) {
								scheduleConf.deleteCharAt(scheduleConf.lastIndexOf("、"));
							}
							scheduleConf.append(")的");
						} else {
							scheduleConf.append("星期").append(week[Integer.parseInt(scheduleConfs[5]) - 1]);
						}
					}

					//日
					if (!"*".equals(scheduleConfs[3]) && !"?".equals(scheduleConfs[3])) {
						if (scheduleConfs[3].contains("-")) {
							String[] scheduleConfDayTo = scheduleConfs[3].split("-");
							scheduleConf.append(scheduleConfDayTo[0]).append("日-").append(scheduleConfDayTo[1]).append("日");
						}
						if (scheduleConfs[3].contains("/")) {
							String[] scheduleConfDay = scheduleConfs[3].split("/");
							scheduleConf.append("从").append(scheduleConfDay[0]).append("日开始，每").append(scheduleConfDay[1]).append("天");
						}
						if (scheduleConfs[3].contains("W")) {
							scheduleConf.append("离").append(scheduleConfs[3].substring(0,scheduleConfs[3].lastIndexOf("W"))).append("号最近的那个工作日");
						}
						if (scheduleConfs[3].contains("L")) {
							scheduleConf.append("最后一天");
						}
						if (scheduleConfs[3].contains(",")) {
							String[] scheduleConfDayAssign = scheduleConfs[3].split(",");
							scheduleConf.append("(");
							for (String s : scheduleConfDayAssign) {
								scheduleConf.append(s).append("号、");
							}
							if ('、' == (scheduleConf.charAt(scheduleConf.length() - 1))) {
								scheduleConf.deleteCharAt(scheduleConf.lastIndexOf("、"));
							}
							scheduleConf.append(")");
						} else {
							scheduleConf.append(scheduleConfs[3]).append("号");
						}
					}

					if (("*".equals(scheduleConfs[5]) || "?".equals(scheduleConfs[5]))
							&& "*".equals(scheduleConfs[4])
							&& ("*".equals(scheduleConfs[3]) || "?".equals(scheduleConfs[3]))) {
						scheduleConf = new StringBuffer("每天");
					}

					//时
					if (!"*".equals(scheduleConfs[2])) {
						if (scheduleConfs[2].contains("-")) {
							String[] scheduleConfHourTo = scheduleConfs[2].split("-");
							scheduleConf.append(scheduleConfHourTo[0]).append("点-").append(scheduleConfHourTo[1]).append("点");
						} else if (scheduleConfs[2].contains("/")) {
							String[] scheduleConfHour = scheduleConfs[2].split("/");
							scheduleConf.append("从").append(scheduleConfHour[0]).append("点开始，每").append(scheduleConfHour[1]).append("小时");
						} else if (scheduleConfs[2].contains(",")) {
							String[] scheduleConfHourAssign = scheduleConfs[2].split(",");
							scheduleConf.append("(");
							for (String s : scheduleConfHourAssign) {
								scheduleConf.append(s).append("点、");
							}
							if ('、' == scheduleConf.charAt(scheduleConf.length() - 1)) {
								scheduleConf.deleteCharAt(scheduleConf.lastIndexOf("、"));
							}
							scheduleConf.append(")");
						} else {
							scheduleConf.append(scheduleConfs[2]).append("点");
						}
					} else {
						scheduleConf.append("每小时");
					}

					//分
					if (!"*".equals(scheduleConfs[1])) {
						if (scheduleConfs[1].contains("-")) {
							String[] scheduleConfMinuteTo = scheduleConfs[1].split("-");
							scheduleConf.append(scheduleConfMinuteTo[0]).append("分-").append(scheduleConfMinuteTo[1]).append("分");
						} else if (scheduleConfs[1].contains("/")) {
							String[] scheduleConfMinute = scheduleConfs[1].split("/");
							scheduleConf.append("从").append(scheduleConfMinute[0]).append("分开始，每").append(scheduleConfMinute[1]).append("分钟");
						} else if (scheduleConfs[1].contains(",")) {
							String[] scheduleConfMinuteAssign = scheduleConfs[1].split(",");
							scheduleConf.append("(");
							for (String s : scheduleConfMinuteAssign) {
								scheduleConf.append(s).append("分、");
							}
							if ('、' == scheduleConf.charAt(scheduleConf.length() - 1)) {
								scheduleConf.deleteCharAt(scheduleConf.lastIndexOf("、"));
							}
							scheduleConf.append(")");
						} else {
							if (!"0".equals(scheduleConfs[1])) {
								scheduleConf.append(scheduleConfs[1]).append("分");
							}
						}
					} else {
						scheduleConf.append("每分钟");
					}

					//秒
					if (!"*".equals(scheduleConfs[0])) {
						if (scheduleConfs[0].contains("-")) {
							String[] scheduleConfSecondTo = scheduleConfs[0].split("-");
							scheduleConf.append(scheduleConfSecondTo[0]).append("秒-").append(scheduleConfSecondTo[1]).append("秒");
						} else if (scheduleConfs[0].contains("/")) {
							String[] scheduleConfSecond = scheduleConfs[0].split("/");
							scheduleConf.append("从").append(scheduleConfSecond[0]).append("秒开始，每").append(scheduleConfSecond[1]).append("秒");
						} else if (scheduleConfs[0].contains(",")) {
							String[] scheduleConfSecondAssign = scheduleConfs[0].split(",");
							scheduleConf.append("(");
							for (String s : scheduleConfSecondAssign) {
								scheduleConf.append(s).append("秒、");
							}
							if ('、' == (scheduleConf.charAt(scheduleConf.length() - 1))) {
								scheduleConf.deleteCharAt(scheduleConf.lastIndexOf("、"));
							}
							scheduleConf.append(")");
						} else {
							if (!"0".equals(scheduleConfs[0])) {
								scheduleConf.append(scheduleConfs[0]).append("秒");
							}
						}
					} else {
						scheduleConf.append("每秒");
					}

					scheduleConf.append("触发");

					xxlJobInfo.setScheduleConfChinese(scheduleConf.toString());
				}
			}
		}
	}

}
