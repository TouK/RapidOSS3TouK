package com.ifountain.comp.converter;

import com.ifountain.comp.test.util.RCompTestCase;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 3:40:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConverterRegistryTest extends RCompTestCase{
    protected void setUp() throws Exception {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        ConverterRegistry.getInstance().unregisterAll();
    }

    protected void tearDown() throws Exception {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
        ConverterRegistry.getInstance().unregisterAll();
    }

    public void testWithDefaultConverter()
    {
        Converter defaultConverter = new Converter(){
            public Object convert(Object value) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        ConverterRegistry.getInstance().setDefaultConverter(defaultConverter);
        assertSame (defaultConverter, ConverterRegistry.getInstance().lookup(Object.class));
    }

    public void testUnRegisterAll()
    {
        Converter converter = new Converter(){
            public Object convert(Object value) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
        ConverterRegistry.getInstance().register(Long.class, converter);
        ConverterRegistry.getInstance().unregisterAll();
        assertNull(ConverterRegistry.getInstance().lookup(Long.class));
    }

    public void testConvert() throws Exception
    {
        final String resultWillBeReturned = "res";
        Converter converter = new Converter(){
            public Object convert(Object value) {
                return resultWillBeReturned;
            }
        };
        ConverterRegistry.getInstance().register(Integer.class, converter);
        assertSame (resultWillBeReturned, ConverterRegistry.getInstance().convert(new Integer(100)));
    }

    public void testConvertWithNullObject() throws Exception
    {
        final String resultWillBeReturned = "res";
        Converter defaultConverter = new Converter(){
            public Object convert(Object value) {
                return resultWillBeReturned;
            }
        };
        ConverterRegistry.getInstance().setDefaultConverter(defaultConverter);
        assertSame (resultWillBeReturned, ConverterRegistry.getInstance().convert(null));
    }

    public void testConvertWithoutDefaultConverterThrowsConversionException()
    {
        try
        {
            ConverterRegistry.getInstance().convert(new Integer(100));
            fail("Should throw conversion exception");
        }catch(Exception e)
        {

        }
    }
}
