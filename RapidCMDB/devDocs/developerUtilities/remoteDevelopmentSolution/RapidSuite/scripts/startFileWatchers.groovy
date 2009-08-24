/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Aug 19, 2009
 * Time: 2:14:39 PM
 * To change this template use File | Settings | File Templates.
 */
application.Cache.retrieve("DIR_COPIERS").each{
    it.destroy();
}

//def dirMappings = [
//        [localDir: "", targetUploadDir: "", targetRsDir:"", excludeds: [".svn", ".cvs","CVS"]]
//]
def dirMappings = [:]
def dirCopiers = [];
application.Cache.store("DIR_COPIERS", dirCopiers);
dirMappings.each {
    def exludedDirs = [:];
    it.excludeds.each {excludedDir ->
        exludedDirs[excludedDir] = excludedDir
    }
    dirCopiers << new ModificationPopulator(new File(it.localDir), it.targetUploadDir, it.targetRsDir, exludedDirs);
}

return "Watchers started"
