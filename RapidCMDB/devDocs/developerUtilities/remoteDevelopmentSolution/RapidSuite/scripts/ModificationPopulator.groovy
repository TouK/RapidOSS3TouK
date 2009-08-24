import com.ifountain.comp.file.DirListener
import remoteModification.RemoteApplicationModification

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 20, 2009
* Time: 6:53:03 PM
* To change this template use File | Settings | File Templates.
*/

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 19, 2009
* Time: 2:15:01 PM
* To change this template use File | Settings | File Templates.
*/
class ModificationPopulator extends DirListener {
    File fromDir;
    String rsDirectory;
    String targetUploadDir;
    Map exludedDirs;
    public ModificationPopulator(File fromDir, String targetUploadDir, String rsDirectory, Map exludedDirs)
    {
        this.fromDir = fromDir;
        this.rsDirectory = rsDirectory?rsDirectory:".";
        this.targetUploadDir = targetUploadDir?targetUploadDir:".";
        this.exludedDirs = exludedDirs;
        initialize([fromDir], exludedDirs);
    }

    public String getRelativeFilePath(File file)
    {
        return file.getCanonicalPath().substring(fromDir.getCanonicalPath().length() + 1).replaceAll("\\\\", "/");
    }

    public String getRsFileLocation(String relativeFilePath)
    {
        return "${rsDirectory}/${relativeFilePath}" .replaceAll("\\\\", "/")
    }

    public String getUploadFileLocation(String relativeFilePath)
    {
        return "${targetUploadDir}/${relativeFilePath}".replaceAll("\\\\", "/")        
    }

    public void fileDeleted(File file)
    {
        def relativeFilePath = getRelativeFilePath(file);
        RemoteApplicationModification modification = RemoteApplicationModification.getActiveModification(relativeFilePath);
        if(modification == null)
        {
            RemoteApplicationModification.add(relativeFilePath: relativeFilePath, targetRsFilePath:getRsFileLocation(relativeFilePath), completeFilePath:file.getCanonicalPath(), lastChangedAt:new Date(), operation: RemoteApplicationModification.DELETE);
        }
        else{
            modification.update(operation:RemoteApplicationModification.DELETE, lastChangedAt:new Date(), targetRsFilePath:getRsFileLocation(relativeFilePath));
        }
    }
    
    public void fileChanged(File file)
    {
        try
        {
            def relativeFilePath = getRelativeFilePath(file);
            RemoteApplicationModification modification = RemoteApplicationModification.getActiveModification(relativeFilePath);
            def propsToBeUpdated = [operation:RemoteApplicationModification.COPY, content:file.getText(), targetUploadFilePath:getUploadFileLocation(relativeFilePath), targetRsFilePath:getRsFileLocation(relativeFilePath), lastChangedAt:new Date(file.lastModified())];
            if(modification == null)
            {
                propsToBeUpdated.relativeFilePath = relativeFilePath;
                propsToBeUpdated.completeFilePath = file.getCanonicalPath();
                modification = RemoteApplicationModification.add(propsToBeUpdated);
            }
            else{
                modification.update(propsToBeUpdated);
            }
        } catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}
