import com.ifountain.comp.file.DirListener
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 19, 2009
* Time: 2:15:01 PM
* To change this template use File | Settings | File Templates.
*/
class DirCopier extends DirListener{
    File fromDir;
    File toDir;
    Map exludedDirs;
    public DirCopier(File fromDir, File toDir, Map exludedDirs)
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

    public void fileDeleted(File file)
    {
        def destFile = getDestFile(file);
        if(destFile.exists())
        {
            println "DELETING ${destFile}"
            if(destFile.isDirectory())
            {
                FileUtils.deleteDirectory (destFile)
            }
            else
            {
                destFile.delete();
            }
        }
    }
    public void fileChanged(File file)
    {
        try
        {
            def destFile = getDestFile(file);
            if(destFile.exists() && destFile.lastModified() == file.lastModified()) return;
            println "COPYING ${file} to ${destFile}"
            destFile.getParentFile().mkdirs();
            FileUtils.copyFile (file, destFile);
        }catch(Throwable t)
        {
            t.printStackTrace();
        }
    }
}