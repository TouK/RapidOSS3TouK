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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class FileUtils {
    private FileUtils() 
    {
    }

    public static Properties getPropertiesFromFile(String baseDirectory, String fileName) throws IOException {
        if(baseDirectory == null)
        {
            throw new NullPointerException("baseDirectory cannot be null");
        }
        if(fileName == null)
        {
            throw new NullPointerException("fileName cannot be null");
        }
        if(baseDirectory.length() != 0){
            if(baseDirectory.charAt(baseDirectory.length() - 1) != '/')
            {
                baseDirectory += "/";
            }
        }
        File file = new File(baseDirectory + fileName);
        if(!file.exists())
        {
            throw new FileNotFoundException("Specified file <" + fileName + "> not found in directory <" + baseDirectory + ">");
        }
        
        FileInputStream in = new FileInputStream(file);
        
        Properties props = new Properties();
        props.load(in);
        in.close();
        
        return props;
    }
    
    public static String readFile(String filePath) throws IOException
    {
    	BufferedReader br = null;
    	try
    	{
    		br = new BufferedReader(new FileReader(filePath));
    		String s = null;
    		StringBuffer content = new StringBuffer();
    		while((s = br.readLine()) != null)
    		{
    			content.append(s);
    		}
    		return content.toString();
    	}
    	finally
    	{
    		if(br != null)
    		{
    			br.close();
    		}
    	}
    }
    public static List<String> readFileToList(String filePath) throws IOException
    {
        List<String> lines = new ArrayList<String>();
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(filePath));
            String s = null;
            while((s = br.readLine()) != null)
            {
                lines.add(s);
            }
            return lines;
        }
        finally
        {
            if(br != null)
            {
                br.close();
            }
        }
    }
    
	
	public static void storeObject(String filename, Object o) throws IOException{
        File file = new File(filename);
        if(file.getParentFile() != null){
            file.getParentFile().mkdirs();
        }
		FileOutputStream fileOut = new FileOutputStream(file);
		ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
		objOut.writeObject(o);
	}
	
	public static Object restoreObject(String filename){
		if(!new File(filename).exists())
		{
			return null;
		}
		Object o = new Object();
		FileInputStream fileIn = null;
		try {
			fileIn = new FileInputStream(filename);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			o = objIn.readObject();
		} catch(Throwable t){
			System.out.println("Error occurred while restoring object from file " + filename);
		}
		finally
		{
			if(fileIn != null)
			{
				try {
					fileIn.close();
				} catch (IOException e) {
				}
			}
		}
		return o;
	}

    public static void copyFile(File in, File out) throws IOException
    {
    	if(out.getParentFile() != null)
    	out.getParentFile().mkdirs();
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1)
        {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }

    public static void copyFile(String in, String out) throws IOException
    {
    	copyFile(new File(in), new File(out));
    }

	public static void deleteFile(File file) {
		if (file.isDirectory())
        {
            File[] files = file.listFiles();
            if (files != null)
            {
                for (int i = 0; i < files.length; i++)
                {
                	deleteFile(files[i]);
                }
            }
        }
        file.delete();
	}

	public static void unzip(String directory, String filename) throws IOException
    {
        
        byte[] buf = new byte[1024];
        ZipInputStream zipinputstream = null;
        ZipEntry zipentry;
        zipinputstream = new ZipInputStream(
            new FileInputStream(filename));

        zipentry = zipinputstream.getNextEntry();
        while (zipentry != null) 
        { 
            //for each entry to be extracted
            String entryName = zipentry.getName();
            File fileentry = new File(directory + "/" +entryName);
            if (fileentry.isDirectory())
			{
            	fileentry.mkdir();
            	zipentry = zipinputstream.getNextEntry();
				continue;
			}
            int n;
            FileOutputStream fileoutputstream;
            File newFile = new File(directory + "/" +  entryName);
            
            newFile.getParentFile().mkdirs();
            
            fileoutputstream = new FileOutputStream(
            		directory + "/" + entryName);             

            while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
                fileoutputstream.write(buf, 0, n);

            fileoutputstream.close(); 
            zipinputstream.closeEntry();
            zipentry = zipinputstream.getNextEntry();

        }//while

        zipinputstream.close();
       
    }
	public static void zip(List dirList, String outputZipFile) throws IOException 
   { 
       File zipFile = new File(outputZipFile);
       if(zipFile.getParentFile() != null)
       {
    	   zipFile.getParentFile().mkdirs();
       }
       ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
       for (Iterator iter = dirList.iterator(); iter.hasNext();) {
    	   zip((File)iter.next(), out);
       }
       out.close();
   }
	private static void zip(File zipDir, ZipOutputStream zos) throws IOException 
	{ 
		if(!zipDir.exists())return;
		File[] dirList = zipDir.listFiles(); 
		if(dirList != null && dirList.length == 0)
		{
			ZipEntry anEntry = new ZipEntry(zipDir.getPath().replaceAll("\\\\", "/")+"/");
			zos.putNextEntry(anEntry);
			return;
		}
		byte[] readBuffer = new byte[2156]; 
		int bytesIn = 0; 
		for(int i=0; i<dirList.length; i++) 
		{ 
			File f = dirList[i]; 
			if(f.isDirectory()) 
			{ 
				zip(f, zos); 
				continue; 
			} 
			FileInputStream fis = new FileInputStream(f); 
			ZipEntry anEntry = new ZipEntry(f.getPath().replaceAll("\\\\", "/")); 
			zos.putNextEntry(anEntry); 
			while((bytesIn = fis.read(readBuffer)) != -1) 
			{ 
				zos.write(readBuffer, 0, bytesIn); 
			} 
			fis.close(); 
		} 
	}
}
