
<%@page language="java"
	contentType="text/xml"
	session="true"
	import="
		java.util.Calendar,
		javax.servlet.http.HttpServletRequest,
		javax.servlet.http.HttpServletResponse,
		org.opennms.web.MissingParameterException,
		org.opennms.web.WebSecurityUtils,
		org.opennms.web.graph.GraphResults,
		org.opennms.web.graph.RelativeTimePeriod,
		org.opennms.web.svclayer.GraphResultsService,
        org.springframework.web.context.WebApplicationContext,
        org.springframework.web.context.support.WebApplicationContextUtils,
        org.opennms.web.XssRequestWrapper,
        org.apache.commons.lang.StringEscapeUtils,
        org.opennms.web.graph.Graph"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<%!

	public GraphResultsService m_graphResultsService;
	public RelativeTimePeriod[] s_periods = RelativeTimePeriod.getDefaultPeriods();

    public void init() throws ServletException {
	    WebApplicationContext webAppContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		m_graphResultsService = (GraphResultsService) webAppContext.getBean("graphResultsService", GraphResultsService.class);
    }


	String[] requiredParameters = new String[] {
                "resourceId",
                "reports"
        };

%>

<%



        for (String requiredParameter : requiredParameters) {
            if (request.getParameter(requiredParameter) == null) {
                throw new MissingParameterException(requiredParameter,
                                                    requiredParameters);
            }
        }

        String[] resourceIds = WebSecurityUtils.sanitizeString(request.getParameterValues("resourceId"));
        String[] reports = WebSecurityUtils.sanitizeString(request.getParameterValues("reports"));


        String relativeTime = "";


        long startLong=0;
        long endLong=0;

        GraphResults model =
            m_graphResultsService.findResults(resourceIds,
                                              reports, startLong,
                                              endLong, relativeTime);
       pageContext.setAttribute("results", model);
%>

<%
	for(GraphResults.GraphResultSet resultSet:model.getGraphResultSets())
	{
		out.println("<Graphs>");
		for(Graph graph:resultSet.getGraphs())
		{
			String url=StringEscapeUtils.escapeXml("graph/graph.png?resourceId="+resultSet.getResource().getId()+"&report="+graph.getName());
			pageContext.setAttribute("graphurl", url);
			%>
				<Graph url="${graphurl}"/>
			<%
//			out.println("<Graph url=\""+url+"\"/>");
		}
		out.println("</Graphs>");
	}

%>

