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
package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.property.FederatedPropertyManager

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 20, 2008
 * Time: 4:50:27 PM
 * To change this template use File | Settings | File Templates.
 */
class KeySetMethod {
    List keys;
    public KeySetMethod(GrailsDomainClass dc, FederatedPropertyManager manager) {
        keys = [];
        def getPropListMethod = new GetPropertiesMethod(dc, manager);
        getPropListMethod.allDomainClassProperties.each{RapidDomainClassProperty prop->
            if(prop.isKey)
            {
                keys.add(prop);
            }
        }
        keys = Collections.unmodifiableList(keys);
    }
}