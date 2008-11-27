import datasource.*;
import connection.*;

class RequesterThread extends Thread{	
	 public void run() {
       println "* "+getName()+" starts";
        while(SnmpConnection.list().size() == 0){
            println "* "+getName()+" will do search";
            def ds = HttpDatasource.get(name:"localhttpds");
	        def results=ds.doRequest("search", [query:"", searchIn:"RsEvent", login:"rsadmin", password:"changeme", max:"100"]);

	        println getName()+" finished search  ${results[0..25]}";
	        def willSleep = new Random(System.currentTimeMillis()).nextInt(20);
	        Thread.sleep(willSleep*1000);
	    }
	    println "* "+getName()+" stops";
    }	
}