package com.ifountain.comp.converter;

import com.ifountain.comp.test.util.RCompTestCase;

import java.util.*;

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

    public void testLookupReturnsParentClassConverterIfClassDoesnotHaveASpecificConverter()  throws Exception
    {
        final String resultWillBeReturned = "res";
        Converter converter = new Converter(){
            public Object convert(Object value) {
                return resultWillBeReturned;
            }
        };
        ConverterRegistry.getInstance().register(Collection.class, converter);
        assertSame (resultWillBeReturned, ConverterRegistry.getInstance().convert(new ArrayList()));
        
        ConverterRegistry.getInstance().unregisterAll();
        ConverterRegistry.getInstance().register(AbstractList.class, converter);
        assertSame (resultWillBeReturned, ConverterRegistry.getInstance().convert(new ArrayList()));

        ConverterRegistry.getInstance().unregisterAll();
        ConverterRegistry.getInstance().register(Collection.class, converter);

        final String resultWillBeReturned1 = "res1";
        Converter updatedConverter = new Converter(){
            public Object convert(Object value) {
                return resultWillBeReturned1;
            }
        };

        ConverterRegistry.getInstance().register(Collection.class, updatedConverter);
        assertSame (resultWillBeReturned1, ConverterRegistry.getInstance().convert(new ArrayList()));
    }

    public void testLookupReturnsParentClassConverterIfClassIsArrayAndDoesnotHaveASpecificConverter()  throws Exception
    {
        final String resultWillBeReturned = "res";
        Converter converter = new Converter(){
            public Object convert(Object value) {
                return resultWillBeReturned;
            }
        };
        ConverterRegistry.getInstance().register(Object[].class, converter);
        assertSame (resultWillBeReturned, ConverterRegistry.getInstance().convert(new int[]{0}));
    }

    public void testRegisterWithSpecificConverterChangesChildConverterDoesnotChangeParentConverter()  throws Exception
    {
        final String resultWillBeReturned = "res";
        Converter converter = new Converter(){
            public Object convert(Object value) {
                return resultWillBeReturned;
            }
        };
        ConverterRegistry.getInstance().register(Collection.class, converter);
        assertSame (resultWillBeReturned, ConverterRegistry.getInstance().convert(new ArrayList()));

        final String resultWillBeReturned1 = "res1";
        Converter listConverter = new Converter(){
            public Object convert(Object value) {
                return resultWillBeReturned1;
            }
        };

        ConverterRegistry.getInstance().register(ArrayList.class, listConverter);
        assertSame (resultWillBeReturned1, ConverterRegistry.getInstance().convert(new ArrayList()));
        assertSame (converter, ConverterRegistry.getInstance().lookup(Collection.class));
    }

    public void testRegisterWithAnotherParentClassChangesChildConvertersUsingAnotherParentConverter()  throws Exception
    {
        Converter collectionsConverter = new Converter(){
            public Object convert(Object value) {
                return null;
            }
        };
        Converter arrayListConverter = new Converter(){
            public Object convert(Object value) {
                return null;
            }
        };
        ConverterRegistry.getInstance().register(Collection.class, collectionsConverter);
        ConverterRegistry.getInstance().register(ArrayList.class, arrayListConverter);

        assertSame (collectionsConverter, ConverterRegistry.getInstance().lookup(LinkedList.class));
        assertSame (arrayListConverter, ConverterRegistry.getInstance().lookup(ArrayList.class));


        Converter listConverter = new Converter(){
            public Object convert(Object value) {
                return null;
            }
        };
        ConverterRegistry.getInstance().register(AbstractCollection.class, listConverter);

        assertSame (listConverter, ConverterRegistry.getInstance().lookup(LinkedList.class));
        assertSame (arrayListConverter, ConverterRegistry.getInstance().lookup(ArrayList.class));
    }
}
