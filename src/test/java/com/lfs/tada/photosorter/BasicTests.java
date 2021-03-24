package com.lfs.tada.photosorter;

import junit.framework.TestCase;

import com.lfs.tada.photosorter.App;

public class BasicTests extends TestCase {

	Restruct restruct = new Restruct();
	
	public void testXX() {
		
		
		assertTrue("Hejsan", true);
		

		assertTrueDataFormat("C-2019-05-26");
		assertTrueDataFormat("D-2016-10-12");
		
		assertFalseDataFormat("D-2016-20-12");
//		assertFalseDataFormat("D-2016-13-12");
		
		assertFalseDataFormat("A-2016-10-12");
		assertFalseDataFormat("X-2019-04-26");
		assertFalseDataFormat("C-A016-10-12");
		assertFalseDataFormat("C-016-10-12");
		assertFalseDataFormat("C-1016-A0-12");
		assertFalseDataFormat("C-1016-1-12");
		assertFalseDataFormat("C-1016-01-2");

		assertFalseDataFormat(" C-2016-10-12");
		assertFalseDataFormat("C-2016-10-12_");

	}
	
	private void assertTrueDataFormat(String date) {
		assertTrue(date, Util.isSourceDir(date));
	}
	
	private void assertFalseDataFormat(String date) {
		assertFalse(date, Util.isSourceDir(date));
	}
	
	
}
