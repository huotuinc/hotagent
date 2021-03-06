package com.huotu.hotagent.admin.controller.log.page;

import com.huotu.hotagent.admin.pages.AbstractPage;
import com.huotu.hotagent.common.constant.SysConstant;
import com.huotu.hotagent.service.entity.log.CommissionLog;
import com.huotu.hotagent.service.entity.role.agent.Agent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by admin on 2016/2/25.
 */
public class CommissionLogListPage  extends AbstractPage {
    @FindBy(css = "table[class~=table-striped]")
    private WebElement balanceLogTable;
    @FindBy(css = ".pagination li")
    private List<WebElement> pageBtn;
    @FindBy(css = "input[name=agentName]")
    private WebElement agentName;
    @FindBy(css = "input[name=customerName]")
    private WebElement customerName;
    @FindBy(css = "input[name=beginTime]")
    private WebElement beginTime;
    @FindBy(css = "input[name=endTime]")
    private WebElement endTime;
    @FindBy(className = "submitSearch")
    private WebElement submitSearch;
    @FindBy(className = "resetSearch")
    private WebElement resetSearch;
    
    List<CommissionLog> commissionLogs;
    List<Agent> mockAgent;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Random random = new Random();

    public CommissionLogListPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validate() {
        //1.校验显示的数据(Table中的数据和分页相关数据)
        validateResult();
        //2.设置查询条件，校验显示的数据（Table中的数据和分页相关数据）
        validateSearchResult();

    }

    @SuppressWarnings("Duplicates")
    private void validateSearchResult() {
        //根据代理商姓名查询
        int agentNo = random.nextInt(mockAgent.size());
        String searchAgentName = mockAgent.get(agentNo).getName();
        agentName.sendKeys(searchAgentName);
        submitSearch.click();

        //代理商姓名为"测试供应商"的记录
        List<CommissionLog> searchCommissionLogs = commissionLogs.stream().filter(balanceLog ->
                balanceLog.getAgent().getName().indexOf(searchAgentName) > -1
        ).collect(Collectors.toList());
        //代理商姓名为"测试供应商"的记录数
        long totalCount = searchCommissionLogs.size();
        //代理商姓名为"测试供应商"的页数
        long searchPageCount = totalCount / SysConstant.DEFAULT_PAGE_SIZE + (totalCount % SysConstant.DEFAULT_PAGE_SIZE == 0 ? 0 : 1);

        List<WebElement> tableTrs = balanceLogTable.findElements(By.xpath("//tr"));
        //页面显示记录数
        int itemSize = tableTrs.size() - 1;
        //分页控件个数
        long pageBtnSize = pageBtn.size();
        //如果页数大于1，减去下一页和尾页两个控件
        if (searchPageCount > 1) {
            pageBtnSize -= 2;
        }
        //校验总页数和分页控件
        assertThat(itemSize).isEqualTo(Math.min(SysConstant.DEFAULT_PAGE_SIZE, searchCommissionLogs.size()));
        assertThat(pageBtnSize).isEqualTo(Math.min(8, searchPageCount));
        //校验数据
        for (int i = 0; i < itemSize; i++) {
            //获取内容区每一行的每一列
            List<WebElement> tds = tableTrs.get(i + 1).findElements(By.tagName("td"));
            //agentName
            assertThat(tds.get(1).getText().replaceAll(" ","")).isEqualTo((searchCommissionLogs.get(i).getAgent() != null ? searchCommissionLogs.get(i).getAgent().getName().replaceAll(" ","") : ""));
            //customerName
            assertThat(tds.get(2).getText().replaceAll(" ","")).isEqualTo(searchCommissionLogs.get(i).getCustomer() != null ? searchCommissionLogs.get(i).getCustomer().getName().replaceAll(" ","") : "");
            //money
            assertThat(Double.valueOf(tds.get(3).getText())).isEqualTo(searchCommissionLogs.get(i).getMoney());
            //createTime
            assertThat(tds.get(4).getText()).isEqualTo(sf.format(searchCommissionLogs.get(i).getCreateTime()));
            //memo
            assertThat(tds.get(5).getText().replaceAll(" ","")).isEqualTo(searchCommissionLogs.get(i).getMemo().replaceAll(" ",""));
        }
    }

    @SuppressWarnings("Duplicates")
    private void validateResult() {
        //总记录数
        int totalCount = commissionLogs.size();
        //页数
        int pageCount = totalCount / SysConstant.DEFAULT_PAGE_SIZE + (totalCount % SysConstant.DEFAULT_PAGE_SIZE == 0 ? 0 : 1);
        List<WebElement> tableTrs = balanceLogTable.findElements(By.xpath("//tr"));
        //页面显示记录数
        int itemSize = tableTrs.size() - 1;
        //分页控件个数
        long pageBtnSize = pageBtn.size();
        //如果页数大于1，减去下一页和尾页两个控件
        if (pageCount > 1) {
            pageBtnSize -= 2;
        }
        assertThat(itemSize).isEqualTo(Math.min(SysConstant.DEFAULT_PAGE_SIZE, commissionLogs.size()));
        assertThat(pageBtnSize).isEqualTo(Math.min(8, pageCount));
        for (int i = 0; i < itemSize; i++) {
            //获取内容区每一行的每一列
            List<WebElement> tds = tableTrs.get(i + 1).findElements(By.tagName("td"));
            //agentName
            assertThat(tds.get(1).getText().replaceAll(" ","")).isEqualTo((commissionLogs.get(i).getAgent() != null ? commissionLogs.get(i).getAgent().getName().replaceAll(" ","") : ""));
            //customerName
            assertThat(tds.get(2).getText().replaceAll(" ","")).isEqualTo(commissionLogs.get(i).getCustomer() != null ? commissionLogs.get(i).getCustomer().getName().replaceAll(" ","") : "");
            //money
            assertThat(Double.valueOf(tds.get(3).getText())).isEqualTo(commissionLogs.get(i).getMoney());
            //createTime
            assertThat(tds.get(4).getText()).isEqualTo(sf.format(commissionLogs.get(i).getCreateTime()));
            //memo
            assertThat(tds.get(5).getText().replaceAll(" ","")).isEqualTo(commissionLogs.get(i).getMemo().replaceAll(" ",""));
        }
    }

    public List<CommissionLog> getCommissionLogs() {
        return commissionLogs;
    }

    public void setCommissionLogs(List<CommissionLog> commissionLogs) {
        this.commissionLogs = commissionLogs;
    }

    public List<Agent> getMockAgent() {
        return mockAgent;
    }

    public void setMockAgent(List<Agent> mockAgent) {
        this.mockAgent = mockAgent;
    }
}
