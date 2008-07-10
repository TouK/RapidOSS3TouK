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
    try{
        def res = Device.search(query, params);
        builder.Results(Total:res.total, Offset:res.offset){
            res.results.each{
                def atts = it.asMap();
                atts.put("id", it.id);
                atts.put("sortOrder", sortOrder++)
                builder.Result(atts);
            }
        }
    }catch(e)
    {
       builder.Results(Total:0, Offset:0) 
    }
}
else
{
    builder.Results(Total:0, Offset:0)
}



new File("res.xml").setText(writer.toString());