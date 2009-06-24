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

class Device extends SmartsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["federatedProperty", "connectedVia",  "hostsAccessPoints", "composedOf", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String location ="";
    
    String model ="";
    
    String snmpReadCommunity ="";
    
    String vendor ="";
    
    String discoveredLastAt ="0";
    
    String description ="";
    
    String discoveryErrorInfo ="";
    
    String discoveryTime ="";
    

    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List connectedVia =[];
    
    List hostsAccessPoints =[];
    
    List composedOf =[];
    static relations = [connectedVia:[isMany:true, type:Link, reverseName:"connectedSystems"],
        hostsAccessPoints:[isMany:true, type:Ip, reverseName:"hostedBy"],
        composedOf:[isMany:true, type:DeviceComponent, reverseName:"partOf"]

    ]
    static constraints={
    location(blank:true,nullable:true)
        
     model(blank:true,nullable:true)
        
     snmpReadCommunity(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     discoveredLastAt(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
     

     discoveryErrorInfo(blank:true,nullable:true)
        
     discoveryTime(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }
    
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}