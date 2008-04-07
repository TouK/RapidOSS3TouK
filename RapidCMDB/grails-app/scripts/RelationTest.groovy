import model.Model
import datasource.BaseDatasource
import model.ModelDatasource
import model.ModelProperty
import model.ModelDatasourceKeyMapping
import com.ifountain.domain.ModelGenerator
import model.ModelRelation

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
 * Time: 10:59:07 AM
 * To change this template use File | Settings | File Templates.
 */
Model.list().each{
    it.parentModel = null;
    it.save(flush: true);
}
Model.list()*.delete(flush:true);
def rcmdbDatasource = BaseDatasource.findByName("RCMDB");

def smartsObject = Model.create(name:"SmartsObject");
def modelDatasource = ModelDatasource.create(datasource:rcmdbDatasource, master:true);
def creationClassName = ModelProperty.create(name:"creationClassName", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
def name = ModelProperty.create(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
def keyMappings = [ModelDatasourceKeyMapping.create(property:creationClassName, datasource:modelDatasource),
                    ModelDatasourceKeyMapping.create(property:name, datasource:modelDatasource)]
smartsObject = constructModel(smartsObject, [creationClassName, name], [modelDatasource], keyMappings);

def redundancyGroup = Model.create(name:"RedundancyGroup");
modelDatasource = ModelDatasource.create(datasource:rcmdbDatasource, master:true);
creationClassName = ModelProperty.create(name:"creationClassName", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
name = ModelProperty.create(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
keyMappings = [ModelDatasourceKeyMapping.create(property:creationClassName, datasource:modelDatasource),
                    ModelDatasourceKeyMapping.create(property:name, datasource:modelDatasource)]
redundancyGroup = constructModel(redundancyGroup, [creationClassName, name], [modelDatasource], keyMappings);


def device = Model.create(name:"Device", parentModel:smartsObject);
constructModel(device, [], [], []);
def link = Model.create(name:"Link", parentModel:smartsObject);
constructModel(link, [], [], []);
def deviceComponent = Model.create(name:"DeviceComponent", parentModel:smartsObject);
constructModel(deviceComponent, [], [], []);
def deviceAdapter = Model.create(name:"DeviceAdapter", parentModel:deviceComponent);
constructModel(deviceAdapter, [], [], []);
def ip = Model.create(name:"IP", parentModel:deviceComponent);
constructModel(ip, [], [], []);
def deviceInterface = Model.create(name:"DeviceInterface", parentModel:deviceAdapter);
constructModel(deviceInterface, [], [], []);
def port = Model.create(name:"Port", parentModel:deviceAdapter);
constructModel(port, [], [], []);
def card = Model.create(name:"Card", parentModel:deviceComponent);
constructModel(card, [], [], []);

createRelation(smartsObject, redundancyGroup, "memberOf", "consistsOf", ModelRelation.MANY, ModelRelation.ONE);
createRelation(device, deviceComponent, "composedOf", "partOf", ModelRelation.ONE, ModelRelation.MANY);
createRelation(device, link, "connectedVia", "connectedSystems", ModelRelation.MANY, ModelRelation.MANY);
createRelation(link, deviceAdapter, "connectedTo", "connectedVia", ModelRelation.ONE, ModelRelation.MANY);
createRelation(deviceAdapter, card, "realizedBy", "realises", ModelRelation.MANY, ModelRelation.ONE);
createRelation(device, ip, "hostsAccessPoints", "hostedBy", ModelRelation.ONE, ModelRelation.MANY);
createRelation(deviceInterface, ip, "underlying", "layeredOver", ModelRelation.ONE, ModelRelation.ONE);

ModelGenerator.getInstance().generateModel(deviceInterface);


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
    model.refresh();
    return model;
}

def createRelation(firstModel, secondModel, firstName, secondName, firstCar, secondCar){
    new ModelRelation(firstModel:firstModel, secondModel:secondModel, firstName:firstName, secondName:secondName, firstCardinality:firstCar, secondCardinality:secondCar).save();
    firstModel.refresh();
    secondModel.refresh();
}