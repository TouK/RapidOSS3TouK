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
abstract class DirListener {
    ThreadGroup allThreads = new ThreadGroup("DirListener")
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