# SYSC3303_A1

This project contains three Java classes:

Sever.java
IntermediateHost.java
Client.java

Server.java simulates an actual server. It receives Client requests (via the IntermediateHost) and returns a response to the Client (again, via the IntermediateHost).

IntermediateHost.java acts as a bridge between the Client & Server. Neither the Client nor the Server know that the IntermediateHost exists.

Client.java simulates a client that sends read & write requests to the Server (via the IntermediateHost). It receives a response from the Server before sending out another request.


Setup Instructions:

In order to run this Client-Server simulation, the Sever and IntermediateHost must be running before the Client.

1. Run Server.java
2. Run IntermediateHost.java
3. Run Client.java
