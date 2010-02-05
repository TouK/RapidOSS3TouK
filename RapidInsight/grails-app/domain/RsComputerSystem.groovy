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

class RsComputerSystem  extends RsTopologyObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "connectedVia", "hostsServices"];
    
    
    };
    static datasources = [:]

    
    String location ="";
    
    String geocodes ="";
    
    String model ="";
    
    String osVersion ="";
    
    String primaryOwnerContact ="";
    
    String primaryOwnerName ="";
    
    String readCommunity ="";
    
    String snmpAddress ="";
    
    String systemName ="";
    
    String systemObjectID ="";
    
    String vendor ="";
    
    org.springframework.validation.Errors errors ;
    
    List connectedVia =[];
    
    List hostsServices =[];
    
    
    static relations = [
    
        connectedVia:[type:RsLink, reverseName:"connectedSystems", isMany:true]
    
        ,hostsServices:[type:RsApplication, reverseName:"hostedBy", isMany:true]
    
    ]
    
    static constraints={
    location(blank:true,nullable:true)
        
     geocodes(blank:true,nullable:true)
        
     model(blank:true,nullable:true)
        
     osVersion(blank:true,nullable:true)
        
     primaryOwnerContact(blank:true,nullable:true)
        
     primaryOwnerName(blank:true,nullable:true)
        
     readCommunity(blank:true,nullable:true)
        
     snmpAddress(blank:true,nullable:true)
        
     systemName(blank:true,nullable:true)
        
     systemObjectID(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "connectedVia", "hostsServices"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE




    
}
