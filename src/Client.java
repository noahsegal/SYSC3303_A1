import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    public static final int INVALID = 0;
    public static final int READ_REQ = 1;
    public static final int WRITE_REQ = 2;
    public static final int MESSAGE_PADDING = 4;

    public DatagramSocket clientSendAndReceiveSocket;

    public DatagramPacket clientSendPacket;
    public DatagramPacket clientReceivePacket;

    public Client() {
        try {
            clientSendAndReceiveSocket = new DatagramSocket();
        } catch (Exception socketException) {
            socketException.printStackTrace();
            System.exit(1);
        }
    }

    public void actLikeAClient() {

        //
        for (int clientCount = 0; clientCount < 11; clientCount++) {
            byte clientMessage[];
            String filename = "test.txt";
            String mode = "octet";

            if (clientCount < 10) {
                if (clientCount % 2 == 0) {
                    System.out.println("Creating Read Request");
                    clientMessage = createClientRequest(filename, mode, READ_REQ);
                }
                else {
                    System.out.println("Creating Write Request");
                    clientMessage = createClientRequest(filename, mode, WRITE_REQ);
                }
            }
            else {
                clientMessage = createClientRequest(filename, mode, INVALID);
            }


            try {
                clientSendPacket = new DatagramPacket(clientMessage, clientMessage.length,
                        InetAddress.getLocalHost(), 23);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println("Client - Sending packet " + (clientCount + 1));
            System.out.println(packetToString(clientSendPacket, clientMessage));

            try {
                clientSendAndReceiveSocket.send(clientSendPacket);
                System.out.println("Client - Packet has been sent");
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }

            // Prepare for incoming response from the Server
            byte[] receiveData = new byte[4];
            clientReceivePacket = new DatagramPacket(receiveData, receiveData.length);

            // Attempt to receive the Server's response
            try {
                clientSendAndReceiveSocket.receive(clientReceivePacket);
                System.out.println("Client - Received Server Response");
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }
            System.out.println("Client - Server's Response:\n" + packetToString(clientReceivePacket, receiveData));
        }
        // Close the Client's socket after transmitting/receiving all 11
        clientSendAndReceiveSocket.close();
    }

    /**
     * Create the byte array containing the Client's message
     * @param filename Filename to be added to the message
     * @param messageMode Mode to be added to the message
     * @param request The type of message request (Read || Write || Invalid)
     * @return A byte representation containing the request type, filename, and mode
     */
    private byte[] createClientRequest(String filename, String messageMode, int request) {
        byte[] byteArray = new byte[filename.length() + messageMode.length() + MESSAGE_PADDING];
        byte[] fileNameAsBytes = filename.getBytes();
        byte[] msgModeAsBytes = messageMode.getBytes();
        int fbLength = fileNameAsBytes.length;
        int mbLength = msgModeAsBytes.length;

        if (request == READ_REQ) {
            byteArray[0] = 0x00;
            byteArray[1] = 0x01;
        }
        else if (request == WRITE_REQ) {
            byteArray[0] = 0x00;
            byteArray[1] = 0x02;
        }
        else if (request == INVALID) {
            byteArray[0] = 0x00;
            byteArray[1] = 0x00;
        }
        System.arraycopy(fileNameAsBytes, 0, byteArray, 2, fbLength);
        byteArray[fbLength + 2] = 0x00; // 0-byte between Filename & Mode

        System.arraycopy(msgModeAsBytes, 0, byteArray, fbLength + 3, mbLength);
        byteArray[2 + fbLength + 1 + mbLength] = 0x00; // 0-byte to end the packet

        return byteArray;
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
     * Main method to instantiate and run the Client
     * @param args We be pirates!
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.actLikeAClient();
    }
}
