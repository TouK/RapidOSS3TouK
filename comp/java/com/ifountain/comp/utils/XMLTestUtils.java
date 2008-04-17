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
/**
 * Created on Aug 15, 2006
 *
 * Author Sezgin kucukkaraaslan
 */
package com.ifountain.comp.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLTestUtils {
    private XMLTestUtils() {
    }

    public static void compareXml(String xml1, String xml2) throws Exception {
    	compareXml(xml1, xml2, new ArrayList());
    }

    public static void compareXml(String xml1, String xml2, List ignoredTags) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputSource source1 = new InputSource(new StringReader(xml1));
        InputSource source2 = new InputSource(new StringReader(xml2));
        Document doc = builder.parse(source1);
        Document doc2 = builder.parse(source2);

        compareNodes(doc, doc2,ignoredTags);
        compareNodes(doc2, doc,ignoredTags);
    }

    public static void compareNodes(Node node1, Node node2, List ignoredTags){

    	Assert.assertEquals(
			"Name of nodes are not same \nNode 1 :\n" + node1.getNodeName() + "\nNode 2 :\n" + node2.getNodeName(),
			node1.getNodeName(),
			node2.getNodeName()
    	);

    	Assert.assertEquals(
    		"Text of nodes are not same \nNode 1 :\n" + node1.getNodeName() + "\nNode 2 :\n" + node2.getNodeName(),
    		node1.getNodeValue() == null ? null : node1.getNodeValue().trim(),
    		node2.getNodeValue() == null ? null : node2.getNodeValue().trim()
    	);

        ArrayList nodeList = getChildNodes(node1.getChildNodes());
        ArrayList nodeList2 = getChildNodes(node2.getChildNodes());
    	Assert.assertEquals(
    		"Number of nodes under \nNode 1 :\n" + node1.getNodeName() + "\nNode 2 :\n" + node2.getNodeName() + "\nis not same. ",
    		nodeList.size(),
    		nodeList2.size()
    	);

        NamedNodeMap attributeForNode1 = node1.getAttributes();
        NamedNodeMap attributeForNode2 = node2.getAttributes();
        if (attributeForNode1 != null) {
            if(attributeForNode2 == null)
            {
                Assert.fail("One of attribute is null");
            }

            Assert.assertEquals(
            	"Number of attributes under \nNode 1 :\n" + node1.getNodeName() + "\nNode 2 :\n" + node2.getNodeName() + "\nis not same. ",
            	attributeForNode1.getLength(),
            	attributeForNode2.getLength()
            );

            for (int i = 0; i < attributeForNode1.getLength(); i++) {
            	if (!ignoredTags.contains(attributeForNode1.item(i).getNodeName()))
				{
                	Assert.assertEquals(
            			"Attribute " + attributeForNode1.item(i).getNodeName() + " doesnot equal in one of \nNode 1 :\n" + node1.getNodeName() + "\nNode 2 :\n"+ node2.getNodeName(),
            			attributeForNode1.item(i).getNodeValue(),
            			attributeForNode2.item(i).getNodeValue()
            		);
                	Assert.assertEquals(
                	        "Attribute " + attributeForNode1.item(i).getNodeName() + " doesnot equal to " + attributeForNode2.item(i).getNodeName(),
                	        attributeForNode1.item(i).getNodeName(),
                	        attributeForNode2.item(i).getNodeName()
                	);
				}
            }
        }
    	else {
            if(attributeForNode2 != null)
            {
            	Assert.fail("One of attribute is null");
            }
        }

        for (int i = 0; i < nodeList.size(); i++) {
            Node childNode1 = (Node) nodeList.get(i);
            for (int j = 0; j < nodeList2.size(); j++) {
                try {
                    compareNodes(childNode1, (Node) nodeList2.get(j),ignoredTags);
                    break;


                } catch (Throwable e) {
                    if(j == nodeList2.size() - 1)
                    {
                        throw (Error)e;
                    }
                }
            }

        }

    }


    private static ArrayList getChildNodes(NodeList list)
    {
        ArrayList arrayList = new ArrayList();
        if(list != null)
        {
             for (int i = 0; i < list.getLength(); i++) {
                 Node node = list.item(i);
                 if(node.getNodeType() == Node.TEXT_NODE)
                 {
                     String value = node.getNodeValue().trim();

                     if(value.length() != 0)
                     {
                         node.setNodeValue(value);
                         arrayList.add(node);
                     }
                 }
                 else
                 {
                     arrayList.add(node);
                 }
             }
        }
        return arrayList;

    }
}
