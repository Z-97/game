package com.alex.game.plaza.customer.manager;
import java.util.Date;
import java.util.List;
import com.alex.game.dbdata.dom.CustomerMessage;
import com.alex.game.dbdata.mapper.CustomerMessageMapper;
import com.alex.game.player.struct.Player;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 客户
 * @author yejuhua
 *
 */
@Singleton
public class CustomerMgr {
	
	private final CustomerMessageMapper customerMessageMapper;
	@Inject
	private CustomerMsgMgr customerMsgMgr;
	@Inject
	public CustomerMgr(CustomerMessageMapper customerMessageMapper) {
		this.customerMessageMapper = customerMessageMapper;
	}
	/**
	 * 获取客服消息
	 * @param player
	 */
	public void loadCustomerMessage(Player player) {
		List<CustomerMessage> customerMessageList=customerMessageMapper.selectByPlayerId(player.getId());
		customerMsgMgr.sendResCustomerMessage(player,customerMessageList);
	}

	/**
	 * 发送客户消息
	 * @param player
	 */
	public void addCustomerMessage(Player player,String content,String phone) {
		if(content==null||content.length()<1) {
			customerMsgMgr.sendResAddCustomerMessage(player, 2);
			return ;
		}
		CustomerMessage customerMessag=createCustomerMessage(player.getId(), content,phone,player.getId());
		customerMessageMapper.insert(customerMessag);
		customerMsgMgr.sendResAddCustomerMessage(player, 0);
	}

	
	public CustomerMessage createCustomerMessage(long sendId,String content,String phone,long playerId) {
		CustomerMessage customerMessage=new CustomerMessage();
		customerMessage.setSendId(sendId);
		customerMessage.setPlayerId(playerId);
		customerMessage.setSendTime(new Date());
		customerMessage.setContent(content);
		customerMessage.setPhone(phone);
		return customerMessage;
	}
	
}
