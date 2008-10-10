/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.util.property;

import com.smarts.repos.*;

import java.util.HashMap;
import java.util.Map;

public class MRPrimitiveSetToEntry extends MRToEntry {

    protected MRPrimitiveSetToEntry(String name, MR_AnyVal value) {
        super(name, value);
    }

    @Override
    public Object getValue() {
        Object[] values = null;
        if(propertyValue.getType() == MR_ValType.MR_DOUBLE_SET)
        {
            double[] set = ((MR_AnyValDoubleSet)propertyValue).getDoubleSetValue();
            values = new Object[set.length];
            for (int i = 0 ; i < set.length ; i++)
            {
                values[i] = new Double(set[i]);
            }
        }
        else if(propertyValue.getType() == MR_ValType.MR_INT_SET || propertyValue.getType() == MR_ValType.MR_UNSIGNEDSHORT_SET )
        {
            int[] set = ((MR_AnyValIntSet)propertyValue).getIntSetValue();
            values = new Object[set.length];
            for (int i = 0 ; i < set.length ; i++)
            {
                values[i] = new Integer(set[i]);
            }
        }
        else if(propertyValue.getType() == MR_ValType.MR_CHAR_SET)
        {
            char[] set = ((MR_AnyValCharSet)propertyValue).getCharSetValue();
            values = new Object[set.length];
            for (int i = 0 ; i < set.length ; i++)
            {
                values[i] = new Character(set[i]);
            }
        }
        else if(propertyValue.getType() == MR_ValType.MR_FLOAT_SET)
        {
            float[] set = ((MR_AnyValFloatSet)propertyValue).getFloatSetValue();
            values = new Object[set.length];
            for (int i = 0 ; i < set.length ; i++)
            {
                values[i] = new Float(set[i]);
            }
        }
        else if(propertyValue.getType() == MR_ValType.MR_LONG_SET || propertyValue.getType() == MR_ValType.MR_UNSIGNEDLONG_SET || propertyValue.getType() == MR_ValType.MR_UNSIGNEDINT_SET)
        {
            long[] set = ((MR_AnyValLongSet)propertyValue).getLongSetValue();
            values = new Object[set.length];
            for (int i = 0 ; i < set.length ; i++)
            {
                values[i] = new Long(set[i]);
            }
        }
        else if(propertyValue.getType() == MR_ValType.MR_SHORT_SET)
        {
            short[] set = ((MR_AnyValShortSet)propertyValue).getShortSetValue();
            values = new Object[set.length];
            for (int i = 0 ; i < set.length ; i++)
            {
                values[i] = new Short(set[i]);
            }
        }
        else if(propertyValue.getType() == MR_ValType.MR_STRING_SET)
        {
            String[] set = ((MR_AnyValStringSet)propertyValue).getStringSetValue();
            values = new Object[set.length];
            for (int i = 0 ; i < set.length ; i++)
            {
                values[i] = new String(set[i]);
            }
        }
        Map<String, Object>[] records = new HashMap[1];
        records[0] = new HashMap<String, Object>();
        
        for (int i = 0; i < values.length; i++) {
            records[0].put("element"+i, values[i]);
        }
        return records;
    }

}
