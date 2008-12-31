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

class RsHistoricalEvent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]

    
    String name ="";
    
    String owner ="";
    
    Boolean acknowledged =false;
    
    Long severity =0;
    
    String source ="";
    
    Long createdAt =0;
    
    Long changedAt =0;
    
    Long clearedAt =0;
    
    String rsDatasource ="";
    
    Long willExpireAt =0;
    
    Long state =0;
    
    String elementName ="";
    
    Long elementId =0;
    
    Long count =1;
    
    Long id ;
    Long activeId ;

    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    
    static relations = [:]    
    
    static constraints={
    name(blank:true,nullable:true)
        
     owner(blank:true,nullable:true)
        
     acknowledged(nullable:true)
        
     severity(nullable:true)
        
     source(blank:true,nullable:true)
        
     createdAt(nullable:true)
        
     changedAt(nullable:true)
        
     clearedAt(nullable:true)
        
     rsDatasource(blank:true,nullable:true)
        
     willExpireAt(nullable:true)
        
     state(nullable:true)
        
     elementName(blank:true,nullable:true)
        
     elementId(nullable:true)
        
     count(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[id:${getProperty("id")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE


    
}
