package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProductoController implements Initializable {
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
    private TextField fieldPrice, fieldCantidad, fieldnameProduct;
    @FXML
    private ComboBox listProveedores;
    public static boolean bandera;
    public String query;
    public static int idProducto;

    private TblProductController tblproduct;
    private static TblProductController mainControllerInstance;
    //

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializa los campos aquí si es necesario
        //fieldPrice = new TextField();
        //fieldCantidad = new TextField();
        //fieldnameProduct = new TextField();
        //listProveedores = new ComboBox<>();
    }
    @FXML
    public void aggProducto() {
        bandera = false;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formProducto.fxml"));
            Parent parent = loader.load();
            ProductoController productoController = loader.getController(); // Obtener el controlador
            productoController.cargandoDatosComBox();

            Scene scene = new Scene(parent);
            stage = new Stage();
            productoController.setStage(stage); // Pasar el Stage a la función para almacenar la ventana actual
            stage.setScene(scene); // Usa el Stage local
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void funcEditarProducto(Producto producto, String nameProveedor) throws SQLException {
        setUpdate(true);
        System.out.println(bandera);
        // Obteniendo la conexión a la base de datos aquí
        connect = DatabaseConnection.getConnection();
        statement = connect.createStatement();

        idProducto = producto.getIdProducto();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formProducto.fxml"));
            Parent parent = loader.load();
            ProductoController productoController = loader.getController(); // Obtener el controlador
            productoController.cargandoDatosComBox();

            productoController.fieldPrice.setText(String.valueOf(producto.getPrecio()));
            productoController.fieldnameProduct.setText(producto.getNombreProducto());
            productoController.fieldCantidad.setText(String.valueOf(producto.getCantidad()));
            productoController.listProveedores.setValue(nameProveedor);

            Scene scene = new Scene(parent);
            stage = new Stage();
            productoController.setStage(stage); // Pasar el Stage a la función para almacenar la ventana actual
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

            if (!fieldCantidad.getText().isEmpty() && !fieldPrice.getText().isEmpty() && !fieldnameProduct.getText().isEmpty() && listProveedores.getValue() != null) {
                // Obtener el nombre del proveedor seleccionado desde el ComboBox
                String nombreProveedorSeleccionado = listProveedores.getValue().toString();

                // Obtener el ID del proveedor correspondiente al nombre seleccionado
                tblproduct = new TblProductController();
                int idProveedorSeleccionado = tblproduct.obtenerIdProveedorPorNombre(nombreProveedorSeleccionado);

                System.out.println(bandera);
                if(bandera){
                    query = "UPDATE `tbl_productos` SET "
                            + "`nombreProducto`=?,"
                            + "`cantidad`=?,"
                            + "`precio`=?,"
                            + "`idProveedor`= ? WHERE idProducto = '"+idProducto+"'";
                }else{
                    query = "INSERT INTO `tbl_productos`(`nombreProducto`, `cantidad`, `precio`, `idProveedor`) VALUES (?,?,?,?)";
                }

                PreparedStatement preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, fieldnameProduct.getText());
                preparedStatement.setString(2, fieldCantidad.getText());
                preparedStatement.setString(3, fieldPrice.getText());

                // Aquí se obtiene el ID del proveedor seleccionado
                preparedStatement.setInt(4, idProveedorSeleccionado);
                preparedStatement.execute();
                // Limpia los campos después de la inserción
                clear();

                // Cierra la ventana de productos (si es necesario)
                if (stage != null) {
                    stage.close();
                }
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

    public void cargandoDatosComBox() throws SQLException {
        // Obteniendo la conexión a la base de datos aquí
        connect = DatabaseConnection.getConnection();
        statement = connect.createStatement();
        result = statement.executeQuery("SELECT * FROM tbl_proveedor");

        // Creamos una lista para almacenar los datos del proveedor y guardarlo en el combox
        List<String> nombresProveedores = new ArrayList<>();

        while (result.next()) {
            String nombreProveedor = result.getString("nombreProveedor");
            nombresProveedores.add(nombreProveedor);
        }

        //Cerramos todos los recursos  utilizados de la base de datos
        result.close();
        statement.close();
        connect.close();

        ObservableList<String> items = FXCollections.observableArrayList(nombresProveedores);
        listProveedores.setItems(items);
    }
    private void clear() {
        fieldnameProduct.setText(null);
        fieldCantidad.setText(null);
        fieldPrice.setText(null);
    }


    //funcion para guardar el stage actual (ventana principal)
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUpdate(boolean bandera){
        this.bandera = bandera;
    }


}
