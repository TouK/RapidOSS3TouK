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
package com.ifountain.comp.utils;

import com.ifountain.comp.test.util.RCompTestCase;



public class StringUtilsTest extends RCompTestCase {
	public void testEscape() throws Exception {

        assertEquals("sdcas&amp;djca&apos;sk", StringUtils.escapeXML("sdcas&djca'sk"));
        assertEquals("sdcas&lt;dj&lt;sk", StringUtils.escapeXML("sdcas<dj<sk"));
        assertEquals("sdcas&lt;dj&gt;sk", StringUtils.escapeXML("sdcas<dj>sk"));
        assertEquals("sdcas&lt;d&amp;j&gt;sk", StringUtils.escapeXML("sdcas<d&j>sk"));
        assertEquals("sdcas&quot;dj&quot;sk", StringUtils.escapeXML("sdcas\"dj\"sk"));
        assertEquals("sd&amp;as&lt;dj&gt;s&amp;k", StringUtils.escapeXML("sd&as<dj>s&k"));
    }
    
    public void testEscapeDownotProcessNullValue() throws Exception {
        assertEquals(null, StringUtils.escapeXML(null));
    }
}
