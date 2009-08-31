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

class ModelDatasource {
    static searchable = {
        except=["model","keyMappings","datasource", "errors", "__operation_class__", "__is_federated_properties_loaded__"]
    };
    Long id;
    Long version;
    Date rsInsertedAt = new Date(0);
    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    DatasourceName datasource;
    Model model;
    String rsOwner = "p"
    List keyMappings = [];
    static relations = [
            datasource:[type:DatasourceName, reverseName:"modelDatasources", isMany:false],
            model:[type:Model, reverseName:"datasources", isMany:false],
            keyMappings:[type:ModelDatasourceKeyMapping, reverseName:"datasource", isMany:true],
    ]
    static cascaded = ["keyMappings":true]
    static constraints = {
         __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
        model(key:["datasource"], validator: {val, obj ->
            def error = null;
            def tempModel = val.parentModel;
            while(tempModel)
            {
                tempModel.datasources.each
                {
                    if(it.datasource.name == obj.datasource.name)
                    {
                        error = ['model.datasource.override', tempModel, it]
                        return;
                    }
                }
                tempModel = tempModel.parentModel;
            }
            return error;
        })
    }
    String toString(){
        return getProperty("datasource").name;  
    }
}
