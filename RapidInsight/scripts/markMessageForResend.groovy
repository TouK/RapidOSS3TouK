import com.ifountain.rcmdb.domain.util.ControllerUtils
import message.RsMessage
import groovy.xml.MarkupBuilder;

if(!params.messageId)
{
	return ControllerUtils.convertErrorToXml("Id should be specified");
}

def message=RsMessage.get(id:params.messageId);
if(!message)
{
	return ControllerUtils.convertErrorToXml("Can not update message. Because it does not exist.");
}

message.markForResend();
if(message.hasErrors())
{
	return ControllerUtils.convertErrorToXml("Can not update message. Reason ${message.errors}");	
}	
else
{
	def props = message.asStringMap(message.getNonFederatedPropertyList().name);
    def sw = new StringWriter();
    def builder = new MarkupBuilder(sw);
    builder.Objects() {
        builder.Object(props);
    }
    return sw.toString() 	
}  

