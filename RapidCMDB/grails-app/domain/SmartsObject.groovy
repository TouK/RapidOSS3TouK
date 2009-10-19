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

class SmartsObject 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["federatedProperty","errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]], "ds1":[mappedName:"ds1", "keys":["name":["nameInDs":"Name"]]]]

    
    String name ="";
    
    String creationClassName ="";
    
    String smartDs ="";
    
    String displayName ="";
    String federatedProperty ="";
    
    Long id ;
    
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __dynamic_property_storage__ ;
    
    static relations  =[:]
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     creationClassName(blank:true,nullable:true)
        
     smartDs(blank:true,nullable:true)
     federatedProperty(blank:true,nullable:true)
        
     displayName(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __dynamic_property_storage__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [federatedProperty:["nameInDs":"FederatedProperty", "datasource":"ds1", "lazy":false]]
    static transients = ["federatedProperty","errors", "__operation_class__", "__dynamic_property_storage__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name]";
    }

    public boolean equals(Object obj) {
        return obj.getProperty("id").longValue() == this.getProperty("id").longValue();
    }

    //AUTO_GENERATED_CODE
}