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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Properties;

import com.ifountain.comp.test.util.RCompTestCase;
import com.ifountain.comp.test.util.file.FileTestUtils;
import com.ifountain.comp.test.util.file.TestFile;



public class FileUtilsTest extends RCompTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetPropertiesFromFileThrowsExceptionIfBaseDirectoyIsNull() throws Exception {
        try {
            FileUtils.getPropertiesFromFile(null, "out.txt");
            fail("Should thro exception beacuse basedirectory is null");
        } catch (NullPointerException e) {
        }
        catch (Exception e) {
            fail("Should throw NullPointerException beacuse BaseDirectory is null");
        }
    }
    
    public void testGetPropertiesFromFileThrowsExceptionIfFileNameIsNull() throws Exception {
        try {
            FileUtils.getPropertiesFromFile(FileTestUtils.TEST_OUTPUT_DIR, null);
            fail("Should throw exception beacuse filename is null");
        } catch (NullPointerException e) {
        }
        catch (Exception e) {
            fail("Should throw NullPointerException beacuse filename is null");
        }
    }
    
    public void testGetPropertiesFromFileThrowsExceptionIfPropertiesFileNotFound() throws Exception {
        String baseDirectory = FileTestUtils.TEST_OUTPUT_DIR;
        String fileName = "out.txt";
        File file = new TestFile(baseDirectory + fileName);
        if(file.exists())
        {
            file.delete();
        }
        
        try {
            FileUtils.getPropertiesFromFile(baseDirectory, fileName);
            fail("Should thro exception beacuse file doesnot exists");
        } catch (FileNotFoundException e) {
        }
        catch (Exception e) {
            fail("Should thro FileNotFoundException beacuse file doesnot exists");
        }
    }
    
    public void testGetPropertiesFromFileLoadPropertiesCorrectly() throws Exception {
        String baseDirectory = FileTestUtils.TEST_OUTPUT_DIR;
        String fileName = "out.txt";
        
        File file = new TestFile(baseDirectory + fileName);
        file.getParentFile().mkdirs();
        
        Properties expectedProps = new Properties();
        expectedProps.put("Prop1", "valueofprop1");
        expectedProps.put("Prop2", "valueofprop2");
        
        FileOutputStream out = new FileOutputStream(file);
        expectedProps.store(out, "");
        out.close();
        
        Properties  returnedProperties = FileUtils.getPropertiesFromFile(baseDirectory, fileName);
        
        assertEquals(expectedProps.size(), returnedProperties.size());
        assertEquals(expectedProps.getProperty("Prop1"), returnedProperties.getProperty("Prop1"));
        assertEquals(expectedProps.getProperty("Prop2"), returnedProperties.getProperty("Prop2"));
    }
    
    public void testGetPropertiesFromFileLoadPropertiesCorrectlyIfSlashNotSpecifiedAtTheEnd() throws Exception {
        String baseDirectory = FileTestUtils.TEST_OUTPUT_DIR.trim().substring(0,FileTestUtils.TEST_OUTPUT_DIR.length()-1);
        String fileName = "out.txt";
        
        File file = new TestFile(baseDirectory +"/" + fileName);
        file.getParentFile().mkdirs();
        
        Properties expectedProps = new Properties();
        expectedProps.put("Prop1", "valueofprop1");
        expectedProps.put("Prop2", "valueofprop2");
        
        FileOutputStream out = new FileOutputStream(file);
        expectedProps.store(out, "");
        out.close();
        
        Properties  returnedProperties = FileUtils.getPropertiesFromFile(baseDirectory, fileName);
        
        assertEquals(expectedProps.size(), returnedProperties.size());
        assertEquals(expectedProps.getProperty("Prop1"), returnedProperties.getProperty("Prop1"));
        assertEquals(expectedProps.getProperty("Prop2"), returnedProperties.getProperty("Prop2"));
    }
    
    public void testGetPropertiesFromFileClosesInputStream() throws Exception {
        String baseDirectory = FileTestUtils.TEST_OUTPUT_DIR;
        String fileName = "out.txt";
        
        File file = new TestFile(baseDirectory + fileName);
        file.getParentFile().mkdirs();
        
        Properties expectedProps = new Properties();
        expectedProps.put("Prop1", "valueofprop1");
        
        FileOutputStream out = new FileOutputStream(file);
        expectedProps.store(out, "");
        out.close();
        
        FileUtils.getPropertiesFromFile(baseDirectory, fileName);
        
        assertTrue(file.delete());
    }
    
    public void testZipFiles() throws Exception {
		File dir1 = new TestFile("dir1/subdir1");
		File dir2 = new TestFile("dir2/subdir2");
		FileTestUtils.deleteFile(dir1);
		FileTestUtils.deleteFile(dir2);
		dir1.mkdirs();
		dir2.mkdirs();
		
		File f1 = new TestFile("dir1/subdir1/file1.txt");
		File f2 = new TestFile("dir2/file1.txt");
		File f3 = new TestFile("dir1/file1.txt");
		FileTestUtils.generateFile(f1.getPath(), "");
		FileTestUtils.generateFile(f2.getPath(), "");
		FileTestUtils.generateFile(f3.getPath(), "");
		
		String zipFileName = FileTestUtils.TEST_OUTPUT_DIR+"backups/zip1.zip";
		FileTestUtils.deleteFile(new TestFile(zipFileName).getParent());
		FileUtils.zip(Arrays.asList(new File[]{dir1.getParentFile(), dir2.getParentFile()}), zipFileName);
		FileTestUtils.deleteFile(dir1.getParent());
		FileTestUtils.deleteFile(dir2.getParent());
		assertTrue(new TestFile(zipFileName).exists());
		
		String extDir = FileTestUtils.TEST_OUTPUT_DIR+"extractDir";
		FileTestUtils.deleteFile(extDir);
		FileUtils.unzip(extDir, zipFileName);
		assertTrue(new TestFile(extDir+"/"+f1.getPath()).exists());
		assertTrue(new TestFile(extDir+"/"+f2.getPath()).exists());
		assertTrue(new TestFile(extDir+"/"+f3.getPath()).exists());
		assertTrue(new TestFile(extDir+"/"+dir2.getPath()).exists());
	}
    
}
