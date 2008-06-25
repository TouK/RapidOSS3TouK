package test
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 19, 2008
 * Time: 6:09:00 PM
 * To change this template use File | Settings | File Templates.
 */
class FileWatcher implements Runnable{
    File dirToWatch;
    Map files = new HashMap();
    Map dirs = new HashMap();
    long lastChangedFile = 0;
    DirListener listener;
    public FileWatcher(File dirToWatch, DirListener listener) {
//        println "watching dir ${dirToWatch}";
        this.dirToWatch = dirToWatch;
        this.listener = listener;
    }


    public void run()
    {
        while(true)
        {
            if(!dirToWatch.exists()) return;
            def currentFiles = dirToWatch.listFiles();
            def filesToBeCopied = [];
            long tmpLastModified = lastChangedFile;
            currentFiles.each { File file->
                def lastMod = file.lastModified();
                if(lastMod > lastChangedFile)
                {
                    if(lastMod > tmpLastModified) tmpLastModified = lastMod;
                    if(!file.isDirectory())
                    {
                        filesToBeCopied+=file;
                    }
                    else
                    {
                        listener.createNewWatcherThread (file);
                    }
                }
            }

            filesToBeCopied.each{
                for(int i=0; i < 20; i++)
                {
                    try
                    {
                        listener.copyFile (it);
                        break;
                    }
                    catch(Throwable e)
                    {
                        e.printStackTrace();
                        Thread.sleep (10);
                    }
                }
                if(i == 50)
                {
                    System.err.println("Could not write ${it}");
                }
            }
            lastChangedFile = tmpLastModified;
            Thread.sleep ((int)(Math.random()*800));
        }
    }
}