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

import com.smarts.repos.MR_AnyVal;

import java.util.Map;

/**
 * User: kinikogl
 * Date: Sep 13, 2004
 * Time: 6:06:29 PM
 */
public class NotificationCreateParams {
    private NotificationIdentifierParams identifierParameters;
    private NotificationNotifyParams notifyParameters;
    private Map<String, MR_AnyVal> attributeParameters;
    private NotificationAggregateParams aggregateParameters;
    private String unknownAgent;

    public NotificationCreateParams(NotificationIdentifierParams identifierParameters,
                                        NotificationNotifyParams notifyParameters, Map<String, MR_AnyVal> attributeParameters,
                                        NotificationAggregateParams aggregateParameters, String unknownAgent) {
        this.identifierParameters = identifierParameters;
        this.notifyParameters = notifyParameters;
        this.attributeParameters = attributeParameters;
        this.aggregateParameters = aggregateParameters;
        this.unknownAgent = unknownAgent;
    }

    public NotificationIdentifierParams getIdentifierParameters() {
        return identifierParameters;
    }

    public void setIdentifierParameters(NotificationIdentifierParams identifierParameters) {
        this.identifierParameters = identifierParameters;
    }

    public NotificationNotifyParams getNotifyParameters() {
        return notifyParameters;
    }

    public void setNotifyParameters(NotificationNotifyParams notifyParameters) {
        this.notifyParameters = notifyParameters;
    }

    public Map<String, MR_AnyVal> getAttributeParameters() {
        return attributeParameters;
    }

    public void setAttributeParameters(Map<String, MR_AnyVal> attributeParameters) {
        this.attributeParameters = attributeParameters;
    }

    public NotificationAggregateParams getAggregateParameters() {
        return aggregateParameters;
    }

    public void setAggregateParameters(NotificationAggregateParams aggregateParameters) {
        this.aggregateParameters = aggregateParameters;
    }

    public String getUnknownAgent() {
        return unknownAgent;
    }

    public void setUnknownAgent(String unknownAgent) {
        this.unknownAgent = unknownAgent;
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        if(identifierParameters != null)
        	buffer.append(identifierParameters.toString());
        if(notifyParameters  != null)
        	buffer.append(notifyParameters.toString());
        if(attributeParameters  != null)
        	buffer.append(attributeParameters).append("\n");
        if(aggregateParameters  != null)            
        	buffer.append(aggregateParameters.toString());
        if(unknownAgent != null)
        	buffer.append("unknownAgent: ").append(unknownAgent);
        
        return buffer.toString();
    }
}
