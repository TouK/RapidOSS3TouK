import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import com.yahoo.platform.yui.compressor.CssCompressor
import com.yahoo.platform.yui.compressor.JavaScriptCompressor

/**
* Created by IntelliJ IDEA.
* User: iFountain
* Date: Jul 15, 2008
* Time: 3:01:09 PM
* To change this template use File | Settings | File Templates.
*/
def rootFile = new File("web-app/js/yui/")

def mergesCss = new File("${rootFile.absoluteFile}/yui.css");
def mergesJs = new File("${rootFile.absoluteFile}/yui.js");
mergesJs.delete();
mergesCss.delete();

def cssFiles = FileUtils.listFiles (rootFile, ["css"] as String[], true);
def jsFiles = FileUtils.listFiles (rootFile, ["js"] as String[], true);

def finalCssFileWriter = new FileWriter(mergesCss);
def finalJsFileWriter = new FileWriter(mergesJs);
cssFiles.each{File cssFile->
    def urlPrefix = "";
    if(cssFile.parentFile.absolutePath != rootFile.absolutePath)
    {
        urlPrefix = cssFile.parentFile.absolutePath.substring (rootFile.absolutePath.length()+1);
        urlPrefix = urlPrefix.replaceAll ('\\\\', '/') + "/";
    }


    String fileContent = cssFile.getText ();
    def parts = fileContent.split("url\\s*\\(", -1);
    def res = new StringBuffer();
    for(int i=0; i < parts.length; i++)
    {
        def part = parts[i];
        if(i != 0)
        {
            def url = StringUtils.substringBefore(part, ")").trim();
            url = url.replaceAll ("'", "");
            url = url.replaceAll ("\"", "");
            def remaining = StringUtils.substringAfter(part, ")")
            res.append("url('").append(urlPrefix).append(url).append ("')").append (remaining);


        }
        else
        {
            res.append(part);
        }
    }
    CssCompressor comp = new CssCompressor(new StringReader(res.toString()));
    comp.compress (finalCssFileWriter,-1);
}
finalCssFileWriter.close();
jsFiles.each{File jsFile->
    println "Compressing ${jsFile}"
    def reporter = new ErrorReporter();
    JavaScriptCompressor compressor = new JavaScriptCompressor(new FileReader(jsFile), reporter);

    if (reporter.warningMessage.length() > 0) {
        throw new Exception(warningMessage.toString());
    }

    if (reporter.errorMessage.length() > 0) {
        throw new Exception(warningMessage.toString());
    }

    compressor.compress(finalJsFileWriter, -1, true, false, false, false);
}
finalJsFileWriter.close();


