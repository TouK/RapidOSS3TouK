package logmanager

import groovy.xml.MarkupBuilder
import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.grails.logmanager.utils.LogUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.apache.commons.io.filefilter.AndFileFilter
import org.apache.commons.io.filefilter.OrFileFilter
import org.apache.commons.io.filefilter.RegexFileFilter
import org.apache.commons.io.filefilter.FalseFileFilter
import org.apache.commons.io.filefilter.NotFileFilter
import org.grails.logmanager.utils.LogManagerUtils

class ViewLogController {
    static Template template;
    static Object lock = new Object();
    def logPersisterService;
    def logManagerSettings;
    def index = { redirect(action:view) }
    def view = {
        return [:];
    }

    private Template getLogOutputTemplate() {
        synchronized (lock)
        {
            if (template == null) {
                SimpleTemplateEngine engine = new SimpleTemplateEngine();
                template = engine.createTemplate(new File("${System.getProperty("base.dir")}/grails-app/templates/log/logoutput.gsp"));
            }
            return template;
        }
    }

    def listFiles = {
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def logFileNames = LogManagerUtils.getLogFileNames(logManagerSettings);
        builder.Files(count:logFileNames.size())
        {
            logFileNames.each{
                builder.File(name:it)
            }
        }
        render(contentType: "text/xml", text: sw.toString());
    }

    def getLog = {
        String fileName = params.logFile;
        long offset = params.offset.toLong();
        if(params.lastN != null)
        {
            long lastN = params.lastN.toLong();
            offset = LogUtils.getLastNLineOffset (fileName, lastN)
        }
        int max = params.max.toInteger();
        def sw = new StringWriter();
        def xmlBuilder = new MarkupBuilder(sw);
        def lines = [];
        try
        {
            if(!LogManagerUtils.isValidLogFile(logManagerSettings, fileName))
            {
                throw new Exception("Invalid log file ${fileName}. You cannot access a file outside of log directories");    
            }
            offset = LogUtils.readLog(fileName, offset, max, lines);
            def logoutputTemplate = getLogOutputTemplate();
            xmlBuilder.Log(fileName: fileName, offset: offset)
            {
                xmlBuilder.Lines {
                    lines.each {line ->
                        synchronized (logoutputTemplate) {
                            Writable w = logoutputTemplate.make(line: line);
                            line = w.toString().trim();
                        }
                        xmlBuilder.Line(content: line);
                    }
                }
            }
            render(contentType: "text/xml", text: sw.toString());
        }
        catch(Exception e)
        {
            render(contentType: "text/xml")
            {
                Errors()
                {
                    Error(message:"Log file exception. Reason:${e.getMessage()}");    
                }
            }
        }
    }

    def reloadTemplate = {
        synchronized (lock)
        {
            template = null;
        }
        render(contentType: "text/xml"){
            Successfull("Log template reloaded successfully");
        }
    }
}
