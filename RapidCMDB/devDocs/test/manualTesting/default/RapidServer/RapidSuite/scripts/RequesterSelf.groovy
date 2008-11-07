import datasource.*;
import connection.*;


println "* RequesterSelf starts";
for(int i=0; i <100; i++)
{
    //println "* RequesterSelf will do search";
    def ds = HttpDatasource.get(name:"local");
    def output=ds.doRequest("search", [query:"", searchIn:"RsEvent", login:"rsadmin", password:"changeme", max:"100"]);
    //def output=ds.doRequest("search", [query:"", searchIn:"RsEvent"]);
    //def willSleep = new Random(System.currentTimeMillis()).nextInt(20)
    //Thread.sleep(willSleep*1000);
}
println "* RequesterSelf stops";
  
