一.文件目录说明
	(1)sql目录:系统初始化脚本
		game_data.sql:游戏数据库sql脚本
		game_dic.sql:游戏字典库sql脚本
		
	(2)game-server-1.0.0.tar:游戏服发布包
		bin目录:游戏服务器启动脚本
			game-server:Linux启动脚本,保证有可执行权限
			game-server.bat:Windows启动脚本,无用
		config:游戏配置目录
			17monipdb.dat:ip数据文件,无法修改
			application.properties:程序配置文件,里面有详细的注释说明
			jdbc目录:数据库链接配置目录
				datadb.properties:游戏数据库jdbc配置文件
				dicdb.properties:游戏字典数据库jdbc配置文件
				logdb.properties:游戏日志数据库jdbc配置文件
		lib目录:游戏项目的所有代码发布的文件和依赖的库文件		
二.部署说明
	1.修改Linux内核参数
		文件数限制修改
		(1)vi /etc/security/limits.conf,修改后当前shell再新登陆
		* soft nofile 65535
		* hard nofile 65535
		(2)tcp参数优化,修改/etc/sysctl.conf文件后,sysctl -p生效
			net.ipv4.tcp_tw_recycle = 1
			net.ipv4.tcp_tw_reuse = 1
			net.ipv4.tcp_fin_timeout = 30
			net.ipv4.tcp_keepalive_time = 1800
			net.ipv4.tcp_rmem = 4096 87380 16777216
			net.ipv4.tcp_wmem = 4096 65536 16777216
			net.ipv4.tcp_timestamps = 0
			net.ipv4.tcp_window_scaling = 0
			net.ipv4.tcp_sack = 0
			net.ipv4.tcp_no_metrics_save = 1
			net.ipv4.tcp_max_orphans = 262144
			net.ipv4.tcp_synack_retries = 2
			net.ipv4.tcp_syn_retries = 2
			net.ipv4.tcp_max_syn_backlog = 8192
			net.ipv4.tcp_max_tw_buckets = 6000
			net.ipv4.ip_local_port_range = 1024  65000
			net.core.netdev_max_backlog = 32768
			net.core.rmem_max = 16777216
			net.core.wmem_max = 16777216
			net.core.somaxconn = 65535
			#IP_TABLE防火墙在内核中会对每个TCP连接的状态进行跟踪，跟踪信息将会放在位于内核内存中的conntrackdatabase中，这个数据库的大小有限，当系统中存在过多的TCP连接时，数据库容量不足，IP_TABLE无法为新的TCP连接建立跟踪信息，于是表现为在connect()调用中阻塞。此时就必须修改内核对最大跟踪的TCP连接数的限制
			net.nf_conntrack_max = 50000		
		(3)mysql参数优化,修改mysql的my.cnf文件,修改后重启mysql
			lower_case_table_names = 1
			
			back_log = 500
			wait_timeout = 1800
			max_connections = 1000
			max_user_connections = 800
			
			innodb_buffer_pool_size = 1024M
			innodb_log_file_size = 500M
			innodb_log_buffer_size = 20M
			innodb_flush_log_at_trx_commit = 0
			innodb_lock_wait_timeout = 50
			
			innodb_autoextend_increment = 128M
			
			key_buffer_size = 400M
			query_cache_size = 40M
			read_buffer_size = 4M
			sort_buffer_size = 4M
			read_rnd_buffer_size = 8M
			tmp_table_size = 16M
			thread_cache_size = 64
			
			character-set-server=utf8mb4
			collation_server=utf8mb4_unicode_ci
			#very import param
			max_allowed_packet = 128M			
		(4)在mysql中使用"uf8mb4"编码创建game_data、game_dic、game_log库
		(5)解压game-server-1.0.0.tar文件,修改config中的相关参数
		(6)修改game-server中DEFAULT_JVM_OPTS参数,如下,服务器内存如果是32g,则-Xms24g -Xmx24g,根据情况调整
		DEFAULT_JVM_OPTS="-server -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider -Xms24g -Xmx24g -XX:NewRatio=1 -XX:SurvivorRatio=25 -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:gc.log -XX:+AggressiveOpts"
		(7)启动服务器,查看日志是否启动成功
三.运维说明
	1.服务器启动命令
		在服务器目录下,执行:nohup ./bin/game-server &命令,如果活跃玩家数量比较多，比如100w玩家数据,10w活跃玩家，启动需要大概30到60秒,玩家数据少,秒启动,
		在general.log中看到监听Http端口[7001]成功和监听Tcp端口[7000]成功则服务器启动成功
		
	2.服务器停服命令
		运维的时候可以自己写启动脚本把服务器的进程号记录下来，也可以ps查找,停服使用kill -15 进程号，必须是kill -15 进程号,切记不能强杀进程,否则后果自负,
		在general.log中看到如下则停服成功,如果数据量大则停服需要耗时一些,耐心等待
			2017-05-27 00:27:29.772 [CloseServer] INFO  com.alex.game.server.GameServer - GameServer关闭
			2017-05-27 00:27:29.773 [CloseServer] INFO  com.alex.game.server.HttpServer - HttpServer关闭
			2017-05-27 00:27:29.775 [CloseServer] INFO  com.alex.game.schedule.manager.ScheduleMgr - Scheduler关闭
			2017-05-27 00:27:29.776 [CloseServer] INFO  com.alex.game.player.manager.PlayerMgr - 正在保存玩家数据...
			2017-05-27 00:27:29.914 [Login-Executor-0] INFO  com.alex.game.login.manager.LoginMgr - 会话[id: 0xf7624ba8, L:/172.16.10.122:7000 ! R:/172.16.10.1:58476]玩家[阿里他爸][301644]退出游戏,退出类型[NORMAL]
			2017-05-27 00:27:29.915 [Login-Executor-0] INFO  com.alex.game.login.manager.LoginMgr - 会话[id: 0x50dc58b0, L:/172.16.10.122:7000 ! R:/172.16.10.1:58015]玩家[左边][301610]退出游戏,退出类型[NORMAL]
			2017-05-27 00:27:30.507 [CloseServer] INFO  com.alex.game.player.manager.PlayerMgr - 保存[507]位玩家数据成功,耗时[731]毫秒
			2017-05-27 00:27:30.507 [CloseServer] INFO  com.alex.game.login.manager.LoginMgr - 关服所有玩家踢下线成功
			2017-05-27 00:27:30.509 [CloseServer] INFO  com.alex.game.server.ExecutorMgr - 所有TaskExecutor关闭
			2017-05-27 00:27:30.509 [CloseServer] INFO  com.alex.game.dblog.core.DbLogService - 正在关闭数据库日志服务...
			2017-05-27 00:27:30.514 [CloseServer] INFO  com.alex.game.dblog.core.DbLogService - 关闭数据库日志服务成功,耗时[4]毫秒
			2017-05-27 00:27:30.514 [CloseServer] INFO  ServerLauncher - 服务器关闭成功,耗时[764]毫秒

	3.服务器运行后产生的文件说明
		(1)gc.log,jvm的gc日志
		(2)nohup.out,Linux的后台运行日志,一般情况下是空,所有的日志都记录到log目录中
		(3)log目录,游戏日志目录
			general.log:游戏运行日志,按天和文件大小滚动记录
			httpservice目录:记录了游戏服务器的http服务调用日志
			mointor目录:记录了游戏监控日志,记录了服务器的在线情况负载情况,运维需要重点关注,发现有任务堆积需要告知程序,调整资源分配策略
			msg目录:记录了玩家请求消息处理的超过10毫秒和处理异常的日志
			player目录:
				channel.log:记录玩家的渠道日志
				recharge.log:记录玩家的充值日志
				save.log:记录玩家的保存耗时日志
				
	4.一般情况下,如果程序有更新,只需要把game-server-1.0.0.tar中的lib目录覆盖服务器中的lib即可
	5.运维每天把服务器的运行情况告知程序,初期每天把服务器的运行日志给程序分析，运维需要经常在日志目录grep Excep、warn、error,
		warn(警告)一般不用担心,Excep和error是必须关注的

		