import groovy.xml.MarkupBuilder;
import RsComputerSystem;

def deviceName = params.deviceName;
def device = RsComputerSystem.get( name : deviceName);

def writer = new StringWriter();
def mapBuilder = new MarkupBuilder(writer);

mapBuilder.graph()
{
	if( device != null )
	{
		def links = device.connectedVia;
		def expandable = ( links) ? "true" : "false";

		mapBuilder.device( id: device.name, model : device.model, type : device.creationClassName, gauged : "true", expands : expandable );
	}
}

return writer.toString();