package com.lfs.tada.photosorter;

import java.io.File;

public class Pair {
	
	private final File left;
	private final File right;
	
	public Pair(File left, File right) {
		this.right = right;
		this.left= left;
	}
	
	public File left() {
		return left;
	}
	
	public File right() {
		return right;
	}
	
}
