package com.lfs.tada.photosorter;

import java.util.regex.Pattern;

public class Util {
	static public boolean isSourceDir(String string) {
		Pattern p = Pattern.compile("^[C]-\\d{4}-[0-1][0-9]-[0-3][0-9]");
		return p.matcher(string).matches();
	}

}
