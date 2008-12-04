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
		props.remove("modelName");
		props.remove("id");
		def instance = model.add(props);
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