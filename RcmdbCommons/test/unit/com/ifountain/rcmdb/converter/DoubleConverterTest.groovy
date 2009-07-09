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
import com.ifountain.rcmdb.converter.DoubleConverter

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 30, 2008
* Time: 1:34:18 PM
* To change this template use File | Settings | File Templates.
*/
class DoubleConverterTest extends RapidCmdbTestCase{
    public void testConvert()
    {
        DoubleConverter converter = new DoubleConverter();
        def returnedDouble = converter.convert (Double, "5.0");
        assertEquals (new Double(5.0), returnedDouble);
    }
    public void testConvertWithEmptyString()
    {
        DoubleConverter converter = new DoubleConverter();
        try{
            converter.convert (Double, "");
            fail("Should throw exception");
        }catch(org.apache.commons.beanutils.ConversionException ex)
        {

        }
    }

    public void testIfInstanceOfLongReturnThatInstance()
    {
        DoubleConverter converter = new DoubleConverter();
        def inputInstance = new Double(1111);
        def returnedDouble = converter.convert (Double, inputInstance);
        assertSame (inputInstance, returnedDouble);
    }
}