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
/*
<Data>
	<Objects>
		<Object id=2001 modelName=Fiction isbn='isbn_Kar' title='Kar' fictionProp='fiction prop for Kar'/>
	</Objects>
	<Relations>
		<Relation fromObjectId='2001' fromModel='Book' toObjectId='2006' toModel='Author' name=myAuthor />
	</Relations>
</Data>
*/
import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.converter.RapidConvertUtils

idmapFilledDuringImportObjects = [:];

return 	importConfigData(web, "config.xml")

// Import Configuration data.
// NOTE: This method removes the existing config data, and imports them from the xml file.
def importConfigData(web, fname){
	def slurper = new XmlSlurper();
	def data = slurper.parse(fname);
	def idMap =[:];
	def objs = [];
	data.Objects.Object.each{obj->
		objs.add(obj.attributes());
	}
	def domClasses = web.grailsApplication.getDomainClasses().clazz.name;
	
	data.Models.Model.each{
		def modelName = it.@name.toString();
		if (domClasses.contains(modelName)){
			def model = web.grailsApplication.getDomainClass(modelName).clazz;
			model.removeAll();
		}
		else{
			logger.warn("Model <$modelName> does not exist in this version!")			
		}
	}
	
	def instances = [];
	objs.each{obj->
		def modelName = obj.modelName;
		if (!domClasses.contains(modelName)){
			logger.warn("Ignored object <${obj.objectId}> since its model <$modelName> does not exist in this version!")
		}
		else{
			def clazz = web.grailsApplication.getDomainClass(modelName).clazz;
			def instance = clazz.newInstance();
			def propList = clazz.getPropertiesList();
			def skip = false;
				
			if (modelName == "relation.Relation"){
				obj.objectId = idMap[obj.objectId];
				obj.reverseObjectId = idMap[obj.reverseObjectId];
				if (obj.objectId == null || obj.reverseObjectId == null){
					logger.warn("One or more objects for this relation in XML file ($fname) do not exist: Relation id: <${obj.id}>")
					skip = true;
				}
			}
			if (!skip){
				propList.each{prop->
					def propValFromXml = obj."$prop.name";
					if (prop.name == "id"){
						def oldIdFromXml = propValFromXml;
						propValFromXml = com.ifountain.rcmdb.domain.IdGenerator.getInstance().getNextId().toString();
						idMap.put(oldIdFromXml, propValFromXml);
					}
					if (prop!="modelName" && propValFromXml!=null && propValFromXml!=''){
						// code to change the prop type from string to internal type
						def fieldType = clazz.metaClass.getMetaProperty(prop.name).type;
						def converter = RapidConvertUtils.getInstance().lookup(fieldType);
						def propVal = converter.convert(fieldType, propValFromXml);
		 				instance.setProperty(prop.name, propVal, false);
					}
				}
		 		clazz.index(instance);
			}
		}
	}
}

// Import object properties from an xml file "as is"
// This can be used when a parent model is deleted or a parent model is added, 
// and no further processing is needed. 
def importObjects(web, fname){
	def slurper = new XmlSlurper();
	def data = slurper.parse(fname);
	idmapFilledDuringImportObjects = [:]
	data.Objects.Object.each{obj->
		def props = [:];
		obj.attributes().each{key, value->	
			props[key] = value;	
		}
		
		def model = web.grailsApplication.getDomainClass(props["modelName"]).clazz;
		def oldId = props.id
		props.remove("modelName");
		props.remove("id");
		def newObj = model.add(props)
		idmapFilledDuringImportObjects.put(oldId,newObj.id)
	}	
}

// Imports objects' properties from an xml file using the new property names. 
// changedPropNameMap parameter is assumed to have the old and new names of properties.
// If it is given as empty map, all props are imported as is. 
// ex: changedPropNameMap = ["surname":"lastname", "address":"addressLine1"]
// where keys in the map are old prop names in the xml file, values are the new prop names in the model.
def importRenamedProperties(web, Map changedPropNameMap, fname){
	idmapFilledDuringImportObjects = [:]
	def slurper = new XmlSlurper();
	def data = slurper.parse(fname);
	data.Objects.Object.each{obj->
		def props = [:];
		obj.attributes().each{key, value->	
			props[key] = value;	
		}
		changedPropNameMap.each{oldProp, newProp->
			def val = props[oldProp];
			if (val!=null){
				props.remove(oldProp);
				props.put(newProp, val);
			}
		}
		def model = web.grailsApplication.getDomainClass(props.modelName).clazz;
		id = props.id;
		props.remove("id");
		props.remove("modelName");
		def modifiedObj = model.add(props)
		idmapFilledDuringImportObjects.put(id,modifiedObj.id)
	}	
}

// Import relations from an xml file "as is"
def importRelations(web, fname){
	importRenamedRelations(web, [:], fname);
}

def importRenamedRelations(web, Map changedRelationNameMap, fname){
	def slurper = new XmlSlurper();
	def data = slurper.parse(fname);
	
	data.Relations.Relation.each{rel->
		def relations = [:];
		relName=rel.attributes().name
		if (changedRelationNameMap.containsKey(relName)){
			relName = changedRelationNameMap."$relName";
		}
		from=rel.attributes().fromObjectId;
		to=rel.attributes().toObjectId;
		if (idmapFilledDuringImportObjects.size()>0){
			from=idmapFilledDuringImportObjects[from];
			if (idmapFilledDuringImportObjects[to]!=null){
				to=idmapFilledDuringImportObjects[to]
			}
			else{
				to = Integer.parseInt(to,10)
			}
				
		}
		fromModelName=rel.attributes().fromModel
		toModelName=rel.attributes().toModel
		def fromModel = web.grailsApplication.getDomainClass(fromModelName).clazz;
		def toModel = web.grailsApplication.getDomainClass(toModelName).clazz;
		def toObj = toModel.get(id:to);
		def fromObj = fromModel.get(id:from);
		def relMap = [:];
		relMap.put(relName,toObj);
		fromObj.addRelation(relMap);
	}	
}

// Assume a property type is changed from string to number:
// ex: 
// Old values: Critical, Major, Minor, Warning, Normal, Unknown
// New values: 5,4,3,2,1,0
def importForTypeChangedProperty(web, changedProp, Map oldValueNewValueMap, fname){
	def slurper = new XmlSlurper();
	def data = slurper.parse(fname);
	data.Objects.Object.each{obj->
		def id;
		def oldValue = obj.attributes()."$changedProp";
		if (oldValue!=null){
			id = obj.attributes().id;
			def newValue = oldValueNewValueMap[oldValue];
			if (newValue!=null){
				def modelName = obj.attributes().modelName;
				def model = web.grailsApplication.getDomainClass(modelName).clazz;
				def myObj = model.get(id:id);
				myObj.update(changedProp:newValue);
			}
			else{
				logger.warn("The obj <$id> has a value <$oldValue> which can not be found in the old-new value map!");
			}
		}
		else{
			logger.warn("Skipping the update for the obj <$id>: It does not have the prop <$changedProp>");
		}
	}	
}

def importBothObjectsAndRelationsForAModelAndItsChildren(web, Map changedRelationNameMap, Map changedPropNameMap,fname){
	importRenamedProperties(web, changedPropNameMap, fname);
	importRenamedRelations(web, changedRelationNameMap, fname);
}

def importBothObjectsAndRelationsForAModelAndItsChildren(web,fname){
	importObjects(web, fname);
	importRelations(web, fname);
}
