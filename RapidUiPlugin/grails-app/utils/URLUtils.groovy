public class URLUtils
{
    public static String createURL(String url, Map params)
    {
        if(params == null)
        {
            params = {};
        }
        String postData = "";
        params.each{String paramName, Object paramValue->
            postData = postData + paramName + "=" + java.net.URLEncoder.encode(String.valueOf(paramValue))+"&";
        }
        if(postData != "")
        {
            postData = postData.substring(0, postData.length()-1);
            if(url.indexOf("?") >= 0)
            {
                url = url + "&" + postData;
            }
            else
            {
                url = url + "?" + postData;
            }
        }
        return url;
    }
}