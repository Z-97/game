package com.alex.game.httpservice;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.LogAction;
import com.alex.game.dblog.core.DbLogService;
import com.alex.game.dblog.recharge.RechargeLog;
import com.alex.game.httpservice.util.RechargeType;
import com.alex.game.player.manager.PlayerMgr;
import com.alex.game.server.http.HttpService;
import com.alex.game.server.http.HttpServiceRes;
import com.google.inject.Inject;

/**
 * 增加玩家金币
 * 
 * @author Alex
 * @date 2017年4月9日 下午7:40:29
 */
public class AddPlayerGoldService implements HttpService {

	private static final Logger PLAYER_RECHARGE_LOG = LoggerFactory.getLogger("PlayerRechargeLog");
	@Inject
	private PlayerMgr playerMgr;

	@Override
	public String getName() {
		return "AddPlayerGoldService";
	}

	@Override
	public HttpServiceRes service(List<String> args) {
		// 后台操作管理员
		String admin = args.get(0);
		Long playerId = Long.parseLong(args.get(1));
		Long gold = Long.parseLong(args.get(2));
		String desc = args.get(3);

		PlayerDom playerDom = playerMgr.selectById(playerId);
		if (playerDom == null) {
			PLAYER_RECHARGE_LOG.warn("管理员[{}]增加金币的玩家[{}]不存在", admin, playerId);
			return HttpServiceRes.failure("玩家不存在");
		}
		if(playerDom.isAgent()){
			return HttpServiceRes.failure("代理不能后台加金币");
		}
		if (gold == null || gold == 0) {
			PLAYER_RECHARGE_LOG.warn("管理员[{}]增加银行金币[{}]不合法", admin, gold);
			return HttpServiceRes.failure("后台加金币不合法");
		}
		
		if (gold + playerDom.gold() < 0) {// 加负数情况
			PLAYER_RECHARGE_LOG.warn("管理员[{}]减金币[{}]超过玩家金币[{}]", admin, gold, playerDom.gold());
			return HttpServiceRes.failure("金币不足，操作失败!");
		}
		
		playerMgr.addGold(playerDom, gold, true, true, LogAction.BANKEND_ADD_GOLD, "操作人[" + admin + "]" + desc);
		playerMgr.updatePlayerAsync(playerDom);

		DbLogService.log(new RechargeLog(playerDom, 0, gold, RechargeType.ADMIN.type, 0));
		PLAYER_RECHARGE_LOG.info("管理员[{}]增加玩家[{}][{}]金币[{}]成功", admin, playerDom.getId(), playerDom.getNickName(), gold);
		return HttpServiceRes.success("增加金币成功");
	}

}

