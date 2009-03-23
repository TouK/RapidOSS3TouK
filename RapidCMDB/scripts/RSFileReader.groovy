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
public class RSFileReader{

	def filePath, tailMode, alwaysFromStart;
	
	public RSFileReader(filePath, tailMode, alwaysFromStart){
		this.filePath = filePath;
		this.tailMode = tailMode;
		this.alwaysFromStart = alwaysFromStart;
	}
	
	public RSFileReader(filePath, tailMode){
		this.filePath = filePath;
		this.tailMode = tailMode;
		this.alwaysFromStart = false;
	}
	
	public RSFileReader(filePath){
		this.filePath = filePath;
		this.tailMode = false;
		this.alwaysFromStart = false;
	}	

	// Do not modify unless you have specific needs that require changes to this method.
	// This method will return you a list of lines appended to the file you are listening to
	def getLines(){
		if (alwaysFromStart) DataStore.remove(filePath);
		
		//def t3 = System.currentTimeMillis();
		def raf = new RandomAccessFile(filePath, "r");
		def fileLength = raf.length();
		def previousFileLength = DataStore.get(filePath);
		def  lines = [];
		def inputFile = new File(filePath);

		if(previousFileLength == null){	// first time reading the file
			if(tailMode){
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
		
		//def t4 = System.currentTimeMillis();
		//def readDuration = t4 - t3;
		//println "READ LINE COUNT: ${lines.size()} in ${readDuration} ms. Lines are: ${lines}";
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
			//println "width+index: " + width+index;
			//println "line.length(): " + line.length();
			if(width+index <= line.length()){
				def endIndex = width+index;
				def value = line.substring(index, endIndex); 
			//	println "value: " + value;
				if(trim){
					values.add(value.trim());
				}
				else{
					values.add(value);
				}
				index = endIndex;
			//	println "new index: " + index;
			}
		}
		return values;
	}
	
	// returns a map of names and values
	static def getNameValueMap(line, fieldDelim, nameValueDelim){
		def nameValues = [:];
		def fields = line.split(fieldDelim)
		println "fields: " + fields;
		for (field in fields){
			def nameValue = field.split(nameValueDelim);
			println "nameValue: " + nameValue;
			nameValues.put(nameValue[0], nameValue[1]);
		}
		return nameValues;
	}
	
}