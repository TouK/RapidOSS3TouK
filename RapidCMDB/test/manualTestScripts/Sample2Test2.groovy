/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
 * User: Pinar Kinikoglu
 * Date: Mar 20, 2008
 * Time: 2:26:09 PM
 * To change this template use File | Settings | File Templates.
 */
/*
    Service Level is provided as parameter (Servicelevel)

     ex: 
http://localhost:12222/RapidCMDB/script/run/Sample2Test2?Servicelevel=Gold
 
 prints following lines in the RapidServer/RapidCMDB/logs/RapidServerOut.log

Customer info
["service1":"manager1"]
    
*/
def slaLevel = params.Servicelevel;
def slas = Sla.searchEvery("level:${slaLevel}");
def custInfo = [:];
slas.each {
   custInfo.put(it.service.name, it.customer.accountmanager)
}

renderOutput(custInfo);
return "Successfully executed. Please check RapidServer/RapidCMDB/logs/RapidServerOut.log file to verify the expected result.";

def renderOutput(info){
    println "Customer info"
    println info;
}
