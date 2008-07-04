import groovy.xml.MarkupBuilder
def devices = Device.list(params);
def offset = params.offset;
if(offset == null){
    offset = "0";
}

def writer = new StringWriter();
def builder = new MarkupBuilder(writer);
def sortOrder = 0;
builder.Results(Total:Device.countHits("name:*"), Offset:offset){
    devices.each{
        def atts = it.asMap();
        atts.put("id", it.id);
        atts.put("sortOrder", sortOrder++)
        builder.Result(atts);
    }
}
return writer.toString();