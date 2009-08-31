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

import com.ifountain.rcmdb.util.RapidCMDBConstants;
class ModelDatasourceKeyMapping {
    static searchable = {
        except=["datasource", "property", "__operation_class__", "errors"]
    };

    Date rsInsertedAt = new Date(0);
    Date rsUpdatedAt  = new Date(0);
    ModelProperty property;
    String rsOwner = "p"
    ModelDatasource datasource;
    String nameInDatasource;
    org.springframework.validation.Errors errors ;
    Object __operation_class__;
    static relations = [
            datasource:[type:ModelDatasource, reverseName:"keyMappings", isMany:false],
            property:[type:ModelProperty, reverseName:"mappedKeys", isMany:false]
    ]
    static constraints = {
        property(key:['datasource'], nullable:true, validator:{val, obj ->
            if(val.propertyDatasource && val.propertyDatasource.datasource.name != RapidCMDBConstants.RCMDB){
                return ['model.keymapping.cannot.be.federated']
            }
        });
        nameInDatasource(nullable:true);
        __operation_class__(nullable:true);
        errors(nullable:true);
    }
    static transients = ["errors", "__operation_class__"];
    String toString(){
        return getProperty("property").name;
    }
}
