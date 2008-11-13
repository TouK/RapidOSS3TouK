import groovy.xml.MarkupBuilder

def query = params.query;

def devices = RsComputerSystem.search("name:${query}*", [max:20]).results;

def writer = new StringWriter();
def builder = new MarkupBuilder(writer);
builder.Suggestions(){
    devices.each{
        builder.Suggestion(name:it.name);
    }
}
return writer.toString();