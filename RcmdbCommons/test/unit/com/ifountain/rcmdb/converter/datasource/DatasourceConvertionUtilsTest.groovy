package com.ifountain.rcmdb.converter.datasource

import com.ifountain.rcmdb.converter.datasource.DefaultConverter
import com.ifountain.rcmdb.converter.datasource.NotConvertingConverter
import com.ifountain.rcmdb.converter.datasource.StringConverter
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import java.sql.Time
import java.sql.Timestamp
import com.ifountain.comp.converter.Converter

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 10:27:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatasourceConvertionUtilsTest extends RapidCmdbTestCase
{
    public void testDefaultConversionSettings()
    {
        DatasourceConvertionUtils.registerDefaultConverters();
        def objectsToBeConvertedToLong = [new Integer(100), 100, new Short((short)100), (short)100, (long)100, new Long(100), (byte)1, new Byte((byte)1)];
        def expectedLongValues = [new Long(100), new Long(100), new Long(100), new Long(100), new Long(100), new Long(100), new Long(1), new Long(1)];
        checkConversion(objectsToBeConvertedToLong, expectedLongValues, DefaultConverter);

        def objectsToBeConvertedToDouble = [new Float(100), (float)100, new Double(100), (double)100];
        def expectedDoubleValues = [new Double(100),new Double(100),new Double(100),new Double(100)];
        checkConversion(objectsToBeConvertedToDouble, expectedDoubleValues, DefaultConverter);

        def objectsToBeConvertedBoolean = [new Boolean(false), false];
        def expectedBooleanValues = [new Boolean(false), false];
        checkConversion(objectsToBeConvertedBoolean, expectedBooleanValues, DefaultConverter);

        def now  = System.currentTimeMillis();
        def objectsToBeConvertedDate = [new Date(now), new Timestamp(now), new Time(now)];
        def expectedDateValues = [new Date(now), new Timestamp(now), new Time(now)];
        checkConversion(objectsToBeConvertedDate, expectedDateValues, com.ifountain.rcmdb.converter.datasource.NotConvertingConverter);

        assertEquals(StringConverter.class, DatasourceConvertionUtils.getRegistry().lookup(Object).class);
    }

    public void testRegisterClosureConverter()
    {
        Closure conversionClosure = {objectValue->
            return String.valueOf(objectValue);
        }
        DatasourceConvertionUtils.register(Long, conversionClosure);
    }

    public void testWithDefaultConverter()
    {
        DatasourceConvertionUtils.registerDefaultConverters();
        NotConvertingConverter defaultConverter = new NotConvertingConverter();
        DatasourceConvertionUtils.getRegistry().setDefaultConverter(defaultConverter);
        assertSame (defaultConverter, DatasourceConvertionUtils.getRegistry().lookup(Object));
    }



    private void checkConversion(List objectsToCheckConversion, List expectedConverionresults, Class expectedConverter)
    {
        for(int i=0; i < objectsToCheckConversion.size(); i++)
        {
            Object objToBeConverted = objectsToCheckConversion.get(i);
            Object expectedConvertedObject = expectedConverionresults.get(i);
            Converter converter = DatasourceConvertionUtils.getRegistry().lookup(objToBeConverted.getClass());
            assertEquals (expectedConverter, converter.class);
            assertEquals(expectedConvertedObject.class.name, converter.convert(objToBeConverted).class.name);
            assertEquals(expectedConvertedObject, converter.convert(objToBeConverted));
        }
    }
}