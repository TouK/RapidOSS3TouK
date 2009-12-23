package scriptTests

import com.ifountain.rcmdb.test.util.RapidCmdbScriptTestCase
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.test.util.CompassForTests
import application.RsApplication
import application.RsApplicationOperations
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Nov 11, 2009
* Time: 4:04:05 PM
* To change this template use File | Settings | File Templates.
*/
class MarkModificationsTest extends RapidCmdbScriptTestCase{
    def scriptName = "markModifications"
    String baseDir
    File baseDirFile
    File versionControlDirectory;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        gcl.addClasspath (new File(getWorkspaceDirectory(), "RapidModules/RapidInsight/operations").path);
        gcl.loadClass("VersionControlUtility")
        initializeScriptManager ("/RapidModules/RapidInsight/scripts");
        addScript (scriptName);
        baseDir = "../testOutput/RapidSuite"
        System.setProperty ("base.dir", baseDir)
        baseDirFile = new File(baseDir)
        File sourceDir = baseDirFile.parentFile;
        versionControlDirectory = new File(sourceDir, "versionControl");
        FileUtils.deleteDirectory (sourceDir);
        baseDirFile.mkdirs();
        RsApplicationTestUtils.initializeRsApplicationOperations(RsApplication);

    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testMarkModificationsCreateChangeSetDirectoryIfThereAreModifications()
    {
        def filesToBeTracked = [];
        filesToBeTracked << new File(baseDir, "file1.txt")
        filesToBeTracked.last().setText("")
        filesToBeTracked << new File(baseDir, "trialDir/subFile1.txt")
        filesToBeTracked.last().parentFile.mkdirs();
        filesToBeTracked.last().setText("")
        filesToBeTracked << new File(baseDir, "trialDir/subFile2.txt")
        filesToBeTracked.last().setText("")

        def filesToBeTrackedPath = filesToBeTracked.canonicalPath*.substring(baseDirFile.canonicalPath.length()+1)*.replaceAll("\\\\", "/")

        //we created base version
        def script = loadScript(scriptName, [:]);
        def result = script.run();

        def directoriesAfterMarkingBaseVersion = versionControlDirectory.listFiles()
        filesToBeTracked[0].delete();
        filesToBeTracked[1].setText("")


        script = loadScript(scriptName, [:]);
        result = script.run();

        def directoriesAfterRunningScriptAgain = versionControlDirectory.listFiles()
        assertEquals (directoriesAfterMarkingBaseVersion.size()+1, directoriesAfterRunningScriptAgain.size())

        def changesXml = new XmlParser().parseText(result);
        def changesAttributes = changesXml.attributes()
        def changeSetDir = directoriesAfterRunningScriptAgain.find {it.name == changesAttributes.name}

        def changes = changesXml.Change*.attributes();
        assertEquals (2, changes.size())
        def change = changes.find{it.path == "RapidSuite/${filesToBeTrackedPath[0]}" && it.operation == "Delete"}
        assertNotNull (change)
        change = changes.find{it.path == "RapidSuite/${filesToBeTrackedPath[1]}" && it.operation == "Change" && it.modifiedAt == ""+filesToBeTracked[1].lastModified()}
        assertNotNull (change)
        def fileToBeCopied = new File(changeSetDir, change.path);
        assertEquals (filesToBeTracked[1].getText(), fileToBeCopied.getText())

        def allFiles = new File(changeSetDir, "allFiles.xml");
        def allFileList = [filesToBeTracked[1], filesToBeTracked[2]]
        assertAllFiles(allFiles, allFileList);

        def changeInfoFile = new File(changeSetDir, "changes.xml");
        assertEquals (result, changeInfoFile.getText());
        Thread.sleep (300);
        //rerun script with comment parameter
        FileUtils.deleteDirectory (changeSetDir);
        def comment = "this is a comment"
        script = loadScript(scriptName, [params:[comment:comment]]);
        result = script.run();

        assertEquals (2, versionControlDirectory.listFiles().size())
        changesXml = new XmlParser().parseText(result);
        changesAttributes = changesXml.attributes()
        changeSetDir = versionControlDirectory.listFiles().find {it.name == changesAttributes.name}
        changeInfoFile = new File(changeSetDir, "changes.xml");
        assertEquals (result, changeInfoFile.getText());

        changes = changesXml.Change*.attributes();
        assertEquals (2, changes.size)
    }


    def getChangeSetDir(def result)
    {
        def changesXml = new XmlParser().parseText(result);
        def changesAttributes = changesXml.attributes()
        return versionControlDirectory.listFiles().find {it.name == changesAttributes.name}
    }

    def assertAllFiles(allFiles, List filesToBeTracked)
    {
        def allFilesXml = new XmlParser().parseText(allFiles.getText()).File;
        assertEquals (filesToBeTracked.size(), allFilesXml.size())
        def fileConfigs = []
        allFilesXml.each{
            fileConfigs << it.attributes();
        }

        filesToBeTracked.each{File fileToBeTracked->
            def expectedFilePath = fileToBeTracked.canonicalPath.substring(baseDirFile.canonicalPath.length()+1).replaceAll("\\\\", "/")
            def fileConf = fileConfigs.find {it.path == "RapidSuite/${expectedFilePath}"}
            assertEquals(""+fileToBeTracked.lastModified(), fileConf.modifiedAt)
        }    
    }

}