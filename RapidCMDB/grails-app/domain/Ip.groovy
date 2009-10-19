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

class Ip extends DeviceComponent
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["hostedBy", "layeredOver", "errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = [:]

    
    String ipAddress ="";
    
    String netMask ="";
    
    String interfaceAdminStatus ="";
    
    String interfaceName ="";
    
    String interfaceOperStatus ="";
    
    String ipStatus ="";
    
    String interfaceKey ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __dynamic_property_storage__ ;
    
    Device hostedBy ;
    
    DeviceInterface layeredOver ;
    


    static constraints={
    ipAddress(blank:true,nullable:true)
        
     netMask(blank:true,nullable:true)
        
     interfaceAdminStatus(blank:true,nullable:true)
        
     interfaceName(blank:true,nullable:true)
        
     interfaceOperStatus(blank:true,nullable:true)
        
     ipStatus(blank:true,nullable:true)
        
     interfaceKey(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __dynamic_property_storage__(nullable:true)
        
     errors(nullable:true)
        
     hostedBy(nullable:true)
        
     layeredOver(nullable:true)
        
     
    }
    static relations = [hostedBy:[isMany:false, type:Device, reverseName:"hostsAccessPoints"],
            layeredOver:[isMany:false, type:DeviceInterface, reverseName:"underlying"]

    ]
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    //AUTO_GENERATED_CODE
}