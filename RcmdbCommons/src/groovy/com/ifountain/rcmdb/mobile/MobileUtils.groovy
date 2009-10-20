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
        def userAgents = [
                "mobile", "ipod", "iphone", "blackberry", "iemobile", "opera mini", "opera mobi",
                "sonyericsson", "nokia", "panasonic", "lg-", "lge-", "lg/", "mot-", "android", "sie-",
                "windows ce", "htc-", "symbian", "palmos", "palmsource", "opwv", "docomo", "samsung", "sec-",
                "up.b", "up/"
        ];
        for(int i = 0; i < userAgents.size(); i ++){
            def uA = userAgents[i]
            if(userAgent.indexOf(uA) > -1){
                return true;
            }
        }
        return false;
    }

    public static def isIphone(request) {
        String userAgent = String.valueOf(request.getHeader("user-agent")).toLowerCase();
        if (userAgent.indexOf("ipod") > -1 || userAgent.indexOf("iphone") > -1) {
            return true;
        }

        return false;
    }

}