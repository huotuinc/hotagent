/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotagent.service.entity.role.agent;

import com.huotu.hotagent.service.common.AgentType;
import com.huotu.hotagent.service.common.Authority;
import com.huotu.hotagent.service.entity.product.Price;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 代理商
 * Created by cwb on 2016/1/21.
 */
@Entity
@Table(name = "age_agent",uniqueConstraints = @UniqueConstraint(columnNames ={"city"}))
@Getter
@Setter
public class Agent extends Login {

    /**
     * 代理商名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 代理商级别
     */
    @ManyToOne
    @JoinColumn(name = "agentLevel")
    private AgentLevel level;

    /**
     * 代理商类型
     */
    @Column(name = "type")
    private AgentType type;

    /**
     * 余额
     */
    @Column(name = "balance")
    private double balance;

    /**
     * 佣金
     */
    @Column(name = "commission")
    private double commission;

    /**
     * 省
     */
    @Column(name = "province")
    private String province;

    /**
     * 市
     */
    @Column(name = "city")
    private String city;

    /**
     * 区
     */
    @Column(name = "district")
    private String district;

    /**
     * 联系人
     */
    @Column(name = "contacts",length = 50)
    private String contacts;

    /**
     * 手机号
     */
    @Column(name = "phoneNo")
    private String phoneNo;

    /**
     * 联系地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 邮箱
     */
    @Column(name = "mail")
    private String mail;

    /**
     * qq号
     */
    @Column(name = "qq")
    private String qq;

    /**
     * 上级代理
     */
    @OneToOne
    @JoinColumn(name = "parentId")
    private Agent parent;

    /**
     * 企业资质图片Uri
     */
    @Column(name = "qualifyUri")
    private String qualifyUri;

    /**
     * 产品价格
     */
    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<Price> prices;

    /**
     * 是否可发展下级代理
     */
    @Column(name = "expandable")
    private boolean expandable = true;

    @Lob
    private Set<Authority> authorities = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = new HashSet<>();
        authorities.forEach(authority -> simpleGrantedAuthorities.add(new SimpleGrantedAuthority(authority.getValue())));
        return simpleGrantedAuthorities;
    }

    public void addAuthority(Authority authority) {
        authorities.add(authority);
    }

    public String getStatus() {
        if(!isAccountNonLocked()) {
            return "冻结";
        }
        if(!isAccountNonExpired()) {
            return "过期";
        }
        return "正常";
    }

    public void addPrice(Price price) {
        price.setAgent(this);
        if(prices==null||prices.size()==0) {
            prices = new HashSet<>();
        }
        prices.add(price);
    }
}
