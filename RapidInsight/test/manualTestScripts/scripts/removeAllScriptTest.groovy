/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Aug 22, 2008
 * Time: 5:07:01 PM
 * To change this template use File | Settings | File Templates.
 */

import script.*

def domClasses = web.grailsApplication.getDomainClasses();

domClasses.each{
	def modelName = it.clazz.name;
	def model = it.clazz
	if (modelName.indexOf('.')==-1 && model.superclass.name == Object.name){
		def keyProps = model.keySet();
		def keyMap = [:]
		def numberValue = 1
		keyProps.each{prop->
			if (prop.type.getName()=="java.lang.String")
				keyMap.put(prop.name,prop.name)
			else{
				keyMap.put(prop.name,numberValue)
				numberValue++
			}
		}
		model.add(keyMap)
	}
}

domClasses.each{
	def modelName = it.clazz.name;
	def model = it.clazz
	if (modelName.indexOf('.')==-1 && model.superclass.name == Object.name){
		def instanceCnt = model.list().size()
		assert instanceCnt>0
	}
}

CmdbScript.runScript("removeAll")

domClasses.each{
	def modelName = it.clazz.name;
	def model = it.clazz
	if (modelName.indexOf('.')==-1 && model.superclass.name == Object.name){
		def instanceCnt = model.list().size()
		assert instanceCnt == 0
	}
}