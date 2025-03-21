//Sample client using sslsockets
import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8043;
    // Client PKCS12 file path
    private static final String PKCS12Location = "/Users/natchapantanachokboonyarat/Desktop/securityCourseProject2/certificates/client.p12"; //'<path>/server.p12 Update the path to PKCS12 file, KLART!;
    private static final String PKCS12Password = "Tonny2002"; // Update if password changed
    //Add custom TrustStore password if not using cacerts
    private static final String TRUSTSTORE_LOCATION = "/Users/natchapantanachokboonyarat/Desktop/securityCourseProject2/certificates/client_truststore.jks"; // Update the path
    private static final String TRUSTSTORE_PASSWORD = "Tonny2002"; //change it är defaultlösenordet på truststore
    public static void main(String[] args) throws Exception {
        char[] passphrase_ks = PKCS12Password.toCharArray();
        char[] passphrase_ts = TRUSTSTORE_PASSWORD.toCharArray();

        //Adding custom TrustStore
        //char[] passphrase_ts = CACERTSPassword.toCharArray();
        //KeyStore ts = KeyStore.getInstance("PKCS12");
        //ts.load(new FileInputStream("/pathtoyour/"truststore.p12"), passphrase_ts);
        //TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        //tmf.init(ts);
        //TrustManager[] trustManagers = tmf.getTrustManagers();
        // Add code for Keystore
        // ?? Allt under är min kod fram tills 'KLART'
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(PKCS12Location)) {
            ks.load(fis, passphrase_ks);
        }
        // Initialize a KeyManagerFactory with the KeyStore
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase_ks);
        KeyManager[] keyManagers = kmf.getKeyManagers();

        //Load custom truststore
        KeyStore ts = KeyStore.getInstance("JKS");
        try (FileInputStream fil = new FileInputStream(TRUSTSTORE_LOCATION)) {
            ts.load(fil, passphrase_ts);
        }

        // Obtain the default TrustManagers for the system’s truststore (cacerts)
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init((KeyStore) ts); // Use the custom truststore ’cacerts’
        TrustManager[] trustManagers = tmf.getTrustManagers();

        SSLContext context = SSLContext.getInstance("TLSv1.3");
        //TrustManager (2nd argu) is null to use the default trust manager cacerts
        //To use custom TrustStore, 2nd argument changes to ’trustManagers’
        //context.init(??, ??, ??); //Add correct arguments
        context.init(keyManagers, trustManagers, new SecureRandom()); //KLART

        // Create client socket
        SSLSocketFactory sf = context.getSocketFactory();
        try (SSLSocket socket = (SSLSocket) sf.createSocket(HOST, PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server.");

            String userMessage;
            while (true) {
                System.out.print("Enter message (or 'exit' to quit): ");
                userMessage = userInput.readLine();
                if ("exit".equalsIgnoreCase(userMessage)) {
                    break;
                }
                out.println(userMessage);
                System.out.println("Server response: " + in.readLine());
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}