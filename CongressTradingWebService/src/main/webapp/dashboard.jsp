<%@ page import="project4task2.congresstradingwebservice.TradeRequest" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="java.util.ArrayList"%>
<%@ page import="project4task2.congresstradingwebservice.APIRequest" %>
<%@ page import="project4task2.congresstradingwebservice.HerokuResponse" %>
<!DOCTYPE html>
<html>
<head>
    <title>Congress Trading Analytics Dashboard</title>
</head>
<body>
<h1><%= "Congress Trading Analytics Dashboard" %>
</h1>
<p>Total requests: <%=request.getAttribute("totalRequests")%></p>
<p>Average response time from API: <%=request.getAttribute("averageAPIResponseTime")%></p>
<p>Average number of trades per request: <%=request.getAttribute("averageRecords")%></p>
<p>Mobile Devices: <%=request.getAttribute("mapMobileDevice")%></p>
<p>Tickers: <%=request.getAttribute("mapTickers")%></p>
<p>Request methods: <%=request.getAttribute("mapRequestMethod")%></p>
<h2>Logs</h2>
<%--Source: https://www.geeksforgeeks.org/getattribute-passing-data-from-server-to-jsp/#:~:text=1)%20First%20create%20data%20at,retrieved%2C%20in%20a%20tabular%20form.--%>
<table width="50%">
    <tr>
        <th><b>Requested At</b></th>
        <th><b>Mobile Device</b></th>
        <th><b>Ticker</b></th>
        <th><b>Request Method</b></th>
    </tr>
    <%ArrayList<TradeRequest> tr =
            (ArrayList<TradeRequest>)request.getAttribute("tradeRequestLog");
        for(TradeRequest t : tr){%>
    <%-- Arranging data in tabular form
    --%>
    <tr>
        <td><%=t.getRequestedAt()%></td>
        <td><%=t.getMobileDevice()%></td>
        <td><%=t.getTicker()%></td>
        <td><%=t.getRequestMethod()%></td>
    </tr>
    <%}%>
</table>
<table width="25%">
    <tr>
        <th><b>API Time to Process (ms)</b></th>
    </tr>
    <%ArrayList<APIRequest> ar =
            (ArrayList<APIRequest>)request.getAttribute("apiRequestLog");
        for(APIRequest a : ar){%>
    <tr>
        <td><%=a.getProcessTime()%></td>
    </tr>
    <%}%>
</table>
<table width="25%">
    <tr>
        <th><b>Number of Trades</b></th>
    </tr>
    <%ArrayList<HerokuResponse> hr =
            (ArrayList<HerokuResponse>)request.getAttribute("herokuResponseLog");
        for(HerokuResponse h : hr){%>
    <tr>
        <td><%=h.getNumberOfRecords()%></td>
    </tr>
    <%}%>
</table>
</body>
</html>