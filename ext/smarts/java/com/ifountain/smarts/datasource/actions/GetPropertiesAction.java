/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 19, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource.actions;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.datasource.Action;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.smarts.repos.MR_AnyVal;

public class GetPropertiesAction implements Action {

    private String className;
    private String instanceName;
    private String[] propertyNames;
    private MR_AnyVal[] properties;
    public GetPropertiesAction(String className, String instanceName, String[] propertyNames) {
        this.className = className;
        this.instanceName = instanceName;
        this.propertyNames = propertyNames;
    }
    @Override
    public void execute(IConnection ds) throws Exception {
        properties = ((SmartsConnectionImpl)ds).getDomainManager().getProperties(className, instanceName, propertyNames);
    }
    public MR_AnyVal[] getProperties() {
        return properties;
    }

}
