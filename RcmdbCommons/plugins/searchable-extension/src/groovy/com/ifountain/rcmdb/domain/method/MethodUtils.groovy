package com.ifountain.rcmdb.domain.method

import org.compass.core.CompassHits
import org.apache.commons.collections.MapUtils
import com.ifountain.rcmdb.converter.RapidConvertUtils
import org.apache.commons.beanutils.ConversionException
import com.ifountain.rcmdb.domain.util.ValidationUtils
import org.springframework.validation.Errors
import org.springframework.validation.BindingResult;
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 18, 2009
 * Time: 6:08:46 PM
 * To change this template use File | Settings | File Templates.
 */
class MethodUtils {

    public static void convertAndSetDomainObjectProperty(BindingResult errors, Object domainObject, String fieldName, Class fieldType, Object value) {
        if(value != null)
        {
            try
            {
                def converter = RapidConvertUtils.getInstance().lookup (fieldType);
                value = converter.convert(fieldType, value);
                domainObject.setProperty(fieldName, value, false);
            }
            catch(ConversionException exception)
            {
                ValidationUtils.addFieldError (errors, fieldName, value, "rapidcmdb.invalid.property.type", [fieldName, fieldType.name, domainObject.class.name]);
            }
        }
        else
        {
            domainObject.setProperty(fieldName, null, false);
        }
    }
    public static void getCompassHitsSubset(CompassHits compassHits, Map options, Closure hitIteratorClosure) {
        List hitList = new ArrayList();
        def maxOption = options["max"]
        int offset = MapUtils.getIntValue(options, "offset");
        int max = MapUtils.getIntValue(options, "max");
        if (maxOption == null || max > compassHits.length())
        {
            max = compassHits.length();
        }
        int low = offset;
        int high = Math.min(low + max, compassHits.length());
        Iterator hitIterator = compassHits.iterator();
        for (int i = 0; i < low && i < high; i++) {
            hitIterator.next();
        }
        while (low < high) {

            hitIteratorClosure(hitIterator.next());
            low++
        }
    }
}