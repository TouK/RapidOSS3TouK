import model.*
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
 * Date: May 23, 2008
 * Time: 10:59:07 AM
 * To change this template use File | Settings | File Templates.
 */

class ModelHelper{

	private Model model;
	private Model parent;
	private datasources = [:];
	private props = [:];
			
	ModelHelper(modelName){
		model = Model.add(name:modelName);
		if (model.hasErrors()) {
			throw new Exception(model.errors.toString());
		}
	}
	
	ModelHelper(modelName, parentModelName){

		def parent = Model.get(name:parentModelName);
		model = Model.add(name:modelName, parentModel:parent);
		if (model.hasErrors()) {
			throw new Exception(model.errors.toString());
		}
		def tempParent = parent;
		while (tempParent!=null){
			// find parent's datasources
			def allparentds = tempParent.datasources;
			for (ds in allparentds){
				def dskeys = ds.keyMappings; 
				def keys =[];
				for (key in dskeys){
					keys.add(["name":key.property.name, "nameInDs":key.nameInDatasource]);
				}	
				datasources.put(ds.datasource.name, ["modelDatasource":ds, "keys":keys]);	
			}
			
			// find parent's properties
			def allprops = tempParent.modelProperties; 
			for (prop in allprops){
				props.put(prop.name, prop);	
			}
			tempParent = tempParent.parentModel;
		}
	}
	
	def setDatasources(List dsList){
	    dsList.each{
			def modelDatasource = ModelDatasource.add(datasource:it.datasource, master:it.master, model:model);
			if (modelDatasource.hasErrors()) {
				throw new Exception(modelDatasource.errors.toString());
			}
			datasources.put(it.datasource.name, ['modelDatasource':modelDatasource, 'keys':it.keys]);
	    }
	}
	
	def setProps(List propList){
		propList.each{
			def propProperties = [:];
			propProperties.putAll(it);
			def modelDsName = propProperties.propertyDatasource;
			if (modelDsName != null){
				def modelDs = datasources[modelDsName].modelDatasource;
				propProperties.remove('propertyDatasource');
				propProperties.put('propertyDatasource', modelDs);
			}
			else{
				def dynamicModelDsName = propProperties.propertySpecifyingDatasource;
				if (dynamicModelDsName != null){
					def dynamicProp = ModelProperty.list().find{it.model.name == model.name && it.name == dynamicModelDsName};
					if (dynamicProp==null){
						dynamicProp	= props[dynamicModelDsName];
						if (dynamicProp == null){
							throw new Exception("Dynamic datasource property can not be found!");	
						}
					}
					propProperties.remove('propertySpecifyingDatasource');
					propProperties.put('propertySpecifyingDatasource', dynamicProp);
				}
			}
			propProperties.put('model', model);
			def property = ModelProperty.add(propProperties);
			if (property.hasErrors()) {
				throw new Exception(property.errors.toString());
			}
			else{
				props.put(property.name, property);		
			}
		}
	}
	
	def setKeyMappings(){
		for (ds in datasources){
			ds.value.keys.each{			
				def prop = it;
				def keyProp = props[it.name]; //ModelProperty.list().find{it.model.name == model.name && it.name == prop.name};
				if (keyProp == null) {
					throw new Exception("Datasource key must be a model property!");
				}
				def nameInDs = prop.nameInDs;
				if (nameInDs == null){
					nameInDs = prop.name;
				}
				def result = ModelDatasourceKeyMapping.add(property:keyProp, nameInDatasource:nameInDs, datasource:ds.value.modelDatasource);
				if (result.hasErrors()) {
					throw new Exception(result.errors.toString());
				}
			}
		}
	}
	
	def createRelation(secondModel, firstName, secondName, firstCar, secondCar){
	    def result = ModelRelation.add(firstModel:model, secondModel:secondModel, firstName:firstName, secondName:secondName, firstCardinality:firstCar, secondCardinality:secondCar);
	    if (result.hasErrors()) {
			throw new Exception(result.errors.toString());
		}
	}
}
 
