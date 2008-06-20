package com.ifountain.testing;

import org.apache.commons.io.FileUtils

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 19, 2008
 * Time: 6:09:46 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class DirListener {
    ThreadGroup allThreads = new ThreadGroup("DirListener")
    Map exludedDirs;
    public DirListener()
    {
    }

    def initialize(List dirsToWatch, Map exludedDirs)
    {
        this.exludedDirs = exludedDirs;
        dirsToWatch.each{
            createNewWatcherThread(it);    
        }

    }
    public void createNewWatcherThread(File dir)
    {
        if(exludedDirs.containsKey(dir.name)) return;
        def watcher = new FileWatcher(dir, this);
        def t = new Thread(allThreads, watcher);
        t.start();
    }
    abstract public void fileChanged(File file);


    public void destroy()
    {
        allThreads.interrupt();
    }

}