import remoteModification.RemoteApplicationModification
import groovy.xml.MarkupBuilder
import java.text.SimpleDateFormat
def sortBy =  params.sort;
def order = params.order;
def modifications = RemoteApplicationModification.getActiveModifications([sort:sortBy, order:order]);
def sw = new StringWriter();
def mb = new MarkupBuilder(sw);
mb.Modifications(total:modifications.size())
{
    modifications.each{modification->
        def props = [filePath:modification.filePath, completeFilePath:modification.completeFilePath]
        props.isActive = modification.isActive;
        props.id = modification.id;
        mb.Modification(props);
    }
}

return sw.toString();