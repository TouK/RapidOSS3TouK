package com.ifountain.rcmdb.domain.util
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
 * User: Administrator
 * Date: Apr 24, 2008
 * Time: 1:20:57 PM
 * To change this template use File | Settings | File Templates.
 */
class Relation
{
    public static int ONE_TO_ONE = 0;
    public static int ONE_TO_MANY = 1;
    public static int MANY_TO_MANY = 2;
    public static int MANY_TO_ONE = 3;
    String name;
    String otherSideName;
    String upperCasedName;
    String upperCasedOtherSideName;
    Class otherSideCls;
    Class cls;
    boolean isCascade = false;
    int type;
    public Relation(String name, String otherSideName, Class cls, Class otherClass, int relType)
    {
        this.name = name;
        this.otherSideName = otherSideName;
        this.otherSideCls = otherClass;
        this.cls = cls;
        this.upperCasedName = DomainClassUtils.getUppercasedPropertyName(name);
        if(otherSideName)
        {
            this.upperCasedOtherSideName = DomainClassUtils.getUppercasedPropertyName(otherSideName);
        }
        this.type = relType;
    }


    def hasOtherSide()
    {
        return otherSideName != null;
    }
    def isOneToOne()
    {
        return type == ONE_TO_ONE;
    }

    def isOneToMany()
    {
        return type == ONE_TO_MANY;
    }

    def isManyToOne()
    {
        return type == MANY_TO_ONE;
    }

    def isManyToMany()
    {
        return type == MANY_TO_MANY;
    }

}