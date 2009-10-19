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
        if (userAgent.indexOf("mobile") > -1
                || userAgent.indexOf("ipod") > -1
                || userAgent.indexOf("iphone") > -1
                || userAgent.indexOf("blackberry") > -1
                || userAgent.indexOf("iemobile") > -1
                || userAgent.indexOf("opera mini") > -1
                || userAgent.indexOf("opera mobi") > -1) {
            return true;
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