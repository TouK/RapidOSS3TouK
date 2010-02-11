import groovy.xml.MarkupBuilder
import org.apache.commons.lang.StringUtils
import org.apache.commons.io.FileUtils
import org.grails.codeeditor.utils.CodeEditorFileUtils;
class CodeController {
    def codeEditorConfiguration;
    def index = {render(view: "codeEditor")}
    def baseDirectory = new File(new File(System.getProperty("base.dir", ".")), "..")
    def view = {
        def file = params.file;
        if (file != null)
        {
            def sw = new StringWriter();
            def builder = new MarkupBuilder(sw);
            try {
                def fObject = CodeEditorFileUtils.getFileRelativeToBaseDir(file);
                if (fObject.exists())
                {
                    def suffix = StringUtils.substringAfterLast(fObject.getName(), ".");
                    if (suffix == "")
                    {
                        suffix = "groovy";
                    }
                    builder.File(file: file, type: suffix, fObject.getText());
                    render(text: sw.toString(), contentType: 'text/xml');
                }
                else
                {
                    addError("code.editor.file.does.not.exist.exception", [file]);
                    render(text: errorsToXml(), contentType: "text/xml")
                }
            } catch (Exception e)
            {
                log.warn("Exception occurred in listFiles", e);
                addError("code.editor.view.exception", [file, e.toString()]);
                render(text: errorsToXml(), contentType: "text/xml")
            }
        }
        else
        {
            addError("code.editor.missing.parameter", ["file"]);
            render(text: errorsToXml(), contentType: "text/xml")
        }
    }

    def template = {
        render(view: "template")
    }

    def listFiles = {
        def file = params.file;
        try {
            def dir = CodeEditorFileUtils.getFileRelativeToBaseDir(file)
            if (!dir.exists())
            {
                addError("code.editor.file.does.not.exist.exception", [file]);
                render(text: errorsToXml(), contentType: "text/xml")
            }
            else
            {
                def sw = new StringWriter();
                def builder = new MarkupBuilder(sw);
                def dirRelativePath = CodeEditorFileUtils.getRelativeFilePath(baseDirectory, dir)
                def filters = ["groovy", "java", "xml", "txt", "js", "css", "gsp", "jsp", "html", "htm"];
                def files = []
                def dirs = []
                dir.listFiles().each {File subFile ->
                    def subFilePath = CodeEditorFileUtils.getRelativeFilePath(baseDirectory, subFile)
                    def subFileName = subFile.name;
                    def subFileType = StringUtils.substringAfter(subFileName, ".");
                    if (subFile.isDirectory())
                    {
                        dirs.add([file: subFilePath, displayName: subFile.getName(), type: subFileType, isDir: subFile.isDirectory()])

                    }
                    else if (filters.contains(subFileType))
                    {
                        files.add([file: subFilePath, displayName: subFile.getName(), isDir: subFile.isDirectory()]);
                    }
                }
                files = files.sort {it.displayName.toLowerCase()}
                dirs = dirs.sort {it.displayName.toLowerCase()}
                builder.Files(rootDir: dirRelativePath) {
                    dirs.each {dirMap ->
                        builder.File(dirMap)
                    }
                    files.each {fileMap ->
                        builder.File(fileMap);
                    }
                }
                render(text: sw.toString(), contentType: 'text/xml');
            }
        } catch (Exception e)
        {
            log.warn("Exception occurred in listFiles", e);
            addError("code.editor.listFiles.exception", [file, e.toString()]);
            render(text: errorsToXml(), contentType: "text/xml")
        }
    }

    def save = {
        if (params.file != null)
        {
            try
            {
                def fileName = params.file;
                def file = CodeEditorFileUtils.getFileRelativeToBaseDir(fileName)
                def content = params.fileContent;
                file.parentFile.mkdirs();
                file.setText(content);
                render(contentType: 'text/xml')
                        {
                            successful("file ${fileName} saved successfully");
                        }
            } catch (e)
            {
                log.warn("Exception occurred in listFiles", e);
                addError("code.editor.save.exception", [params.file, e.toString()]);
                render(text: errorsToXml(), contentType: "text/xml")
            }
        }
        else
        {
            addError("code.editor.missing.parameter", ["file"]);
            render(text: errorsToXml(), contentType: "text/xml")
        }
    }

    def delete = {
        if (params.file != null)
        {
            def fileName = params.file;
            try
            {
                def file = CodeEditorFileUtils.getFileRelativeToBaseDir(fileName);
                if (file.exists())
                {
                    file.delete();
                    render(contentType: 'text/xml')
                            {
                                successful("file ${fileName} deleted successfully");
                            }
                }
                else
                {
                    addError("code.editor.file.does.not.exist.exception", [fileName]);
                    render(text: errorsToXml(), contentType: "text/xml")
                }
            } catch (e)
            {
                log.warn("Exception occurred in listFiles", e);
                addError("code.editor.delete.exception", [fileName, e.toString()]);
                render(text: errorsToXml(), contentType: "text/xml")
            }
        }
        else
        {
            addError("code.editor.missing.parameter", ["file"]);
            render(text: errorsToXml(), contentType: "text/xml")
        }
    }
}
