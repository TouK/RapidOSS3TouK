baseDir = "C:/DATA/TempIF/RapidServer/RapidSuite";
OODirList = [baseDir+"/scripts/myscripts/dir1", baseDir+"/scripts/myscripts/dir3"];
IFDirList = [baseDir+"/scripts/myscripts/dir2"];

groovyPattern = ~/.*groovy/;
javaPattern = ~/.*java/;
jsPattern = ~/.*js/;
OO_FLAG = 0;
IF_FLAG = 1;
copyrightDetectionStr1 = "copyright";
copyrightDetectionStr2 = "iFountain";

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
    logger.warn("Open Source");
    logger.warn("GROOVY");
    editFiles(it, OO_FLAG, groovyPattern);
    logger.warn("JAVA");
    editFiles(it, OO_FLAG, javaPattern);
    logger.warn("JS");
    editFiles(it, OO_FLAG, jsPattern);
}	

IFDirList.each{
    logger.warn("Ifountain");
    logger.warn("GROOVY");
    editFiles(it, IF_FLAG, groovyPattern);	
    logger.warn("JAVA");
    editFiles(it, IF_FLAG, javaPattern);
    logger.warn("JS");
    editFiles(it, IF_FLAG, jsPattern);
}

return "DONE";


def editFiles(pathStr, flag, pattern){
    def path = new File(pathStr);
    logger.warn("Processing directory: ${path.canonicalPath}.");
    path.eachDir{
    	editFiles(it.canonicalPath, flag, pattern);
    }
    path.eachFileMatch(pattern) {fname-> 
		content = fname.text;
		if((content.indexOf(copyrightDetectionStr1)==-1) || (content.indexOf(copyrightDetectionStr2)==-1)){   
			if(flag == OO_FLAG){
				logger.warn("will add OO license comment to ${fname}"); 
				fname.write(OOLicenseText + content);
			}
			else{
				logger.warn("will add IF license comment to ${fname}"); 
				fname.write(IFLicenseText + content);
			}
		}
		else{
			logger.warn("License comment already exists in ${fname}");
		}
    }
}
