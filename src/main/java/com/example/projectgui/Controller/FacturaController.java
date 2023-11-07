package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Factura;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FacturaController {

    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos

    @FXML
    public TableView<Factura> tablaFacturas;
    @FXML
    public TableColumn<Factura,Integer> columnIdFactura;
    @FXML
    public TableColumn<Factura,String> columnFechaFactura;
    @FXML
    public TableColumn<Factura,Double> columnMontoTotal;
    @FXML
    public TableColumn<Factura,Integer> columnCantidadProductos;
    @FXML
    public TableColumn<Factura,String> columnProductos;
    @FXML
    public TableColumn<Factura,String> columnNombreCliente;
    @FXML
    public TableColumn<Factura,String> columnTelefonoCliente;

    public void initialize()
    {
        columnIdFactura.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        columnFechaFactura.setCellValueFactory(new PropertyValueFactory<>("fechaFactura"));
        columnMontoTotal.setCellValueFactory(new PropertyValueFactory<>("montoTotal"));
        columnCantidadProductos.setCellValueFactory(new PropertyValueFactory<>("cantidadProducto"));
        columnProductos.setCellValueFactory(new PropertyValueFactory<>("productos"));
        columnNombreCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        columnTelefonoCliente.setCellValueFactory(new PropertyValueFactory<>("telefonoCliente"));

        cargarDatosDeFacturas();

    }

    private void cargarDatosDeFacturas() {

        List<Factura> listaDeFacturas = new ArrayList<>();

        conexion = DatabaseConnection.getConnection();
        String consultaFacturas = "SELECT F.idFactura, F.fechaFactura, F.montoTotal, V.productos, V.cantidadProducto, V.nombreCliente, V.telefonoCliente FROM tbl_facturas F JOIN tbl_ventas V ON F.idVenta = V.idVenta;";
        try {
            listaDeFacturas.clear();
            estado = conexion.createStatement();
            resultado = estado.executeQuery(consultaFacturas);

            while (resultado.next())
            {
                int idFactura = resultado.getInt("idFactura");
                String fechaFactura = resultado.getString("fechaFactura");
                double montoTotal = resultado.getDouble("montoTotal");
                String productos = resultado.getString("productos");
                int cantidadProducto = resultado.getInt("cantidadProducto");
                String nombreCliente = resultado.getString("nombreCliente");
                String telefonoCliente = resultado.getString("telefonoCliente");

                Factura factura = new Factura(idFactura,fechaFactura,montoTotal,cantidadProducto,productos,nombreCliente,telefonoCliente);
                listaDeFacturas.add(factura);
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        ObservableList<Factura> listaObservableFacturas = FXCollections.observableArrayList(listaDeFacturas);
        tablaFacturas.setItems(listaObservableFacturas);

    }

}
