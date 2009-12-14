import org.apache.commons.io.filefilter.TrueFileFilter
import groovy.xml.MarkupBuilder
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Nov 12, 2009
* Time: 11:59:31 AM
* To change this template use File | Settings | File Templates.
*/
class VersionControlUtility {
    public static final String allFilesName = "allFiles.xml"
    public static final String changesFileName = "changes.xml"
    File baseDirectory = new File(System.getProperty("base.dir", "."));
    File sourceDir = baseDirectory.parentFile;
    File versionControlDirectory = new File(sourceDir, "versionControl");
    def synchronized getChangeSetList()
    {
        def changeSets = [];
        versionControlDirectory.listFiles().each {File changeSet ->
            try {
                def changeSetTime = Long.parseLong(changeSet.getName());
                def changeSetInfo = [file: changeSet, changesFile: getChangesFile(changeSet), date: new Date(changeSetTime), isValid:isChangeSet(changeSet)];
                changeSets << changeSetInfo;
            } catch (NumberFormatException e) {/*ignore */}
        }
        return changeSets.sort {it.date};
    }

    def getChangesAsMap(File changeSetDir)
    {
        def changesFile = getChangesFile(changeSetDir)
        def changesXml = new XmlParser().parseText(changesFile.getText())
        def attributes = changesXml.attributes();
        def changesMap = [:]
        changesMap.putAll (attributes);
        def changes = changesXml.Change*.attributes()
        changes.each{
            it.modifiedAt = new Date(Long.parseLong(it.modifiedAt));
        }
        changesMap.changes = changes;
        return changesMap;
    }
    def synchronized markModifications(List excludedFilesP, Map options)
    {
        def comment = options.comment;
        def forceMark = options.forceMark == "true" || options.forceMark == true;
        if (comment == null)
        {
            comment = "";
        }
        List excludedFiles = new ArrayList(excludedFilesP);
        excludedFiles.add(versionControlDirectory.getCanonicalPath().substring(sourceDir.canonicalPath.length() + 1));
        def changeSetDirectory = constructChangeSetDirFile()

        def oldFilesXml = findLastChangeSet(versionControlDirectory, allFilesName);
        def filesInSourceDirectory = listFiles(sourceDir, excludedFiles);


        def changes = [];
        if (oldFilesXml != null) {
            def oldFileInfo = [:]
            def files = oldFilesXml.File
            files.each {file ->
                def path = file.@path.toString()
                def modifiedAt = Long.parseLong(file.@modifiedAt.toString());
                oldFileInfo[file.@path.toString()] = [path: path, modifiedAt: modifiedAt];
            }
            def changedFiles = []
            filesInSourceDirectory.each {conf ->
                def oldFileConfig = oldFileInfo.remove(conf.path);
                if(oldFileConfig == null)
                {
                    conf.operation = "Create"
                    changedFiles.add(conf)
                }
                else if (oldFileConfig.modifiedAt != conf.modifiedAt)
                {
                    conf.operation = "Change"
                    changedFiles.add(conf)
                }
            }
            def removedFiles = new ArrayList(oldFileInfo.values());
            removedFiles.each {removedFileConfig ->
                removedFileConfig.operation = "Delete"
                changes.add(removedFileConfig)
            }
            changedFiles.each {changedFileConfig ->
                changes.add(changedFileConfig)
                def targetFile = new File(changeSetDirectory, changedFileConfig.path)
                def sourceFile = new File(sourceDir, changedFileConfig.path)
                FileUtils.copyFile(sourceFile, targetFile)
            }
        }
        def changedFilesStringBuffer = new StringWriter();
        def mb = new MarkupBuilder(changedFilesStringBuffer);
        mb.Changes(comment: comment, name: changeSetDirectory.name) {
            changes.each {change ->
                mb.Change(change);
            }
        }
        if (!changes.isEmpty() || oldFilesXml == null || forceMark)
        {
            changeSetDirectory.mkdirs();
            createAllFilesList(getFileListFile(changeSetDirectory), filesInSourceDirectory);
            getChangesFile(changeSetDirectory).setText(changedFilesStringBuffer.toString())
        }
        return changedFilesStringBuffer.toString();
    }
    public File getChangeSetDir(String changeSetName)
    {
        return new File(versionControlDirectory, changeSetName);
    }
    public synchronized File constructChangeSetDirFile()
    {
        def changeSetDirectory = null;
        while (changeSetDirectory == null)
        {
            def changesDirectoryName = String.valueOf(System.currentTimeMillis());
            changeSetDirectory = new File(versionControlDirectory, changesDirectoryName)
            if (changeSetDirectory.exists())
            {
                changeSetDirectory = null;
                Thread.sleep(100);
            }
        }
        return changeSetDirectory;
    }
    def getChangesFile(File changeSetDir)
    {
        return new File(changeSetDir, changesFileName);
    }
    def getFileListFile(File changeSetDir)
    {
        return new File(changeSetDir, allFilesName);
    }
    private def isOneOfExcludedFiles(String relativePath, List excludedFileList)
    {
        for (int i = 0; i < excludedFileList.size(); i++)
        {
            def excludedFile = excludedFileList[i]
            if (relativePath.startsWith(excludedFile))
            {
                return true;
            }
        }
        return false;
    }

    private def findLastChangeSet(File versionControlDirectory, String allFilesName)
    {
        def changeSets = versionControlDirectory.listFiles().findAll {it.isDirectory()}
        changeSets = changeSets.sort {it.lastModified()}.reverse();
        for (int i = 0; i < changeSets.size(); i++)
        {
            def changeSetDir = changeSets[i];
            if (isChangeSet(changeSetDir))
            {
                def lastAllFiles = getFileListFile(changeSetDir)
                return new XmlParser().parseText(lastAllFiles.getText());
            }

        }
        return null;
    }

    private boolean isChangeSet(File changeSetDir)
    {
        def lastAllFiles = getFileListFile(changeSetDir)
        def changesFile = getChangesFile(changeSetDir)
        if (lastAllFiles.exists() && changesFile.exists())
        {
            try {
                new XmlParser().parseText(lastAllFiles.getText());
                new XmlParser().parseText(changesFile.getText());
                return true;
            } catch (Throwable t) {/*ignore*/}
        }
        return false;
    }
    private def listFiles(File sourceDir, List excludedFiles)
    {
        excludedFiles = excludedFiles*.replaceAll("\\\\", "/");
        def foundFiles = []
        def sourceDirLength = sourceDir.canonicalPath.length() + 1;
        FileUtils.listFiles(sourceDir, new TrueFileFilter(), new TrueFileFilter()).each {File f ->
            def filePath = f.getCanonicalPath().substring(sourceDirLength).replaceAll("\\\\", "/")
            if (!f.isDirectory() && !isOneOfExcludedFiles(filePath, excludedFiles))
            {
                foundFiles.add([path: filePath, modifiedAt: f.lastModified()]);
            }
        }
        return foundFiles;
    }

    private def createAllFilesList(File allFiles, List fileConfigurations)
    {
        def sw = new StringWriter();
        def mb = new MarkupBuilder(sw);
        mb.Files {
            fileConfigurations.each {newFileConf ->
                mb.File(newFileConf)
            }
        }
        allFiles.setText(sw.toString());
    }
}