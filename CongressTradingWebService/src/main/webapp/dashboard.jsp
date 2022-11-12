<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>dashboard</title>
</head>
<body>
<h1><%= "Dashboard Upstanding Citizen" %>
</h1>
<p>Mobile Devices: <%=request.getAttribute("mapMobileDevice")%></p>
<p>Tickers: <%=request.getAttribute("mapTickers")%></p>
<p>Request methods: <%=request.getAttribute("mapRequestMethod")%></p>
<p>Average response time from API: <%=request.getAttribute("averageAPIResponseTime")%></p>
<p>Average number of records from API: <%=request.getAttribute("averageRecords")%></p>
<p>Total requests: <%=request.getAttribute("totalRequests")%></p>
</body>
</html>