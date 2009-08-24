import org.apache.commons.io.FileUtils

rsHome = new File(System.getProperty("base.dir", "."), "..");
def file = params.file
def operation = params.operation
def srcFile = new File(rsHome, "RapidSuite/uploadedFiles/uploadedCustomerFiles/${file}");
def targetFile = new File(rsHome, "${file}");
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
    if(!file.getCanonicalPath().startsWith(rsHome.getCanonicalPath()))
    {
        throw new Exception("Failed to copy file ${file}. It is not allowed to copy a file from outside of RS_HOME");
    }
}