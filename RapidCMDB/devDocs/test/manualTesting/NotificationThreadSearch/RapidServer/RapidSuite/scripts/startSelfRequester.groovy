import datasource.*;
import connection.*;


println "* RequesterSelf starts";
while(SnmpConnection.list().size() == 0){
    //println "* RequesterSelf will do search";
    def ds = HttpDatasource.get(name:"localhttpds");
    def output=ds.doRequest("search", [query:"", searchIn:"RsEvent", login:"rsadmin", password:"changeme", max:"100"]);
    //def output=ds.doRequest("search", [query:"", searchIn:"RsEvent"]);
    //def willSleep = new Random(System.currentTimeMillis()).nextInt(20)
    //Thread.sleep(willSleep*1000);
    
    println "RequesterSelf finished search , output : "+output[0..25];
}
println "* RequesterSelf stops";
  
