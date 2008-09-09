import groovy.xml.MarkupBuilder;

def writer = new StringWriter();
def builder = new MarkupBuilder(writer);

builder.devices()
{
     builder.device( id : "device1", status : "up", load : "32");
     builder.device( id : "device2", status : "down", load : "86");
}

return writer.toString();