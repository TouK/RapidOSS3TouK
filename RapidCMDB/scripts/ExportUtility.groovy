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

def exportAllData(web,fname){
	def writer = new StringWriter();
	def builder = new MarkupBuilder(writer);
	
	builder.Data{
		def domClasses = web.grailsApplication.getDomainClasses();
		def objs = [];
		def relations = [];
		domClasses.each{
			def modelName = it.clazz.name;
			if (modelName.indexOf('.')==-1 && it.clazz.superclass.getName()=="java.lang.Object"){
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
			def propMap = obj.asMap();
			propMap.remove('__operation_class__');
			propMap.remove('__is_federated_properties_loaded__');
			propMap.remove('errors');
			propMap.put("modelName", obj.getClass().getName());
			propMap.put("id", obj.id);
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
					println "WARNING: A property <$prop>, which is not in the model, is tried to be exported for object id: ${obj.id}";
				}
			}	
			objList.add(newPropMap);
		}
	}
}

def prepareRelationsForExport(web, modelName, relationData){
	def relations = [:];
	def model = web.grailsApplication.getDomainClass(modelName).clazz;
// 	def children = web.grailsApplication.getDomainClass(modelName).subClasses;
	traverseChildModel(web, model, relations);
	def objs = model.list();
	objs.each{obj->
		def fromObjClass = obj.getClass().getName(); 
		def fromObjId = obj.id;
		def objRelations = relations.get(fromObjClass);
		objRelations.each{relation->
			def toObj = obj."$relation";
			if (toObj!=null){
				def toObjClass = toObj.getClass().getName(); 
				def toObjId ;
				if (toObjClass == "java.util.ArrayList"){
					toObj.each{manyRelationObj->
						toObjClass = manyRelationObj.getClass().getName();
						toObjId = manyRelationObj.id;	
						relationData.add([fromObjectIdentifier:fromObjId, fromModel:fromObjClass , toModel:toObjClass , toObjectIdentifier:toObjId, name:relation]);
					}
				}
				else{						
					toObjId = toObj.id;
					relationData.add([fromObjectIdentifier:fromObjId, fromModel:fromObjClass , toModel:toObjClass , toObjectIdentifier:toObjId, name:relation]);
				}
			}
		}
	}	
}

def traverseChildModel(web, children,relations){
	children.each{childModel->
		def childName = childModel.name;
		def props = web.grailsApplication.getDomainClass(childName).clazz.getPropertiesList();
		props.each{
			if (it.isRelation){
				def relationList = relations.get(childName);
				if (relationList!=null){
					relationList.add(it.name);
					relations.put(childName,relationList);
				} else{
					relations.put(childName,[it.name]);
				}
			}	
		}
		traverseChildModel(web, web.grailsApplication.getDomainClass(childModel.name).subClasses, relations);	
	}
	
}

def writeToAnXml(strXml, fname){
	def fw = new FileWriter(new File(fname));
	fw.write(strXml);
	fw.close();	
}