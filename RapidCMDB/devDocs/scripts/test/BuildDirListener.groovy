package test

import org.apache.commons.io.FileUtils
import com.ifountain.comp.file.DirListener

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 19, 2008
 * Time: 6:09:46 PM
 * To change this template use File | Settings | File Templates.
 */
class BuildDirListener extends DirListener{
    ThreadGroup allThreads = new ThreadGroup("DirListener")
    File fromDir;
    File toDir;
    Map exludedDirs;
    public BuildDirListener(File fromDir, File toDir, Map exludedDirs)
    {
        this.fromDir = fromDir;
        this.toDir = toDir;
        this.exludedDirs = exludedDirs;
        initialize([fromDir], exludedDirs);
    }

    public File getDestFile(File file)
    {
        return new File(toDir.getAbsolutePath()+"/"+file.getAbsolutePath().substring(fromDir.getAbsolutePath().length()+1));
    }

    public void fileChanged(File file)
    {
        def destFile = getDestFile(file);
        if(destFile.exists() && destFile.lastModified() == file.lastModified()) return;
        println "COPYING ${file}"
        destFile.getParentFile().mkdirs();
        FileUtils.copyFile (file, destFile);
    }
}