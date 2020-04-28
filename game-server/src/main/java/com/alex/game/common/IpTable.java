package com.alex.game.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.netty.util.internal.ThreadLocalRandom;

/**
 * ip表
 * 
 * @author Alex
 * @date 2017年5月24日 下午3:25:13
 */
@Singleton
public class IpTable {
	
	private static final Logger LOG = LoggerFactory.getLogger(IpTable.class);
	// ip数据文件
	private static final String IP_DATA_FILEE = "config/17monipdb.dat";
	// 敏感ip的位置
	private static final String[] SENSITIVE_IP_LOCATIONS = {"柬埔寨", "香港"};

    private int offset;
    private int[] index = new int[256];
    private ByteBuffer dataBuffer;
    private ByteBuffer indexBuffer;
    private final ReentrantLock lock = new ReentrantLock();
    
    /**
     * 从ip文件中加载数据
     */
    @Inject
    private void load() {
    	File ipFile = new File(IP_DATA_FILEE);
        lock.lock();
        try(FileInputStream fin = new FileInputStream(ipFile)) {
            dataBuffer = ByteBuffer.allocate(Long.valueOf(ipFile.length()).intValue());
            int readBytesLength;
            byte[] chunk = new byte[4096];
            while (fin.available() > 0) {
                readBytesLength = fin.read(chunk);
                dataBuffer.put(chunk, 0, readBytesLength);
            }
            dataBuffer.position(0);
            int indexLength = dataBuffer.getInt();
            byte[] indexBytes = new byte[indexLength];
            dataBuffer.get(indexBytes, 0, indexLength - 4);
            indexBuffer = ByteBuffer.wrap(indexBytes);
            indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
            offset = indexLength;

            int loop = 0;
            while (loop++ < 256) {
                index[loop - 1] = indexBuffer.getInt();
            }
            indexBuffer.order(ByteOrder.BIG_ENDIAN);
        } catch (IOException e) {
        	LOG.error("加载ip字典数据异常", e);
        	System.exit(1);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 替换敏感ip为国内ip
     * 
     * @param ip
     * @return
     */
	public String replaceSensitiveIp(String ip) {
		String[] ipData = find(ip);
		String country = ipData[0];
		String province = ipData[1];
		if (contains(country, SENSITIVE_IP_LOCATIONS) || contains(province, SENSITIVE_IP_LOCATIONS)) {
			return replaceSensitiveIp(randomChineseIp());
		}

		return ip;
	}
    
    /**
     * location是否是指定locations范围内
     * 
     * @param location
     * @param locations
     * @return
     */
    private static boolean contains(String location, String... locations) {
    	for (int i = 0; i < locations.length; i++) {
			if (location.equals(locations[i])) {
				return true;
			}
		}
    	
    	return false;
    }
    
    /**
     * 根据ip返回[国家,省,市]
     * 
     * @param ip
     * @return		[中国, 四川, 成都, ]
     */
    public String[] find(String ip) {
        @SuppressWarnings("deprecation")
		int ip_prefix_value = new Integer(ip.substring(0, ip.indexOf(".")));
        long ip2long_value = ip2long(ip);
        int start = index[ip_prefix_value];
        int max_comp_len = offset - 1028;
        long index_offset = -1;
        int index_length = -1;
        byte b = 0;
        for (start = start * 8 + 1024; start < max_comp_len; start += 8) {
            if (int2long(indexBuffer.getInt(start)) >= ip2long_value) {
                index_offset = bytesToLong(b, indexBuffer.get(start + 6), indexBuffer.get(start + 5), indexBuffer.get(start + 4));
                index_length = 0xFF & indexBuffer.get(start + 7);
                break;
            }
        }

        byte[] areaBytes;

        lock.lock();
        try {
            dataBuffer.position(offset + (int) index_offset - 1024);
            areaBytes = new byte[index_length];
            dataBuffer.get(areaBytes, 0, index_length);
        } finally {
            lock.unlock();
        }

        return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
    }
    
    /**
     * 随机ip
     * 
     * @return
     */
    public static String randomIp() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        StringBuilder str = new StringBuilder();
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(r.nextInt(1000000) % 255);

        return str.toString();

    }
    
    /**
     * 随机中国ip
     * 
     * @return
     */
    private String randomChineseIp() {
        String randomIp = randomIp();
        String[] locations = find(randomIp);
        if (!"中国".equals(locations[0])) {
            return randomChineseIp();
        }
        return randomIp;
    }
    
    private static long bytesToLong(byte a, byte b, byte c, byte d) {
        return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
    }

    private static int str2Ip(String ip) {
        String[] ss = ip.split("\\.");
        int a, b, c, d;
        a = Integer.parseInt(ss[0]);
        b = Integer.parseInt(ss[1]);
        c = Integer.parseInt(ss[2]);
        d = Integer.parseInt(ss[3]);
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    private static long ip2long(String ip) {
        return int2long(str2Ip(ip));
    }

    private static long int2long(int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }
    
}
