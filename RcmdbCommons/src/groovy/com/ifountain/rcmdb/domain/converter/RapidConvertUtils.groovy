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
package com.ifountain.rcmdb.domain.converter

import org.apache.commons.beanutils.ConvertUtilsBean
import org.apache.commons.beanutils.Converter

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 2:35:48 PM
* To change this template use File | Settings | File Templates.
*/
class RapidConvertUtils extends ConvertUtilsBean{
    private static RapidConvertUtils convertUtils;
    private Map converters;
    public RapidConvertUtils() {
        super(); //To change body of overridden methods use File | Settings | File Templates.
        converters = new HashMap();
    }

    public static RapidConvertUtils getInstance()
    {
        if(convertUtils == null)
        {
            convertUtils = new RapidConvertUtils();
            convertUtils.deregister();
        }
        return convertUtils;
    }

    public void register(Converter converter, Class aClass) {

        if(converters != null)
        {
            converters.put (aClass, converter);
        }
    }

    

    public void deregister(Class aClass) {
        if(converters != null)
        {
            converters.remove(aClass)
        }
    }

    public Converter lookup(Class aClass) {
        if(converters != null)
        {
            return ((Converter) converters.get(aClass));
        }
        return null;
    }

}