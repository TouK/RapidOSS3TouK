import grails.util.GrailsWebUtil
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

public class URLUtils
{
    private static final char SLASH = '/';
    private static final String ENTITY_AMPERSAND = "&";
    public static String createURL(String url, Map parameterValues) {
        if (parameterValues == null) parameterValues = Collections.EMPTY_MAP;
        GrailsWebRequest webRequest = (GrailsWebRequest) RequestContextHolder.currentRequestAttributes();
        return createURLWithWebRequest(url, parameterValues, webRequest);
    }

    private static String createURLWithWebRequest(String url, Map parameterValues, GrailsWebRequest webRequest) {
        HttpServletRequest request = webRequest.getCurrentRequest();
        StringBuffer actualUriBuf = new StringBuffer(url);
        appendRequestParams(actualUriBuf, parameterValues, request);
        return actualUriBuf.toString();
    }

    private static void appendRequestParams(StringBuffer actualUriBuf, Map params, HttpServletRequest request) {

        boolean querySeparator = false;

        for (Iterator i = params.keySet().iterator(); i.hasNext();) {
            Object name = i.next();
            if (!querySeparator) {
                actualUriBuf.append('?');
                querySeparator = true;
            }
            else {
                actualUriBuf.append(ENTITY_AMPERSAND);
            }
            Object value = params.get(name);
            appendRequestParam(actualUriBuf, name, value, request);

        }
    }

    private static void appendRequestParam(StringBuffer actualUriBuf, Object name, Object value, HttpServletRequest request) {
        if (value == null)
            value = "";

        actualUriBuf.append(urlEncode(name, request)).append('=').append(urlEncode(value, request));
    }

    private static String urlEncode(Object obj, ServletRequest request) {
        try {
            String charset = request.getCharacterEncoding();

            return URLEncoder.encode(obj.toString(), (charset != null) ? charset : GrailsWebUtil.DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException ex) {
            throw new Exception("Error creating URL, cannot URLEncode to the client's character encoding: " + ex.getMessage(), ex);
        }
    }

}