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

class DeviceAdapter extends DeviceComponent
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["realizedBy", "connectedVia", "maxSpeed", "adminStatus", "maxTransferUnit", "mode", "status", "duplexMode", "currentUtilization", "operStatus", "isFlapping", "deviceID", "peerSystemName", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String description ="";
    
    String type ="";
    
    String isManaged ="";
    
    String maxSpeed ="";
    
    String adminStatus ="";
    
    Long maxTransferUnit =0;
    
    String mode ="";
    
    String status ="";
    
    String duplexMode ="";
    
    Double currentUtilization =0;
    
    String operStatus ="";
    
    String isFlapping ="";
    
    String deviceID ="";
    
    String peerSystemName ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    Card realizedBy ;
    
    Link connectedVia ;
    

    static relations = [realizedBy:[isMany:false, type:Card, reverseName:"realises"],
        connectedVia:[isMany:false, type:Link, reverseName:"connectedTo"]

    ]
    static constraints={
    description(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     isManaged(blank:true,nullable:true)
        
     maxSpeed(blank:true,nullable:true)
        
     adminStatus(blank:true,nullable:true)
        
     maxTransferUnit(nullable:true)
        
     mode(blank:true,nullable:true)
        
     status(blank:true,nullable:true)
        
     duplexMode(blank:true,nullable:true)
        
     currentUtilization(nullable:true)
        
     operStatus(blank:true,nullable:true)
        
     isFlapping(blank:true,nullable:true)
        
     deviceID(blank:true,nullable:true)
        
     peerSystemName(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     realizedBy(nullable:true)
        
     connectedVia(nullable:true)
        
     
    }

    static propertyConfiguration= ["maxSpeed":["nameInDs":"MaxSpeed", "datasourceProperty":"smartDs", "lazy":true], "adminStatus":["nameInDs":"AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "maxTransferUnit":["nameInDs":"MaxTransferUnit", "datasourceProperty":"smartDs", "lazy":true], "mode":["nameInDs":"Mode", "datasourceProperty":"smartDs", "lazy":true], "status":["nameInDs":"Status", "datasourceProperty":"smartDs", "lazy":true], "duplexMode":["nameInDs":"DuplexMode", "datasourceProperty":"smartDs", "lazy":true], "currentUtilization":["nameInDs":"CurrentUtilization", "datasourceProperty":"smartDs", "lazy":true], "operStatus":["nameInDs":"OperStatus", "datasourceProperty":"smartDs", "lazy":true], "isFlapping":["nameInDs":"IsFlapping", "datasourceProperty":"smartDs", "lazy":true], "deviceID":["nameInDs":"DeviceID", "datasourceProperty":"smartDs", "lazy":true], "peerSystemName":["nameInDs":"PeerSystemName", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["maxSpeed", "adminStatus", "maxTransferUnit", "mode", "status", "duplexMode", "currentUtilization", "operStatus", "isFlapping", "deviceID", "peerSystemName", "errors", "__operation_class__", "__is_federated_properties_loaded__"];

    //AUTO_GENERATED_CODE
}