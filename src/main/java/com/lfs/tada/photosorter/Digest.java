package com.lfs.tada.photosorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digest {

	private static Digest INST = null;
	
	private MessageDigest digest = null;
	
	private Digest() throws NoSuchAlgorithmException {
		digest = MessageDigest.getInstance("SHA-256");
	}
	
	public static Digest getInstance() throws NoSuchAlgorithmException {
		if (INST == null) {
			System.out.println("Creating digest...");
			INST = new Digest();
		}
		return INST;
	}
	
	public String  digest(File file) throws IOException {
    	byte[] encodedhash = digest.digest(Files.readAllBytes(Paths.get(file.getPath())));
		return bytesToHex(encodedhash);
	}
	
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
