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

import com.ifountain.core.domain.annotations.*;

class Team {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["managedBy", "errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String maskot ="";
    
    String name ="";

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;
    Employee managedBy ;
    org.springframework.validation.Errors errors ;
    Object __operation_class__;
    Object __dynamic_property_storage__;

    static relations = [managedBy:[isMany:false, type:Employee, reverseName:"manages"]
    ]
    static constraints={
    maskot(blank:true,nullable:true)
        
     name(blank:false,nullable:false,key:[])
        
     managedBy(nullable:true)
     errors(nullable:true)   
     __operation_class__(nullable:true)

    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name]";
    }

    public boolean equals(Object obj) {
        return obj.getProperty("id").longValue() == this.getProperty("id").longValue();
    }
    
    //AUTO_GENERATED_CODE
    
}
