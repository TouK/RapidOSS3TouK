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
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.SuffixFileFilter
import org.apache.commons.lang.StringUtils
import org.apache.commons.collections.map.ReferenceMap

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 9, 2008
 * Time: 2:18:36 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidUITagLib
{
    static namespace = "rui"
    static root = "web-app\\js\\";
    static ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);
    
    def javascript = { attrs ->
        Map previouslyAddedJavascriptFiles = map.get(out);
        if(previouslyAddedJavascriptFiles == null)
        {
            previouslyAddedJavascriptFiles = [:]
            map.put(out, previouslyAddedJavascriptFiles);
        }
        if (!attrs.dir)
            throwTagError("Tag [javascript] is missing required attribute [dir]")
        if (!attrs.file)
        throwTagError("Tag [javascript] is missing required attribute [file]")


        def dir = "js/${attrs.dir}"
        def file = attrs.file
        def rootDir = new File("${System.getProperty("base.dir")}/web-app");
        def jsFile = new File("${rootDir.absolutePath}/${dir}/${file}");
        def importedFiles = getAllImportedScripts(jsFile);
        importedFiles.each{JavascriptFile jsFileObject->
                def importedJsFileLink = jsFileObject.file.absolutePath.substring(rootDir.absolutePath.length()+1).replace('\\', '/')
                importedJsFileLink = createLinkTo(file: importedJsFileLink)
                if(!previouslyAddedJavascriptFiles.containsKey(importedJsFileLink))
                {
                    previouslyAddedJavascriptFiles[importedJsFileLink] = importedJsFileLink;
                    addScriptFile(out, importedJsFileLink);
                }
        }
    }


    def addScriptFile(out, path)
    {
        out << "<script type=\"text/javascript\" src=\"${path}\"></script>"
        out << "\n"
    }
    
    def getAllImportedScripts ( jsFile )
    {
        def importMap = [:];
        def rootFile = new File("web-app\\js\\yui\\");
        findFilesToBeImportedWithMap( importMap, jsFile, 0 );
        def orderedFileList = new ArrayList(importMap.values());
        Collections.sort(orderedFileList);
        return orderedFileList
    }

    def findFilesToBeImportedWithMap (importMap, File jsFile, int iteration )
    {
        addFileToMap ( importMap, jsFile, iteration);
        def jsFileName = StringUtils.substringBefore(jsFile.name, ".");
        String importFilePath = "${jsFile.parentFile.absolutePath}\\${jsFileName}_import.conf";
        def importFile = new File( importFilePath );

        if(!importFile.exists())
        {
            return;
        }
        else
        {
            def importFileArray = importFile.getText().split ("\\s+", -1);
            def index = 0;
            importFileArray.each {
                String fileName = it.trim();
                if(fileName != "")
                {
                    String filePath = root + importFileArray[index];
                    def aFile = new File( filePath );
                    findFilesToBeImportedWithMap( importMap, aFile , iteration + 1 );
                }
                index++;
            }                       
        }       
    }

    def addFileToMap( importMap, File jsFile, int iteration )
    {
        def mapValue = importMap.getAt(jsFile.path );

        if( mapValue != null)
        {
            mapValue.order = mapValue.order < iteration ? iteration : mapValue.order;
        }
        else
        {

            importMap[jsFile.path] = new JavascriptFile(file:jsFile, order:iteration);
        }

    }

    def stylesheet = { attrs ->
        if (!attrs.dir)
            throwTagError("Tag [stylesheet] is missing required attribute [dir]")
        def includeType = attrs.includeType?attrs.includeType:"file";

        if(includeType == "dir" || includeType == "recursive")
        {
            def rootDir = new File("${System.getProperty("base.dir")}/web-app");
            def src = new File("${System.getProperty("base.dir")}/web-app/${attrs.dir}")
            def files = FileUtils.listFiles(src, ["css"] as String[], includeType == "recursive")
            files.each{File f->
                def css = f.absolutePath.substring(rootDir.absolutePath.length()+1).replace('\\', '/')
                css = createLinkTo(file: css)
                out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${css}\" />"
                out << "\n"
            }
        }
        else
        {
            if (!attrs.file)
            throwTagError("Tag [stylesheet] is missing required attribute [file]")
            def href = createLinkTo(dir: "${attrs.dir}", file: attrs.file)
            out << "<link rel=\"stylesheet\" type=\"text/css\" href=\"${href}\" />"
        }

    }

}

class JavascriptFile implements Comparable
{
    File file;
    String name;
    int order;

    public int compareTo(Object t) {

        if(t.order > order)
        {
            return 1;
        }
        else if(t.order < order)
        {
            return -1;
        }
        
        return 0
    }

    public String toString() {
        return "${file.path} Order: ${order}".toString(); //To change body of overridden methods use File | Settings | File Templates.
    }

    
    
}