package datasource
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Aug 20, 2009
 * Time: 8:45:43 AM
 * To change this template use File | Settings | File Templates.
 */
class HttpActionUtils {
    public static String getCompleteUrl(String baseUrl, String actionUrl){
        if(baseUrl.length() > 0 && baseUrl.charAt(baseUrl.length() -1) == '/'){
            baseUrl = baseUrl.substring(0, baseUrl.length() -1)
        }
        if(actionUrl.length() > 0 && actionUrl.charAt(0) == '/'){
            actionUrl = actionUrl.substring(1);
        }
        return baseUrl + "/" + actionUrl;
    }
}