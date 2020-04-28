package com.alex.game.dblog.core;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.alex.game.dbdata.dom.PlayerDom;
import com.alex.game.dblog.LogAction;
import com.alex.game.dblog.resource.GoldLog;

/**
 * DbLogService基准测试,测试MyISAM和INNERDB,8g 4*2核， 8  Intel(R) Xeon(R) CPU E5-2620 v3 @ 2.40GHz，mysql配置如下：
# For advice on how to change settings please see
# http://dev.mysql.com/doc/refman/5.7/en/server-configuration-defaults.html

[mysqld]
#
# Remove leading # and set to the amount of RAM for the most important data
# cache in MySQL. Start at 70% of total RAM for dedicated server, else 10%.
# innodb_buffer_pool_size = 128M
#
# Remove leading # to turn on a very important data integrity option: logging
# changes to the binary log between backups.
# log_bin
#
# Remove leading # to set options mainly useful for reporting servers.
# The server defaults are faster for transactions and fast SELECTs.
# Adjust sizes as needed, experiment to find the optimal values.
# join_buffer_size = 128M
# sort_buffer_size = 2M
# read_rnd_buffer_size = 2M
datadir=/data/mysql/data
socket=/data/mysql/data/mysql.sock

# Disabling symbolic-links is recommended to prevent assorted security risks
symbolic-links=0

log-error=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid

sql-mode="ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES ,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION"

event_scheduler = 1

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

max_allowed_packet = 64M
 * @author Alex
 * @date 2017年4月13日 下午3:17:06
 */
public class BenchmarkTest {
	
	private static final int TEST_THREADS = 10;
	private static final int TEST_TASKS = 1000000;

	/**
	 * 测试10个线程每个线程写入100w日志的耗时，InnoDB：100入库时间4分35秒，MyISAM：4分56秒
	 * @throws InterruptedException 
	 */
	@Test
	public void benchmarkTest() throws InterruptedException {
		DbLoggerFactory.registerAllLoggers();
		PlayerDom playerDom =  new PlayerDom();
		playerDom.setId(123456);
		playerDom.setNickName("测试玩家");
		playerDom.setUserName("Alex123");
		playerDom.setChannelId("c100203");
		playerDom.setPackageId("cpkg100200");
		playerDom.setLoginIp("127.0.0.1");
		playerDom.setLoginTime(new Date());
		playerDom.setRegisterIp("127.0.0.1");
		playerDom.setRegisterTime(new Date());
		playerDom.getGold().set(10000);
		playerDom.getBankGold().set(2000);
		
		ExecutorService executorService = Executors.newFixedThreadPool(TEST_THREADS);
		
		for (int j = 0; j < TEST_TASKS; j++) {
			int gold = j;
			executorService.execute(() -> {
				// 测试写入GoldLog
				DbLogService.log(new GoldLog(playerDom, gold, gold, LogAction.BANKEND_ADD_BANK_GOLD));
			});
		}
		
		Thread.sleep(100000000);
	}
}
