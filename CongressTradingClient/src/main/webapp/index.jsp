<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Mock Android App</title>
</head>
<body>
<form action="getTickerTrades" method="GET">
    <label for="letter">Enter a ticker.</label>
    <input id="ticker" type="text" name="ticker" value="" /><br>
    <input type="submit" value="Click Here" />
</form>
<p id="response_text"></p>
</body>
</html>
