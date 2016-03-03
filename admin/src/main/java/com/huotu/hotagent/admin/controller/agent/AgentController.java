/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotagent.admin.controller.agent;

import com.huotu.hotagent.admin.service.StaticResourceService;
import com.huotu.hotagent.common.constant.ApiResult;
import com.huotu.hotagent.common.constant.ResultCodeEnum;
import com.huotu.hotagent.common.constant.SysConstant;
import com.huotu.hotagent.common.utils.CommonUtils;
import com.huotu.hotagent.service.common.AgentType;
import com.huotu.hotagent.service.common.Authority;
import com.huotu.hotagent.service.entity.log.BalanceLog;
import com.huotu.hotagent.service.entity.product.Price;
import com.huotu.hotagent.service.entity.product.Product;
import com.huotu.hotagent.service.entity.role.agent.Agent;
import com.huotu.hotagent.service.entity.role.agent.AgentLevel;
import com.huotu.hotagent.service.model.AgentSearch;
import com.huotu.hotagent.service.model.ProductPrice;
import com.huotu.hotagent.service.service.log.BalanceLogService;
import com.huotu.hotagent.service.service.product.PriceService;
import com.huotu.hotagent.service.service.product.ProductService;
import com.huotu.hotagent.service.service.role.agent.AgentLevelService;
import com.huotu.hotagent.service.service.role.agent.AgentService;
import com.huotu.hotagent.service.service.role.agent.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.util.*;


/**
 * 代理商相关
 * Created by allan on 1/25/16.
 */
@Controller
public class AgentController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private AgentLevelService agentLevelService;
    @Autowired
    private StaticResourceService staticResourceService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private BalanceLogService balanceLogService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PriceService priceService;


    /**
     * 代理商列表
     * @return
     */
    @RequestMapping(value = "/agents", method = RequestMethod.GET)
    @PreAuthorize("hasAnyAuthority('MANAGER_ROOT','MANAGER_AGENT')")
    public String AgentList(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            AgentSearch agentSearch,
            Model model
    ) {
        model.addAttribute("agentSearch", agentSearch);
        Page<Agent> agents = agentService.findAll(pageNo, SysConstant.DEFAULT_PAGE_SIZE, agentSearch);
        int totalPages = agents.getTotalPages();
        model.addAttribute("pageSize", agents.getSize());
        model.addAttribute("agents", agents.getContent());
        model.addAttribute("totalRecords", agents.getTotalElements());
        model.addAttribute("totalPages",totalPages);
        model.addAttribute("currentPage", pageNo);
        List<AgentLevel> agentLevels = agentLevelService.agentLevelList();
        model.addAttribute("agentLevels", agentLevels);
        model.addAttribute("hasNext",agents.hasNext());
        model.addAttribute("hasPrevious",agents.hasPrevious());
        int pageBtnNum = totalPages > SysConstant.DEFAULT_PAGE_BUTTON_NUM ? SysConstant.DEFAULT_PAGE_BUTTON_NUM : totalPages;
        int startPageNo = CommonUtils.calculateStartPageNo(pageNo, pageBtnNum, totalPages);
        List<Integer> pageNos = new ArrayList<>();
        for(int i=1;i<=pageBtnNum;i++) {
            pageNos.add(startPageNo);
            startPageNo++;
        }
        model.addAttribute("pageNos", pageNos);
        return "agent/agent_list";
    }

    @RequestMapping(value = "/agents/{id}",method = RequestMethod.GET)
    public String showAgentDetail(@PathVariable Long id,Model model) throws Exception {
        Agent agent = agentService.findById(id);
        agent.setQualifyUri(staticResourceService.getResource(agent.getQualifyUri()).toString());
        model.addAttribute("agent",agent);
        return "agent/agent_detail";
    }

    /**
     * 修改代理商页面
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/editAgentForm/{id}", method = RequestMethod.GET)
    public String editAgentForm(Model model,@PathVariable Long id) {
        Agent agent = agentService.findById(id);
        Set<Price> prices = agent.getPrices();
        model.addAttribute("agent",agent);
        model.addAttribute("prices",prices);
        List<AgentLevel> agentLevels = agentLevelService.agentLevelList();
        model.addAttribute("agentLevels",agentLevels);
        model.addAttribute("agentTypes",AgentType.values());
        return "agent/agent_edit";
    }

    /**
     * 新增代理商页面
     * @param model
     * @return
     */
    @RequestMapping(value = "/newAgentForm",method = RequestMethod.GET)
    public String newAgentForm(Model model) {
        Set<Price> prices = priceService.getBasePrices();
        model.addAttribute("prices",prices);
        List<AgentLevel> agentLevels = agentLevelService.agentLevelList();
        model.addAttribute("agentLevels",agentLevels);
        model.addAttribute("agentTypes",AgentType.values());
        return "agent/agent_new";
    }

    /**
     * 新增代理商
     * @param name
     * @param username
     * @param password
     * @param level
     * @param type
     * @param balance
     * @param province
     * @param city
     * @param district
     * @param contacts
     * @param phoneNo
     * @param address
     * @param mail
     * @param qq
     * @param qualifyUri
     * @param expandable
     * @param prices 字符串数组，单个元素的结构形如"1|5000","|"前的数字代表productId，后面的代表价格
     * @return
     */
    @RequestMapping(value = "/agents",method = RequestMethod.POST)
    @Transactional
    public String createNewAgent(String name,
                                 String username,
                                 String password,
                                 AgentLevel level,
                                 AgentType type,
                                 @RequestParam(required = false,defaultValue = "0") double balance,
                                 String province,
                                 String city,
                                 String district,
                                 String contacts,
                                 String phoneNo,
                                 String address,
                                 String mail,
                                 String qq,
                                 String qualifyUri,
                                 @RequestParam(required = false,defaultValue = "1") boolean expandable,
                                 String... prices){
        Agent agent = new Agent();
        agent.setCreateTime(new Date());
        agent.addAuthority(Authority.AGENT_ROOT);
        agent.setUsername(username);
        agent.setName(name);
        agent.setLevel(level);
        agent.setType(type);
        agent.setBalance(balance);
        agent.setProvince(province);
        agent.setCity(city);
        agent.setDistrict(district);
        agent.setContacts(contacts);
        agent.setPhoneNo(phoneNo);
        agent.setAddress(address);
        agent.setMail(mail);
        agent.setQq(qq);
        agent.setQualifyUri(qualifyUri);
        agent.setExpandable(expandable);
        for(String price : prices) {
            Price pe = new Price();
            pe.setAgent(agent);
            String[] strs = price.split("|");
            Product product = productService.findOne(Long.parseLong(strs[0]));
            pe.setProduct(product);
            pe.setPrice(Double.parseDouble(strs[1]));
        }
        loginService.newLogin(agent,password);
        return "redirect:/agents";
    }

    /**
     * 修改代理商
     * @param level
     * @param type
     * @param province
     * @param city
     * @param district
     * @param contacts
     * @param phoneNo
     * @param address
     * @param mail
     * @param qq
     * @param qualifyUri
     * @param expandable
     * @param prices 字符串数组，单个元素的结构形如"1|5000","|"前的数字代表productId，后面的代表价格
     * @return
     */
    @RequestMapping(value = "/agents/{id}",method = RequestMethod.POST)
    @Transactional
    public String editAgent(@PathVariable Long id,
                            AgentLevel level,
                            String name,
                            AgentType type,
                            String province,
                            String city,
                            String district,
                            String contacts,
                            String phoneNo,
                            String address,
                            String mail,
                            String qq,
                            String qualifyUri,
                            @RequestParam(required = false,defaultValue = "1") boolean expandable,
                            String... prices) {
        Agent agent = agentService.findById(id);
        agent.setName(name);
        agent.setLevel(level);
        agent.setType(type);
        agent.setProvince(province);
        agent.setCity(city);
        agent.setDistrict(district);
        agent.setContacts(contacts);
        agent.setPhoneNo(phoneNo);
        agent.setAddress(address);
        agent.setMail(mail);
        agent.setQq(qq);
        agent.setQualifyUri(qualifyUri);
        agent.setExpandable(expandable);
        for(String price : prices) {
            Price pe = new Price();
            pe.setAgent(agent);
            String[] strs = price.split("|");
            Product product = productService.findOne(Long.parseLong(strs[0]));
            pe.setProduct(product);
            pe.setPrice(Double.parseDouble(strs[1]));
        }
        return "redirect:/agents";
    }

    /**
     * 充值
     * @param id
     * @param money
     * @return
     */
    @RequestMapping("/recharge")
    @ResponseBody
    @Transactional
    public ApiResult recharge(Long id,@RequestParam(defaultValue = "0") double money) {
        Agent agent = agentService.findById(id);
        if(money<0) {
            return ApiResult.resultWith(ResultCodeEnum.CAN_NOT_BE_NEGATIVE);
        }
        agent.setBalance(agent.getBalance()+money);
        agentService.save(agent);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 修改密码
     * @param id
     * @param password
     * @return
     */
    @RequestMapping("/resetPassword")
    @ResponseBody
    @Transactional
    public ApiResult resetPassword(Long id,String password) {
        Agent agent = agentService.findById(id);
        if(StringUtils.isEmpty(password)) {
            return ApiResult.resultWith(ResultCodeEnum.PASSWORD_NULL);
        }
        loginService.newLogin(agent,password);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 新增（修改）代理商
     *
     * @param agent
     * @return
     */
    @RequestMapping(value = "/agents", method = RequestMethod.POST)
    @Transactional
    public String AgentEdit(Agent agent,ProductPrice productPrice) throws Exception{
        agent.setAuthorities(new HashSet<>(Arrays.asList(Authority.AGENT_ROOT)));
        Set<Price> prices = priceService.setPrices(agent, productPrice);
        agent.setPrices(prices);
        loginService.newLogin(agent, agent.getPassword());
        return "redirect:/agents";
    }

    /**
     * 检测指定城市是否可设置独家代理
     * @param city
     * @return
     */
    @RequestMapping("/checkCity")
    @ResponseBody
    public ApiResult checkCity(String city) {
        List<Agent> agents = agentService.findByCity(city);
        if(agents.size()==0) {
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }
        if(agents.size()==1) {
            return ApiResult.resultWith(ResultCodeEnum.HAS_SOLE_ALREADY);
        }
        return ApiResult.resultWith(ResultCodeEnum.IS_NORMAL_AGENT_AREA);
    }

    @RequestMapping("/checkUsername")
    @ResponseBody
    public String checkUsername(String username) {
        Agent agent = agentService.findByUsername(username);
        if(agent==null) {
            return "true";
        }
        return "已经有同名的账号";
    }

    @RequestMapping("/uploadImg")
    @ResponseBody
    @SuppressWarnings("Duplicates")
    public ApiResult uploadImg(MultipartFile qualify,String qualifyUri) throws Exception {
        //delete img
        if(qualify.getSize()==0) {
            staticResourceService.deleteResource(qualifyUri);
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS,"");
        }
        if (ImageIO.read(qualify.getInputStream()) == null) {
            return ApiResult.resultWith(ResultCodeEnum.NOT_IMG);
        }
        //change img
        if(!StringUtils.isEmpty(qualifyUri)) {
            staticResourceService.deleteResource(qualifyUri);
        }
        String fileName = qualify.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = StaticResourceService.AGENT_IMG + UUID.randomUUID().toString() + "." + suffix;
        staticResourceService.uploadResource(path, qualify.getInputStream());
        String fullPath = staticResourceService.getResource(path).toString();
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS,Arrays.asList(path,fullPath).toArray());
    }

}
