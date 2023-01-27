# congress_trading_app
Repository for Distributed Systems Project 4, Fall 2022. 

#### Description
Our goal is to create a simple tool to bring transparency to stock market trades done by members of Congress (House and Senate). Voters will be able to educate themselves on what their members of Congress are doing in the stock market. 

Our mobile application will prompt the user for a stock ticker (ex. $AAPL, $MSFT). The application will then send an HTTP request to the QuiverQuant API and return all the members of Congress who have recently traded on that stock ticker and the details about their trade: Transaction (Purchase or Sale), Amount, and Date.

#### Includes
- Android mobile application
- Analytics tracking dashboard
- MongoDB database

#### API
QuiverQuant
https://www.quiverquant.com/  
https://api.quiverquant.com/docs/ 
