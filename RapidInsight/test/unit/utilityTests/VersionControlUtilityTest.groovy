package utilityTests

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Nov 12, 2009
* Time: 12:07:25 PM
* To change this template use File | Settings | File Templates.
*/
class VersionControlUtilityTest extends RapidCmdbTestCase{
    def sourceDir
    File sourceDirFile
    File versionControlDirectory;
    def utility;
    def excludedFileNames;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        System.setProperty ("base.dir", "../testOutput/RapidSuite")
        def gcl = new GroovyClassLoader();
        gcl.addClasspath (new File(getWorkspaceDirectory(), "RapidModules/RapidInsight/operations").path);
        utility = gcl.loadClass("VersionControlUtility").newInstance();
        sourceDirFile = utility.sourceDir
        sourceDir= sourceDirFile.canonicalPath;
        versionControlDirectory = utility.versionControlDirectory
        excludedFileNames = [
                "RapidSuite/data",
                "temp"
        ]
        FileUtils.deleteDirectory (sourceDirFile);
        sourceDirFile.parentFile.mkdirs();
        sourceDirFile.mkdir();
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testMarkModificationsDoesNotMarkFileChangesIfThereAreNoBaseVersion()
    {

        def filesToBeTracked = [];
        filesToBeTracked << new File("${sourceDir}/file1.txt")
        filesToBeTracked.last().setText("")
        filesToBeTracked << new File("${sourceDir}/trialDir/subFile1.txt")
        filesToBeTracked.last().parentFile.mkdir();
        filesToBeTracked.last().setText("")

        def excludedFiles = [];
        excludedFiles << new File("${sourceDir}/RapidSuite/data")
        excludedFiles.last().mkdirs();
        excludedFiles << new File("${excludedFiles[0]}/data1.txt")
        excludedFiles.last().setText("")
        excludedFiles << new File("emptydir")
        excludedFiles.last().mkdirs()
        def result = utility.markModifications (excludedFileNames, [:]);
        validateNoChangeChangeSet(result, filesToBeTracked);



    }

    public void testMarkModificationsDoesNotCreateChangeSetDirectoryIfThereAreNoModifications()
    {
        def filesToBeTracked = [];
        filesToBeTracked << new File("${sourceDir}/file1.txt")
        filesToBeTracked.last().setText("")
        filesToBeTracked << new File("${sourceDir}/trialDir/subFile1.txt")
        filesToBeTracked.last().parentFile.mkdirs();
        filesToBeTracked.last().setText("")

        //we created base version
        def result = utility.markModifications (excludedFileNames, [:]);

        def directoriesAfterMarkingBaseVersion = versionControlDirectory.listFiles()

        result = utility.markModifications (excludedFileNames, [:]);

        def directoriesAfterRunningScriptAgain = versionControlDirectory.listFiles()
        assertEquals (directoriesAfterMarkingBaseVersion.size(), directoriesAfterRunningScriptAgain.size())
        directoriesAfterMarkingBaseVersion.each{file->
            assertNotNull (directoriesAfterMarkingBaseVersion.find {it.canonicalPath == file.canonicalPath})
        }

    }

    public void testGetChangeSetList()
    {
        def changeSets = [new File(versionControlDirectory, "111111"),
                new File(versionControlDirectory, "2"),
                new File(versionControlDirectory, "invalidName")]
        changeSets.each{
            it.mkdirs()
            def allFilesFile = utility.getFileListFile(it)
            def changesFile = utility.getChangesFile(it)
            allFilesFile.setText("<Files/>");
            changesFile.setText("<Changes/>");
        }
        def changeSetWithNoAllFiles = new File(versionControlDirectory, "11111111111111");
        changeSetWithNoAllFiles.mkdirs()
        def returnedChageSetFiles = utility.getChangeSetList();
        assertEquals (3, returnedChageSetFiles.size())
        assertEquals (changeSets[1].canonicalPath, returnedChageSetFiles[0].file.canonicalPath)
        assertEquals (utility.getChangesFile(changeSets[1]).canonicalPath, returnedChageSetFiles[0].changesFile.canonicalPath)
        assertEquals (true, returnedChageSetFiles[0].isValid)
        assertEquals (changeSets[0].canonicalPath, returnedChageSetFiles[1].file.canonicalPath)
        assertEquals (utility.getChangesFile(changeSets[0]).canonicalPath, returnedChageSetFiles[1].changesFile.canonicalPath)
        assertEquals (true, returnedChageSetFiles[1].isValid)

        assertEquals (false, returnedChageSetFiles[2].isValid)
        assertEquals (changeSetWithNoAllFiles.canonicalPath, returnedChageSetFiles[2].file.canonicalPath)
        assertEquals (utility.getChangesFile(changeSetWithNoAllFiles).canonicalPath, returnedChageSetFiles[2].changesFile.canonicalPath)
    }

    public void testMarkModificationsCreateChangeSetDirectoryIfThereAreModifications()
    {
        def filesToBeTracked = [];
        filesToBeTracked << new File("${sourceDir}/file1.txt")
        filesToBeTracked.last().setText("")
        filesToBeTracked << new File("${sourceDir}/trialDir/subFile1.txt")
        filesToBeTracked.last().parentFile.mkdirs();
        filesToBeTracked.last().setText("")
        filesToBeTracked << new File("${sourceDir}/trialDir/subFile2.txt")
        filesToBeTracked.last().setText("")

        def filesToBeTrackedPath = filesToBeTracked.canonicalPath*.substring(sourceDirFile.canonicalPath.length()+1)*.replaceAll("\\\\", "/")

        //we created base version
        def result = utility.markModifications (excludedFileNames, [:]);
        def baseChangeSetDir = getChangeSetDir(result)

        Thread.sleep (100)
        def directoriesAfterMarkingBaseVersion = versionControlDirectory.listFiles()
        filesToBeTracked[0].delete();
        filesToBeTracked[1].setText("")
        filesToBeTracked << new File("${sourceDir}/trialDir/subFile3.txt")
        filesToBeTracked.last().setText("");
        filesToBeTrackedPath = filesToBeTracked.canonicalPath*.substring(sourceDirFile.canonicalPath.length()+1)*.replaceAll("\\\\", "/")


        result = utility.markModifications (excludedFileNames, [:]);

        def directoriesAfterRunningScriptAgain = versionControlDirectory.listFiles()
        assertEquals (directoriesAfterMarkingBaseVersion.size()+1, directoriesAfterRunningScriptAgain.size())

        def changesXml = new XmlParser().parseText(result);
        def changesAttributes = changesXml.attributes()
        def changeSetDir = directoriesAfterRunningScriptAgain.find {it.name == changesAttributes.name}

        def changes = changesXml.Change*.attributes();
        assertEquals (3, changes.size())
        println changes
        def change = changes.find{it.path == filesToBeTrackedPath[0] && it.operation == "Delete"}
        assertNotNull (change)
        change = changes.find{it.path == filesToBeTrackedPath[1] && it.operation == "Change" && it.modifiedAt == ""+filesToBeTracked[1].lastModified()}
        assertNotNull (change)
        change = changes.find{it.path == filesToBeTrackedPath[3] && it.operation == "Create" && it.modifiedAt == ""+filesToBeTracked[3].lastModified()}
        assertNotNull (change)
        def fileToBeCopied = new File("${changeSetDir}/${change.path}");
        assertEquals (filesToBeTracked[1].getText(), fileToBeCopied.getText())

        def allFiles = utility.getFileListFile(changeSetDir)
        def allFileList = [filesToBeTracked[1], filesToBeTracked[2], filesToBeTracked[3]]
        assertAllFiles(allFiles, allFileList);

        def changeInfoFile = utility.getChangesFile(changeSetDir)
        assertEquals (result, changeInfoFile.getText());

        //rerun script with comment parameter
        FileUtils.deleteDirectory (changeSetDir);
        def comment = "this is a comment"
        result = utility.markModifications (excludedFileNames, [comment:comment]);

        assertEquals (2, versionControlDirectory.listFiles().size())
        changesXml = new XmlParser().parseText(result);
        changesAttributes = changesXml.attributes()
        changeSetDir = versionControlDirectory.listFiles().find {it.name == changesAttributes.name}
        changeInfoFile = utility.getChangesFile(changeSetDir)
        assertEquals (result, changeInfoFile.getText());

        changes = changesXml.Change*.attributes();
        assertEquals (3, changes.size)

        def changesMap = utility.getChangesAsMap(changeSetDir);
        assertEquals(changeSetDir.name, changesMap.name)
        assertEquals(comment, changesMap.comment)
        assertEquals (3, changesMap.changes.size())
        change = changesMap.changes.find{it.path == filesToBeTrackedPath[0] && it.operation == "Delete"}
        assertNotNull (change)
        change = changesMap.changes.find{it.path == filesToBeTrackedPath[1] && it.operation == "Change" && it.modifiedAt == new Date(filesToBeTracked[1].lastModified())}
        assertNotNull (change)
        change = changes.find{it.path == filesToBeTrackedPath[3] && it.operation == "Create" && it.modifiedAt == ""+filesToBeTracked[3].lastModified()}
        assertNotNull (change)
    }

    public void testMarkModificationsCreateChangeSetDirectoryIfForceMarkIsSpecified()
    {
        def filesToBeTracked = [];
        filesToBeTracked << new File("${sourceDir}/file1.txt")
        filesToBeTracked.last().setText("")

        def filesToBeTrackedPath = filesToBeTracked.canonicalPath*.substring(sourceDirFile.canonicalPath.length()+1)*.replaceAll("\\\\", "/")

        //we created base version
        def result = utility.markModifications (excludedFileNames, [:]);


        result = utility.markModifications (excludedFileNames, [forceMark:"true"]);

        validateNoChangeChangeSet(result, filesToBeTracked);

    }

    public void testMarkModificationsWillDiscardCorruptedChangeSets()
    {
        def filesToBeTracked = [];
        filesToBeTracked << new File("${sourceDir}/file1.txt")
        filesToBeTracked.last().setText("")

        def filesToBeTrackedPath = filesToBeTracked.canonicalPath*.substring(sourceDirFile.canonicalPath.length()+1)*.replaceAll("\\\\", "/")

        //we created base version
        def result = utility.markModifications (excludedFileNames, [:]);

        //delete allFiles.xml
        def changeSetDir = getChangeSetDir(result)
        def allFiles = utility.getFileListFile(changeSetDir)
        allFiles.delete();

        result = utility.markModifications (excludedFileNames, [:]);
        validateNoChangeChangeSet(result, filesToBeTracked);

        //corrupted allfiles
        changeSetDir = getChangeSetDir(result)
        allFiles = utility.getFileListFile(changeSetDir)
        allFiles.setText ("<Obj")

        result = utility.markModifications (excludedFileNames, [:]);
        validateNoChangeChangeSet(result, filesToBeTracked);

    }

    public void testMarkModificationsWillDiscardCorruptedChangeSetsAndWillUseLastNotCorruptedOnes()
    {
        def filesToBeTracked = [];
        filesToBeTracked << new File("${sourceDir}/file1.txt")
        filesToBeTracked.last().setText("")

        def filesToBeTrackedPath = filesToBeTracked.canonicalPath*.substring(sourceDirFile.canonicalPath.length()+1)*.replaceAll("\\\\", "/")

        //we created base version
        def result = utility.markModifications (excludedFileNames, [:]);
        Thread.sleep (100)

        filesToBeTracked[0].setText("")

        result = utility.markModifications (excludedFileNames, [:]);

        //corrupted allfiles
        def changeSetDir = getChangeSetDir(result)
        def allFiles = utility.getFileListFile(changeSetDir)
        allFiles.setText ("<Obj")

        //rerun script will use base version
        result = utility.markModifications (excludedFileNames, [:]);

        def changesXml = new XmlParser().parseText(result);
        def changesAttributes = changesXml.attributes()
        changeSetDir = versionControlDirectory.listFiles().find {it.name == changesAttributes.name}
        def changeInfoFile = utility.getChangesFile(changeSetDir)
        assertEquals (result, changeInfoFile.getText());

        def changes = changesXml.Change*.attributes();
        assertEquals (1, changes.size())
        def change = changes.find{it.path == filesToBeTrackedPath[0] && it.operation == "Change" && it.modifiedAt == ""+filesToBeTracked[0].lastModified()}
        assertNotNull (change)
        def fileToBeCopied = new File("${changeSetDir}/${change.path}");
        assertEquals (filesToBeTracked[0].getText(), fileToBeCopied.getText())

        allFiles = utility.getFileListFile(changeSetDir)
        assertAllFiles(allFiles, filesToBeTracked);
    }

    def validateNoChangeChangeSet(String result, List filesToBeTracked)
    {
        def expectedResult = """
            <Changes comment="" name="">
            </Changes>
        """
        assertEqualsXML (expectedResult, result, ["name"])
        def changeSetDir = getChangeSetDir(result)

        def allFiles = utility.getFileListFile(changeSetDir)
        assertAllFiles(allFiles, filesToBeTracked);

        def changeInfoFile = utility.getChangesFile(changeSetDir);
        assertEquals (result, changeInfoFile.getText());
        assertEquals (2, changeSetDir.listFiles().size())
        def changesMap = utility.getChangesAsMap(changeSetDir);
        assertEquals (changeSetDir.name, changesMap.name)
        assertEquals ("", changesMap.comment)
        assertEquals (0, changesMap.changes.size())
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
            def expectedFilePath = fileToBeTracked.canonicalPath.substring(sourceDirFile.canonicalPath.length()+1).replaceAll("\\\\", "/")
            def fileConf = fileConfigs.find {it.path == expectedFilePath}
            assertEquals(""+fileToBeTracked.lastModified(), fileConf.modifiedAt)
        }
    }
}