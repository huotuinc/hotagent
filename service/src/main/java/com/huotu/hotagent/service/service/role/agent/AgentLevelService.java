/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotagent.service.service.role.agent;

import com.huotu.hotagent.service.entity.role.agent.AgentLevel;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 代理商等级
 * Created by allan on 1/26/16.
 */
public interface AgentLevelService {
    AgentLevel save(AgentLevel agentLevel);

    List<AgentLevel> agentLevelList();

    AgentLevel findByLevel(int level);

    boolean exist();

    Page<AgentLevel> findAgentLevels(int pageNo);
}
