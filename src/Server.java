import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class Server {

    private static final int NICE = 69;
    private static final String READ = "read";
    private static final String WRITE = "write";

    private static final int FILENAME_START_LOC = 2;
    private static final int SERVER_ERROR = 1;

    public DatagramSocket serverSendSocket;
    public DatagramSocket serverReceiveSocket;

    public DatagramPacket serverSendPacket;
    public DatagramPacket serverReceivePacket;

    public Server() {
        try {
            serverReceiveSocket = new DatagramSocket(NICE);
        } catch (Exception socketException) {
            socketException.printStackTrace();
            System.exit(SERVER_ERROR);
        }
    }

    /**
     * Server's entire logic
     * Interprets both Read and Write requests
     * Sends a response back to the Client
     */
    public void actLikeAServer() {
        while (true) {

            String requestType = "";
            byte[] filenameAsBytes = new byte[1];
            byte[] modeAsBytes = new byte[1];

            // receiveData is the buffer that will hold incoming data
            byte receiveData[] = new byte[32];
            byte message[] = new byte[] {0x00, 0x00, 0x00, 0x00};

            serverReceivePacket = new DatagramPacket(receiveData, receiveData.length);

            try {
                System.out.println("Server - Waiting to receive");
                serverReceiveSocket.receive(serverReceivePacket);
            }
            catch (Exception socketException) {
                System.out.println("Server - Receive Socket Timeout: " + socketException);
                socketException.printStackTrace();
                System.exit(SERVER_ERROR);
            }
            System.out.println("Server - Received Socket (successful)");

            // Data must start with 01 or 02, anything else is invalid
            try {
                // Invalid file
                if (receiveData[0] != 0 || receiveData[1] == 0) {
                    throw new NoSuchFieldException();
                }
                else if (receiveData[1] == 0x01) {
                    requestType = READ;
                    message[1] = 0x03;
                    message[1] = 0x01;
                }
                else if (receiveData[1] == 0x02) {
                    requestType = WRITE;
                    message[1] = 0x04;
                }

                // Should detect two instances of 0. After that, no more data
                // Ignore the first two bytes (01 or 02)
                int numberOfZeros = 0;
                for (int i = 2; numberOfZeros < 2; i++) {

                    if (receiveData[i] == 0x00) {
                        numberOfZeros++;
                        if (numberOfZeros == 1) { // Location of Filename
                            filenameAsBytes = Arrays.copyOfRange(receiveData, FILENAME_START_LOC, i);
                        }
                        else if (numberOfZeros == 2) { // Location of Mode
                            modeAsBytes = Arrays.copyOfRange(receiveData, filenameAsBytes.length + 3, i);

                            // Reached the last zero (0) and end of packet
                            if (i > receiveData.length) throw new NoSuchFieldException();
                        }
                    }
                }
            } catch (NoSuchFieldException nsfe) {
                System.out.println("Server - Field not valid. Goodbye!");
                System.exit(SERVER_ERROR);
            }

            // Print out Packet information
            System.out.println("Server - Received the following " + requestType + " request:");
            System.out.println(packetToString(serverReceivePacket, receiveData));

            System.out.println("Packet Filename: " + new String(filenameAsBytes));
            System.out.println("Packet Mode: " + new String(modeAsBytes));

            // Server Response - Create new packet & socket to send to Client
            try {
                serverSendSocket = new DatagramSocket();
            } catch (Exception socketException) {
                System.out.println("Server - Send Socket Timeout: " + socketException);
                socketException.printStackTrace();
                System.exit(SERVER_ERROR);
            }
            serverSendPacket = new DatagramPacket(message, message.length,
                    serverReceivePacket.getAddress(), serverReceivePacket.getPort());
            try {
                serverSendSocket.send(serverSendPacket);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(SERVER_ERROR);
            }

            serverSendSocket.close();
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
     * Main method to instantiate and run the Server
     * @param args We be pirates!
     */
    public static void main (String[] args) {
        Server server = new Server();
        server.actLikeAServer();
    }
}
