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

    public void fileDeleted(File file)
    {
        
    }
    public void fileChanged(File file)
    {
        try
        {
            def destFile = getDestFile(file);
            if(destFile.exists() && destFile.lastModified() == file.lastModified()) return;
            println "COPYING ${file}"
            destFile.getParentFile().mkdirs();
            FileUtils.copyFile (file, destFile);
        }catch(Throwable t)
        {
            t.printStackTrace();            
        }
    }
}