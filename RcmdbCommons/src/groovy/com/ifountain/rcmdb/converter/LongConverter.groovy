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
package com.ifountain.rcmdb.converter

import org.apache.commons.beanutils.Converter

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 10:57:51 AM
* To change this template use File | Settings | File Templates.
*/
class LongConverter implements Converter{
    org.apache.commons.beanutils.converters.LongConverter converter;
    public LongConverter()
    {
         converter = new org.apache.commons.beanutils.converters.LongConverter();        
    }
    public Object convert(Class aClass, Object o) {
        return converter.convert(aClass, o);
    }
    
}