/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package App;

import ConexionServidorHTTP.ConexionHTTP;

/**
 *
 * @author DAM_M
 */
public class Lanzador {

    public static void main(String[] args) {
        ConexionHTTP miConexionHTTP = new ConexionHTTP();
        miConexionHTTP.establecerConexion();
        
    }
}
