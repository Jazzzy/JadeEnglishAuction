set CLASSPATH=%CLASSPATH%;.;c:\jade\lib\jade.jar;c:\jade\lib\jadeTools.jar;c:\jade\lib\Base64.jar;c:\jade\lib\http.jar

::Buyers
cd C:\Users\gladi\Documents\GitHub\JadeEnglishAuction\Buyer\out\production\Buyer

java jade.Boot -container -container-name buyer -host localhost -agents Buyer2:jade.BookBuyerAgent

pause