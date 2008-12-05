/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package com.ifountain.rcmdb.test.util
import datasource.*;
import script.*;
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 23, 2008
 * Time: 3:54:54 PM
 * To change this template use File | Settings | File Templates.
 */
class CompassForTestsTest extends RapidCmdbTestCase{
    def test1()
    {
        CompassForTests.initialize ([datasource.BaseListeningDatasource]);
        assertNull (datasource.BaseListeningDatasource.add(prop1:"asdasdasd"));
        CompassForTests.setAddObjects([new BaseListeningDatasource(id:0)]);
        assertEquals (0, datasource.BaseListeningDatasource.add(prop1:"asdasdasd").id);
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
        CmdbScript.startListening("asd");
    }

}