/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
 * noted in a separate copyright notice. All rights reserved.
 * This file is part of RapidCMDB.
 * 
 * RapidCMDB is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.util.property;

import java.util.HashMap;
import java.util.Map;

import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValCharSet;
import com.smarts.repos.MR_AnyValDoubleSet;
import com.smarts.repos.MR_AnyValFloatSet;
import com.smarts.repos.MR_AnyValIntSet;
import com.smarts.repos.MR_AnyValLongSet;
import com.smarts.repos.MR_AnyValShortSet;
import com.smarts.repos.MR_AnyValStringSet;
import com.smarts.repos.MR_ValType;

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
            records[0].put("element"+i, values[i].toString());
        }
        return records;
    }

}
