package com.proto.classes;

import java.io.Serializable;

public class SaveBucket implements Serializable {

	private static final long serialVersionUID = -3088570810547501209L;

	public static final String SAVE_KEY = "Saved State Object";
	
	public int val;
	
	public SaveBucket(int i) {
		val = i;
	}
}
