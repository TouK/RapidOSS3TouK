package com.ifountain.compass

import com.ifountain.compass.converter.CompassDateConverter
import com.ifountain.compass.converter.CompassLongConverter
import com.ifountain.compass.converter.CompassStringConverter
import com.ifountain.compass.converter.CompassBooleanConverter
import com.ifountain.compass.converter.CompassDoubleConverter
import com.ifountain.compass.analyzer.WhiteSpaceLowerCaseAnalyzer
import com.ifountain.compass.index.WrapperIndexDeletionPolicy
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.ifountain.compass.query.RapidLuceneQueryParser

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 17, 2009
* Time: 10:49:43 AM
* To change this template use File | Settings | File Templates.
*/
class DefaultCompassConfiguration {
    public static Map getDefaultSettings(groovy.util.ConfigObject configObject)
    {
        def defaultDateFormat = configObject?.toProperties()?.get("rapidcmdb.date.format");
        def dateFormat = "yyyy-dd-MM||yyyy-dd-MM HH||yyyy-dd-MM HH:mm||yyyy-dd-MM HH:mm:ss||yyyy-dd-MM HH:mm:ss.SSS||MM-dd-yyyy||MM-dd-yyyy HH||MM-dd-yyyy HH:mm||MM-dd-yyyy HH:mm:ss||MM-dd-yyyy HH:mm:ss.SSS".toString();
        dateFormat = defaultDateFormat?"${dateFormat}||${dateFormat}".toString():dateFormat;
        return ["compass.converter.date.type": CompassDateConverter.class.name,
                "compass.converter.date.format": dateFormat,
                "compass.converter.long.type": CompassLongConverter.class.name,
                "compass.converter.string.type": CompassStringConverter.class.name,
                "compass.converter.long.format": "#000000000000000000000000000000",
                "compass.converter.boolean.type": CompassBooleanConverter.class.name,
                "compass.converter.double.type": CompassDoubleConverter.class.name,
                "compass.converter.double.format": "#000000000000000000000000000000.00000000000000",
                "compass.engine.analyzer.default.type": WhiteSpaceLowerCaseAnalyzer.class.name,
                "compass.engine.store.wrapper.wrapper1.type": "com.ifountain.compass.CompositeDirectoryWrapperProvider",
                "compass.engine.store.wrapper.wrapper1.awaitTermination": "10000000",
                "compass.cache.first": "org.compass.core.cache.first.NullFirstLevelCache",
                "compass.transaction.lockTimeout": "45",
                "compass.engine.store.indexDeletionPolicy.type": WrapperIndexDeletionPolicy.name,
                "compass.engine.queryParser.default.type": RapidLuceneQueryParser.class.name
        ]
    }
}