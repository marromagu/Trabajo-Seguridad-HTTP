/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ConexionServidorHTTP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mario
 */
public class ConexionHTTP {
    private final int PUERTO = 5050;
    
    public void establecerConexion(){
        try {
            ServerSocket skServidor = new ServerSocket(PUERTO);
            System.out.println("-> Servidor HTTP lanzado por el puerto: "+ PUERTO);
            System.out.println("-> localhost:"+ PUERTO);
            while (true) {                
                Socket skCliente = skServidor.accept();
                System.out.println("-> Conexion Acceptada");
                new AtenderCliente(skCliente).start();
                
            }
        } catch (IOException ex) {
            Logger.getLogger(ConexionHTTP.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("-> Ups, ha ocurrido algo inesperado: ");
        }
    }
}
