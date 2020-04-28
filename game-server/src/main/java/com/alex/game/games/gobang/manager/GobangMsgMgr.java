package com.alex.game.games.gobang.manager;

import java.util.List;

import com.alex.game.games.gobang.struct.Gobang;
import com.alex.game.games.gobang.struct.GobangSeat;
import com.alex.game.games.gobang.struct.GobangTable;
import com.alex.game.gobang.GobangProto.GobangChess;
import com.alex.game.gobang.GobangProto.GobangSeatInfo;
import com.alex.game.gobang.GobangProto.GobangTableInfo;
import com.alex.game.gobang.GobangProto.ResAnswerPeace;
import com.alex.game.gobang.GobangProto.ResAnswerRetractChess;
import com.alex.game.gobang.GobangProto.ResCancel;
import com.alex.game.gobang.GobangProto.ResCancelMsg;
import com.alex.game.gobang.GobangProto.ResEnterGobangRoom;
import com.alex.game.gobang.GobangProto.ResExitRoom;
import com.alex.game.gobang.GobangProto.ResGobangStage;
import com.alex.game.gobang.GobangProto.ResGobangWinfo;
import com.alex.game.gobang.GobangProto.ResMoveGobang;
import com.alex.game.gobang.GobangProto.ResMoveGobangMsg;
import com.alex.game.gobang.GobangProto.ResOtherEnterTable;
import com.alex.game.gobang.GobangProto.ResPeace;
import com.alex.game.gobang.GobangProto.ResPeaceResult;
import com.alex.game.gobang.GobangProto.ResReady;
import com.alex.game.gobang.GobangProto.ResReadyMsg;
import com.alex.game.gobang.GobangProto.ResRetractChess;
import com.alex.game.gobang.GobangProto.ResRetractChessResult;
import com.alex.game.gobang.GobangProto.ResSeatColor;
import com.alex.game.gobang.GobangProto.RescreateGobangRoom;
import com.alex.game.message.CommonMessageProto.CommonMessage;
import com.alex.game.player.manager.PlayerMsgMgr;
import com.alex.game.player.struct.Player;
import com.alex.game.server.tcp.MsgHandlerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.protobuf.ByteString;

@Singleton
public class GobangMsgMgr {

	@Inject
	private PlayerMsgMgr msgMgr;

	/**
	 * 创建桌子返回
	 * 
	 * @param player
	 * @param res
	 * @param tableId
	 */
	public void sendRescreateGobangRoom(Player player, int res) {
		RescreateGobangRoom.Builder rescreateGobangRoom = RescreateGobangRoom.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("gobang.RescreateGobangRoom"),
				rescreateGobangRoom.build().toByteString());
	}

	public void sendResEnterRoomMsg(GobangSeat seat, int stepTime, int gameTime) {
		ResEnterGobangRoom.Builder resEnterGobangRoom = ResEnterGobangRoom.newBuilder();
		GobangTableInfo.Builder goTableInfo = GobangTableInfo.newBuilder();
		goTableInfo.setPwd(seat.table().id + "");
		resEnterGobangRoom.setGoTableInfo(goTableInfo);
		for (GobangSeat s : seat.table.seats) {
			if (s.playerId != 0) {
				GobangSeatInfo.Builder goingo = getSeatInfo(s);
				resEnterGobangRoom.addSeatInfo(goingo);
			}

		}
		resEnterGobangRoom.setStepTime(stepTime);
		resEnterGobangRoom.setGameTime(gameTime);
		msgMgr.writeMsg(seat.playerId, MsgHandlerFactory.getProtocol("gobang.ResEnterGobangRoom"),
				resEnterGobangRoom.build().toByteString());
	}

	private GobangSeatInfo.Builder getSeatInfo(GobangSeat s) {
		GobangSeatInfo.Builder goingo = GobangSeatInfo.newBuilder();
		goingo.setPlayerId(s.playerId);
		goingo.setOrder(s.order);
		goingo.setIcon(s.icon);
		goingo.setNickName(s.nickName);
		return goingo;
	}

	/**
	 * 其他人进入房间
	 * 
	 * @param seat
	 */
	public void sendOtherEnterTableMsg(GobangSeat seat) {
		ResOtherEnterTable.Builder resOtherEnterTable = ResOtherEnterTable.newBuilder();
		GobangSeatInfo.Builder goingo = getSeatInfo(seat);
		resOtherEnterTable.setSeatInfo(goingo);
		for (GobangSeat s : seat.table.seats) {
			if (s.playerId != 0 && s.playerId != seat.playerId) {
				msgMgr.writeMsg(s.playerId, MsgHandlerFactory.getProtocol("gobang.ResOtherEnterTable"),
						resOtherEnterTable.build().toByteString());
			}
		}
	}

	public void sendResStage(GobangTable table, int val, int time) {
		ResGobangStage.Builder resGobangStage = ResGobangStage.newBuilder();
		resGobangStage.setStage(val);
		sendTablePlayersMsg(table, MsgHandlerFactory.getProtocol("gobang.ResGobangStage"),
				resGobangStage.build().toByteString());
	}

	public void sendResReay(Player player, int res) {
		ResReady.Builder rescreateGobangRoom = ResReady.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("gobang.ResReady"),
				rescreateGobangRoom.build().toByteString());

	}

	public void sendReadyMsg(GobangSeat seat) {
		ResReadyMsg.Builder msg = ResReadyMsg.newBuilder();
		msg.setPlayerId(seat.playerId);
		sendTablePlayersMsg(seat.table, MsgHandlerFactory.getProtocol("gobang.ResReadyMsg"),
				msg.build().toByteString());
	}

	/**
	 * 向同桌的玩家(包括自己)发送消息
	 * 
	 * @param table
	 * @param msg
	 */
	public void sendTablePlayersMsg(GobangTable table, int protocol, ByteString byteString) {
		CommonMessage.Builder commonMessage = CommonMessage.newBuilder();
		commonMessage.setId(protocol);
		commonMessage.setContent(byteString);
		List<GobangSeat> seats = table.seats;
		for (int i = 0; i < seats.size(); i++) {
			GobangSeat seat = seats.get(i);
			long playerId = seat.playerId;
			if (playerId > 0) {
				msgMgr.writeMsg(playerId, commonMessage.build());
			}
		}
	}

	public void sendResMoveGobang(Player player, int res) {
		ResMoveGobang.Builder rescreateGobangRoom = ResMoveGobang.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("gobang.ResMoveGobang"),
				rescreateGobangRoom.build().toByteString());

	}

	public void sendResPeace(Player player, int res) {
		ResPeace.Builder rescreateGobangRoom = ResPeace.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("gobang.ResPeace"),
				rescreateGobangRoom.build().toByteString());

	}

	/**
	 * 发送求和请求
	 * 
	 * @param seat
	 */
	public void sendResPeaceMsg(long playerId) {
		CommonMessage.Builder commonMessage = CommonMessage.newBuilder();
		commonMessage.setId(MsgHandlerFactory.getProtocol("gobang.ResPeaceMsg"));
		msgMgr.writeMsg(playerId, commonMessage.build());

	}

	public void sendResAnswerPeace(Player player, int res) {
		ResAnswerPeace.Builder rescreateGobangRoom = ResAnswerPeace.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("gobang.ResAnswerPeace"),
				rescreateGobangRoom.build().toByteString());
	}

	/**
	 * 求和结果通知
	 * 
	 * @param playerId
	 * @param state
	 */
	public void sendResPeaceResult(long playerId, boolean state) {
		ResPeaceResult.Builder resPeace = ResPeaceResult.newBuilder();
		resPeace.setState(state);
		CommonMessage.Builder commonMessage = CommonMessage.newBuilder();
		commonMessage.setId(MsgHandlerFactory.getProtocol("gobang.ResPeaceResult"));
		commonMessage.setContent(resPeace.build().toByteString());
		msgMgr.writeMsg(playerId, commonMessage.build());

	}

	/**
	 * 返回悔棋发送结果
	 * 
	 * @param player
	 * @param res
	 */
	public void sendResRetractChess(Player player, int res) {
		ResRetractChess.Builder rescreateGobangRoom = ResRetractChess.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("gobang.ResRetractChess"),
				rescreateGobangRoom.build().toByteString());

	}

	/**
	 * 发送悔棋请求
	 * 
	 * @param anotherPlayerId
	 */
	public void sendResRetractChessMsg(long anotherPlayerId) {
		CommonMessage.Builder commonMessage = CommonMessage.newBuilder();
		commonMessage.setId(MsgHandlerFactory.getProtocol("gobang.ResRetractChessMsg"));
		msgMgr.writeMsg(anotherPlayerId, commonMessage.build());

	}

	public void sendResAnswerRetractChess(Player player, int res) {
		ResAnswerRetractChess.Builder rescreateGobangRoom = ResAnswerRetractChess.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("gobang.ResAnswerRetractChess"),
				rescreateGobangRoom.build().toByteString());
	}

	public void sendResRetractChessResult(GobangTable table, boolean state, Gobang gobang1,Gobang gobang2) {
		ResRetractChessResult.Builder msg = ResRetractChessResult.newBuilder();
		GobangChess.Builder gobangChess = GobangChess.newBuilder();
		if (gobang1 != null) {
			gobangChess.setX(gobang1.getX());
			gobangChess.setY(gobang1.getY());
			gobangChess.setColor(gobang1.getColor().val);
			msg.addChess(gobangChess);
		}
		GobangChess.Builder gobangChess2 = GobangChess.newBuilder();
		if (gobang2 != null) {
			gobangChess2.setX(gobang2.getX());
			gobangChess2.setY(gobang2.getY());
			gobangChess2.setColor(gobang2.getColor().val);
			msg.addChess(gobangChess2);
		}
		msg.setState(state);
		sendTablePlayersMsg(table, MsgHandlerFactory.getProtocol("gobang.ResRetractChessResult"),
				msg.build().toByteString());
	}

	public void sendResRetractChessResult(long playerId, boolean state) {
		ResRetractChessResult.Builder msg = ResRetractChessResult.newBuilder();
		msg.setState(state);
		msgMgr.writeMsg(playerId, MsgHandlerFactory.getProtocol("gobang.ResRetractChessResult"),
				msg.build().toByteString());
	}

	public void sendResExitRoom(Player player, int res) {
		ResExitRoom.Builder rescreateGobangRoom = ResExitRoom.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("gobang.ResExitRoom"),
				rescreateGobangRoom.build().toByteString());
	}

	public void sendResCancelReady(Player player, int res) {
		ResCancel.Builder rescreateGobangRoom = ResCancel.newBuilder();
		rescreateGobangRoom.setRes(res);
		msgMgr.sendMsg(player, MsgHandlerFactory.getProtocol("gobang.ResCancel"),
				rescreateGobangRoom.build().toByteString());

	}

	public void sendCancelReadyMsg(GobangSeat seat) {
		ResCancelMsg.Builder msg = ResCancelMsg.newBuilder();
		msg.setPlayerId(seat.playerId);
		sendTablePlayersMsg(seat.table, MsgHandlerFactory.getProtocol("gobang.ResCancelMsg"),
				msg.build().toByteString());
	}

	public void sendGobangWinfo(GobangTable table) {
		ResGobangWinfo.Builder msg = ResGobangWinfo.newBuilder();
		msg.setWinType(table.winType);
		sendTablePlayersMsg(table, MsgHandlerFactory.getProtocol("gobang.ResGobangWinfo"), msg.build().toByteString());
	}

	public void sendResSeatColor(GobangTable table) {
		ResSeatColor.Builder msg = ResSeatColor.newBuilder();
		for (GobangSeat seat : table.seats) {
			if (seat.gobangColor == 1) {
				msg.setWPlayerId(seat.playerId);
			} else {
				msg.setBPlayerId(seat.playerId);
			}
		}
		sendTablePlayersMsg(table, MsgHandlerFactory.getProtocol("gobang.ResSeatColor"), msg.build().toByteString());
	}

	public void sendResMoveGobangMsg(GobangSeat seat, int x, int y) {
		ResMoveGobangMsg.Builder msg = ResMoveGobangMsg.newBuilder();
		msg.setTotalTime(seat.totalTime);
		GobangChess.Builder gobangChess = GobangChess.newBuilder();
		gobangChess.setColor(seat.gobangColor);
		gobangChess.setX(x);
		gobangChess.setY(y);
		msg.setChess(gobangChess);
		sendTablePlayersMsg(seat.table, MsgHandlerFactory.getProtocol("gobang.ResMoveGobangMsg"),
				msg.build().toByteString());
	}

}
