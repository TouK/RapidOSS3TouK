/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 3, 2009
 * Time: 6:04:36 PM
 * To change this template use File | Settings | File Templates.
 */
def getConfiguration(mapType)
{
    CONFIG=[:]
    CONFIG.DEFAULT_NODE_MODEL="RsComputerSystem";
    CONFIG.USE_DEFAULT_NODE_MODEL=true; //if false nodeModel will be extracted from node name parameters
    CONFIG.NODE_PROPERTY_MAPPING=["displayName":"name","model":"model","type":"className"];

    CONFIG.USE_MAP_TYPE=false;
    CONFIG.DEFAULT_MAP_TYPE="default"; //used if mapType is not passed as a parameter to script

    CONFIG.DEFAULT_CONNECTION_MODEL="RsLink";
    CONFIG.CONNECTION_SOURCE_PROPERTY="a_ComputerSystemName";
    CONFIG.CONNECTION_TARGET_PROPERTY="z_ComputerSystemName";
    CONFIG.CONNECTION_SOURCE_CLASS_PROPERTY="a_RsClassName";
    CONFIG.CONNECTION_TARGET_CLASS_PROPERTY="z_RsClassName";

    return CONFIG;
}
