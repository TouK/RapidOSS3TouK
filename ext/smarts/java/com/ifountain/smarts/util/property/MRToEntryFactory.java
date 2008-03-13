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
        else
          return new MROthersToEntry(name, value);
    }
}
