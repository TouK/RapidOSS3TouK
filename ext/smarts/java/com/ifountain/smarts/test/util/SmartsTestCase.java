/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 20, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.test.util;

import com.ifountain.core.test.util.DatasourceTestUtils;
import com.ifountain.core.test.util.RapidCoreTestCase;

public class SmartsTestCase extends RapidCoreTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DatasourceTestUtils.getParamSupplier().setParam(SmartsTestUtils.getConnectionParam(SmartsTestConstants.SMARTS_SAM_CONNECTION_TYPE));
    }
}
