import org.apache.commons.io.FileUtils

baseDir = new File(System.getProperty("base.dir", "."));
def file = params.file
def operation = params.operation
def srcFile = new File(baseDir, "uploadedFiles/uploadedCustomerFiles/${file}");
def targetFile = new File(baseDir, "../${file}");
checkFileIsInRsHome(srcFile);
checkFileIsInRsHome(targetFile);
if(operation == "copy")
{
    FileUtils.copyFile (srcFile, targetFile);
}
else if(operation == "delete")
{
    targetFile.delete();
}
return "<successful>succesfully copied file</successful>"


def checkFileIsInRsHome(File file)
{
    if(file.getCanonicalPath().startsWith(baseDir.getCanonicalPath()))
    {
        throw new Exception("Failed to copy file ${file}. It is not allowed to copy a file from outside of RS_HOME");
    }
}