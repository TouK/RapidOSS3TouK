import com.ifountain.rcmdb.util.DataStore

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
import org.apache.log4j.Logger

public class RsFileReader{

	def filePath, readMode;
	static Logger logger;
	final static TAIL = "TAIL"
	final static READONCE = "READONCE"
	final static READFROMSTART = "READFROMSTART"
	
	public RsFileReader(filePath, readMode){
		this.logger = Logger.getRootLogger();
		this.filePath = filePath;
		this.readMode = readMode;
	}	

	public RsFileReader(Logger logger, filePath, readMode){
		this.logger = logger;
		this.filePath = filePath;
		this.readMode = readMode;
	}	
	
	// Do not modify unless you have specific needs that require changes to this method.
	// This method will return you a list of lines appended to the file you are listening to.
	// Throws exception if file cannot be accessed! Handle exception as needed in the calling script.
	def getLines(){
		if (readMode == READONCE) DataStore.remove(filePath);
		def  lines = [];
		def raf;
		try{
			raf = new RandomAccessFile(filePath, "r");
			def fileLength = raf.length();
			def previousFileLength = DataStore.get(filePath);
			def inputFile = new File(filePath);
	
			if(previousFileLength == null){	// first time reading the file
				if(readMode == TAIL){
					DataStore.put(filePath,fileLength);
					return lines;
				}
				else{
					lines = inputFile.readLines();	
					DataStore.put(filePath,fileLength);
				}
			}
			else{
				if(fileLength < previousFileLength){  // file rolled, we will read from the start
					lines = inputFile.readLines();
					DataStore.put(filePath,fileLength);
				}
				else if(fileLength == previousFileLength){ // no appends to the file
					return lines;
				}
				else{
					raf.seek(previousFileLength);
					while (raf.getFilePointer() < raf.length()) {
						lines.add(raf.readLine());
					}
					DataStore.put(filePath,fileLength);
				}
			}
		}
		catch(Exception e){
			logger.error(e.getMessage());
		}
		finally{
			if(raf!=null) raf.close();
		}
		return lines;
	}
	
	
	static def getDelimitedValues(line, delimiter){
		return Arrays.asList(line.split(delimiter));
	}
	
	static def getFixedWidthValues(line, widthList){
		return getFixedWidthValues(line, widthList, false);
	}
	
	// if line length is greater than total widthList, remainder of the line is ignored
	// if smaller, only the fields that 
	static def getFixedWidthValues(line, widthList, trim){
		def values = [];
		def index = 0;
		for (width in widthList){
// 			logger.info("line.length(): ${line.length()}");
			if(width+index <= line.length()){
				def endIndex = width+index;
				def value = line.substring(index, endIndex); 
				if(trim){
					values.add(value.trim());
				}
				else{
					values.add(value);
				}
				index = endIndex;
			}
		}
		return values;
	}
	
	// returns a map of names and values
	static def getNameValueMap(line, fieldDelim, nameValueDelim){
		def nameValues = [:];
		def fields = line.split(fieldDelim)
 		logger.info("Fields: $fields");
		for (field in fields){
			def nameValue = field.split(nameValueDelim);
 			logger.info("nameValue: $nameValue");
			nameValues.put(nameValue[0], nameValue[1]);
		}
		return nameValues;
	}
	
}