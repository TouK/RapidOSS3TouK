package com.ifountain.rcmdb.domain.converter

import org.compass.core.converter.basic.FormatConverter
import org.compass.core.converter.Converter
import org.compass.core.config.CompassConfigurable

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 5:33:29 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractCompassFormattedConverterWrapper extends AbstractCompassConverterWrapper implements FormatConverter, CompassConfigurable{
    public void setFormat(String s) {
        ((FormatConverter)getConverter()).setFormat (s);
    }

    public FormatConverter copy() {
        return ((FormatConverter)getConverter()).copy();
    }

}