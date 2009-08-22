/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Aug 20, 2009
 * Time: 6:53:03 PM
 * To change this template use File | Settings | File Templates.
 */

import com.ifountain.comp.file.DirListener
import org.apache.commons.io.FileUtils
import remoteModification.RemoteApplicationModification

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 19, 2009
* Time: 2:15:01 PM
* To change this template use File | Settings | File Templates.
*/
class ModificationPopulator extends DirListener {
    File fromDir;
    Map exludedDirs;
    public ModificationPopulator(File fromDir, Map exludedDirs)
    {
        this.fromDir = fromDir;
        this.exludedDirs = exludedDirs;
        initialize([fromDir], exludedDirs);
    }

    public String getFileRelativePath(File file)
    {
        return file.getCanonicalPath().substring(fromDir.getCanonicalPath().length() + 1);
    }

    public void fileDeleted(File file)
    {
        def filePath = getFileRelativePath(file);
        RemoteApplicationModification modification = RemoteApplicationModification.getActiveModification(filePath);
        if(modification == null)
        {
            RemoteApplicationModification.add(filePath: filePath, completeFilePath:file.getCanonicalPath(), lastChangedAt:new Date(), operation: RemoteApplicationModification.DELETE);
        }
        else{
            modification.update(operation:RemoteApplicationModification.DELETE, lastChangedAt:new Date());
        }
    }
    
    public void fileChanged(File file)
    {
        try
        {
            def filePath = getFileRelativePath(file);
            RemoteApplicationModification modification = RemoteApplicationModification.getActiveModification(filePath);
            def propsToBeUpdated = [operation:RemoteApplicationModification.COPY, content:file.getText(), lastChangedAt:new Date(file.lastModified())];
            if(modification == null)
            {
                propsToBeUpdated.filePath = filePath;
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