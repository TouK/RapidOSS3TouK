import datasource.*;

logger.warn("*gonna do notwebsearch");

def output=""

def ds=HttpDatasource.get(name:NotificationOperationsConstants.HTTP_DATASOURCE_NAME);

output+=" ${ds} <br>"

output+=ds.doGetRequest("search?format=xml",[format:"xml",sort:"id",order:"asc",searchIn:"RsEvent",offset:"0",max:"1000",rnd:"1225386195701",login:"rsadmin",password:"changeme"]);

logger.warn("done notwebsearch");


//http://localhost:12222/RapidSuite/search?format=xml&searchIn=RsEvent&offset=0&sort=id&order=asc&max=100&query=&rnd=1225386195701&login=rsadmin&password=changeme
//return output;