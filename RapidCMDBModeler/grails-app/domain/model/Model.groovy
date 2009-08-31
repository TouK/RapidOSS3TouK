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
import com.ifountain.compass.CompositeDirectoryWrapperProvider

class Model {
    static searchable = {
        except = ["fromRelations", "toRelations", "modelProperties", "datasources", "parentModel", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    Long id;
    Long version;
    Date rsInsertedAt = new Date(0);
    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    String name;
    String indexName = "";
    String storageType = CompositeDirectoryWrapperProvider.FILE_DIR_TYPE;
    String rsOwner = "p"
    Boolean resourcesWillBeGenerated = false;
    Model parentModel;
    List modelProperties = [];
    List datasources = [];
    List fromRelations = [];
    List toRelations = [];
    static relations = [
            parentModel:[type:Model, isMany:false],
            modelProperties:[type:ModelProperty, reverseName:"model", isMany:true],
            datasources:[type:ModelDatasource, reverseName:"model", isMany:true],
            fromRelations:[type:ModelRelation, reverseName:"firstModel", isMany:true],
            toRelations:[type:ModelRelation, reverseName:"secondModel", isMany:true],
    ]
    static cascaded = ["datasources":true, "fromRelations":true, "toRelations":true, modelProperties:true]
    static constraints = {
        name(blank:false, key:[], validator:{val, obj ->
            def firstChar = val.charAt(0);
            if(!(firstChar >= 65 && firstChar <= 90)){
                return ['model.name.lowercased'];
            }
            def invalidNames = ConfigurationHolder.config.flatten().get("rapidcmdb.invalid.names")
            if(invalidNames.contains(val.toLowerCase()))
            {
                return ['model.name.invalid'];                
            }
        });
        parentModel(nullable:true);
        resourcesWillBeGenerated(nullable:true);
        storageType(inList:[CompositeDirectoryWrapperProvider.FILE_DIR_TYPE, CompositeDirectoryWrapperProvider.RAM_DIR_TYPE, CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE]);
         __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
    }
//
//    def getOperations()
//    {
//        def operations = [];
//        if(name)
//        {
//            Class cls = ApplicationHolder.application.getClassForName(name)
//            if(cls)
//            {
//                def methods = cls.getDeclaredMethods();
//                methods.each{domainMethod->
//                    def annotation = domainMethod.getAnnotation(CmdbOperation.class)
//                    if(annotation)
//                    {
//                        operations += [name:domainMethod.getName(), description:annotation.description()];
//                        return;
//                    }
//                }
//            }
//        }
//        return operations;
//    }
        
    String toString(){
        return "$name";
    }

}
