package com.lfs.tada.photosorter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import org.junit.jupiter.api.Test;

import junit.framework.TestCase;

public class FilesTests extends TestCase {

	static Path tempDir;
	
	
	

	@Test
	public void testUsingTempFolder() throws IOException {

		Path tempDir = Files.createTempDirectory("files_test_dir");
		
		System.out.println("rtrttr"+tempDir.toString());
		
		
		Path fileCreate = tempDir.resolve("C-2019-");

		Path file = Files.createFile(fileCreate);
	
		System.out.println(file.getFileName());
		
		
//		Files.walk(tempDir)
//			.forEach(f -> {
//					
//					System.out.println(f.getFileName());
//					try {
//						Files.delete(f);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
////				}
//			});
		
		Files.delete(tempDir);
		
//		Path p = Files.createFile(tempDir);
	}

}
