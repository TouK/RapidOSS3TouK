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
package com.ifountain.smarts.util.params;

import java.util.Map;

import com.smarts.repos.MR_AnyVal;

/**
 * @author Ozgur Alkaner
 */
public class NotificationUpdateParams implements NotificationParams {

    private Map<String, MR_AnyVal> attributeParameters;

    public NotificationUpdateParams(Map<String, MR_AnyVal> attributeParameters) {
        this.attributeParameters = attributeParameters;
    }

    public MR_AnyVal[] getMethodArgs() {
        return null;
    }

    public Map<String, MR_AnyVal> getAttributeParameters() {
        return attributeParameters;
    }

    public void setAttributeParameters(Map<String, MR_AnyVal> attributeParameters) {
        this.attributeParameters = attributeParameters;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Attributes to Update: ").append(attributeParameters).append("\n");
        return buffer.toString();
    }
}
