package com.alex.game.plaza.bank.hanlder;

import com.alex.game.bank.BankProto.ReqResetBankPwd;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.struct.Player;
import com.alex.game.plaza.bank.manager.BankMgr;
import com.alex.game.server.tcp.Ihandler;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
/**
 * 重置银行密码
 * @author yejuhua
 *
 */
public class ReqResetBankPwdHandler implements Ihandler {
	@Inject
	private BankMgr bankMgr;
	@Override
	public void action(Player player, CommonMessage msg) throws InvalidProtocolBufferException {
		ReqResetBankPwd reqResetBankPwd=ReqResetBankPwd.parseFrom(msg.getContent());
		bankMgr.resetBankPwd(player,reqResetBankPwd.getNewBankPwd(),reqResetBankPwd.getCode());
	}

}
