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
ListeningAdapterManagerTest.scriptMap.staticParamMap=staticParamMap
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