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
import com.smarts.repos.MR_PropertyNameValue;

public class GetAllPropertiesAction implements Action {

    private String className;
    private String instanceName;
    private long propertyTypeFlag;
    private MR_PropertyNameValue[] allProperties;
    public GetAllPropertiesAction(String className, String instanceName,long propertyTypeFlag) {
        this.className = className;
        this.instanceName = instanceName;
        this.propertyTypeFlag = propertyTypeFlag;
    }
    @Override
    public void execute(IConnection ds) throws Exception {
        allProperties = ((SmartsConnectionImpl)ds).getDomainManager().getAllProperties(className, instanceName, propertyTypeFlag);
    }
    public MR_PropertyNameValue[] getAllProperties() {
        return allProperties;
    }

}
