import org.apache.commons.io.FileUtils

rsHome = new File(System.getProperty("base.dir", "."), "..");
def from = params.from
def to = params.to
def operation = params.operation
def srcFile = new File(rsHome, "RapidSuite/uploadedFiles/${from}");
def targetFile = new File(rsHome, "${to}");
checkFileIsInRsHome(srcFile);
checkFileIsInRsHome(targetFile);
targetFile.parentFile.mkdirs();
if(operation == "copy")
{
    FileUtils.copyFile (srcFile, targetFile);
}
else if(operation == "delete")
{
    if(targetFile.exists())
    {
        if(!targetFile.isDirectory())
        {
            FileUtils.deleteDirectory (targetFile);
        }
        else
        {
            throw new Exception("You cannot delete a nonempty directory.");
        }
    }
}
return "<successful>succesfully copied file</successful>"


def checkFileIsInRsHome(File file)
{
    if(!file.getCanonicalPath().startsWith(rsHome.getCanonicalPath()))
    {
        throw new Exception("Failed to copy file ${file}. It is not allowed to copy a file from outside of RS_HOME");
    }
}