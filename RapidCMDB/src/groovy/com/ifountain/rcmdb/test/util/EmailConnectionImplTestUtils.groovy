package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 24, 2008
 * Time: 4:07:40 PM
 * To change this template use File | Settings | File Templates.
 */

import com.ifountain.comp.test.util.CommonTestUtils

class EmailConnectionImplTestUtils {

   public static Map getConnectionParams(){
        def paramMap=["SmtpHost":"Smtp.SmtpHost","SmtpPort":"Smtp.SmtpPort","Username":"Smtp.Username","Password":"Smtp.Password","Protocol":"Smtp.Protocol"]

        def params=[:]
        paramMap.each{ key , propKey ->
            params[key]= CommonTestUtils.getTestProperty(propKey);
        }

        return params;
    }

    public static Map getSendEmailParams(){
        def paramMap=["from":"SendEmail.from","to":"SendEmail.to","subject":"SendEmail.subject","body":"SendEmail.body"]

        def params=[:]
        paramMap.each{ key , propKey ->
            params[key]= CommonTestUtils.getTestProperty(propKey);
        }

        return params;
    }
}