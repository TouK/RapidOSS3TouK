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
package ui.map

import com.ifountain.core.domain.annotations.*;

class EdgeNode {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB":["keys":["mapName":["nameInDs":"mapName"], "username":["nameInDs":"username"], "from":["nameInDs":"from"], "to":["nameInDs":"to"]]]]

    String rsOwner = "p"
    String from ="";
    
    String to ="";
    
    String username ="";
    
    String mapName ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    

    static relations = [:]
    static constraints={
    from(blank:false,nullable:false)
        
     to(blank:false,nullable:false)
        
     username(blank:false,nullable:false)
        
     mapName(blank:false,nullable:false,key:["username", "from", "to"])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[from:$from, mapName:$mapName, to:$to, username:$username]";
    }
    
    //AUTO_GENERATED_CODE



    
}
