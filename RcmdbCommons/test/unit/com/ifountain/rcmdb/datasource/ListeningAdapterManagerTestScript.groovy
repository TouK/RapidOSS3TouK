/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 24, 2008
 * Time: 1:56:50 PM
 * To change this template use File | Settings | File Templates.
 */
import com.ifountain.rcmdb.datasource.*

ListeningAdapterManagerTest.scriptMap.datasource=datasource
ListeningAdapterManagerTest.scriptMap.staticParam=staticParam
ListeningAdapterManagerTest.scriptMap.logger=logger

println "script started"
ListeningAdapterManagerTest.scriptMap.scriptRunStarted=true

println "script ended"
ListeningAdapterManagerTest.scriptMap.scriptRunEnded=true


def init(){
   ListeningAdapterManagerTest.scriptMap.scriptInitInvoked=true 
}

def cleanUp(){
   ListeningAdapterManagerTest.scriptMap.cleanUpInvoked=true
}

def getParameters(){
   return [
           "returnparam1":"param1"
   ]
}