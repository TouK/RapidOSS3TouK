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
package com.ifountain.rcmdb.converter

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.converter.BooleanConverter

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 4:46:45 PM
 * To change this template use File | Settings | File Templates.
 */
class BooleanConverterTest extends RapidCmdbTestCase{
    public void testConvert()
    {
        BooleanConverter converter = new BooleanConverter();
        def returnedBoolean = converter.convert (Boolean, "true");
        assertEquals (new Boolean(true), returnedBoolean);
    }

    public void testConvertWithEmptyString()
    {
        BooleanConverter converter = new BooleanConverter();
        try{
            converter.convert (Boolean, "");
            fail("Should throw exception");
        }catch(org.apache.commons.beanutils.ConversionException ex)
        {

        }
    }

    public void testIfInstanceOfBooleanReturnThatInstance()
    {
        BooleanConverter converter = new BooleanConverter();
        def inputInstance = new Boolean(true);
        def returnedBoolean = converter.convert (Boolean, inputInstance);
        assertSame (inputInstance, returnedBoolean);
    }
}