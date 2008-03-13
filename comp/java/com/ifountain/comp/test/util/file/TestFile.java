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
/*
 * Created on Jan 21, 2008
 *
 */
package com.ifountain.comp.test.util.file;

import java.io.File;

public class TestFile extends File
{

    public static final String TESTOUTPUT_DIR = "output/testing";
    
    public TestFile()
    {
        super(getFilePath(""));
    }
    public TestFile(String pathname)
    {
        super(getFilePath(pathname));
    }
    public TestFile(File targetLocation)
    {
        this(targetLocation.getPath());
    }
    private static String getFilePath(String fileName)
    {
        if(new File(fileName).isAbsolute())
        {
            return fileName;
        }
        
        if(!fileName.toLowerCase().replaceAll("\\\\", "/").startsWith(TESTOUTPUT_DIR.toLowerCase().replaceAll("\\\\", "/")))
        {
            fileName = TESTOUTPUT_DIR+"/"+fileName;
        }
        return fileName;
    }

}
