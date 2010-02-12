/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 8, 2009
 * Time: 9:13:34 AM
 * To change this template use File | Settings | File Templates.
 */

//you can access to the map via
//for top node : http://localhost:12222/RapidSuite/index/maps.gsp?name=sampleNode&rsClassName=RsTopologyObject&mapType=sampleMap
//for middle nodes :   http://localhost:12222/RapidSuite/index/maps.gsp?name=ev0&rsClassName=RsEvent&mapType=sampleMap
//for lowest nodes :   http://localhost:12222/RapidSuite/index/maps.gsp?name=computer0&rsClassName=RsComputerSystem&mapType=sampleMap

def object=RsTopologyObject.add(name:"sampleNode",displayName:"sampleDisplayName",className:"SampleClass");
3.times{
	def event=RsEvent.add(name:"ev${it}");
	RsMapConnection.add(name:"con${it}",mapType:"sampleMap",a_Name:object.name,a_RsClassName:object.class.name,z_Name:event.name,z_RsClassName:event.class.name);
	def subNode=RsComputerSystem.add(name:"computer${it}",displayName:"computer${it}",className:"ComputerSystem");
	RsMapConnection.add(name:"subcon${it}",mapType:"sampleMap",a_Name:event.name,a_RsClassName:event.class.name,z_Name:subNode.name,z_RsClassName:subNode.class.name);
}




return """

for top node : http://localhost:12222/RapidSuite/index/maps.gsp?name=sampleNode&rsClassName=RsTopologyObject&mapType=sampleMap
<br>for middle nodes :   http://localhost:12222/RapidSuite/index/maps.gsp?name=ev0&rsClassName=RsEvent&mapType=sampleMap
<br>for lowest nodes :   http://localhost:12222/RapidSuite/index/maps.gsp?name=computer0&rsClassName=RsComputerSystem&mapType=sampleMap

"""