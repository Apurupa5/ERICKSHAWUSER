# ERICKSHAWUSER
(Team Size-3)
ERickshaw is an android application to facilitate students of IIIT-Delhi to book the ERickshaw from this application.\
This Application has two different apps \
One is for the User side and another is for Driver Side\
A user can search for a location and can book a rickshaw based on the availability.\
He can get estimate fare of the ride by searching for the location and selecting it. \
After clicking on Ridenow button, we have geocoder service to get lat/long details from the entered destination address and then we calculate\
the distance between the pickup point and destination after which we give a ride estimate on it.\

Backend tables are updated accordingly after making a ride request.\

After making a ride request, the user side app will have a background task that continuously check for ride acceptance status in a table in back end. If within a stipulated amount of the ride status is accepted, then driver details are notified as a push notification to the user. If no driver accepts the request the ride is cancelled and we prompt the user to try booking again.\

There is a background service that timely gets available online drivers at the location.\

A request goes from user to all the available drivers who are 
online and within a certain radius of the user.\
If a driver accepts a request within a certain amount of time , the user is notified via a push notification and he can track the location of the driver in real time. \

Also the user has other features like viewing his ride history, viewing and editing his profile and registration for first time users etc.\

#######
Driver app is another separate of ERickshaw where it has the following features\
After logging in Drivers' present location is constantly updated in the database\


