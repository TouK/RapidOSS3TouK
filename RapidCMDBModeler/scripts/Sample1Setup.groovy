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
 * Date: June 12, 2008
 * Time: 2:59:03 PM
 * To change this template use File | Settings | File Templates.
 */

import model.*;

def datasources = [];
def props = [];

Model.get(name:"Developer")?.remove();
Model.get(name:"Employee")?.remove();
Model.get(name:"Person")?.remove();
Model.get(name:"Team")?.remove();
Model.get(name:"Task")?.remove();

/* Define properties. Note that :
1. 'propertyDatasource' takes the Datasource name
2. 'propertySpecifyingDatasource' takes the dynamic datasource property name
3. all the datasources, except RCMDB, refered to are assumed to be created before running the script
*/
def modelhelperPerson = new ModelHelper("Person");

def name = [name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def bday = [name:"bday", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
props.add(name);
props.add(bday);

// Identify the datasource names. 
def rcmdbDs = DatasourceName.get(name:"RCMDB");
def rcmdbKey = [name:"name"];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey]]);

// The order of setting datasources, properties, and keymappings should be as follows:
modelhelperPerson.datasources = datasources;
modelhelperPerson.props = props; 
modelhelperPerson.setKeyMappings();  

def modelhelperEmployee = new ModelHelper("Employee", "Person"); 
def dept = [name:"dept", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def salary = [name:"salary", type:ModelProperty.numberType, defaultValue:1000, blank:false, lazy:false, propertyDatasource:"RCMDB"];
props = [];
props.add(dept);
props.add(salary);

modelhelperEmployee.props = props; 

def modelhelperDeveloper = new ModelHelper("Developer", "Employee"); 
def language = [name:"language", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
props = [];
props.add(language);

modelhelperDeveloper.props = props; 

def modelhelperTeam = new ModelHelper("Team"); 
name = [name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def mascot = [name:"mascot", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
props = [];
props.add(name);
props.add(mascot);

rcmdbDs = DatasourceName.get(name:"RCMDB");
rcmdbKey = [name:"name"];
datasources = [];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey]]);

modelhelperTeam.datasources = datasources;
modelhelperTeam.props = props; 
modelhelperTeam.setKeyMappings();  


def modelhelperTask = new ModelHelper("Task"); 
name = [name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
props = [];
props.add(name);

rcmdbDs = DatasourceName.get(name:"RCMDB");
rcmdbKey = [name:"name"];
datasources = [];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey]]);

modelhelperTask.datasources = datasources;
modelhelperTask.props = props; 
modelhelperTask.setKeyMappings();  

// Relation is supposed to be initiated from the first model class. 
// Therefore, you only provide the secondClass name.
// Param1: Second class name
// Param2: Relation name from first class to second class
// Param3: Reverse relation name from second class to first class
// Param4: Cardinality to first class
// Param5: Cardinality to second class

modelhelperEmployee.createRelation(modelhelperEmployee.model, "prevEmp", "nextEmp", ModelRelation.ONE, ModelRelation.ONE);
// UNCOMMENT WHEN CMDB-283 IS FIXED modelhelperEmployee.createRelation(modelhelperEmployee.model, "employees", "manager", ModelRelation.ONE, ModelRelation.MANY);
modelhelperEmployee.createRelation(modelhelperTeam.model, "manages", "managedBy", ModelRelation.ONE, ModelRelation.MANY);
modelhelperDeveloper.createRelation(modelhelperTask.model, "worksOn", "workedOnBy", ModelRelation.MANY, ModelRelation.MANY);

return "Success"