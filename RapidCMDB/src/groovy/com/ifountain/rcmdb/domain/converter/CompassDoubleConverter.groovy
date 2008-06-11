package com.ifountain.rcmdb.domain.converter

import org.compass.core.converter.Converter

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 30, 2008
* Time: 1:21:09 PM
* To change this template use File | Settings | File Templates.
*/
class CompassDoubleConverter extends AbstractCompassConverterWrapper{
    org.compass.core.converter.basic.DoubleConverter doubleConverter = new org.compass.core.converter.basic.DoubleConverter();
    protected Converter getConverter() {
        return doubleConverter; //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object getDefaultValue() {
        return new Double(0);
    }
}