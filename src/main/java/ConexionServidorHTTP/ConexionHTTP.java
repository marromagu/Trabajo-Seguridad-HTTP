/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ConexionServidorHTTP;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author mario
 */
public class ConexionHTTP {

    private final int PUERTO = 5050;
    private final String KEY_PASSWORD = "Mario";

    public void establecerConexion() {
        try {
            //Configuracion del KeyStore
            char[] contraseña = KEY_PASSWORD.toCharArray();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            FileInputStream keyStoreStream = new FileInputStream("keystore.jks");
            keyStore.load(keyStoreStream, contraseña);

            //Configuracion del KeyManagerFactory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, contraseña);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PUERTO);

            System.out.println("-> Servidor HTTP lanzado por el puerto: " + PUERTO);
            System.out.println("-> localhost:" + PUERTO);

            while (true) {
                SSLSocket skCliente = (SSLSocket) sslServerSocket.accept();
                System.out.println("-> Conexion Acceptada");
                new AtenderCliente(skCliente).start();

            }
        } catch (IOException ex) {
            Logger.getLogger(ConexionHTTP.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("-> Ups, ha ocurrido algo inesperado: ");
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyManagementException | CertificateException ex) {
            Logger.getLogger(ConexionHTTP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
