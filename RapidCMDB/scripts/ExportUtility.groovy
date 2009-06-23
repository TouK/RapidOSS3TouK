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
		<Relation fromObjectIdentifier='2001' fromModel='Book' toObjectIdentifier='2006' toModel='Author' name=myAuthor />
	</Relations>
</Data>
*/
import groovy.xml.MarkupBuilder

def exportConfigData(web, fname){
	def excludeList = ["application.ObjectId","relation.Relation"]; //"relation.Relation" will be exported at the end
    
	def writer = new StringWriter();
	def builder = new MarkupBuilder(writer);
	builder.Data{
		def domClasses = web.grailsApplication.getDomainClasses();
		def objs = [];
		def modelNames = [];
		
		domClasses.each{
			def modelName = it.clazz.name;
			def model = web.grailsApplication.getDomainClass(modelName).clazz;
			if (modelName.indexOf('.')>-1 && model.superclass.name == Object.name && !excludeList.contains(modelName)){
				modelNames.add(modelName);
				preparePropsForExport(web, modelName, objs);
			}
		}
		
		// Add relation.Relation instances by removing the relations between user models
		def ids = objs.id;
		def model = web.grailsApplication.getDomainClass("relation.Relation").clazz;
		def instances = model.list();
		def props = web.grailsApplication.getDomainClass(objModelName).clazz.getNonFederatedPropertyList();
		if (instances.size()>0){
			instances.each{obj->
				if (ids.contains(obj.objectId) && ids.contains(obj.reverseObjectId)){
					def objModelName = obj.getClass().getName();
					def propValue;
					def propMap = [:];
					props.each{prop->
                        propValue = obj."$prop.name";
                        propMap.put(prop.name, propValue);
					}
					propMap.id = obj.id;
					propMap.modelName = objModelName;
					objs.add(propMap);
				}
			}
		}
	
		builder.Models{
			modelNames.each{
				builder.Model(name:it);
			}
		}
		
		builder.Objects{
			objs.each{obj->
				builder.Object(obj);
			}
		}
	}
	
	def strXml = writer.toString();
	writeToAnXml(strXml, fname);
	return strXml;	
}

def exportAllData(web,fname){
	def writer = new StringWriter();
	def builder = new MarkupBuilder(writer);
	
	builder.Data{
		def domClasses = web.grailsApplication.getDomainClasses();
		def objs = [];
		def relations = [];
		domClasses.each{
			def modelName = it.clazz.name;
			if (modelName.indexOf('.')==-1 && it.clazz.superclass.name == Object.name){ // getName()=="java.lang.Object" 
				preparePropsForExport(web, modelName, objs);
				prepareRelationsForExport(web, modelName, relations);
			}
		}
		builder.Objects{
			objs.each{obj->
				builder.Object(obj);
			}
		}
		
		builder.Relations{
			relations.each{rel->
				builder.Relation(rel);
			}
		}
	}
	
	def strXml = writer.toString();
	writeToAnXml(strXml, fname);
	return strXml;	
}

def exportBothPropertiesAndRelationsForAModelAndItsChildren(web, modelName,fname){
	def writer = new StringWriter();
	def builder = new MarkupBuilder(writer);
	
	builder.Data{
		def objs = [];
		def relations = [];
		preparePropsForExport(web, modelName, objs);
		prepareRelationsForExport(web, modelName, relations);
		builder.Objects{
			objs.each{obj->
				builder.Object(obj);
			}
		}
		builder.Relations{
			relations.each{rel->
				builder.Relation(rel);
			}
		}
	}
	
	def strXml = writer.toString();
	writeToAnXml(strXml, fname);
	return strXml;
		
}

def exportPropertiesForAModelAndItsChildren(web, modelName,fname){
	def writer = new StringWriter();
	def builder = new MarkupBuilder(writer);
	
	builder.Data{
		def objs = [];
		preparePropsForExport(web, modelName, objs);
		builder.Objects{
			objs.each{obj->
				builder.Object(obj);
			}
		}
	}
	
	def strXml = writer.toString();
	writeToAnXml(strXml, fname);
	return strXml;
		
}

def exportRelationsForAModelAndItsChildren(web, modelName,fname){
	def writer = new StringWriter();
	def builder = new MarkupBuilder(writer);
	
	builder.Data{
		def relations = [];
		prepareRelationsForExport(web, modelName, relations);
		builder.Relations{
			relations.each{rel->
				builder.Relation(rel);
			}
		}
	}
	
	def strXml = writer.toString();
	writeToAnXml(strXml, fname);
	return strXml;
		
}

def exportIdsAndSelectedPropertiesForAModelAndItsChildren(web, modelName, propList, fname){
	def writer = new StringWriter();
	def builder = new MarkupBuilder(writer);
	
	builder.Data{
		def objs = [];
		def relations = [];
		prepareSelectedPropsForExport(web, modelName, propList, objs);
		builder.Objects{
			objs.each{obj->
				builder.Object(obj);
			}
		}
	}
	
	def strXml = writer.toString();
	writeToAnXml(strXml, fname);
	return strXml;
}

def preparePropsForExport(web, modelName, objList){
	def model = web.grailsApplication.getDomainClass(modelName).clazz;
	def objs = model.list();
	if (objs.size()>0){
		objs.each{obj->
			def objModelName = obj.getClass().getName();
			def props = obj.getNonFederatedPropertyList();
			def propMap = [:];
			props.each{prop->
                def propValue = obj."${prop.name}";
                propMap.put(prop.name, propValue);
			}
			propMap.id = obj.id;
			propMap.modelName = objModelName;
			objList.add(propMap);
		}
	}
}

def preparePropsAndKeyRelationsAsPropForExport(web, modelName, objList){
	def model = web.grailsApplication.getDomainClass(modelName).clazz;
	def objs = model.list();
	if (objs.size()>0){
		objs.each{obj->
			def objModelName = obj.getClass().getName();
			def props = obj.getPropertiesList();
			def propValue;
			def propMap = [:];
			props.each{prop->
				def propName = prop.name;
				propValue = obj."$propName";
				if (prop.isRelation){
					if (prop.isKey){
						propValue = propValue.id;
						propMap.put(propName,propValue);
					}
				}
				else if (!prop.isOperationProperty && !prop.isFederated){
				    propMap.put(propName,propValue);
				}
			}
			propMap.id = obj.id;
			propMap.modelName = objModelName;
			objList.add(propMap);
		}
	}
}

def preparePropsAndRelationsAsPropForExport(web, modelName, objList){
	def model = web.grailsApplication.getDomainClass(modelName).clazz;
	def objs = model.list();
	if (objs.size()>0){
		objs.each{obj->
			def objModelName = obj.getClass().getName();
			def props = obj.getPropertiesList();
			def propMap = [:];
			props.each{prop->
			    if(!prop.isOperationProperty && !prop.isFederated)
                {
                    def propName = prop.name;
                    def propValue = obj."$propName";
                    if (prop.isRelation){
                        if (propValue.getClass().getName() == "java.util.ArrayList"){
                            def tmp = "";
                            propValue.each{
                                tmp = it.id+" "+tmp;
                            }
                            propValue = tmp;
                        }
                        else{
                            if (propValue!=null){
                                propValue = propValue.id;
                            }
                        }
                    }
                    propMap.put(propName,propValue);
                }
			}
			propMap.id = obj.id;
			propMap.modelName = objModelName;
			objList.add(propMap);
		}
	}
}

def prepareSelectedPropsForExport(web, modelName, propList, objList){
	def model = web.grailsApplication.getDomainClass(modelName).clazz;
	def objs = model.list();
	if (objs.size()>0){
		objs.each{obj->
			def propMap = obj.asMap();
			def newPropMap = [:];
			newPropMap.id = obj.id;
			newPropMap.modelName = obj.getClass().getName();
			propList.each{prop->
				if (propMap.containsKey(prop)){
					newPropMap.put(prop, propMap[prop]);
				}
				else{
					logger.warn("WARNING: A property <$prop>, which is not in the model, is tried to be exported for object id: ${obj.id}");
				}
			}	
			objList.add(newPropMap);
		}
	}
}

def prepareRelationsForExport(web, modelName, relationData){
	def model = web.grailsApplication.getDomainClass(modelName).clazz;
	def objs = model.list();
	if (objs.size()>0){
		objs.each{obj->
			def fromObjClass = obj.getClass().getName(); 
			def fromObjId = obj.id; 
			def props = obj.getRelationPropertyList();
			props.each{prop->
                def relation = prop.name;
                def toObj = obj."$relation";
                if (toObj!=null){
                    def toObjClass = toObj.getClass().getName();
                    def toObjId ;
                    if (toObjClass == "java.util.ArrayList"){
                        toObj.each{manyRelationObj->
                            toObjClass = manyRelationObj.getClass().getName();
                            toObjId = manyRelationObj.id;
                            relationData.add([fromObjectId:fromObjId, fromModel:fromObjClass, toModel:toObjClass , toObjectId:toObjId, name:relation]);
                        }
                    }
                    else{
                        toObjId = toObj.id;
                        relationData.add([fromObjectId:fromObjId, fromModel:fromObjClass, toModel:toObjClass , toObjectId:toObjId, name:relation]);
                    }
                }
			}
		}
	}
}

def writeToAnXml(strXml, fname){
	def fw = new FileWriter(new File(fname));
	fw.write(strXml);
	fw.close();	
}