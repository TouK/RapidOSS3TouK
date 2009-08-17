package com.ifountain.rcmdb.mobile
/**
 * Created by IntelliJ IDEA.
 * User: ifountain
 * Date: Aug 14, 2009
 * Time: 1:49:02 PM
 * To change this template use File | Settings | File Templates.
 */
class MobileUtils {

    public static def isMobile(request) {
        String userAgent = String.valueOf(request.getHeader("user-agent")).toLowerCase();
    	if(userAgent.indexOf("mobile") >= 0 || userAgent.indexOf("ipod") >= 0 || userAgent.indexOf("iphone") >= 0)         {
        	return true;
        }
        return false;
    }  

}