import remoteModification.RemoteApplicationModification
import datasource.HttpDatasource
import groovy.xml.MarkupBuilder

def modificationId = params.modificationId;
def modificationComment = params.comment;
def modificationOperation = params.operation;
if(modificationId == null)
{
    throw new Exception("No id parameter specified");
}
if(modificationComment == null)
{
    modificationComment = "";
}

RemoteApplicationModification modification = RemoteApplicationModification.get(id:modificationId)
if(modification == null)
{
    throw new Exception("No modification is defined with id ${modificationId}");
}

if(modificationOperation == "commit")
{
    HttpDatasource ds = HttpDatasource.list()[0]
    if(ds == null)
    {
        throw new Exception("No http datasource is defined");
    }
    modification.commit(ds, modificationComment);
}
else if(modificationOperation == "ignore")
{
    modification.ignore(modificationComment);
}
else
{
    throw new Exception("invalid operation ${modificationOperation}");    
}

def sw = new StringWriter();
def mb = new MarkupBuilder(sw);
mb.Modifications()
{
    mb.Modification(id:modificationId, filePath:modification.filePath, willBeRemoved:true);
}

return sw.toString();



