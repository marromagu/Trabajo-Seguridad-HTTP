/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package App;

import ConexionServidorHTTP.ConexionHTTP;
import Datos.*;

/**
 * s
 *
 * @author DAM_M
 */
public class Lanzador {

    public static void main(String[] args) {
        ConexionHTTP miConexionHTTP = new ConexionHTTP();
        miConexionHTTP.establecerConexion();
//        ConexionConBDD miCone = new ConexionConBDD();
//        Usuario miUsuario;
//        for (int i = 0; i < 10; i++) {
//            miUsuario = new Usuario(i);
//            miUsuario.setContraseña(miCone.obtenerContraID(i));
//            miUsuario.setNombre(miCone.obtenerNombreJugadorPorID(i));
//            System.out.println(miUsuario.hashCode());
//            String nuevaContraseña = String.valueOf(miUsuario.hashCode());
//            miCone.actualizarContraseñaPorID(i, nuevaContraseña);
//        }

    }
}
