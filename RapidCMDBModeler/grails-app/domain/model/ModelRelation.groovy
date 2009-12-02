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
package model

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ModelRelation {
     static searchable = {
         except=["firstModel", "secondModel", "errors", "__operation_class__", "__dynamic_property_storage__"]
     };
     public static String ONE = "One";
     public static String MANY = "Many";
     Model firstModel;
    Long rsInsertedAt =0;
    Long rsUpdatedAt =0;
     String rsOwner = "p"
     Model secondModel;
     String firstName;
     String secondName;
     String firstCardinality;
     String secondCardinality;
     org.springframework.validation.Errors errors ;
     Object __operation_class__;
     Object __dynamic_property_storage__;

     static relations = [
            secondModel:[type:Model, reverseName:"toRelations", isMany:false],
            firstModel:[type:Model, reverseName:"fromRelations", isMany:false],
     ]

     static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

     static constraints = {
         firstCardinality(inList:[ONE, MANY]);
         errors(nullable:true);
         __operation_class__(nullable:true);
         __dynamic_property_storage__(nullable:true);
         secondCardinality(inList:[ONE, MANY]);
         firstName(blank:false, key:['firstModel', 'secondName','secondModel'], validator:{val, obj ->
            if(!val.matches(ConfigurationHolder.config.toProperties()["rapidcmdb.property.validname"])){
                return ['modelrelation.name.not.match'];
            }
            def invalidNames = ConfigurationHolder.config.flatten().get("rapidcmdb.invalid.names")
            if(invalidNames.contains(val.toLowerCase()))
            {
                return ['modelrelation.name.invalid'];
            }
        });
        secondName(blank:false, validator:{val, obj ->
            if(!val.matches(ConfigurationHolder.config.toProperties()["rapidcmdb.property.validname"])){
                return ['modelrelation.name.not.match'];
            }
            def invalidNames = ConfigurationHolder.config.flatten().get("rapidcmdb.invalid.names")
            if(invalidNames.contains(val.toLowerCase()))
            {
                return ['modelrelation.name.invalid'];
            }
        });
     }

     String toString(){
         return "$firstName";
     }
}
