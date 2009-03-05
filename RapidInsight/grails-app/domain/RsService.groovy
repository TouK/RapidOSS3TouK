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
class RsService  extends RsGroup {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["relatedServiceTickets"];
    
    
    };
    static datasources = [:]

    
    String observedState ="";
    
    Long lastChangedAt =0;
    
    Long interval =0;
    
    Long consideredDownAt =0;
    
    Boolean hasHeartbeat =false;
    
    List relatedServiceTickets =[];
    org.springframework.validation.Errors errors ;
    
    
    static relations = [
    
        relatedServiceTickets:[type:RsTicket, reverseName:"relatedServices", isMany:true]
    
    ]
    
    static constraints={
    observedState(blank:true,nullable:true)
        
     lastChangedAt(nullable:true)
        
     interval(nullable:true)
        
     consideredDownAt(nullable:true)
        
     hasHeartbeat(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["relatedServiceTickets"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE










}
