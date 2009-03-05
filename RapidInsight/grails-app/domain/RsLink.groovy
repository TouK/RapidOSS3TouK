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

class RsLink  extends RsTopologyObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["connectedSystems"];
    
    
    };
    static datasources = [:]

    
    String a_ComputerSystemName ="";
    
    String a_Name ="";
    
    String z_ComputerSystemName ="";
    
    String z_Name ="";
    
    List connectedSystems =[];
    org.springframework.validation.Errors errors ;
    
    
    static relations = [
    
        connectedSystems:[type:RsComputerSystem, reverseName:"connectedVia", isMany:true]
    
    ]
    
    static constraints={
    a_ComputerSystemName(blank:true,nullable:true)
        
     a_Name(blank:true,nullable:true)
        
     z_ComputerSystemName(blank:true,nullable:true)
        
     z_Name(blank:true,nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["connectedSystems"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE


    
}
