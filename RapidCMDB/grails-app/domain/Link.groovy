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
        except = ["connectedTo", "connectedSystems", "aa_AdminStatus", "aa_OperStatus", "aa_DisplayName", "zz_AdminStatus", "zz_OperStatus", "zz_DisplayName", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
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
    
    Object __is_federated_properties_loaded__ ;
    
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
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }
    static relations = [connectedTo:[isMany:true, type:DeviceAdapter, reverseName:"connectedVia"],
            connectedSystems:[isMany:true, type:Device, reverseName:"connectedVia"]

    ]
    static propertyConfiguration= ["aa_AdminStatus":["nameInDs":"A_AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "aa_OperStatus":["nameInDs":"A_OperStatus", "datasourceProperty":"smartDs", "lazy":true], "aa_DisplayName":["nameInDs":"A_DisplayName", "datasourceProperty":"smartDs", "lazy":true], "zz_AdminStatus":["nameInDs":"Z_AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "zz_OperStatus":["nameInDs":"Z_OperStatus", "datasourceProperty":"smartDs", "lazy":true], "zz_DisplayName":["nameInDs":"Z_DisplayName", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["aa_AdminStatus", "aa_OperStatus", "aa_DisplayName", "zz_AdminStatus", "zz_OperStatus", "zz_DisplayName", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}