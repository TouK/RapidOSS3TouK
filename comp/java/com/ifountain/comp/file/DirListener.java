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

import java.util.Map;
import java.util.List;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 26, 2008
 * Time: 9:47:32 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DirListener {
    ThreadGroup allThreads = new ThreadGroup("DirListener");
    Map exludedDirs;
    public DirListener()
    {
    }

    public void initialize(List<File> dirsToWatch, Map exludedDirs)
    {
        this.exludedDirs = exludedDirs;
        for(int i=0; i < dirsToWatch.size(); i++)
        {
            File dir = dirsToWatch.get(i);
            createNewWatcherThread(dir);
        }

    }
    public void createNewWatcherThread(File dir)
    {
        if(exludedDirs.containsKey(dir.getName())) return;
        FileWatcher watcher = new FileWatcher(dir, this);
        Thread t = new Thread(allThreads, watcher);
        t.start();
    }
    abstract public void fileChanged(File file);


    public void destroy()
    {
        allThreads.interrupt();
    }

}