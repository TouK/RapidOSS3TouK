/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
package com.ifountain.smarts.util.params;

import com.smarts.repos.MR_AnyVal;

import java.util.Map;

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
