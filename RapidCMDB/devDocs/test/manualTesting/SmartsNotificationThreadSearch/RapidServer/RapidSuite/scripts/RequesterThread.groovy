/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
import datasource.*;
import connection.*;

class RequesterThread extends Thread{	
	 public void run() {
       println "* "+getName()+" starts";
        while(com.ifountain.rcmdb.util.RCMDBDataStore.get("SmartsNotificationThreadSearchStop") == null){
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