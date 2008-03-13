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

import java.util.Map;

import com.smarts.repos.MR_AnyVal;

public abstract class MRToEntry {

    protected String propertyName;
    protected MR_AnyVal propertyValue;

    protected MRToEntry(String name, MR_AnyVal value)
    { 
        propertyName = name;
        propertyValue = value;
    }
    public void putToMap(Map<String, Object> map){
        map.put(propertyName, getValue());
    }
    public abstract Object getValue() ;
}
