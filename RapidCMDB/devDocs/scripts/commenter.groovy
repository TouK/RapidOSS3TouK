baseDir = "D:/tempworkspace/IdeaWorkspace/";
OODirList = [baseDir+"RapidModules/RcmdbCommons"];

IFDirList = []
//OODirList = [baseDir+"Apg/src/groovy", baseDir+"Hyperic"];
//IFDirList = [baseDir+"Smarts",baseDir+"Netcool" ];

groovyPattern = ~".*\\.groovy";
javaPattern = ~".*\\.java";
jsPattern = ~".*\\.js";
OO_FLAG = 0;
IF_FLAG = 1;
copyrightDetectionStr1 = "copyright";
copyrightDetectionStr2 = "iFountain";

exceptListPattern=[]
exceptListPattern.add(".*Code licensed under the BSD License")
exceptListPattern.add(".*Licensed under the Apache License")
exceptListPattern.add(".*Jack Slocum")
exceptListPattern.add(".*Thomas Fuchs")
exceptListPattern.add(".*MIT-style license")







IFLicenseText = "/*\r\n" +
"* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be\r\n" +
"* noted in a separate copyright notice. All rights reserved.\r\n" +
"*/\r\n";

OOLicenseText = "/* \r\n" +
"* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be\r\n" +
"* noted in a separate copyright notice. All rights reserved.\r\n" +
"* This file is part of RapidCMDB.\r\n" +
"*\r\n" +
"* RapidCMDB is free software; you can redistribute it and/or modify\r\n" +
"* it under the terms version 2 of the GNU General Public License as\r\n" +
"* published by the Free Software Foundation. This program is distributed\r\n" +
"* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without\r\n" +
"* even the implied warranty of MERCHANTABILITY or FITNESS FOR A\r\n" +
"* PARTICULAR PURPOSE. See the GNU General Public License for more\r\n" +
"* details.\r\n" +
"*\r\n" +
"* You should have received a copy of the GNU General Public License\r\n" +
"* along with this program; if not, write to the Free Software\r\n" +
"* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307\r\n" +
"* USA.\r\n" +
"*/\r\n";



OODirList.each{
    println("Open Source");
    println("GROOVY");
    editFiles(it, OO_FLAG, groovyPattern);
    println("JAVA");
    editFiles(it, OO_FLAG, javaPattern);
    println("JS");
    editFiles(it, OO_FLAG, jsPattern);
}

IFDirList.each{
    println("Ifountain");
    println("GROOVY");
    editFiles(it, IF_FLAG, groovyPattern);
    println("JAVA");
    editFiles(it, IF_FLAG, javaPattern);
    println("JS");
    editFiles(it, IF_FLAG, jsPattern);
}

return "DONE";


def editFiles(pathStr, flag, pattern){
    
    def path = new File(pathStr);
    //println("Processing directory: ${path.canonicalPath}");


    path.eachDir{
    	editFiles(it.canonicalPath, flag, pattern);
    }

    path.eachFileMatch(pattern) {fname->        
		content = fname.text;
		if((content.indexOf(copyrightDetectionStr1)==-1) || (content.indexOf(copyrightDetectionStr2)==-1)){
            def skipFile=false
            exceptListPattern.each{ exceptPattern ->
               def matcher= ( content =~ exceptPattern )               
               if(matcher.size()>0)
               {
                   skipFile=true
                   return;
               }
               
            }
            if(skipFile)
            {
                println("    Skipping file, because its in exceptList ${fname}");
            }
            else{
                if(flag == OO_FLAG){
                    println("\t    will add OO license comment to ${fname}");
                    //fname.write(OOLicenseText + content);
                }
                else{
                    println("\t    will add IF license comment to ${fname}");
                    //fname.write(IFLicenseText + content);
                }
            }

		}
		else{
			println("    License comment already exists in ${fname}");
		}
    }
}
