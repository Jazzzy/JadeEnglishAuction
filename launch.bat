set CLASSPATH=%CLASSPATH%;.;c:\jade\lib\jade.jar;c:\jade\lib\jadeTools.jar;c:\jade\lib\Base64.jar;c:\jade\lib\http.jar

::Seller
cd C:\Users\gladi\Documents\GitHub\JadeEnglishAuction\Seller\out\production\Seller

java jade.Boot -gui -agents Seller:jade.BookSellerAgent

pause






