<%@page language="java"
	contentType="text/xml"
	session="true"
	import="
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
        org.opennms.web.graph.Graph,
        java.util.*,
        org.opennms.web.element.*,
        org.opennms.netmgt.utils.IfLabel,
        org.opennms.netmgt.model.OnmsResource,
		org.opennms.web.svclayer.ResourceService,
		org.opennms.web.Util"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%!

	public GraphResultsService m_graphResultsService;
	public RelativeTimePeriod[] s_periods = RelativeTimePeriod.getDefaultPeriods();
	public ResourceService m_resourceService;

    public void init() throws ServletException {
	    WebApplicationContext webAppContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		m_graphResultsService = (GraphResultsService) webAppContext.getBean("graphResultsService", GraphResultsService.class);
		m_resourceService = (ResourceService) webAppContext.getBean("resourceService", ResourceService.class);
    }

	String[] requiredParameters = new String[] {
                //"resourceId",
                "reports",
                "nodeid",
                "type"
        };

%>
<%



        for (String requiredParameter : requiredParameters) {
            if (request.getParameter(requiredParameter) == null) {
                throw new MissingParameterException(requiredParameter,
                                                    requiredParameters);
            }
        }

        //String[] resourceIds = WebSecurityUtils.sanitizeString(request.getParameterValues("resourceId"));
        String[] reports = WebSecurityUtils.sanitizeString(request.getParameterValues("reports"));

        String type = request.getParameter("type");
        String nodeId = request.getParameter("nodeid");
		int nodeIdInt = Integer.parseInt(nodeId);

        List resourceIdList=new ArrayList();
        if(type.compareTo("node")==0)
        {
            resourceIdList.add("node["+nodeId+"].nodeSnmp[]");
        }
		else if(type.compareTo("ipinterface")==0)
		{
			String interfaceId = request.getParameter("ipinterfaceid");
			if(interfaceId==null)
			{
				throw new MissingParameterException("ipinterfaceid",requiredParameters);
			}


			Interface intf_db = ElementUtil.getInterfaceByParams(request);

			String ipAddr = intf_db.getIpAddress();
			int ifIndex = -1;
			if (intf_db.getIfIndex() > 0) {
				ifIndex = intf_db.getIfIndex();
			}

			String ifLabel;
			if (ifIndex != -1) {
			  ifLabel = IfLabel.getIfLabelfromIfIndex(nodeIdInt, ipAddr, ifIndex);
			}
			else {
			  ifLabel = IfLabel.getIfLabel(nodeIdInt, ipAddr);
			}

			 List<OnmsResource> resources = m_resourceService.findNodeChildResources(nodeIdInt);
	          for (OnmsResource resource : resources) {

	              if (resource.getName().equals(ipAddr) || resource.getName().equals(ifLabel)) {
		              resourceIdList.add(resource.getId());
	              }
	          }

		}

        String[] resourceIds=(String []) resourceIdList.toArray(new String[0]);

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
	out.println("<Graphs>");
	for(GraphResults.GraphResultSet resultSet:model.getGraphResultSets())
	{

		for(Graph graph:resultSet.getGraphs())
		{
			String url=StringEscapeUtils.escapeXml("graph/graph.png?resourceId="+resultSet.getResource().getId()+"&report="+graph.getName());
			pageContext.setAttribute("graphurl", url);
			%>
			<Graph url="${graphurl}"/>
			<%
//			out.println("<Graph url=\""+url+"\"/>");
		}

	}
	out.println("</Graphs>");

%>