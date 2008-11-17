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

public class GetPropTypeAction implements Action {

    private int propType;
    private String propertyName;
    private String className;
    public GetPropTypeAction(String className, String propertyName) {
        this.className = className;
        this.propertyName = propertyName;
    }
    @Override
    public void execute(IConnection ds) throws Exception {
        propType = ((SmartsConnectionImpl)ds).getDomainManager().getPropType(className, propertyName);
    }
    
    public int getPropType(){
        return propType;
    }

}
