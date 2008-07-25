package com.ifountain.rcmdb.domain.converter

import org.compass.core.converter.Converter
import org.compass.core.converter.basic.FormatConverter

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: May 29, 2008
 * Time: 5:59:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompassLongConverter extends AbstractCompassConverterWrapper
{
    org.compass.core.converter.basic.LongConverter longConverter = new org.compass.core.converter.basic.LongConverter();
    protected FormatConverter getConverter() {
        return longConverter; //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object getDefaultValue() {
        return new Long(0);
    }

}
