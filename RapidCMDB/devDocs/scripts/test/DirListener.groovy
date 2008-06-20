package test

import org.apache.commons.io.FileUtils

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 19, 2008
 * Time: 6:09:46 PM
 * To change this template use File | Settings | File Templates.
 */
class DirListener {
    ThreadGroup allThreads = new ThreadGroup("DirListener")
    File fromDir;
    File toDir;
    Map exludedDirs;
    public DirListener(File fromDir, File toDir, Map exludedDirs)
    {
        this.fromDir = fromDir;
        this.toDir = toDir;
        this.exludedDirs = exludedDirs;
        createNewWatcherThread(fromDir);
    }
    public void createNewWatcherThread(File dir)
    {
        if(exludedDirs.containsKey(dir.name)) return;
        def watcher = new FileWatcher(dir, this);
        def t = new Thread(allThreads, watcher);
        t.start();
    }

    public File getDestFile(File file)
    {
        return new File(toDir.getAbsolutePath()+"/"+file.getAbsolutePath().substring(fromDir.getAbsolutePath().length()+1));
    }

    public void copyFile(File file)
    {
        def destFile = getDestFile(file);
        if(destFile.exists() && destFile.lastModified() == file.lastModified()) return;
        println "COPYING ${file}"
        destFile.getParentFile().mkdirs();
        FileUtils.copyFile (file, destFile);
    }

    public void deleteFile(File file)
    {
        def destFile = getDestFile(file);
        destFile.delete();
    }

    public void destroy()
    {
        allThreads.interrupt();
    }

    public static void main(String[]args)
    {

//        def excludedDirs = [".svn":".svn",
//                "reports":"reports"]
//        test.DirListener listener1 = new test.DirListener(new File("c:/temp/RapidServer/RapidCMDB"), new File("c:/temp2/RapidServer/RapidCMDB"), excludedDirs);
//        test.DirListener listener2 = new test.DirListener(new File("c:/temp/RapidServer/RcmdbCommons"), new File("c:/temp2/RapidServer/RapidCMDB"), excludedDirs);
//        Thread.sleep (120000);
    }
}