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
package com.ifountain.comp.test.util.testcase;

import junit.framework.ComparisonFailure;

import org.xml.sax.SAXParseException;

public class RapidTestCaseTest extends RapidTestCase {
	
	public void testAssertEqualsXml() throws Exception {
		assertEqualsXML("<a/>", "<a/>");
		try {
			assertEqualsXML("<a/>", "<b/>");
			fail("Should have thrown exception.");
		} catch (ComparisonFailure e) {
			assertTrue(e.getMessage(), e.getMessage().indexOf("Name of nodes are not same \nNode 1 :\na\nNode 2 :\nb") > -1);
		}		
		
		try
		{
			assertEqualsXML("<a/>", "invalid_xml");
			fail("Should have thrown exception.");
		}
		catch(Exception e)
		{
			assertEquals(SAXParseException.class, e.getCause().getClass());
			assertEquals("Expected or actual xml does not seem valid since it can not be parsed by sax parser.", e.getMessage());
		}
	}
}
