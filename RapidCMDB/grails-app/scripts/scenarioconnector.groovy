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
 * User: Administrator
 * Date: Mar 20, 2008
 * Time: 1:38:50 PM
 * To change this template use File | Settings | File Templates.
 */

def ds1 = SingleTableDatabaseDatasource.findByName("DS1");
def ds2 = SingleTableDatabaseDatasource.findByName("DS2");

def records = ds1.getRecords(["name", "classname", "displayname"]);
println "records from DS1 size: " + records.size();
for(record in records){
    def className = record.CLASSNAME;
    def name = record.NAME;
    def displayname = record.DISPLAYNAME;
    if(className == "Device"){
          println "creating new device with " + name + " " + displayname;
          println new Device(name:name, displayname:displayname, dsname:"DS1").save();
          println "successfully created.";
    }
    else{
        println "creating new link with " + name + " " + displayname;
          new Link(name:name, displayname:displayname, dsname:"DS1").save();
           println "successfully created.";
    }
}
records = ds2.getRecords(["id", "classname", "displayname"]);
for(record in records){
    def className = record.CLASSNAME;
    def name = record.ID;
    def displayname = record.DISPLAYNAME;
    if(className == "Device"){
          new Device(name:name, displayname:displayname, dsname:"DS2").save();
    }
    else{
        new Link(name:name, displayname:displayname, dsname:"DS2").save();
    }
}
