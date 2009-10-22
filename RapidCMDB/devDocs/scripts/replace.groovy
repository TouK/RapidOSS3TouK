import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.apache.commons.io.filefilter.RegexFileFilter
import org.apache.commons.io.filefilter.OrFileFilter
import org.apache.commons.io.IOCase
import replacement.RsRegExpFileFilter

def dirs = ["D:/IdeaWorkspace"]
def includedFilePathRegExp = [
    ".*/web-app/.*\\.gsp"
]
def replacementPairs = ["\\.\\./states":"../images/components/rapids/states",
"'server_icon.png'":"'../images/map/server_icon.png'",
"'router_icon.png'":"'../images/map/router_icon.png'",
"'switch_icon.png'":"'../images/map/switch_icon.png'"
]

def fileFilter = new TrueFileFilter();
def filters = [];
includedFilePathRegExp.each{String pathExp->
    filters.add (new RsRegExpFileFilter(pathExp));
}
if(!filters.isEmpty())
{
    fileFilter = new OrFileFilter(filters);
}
dirs.each{String filePath->
    File rootDir = new File(filePath);
    def foundFiles = FileUtils.listFiles (rootDir, fileFilter,  new TrueFileFilter());
    foundFiles.each{File fileToBeProcessed->
        def text = fileToBeProcessed.getText();
        def fileText = text;
        replacementPairs.each{textToBeReplaced, newText->
            text = text.replaceAll(textToBeReplaced, newText)
        }
        if(fileText != text)
        {
            println "Replacing text in file ${fileToBeProcessed.getPath()}";
            fileToBeProcessed.setText (""+text)
        }

    }
}