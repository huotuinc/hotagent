/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotagent.agent.controller;

import com.huotu.hotagent.common.constant.ApiResult;
import com.huotu.hotagent.common.constant.ResultCodeEnum;
import com.huotu.hotagent.service.common.AgentType;
import com.huotu.hotagent.service.entity.role.agent.Agent;
import com.huotu.hotagent.service.entity.role.agent.AgentLevel;
import com.huotu.hotagent.service.service.log.BalanceLogService;
import com.huotu.hotagent.service.service.role.agent.AgentLevelService;
import com.huotu.hotagent.service.service.role.agent.AgentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * Created by chendeyu on 2016/1/25.
 */

@Controller
public class AgentController {

    private static final Log log = LogFactory.getLog(AgentController.class);

    @Autowired
    AgentService agentService;

    @Autowired
    AgentLevelService agentLevelService;

    @Autowired
    BalanceLogService balanceLogService;

    @Autowired
    private PasswordEncoder passwordEncoder;



    /**
     *�˻���Ϣ
     */
    @RequestMapping("/showAccount")
    public ModelAndView showAccount(@RequestParam(value = "id", defaultValue = "0") Long id) throws Exception{
        ModelAndView modelAndView=new ModelAndView();
        Agent agent = agentService.findById(id);
        modelAndView.setViewName("/views/agent/showAccount");
        modelAndView.addObject("agent",agent);
        return modelAndView;
    }



    /**
     *�ҵĴ������б�
     */
    @RequestMapping("/myAgents")
    public ModelAndView showAgentList(@RequestParam(value = "id", defaultValue = "0") Long id) throws Exception{
        ModelAndView modelAndView=new ModelAndView();
        Agent agent = agentService.findById(id);
        modelAndView.setViewName("views/agent/myAgents");
        modelAndView.addObject("agent",agent);
        return modelAndView;
    }



    /**
     *���˴�������Ϣ/�޸Ĵ�����
     */
    @RequestMapping("/showAgent")
    public ModelAndView showAgent(@RequestParam(value = "id", defaultValue = "0") Long id) throws Exception{
        ModelAndView modelAndView=new ModelAndView();
        Agent agent = agentService.findById(id);
        modelAndView.setViewName("views/agent/showAgent");
        modelAndView.addObject("agent",agent);
        return modelAndView;
    }


    /**
     *�����¼�������
     */
    @RequestMapping("/addAgent")
    public ModelAndView addAgent(@AuthenticationPrincipal Agent agent) throws Exception{
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("views/agent/agent_add");
        return modelAndView;
    }



    /**
     *ɾ���¼�������
     */
    @RequestMapping(value = "/delAgent",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult delAgent(@RequestParam(value = "id") Long id) throws Exception{

        ApiResult apiResult =null;
        try {
            apiResult= agentService.delAgent(id);
        }catch (Exception ex){
            log.error(ex.getMessage());
            apiResult = ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
        }
        return apiResult;
    }


    /**
     *�����¼�������
     */
    @RequestMapping(value = "/lockAgent",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult lockAgent(@RequestParam(value = "id") Long id) throws Exception{

        ApiResult apiResult =null;
        try {
            apiResult= agentService.lockAgent(id);
        }catch (Exception ex){
            log.error(ex.getMessage());
            apiResult = ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
        }
        return apiResult;
    }


    /**
     *�޸Ĵ���������
     */
    @RequestMapping(value="/updatePw",method = RequestMethod.GET)
    public ModelAndView updatePw(@AuthenticationPrincipal Agent agent) throws Exception{
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("views/agent/agentPw_edit");
        modelAndView.addObject("agent",agent);
        return modelAndView;
    }


    /**
     *�����޸Ĵ���������
     */
    @RequestMapping(value = "/savePw",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult savePw(@AuthenticationPrincipal Agent agent,
                               @RequestParam(value = "password") String password,@RequestParam(value = "newPassword") String newPassword) throws Exception{

        ApiResult apiResult =null;
        try {
           Boolean bl = passwordEncoder.matches(password,agent.getPassword());
            if(bl){
                agent.setPassword(passwordEncoder.encode(newPassword));
                agentService.save(agent);
                apiResult= ApiResult.resultWith(ResultCodeEnum.SUCCESS);
            }
            else{
                apiResult = ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
            }

        }catch (Exception ex){
            log.error(ex.getMessage());
            apiResult = ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        }
        return apiResult;
    }


    /**
     *�����¼���������Ϣ
     */
    @RequestMapping(value = "/saveLowerAg ",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult saveLowerAg(@AuthenticationPrincipal Agent Higher,
                                 Agent agent,int agentType,int agentLevel) throws Exception{

        ApiResult apiResult =null;
        try {
            Date date = new Date();
            AgentLevel aLevel = agentLevelService.findByLevel(agentLevel);
            AgentType type = AgentType.valueOf(agentType);
            agent.setType(type);
            agent.setLevel(aLevel);
            agent.setParent(Higher);
            agent.setExpandable(false);
            agent.setCreateTime(date);
            agentService.save(agent);
            apiResult= ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }catch (Exception ex){
            log.error(ex.getMessage());
            apiResult = ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        }
        return apiResult;
    }

    /**
     *���¼������̳�ֵ
     */
    @RequestMapping(value = "/importBl",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult importBl(@AuthenticationPrincipal Agent agent,double money) throws Exception{

        ApiResult apiResult = null;
        Long id = agent.getId();
        try {
            Boolean bl=balanceLogService.importBl(id,money);
            if(bl==true){
                apiResult =  ApiResult.resultWith(ResultCodeEnum.SUCCESS);
            }
            else {
                apiResult = ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
            apiResult = ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        }
        return apiResult;

    }





}
