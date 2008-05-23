import connection.*
import datasource.*
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
	private properties = [:];
			
	ModelHelper(modelName){
		model = new Model(name:modelName);
	}
	
	ModelHelper(modelName, parentModelName){
		def parent = Model.findByName(parentModelName);
		model = new Model(name:modelName, parentModel:parent);
		
		def tempParent = parent;
		while (tempParent!=null){
			// find parent's datasources
			def allparentds = ModelDatasource.findAllByModel(tempParent);
			for (ds in allparentds){
				def dskeys = ModelDatasourceKeyMapping.findAllByDatasource(ds);
				def keys =[];
				for (key in dskeys){
					keys.add(["name":key.property.name, "nameInDs":key.nameInDatasource]);
				}	
				datasources.put(ds.datasource.name, ["modelDatasource":ds, "keys":keys]);	
			}
			
			// find parent's properties
			def allprops = ModelProperty.findAllByModel(tempParent);
			for (prop in allprops){
				properties.put(prop.name, prop);	
			}
			tempParent = tempParent.parentModel;
		}
	}
	
	def setDatasources(List dsList){
		model = model.save();
	    dsList.each{
			def modelDatasource = new ModelDatasource(datasource:it.datasource, master:it.master, model:model).save();
			datasources.put(it.datasource.name, ['modelDatasource':modelDatasource, 'keys':it.keys]);
	    }
	}
	
	def setProperties(List propList){
		model = model.save();		
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
				def dynamicProp	= ModelProperty.findByModelAndName(model,dynamicModelDsName);
				if (dynamicProp==null){
					dynamicProp	= properties[dynamicModelDsName];
				}
				else{
					throw exception("Dynamic datasource property can not be found!");
				}
				propProperties.remove('propertySpecifyingDatasource');
				propProperties.put('propertySpecifyingDatasource', dynamicProp);
				}
			}
			propProperties.put('model', model);
			new ModelProperty(propProperties).save();
		}
	}
	
	def setKeyMappings(){
		model = model.save();
		for (ds in datasources){
			ds.value.keys.each{
				def keyProp = ModelProperty.findByName(it.name);
				def nameInDs = it.nameInDs;
				if (nameInDs == null){
					nameInDs = it.name;
				}
				new ModelDatasourceKeyMapping(property:keyProp, nameInDatasource:nameInDs, datasource:ds.value.modelDatasource).save();
			}
		}
	}
	
	def setRelations(List relList){
		
	}	
	
	def constructModel()
	{
	    model = model.save();
	    model.refresh();
	    return model;
	}
	
	def createRelation(firstModel, secondModel, firstName, secondName, firstCar, secondCar){
	    new ModelRelation(firstModel:firstModel, secondModel:secondModel, firstName:firstName, secondName:secondName, firstCardinality:firstCar, secondCardinality:secondCar).save();
	    firstModel.refresh();
	    secondModel.refresh();
	}


}
 
