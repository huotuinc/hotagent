/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotagent.service.service.impl;

import com.huotu.hotagent.common.constant.ApiResult;
import com.huotu.hotagent.common.constant.ResultCodeEnum;
import com.huotu.hotagent.service.entity.role.agent.Agent;
import com.huotu.hotagent.service.service.BalanceLogService;
import com.huotu.hotagent.service.service.role.agent.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by chendeyu on 2016/1/27.
 */
public class BalanceLogServiceImpl implements BalanceLogService {

    @Autowired
    AgentService agentService;

    @Override
    @Transactional(value = "transactionManager")
    public ApiResult importBl(Long id, double money) {
        ApiResult apiResult =null;
        Agent lowAgent=agentService.findById(id);
        Agent highAgent=lowAgent.getParent();
        if(highAgent.getBalance()-money>0) {//当一级代理商余额足够时
            lowAgent.setBalance(lowAgent.getBalance() + money);
            highAgent.setBalance(highAgent.getBalance() - money);
        }
        else {
            if(highAgent.getBalance()+highAgent.getCommission()-money>0){//当余额+佣金大于充值金额时
                highAgent.setBalance(0);
                highAgent.setCommission(highAgent.getBalance()+highAgent.getCommission()-money);
                lowAgent.setBalance(lowAgent.getBalance()+money);
            }
            else {
                apiResult= ApiResult.resultWith(ResultCodeEnum.IMPORT_ERROR);
                return apiResult;
            }
        }
        apiResult= ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        return apiResult;
    }
}
