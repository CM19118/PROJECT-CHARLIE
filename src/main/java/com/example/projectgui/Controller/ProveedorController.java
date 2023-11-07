package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Producto;
import com.example.projectgui.Models.Proveedor;
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
import java.util.ResourceBundle;

public class ProveedorController implements Initializable {
    //INSTANCIAS PARA LA TABLA DE PRODUCTOS

    // Variable estática para almacenar el Stage de la ventana principal
    private Stage stage;
    //INSTANCIAS PARA LA BASE DE DATOS
    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;
    //ventana producto

    @FXML
    private TextField fieldnameProveedor, fieldDireccion, fieldCorreo, fieldTipo, fieldTelefono, fieldObservaciones;
    public static boolean bandera;
    public String query;
    public static int idProveedor;

    private TblProductController tblproduct;
    //

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void aggProveedor() {
        bandera = false;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formProveedor.fxml"));
            Parent parent = loader.load();
            ProveedorController proveedorController = loader.getController(); // Obtener el controlador

            Scene scene = new Scene(parent);
            stage = new Stage();
            proveedorController.setStage(stage); // Pasar el Stage a la función para almacenar la ventana actual
            stage.setScene(scene); // Usa el Stage local
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void funcEditarProveedor(Proveedor proveedor) throws SQLException {
        setUpdate(true);
        // Obteniendo la conexión a la base de datos aquí
        connect = DatabaseConnection.getConnection();
        statement = connect.createStatement();

        idProveedor = proveedor.getIdProveedor();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formProveedor.fxml"));
            Parent parent = loader.load();
            ProveedorController proveedorController = loader.getController(); // Obtener el controlador

            proveedorController.fieldnameProveedor.setText(proveedor.getNombreProveedor());
            proveedorController.fieldCorreo.setText(proveedor.getCorreo());
            proveedorController.fieldDireccion.setText(proveedor.getDireccion());
            proveedorController.fieldTipo.setText(proveedor.getTipo());
            proveedorController.fieldTelefono.setText(String.valueOf(proveedor.getTelefono()));
            proveedorController.fieldObservaciones.setText(proveedor.getObservaciones());

            Scene scene = new Scene(parent);
            stage = new Stage();
            proveedorController.setStage(stage); // Pasar el Stage a la función para almacenar la ventana actual
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
        Connection connect = null; // Declarando la conexión aquí

        try {
            connect = DatabaseConnection.getConnection(); // Abre la conexión

            if (!fieldnameProveedor.getText().isEmpty() && !fieldObservaciones.getText().isEmpty() && !fieldTelefono.getText().isEmpty()
                    && !fieldCorreo.getText().isEmpty() && !fieldTipo.getText().isEmpty()) {

                // Obtener el ID del proveedor correspondiente al nombre seleccionado
                tblproduct = new TblProductController();

                System.out.println(bandera);
                if(bandera){
                    query = "UPDATE `tbl_proveedor` SET "
                            + "`nombreProveedor`=?,"
                            + "`direccion`=?,"
                            + "`correo`=?,"
                            + "`tipo`=?,"
                            + "`telefono`=?,"
                            + "`observaciones`=?,"
                            + "`idProveedor`= ? WHERE idProveedor = '"+idProveedor+"'";
                }else{
                    query = "INSERT INTO `tbl_proveedor`(`nombreProveedor`, `direccion`, `correo`, `tipo`, `telefono`, `observaciones`) VALUES (?,?,?,?,?,?)";
                }

                PreparedStatement preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, fieldnameProveedor.getText());
                preparedStatement.setString(2, fieldDireccion.getText());
                preparedStatement.setString(3, fieldCorreo.getText());
                preparedStatement.setString(4, fieldTipo.getText());
                preparedStatement.setString(5, fieldTelefono.getText());
                preparedStatement.setString(6, fieldObservaciones.getText());

                if(bandera){
                    preparedStatement.setString(7, String.valueOf(idProveedor));
                }

                preparedStatement.execute();
                // Limpia los campos después de la inserción
                //clear();

                // Cierra la ventana de productos (si es necesario)
                if (stage != null) {
                    stage.close();
                }
                // Actualiza la tabla de productos
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
