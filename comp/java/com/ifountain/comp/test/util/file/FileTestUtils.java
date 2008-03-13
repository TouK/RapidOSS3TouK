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
/**
 * Created on Aug 13, 2006
 *
 * Author Sezgin kucukkaraaslan
 */
package com.ifountain.comp.test.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;



public class FileTestUtils {
	
    public static final String TEST_OUTPUT_DIR = "output/testing/";
    public static Properties testProperties;

    
    public static void initializeFromFile(String fileName){
        testProperties = new Properties();
        FileInputStream stream = null;
        try {
        	
        	stream = new FileInputStream(fileName);
            testProperties.load(stream);
            
        } catch (IOException e) {
        	
            e.printStackTrace();
            
        } finally {
        	try {
				if (stream != null ) stream.close();
			} catch (IOException e1) {

			}
        }
    }	

    public static void generateFile(String fileName, List lines) throws IOException {
        PrintWriter printWriter = null;
        File file = new TestFile(fileName);
        if(file.getParentFile() != null)
        {
            file.getParentFile().mkdirs();
        }
        FileWriter fw = new FileWriter(file);
        try {
            printWriter = new PrintWriter(fw);
            for (int i = 0; i < lines.size(); i++) {
                printWriter.println((String) lines.get(i));
            }
        } finally {
            if(printWriter != null)
                printWriter.close();
            if(fw != null)
                fw.close();
        }
    }

    
    public static String getText(String fileName) throws IOException
    {
    	StringBuffer text = new StringBuffer();
        FileReader fileReader = null;
        BufferedReader reader = null;
        try{
            fileReader = new FileReader(new TestFile(fileName));
            reader = new BufferedReader(fileReader);
            String line = null;
            while((line = reader.readLine()) != null){
                text.append(line);
            }
        }
        finally{
            if(reader != null ) reader.close();
            if(fileReader != null ) fileReader.close();
        }
        return text.toString();
    }
    
    public static ArrayList readFile(String fileName) throws IOException{
        ArrayList lines = new ArrayList();
        FileReader fileReader = null;
        BufferedReader reader = null;
        try{
            fileReader = new FileReader(new TestFile(fileName));
            reader = new BufferedReader(fileReader);
            String line = null;
            while((line = reader.readLine()) != null){
                lines.add(line);
            }
        }
        finally{
            if(reader != null ) reader.close();
            if(fileReader != null ) fileReader.close();
        }
        return lines;
    }
    
    
    public static void deleteFile(File file,int maxIterationCount) throws Exception
    {
        if(!(file instanceof TestFile))
        {
            file = new TestFile(file.getPath());
        }
        int iterationCount = 0;
        if(file != null)
        {
            while(file.exists() && iterationCount < maxIterationCount)
            {
                _deleteFile(file);
                Thread.sleep(50);
                iterationCount++;
            }
            if(iterationCount == maxIterationCount)
            {
                throw new Exception("Could not delete file : <" + file.getName() + ">. Opened streams may not realesed file.");
            }
        }
    }
    
    public static void deleteFile(String fileName, int maxIterationCount) throws Exception{
        deleteFile(new TestFile(fileName), maxIterationCount);
    }
    public static void deleteFile(String fileName) throws Exception{
        deleteFile(fileName, 20);
    }
    
    public static void deleteFile(File file) throws Exception
    {
        deleteFile(file, 20);
    }
    private static void _deleteFile(File file) throws Exception
    {
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            if (files != null)
            {
                for (int i = 0; i < files.length; i++)
                {
                    _deleteFile(files[i]);
                }
            }
        }
        file.delete();
    }
    
    public static void appendToFile(String fileName , List lines) throws Exception
    {
        FileWriter fw = new FileWriter(new TestFile(fileName), true);
        PrintWriter pw = new PrintWriter(fw);
        for (int i = 0 ; i < lines.size() ; i++)
        {
            pw.println((String)lines.get(i));
        }
        pw.close();
    }
    
    public static void appendToFile(String fileName, String line) throws Exception
    {
        ArrayList lines = new ArrayList();
        lines.add(line);
        appendToFile(fileName, lines);
    }
    
    public static void copyFile(File sourceLocation, File targetLocation) throws Exception{
        if(targetLocation instanceof TestFile)
        {
            targetLocation = new TestFile(targetLocation);
        }
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdirs();
            }
            
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
            	if(!children[i].equals("CVS"))
            	{
            		copyFile(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
            	}
            }
        } else {
            
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } 
    }

	public static void generateFile(String filePath, String text) throws IOException
	{
		ArrayList lines = new ArrayList();
		lines.add(text);
		generateFile(filePath, lines);
	}

    public static void deleteFile(String filePath, String extension) throws Exception
    {
        Collection files = FileUtils.listFiles(new TestFile(filePath), new String[]{extension}, true);
        for (Iterator iterator = files.iterator(); iterator.hasNext();)
        {
            File file = (File) iterator.next();
            int trialCount = 0;
            int maxNumberOftrials = 20;
            while(trialCount < maxNumberOftrials && file.exists())
            {
                file.delete();
                trialCount++;
            }
            if(trialCount == maxNumberOftrials)
            {
                throw new Exception("Could not delete file <" + file.getPath() + ">");
            }
        }
    }
}
