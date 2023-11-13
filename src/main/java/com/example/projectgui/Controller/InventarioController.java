package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Inventario;

//Librerias para el generar PDF
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;


import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class InventarioController {

    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos


    //----------------------- Botones para imprimir el inventario -----------------------------
    @FXML
    private Button btnInventarioSinExistencia; //Para imprimir el inventario sin existencias

    @FXML
    private Button btnImprimirInventario; //Para imprimir el inventario en general

    @FXML
    private Button btnGuardarTotalInventario;

    //-------------------------------- Texto de la suma de inventario --------------------------------
    @FXML
    private Text txtTotalInventario;


    //-------------------------- Configuracion de los campos de la tabla que contendrá el inventario ----------
    @FXML
    public TableView<Inventario> tablaInventario;
    @FXML
    public TableColumn<Inventario, Integer> colIdProducto;
    @FXML
    public TableColumn<Inventario, String> colNombreProducto;
    @FXML
    public TableColumn<Inventario, Integer> colCantidad;
    @FXML
    public TableColumn<Inventario, Double> colPrecioCosto;
    @FXML
    public TableColumn<Inventario, Double> colTotalCosto;
    @FXML
    public TableColumn<Inventario, String> colProveedor;

    //Variable global para almacenar la suma de todo el inventario
    double sumaTotalCosto = 0.0;

    //------------------- Se configuran los campos para mostrar los datos en la tabla --------------------------
    public void initialize() {

        // Configura las columnas para que muestren los datos
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioCosto.setCellValueFactory(new PropertyValueFactory<>("precioCosto"));
        colTotalCosto.setCellValueFactory(new PropertyValueFactory<>("totalCosto"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));

        //Se llava a la funcion para cargar los datos en el diseño de la tabla que hemos echo para mostrar el inventario
        cargarDatosDeInventario();

        //Para mostrar la tabla con los productos sin existencias
        btnInventarioSinExistencia.setOnAction(actionEvent -> imprimirInventarioSinExistencia());

        //Para imprimir la tabla de inventario
        btnImprimirInventario.setOnAction(actionEvent -> imprimirInventario());

        //para guardar el total
        btnGuardarTotalInventario.setOnAction(event -> guardarTotalInventario());
    }

    //-------------------- Funcion para poder cargar los datos de la base de datos a la tabla que se ha diseñado-------
    private void cargarDatosDeInventario() {

        //Se crea una lista donde se almacenaran los diferentes procutos
        List<Inventario> listaInventario = new ArrayList<>();

        //Conexion y consulta a la base de datos
        conexion = DatabaseConnection.getConnection();
        String consulta = "SELECT p.idProducto,p.nombreProducto,p.cantidad,p.precioCosto,p.totalCosto,pr.nombreProveedor as Proveedor FROM tbl_productos p JOIN tbl_proveedor pr ON p.idProveedor = pr.idProveedor;";

        try {
            listaInventario.clear(); //Se limpia la lista para que no se dupliquen los datos
            estado = conexion.createStatement();
            resultado = estado.executeQuery(consulta);

            while (resultado.next())
            {
                int idProducto = resultado.getInt("idProducto");
                String nombreProducto = resultado.getString("nombreProducto");
                int cantidad = resultado.getInt("cantidad");
                double precioCosto = resultado.getDouble("precioCosto");
                double totalCosto = resultado.getDouble("totalCosto");
                String proveedor = resultado.getString("Proveedor");

                // Se suma al totalCosto
                sumaTotalCosto += totalCosto;


                //Se crea un un obejeto con el contrutor de la clase Inventario para pasarlo como parametro a la lista
                Inventario producto = new Inventario(idProducto,nombreProducto,cantidad,precioCosto,totalCosto,proveedor);
                listaInventario.add(producto);
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        txtTotalInventario.setText(String.valueOf(sumaTotalCosto));

        // Convertir la lista en un ObservableList
        ObservableList<Inventario> listaProductosObservable = FXCollections.observableArrayList(listaInventario);

        // Establecer el ObservableList como el modelo de datos de la tabla
        tablaInventario.setItems(listaProductosObservable);
    }

    private void imprimirInventarioSinExistencia() {


        //Se crea una nueva lisata para almacenar los datos obtenidos
        List<Inventario> listaSinExistencia = new ArrayList<>();
        String consulta = "SELECT p.idProducto, p.nombreProducto, p.cantidad, p.precioCosto, p.totalCosto, pr.nombreProveedor as Proveedor " +
                "FROM tbl_productos p " +
                "JOIN tbl_proveedor pr ON p.idProveedor = pr.idProveedor " +
                "WHERE p.cantidad = 0;";

        try {

            listaSinExistencia.clear(); //Para que no se dupliquen los datos


            estado = conexion.createStatement();
            resultado = estado.executeQuery(consulta);

            while (resultado.next()) {
                int idProducto = resultado.getInt("idProducto");
                String nombreProducto = resultado.getString("nombreProducto");
                int cantidad = resultado.getInt("cantidad");
                double precioCosto = resultado.getDouble("precioCosto");
                double totalCosto = resultado.getDouble("totalCosto");
                String proveedor = resultado.getString("Proveedor");


                Inventario inventario = new Inventario(idProducto, nombreProducto, cantidad, precioCosto, totalCosto, proveedor);
                listaSinExistencia.add(inventario);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        //Lista de tipo observable
        ObservableList<Inventario> listaSinExistenciaOservable = FXCollections.observableArrayList(listaSinExistencia);
        tablaInventario.setItems(listaSinExistenciaOservable);
    }


    private void imprimirInventario()
    {
        //Se le da fromato a la fecha dia-mes-año
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        String fechaFormateada = formatoFecha.format(new Date());

        // Generar un número aleatorio par añadir a la fecha y que no se repitan los archivos
        Random rand = new Random();
        int numeroAleatorio = rand.nextInt(10000);

        //Se Crea el nombre del archvio y se guarda en el directorio que deseamos
        String nombreArchivo = "InventarioImpreso/Inventario_" + fechaFormateada + "_" + numeroAleatorio + ".pdf";

        try {
            // Se crea el documento pdf
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));

            //EN esta linea se abre el documento para escribir sobre él
            documento.open();

            //Se agrega el encabezado
            Paragraph encabezado = new Paragraph("Inventario Reparaciones Kelly", FontFactory.getFont(FontFactory.HELVETICA_BOLD,18));
            encabezado.setAlignment(Element.ALIGN_CENTER);
            documento.add(encabezado);

            // Salto de línea
            documento.add(new Paragraph("\n"));

            //Determinamos la fecha y hora
            SimpleDateFormat formatoFechaHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String fechaHoraFormateada = formatoFechaHora.format(new Date());

            //Se agrega la fecha y hora al documento
            Paragraph fechaHora = new Paragraph("Fecha y Hora: " + fechaHoraFormateada);
            documento.add(fechaHora);

            // Salto de línea
            documento.add(new Paragraph("\n"));

            PdfPTable tabla = new PdfPTable(5);
            PdfPCell celda;

            // Agregar encabezados de columna con un color en RGB
            celda = new PdfPCell(new Paragraph("idProducto"));
            celda.setBackgroundColor(new BaseColor(173, 216, 230));
            tabla.addCell(celda);

            celda = new PdfPCell(new Paragraph("Nombre Producto"));
            celda.setBackgroundColor(new BaseColor(173, 216, 230));
            tabla.addCell(celda);

            celda = new PdfPCell(new Paragraph("Cantidad"));
            celda.setBackgroundColor(new BaseColor(173, 216, 230));
            tabla.addCell(celda);

            celda = new PdfPCell(new Paragraph("Precio"));
            celda.setBackgroundColor(new BaseColor(173, 216, 230));
            tabla.addCell(celda);

            celda = new PdfPCell(new Paragraph("Proveedor"));
            celda.setBackgroundColor(new BaseColor(173, 216, 230));
            tabla.addCell(celda);


            // Se agregan los datos a la tabla
            for (Inventario producto : tablaInventario.getItems()) {
                tabla.addCell(String.valueOf(producto.getIdProducto()));
                tabla.addCell(producto.getNombreProducto());
                tabla.addCell(String.valueOf(producto.getCantidad()));
                tabla.addCell(String.valueOf(producto.getPrecioCosto()));
                tabla.addCell(producto.getProveedor());
            }

            documento.add(tabla);

            //Se cierra el docuemnto
            documento.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }//Fin de la funcion imprimir;

    public void guardarTotalInventario()
    {
        conexion = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement;
         try {
             // Consulta SQL de inserción
             String query = "INSERT INTO tbl_inventariototal (inventarioTotal) VALUES (?)";
             preparedStatement = conexion.prepareStatement(query);
             preparedStatement.setDouble(1, sumaTotalCosto);

             int filasAfectadas = preparedStatement.executeUpdate();

             if (filasAfectadas > 0){
                 Alert alert = new Alert(Alert.AlertType.INFORMATION);
                 alert.setTitle("Informacion");
                 alert.setHeaderText("Se guardó con exito !!");
                 alert.showAndWait();
             }
             else {
                 Alert alert = new Alert(Alert.AlertType.INFORMATION);
                 alert.setTitle("Informacion");
                 alert.setContentText("No se guardó !!");
                 alert.showAndWait();
             }

         }catch (SQLException e)
         {
             e.printStackTrace();
         }
    }

}//Fin de la clase


