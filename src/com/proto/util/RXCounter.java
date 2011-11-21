package com.proto.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class RXCounter {
	private static final String BASE_DIR = "/sys/class/net";
	private static final String TARGET_FILE = "/statistics/rx_bytes";

	/**
	 * The TrafficStats class is only valid for API>=8, but this gets around that by reading
	 * from the file that it checks.
	 *
	 * @return The total number of bytes received
	 */
	public static long getRXCount() {
		long total = 0;

		List<File> dirs = getSubDirectories(BASE_DIR);
		for (File f : dirs)
			total += getFileVal(f.getAbsolutePath() + TARGET_FILE);

		return total;
	}

	private static List<File> getSubDirectories(String path) {
		List<File> dirs = new ArrayList<File>();
		File[] files = new File(path).listFiles();

		for (File f : files) {
			if (f.isDirectory())
				dirs.add(f);
		}	

		return dirs;
	}

	private static long getFileVal(String path) {
		try {
			System.out.println("Reading File: " + path);
			BufferedReader in = new BufferedReader(new FileReader(path));
			long x = Long.valueOf(in.readLine());
			System.out.println("Value: " + x);

			return x;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
