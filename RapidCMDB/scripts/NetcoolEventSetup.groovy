import connection.NetcoolConnection
import datasource.BaseDatasource
import datasource.NetcoolDatasource

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
 * Date: Apr 7, 2008
 * Time: 2:59:03 PM
 * To change this template use File | Settings | File Templates.
 */
 
NC_URL = 'jdbc:sybase:Tds:ossmuse:4100/?LITERAL_PARAMS=true';
USERNAME = 'root';
PSW = ''; 
def ncConName = "ncConn";
def ncDsName = "NCOMS";

generateNetcoolConnAndDS(ncConName, ncDsName);

Model.findByName("NetcoolEvent")?.delete(flush:true);

def rcmdbDS = BaseDatasource.findByName("RCMDB");

// NetcoolEvent class
def netcoolEvent = Model.create(name:"NetcoolEvent");
def rcmdbModelDatasource = ModelDatasource.create(datasource:rcmdbDS, master:true);

def serverserial = ModelProperty.create(name:"serverserial", type:ModelProperty.numberType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def keyMappings = [ModelDatasourceKeyMapping.create(property:serverserial, datasource:rcmdbModelDatasource)];
def propList = [];
propList += serverserial;
def columns = [:];
columns.putAll(NetcoolDatasource.FIELDMAP);
columns.each{propName, propType->
	propType = "string";
	if (propName != "Class"){
		propList += ModelProperty.create(name:propName.toLowerCase(), type:propType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
	}
	else{
		propList += ModelProperty.create(name:"netcoolclass", type:propType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
	}
}
netcoolEvent = constructModel(netcoolEvent, propList, [rcmdbModelDatasource], keyMappings);
ModelGenerator.getInstance().generateModel(netcoolEvent);

return "Successfully created NetcoolEvent model class.";


def generateNetcoolConnAndDS(ncConName, ncDsName){
	def conn1 = NetcoolConnection.findByName(ncConName);
	if(conn1 == null){
	    conn1 = new NetcoolConnection(name: ncConName, url: NC_URL,username: USERNAME, password:PSW).save();
	}

	def ncDatasource= NetcoolDatasource.findByName(ncDsName);
	if (ncDatasource == null){
	    ncDatasource = new NetcoolDatasource(connection:conn1, name:ncDsName).save();
	}
}

def constructModel(model, listOfProperties, listOfDatasources, listOfKeyMappings)
{
    model = model.save();
    listOfDatasources.each{
        it.model = model;
        it.save();
    }
    listOfProperties.each{
        it.model = model;
        it.save();
    }

    listOfKeyMappings.each{
        it.save();
    }

    listOfDatasources.each{
        it.refresh();
    }
    model.refresh();
    return model;
}

def createRelation(firstModel, secondModel, firstName, secondName, firstCar, secondCar){
    ModelRelation.add(firstModel:firstModel, secondModel:secondModel, firstName:firstName, secondName:secondName, firstCardinality:firstCar, secondCardinality:secondCar);
    firstModel.refresh();
    secondModel.refresh();
}
