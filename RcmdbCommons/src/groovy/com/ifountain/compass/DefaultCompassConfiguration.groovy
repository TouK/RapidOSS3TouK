package com.ifountain.compass

import com.ifountain.compass.analyzer.WhiteSpaceLowerCaseAnalyzer
import com.ifountain.compass.converter.*
import com.ifountain.compass.index.WrapperIndexDeletionPolicy
import com.ifountain.compass.query.RapidLuceneQueryParser
import org.compass.core.lucene.LuceneEnvironment
import com.ifountain.compass.transaction.processor.SingleCompassSessionTransactionProcessor
import com.ifountain.compass.transaction.processor.SingleCompassSessionTransactionProcessorFactory
import com.ifountain.compass.analyzer.LowerCaseAnalyzer

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 17, 2009
* Time: 10:49:43 AM
* To change this template use File | Settings | File Templates.
*/
public class DefaultCompassConfiguration {
    public static Map getDefaultSettings(groovy.util.ConfigObject configObject)
    {
        def defaultDateFormat = configObject?.toProperties()?.get("rapidcmdb.date.format");
        def dateFormat = "yyyy-dd-MM||yyyy-dd-MM HH||yyyy-dd-MM HH:mm||yyyy-dd-MM HH:mm:ss||yyyy-dd-MM HH:mm:ss.SSS||MM-dd-yyyy||MM-dd-yyyy HH||MM-dd-yyyy HH:mm||MM-dd-yyyy HH:mm:ss||MM-dd-yyyy HH:mm:ss.SSS".toString();
        dateFormat = defaultDateFormat?"${defaultDateFormat}||${dateFormat}".toString():dateFormat;
        def defSt = ["compass.converter.date.type": CompassDateConverter.class.name,
                "compass.converter.date.format": dateFormat,
                "compass.converter.long.type": CompassLongConverter.class.name,
                "compass.converter.string.type": CompassStringConverter.class.name,
                "compass.converter.long.format": "#000000000000000000000000000000",
                "compass.converter.unformattedlong.type": CompassLongConverter.class.name,
                "compass.converter.unformattedlong.format": "#",
                "compass.converter.unformatteddouble.type": CompassDoubleConverter.class.name,
                "compass.converter.unformatteddouble.format": "#.00000000000000",
                "compass.converter.boolean.type": CompassBooleanConverter.class.name,
                "compass.converter.double.type": CompassDoubleConverter.class.name,
                "compass.converter.double.format": "#000000000000000000000000000000.00000000000000",
                "compass.engine.analyzer.default.type": WhiteSpaceLowerCaseAnalyzer.class.name,
                "compass.engine.analyzer.lowercase.type": LowerCaseAnalyzer.class.name,
                "compass.engine.store.wrapper.wrapper1.type": "com.ifountain.compass.CompositeDirectoryWrapperProvider",
                "compass.engine.store.wrapper.wrapper1.awaitTermination": "10000000",
                "compass.cache.first": "org.compass.core.cache.first.NullFirstLevelCache",
                "compass.transaction.lockTimeout": "3600",
                "compass.registerShutdownHook":"false",
                "compass.engine.store.indexDeletionPolicy.type": WrapperIndexDeletionPolicy.name,
                "compass.engine.queryParser.default.type": RapidLuceneQueryParser.class.name ,
                "compass.engine.maxBufferedDocs":"1000",
                "compass.engine.ramBufferSize":"60",
                "compass.engine.cacheIntervalInvalidation":"-1"
        
        ]
//        defSt.put(LuceneEnvironment.Transaction.Processor.PREFIX+SingleCompassSessionTransactionProcessor.NAME+"."+LuceneEnvironment.Transaction.Processor.CONFIG_TYPE, SingleCompassSessionTransactionProcessorFactory.class.name);
//        defSt.put(LuceneEnvironment.Transaction.Processor.TYPE, SingleCompassSessionTransactionProcessor.NAME);
        return defSt;
    }
}