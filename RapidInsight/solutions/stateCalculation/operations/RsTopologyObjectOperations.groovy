import application.RapidApplication
import com.ifountain.rcmdb.domain.util.DomainClassUtils

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
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111NOTSET307
* USA.
*/
public class RsTopologyObjectOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    static def VISIBLY_EXCLUDED_LOCAL_PROPS=["id","rsDatasource"];
    static def VISIBLE_EXCLUDED_LOCAL_PROPS_PER_CLASS=[
        "RsComputerSystem":[],
        "RsGroup":[],
        "RsLink":[]
    ];

    int getState()
    {
        return RapidApplication.getUtility("StateCalculator").getObjectState(this.domainObject);
    }
    def currentState()
    {
        return RapidApplication.getUtility("StateCalculator").loadObjectState(this.domainObject);
    }
    
    def beforeInsert(){
        RapidApplication.getUtility("ObjectProcessor").objectInBeforeInsert(this.domainObject);
	}
	def beforeUpdate(params)
    {
        RapidApplication.getUtility("ObjectProcessor").objectInBeforeUpdate(this.domainObject,params);
    }
	def afterInsert(){
        RapidApplication.getUtility("ObjectProcessor").objectIsAdded(this.domainObject);
    }
    def afterUpdate(params){
        RapidApplication.getUtility("ObjectProcessor").objectIsUpdated(this.domainObject,params);
    }
    def afterDelete()
    {
        RapidApplication.getUtility("ObjectProcessor").objectIsDeleted(this.domainObject);
    }


    public def retrieveVisibleProperties()
    {
        //populate Local Data
        def _errors="";
        String className = this.domainObject.getClass().getName();
        def allProperties = this.domainObject.getPropertiesList();
        def objectRelationNames = DomainClassUtils.getRelations(className);

        def objectProperties=[:];
        def objectPropertyHasErrors=[:];

        def VISIBLY_EXCLUDED_LOCAL_PROPS_FOR_CLASS=VISIBLE_EXCLUDED_LOCAL_PROPS_PER_CLASS[className];
        if(VISIBLY_EXCLUDED_LOCAL_PROPS_FOR_CLASS==null)
        {
           VISIBLY_EXCLUDED_LOCAL_PROPS_FOR_CLASS=[];
        }

        allProperties.each{ property ->
            def propertyName=property.name;
            if (!VISIBLY_EXCLUDED_LOCAL_PROPS.contains(propertyName) && !VISIBLY_EXCLUDED_LOCAL_PROPS_FOR_CLASS.contains(propertyName)) {
                if(!objectRelationNames.containsKey(propertyName))
                {
                      objectProperties[propertyName]=this.domainObject[propertyName];
                      objectPropertyHasErrors[propertyName]=domainObject.hasErrors(propertyName);
                }
                else
                {
                     def relatedObjects = this.domainObject.getRelatedModelPropertyValues(propertyName, ["className", "name"]);
                     def sortedRelatedObjects = relatedObjects.sort {"${it.className}${it.name}"};
                     objectProperties[propertyName]=sortedRelatedObjects;
                }
            }
        }

        //Merge Object Properties with External/Other Properties
        def mergedProperties=[:];
        if(_errors)
            mergedProperties._errors=_errors;

        /*
           mergedProperties.externalProp1="externalvalue1";
           mergedProperties.externalProp2="externalvalue2";
         */

        //traverse through object properties, if not exist in mergedProperties add it
        objectProperties.keySet().each{  propertyName ->
            if(!mergedProperties.containsKey(propertyName))
            {
                mergedProperties[propertyName]=objectProperties[propertyName];
            }
        }

        mergedProperties._objectPropertyHasErrors=objectPropertyHasErrors;
        return mergedProperties;
    }
}

