package com.github.nagaseyasuhito.benchmark.array.merge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Logger;

import org.junit.Test;

import com.google.common.base.Stopwatch;

public class ArrayMergeTest {
	private static final Logger log = Logger.getLogger(ArrayMergeTest.class.getCanonicalName());

	@Test
	public void benchmark() throws IOException {
		byte[] left = new byte[64];
		byte[] right = new byte[64];
		Arrays.fill(left, (byte) 0x01);
		Arrays.fill(right, (byte) 0x02);

		// ウォームアップ
		for (int i = 0; i < 100_000_000; i++) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(left);
			out.write(right);
			out.toByteArray();

			byte[] merged = new byte[left.length + right.length];
			System.arraycopy(left, 0, merged, 0, left.length);
			System.arraycopy(right, 0, merged, left.length, right.length);

			ByteBuffer.allocate(left.length + right.length).put(left).put(right).array();
		}
		log.info("Warmup finished");

		// PROS: 流れるようなインターフェースで記述ができる
		Stopwatch byteBuffer = Stopwatch.createStarted();
		for (int i = 0; i < 100_000_000; i++) {
			ByteBuffer.allocate(left.length + right.length).put(left).put(right).array();
		}
		log.info("ByteBuffer: " + byteBuffer);

		// CONS: コードが冗長
		Stopwatch arraycopy = Stopwatch.createStarted();
		for (int i = 0; i < 100_000_000; i++) {
			byte[] merged = new byte[left.length + right.length];
			System.arraycopy(left, 0, merged, 0, left.length);
			System.arraycopy(right, 0, merged, left.length, right.length);
		}
		log.info("System.arraycopy: " + arraycopy);

		// PROS: lengthを指定しなくて良い
		// CONS: IOExceptionを投げる
		Stopwatch byteArrayOutputStream = Stopwatch.createStarted();
		for (int i = 0; i < 100_000_000; i++) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(left);
			out.write(right);
			out.toByteArray();
		}
		log.info("ByteArrayOutputStream: " + byteArrayOutputStream);
	}
}
