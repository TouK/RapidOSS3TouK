/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.util.property;


import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_ValType;

public class MRToEntryFactory {

    public static MRToEntry getMRToEntry(String name, MR_AnyVal value){
        if (value.getType() == MR_ValType.MR_OBJREF)
            return new MRObjRefToEntry(name, value);
        else if (value.getType() == MR_ValType.MR_OBJREF_SET)
            return new MRObjRefSetToEntry(name, value);
        else if (value.getType() == MR_ValType.MR_ANYVALARRAY)
            return new MRArrayToEntry(name, value);
        else if (value.getType() == MR_ValType.MR_ANYVALARRAY_SET)
            return new MRArraySetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_DOUBLE_SET)
            return new MRPrimitiveSetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_INT_SET)
            return new MRPrimitiveSetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_CHAR_SET)
            return new MRPrimitiveSetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_FLOAT_SET)
            return new MRPrimitiveSetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_LONG_SET)
            return new MRPrimitiveSetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_SHORT_SET)
            return new MRPrimitiveSetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_STRING_SET)
            return new MRPrimitiveSetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_UNSIGNEDINT_SET)
            return new MRPrimitiveSetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_UNSIGNEDLONG_SET)
            return new MRPrimitiveSetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_UNSIGNEDSHORT_SET)
            return new MRPrimitiveSetToEntry(name, value);
        else if(value.getType() == MR_ValType.MR_VOID)
            throw new RuntimeException("Can not convert MR_VOID property \""+name+"\"");
        else
          return new MROthersToEntry(name, value);
    }
}
