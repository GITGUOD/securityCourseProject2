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
    //private static final String TSPassword = "changeit";
    public static void main(String[] args) throws Exception {
        char[] passphrase_ks = PKCS12Password.toCharArray();
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

        // Obtain the default TrustManagers for the system’s truststore (cacerts)
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

        tmf.init((KeyStore) null); // Use the system’s truststore ’cacerts’

        TrustManager[] trustManagers = tmf.getTrustManagers();
        
        SSLContext context = SSLContext.getInstance("TLSv1.3");
        //TrustManager (2nd argu) is null to use the default trust manager cacerts
        //To use custom TrustStore, 2nd argument changes to ’trustManagers’
        //context.init(??, ??, ??); //Add correct arguments
        context.init(keyManagers, trustManagers, new SecureRandom()); //KLART

        SSLSocketFactory sf = context.getSocketFactory();
            try (SSLSocket s = (SSLSocket) sf.createSocket(HOST,PORT)) {
                OutputStream toserver = s.getOutputStream();
                toserver.write("\nConnection established.\n\n".getBytes());
                System.out.print("\nConnection established.\n\n");
                int inCharacter=0;
                inCharacter = System.in.read();
                try {
                    while (inCharacter != '~')
                    {
                    toserver.write(inCharacter);
                    toserver.flush();
                    inCharacter = System.in.read();
                    }
                }catch (SocketException | EOFException e) {
                    System.out.print("\nClient Closing.\n\n");
                    e.printStackTrace();
                    toserver.close();
                    s.shutdownOutput();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
            }
        }catch (IOException e) {
            System.out.println("Cannot estabilish connection to server.");
            e.printStackTrace();
            
        } finally {
            System.out.println("client stopped.");
        }
    }
}