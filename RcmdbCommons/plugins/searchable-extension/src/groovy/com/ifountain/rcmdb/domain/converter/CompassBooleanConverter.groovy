package com.ifountain.rcmdb.domain.converter

import org.compass.core.converter.Converter


/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 5:01:07 PM
 * To change this template use File | Settings | File Templates.
 */
class CompassBooleanConverter extends AbstractCompassConverterWrapper{
    org.compass.core.converter.basic.BooleanConverter dateConverter = new org.compass.core.converter.basic.BooleanConverter();

    protected Converter getConverter() {
        return dateConverter; //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object getDefaultValue() {
        return new Boolean(false);
    }
}