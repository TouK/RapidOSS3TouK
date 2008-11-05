def impUtility = new ImportUtility();

impUtility.importRelations(web,"exportRsNetworkAdaptorRelations.xml");

// Import objects as is
impUtility.importObjects(web, "exportNotifications.xml");

// Import property[ies] with their new name[s]
changedPropNameMap = ["surname":"lastname"];
impUtility.importRenamedProperties(web, changedPropNameMap, "exportAuthors.xml");

// Import relations as is
impUtility.importRelations(web, "");

// Import relation[s] with their new names
changedRelationNameMap = ["myPublisher":"publishedBy", "myAuthors":"writtenBy"];
impUtility.importRenamedRelations(web, changedRelationNameMap, "exportBooks.xml");
	
// Import a property whose type and therefore values changed 
changedProp = "severity";
oldValueNewValueMap = ["Critical":"5","Major":"4","Minor":"3","Normal":"2","Undetermined":"1"];
impUtility.importForTypeChangedProperty(web, changedProp, oldValueNewValueMap, "exportEvents.xml");

// Import some renamed properties and relations at the same time
changedPropNameMap = ["identifier":"isbn"];
changedRelationNameMap = ["myPublisher":"publishedBy", "myAuthors":"writtenBy"];
impUtility.importBothObjectsAndRelationsForAModelAndItsChildren(web, changedRelationNameMap, changedPropNameMap,"exportBooks.xml");
