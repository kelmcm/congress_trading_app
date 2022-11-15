<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="project4task2.congresstradingwebservice.Log" %>
<%@ page import="java.util.HashMap" %>
<!DOCTYPE html>
<html>
<head>
    <title>Congress Trading App, Analytics Dashboard</title>
    <style>
        .keyValue {
            width: 30%;
            height: 50px;
            border: 1px solid black;
            float: left;
        }
    </style>
</head>
<body>
<button onclick="location.reload();" style="float: right">Refresh</button>
<h1><%= "Congress Trading Analytics Dashboard" %></h1>
<table border="1" style="text-align: center" width="100%">
    <tr>
        <th><b>Total requests</b></th>
        <th><b>Average process time from QuiverQuantAPI (ms)</b></th>
        <th><b>Average number of trades per request:</b></th>
    </tr>
    <tr>
        <td><%=request.getAttribute("totalRequests")%></td>
        <td><%=request.getAttribute("averageAPIResponseTime")%></td>
        <td><%=request.getAttribute("averageRecords")%></td>
    </tr>
</table>
<h3>Requests by mobile device:</h3>
<% HashMap<String, Integer> mapMobileDevice = (HashMap<String, Integer>) request.getAttribute("mapMobileDevice"); %>
<table border="1">
    <tr>
        <th><b>Mobile device</b></th>
        <th><b>Count</b></th>
    </tr>
    <% for(HashMap.Entry<String, Integer> md : mapMobileDevice.entrySet()) {%>
        <tr>
            <td><%=md.getKey()%></td>
            <td style="text-align: center"><%=md.getValue()%></td>
        </tr>
    <%}%>
</table>

<h3>Requests by ticker:</h3>
<% HashMap<String, Integer> mapTickers = (HashMap<String, Integer>) request.getAttribute("mapTickers"); %>
<table border="1">
    <tr>
        <th><b>Ticker</b></th>
        <th><b>Count</b></th>
    </tr>
    <% for(HashMap.Entry<String, Integer> mt : mapTickers.entrySet()) {%>
    <tr>
        <td><%=mt.getKey()%></td>
        <td style="text-align: center"><%=mt.getValue()%></td>
    </tr>
    <%}%>
</table>

<h3>Requests by system language:</h3>
<% HashMap<String, Integer> mapLanguage = (HashMap<String, Integer>) request.getAttribute("mapLanguage"); %>
<table border="1">
    <tr>
        <th><b>System language</b></th>
        <th><b>Count</b></th>
    </tr>
    <% for(HashMap.Entry<String, Integer> ml : mapLanguage.entrySet()) {%>
    <tr>
        <td><%=ml.getKey()%></td>
        <td style="text-align: center"><%=ml.getValue()%></td>
    </tr>
    <%}%>
</table>

<h3>Logs</h3>
<%--Source: https://www.geeksforgeeks.org/getattribute-passing-data-from-server-to-jsp/#:~:text=1)%20First%20create%20data%20at,retrieved%2C%20in%20a%20tabular%20form.--%>
<table border="1">
    <tr>
        <th><b>Requested at</b></th>
        <th><b>Mobile device</b></th>
        <th><b>Ticker</b></th>
        <th><b>System language</b></th>
        <th><b>Number of trades</b></th>
        <th><b>Process time (ms)</b></th>
    </tr>
    <%ArrayList<Log> logs =
            (ArrayList<Log>)request.getAttribute("logs");
        for(Log log : logs){%>
    <tr>
        <td><%=log.getRequestedAt()%></td>
        <td><%=log.getMobileDevice()%></td>
        <td><%=log.getTicker()%></td>
        <td><%=log.getLanguage()%></td>
        <td><%=log.getNumberOfRecords()%></td>
        <td><%=log.getProcessTime()%></td>
    </tr>
    <%}%>
</table>
</body>
</html>