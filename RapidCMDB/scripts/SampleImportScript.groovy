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
