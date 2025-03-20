import java.io.*;

import java.net.*;
import java.security.*;
import javax.net.ssl.*;

    public class Server {
    private static final int PORT = 8043; // likely this port number is ok to use
    // Server PKCS12 file path
    private static final String PKCS12Location = "/Users/natchapantanachokboonyarat/Desktop/TonnysEITF55/server.p12"; //'<path>/server.p12' // Update the path to PKCS12 file, KLART!;
    private static final String PKCS12Password = "Tonny2002"; // Update if password changed OK!

    public static void main (String[] args) throws Exception {
        boolean keepRunning = true;
        // First we need to load a keystore
        char[] passphrase = PKCS12Password.toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");

        try (FileInputStream fis = new FileInputStream(PKCS12Location)) {
            ks.load(fis, passphrase);
        }
        // Initialize a KeyManagerFactory with the KeyStore
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);

        // KeyManagers from the KeyManagerFactory
        KeyManager[] keyManagers = kmf.getKeyManagers();

        //Adding custom TrustStore
        //System.setProperty("javax.net.ssl.trustStore","<pathtoyour>/truststore.p12");
        //System.setProperty("javax.net.ssl.trustStorePassword","changeit");
        //tmf.init(ts);

        // Obtain the default TrustManagers for the system’s truststore (cacerts)
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

        tmf.init((KeyStore) null); // Use the system’s truststore ’cacerts’

        TrustManager[] trustManagers = tmf.getTrustManagers();

        // Create an SSLContext to run TLSv1.3 and initialize it with
        SSLContext context = SSLContext.getInstance("TLSv1.3");
        context.init(keyManagers, trustManagers, new SecureRandom());
        SSLServerSocketFactory ssf = context.getServerSocketFactory();

        // Create server socket
        try (SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(PORT)) {
        //ss.setNeedClientAuth(true); // Require client authentication
        System.out.println("Server started and waiting for connections...");
        // Continuously accept new connections
        while (keepRunning) {
                    
                try (SSLSocket s = (SSLSocket) ss.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                    System.out.println("Client connected.");
                    // Read and process input from the client
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                    
                } catch (SocketException | EOFException e) {
                    System.out.println("Client disconnected abruptly.");
                    keepRunning = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
            System.out.println("Server stopped.");
            }
    }
}
