package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Compras;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ComprasController implements Initializable{
    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos
    /////////////////////////////////////////////////////////
    @FXML
    private TableView<Compras> tablaCompras;

    @FXML
    private TableColumn<Compras, Integer> columnIdCompra;

    @FXML
    private TableColumn<Compras, String> columnNombreArticulo;

    @FXML
    private TableColumn<Compras, Integer> columnCantidad;

    @FXML
    private TableColumn<Compras, Double> columnMontoAPagar;

    @FXML
    private TableColumn<Compras, String> columnFechaCompra;

    private ObservableList<Compras> dataList = FXCollections.observableArrayList();

    /////////////////////////////////////////////////////////
    @FXML private ComboBox<String> listNombreArticulos, listProveedores;
    @FXML public TextField fieldCantidad, fieldMonto;
    @FXML public Button btnAggCompra;
    @FXML public DatePicker fechaData;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = new Stage();
        setStage(stage);
        MostrasrlistaCompras();
    }

    public ObservableList<Compras> listaCompras() {
        conexion = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM tbl_compras";
        try {
            dataList.clear();
            estado = conexion.createStatement();
            resultado = estado.executeQuery(sql);
            Compras compras;

            while (resultado.next()) {
                int id = resultado.getInt("id");
                String nombreArticulo = resultado.getString("nombreArticulo");
                int cantidad = resultado.getInt("cantidad");
                double montoCompra = resultado.getDouble("montoCompra");
                String fechaCompra = resultado.getString("fechaCompra");

                compras = new Compras(id,nombreArticulo,cantidad,montoCompra,fechaCompra);
                dataList.addAll(compras);
            }
            conexion.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }
    private void MostrasrlistaCompras() {
        if (tablaCompras != null) {
            ObservableList<Compras> mostrarCompras = listaCompras();

            columnIdCompra.setCellValueFactory(new PropertyValueFactory<>("id"));
            columnNombreArticulo.setCellValueFactory(new PropertyValueFactory<>("nombreArticulo"));
            columnCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            columnMontoAPagar.setCellValueFactory(new PropertyValueFactory<>("montoCompra"));
            columnFechaCompra.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));

            tablaCompras.setItems(mostrarCompras);
            tablaCompras.refresh();
        }
    }
    @FXML
    public void BotonManejoCompra() {
        String query = "";

        try {
            conexion = DatabaseConnection.getConnection(); // Abre la conexión
            if (!fieldCantidad.getText().isEmpty() && !fieldMonto.getText().isEmpty() && listProveedores.getValue() != null && listNombreArticulos.getValue() != null) {
                // Obtener el ID del proveedor correspondiente al nombre seleccionado
                query = "INSERT INTO `tbl_compras`(`nombreArticulo`, `cantidad`, `montoCompra`, `fechaCompra`) VALUES (?,?,?,?)";
                PreparedStatement preparedStatement = conexion.prepareStatement(query);
                preparedStatement.setString(1, listNombreArticulos.getValue().toString());
                preparedStatement.setInt(2, Integer.parseInt(fieldCantidad.getText()));
                preparedStatement.setDouble(3, Double.parseDouble(fieldMonto.getText()));
                preparedStatement.setString(4, fechaData.getValue().toString());
                preparedStatement.execute();

                // Actualizar la cantidad de productos en la tabla tbl_productos
                query = "UPDATE `tbl_productos` SET `cantidad` = `cantidad` + ? WHERE `nombreProducto` = ?";
                PreparedStatement updateStatement = conexion.prepareStatement(query);
                updateStatement.setInt(1, Integer.parseInt(fieldCantidad.getText()));
                updateStatement.setString(2, listNombreArticulos.getValue().toString());
                updateStatement.executeUpdate();

                MostrasrlistaCompras();

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
            throw new RuntimeException(e);
        }
    }

    public void cargandoProveedoresCombox() throws SQLException {
        // Obteniendo la conexión a la base de datos aquí
        conexion = DatabaseConnection.getConnection();
        estado = conexion.createStatement();
        resultado = estado.executeQuery("SELECT * FROM tbl_proveedor");

        // Creamos una lista para almacenar los datos del proveedor y guardarlo en el combox
        List<String> nombresProveedores = new ArrayList<>();

        while (resultado.next()) {
            String nombreProveedor = resultado.getString("nombreProveedor");
            nombresProveedores.add(nombreProveedor);
        }

        //Cerramos todos los recursos  utilizados de la base de datos
        resultado.close();
        estado.close();
        conexion.close();

        ObservableList<String> items = FXCollections.observableArrayList(nombresProveedores);
        listProveedores.setItems(items);
    }

    public void cargandoProductosCombox() throws SQLException {
        // Obteniendo la conexión a la base de datos aquí
        conexion = DatabaseConnection.getConnection();
        estado = conexion.createStatement();
        resultado = estado.executeQuery("SELECT * FROM tbl_productos");

        // Creamos una lista para almacenar los datos del proveedor y guardarlo en el combox
        List<String> nombresProductos = new ArrayList<>();

        while (resultado.next()) {
            String nombreProducto = resultado.getString("nombreProducto");
            nombresProductos.add(nombreProducto);
        }

        //Cerramos todos los recursos  utilizados de la base de datos
        resultado.close();
        estado.close();
        conexion.close();

        ObservableList<String> items = FXCollections.observableArrayList(nombresProductos);
        listNombreArticulos.setItems(items);
    }

    @FXML
    public void aggCompra() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formCompras.fxml"));
            Parent parent = loader.load();
            ComprasController comprasController = loader.getController(); // Obtener el controlador
            comprasController.cargandoProductosCombox();
            comprasController.cargandoProveedoresCombox();

            Scene scene = new Scene(parent);
            stage = new Stage();
            comprasController.setStage(stage); // Pasar el Stage a la función para almacenar la ventana actual
            stage.setScene(scene); // Usa el Stage local
            stage.initStyle(StageStyle.UTILITY);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onCancelarCompra(){
        stage.close();
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }



}
