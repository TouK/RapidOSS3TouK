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
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

importData(web, "sampleRiData.xml");
return "Successfully created the sample RI data"

def importData(web, fname){
	def slurper = new XmlSlurper();
	def data = slurper.parse(fname);
	def idmap = [:];
	def relation = ["connectedVia"];

	data.Objects.Object.each{obj->
		def props = [:];
		props.putAll(obj.attributes());
		def modelName = props.modelName;
		if (modelName=="RsEventJournal"){
			props.eventId = idmap[props.eventId];		
			def timeStampFormat = "EEE MMM dd H:mm:ss z yyyy";
			def dateFormatter = new SimpleDateFormat(timeStampFormat);	
			Date myFormattedDate = dateFormatter.parse(props.rsTime);
			props.rsTime = myFormattedDate;
		}
		def model = web.grailsApplication.getDomainClass(modelName).clazz;
		def oldIdFromXml = props.id;
		if (modelName=="RsRiHistoricalEvent"){
			props.activeId = oldIdFromXml;
		}
		props.remove("modelName");
		props.remove("id");
	
		def instance = model.add(props);
		if (instance.hasErrors()){
			println "Model:$modelName => ${instance.errors}"
		}
		else{
			if (modelName=="RsRiHistoricalEvent"){
				instance.activeId = instance.id;
			}
		}
		idmap.put(oldIdFromXml, instance.id);
	}	
	
	data.Relations.Relation.each{rel->
		relName=rel.attributes().name;
		fromModelName=rel.attributes().fromModel;
		toModelName=rel.attributes().toModel;		
		if (relation.contains(relName)){
			from=idmap[rel.attributes().fromObjectId];
			to=idmap[rel.attributes().toObjectId];	
			def fromModel = web.grailsApplication.getDomainClass(fromModelName).clazz;
			def toModel = web.grailsApplication.getDomainClass(toModelName).clazz;
			def toObj = toModel.get(id:to);
			def fromObj = fromModel.get(id:from);
			def relMap = [:];
			relMap.put(relName,toObj);
			fromObj.addRelation(relMap);
		}
	}
}