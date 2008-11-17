package com.ifountain.smarts.datasource;

import org.apache.log4j.Logger;

import java.util.Hashtable;

import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_PropertyNameValue;

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
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jul 16, 2008
 * Time: 6:04:16 PM
 */
public class InMemoryNotification {
    private String name;
    private Hashtable _monitoredAttributes;
    private Logger logger;

    public InMemoryNotification(String name, Hashtable monitoredAttributes, Logger logger) {
        this.name = name;
        this._monitoredAttributes = monitoredAttributes;
        this.logger = logger;
    }

    public String getName() {
        return name;
    }

    public Hashtable getMonitoredAttributes() {
        return _monitoredAttributes;
    }

    public boolean isChanged(MR_PropertyNameValue[] nameValuePairs) {
        boolean changed = false;
        for (int i = 0; i < nameValuePairs.length; i++) {
            MR_PropertyNameValue nameValuePair = nameValuePairs[i];
            String propName = nameValuePair.getPropertyName();
            if (_monitoredAttributes.containsKey(propName)) {
                MR_AnyVal before = (MR_AnyVal) _monitoredAttributes.get(propName);
                MR_AnyVal after = nameValuePair.getPropertyValue();
                if (!before.equals(after)) {
                    _monitoredAttributes.remove(propName);
                    _monitoredAttributes.put(propName, after);
                    logger.debug("Attribute " + propName + " value was: " + before + ", changed to: " + after);
                    changed = true;
                }
                else
                {
                    logger.debug("Attribute " + propName + " value did not change");
                }
            }
            else
            {
                logger.debug("Attribute " + propName + " is not monitored, discarding.");
            }
        }
        return changed;
    }
}
