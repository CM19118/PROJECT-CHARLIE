package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Compras;
import com.example.projectgui.Models.Garantia;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ComprasController implements Initializable {
    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos
    /////////////////////////////////////////////////////////
    @FXML
    public TableView<Compras> tablaCompras;
    @FXML
    public TableColumn<Garantia, Integer> columnIdCompra;
    @FXML
    public TableColumn<Garantia, String> columnNombreArticulo;
    @FXML
    public TableColumn<Garantia, String> columnCantidad;
    @FXML
    public TableColumn<Garantia, String> columnMontoAPagar;
    @FXML
    public TableColumn<Garantia, String> columnFechaCompra;
    @FXML public ComboBox listNombreArticulos, listProveedores;
    @FXML public TextField fieldCantidad, fieldMonto;
    @FXML public Button btnAggCompra, btnCancelarComp;
    List<Compras> listaCompras = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnIdCompra.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnNombreArticulo.setCellValueFactory(new PropertyValueFactory<>("nombreArticulo"));
        columnCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnMontoAPagar.setCellValueFactory(new PropertyValueFactory<>("montoCompra"));
        columnFechaCompra.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));

        cargarRegistroDeGarantias();
    }
    @FXML
    public void BotonManejoCompra(){


    }

    @FXML
    public void onCancelarCompra(){


    }

    private void cargarRegistroDeGarantias() {

        conexion = DatabaseConnection.getConnection();
        String consultaGarantias = "SELECT * FROM tbl_ventas;";

        try {
            listaCompras.clear();
            estado = conexion.createStatement();
            resultado = estado.executeQuery(consultaGarantias);

            while (resultado.next())
            {
                int idCompra = resultado.getInt("id");
                String nombreArticulo = resultado.getString("nombreArticulo");
                int cantidad = resultado.getInt("cantidad");
                double montoCompra = resultado.getDouble("montoCompra");
                String fechaCompra = resultado.getString("fechaCompra");

                Compras compra = new Compras(idCompra,nombreArticulo,cantidad,montoCompra,fechaCompra);
                listaCompras.add(compra);

            }

            ObservableList<Compras> listaObservableCompras = FXCollections.observableArrayList(listaCompras);
            tablaCompras.setItems(listaObservableCompras);

        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
