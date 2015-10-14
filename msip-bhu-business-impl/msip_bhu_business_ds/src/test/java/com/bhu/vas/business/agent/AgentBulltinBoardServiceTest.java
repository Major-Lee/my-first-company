package com.bhu.vas.business.agent;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.helper.AgentBulltinType;
import com.bhu.vas.api.rpc.agent.model.AgentBulltinBoard;
import com.bhu.vas.business.ds.agent.service.AgentBulltinBoardService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;

/**
 * 
 * @author Edmond
 *
 */
public class AgentBulltinBoardServiceTest extends BaseTest {

    @Resource
    public AgentBulltinBoardService agentBulltinBoardService;

    @Test
    public void batchCreate() {
    	//List<AgentBulltinBoard> entities = new ArrayList<AgentBulltinBoard>();
    	for(int i=0;i<100;i++){
    		AgentBulltinBoard agentBulltinBoard = new AgentBulltinBoard();
        	agentBulltinBoard.setPublisher(RandomData.intNumber(10080, 50000));
        	agentBulltinBoard.setConsumer(RandomData.intNumber(10080, 50000));
        	agentBulltinBoard.setType(RandomPicker.pick(AgentBulltinType.values()).getKey());
        	agentBulltinBoard.setContent("钱来了");
        	//entities.add(agentBulltinBoard);
        	agentBulltinBoardService.insert(agentBulltinBoard);
    	}
    	
    	//agentBulltinBoardService.insertAll(entities);//insert(agentBulltinBoard);
    }

    @Test
    public void list() {
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("type", RandomPicker.pick(AgentBulltinType.values()).getKey()).andSimpleCaulse(" 1=1 ");
        mc.setPageNumber(1);
        mc.setPageSize(100);
        List<AgentBulltinBoard> results = agentBulltinBoardService.findModelByModelCriteria(mc);
        for(AgentBulltinBoard bulltin:results){
        	System.out.printf("%s %s %s %s %s \n", bulltin.getId(),bulltin.getPublisher(),bulltin.getConsumer(),bulltin.getType(),bulltin.getContent());
        }
        
    }

}
