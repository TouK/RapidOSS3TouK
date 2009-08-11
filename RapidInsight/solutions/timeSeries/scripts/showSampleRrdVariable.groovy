/**
 * User: ifountain
 * Date: Jul 3, 2009
 * Time: 10:36:17 AM
 */

def sampleRrdVariable = RrdVariable.get(name:"sampleRrdVariable")

def config = [:]
config["destination"] = "web"

sampleRrdVariable.graph(config)
