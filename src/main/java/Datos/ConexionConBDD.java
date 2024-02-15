package Datos;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConexionConBDD implements Serializable {

    private final String NameDataBase = "BDD_HundirLaFlota";
    private final String User = "root";
    private final String Password = "root";
    private final String Driver = "com.mysql.cj.jdbc.Driver";
    private final String URL = "jdbc:mysql://localhost:3306/" + NameDataBase;

    public ConexionConBDD() {
    }

    public Connection getConexion() {
        Connection conexion = null;
        try {
            Class.forName(Driver);
            conexion = DriverManager.getConnection(URL, User, Password);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Error en ConexionConBDD: Controlador JDBC no encontrado");
            cnfe.printStackTrace(); // Imprimir detalles de la excepción
        } catch (SQLException sqle) {
            System.out.println("Error en ConexionConBDD: al conectar a la BDD");
            sqle.printStackTrace(); // Imprimir detalles de la excepción
        }

        return conexion;
    }

    public void cerrarConexion(Connection conexion) {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Error en ConexionConBDD: Se cerró la conexión a la BDD.");
            }
        } catch (SQLException sqle) {
            System.out.println("Error en ConexionConBDD: al cerrar la conexión a la BDD");
        }
    }

    public int consultarIDJugador(String nombreUsuario, int contraseña) {
        int idJugador = -1;

        try (Connection conexion = getConexion()) {
            String sql = "SELECT id_jugador FROM Jugadores WHERE nombre = ? AND contraseña = ?";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                // Establecer los parámetros en la consulta preparada
                statement.setString(1, nombreUsuario);
                statement.setInt(2, contraseña);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        idJugador = resultSet.getInt("id_jugador");
                    } else {
                        // No se encontró un jugador con las credenciales proporcionadas
                        System.out.println("Error en ConexionConBDD: Nombre de usuario o contraseña incorrectos.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: al obtener la ID del jugador: " + e.getMessage());
        }

        return idJugador;
    }

    public HashMap<Integer, String> obtenerPartidasTerminadasPorJugador(int idJugador) {
        HashMap<Integer, String> mapaPartidasTerminadas = new HashMap<>();

        try (Connection conexion = getConexion()) {
            String sql = "SELECT id_partida, jugador_1, jugador_2, ganador, ultimo_turno FROM Partidas "
                    + "WHERE (jugador_1 = ? OR jugador_2 = ?) AND estado = 'X'";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                // Establecer el parámetro idJugador en la consulta preparada
                statement.setInt(1, idJugador);
                statement.setInt(2, idJugador);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idPartida = resultSet.getInt("id_partida");
                        int jugador1ID = resultSet.getInt("jugador_1");
                        int jugador2ID = resultSet.getInt("jugador_2");
                        int ganadorID = resultSet.getInt("ganador");
                        int ultimoTurnoID = resultSet.getInt("ultimo_turno");

                        // Obtener los nombres de los jugadores utilizando el método creado anteriormente
                        String nombreJugador1 = obtenerNombreJugadorPorID(jugador1ID);
                        String nombreJugador2 = obtenerNombreJugadorPorID(jugador2ID);
                        String nombreGanador = obtenerNombreJugadorPorID(ganadorID);
                        String nombreUltimoTurno = obtenerNombreJugadorPorID(ultimoTurnoID);

                        // Crear cadena representativa de la partida con nombres de jugadores
                        String representacionPartida = String.format("%d;%s;%s;%s;%s",
                                idPartida, nombreJugador1, nombreJugador2, nombreGanador, nombreUltimoTurno);

                        // Agregar la representación al HashMap
                        mapaPartidasTerminadas.put(idPartida, representacionPartida);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: al obtener las partidas terminadas: " + e.getMessage());
        }

        return mapaPartidasTerminadas;
    }

    public String obtenerNombreJugadorPorID(int idJugador) {
        String nombreJugador = null;

        try (Connection conexion = getConexion()) {
            String sql = "SELECT nombre FROM Jugadores WHERE id_jugador = ?";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                // Establecer el parámetro idJugador en la consulta preparada
                statement.setInt(1, idJugador);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        nombreJugador = resultSet.getString("nombre");
                    } else {
                        // No se encontró un jugador con la ID proporcionada
                        System.out.println("No se encontró un jugador con la ID: " + idJugador);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: al obtener el nombre del jugador: " + e.getMessage());
        }

        return nombreJugador;
    }
}
