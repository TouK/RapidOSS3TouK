/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package com.ifountain.comp.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 26, 2008
 * Time: 9:51:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileWatcher implements Runnable{
    File dirToWatch;
    Map files = new HashMap();
    long lastChangedFile = 0;
    DirListener listener;
    Map exludedFiles;
    public FileWatcher(File dirToWatch, DirListener listener, Map exludedFiles) {
        this.exludedFiles = exludedFiles;
        this.dirToWatch = dirToWatch;
        this.listener = listener;
    }


    public void run()
    {
        try
        {
            while(true)
            {
                if(!dirToWatch.exists()) return;
                File[] currentFiles = dirToWatch.listFiles();
                long tmpLastModified = lastChangedFile;
                Map oldFiles = new HashMap(files);
                files.clear();
                for(int i=0; i < currentFiles.length; i++)
                {

                    File file = currentFiles[i];
                    files.put(file.getName(), file);
                    oldFiles.remove(file.getName());
                    try
                    {
                        if(!exludedFiles.containsKey(file.getCanonicalPath()) && !exludedFiles.containsKey(file.getName()))
                        {
                            long lastMod = file.lastModified();
                            if(lastMod > lastChangedFile)
                            {
                                if(lastMod > tmpLastModified) tmpLastModified = lastMod;
                                if(!file.isDirectory())
                                {
                                    listener.fileChanged (file);
                                }
                                else
                                {
                                    listener.createNewWatcherThread (file);
                                }
                            }
                        }
                    }catch(IOException e)
                    {
                    }
                }
                Collection entries = oldFiles.values();
                for(Iterator it = entries.iterator(); it.hasNext();)
                {
                    listener.fileDeleted((File)it.next());
                }

                lastChangedFile = tmpLastModified;
                Thread.sleep ((int)(Math.random()*800));
            }
        }
        catch(InterruptedException ex)
        {

        }
    }
}