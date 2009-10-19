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

class Link extends SmartsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["connectedTo", "connectedSystems", "errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = [:]

    
    String aa_AdminStatus ="";
    
    String aa_OperStatus ="";
    
    String aa_DisplayName ="";
    
    String zz_AdminStatus ="";
    
    String zz_OperStatus ="";
    
    String zz_DisplayName ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __dynamic_property_storage__ ;
    
    List connectedTo =[];
    
    List connectedSystems =[];
    

    static constraints={
    aa_AdminStatus(blank:true,nullable:true)
        
     aa_OperStatus(blank:true,nullable:true)
        
     aa_DisplayName(blank:true,nullable:true)
        
     zz_AdminStatus(blank:true,nullable:true)
        
     zz_OperStatus(blank:true,nullable:true)
        
     zz_DisplayName(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __dynamic_property_storage__(nullable:true)
        
     errors(nullable:true)
        
     
    }
    static relations = [connectedTo:[isMany:true, type:DeviceAdapter, reverseName:"connectedVia"],
            connectedSystems:[isMany:true, type:Device, reverseName:"connectedVia"]

    ]
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    //AUTO_GENERATED_CODE
}