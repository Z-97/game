package com.alex.game.dblog.httpservice;

import com.alex.game.dblog.core.DbLog;
import com.alex.game.dblog.core.TableType;
import com.alex.game.dblog.core.annotation.Column;
import com.alex.game.dblog.core.annotation.LogTable;

/**
 * HttpService日志
 * 
 * @author Alex
 * @date 2017年4月7日 下午4:11:58
 */
@LogTable(name="httpservice_log", type=TableType.MONTH)
public class HttpServiceLog extends DbLog {

	@Column(name="ip", type="varchar(128)", remark="发送后台命令的ip")
	public final String ip;
	@Column(name="content", type="varchar(4096)", remark="命令内容")
	public final String content;
	@Column(name="res", type="text", remark="接口返回")
	public final String res;
	public HttpServiceLog(String ip, String content,String res) {
		this.ip = ip;
		this.content = content;
		this.res=res;
	}
	
	
}
