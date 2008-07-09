import groovy.xml.MarkupBuilder
def offset = params.offset;
if(offset == null){
    offset = "0";
}
def query = params.query;

def writer = new StringWriter();
def builder = new MarkupBuilder(writer);
def sortOrder = 0;
if(query != null)
{
    def res = Device.search(query, params);
    builder.Results(Total:res.total, Offset:res.offset){
        res.results.each{
            def atts = it.asMap();
            atts.put("id", it.id);
            atts.put("sortOrder", sortOrder++)
            builder.Result(atts);
        }
    }
}
else
{
    builder.Results(Total:0, Offset:0)
}



return writer.toString();