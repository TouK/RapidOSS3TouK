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
package com.ifountain.rcmdb.domain.generation
/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Apr 27, 2008
 * Time: 12:22:51 AM
 * To change this template use File | Settings | File Templates.
 */
class ClassParser {
    static final String classLinePattern = "\\s*(public|private|protected|)\\s*class\\s+\\S+(\\s+extends\\s+\\S+)*.*";
    static final String packageLinePattern = "\\s*package\\s+\\S+";
    static final String importLinePattern = "\\s*import.*";
    static final String classDefinitionReplacementPattern = "\\s*(public|private|protected|)\\s*class\\s+\\S+(\\s+extends\\s+\\S+)*[^{]";
    static final String AUTO_GENERATED_CODE_COMMENT = "//AUTO_GENERATED_CODE";
    def static ClassContent parseClass(File classFile)
    {
        int lastType = -1;
        ClassContent classContent = new ClassContent();
        def currentLines = [];
        def autoGeneratedTypeStarted = false;
        def classLineStarted = false;
        classFile.eachLine {String line->
            def type = ClassContentLine.LINE;
            if(line.matches(packageLinePattern))
            {
                type = ClassContentLine.PACKAGE_DECLERATION_LINE;
            }
            else if(line.matches(importLinePattern))
            {
                type = ClassContentLine.IMPORT_LINE;
            }
            else if(line.matches(classLinePattern))
            {
                type = ClassContentLine.CLASS_DECLERATION_LINE;
                if(line.trim().endsWith("{"))
                {
                    classLineStarted = false;
                }
                else
                {
                    classLineStarted = true;
                }
            }
            else if(classLineStarted)
            {
                type = ClassContentLine.CLASS_DECLERATION_LINE;
                if(line.trim().endsWith("{"))
                {
                    classLineStarted = false;
                }
            }
            else if(autoGeneratedTypeStarted || line.indexOf(AUTO_GENERATED_CODE_COMMENT) > 0)
            {
                type = ClassContentLine.AUTO_GENERATED_LINE;
                if(autoGeneratedTypeStarted && line.indexOf(AUTO_GENERATED_CODE_COMMENT) > 0)
                {
                    autoGeneratedTypeStarted = false;
                }
                else
                {
                    autoGeneratedTypeStarted = true;                    
                }
            }
            classContent.addLine (line, type);
        }

        return classContent;
    }
}

class ClassContentLine
{
    static int LINE = -1;
    static int PACKAGE_DECLERATION_LINE = 0;
    static int IMPORT_LINE = 1;
    static int CLASS_DECLERATION_LINE = 2;
    static int REMAINING_LINE = 3;
    static int AUTO_GENERATED_LINE = 4;
    String line;
    int type;

    public boolean equals(Object obj) {
        if(obj instanceof ClassContentLine)
        {
            def res = obj.line.equals(line) && obj.type == type;
            return res;
        }
        return super.equals(obj); //To change body of overridden methods use File | Settings | File Templates.
    }

    public String toString() {
        return "[$line] ${type}"
    }

    


}

class ClassContent
{
    def parentClass;
    def implementedClasses;
    def lines = [];
    def classDeclerationContent="";
    def ClassContent()
    {
    }

    def addLine(line, type)
    {
        lines += new ClassContentLine(line:line, type:type);
        if(type == ClassContentLine.CLASS_DECLERATION_LINE)
        {
            classDeclerationContent += line + " ";
        }
    }

    public boolean equals(Object obj) {
        if(obj instanceof ClassContent)
        {
            return lines.equals(obj.lines);
        }
        return super.equals(obj); //To change body of overridden methods use File | Settings | File Templates.
    }

    def getLines(int type)
    {
        return lines.findAll{line->
            line.type == type;
        }
    }

    def getParentClass()
    {
        def parts = org.apache.commons.lang.StringUtils.substringAfter(classDeclerationContent, " extends ").trim().split(" ", -1);
        def extendedClass = org.apache.commons.lang.StringUtils.substringBefore(parts[0], "{");
        if(extendedClass.length() == 0)
        {
            return null;
        }
        return extendedClass;
    }
    def getImplementedClasses()
    {
        def parts = org.apache.commons.lang.StringUtils.substringAfter(classDeclerationContent, " implements ").trim().split(" ", -1);
        def implementedClasses = [];
        parts.each {implementedClass->
            if(implementedClass.length() != 0 && implementedClass != "{")
            {
                implementedClass = org.apache.commons.lang.StringUtils.substringBefore(implementedClass, "{");
                implementedClass = org.apache.commons.lang.StringUtils.substringBefore(implementedClass, ",");
                implementedClasses += implementedClass;
            }
        }
        return implementedClasses;
    }

    public String toString() {
        def buffer = new StringBuffer();
        lines.each
        {
            buffer.append(it.line).append("\n");
        }
        return buffer.toString(); //To change body of overridden methods use File | Settings | File Templates.
    }



    
}