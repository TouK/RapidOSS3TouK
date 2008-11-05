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

// Import object properties from an xml file "as is"
// This can be used when a parent model is deleted or a parent model is added, 
// and no further processing is needed. 
def importObjects(web, fname){
	def slurper = new XmlSlurper();
	def data = slurper.parse(fname);
	data.Objects.Object.each{obj->
		def props = [:];
		obj.attributes().each{key, value->	
			props[key] = value;	
		}
		
		def model = web.grailsApplication.getDomainClass(props["modelName"]).clazz;
		props.remove("modelName");
		props.remove("id");
		model.add(props);
	}	
}

// Imports objects' properties from an xml file using the new property names. 
// changedPropNameMap parameter is assumed to have the old and new names of properties.
// If it is given as empty map, all props are imported as is. 
// ex: changedPropNameMap = ["surname":"lastname", "address":"addressLine1"]
// where keys in the map are old prop names in the xml file, values are the new prop names in the model.
def importRenamedProperties(web, Map changedPropNameMap, fname){
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
		props.remove(id);
		props.remove("modelName");
		def updatedObj = model.get(id:id);
		updatedObj.update(props);
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
		from=rel.attributes().fromObjectIdentifier
		to=rel.attributes().toObjectIdentifier
		fromModelName=rel.attributes().fromModel
		toModelName=rel.attributes().toModel
		def fromModel = web.grailsApplication.getDomainClass(fromModelName).clazz;
		def toModel = web.grailsApplication.getDomainClass(toModelName).clazz;
		def toObj = toModel.get(id:to);
		def fromObj = fromModel.get(id:from);
		fromObj.addRelation(relName:toObj);
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
				println "The obj <$id> has a value <$oldValue> which can not be found in the old-new value map!"
			}
		}
		else{
			println "Skipping the update for the obj <$id>: It does not have the prop <$changedProp>"
		}
	}	
}

def importBothObjectsAndRelationsForAModelAndItsChildren(web, Map changedRelationNameMap, Map changedPropNameMap,fname){
	importRenamedProperties(web, changedPropNameMap, fname);
	importRenamedRelations(web, changedRelationNameMap, fname);
}
