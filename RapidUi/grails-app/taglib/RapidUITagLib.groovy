import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.SuffixFileFilter

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
    def javascript = { attrs ->
        if (!attrs.dir)
            throwTagError("Tag [javascript] is missing required attribute [dir]")
        def includeType = attrs.includeType?attrs.includeType:"file";
        if(includeType == "dir" || includeType == "recursive")
        {
            def rootDir = new File("${System.getProperty("base.dir")}/web-app");
            def src = new File("${System.getProperty("base.dir")}/web-app/js/${attrs.dir}")
            def files = FileUtils.listFiles(src, ["js"]  as String[], includeType == "recursive")

            files.each{File f->
                def js = f.absolutePath.substring(rootDir.absolutePath.length()+1).replace('\\', '/')
                js = createLinkTo(file: js)
                out << "<script type=\"text/javascript\" src=\"${js}\"></script>\n"
            }
        }
        else
        {
            if (!attrs.file)
            throwTagError("Tag [javascript] is missing required attribute [file]")
            def src = createLinkTo(dir: "js/${attrs.dir}", file: attrs.file)
            out << "<script type=\"text/javascript\" src=\"${src}\"></script>"
            out << "\n"
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