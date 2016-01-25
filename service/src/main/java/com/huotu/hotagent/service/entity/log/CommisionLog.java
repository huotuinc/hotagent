package com.huotu.hotagent.service.entity.log;

import com.huotu.hotagent.service.common.LogType;
import com.huotu.hotagent.service.entity.role.Agent;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 佣金日志
 * Created by cwb on 2016/1/22.
 */
@Entity
@Table(name = "age_commisionLog")
@Getter
@Setter
public class CommisionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 所属代理商
     */
    @ManyToOne
    @JoinColumn(name = "agentId")
    private Agent agent;

    /**
     * 因哪个代理商产生的佣金
     */
    @ManyToOne
    @JoinColumn(name = "quarryId")
    private Agent quarry;

    /**
     * 金额
     */
    @Column(name = "money")
    private double money;

    /**
     * 创建时间
     */
    @Column(name = "createTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 备注
     */
    @Column(name = "memo")
    private String memo;

    /**
     * 支出金额
     */
    @Column(name = "exportMoney")
    private double exportMoney;

    /**
     * 收入金额
     */
    @Column(name = "importMoney")
    private double importMoney;

    /**
     * 日志类型
     */
    @Column(name = "logType")
    private LogType logType;
}
