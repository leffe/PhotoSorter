package com.lfs.tada.photosorter;

import java.io.File;
import java.io.IOException;

public class FileParts {
	
	private File file;
	
	private final String name;
	private final String extension;
	
	private int suffix;
	
	public FileParts(File fileToMove, File targetDir) throws IOException {
		this.suffix = 0;
		
		name = getName(fileToMove);
		extension = getExtension(fileToMove);

		this.file = new File(targetDir.getCanonicalPath() + "\\" + name + "." + extension);
	}
	
	public static String getName(File file) {
		String[] parts = file.getName().split("\\.");
		return parts[0];
	}
	
	public static String getExtension(File file) {
		String[] parts = file.getName().split("\\.");
		if (parts.length > 1) {
			return parts[1];
		} else {
			return "";
		}
	}
	
	
	public File getFile() {
		return file;
	}

	public File tryNewName() {
		
		suffix++;

		String newname = null;
		try {
			newname = file.getParentFile().getCanonicalPath() + "\\" + name + "_" + String.valueOf(suffix) + "." + extension;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		file = new File(newname);
		
		return file;
	}

}
