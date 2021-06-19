package com.xxl.job.core.executor.service.jobhandler;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;

public class DemoJobHandler extends IJobHandler {
    @XxlJob(value = "demoHandler")
    public void execute() throws Exception {
        System.out.println("sfsfssff");
    }
}
