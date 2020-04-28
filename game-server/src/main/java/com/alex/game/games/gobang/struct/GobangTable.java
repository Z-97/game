package com.alex.game.games.gobang.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import com.alex.game.games.common.AbstractRoom;
import com.alex.game.games.common.AbstractSeat;
import com.alex.game.games.common.AbstractTable;
/**
 * 五子棋桌子
 * @author yejuhua
 *
 */
public class GobangTable extends AbstractTable {
	/**
	 * 五子棋房间 
	 */
	private GobangRoom room;
	// 当前阶段
	public GobangStage stage = GobangStage.READY;
	
	//棋盘行数
	public int ROWS=14;
	//棋盘列数
	public int COLS=14;
	//初始化每个数组元素为null
	public Gobang[] chessList=new Gobang[(ROWS+1)*(COLS+1)];
	//默认开始是黑棋先下
	public boolean isBack=true;
	public int chessCount;//当前棋盘的棋子个数
	public int xIndex,yIndex;//当前刚下棋子的索引
	//桌子所有者
	public long playerId=0;
	//胜利者
	public long winPlayerId=0;
	// 房间里面的位置(order:0)
	public final List<GobangSeat> seats;
	// 游戏阶段(等待、准备、游戏，结算)定时Future
	public ScheduledFuture<?> stageFuture = null;
	public int winType = 0;
	//步时计时
	public ScheduledFuture<?>  stepFuture = null;
	//局时计时
	public ScheduledFuture<?>  totalFuture = null;
	public GobangTable(int id,GobangRoom room) {
		super(id);
		this.room=room;
		this.seats = Collections.unmodifiableList(createSeats());
	}

	@Override
	public AbstractRoom room() {
		return room;
	}

	@Override
	public List<? extends AbstractSeat> seats() {
		return seats;
	}
	/**
	 * 创建位置
	 * 
	 * @param num
	 * @return
	 */
	private List<GobangSeat> createSeats() {
		int num=2;
		List<GobangSeat> seats = new ArrayList<>(num);
		for (int i = 0; i < num; i++) {
            seats.add(new GobangSeat(i, this));
        }
		return seats;
	}
	/*判断那方赢*/
	public boolean isWin(){
		GobangColor c=isBack ? GobangColor.BLACK : GobangColor.WHITE;
		
		int continueCount=1;//连续棋子的个数
		for(int x=xIndex-1;x>=0;x--){//横向向左寻找
			
			if(getChess(x,yIndex,c)!=null){
				continueCount++;
			}else
				break;
		}
		for(int x=xIndex+1;x<=ROWS;x++){//横向向右寻找
			if(getChess(x,yIndex,c)!=null){
				continueCount++;
			}else
				break;
		}
		if(continueCount>=5){//判断记录数大于等于五，即表示此方获胜
			return true;
		}else
			continueCount=1;
		//
		for(int y=yIndex-1;y>=0;y--){//纵向向上寻找

			if(getChess(xIndex,y,c)!=null){
				continueCount++;
			}else
				break;
		}
		for(int y=yIndex+1;y<=ROWS;y++){//纵向向下寻找
			if(getChess(xIndex,y,c)!=null){
				continueCount++;
			}else
				break;
		}
		if(continueCount>=5){//判断记录数大于等于五，即表示此方获胜
			return true;
		}else {
			continueCount=1;
		}
		for(int x=xIndex+1,y=yIndex-1;y>=0&&x<=COLS;x++,y--){//右下寻找
			if(getChess(x,y,c)!=null){
				continueCount++;
			}else
				break;
		}
		for(int x=xIndex-1,y=yIndex+1;y<=ROWS&&x>=0;x--,y++){//左上寻找
			if(getChess(x,y,c)!=null){
				continueCount++;
			}else
				break;
		}
		if(continueCount>=5){//判断记录数大于等于五，即表示此方获胜
			return true;
		}else
			continueCount=1;
		//
		for(int x=xIndex-1,y=yIndex-1;y>=0&&x>=0;x--,y--){//左下寻找
			if(getChess(x,y,c)!=null){
				continueCount++;
			}else
				break;
		}
		for(int x=xIndex+1,y=yIndex+1;y<=ROWS&&x<=COLS;x++,y++){//右上寻找
			if(getChess(x,y,c)!=null){
				continueCount++;
			}else
				break;
		}
		if(continueCount>=5){//判断记录数大于等于五，即表示此方获胜
			return true;
		}else
			continueCount=1;
		return false;		
	}

	/**
	 * 查找当前位置是落子
	 * @param xIndex
	 * @param yIndex
	 * @param color
	 * @return
	 */
	private Gobang getChess(int xIndex,int yIndex,GobangColor color){
		for(Gobang c:chessList){
			if(c!=null&&c.getX()==xIndex&&c.getY()==yIndex&&c.getColor()==color)
				return c;
		}
		return null;
	}
	/**
	 * 当前位置是否有棋子
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean findChess(int x,int y){
		for(Gobang c:chessList){
			if(c!=null&&c.getX()==x&&c.getY()==y)
				return true;
		}
		return false;
	}
	public int playerNum() {
		int playerNum=0;
		for (GobangSeat seat : this.seats) {
			if (seat.playerId != 0) {
				playerNum++;
			}
		}
		return playerNum;
	}
	/**
	 * reset桌子
	 */
	public void reset() {
		this.stage = GobangStage.READY;
		this.isBack = true;
		this.chessCount = 0;
		this.xIndex = 0;
		this.yIndex = 0;
		this.winPlayerId = 0;
		this.winType =0 ;
		for (int i = 0; i < chessList.length; i++)
			chessList[i] = null;

	}
	public void clear() {
		this.reset();
		this.playerId =0 ;
	}

	/**
	 * 玩家是否到齐
	 * @return
	 */
	public boolean isAllWaittingOver() {
		if(stage!=GobangStage.READY){
			return false;
		}
		for (GobangSeat s : this.seats) {
			if (s.playerId == 0) {
				return false;
			}

		}
		return true;
	}
	/**
	 * 在线所有玩家准备完成
	 * @return
	 */
	public boolean isAllReadyOver(){
		if(stage!=GobangStage.READY){
			return false;
		}
	
		for (GobangSeat s : this.seats) {
			if (s.playerId == 0||!s.readied) {
				return false;
			}

		}
		
		return true;
	}

	/**
	 * 查找其他玩家
	 * @param id
	 * @return
	 */
	public long findAnotherPlayerId(long id) {
		for (GobangSeat s : this.seats) {
			if (s.playerId != id) {
				return s.playerId;
			}

		}
		return 0;
	}
	public GobangSeat findAnotherGobangSeat(long id) {
		for (GobangSeat s : this.seats) {
			if (s.playerId != id) {
				return s;
			}

		}
		return null;
	}
	/**
	 * 悔棋
	 * @return
	 */
	public Gobang goback(){
		if(chessCount==0)
			return null;
		Gobang gobang=chessList[chessCount-1];
		chessList[chessCount-1]=null;
		chessCount--;
		if(chessCount>0){
			xIndex=chessList[chessCount-1].getX();
			yIndex=chessList[chessCount-1].getY();
		}
		isBack=!isBack;
		return gobang;
	}

	/**
	 * 随机设置黑白
	 */
	public void setSeatColor() {
		List<GobangColor> colorList = new ArrayList<>();
		for (GobangColor gc : GobangColor.values()) {
			colorList.add(gc);
		}
		Collections.shuffle(colorList);
		for (GobangSeat s : this.seats) {
			s.gobangColor=colorList.remove(0).val;
		}
	}

	/**
	 * 是否全都退出了房间
	 * @return
	 */
	public boolean isAllExit() {
		for (GobangSeat s : this.seats) {
			if (s.playerId >0 ) {
				return false;
			}
		}
		return true;
	}
}
