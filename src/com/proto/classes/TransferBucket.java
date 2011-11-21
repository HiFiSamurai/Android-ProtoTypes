package com.proto.classes;

import java.io.Serializable;

public class TransferBucket implements Serializable {

	private static final long serialVersionUID = 3572734781494958770L;
	
	public static final String TRANSFER_KEY = "Transfer Object";
	
	public String val;
	
	public TransferBucket(String s) {
		val = s;
	}
}
