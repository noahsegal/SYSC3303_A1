import java.io.IOException;
import java.net.*;

public class IntermediateHost {

    private static final int NICE = 69;

    public DatagramSocket hostSendSocket;
    public DatagramSocket hostReceiveSocket;
    public DatagramSocket hostSendAndReceiveSocket;

    public DatagramPacket hostSendPacket;
    public DatagramPacket hostReceivePacketFromServer;
    public DatagramPacket hostReceivePacketFromClient;

    public IntermediateHost() {
        try {
            hostReceiveSocket = new DatagramSocket(23);
            hostSendAndReceiveSocket = new DatagramSocket();
        }
        catch (SocketException socketException) {
            socketException.printStackTrace();
            System.exit(1);
        }
    }

    private void actLikeAnIntermediateHostAndActYourAge() {
        while (true) {
            byte[] clientMessage = new byte[32];
            byte[] serverResponse = new byte[4];

            // Receive packet from the Client and print the message it received
            hostReceivePacketFromClient = new DatagramPacket(clientMessage, clientMessage.length);
            try {
                System.out.println("Intermediate - Awaiting Packet From Client");
                hostReceiveSocket.receive(hostReceivePacketFromClient);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }
            System.out.println("Intermediate - Received Packet From Client:\n"
                    + packetToString(hostReceivePacketFromClient, clientMessage));

            // Create a packet containing Client message to be sent to the Server
            try{
                hostSendPacket = new DatagramPacket(clientMessage, clientMessage.length, InetAddress.getLocalHost(), NICE);
            } catch (UnknownHostException unknownHostException) {
                unknownHostException.printStackTrace();
                System.exit(1);
            }

            System.out.println("Intermediate - Packet to be sent to Server:\n"
                    + packetToString(hostSendPacket, clientMessage));

            // Send Client's packet to the Server
            try {
                hostSendAndReceiveSocket.send(hostSendPacket);
                System.out.println("Intermediate - Packet has been sent to the Server");
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }

            // Receive response from Server and print the response it received
            hostReceivePacketFromServer = new DatagramPacket(serverResponse, serverResponse.length);
            try {
                System.out.println("Intermediate - Awaiting Response From Server");
                hostSendAndReceiveSocket.receive(hostReceivePacketFromServer);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }
            System.out.println("Intermediate - Received Response From Server:\n"
                    + packetToString(hostReceivePacketFromServer, serverResponse));

            // Create a packet containing Server response to be sent to the Client
            hostSendPacket = new DatagramPacket(serverResponse, serverResponse.length,
                    hostReceivePacketFromClient.getAddress(), hostReceivePacketFromClient.getPort());
            try {
                hostSendSocket = new DatagramSocket();
            } catch (SocketException socketException) {
                socketException.printStackTrace();
                System.exit(1);
            }
            System.out.println("Intermediate - Response to be sent to Client:\n:"
                    + packetToString(hostSendPacket, serverResponse));

            // Send the Server's response to the Client
            try {
                hostSendAndReceiveSocket.send(hostSendPacket);
                System.out.println("Intermediate - Response has been sent to the Client");
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }
        }
    }


    /**
     * Private method to convert a DatagramPacket to a String
     * @param packet DatagramPacket to convert
     * @param packetInfo DatagramPacket information
     * @return String representation of the DatagramPacket
     */
    private String packetToString(DatagramPacket packet, byte[] packetInfo) {
        StringBuilder sb = new StringBuilder();

        sb.append("Host Address: ");
        sb.append(packet.getAddress());
        sb.append(", Port: ");
        sb.append(packet.getPort());

        sb.append("\nPacket Contents (String):\n");
        sb.append(new String(packetInfo, 0, packet.getLength()));

        sb.append("\nPacket Contents (bytes):\n");
        for (int i = 0; i < packetInfo.length; i++) {
            sb.append(i);
            sb.append(": ");
            sb.append((char)packetInfo[i]);
            sb.append("\n");
        }

        return sb.toString();
    }


    /**
     * Main method to instantiate and run the IntermediateHost
     * @param args We be pirates!
     */
    public static void main(String[] args) {
        IntermediateHost intermediateHost = new IntermediateHost();
        intermediateHost.actLikeAnIntermediateHostAndActYourAge();
    }
}
