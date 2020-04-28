package com.alex.game.games.gobang.struct;
/**
 * 棋子
 * @author yejuhua
 *
 */
public class Gobang {
	//棋子在棋盘中的x索引值
	private int x;
	//棋子在棋盘中的y索引值
	private int y;
	private GobangColor color;
	
	public Gobang(int x, int y, GobangColor color) {
		this.x=x;
		this.y=y;
		this.color=color;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public GobangColor getColor() {
		return color;
	}
	public void setColor(GobangColor color) {
		this.color = color;
	}
}
