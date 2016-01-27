/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotagent.service.service.role.manager.impl;

import com.huotu.hotagent.service.entity.role.manager.Manager;
import com.huotu.hotagent.service.repository.role.manager.ManagerRepository;
import com.huotu.hotagent.service.service.role.manager.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by allan on 1/27/16.
 */
@Service
public class ManagerServiceImpl implements ManagerService {
    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public Manager save(Manager manager) {
        return managerRepository.save(manager);
    }

    @Override
    public Manager findByUsername(String username) {
        return managerRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = managerRepository.findByUsername(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("未找到该管理员");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new DisabledException("该管理员帐号已被冻结,请联系超级管理员");
        }

        return userDetails;
    }
}
