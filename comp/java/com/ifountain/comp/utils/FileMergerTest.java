/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
 * noted in a separate copyright notice. All rights reserved.
 * This file is part of RapidCMDB.
 * 
 * RapidCMDB is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */
package com.ifountain.comp.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ifountain.comp.test.util.file.FileTestUtils;
import com.ifountain.comp.test.util.file.TestFile;


/**
 * @author yekmer
 */

//Be Carefullllll test html testes everything. so if you change it , most of the testes crashes.
//Don't change it. if you change you should change most of the testes.

public class FileMergerTest extends TestCase
{
	FileMerger fileMerger = new FileMerger();
	//String[] files = {"jsler/test.html"};
	protected void setUp() throws Exception
	{
		super.setUp();
		FileMerger.FILE_PATH = "index.html";
		FileMerger.APP_PATH = "RapidInsight/RapidInsight/clients/js/";
		FileMerger.TARGET_PATH = TestFile.TESTOUTPUT_DIR;
		FileMerger.ROOT_PATH = "RapidApplicationServer/RapidApplicationServer/webapps/root";
        FileMerger.willCombineCss = true;

    }
	
	public static void main(String[] args)
	{
		
	}
	
	public void testGetFileContent_asString() throws Exception {
		
		String fileName = "test.html";
		FileTestUtils.deleteFile(fileName);
		File file = new TestFile(fileName);
		StringBuffer wholeFileAsString = new StringBuffer();
		fileMerger.getFileContentAsString(file, wholeFileAsString);
		assertEquals("", wholeFileAsString.toString());
		String expectedString = "A <head>ABC";
		ArrayList lines = new ArrayList();
		lines.add(expectedString);
		FileTestUtils.generateFile(fileName, lines);
		fileMerger.getFileContentAsString(file, wholeFileAsString);
		assertEquals(expectedString + "\n", wholeFileAsString.toString());
		
	}
	
	
	public void testGetJSPathsAsList() {
		StringBuffer wholeHTML = new StringBuffer();
		wholeHTML.append("abc\n");
		wholeHTML.append("<script language=\"javascript\" src=\"/jslib/rapidjs/data/xml1json.js\" >AAAAAAAAA</ script>\n");
		wholeHTML.append("<script src=\"/jslib/rapidjs/data/xml2json.js\">AAAAAAAAA</wrongTag>\n");
		wholeHTML.append("<script src=\"rijslib/component/layout/FilterTool.js\">BBBBBBBBBBB</script>\n");
		wholeHTML.append("<script language=\"javascript\" src=\"/jslib/rapidjs/data/xml5json.js\" />\n");
		wholeHTML.append("xyz\n");
		List JSPaths = new ArrayList();
		fileMerger.setPathsAndRemoveFromWholeHTML(wholeHTML.toString() , FileMerger.JSPATTERN , JSPaths);
		//esitlemeyi unutma
		assertEquals("RapidApplicationServer/RapidApplicationServer/webapps/root/jslib/rapidjs/data/xml1json.js", JSPaths.get(0).toString());
		assertEquals(FileMerger.APP_PATH + "rijslib/component/layout/FilterTool.js", JSPaths.get(1).toString());
		assertEquals("RapidApplicationServer/RapidApplicationServer/webapps/root/jslib/rapidjs/data/xml5json.js", JSPaths.get(2).toString());	
		assertEquals(3, JSPaths.size());
		
	}
	public void testGetCssPaths()
	{
		StringBuffer wholeHTML = new StringBuffer();
		wholeHTML.append("abc\n");
		wholeHTML.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"ricomp/css/allCSS1.css\"/>");
		wholeHTML.append("<lin k rel=\"stylesheet\" type=\"text/css\" href=\"ricomp/css/allCSS2.css\"/>");
		wholeHTML.append("<link rel=\"stylesheet\" href=\"ricomp/css/allCSS3.css\" type=\"text/css\" />");
		wholeHTML.append("<link rel=\"stylesheet\" ref=\"ricomp/css/allCSS4.css\" type=\"text/css\" />");
		wholeHTML.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"ricomp/css/allCSS5.css\" ></ link>");
		wholeHTML.append("xyz\n");
		List CSSPaths = new ArrayList();
		fileMerger.setPathsAndRemoveFromWholeHTML(wholeHTML.toString() , FileMerger.CSSPATTERN , CSSPaths);
		assertEquals("RapidInsight/RapidInsight/clients/js/ricomp/css/allCSS1.css", CSSPaths.get(0).toString());
		assertEquals(FileMerger.APP_PATH + "ricomp/css/allCSS3.css", CSSPaths.get(1).toString());
		assertEquals("RapidInsight/RapidInsight/clients/js/ricomp/css/allCSS5.css", CSSPaths.get(2).toString());
		assertEquals(3, CSSPaths.size());		
	}
	public void testCreateFile() throws Exception
	{
		String content = "A <head>ABC\n";
		File file = new TestFile("test.html");
		FileTestUtils.deleteFile(file);
		fileMerger.createFile(content , file.getPath());
		StringBuffer actualFileContent = new StringBuffer();
		fileMerger.getFileContentAsString(new TestFile(file) , actualFileContent);
		assertEquals(content, actualFileContent.toString());
	}
	public void testwriteModifiedHTML() throws Exception
	{
		String htmlWithNoScripts = "A <head>ABC\n";
		File file = new TestFile("test.html");
		
		FileTestUtils.deleteFile(file);
		fileMerger.createFile(htmlWithNoScripts, file.getPath());
		fileMerger.writeModifiedHTML(htmlWithNoScripts , file);
		assertTrue(file.exists());		
		
		String expectedFileContent = "A <head>\n\t<script src=\"test.js\"></script>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"test.css\"/>\nABC\n";
		StringBuffer actualContent = new StringBuffer();
		fileMerger.getFileContentAsString(file, actualContent);
		assertEquals(expectedFileContent, actualContent.toString());
		
	}
	
	public void testwriteMergedCSS() throws Exception
	{
	    File file = new TestFile("test.html");
		FileTestUtils.deleteFile(file);
		List cssPaths = new ArrayList();
		cssPaths.add("RapidApplicationServer/RapidApplicationServer/webapps/root/jslib/css/treegrid.css");
		String htmlWithNoScripts = "A <head>ABC\n";
		fileMerger.createFile(htmlWithNoScripts, file.getPath());
		fileMerger.writeModifiedHTML(htmlWithNoScripts , file);
		assertTrue(file.exists());
		String expectedFileContent = "A <head>\n\t<script src=\"test.js\"></script>\n	<link rel=\"stylesheet\" type=\"text/css\" href=\"test.css\"/>\nABC\n";
		StringBuffer actualContent = new StringBuffer();
		fileMerger.getFileContentAsString(file, actualContent);
		assertEquals(expectedFileContent, actualContent.toString());
		
	}	
	
	public void testwriteMergedJS() throws Exception
	{
	    File file = new TestFile("test.html");
		FileTestUtils.deleteFile(file);
		List jsPaths = new ArrayList();
		jsPaths.add("RapidApplicationServer/RapidApplicationServer/webapps/root/jslib/yui/build/utilities/utilities.js");
		String htmlWithNoScripts = "A <head>ABC\n";
		fileMerger.createFile(htmlWithNoScripts, file.getPath());
		fileMerger.writeModifiedHTML(htmlWithNoScripts , file);
		assertTrue(file.exists());
		String expectedFileContent ="A <head>\n\t<script src=\"test.js\"></script>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"test.css\"/>\nABC\n";
		StringBuffer actualContent = new StringBuffer();
		fileMerger.getFileContentAsString(file, actualContent);
		assertEquals(expectedFileContent, actualContent.toString());
		
	}	
}
