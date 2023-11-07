package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Empleado;
import com.example.projectgui.Models.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EmpleadoController implements Initializable {
    private Stage stage;

    //INSTANCIAS PARA LA BASE DE DATOS
    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;
    //ventana producto

    @FXML
    private TextField fieldNombres, fieldApellidos, fieldEdad, fieldDui, fieldDireccion, fieldTelefono, fieldCorreo, fieldCargo;
    public static boolean bandera;
    public String query;
    public static int idEmpleado, estadoEmpleado, estadoUsuario;

    private TblProductController tblproduct;
    //

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void aggEmpleado() {
        bandera = false;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formEmpleado.fxml"));
            Parent parent = loader.load();
            EmpleadoController empleadoController = loader.getController(); // Obtener el controlador

            Scene scene = new Scene(parent);
            stage = new Stage();
            empleadoController.setStage(stage); // Pasar el Stage a la función para almacenar la ventana actual
            stage.setScene(scene); // Usa el Stage local
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void funcEditarEmpleado(Empleado empleado) throws SQLException {
        setUpdate(true);
        System.out.println(bandera);
        // Obteniendo la conexión a la base de datos aquí
        connect = DatabaseConnection.getConnection();
        statement = connect.createStatement();

        idEmpleado = empleado.getIdEmpleado();
        estadoEmpleado = empleado.getEstadoEmpleado();
        estadoUsuario = empleado.getEstadoUsuario();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formEmpleado.fxml"));
            Parent parent = loader.load();
            EmpleadoController empleadoController = loader.getController(); // Obtener el controlador

            empleadoController.fieldNombres.setText(empleado.getNombres());
            empleadoController.fieldApellidos.setText(empleado.getApellidos());
            empleadoController.fieldEdad.setText(String.valueOf(empleado.getEdad()));
            empleadoController.fieldDui.setText(empleado.getDui());
            empleadoController.fieldDireccion.setText(empleado.getDireccion());
            empleadoController.fieldTelefono.setText(empleado.getTelefono());
            empleadoController.fieldCorreo.setText(empleado.getCorreo());
            empleadoController.fieldCargo.setText(empleado.getCargo());

            Scene scene = new Scene(parent);
            stage = new Stage();
            empleadoController.setStage(stage); // Pasar el Stage a la función para almacenar la ventana actual
            stage.setScene(scene); // Usa el Stage local
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onCancelarButtonClick() {
        // Agregamos la funcionalidad que deseas ejecutar cuando se hace clic en "CANCELAR"
        System.out.println("presionando boton 'CANCELAR'.");
        // Cierra la ventana actual
        stage.close();
    }
    @FXML
    private void onAceptarButtonClick() throws SQLException {
        // Funcionalidad que hará cuando se le dé clic al botón ACEPTAR de la ventana productos
        System.out.println("Botón 'ACEPTAR' está siendo presionado.");
        Connection connect = null; // Declarar la conexión aquí

        try {
            connect = DatabaseConnection.getConnection(); // Abre la conexión

            if (!fieldNombres.getText().isEmpty() && !fieldApellidos.getText().isEmpty() && !fieldEdad.getText().isEmpty() &&
                !fieldDui.getText().isEmpty() && !fieldDireccion.getText().isEmpty() && !fieldTelefono.getText().isEmpty() &&
                !fieldCorreo.getText().isEmpty() && !fieldCargo.getText().isEmpty()) {
                // Obtener el ID del proveedor correspondiente al nombre seleccionado
                tblproduct = new TblProductController();

                System.out.println(bandera);
                if(bandera){
                    query = "UPDATE `tbl_empleados` SET "
                            + "`nombres`=?,"
                            + "`apellidos`=?,"
                            + "`edad`=?,"
                            + "`dui`=?,"
                            + "`direccion`=?,"
                            + "`telefono`=?,"
                            + "`correo`=?,"
                            + "`cargo`=?,"
                            + "`estadoEmpleado`=?,"
                            + "`estadoUsuario`=?,"
                            + "`idEmpleado`= ? WHERE idEmpleado = '"+idEmpleado+"'";
                }else{
                    query = "INSERT INTO `tbl_empleados`(`nombres`, `apellidos`, `edad`, `dui`, `direccion`, `telefono`, `correo`, `cargo`, `estadoEmpleado`, `estadoUsuario`) VALUES (?,?,?,?,?,?,?,?,?,?)";
                }

                PreparedStatement preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, fieldNombres.getText());
                preparedStatement.setString(2, fieldApellidos.getText());
                preparedStatement.setInt(3, Integer.parseInt(fieldEdad.getText()));
                preparedStatement.setString(4, fieldDui.getText());
                preparedStatement.setString(5, fieldDireccion.getText());
                preparedStatement.setString(6, fieldTelefono.getText());
                preparedStatement.setString(7, fieldCorreo.getText());
                preparedStatement.setString(8, fieldCargo.getText());
                preparedStatement.setInt(9, 1);
                preparedStatement.setInt(10, 0);


                if(bandera){
                    preparedStatement.setInt(9, estadoEmpleado);
                    preparedStatement.setInt(10, estadoUsuario);
                    preparedStatement.setInt(11, idEmpleado);
                }

                preparedStatement.execute();
                // Cierra la ventana de productos (si es necesario)
                if (stage != null) {
                    stage.close();
                }
                // Actualiza la tabla

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("POR FAVOR COMPLETE TODOS LOS DATOS!! :D");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores
        } finally {
            if (connect != null) {
                try {
                    connect.close(); // Cierra la conexión en el bloque finally
                } catch (SQLException e) {
                    e.printStackTrace(); // Manejo de errores
                }
            }
        }
    }

    //funcion para guardar el stage actual (ventana principal)
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUpdate(boolean bandera){
        this.bandera = bandera;
    }
}
