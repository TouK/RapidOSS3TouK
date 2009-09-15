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
package com.ifountain.rcmdb.domain.property
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 10:18:49 AM
 * To change this template use File | Settings | File Templates.
 */
class DefaultDomainClassPropertyInterceptor implements DomainClassPropertyInterceptor{

    public void setDomainClassProperty(MetaClass domainMetaClass, Class domainClass, Object domainObject, String propertyName, Object value)
    {
        def metaProp = domainMetaClass.getMetaProperty(propertyName);
        if(metaProp != null)
        {
            metaProp.setProperty(domainObject, value);
        }
        else
        {
            throw new MissingPropertyException(propertyName, domainObject.class)
        }
    }

    public Object getDomainClassProperty(MetaClass domainMetaClass, Class domainClass, Object domainObject, String propertyName) {
        def metaProp = domainMetaClass.getMetaProperty(propertyName);
        if(metaProp != null)
        {
            return metaProp.getProperty(domainObject)
        }
        else
        {
            throw new MissingPropertyException(propertyName, domainObject.class)
        }
    }

}