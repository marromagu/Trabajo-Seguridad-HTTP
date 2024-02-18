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

    public HashMap<Integer, String> obtenerPartidasTerminadas() {
        HashMap<Integer, String> mapaPartidasTerminadas = new HashMap<>();

        try (Connection conexion = getConexion()) {
            String sql = "SELECT id_partida, jugador_1, jugador_2, ganador, ultimo_turno FROM Partidas "
                    + "WHERE estado = 'X'";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
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

    public String obtenerPartidas(int id) {
        String representacionPartida = null;

        try (Connection conexion = getConexion()) {
            String sql = "SELECT jugador_1, jugador_2 FROM Partidas WHERE id_partida = ?";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Mover el cursor al primer registro
                    if (resultSet.next()) {
                        int jugador1ID = resultSet.getInt("jugador_1");
                        int jugador2ID = resultSet.getInt("jugador_2");

                        // Crear cadena representativa de la partida con nombres de jugadores
                        representacionPartida = String.format("%d;%d", jugador1ID, jugador2ID);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: " + e.getMessage());
        }

        return representacionPartida;
    }

    public ArrayList<String> obtenerDisparosDePartida(int idPartida) {
        ArrayList<String> listaDisparos = new ArrayList<>();

        try (Connection conexion = getConexion()) {
            String sql = "SELECT * FROM Disparos WHERE id_partida = ?";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                // Establecer el parámetro idPartida en la consulta preparada
                statement.setInt(1, idPartida);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idDisparo = resultSet.getInt("id_disparo");
                        int jugadorId = resultSet.getInt("jugador_id");
                        int posicionX = resultSet.getInt("posicion_x");
                        int posicionY = resultSet.getInt("posicion_y");
                        String mensajeResultado = resultSet.getString("resultado").equals("T") ? "T" : "A";
                        //String jugador = obtenerNombreJugadorPorID(jugadorId);

                        // Crear cadena representativa del disparo
                        String representacionDisparo = String.format("%d-%d-%d-%s",
                                jugadorId, posicionX, posicionY, mensajeResultado);

                        // Agregar la representación al ArrayList
                        listaDisparos.add(representacionDisparo);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: al obtener los disparos de la partida: " + e.getMessage());
        }

        return listaDisparos;
    }

    public boolean hayBarcoEnemigoEnCoordenada(int idJugador, int idPartida, int posicionX, int posicionY) {
        boolean hayBarcoEnemigo = false;

        try (Connection conexion = getConexion()) {
            String sql = "SELECT B.id_barco "
                    + "FROM Barcos B "
                    + "JOIN Disparos D ON B.id_partida = D.id_partida "
                    + "               AND B.posicion_x = D.posicion_x "
                    + "               AND B.posicion_y = D.posicion_y "
                    + "               AND B.jugador_id != D.jugador_id "
                    + "WHERE B.id_partida = ? "
                    + "  AND B.jugador_id = ? "
                    + "  AND B.posicion_x = ? "
                    + "  AND B.posicion_y = ?";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                statement.setInt(1, idPartida);
                statement.setInt(2, idJugador);
                statement.setInt(3, posicionX);
                statement.setInt(4, posicionY);

                try (ResultSet resultSet = statement.executeQuery()) {
                    hayBarcoEnemigo = resultSet.next(); // Devuelve true si hay al menos una fila en el resultado
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: al verificar la presencia de un barco enemigo en la coordenada: " + e.getMessage());
        }

        return hayBarcoEnemigo;
    }

    public ArrayList<String> consultarBarcosEnPartida(int idPartida) {
        ArrayList<String> barcosEnPartida = new ArrayList<>();

        try (Connection conexion = getConexion()) {
            String sql = "SELECT * FROM Barcos WHERE id_partida = ?";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                statement.setInt(1, idPartida);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idBarco = resultSet.getInt("id_barco");
                        int jugadorId = resultSet.getInt("jugador_id");
                        int tamaño = resultSet.getInt("tamaño");
                        int posicionX = resultSet.getInt("posicion_x");
                        int posicionY = resultSet.getInt("posicion_y");
                        String orientacion = resultSet.getString("orientacion");

                        String barcoString = jugadorId + "-" + tamaño + "-" + posicionX + "-" + posicionY + "-" + orientacion;
                        barcosEnPartida.add(barcoString);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: al consultar los barcos en una partida: " + e.getMessage());
        }

        return barcosEnPartida;
    }

    public String obtenerGanadorDePartida(int idPartida) {
        String nombreGanador = null;

        try (Connection conexion = getConexion()) {
            String sql = "SELECT j.nombre AS nombre_ganador "
                    + "FROM Partidas p "
                    + "JOIN Jugadores j ON p.ganador = j.id_jugador "
                    + "WHERE p.id_partida = ?";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                statement.setInt(1, idPartida);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        nombreGanador = resultSet.getString("nombre_ganador");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: al obtener el ganador de la partida: " + e.getMessage());
        }

        return nombreGanador;
    }

    public String obtenerNombrePorID(int idJugador) {
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

    public String obtenerContraID(int idJugador) {
        String contraseñaJugador = null;

        try (Connection conexion = getConexion()) {
            String sql = "SELECT contraseña FROM Jugadores WHERE id_jugador = ?";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                // Establecer el parámetro idJugador en la consulta preparada
                statement.setInt(1, idJugador);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        contraseñaJugador = resultSet.getString("contraseña");
                    } else {
                        // No se encontró un jugador con la ID proporcionada
                        System.out.println("No se encontró un jugador con la ID: " + idJugador);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: al obtener la contraseña del jugador: " + e.getMessage());
        }

        return contraseñaJugador;
    }

    public boolean actualizarContraseñaPorID(int idJugador, String nuevaContraseña) {
        boolean actualizacionExitosa = false;

        try (Connection conexion = getConexion()) {
            String sql = "UPDATE Jugadores SET contraseña = ? WHERE id_jugador = ?";

            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                // Establecer los parámetros de la consulta preparada
                statement.setString(1, nuevaContraseña);
                statement.setInt(2, idJugador);

                // Ejecutar la actualización y verificar si se actualizó algún registro
                int filasActualizadas = statement.executeUpdate();
                if (filasActualizadas > 0) {
                    actualizacionExitosa = true;
                    System.out.println("Se actualizó la contraseña del jugador con ID " + idJugador + ".");
                } else {
                    System.out.println("No se encontró ningún jugador con la ID " + idJugador + ".");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ConexionConBDD: al actualizar la contraseña del jugador: " + e.getMessage());
        }

        return actualizacionExitosa;
    }

}
