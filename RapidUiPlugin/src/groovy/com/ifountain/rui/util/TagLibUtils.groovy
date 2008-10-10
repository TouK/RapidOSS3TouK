package com.ifountain.rui.util

import groovy.xml.MarkupBuilder

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
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Oct 10, 2008
 * Time: 11:29:29 AM
 */
class TagLibUtils {
   static def getConfigAsXml(tagName, attrs, validAttrs, innerXML){
        def writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        def attMap = [:]
        def listAttributes = [:]
        validAttrs.each{
            def value = attrs.get(it);
            if(value instanceof List){
                listAttributes.put(it, value)
            }
            else{
                attMap.put(it, value);    
            }

        }
        if(innerXML != null){
           builder."${tagName}"(attMap){
               listAttributes.each{key, valueList ->
                   builder."${key}"(){
                       valueList.each{value ->
                           builder.Item(value)
                       }
                   }
               }
               builder.yieldUnescaped(innerXML)
           }
        }
        else{
           builder."${tagName}"(attMap){
               listAttributes.each{key, valueList ->
                   builder."${key}"(){
                       valueList.each{value ->
                           builder.Item(value)
                       }
                   }
               }
           }
        }
        return  writer.toString();
    }
    static def getConfigAsXml(tagName, attrs, validAttrs){
        return getConfigAsXml(tagName, attrs, validAttrs, null);
    }
}