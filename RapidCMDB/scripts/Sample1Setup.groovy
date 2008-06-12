import model.Model
import model.ModelDatasource
import model.ModelProperty
import model.ModelDatasourceKeyMapping
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import model.ModelRelation
import model.DatasourceName

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
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 7, 2008
 * Time: 2:59:03 PM
 * To change this template use File | Settings | File Templates.
 */

Model.findByName("Developer")?.delete(flush:true);
Model.findByName("Employee")?.delete(flush:true);
Model.findByName("Person")?.delete(flush:true);
Model.findByName("Team")?.delete(flush:true);
Model.findByName("Task")?.delete(flush:true);
def rcmdbDatasource = DatasourceName.findByName("RCMDB");
if(rcmdbDatasource == null){
    rcmdbDatasource = new DatasourceName(name: "RCMDB");
    rcmdbDatasource.save();
}


def person = new Model(name:"Person");
def modelDatasource = new ModelDatasource(datasource:rcmdbDatasource, master:true);
def name = new ModelProperty(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
def bday = new ModelProperty(name:"bday", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
def keyMappings = [new ModelDatasourceKeyMapping(property:name, datasource:modelDatasource)]
person = constructModel(person, [name, bday], [modelDatasource], keyMappings);

def employee = new Model(name:"Employee", parentModel:person);
def dept = new ModelProperty(name:"dept", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
def salary = new ModelProperty(name:"salary", type:ModelProperty.numberType, defaultValue:1000, blank:false, lazy:false, propertyDatasource:modelDatasource);
employee = constructModel(employee, [dept, salary], [], []);

def developer = new Model(name:"Developer", parentModel:employee);
def language = new ModelProperty(name:"language", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
developer = constructModel(developer, [language], [], []);

def team = new Model(name:"Team");
modelDatasource = new ModelDatasource(datasource:rcmdbDatasource, master:true);
name = new ModelProperty(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
def maskot = new ModelProperty(name:"maskot", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
keyMappings = [new ModelDatasourceKeyMapping(property:name, datasource:modelDatasource)]
team = constructModel(team, [name, maskot], [modelDatasource], keyMappings);

def task = new Model(name:"Task");
modelDatasource = new ModelDatasource(datasource:rcmdbDatasource, master:true);
name = new ModelProperty(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
keyMappings = [new ModelDatasourceKeyMapping(property:name, datasource:modelDatasource)]
task = constructModel(task, [name], [modelDatasource], keyMappings);

createRelation(employee, employee, "prevEmp", "nextEmp", ModelRelation.ONE, ModelRelation.ONE);
//createRelation(employee, employee, "employees", "manager", ModelRelation.ONE, ModelRelation.MANY);
createRelation(employee, team, "manages", "managedBy", ModelRelation.ONE, ModelRelation.MANY);
createRelation(developer, task, "worksOn", "workedOnBy", ModelRelation.MANY, ModelRelation.MANY);


ModelGenerator.getInstance().generateModel(developer);

def constructModel(model, listOfProperties, listOfDatasources, listOfKeyMappings)
{
    model = model.save();
    listOfDatasources.each
    {
        it.model = model;
        it.save();
    }
    listOfProperties.each {
        it.model = model;
        it.save();
    }

    listOfKeyMappings.each
    {
        it.save();
    }
    listOfDatasources.each
    {
        it.refresh();
    }
    model.refresh();
    return model;
}

def createRelation(firstModel, secondModel, firstName, secondName, firstCar, secondCar){
    new ModelRelation(firstModel:firstModel, secondModel:secondModel, firstName:firstName, secondName:secondName, firstCardinality:firstCar, secondCardinality:secondCar).save();
    firstModel.refresh();
    secondModel.refresh();
}
