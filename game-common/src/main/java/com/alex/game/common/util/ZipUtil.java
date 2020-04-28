/*
 * Copyright (c) 2016, Alex. All Rights Reserved.
 */

package com.alex.game.common.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Zip工具类
 *
 * @author Alex
 * @date 2016/7/8 9:54
 */
public class ZipUtil {

	/**
	 * 压缩字节
	 *
	 * @param data
	 * @return
	 */
	public static byte[] compress(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.reset();
		deflater.setInput(data);
		deflater.finish();

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length / 2);) {
			byte[] buf = new byte[data.length / 2];
			while (!deflater.finished()) {
				int numBytes = deflater.deflate(buf);
				bos.write(buf, 0, numBytes);
			}
			
			deflater.end();
			return bos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 解压字节
	 *
	 * @param data
	 * @return
	 */
	public static byte[] decompress(byte[] data) {
		Inflater inflater = new Inflater();
		inflater.reset();
		inflater.setInput(data);

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length * 4);) {
			byte[] buf = new byte[data.length];
			while (!inflater.finished()) {
				int numBytes = inflater.inflate(buf);
				outputStream.write(buf, 0, numBytes);
			}
			inflater.end();
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
