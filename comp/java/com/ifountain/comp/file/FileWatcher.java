package com.ifountain.comp.file;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

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
    Map dirs = new HashMap();
    long lastChangedFile = 0;
    DirListener listener;
    public FileWatcher(File dirToWatch, DirListener listener) {
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
                for(int i=0; i < currentFiles.length; i++)
                {
                    File file = currentFiles[i];
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

                lastChangedFile = tmpLastModified;
                Thread.sleep ((int)(Math.random()*800));
            }
        }
        catch(InterruptedException ex)
        {

        }
    }
}