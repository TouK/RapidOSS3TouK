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
package com.ifountain.compass.converter

import org.compass.core.Property.Index
import org.compass.core.Property.Store
import org.compass.core.Property.TermVector
import org.compass.core.Resource
import org.compass.core.config.CompassConfigurable
import org.compass.core.config.CompassSettings
import org.compass.core.converter.Converter
import org.compass.core.converter.mapping.ResourcePropertyConverter
import org.compass.core.mapping.Mapping
import org.compass.core.mapping.ResourcePropertyMapping
import org.compass.core.marshall.MarshallingContext
import org.compass.core.converter.basic.FormatConverter
import com.ifountain.rcmdb.domain.util.DomainClassDefaultPropertyValueHolder
import com.ifountain.compass.CompassConstants

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 30, 2008
* Time: 1:21:35 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractCompassConverterWrapper  implements ResourcePropertyConverter{
    public boolean marshall(Resource resource, Object o, Mapping mapping, MarshallingContext marshallingContext) {
        if(o == null)
        {
            o = convertNullProperty(resource.getAlias(), mapping.name);
        }
        return getConverter().marshall(resource, o, mapping, marshallingContext);
    }
    public Object unmarshall(Resource resource, Mapping mapping, MarshallingContext marshallingContext) {
        try
        {
            return getConverter().unmarshall(resource, mapping, marshallingContext);
        }
        catch(Throwable e)
        {
            return getDefaultValue();
        }
    }
    public void configure(CompassSettings compassSettings)
    {
        ((CompassConfigurable)getConverter()).configure (compassSettings);    
    }

    public Object fromString(String s, ResourcePropertyMapping resourcePropertyMapping) {
        return ((ResourcePropertyConverter)getConverter()).fromString (s, resourcePropertyMapping);
    }

    public String toString(Object o, ResourcePropertyMapping resourcePropertyMapping) {
        return ((ResourcePropertyConverter)getConverter()).toString (o, resourcePropertyMapping);
    }

    public boolean canNormalize() {
        return ((ResourcePropertyConverter)getConverter()).canNormalize ();
    }

    public Index suggestIndex() {
        return ((ResourcePropertyConverter)getConverter()).suggestIndex ();
    }

    public TermVector suggestTermVector() {
        return ((ResourcePropertyConverter)getConverter()).suggestTermVector ();
    }

    public Store suggestStore() {
        return ((ResourcePropertyConverter)getConverter()).suggestStore ();
    }

    public Boolean suggestOmitNorms() {
        return ((ResourcePropertyConverter)getConverter()).suggestOmitNorms ();
    }


    protected abstract Converter getConverter();
    protected abstract Object getDefaultValue();
    protected Object getMarshallingDefaultValue()
    {
        return getDefaultValue();
    }

    protected Object convertNullProperty(String alias, String propName)
    {
       return CompassConverterUtils.getNullPropertyValue(alias, propName, getMarshallingDefaultValue());
    }
}