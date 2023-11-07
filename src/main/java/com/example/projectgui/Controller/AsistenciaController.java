package com.example.projectgui.Controller;


import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.SessionManager;
import com.example.projectgui.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.sql.*;
import java.time.LocalDateTime;

public class AsistenciaController {

    //------------ Varibles para la conexion a la base de datos ------------------------
    private Connection conexion;
    private Statement estado;
    private ResultSet resultado;

    @FXML
    private Button btnMarcarAsistenciaEntrada;
    @FXML
    private Button btnMarcarAsistenciaSalida;

    // Obtenemos la instancia de UserSession desde SesionManager
    UserSession userSession = SessionManager.getUsuarioSesion();
    String usernamee = userSession.getUsername();
    String rol = userSession.getRol();
    int idUsuario = userSession.getIdUsuario();
    int idEmpleado = userSession.getIdEmpleado();


    public void initialize()
    {

        btnMarcarAsistenciaEntrada.setOnAction(actionEvent -> asistenciaEntrada());
        btnMarcarAsistenciaSalida.setOnAction(actionEvent -> asistenciaSalida());
    }

    private void asistenciaSalida() {

        conexion = DatabaseConnection.getConnection();
        LocalDateTime fechaHoraSalida = LocalDateTime.now();

        //Se verifica la hora de entrada y de salid, para ver si el empleado ya marcó ambas
        String consultaVerificacion = "SELECT horaEntrada, horaSalida FROM tbl_asistencias WHERE idEmpleado = ? AND fecha = ?";


        try {

            PreparedStatement preparedStatementVerificacion = conexion.prepareStatement(consultaVerificacion);
            preparedStatementVerificacion.setInt(1, idEmpleado); // Id del empleado
            preparedStatementVerificacion.setDate(2, java.sql.Date.valueOf(fechaHoraSalida.toLocalDate())); // Fecha actual

            ResultSet resultSet = preparedStatementVerificacion.executeQuery();

            //Este primer condicional es para verificar si ya hay un registro
            if (resultSet.next()) {

                //Se obtinen las horas correspondientes en la consulta que se ha echo
                Time horaEntrada = resultSet.getTime("horaEntrada");
                Time horaSalida = resultSet.getTime("horaSalida");

                //En este if se valida que si las dos horas no son null el empleado ya marcó ambas horas
                if (horaEntrada != null && horaSalida != null) {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setHeaderText(null);
                    alerta.setContentText("YA SE HA REGISTRADO LA HORA DE ENTRADA Y SALIDA");
                    alerta.showAndWait();

                } else {
                    //En dado caso haya un campo null, corresponde a la hora de salida y se procede a registrar
                    String consultaRegistroSalida = "UPDATE tbl_asistencias SET horaSalida = ? WHERE idEmpleado = ? AND fecha = ?";
                    PreparedStatement preparedStatementRegistroSalida = conexion.prepareStatement(consultaRegistroSalida);
                    preparedStatementRegistroSalida.setTime(1, java.sql.Time.valueOf(fechaHoraSalida.toLocalTime())); // Hora actual
                    preparedStatementRegistroSalida.setInt(2, idEmpleado); // Id del empleado
                    preparedStatementRegistroSalida.setDate(3, java.sql.Date.valueOf(fechaHoraSalida.toLocalDate())); // Fecha actual

                    preparedStatementRegistroSalida.executeUpdate();

                    Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                    alerta.setHeaderText(null);
                    alerta.setContentText("Hora de salida registrada con éxito!!");
                    alerta.showAndWait();
                }
            } else {

                // Si no hay resultados, significa que no hay registros de asistencia previos para el empleado en la misma fecha
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setHeaderText(null);
                alerta.setContentText("NO SE HA MARCADO LA HORA DE ENTRADA");
                alerta.showAndWait();
            }

            // Cerrar la conexión
            conexion.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void asistenciaEntrada() {
        conexion = DatabaseConnection.getConnection();
        LocalDateTime fechaHoraEntrada = LocalDateTime.now();

        // se verifica si ya se ha marcado una fecha y hora de entrada
        String consultaVerificacionEntrada = "SELECT horaEntrada FROM tbl_asistencias WHERE idEmpleado = ? AND fecha = ?";

        try {
            PreparedStatement preparedStatementAsistenciaEntrada = conexion.prepareStatement(consultaVerificacionEntrada);
            preparedStatementAsistenciaEntrada.setInt(1, idEmpleado); // Id del empleado
            preparedStatementAsistenciaEntrada.setDate(2, java.sql.Date.valueOf(fechaHoraEntrada.toLocalDate())); // Fecha actual

            ResultSet resultSet = preparedStatementAsistenciaEntrada.executeQuery();

            // Si hay algún resultado, significa que ya existe una asistencia para el empleado en la misma fecha
            if (resultSet.next()) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setHeaderText(null);
                alerta.setContentText("YA SE HA MARCADO LA HORA DE ENTRADA");
                alerta.showAndWait();

            } else {
                // Si no hay resultados, proceder a insertar la nueva asistencia
                String consultaRegistroEntrada = "INSERT INTO tbl_asistencias (idEmpleado, fecha, horaEntrada) VALUES (?, ?, ?)";

                PreparedStatement preparedStatement = conexion.prepareStatement(consultaRegistroEntrada);

                // Establecer los valores de los parámetros
                preparedStatement.setInt(1, idEmpleado); // Id del empleado
                preparedStatement.setDate(2, java.sql.Date.valueOf(fechaHoraEntrada.toLocalDate())); // Fecha actual
                preparedStatement.setTime(3, java.sql.Time.valueOf(fechaHoraEntrada.toLocalTime())); // Hora actual

                // Ejecutar la consulta
                preparedStatement.executeUpdate();

                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setHeaderText(null);
                alerta.setContentText("Hora de entrada registrada con exito !!");
                alerta.showAndWait();
            }

            // Cerrar la conexión
            conexion.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
