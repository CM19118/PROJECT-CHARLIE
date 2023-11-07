package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Venta;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class VentasController{

    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos

    //Boton para imprimir el informe de articulos vendidos y botones de buscar
    @FXML
    public Button btnImprimirInforme;
    @FXML
    public Button btnFiltrarPorFecha;
    @FXML
    public Button btnFiltrarPorRango;



    // Para los elemntos del calendario
    @FXML
    public DatePicker buscarPorFecha;
    @FXML
    public DatePicker fecha1, fecha2;

    @FXML
    public TableView<Venta> tablaVentas;
    @FXML
    public TableColumn<Venta, Integer> columnIdVenta;
    @FXML
    public TableColumn<Venta, String> columnProductos;
    @FXML
    public TableColumn<Venta, String> columnFechaVenta;
    @FXML
    public TableColumn<Venta, Integer> columnCantidadProducto;
    @FXML
    public TableColumn<Venta, Double> columnTotalProdcuto;


    public void initialize()
    {
        columnIdVenta.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        columnProductos.setCellValueFactory(new PropertyValueFactory<>("productos"));
        columnFechaVenta.setCellValueFactory(new PropertyValueFactory<>("fechaVenta"));
        columnCantidadProducto.setCellValueFactory(new PropertyValueFactory<>("cantidadProducto"));
        columnTotalProdcuto.setCellValueFactory(new PropertyValueFactory<>("total"));

        cargarRegistroDeVentas();

        //Para imprimir los articulos vendidos en general
        btnImprimirInforme.setOnAction(actionEvent -> imprimirInformeDeVentas());

        //Para filtrar por fecha
        btnFiltrarPorFecha.setOnAction(actionEvent -> filtrarPorFecha());

        //Para filtrar por rango de fecha
        btnFiltrarPorRango.setOnAction(actionEvent -> filtrarPorRango());
    }

    private void cargarRegistroDeVentas() {

        List<Venta> listaDeVentas = new ArrayList<>();

        conexion = DatabaseConnection.getConnection();
        String consultaVentas = "SELECT idVenta, productos, fechaVenta, cantidadProducto, total FROM tbl_ventas;";

        try {
            listaDeVentas.clear();
            estado = conexion.createStatement();
            resultado = estado.executeQuery(consultaVentas);

            while (resultado.next())
            {
                int idVenta = resultado.getInt("idVenta");
                String productos = resultado.getString("productos");
                String fechaVenta = resultado.getString("fechaVenta");
                int cantidadProductos = resultado.getInt("cantidadProducto");
                double total = resultado.getDouble("total");

                Venta venta = new Venta(idVenta,fechaVenta,productos,cantidadProductos,total);
                listaDeVentas.add(venta);
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        ObservableList<Venta> listaDeVentasObservable = FXCollections.observableArrayList(listaDeVentas);
        tablaVentas.setItems(listaDeVentasObservable);
    }
     private void imprimirInformeDeVentas()
     {
         //Se crea el fromato de la fecha para poder añadirla al archivo pdf
         SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
         String fechaFormateada = formatoFecha.format(new Date());

         //Se genera un numero ramdom, esto con el obejetivo de que el archivo que se cree no exista y de un problema a la hora de crear el archvio
         Random rand = new Random();
         int numeroAleatorio = rand.nextInt(10000);

         //Se ingresa la ruta y el nombre del archivo que se va a crear
         String nombreArchivo = "InformeArticulosVendidos/InformeArticulosVendidos_"+fechaFormateada+"_"+numeroAleatorio+".pdf";

         try{

             //Se crea el documento pdf
             Document documento = new Document();
             PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));

             // Se abre el documento
             documento.open();

             //Se agrega el encabezado del documento pdf
             Paragraph encabezadoDeInforme = new Paragraph("Reparaciones Kelly", FontFactory.getFont(FontFactory.HELVETICA_BOLD,18));
             encabezadoDeInforme.setAlignment(Element.ALIGN_CENTER);
             documento.add(encabezadoDeInforme);

             //Segundo encaabezado de como subtitulo indicando el tirulo del informe
             Paragraph encabezadoSecundario = new Paragraph("Informe de Articulos Vendidos",FontFactory.getFont(FontFactory.HELVETICA_BOLD,15));
             encabezadoSecundario.setAlignment(Element.ALIGN_CENTER);
             documento.add(encabezadoSecundario);

             //Salto de linea
             documento.add(new Paragraph("\n"));

             SimpleDateFormat fechaYhora = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
             String fechaDelInforme = fechaYhora.format(new Date());

             //Se agrega la fecha y la hora al doccumento
             Paragraph fechaAgregar = new Paragraph("Imforme Impreso el: "+fechaDelInforme);
             documento.add(fechaAgregar);

             //Salto de linea
             documento.add(new Paragraph("\n"));

             PdfPTable tabla = new PdfPTable(5);
             PdfPCell celda;

             // Agregar encabezados de columna con un color en RGB
             celda = new PdfPCell(new Paragraph("ID Venta"));
             celda.setBackgroundColor(new BaseColor(173, 216, 230));
             tabla.addCell(celda);

             celda = new PdfPCell(new Paragraph("Fecha de la Venta"));
             celda.setBackgroundColor(new BaseColor(173, 216, 230));
             tabla.addCell(celda);

             celda = new PdfPCell(new Paragraph("Productos Comprados"));
             celda.setBackgroundColor(new BaseColor(173, 216, 230));
             tabla.addCell(celda);

             celda = new PdfPCell(new Paragraph("Total de Productos Comprados"));
             celda.setBackgroundColor(new BaseColor(173, 216, 230));
             tabla.addCell(celda);

             celda = new PdfPCell(new Paragraph("Total Gastado"));
             celda.setBackgroundColor(new BaseColor(173, 216, 230));
             tabla.addCell(celda);

             //Se recurre la tabla que contine los elementos que se desean ingresar al documento
             for (Venta venta : tablaVentas.getItems())
             {
                 tabla.addCell(String.valueOf(venta.getIdVenta()));
                 tabla.addCell(String.valueOf(venta.getFechaVenta()));
                 tabla.addCell(String.valueOf(venta.getProductos()));
                 tabla.addCell(String.valueOf(venta.getCantidadProducto()));
                 tabla.addCell(String.valueOf(venta.getTotal()));
             }

             documento.add(tabla);

             //Se cierra el documento pdf
             documento.close();

         }catch (Exception e)
         {
             e.printStackTrace();
         }

     } //Fin de la función de imprimir

    private void filtrarPorFecha()
    {
        //Se obtine la fecha del campo que se ha indicado para ingresar la fecha
        LocalDate fechaCalendario1 = buscarPorFecha.getValue();

        if (fechaCalendario1 == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("INGRESE UNA FECHA");
            alert.showAndWait();

        }else{

            //Se crea una lista para ingresar los datos por fecha
            List<Venta> listaVentasPorFecha = new ArrayList<>();
            String consultaPorFecha = "SELECT idVenta, productos, fechaVenta, cantidadProducto, total FROM tbl_ventas WHERE fechaVenta = '"+fechaCalendario1+"';";

            try {
                listaVentasPorFecha.clear();

                estado = conexion.createStatement();
                resultado = estado.executeQuery(consultaPorFecha);

                while (resultado.next())
                {
                    int idVenta = resultado.getInt("idVenta");
                    String productos = resultado.getString("productos");
                    String fechaVenta = resultado.getString("fechaVenta");
                    int cantidadProductos = resultado.getInt("cantidadProducto");
                    double total = resultado.getDouble("total");

                    Venta ventaObjeto = new Venta(idVenta,fechaVenta,productos,cantidadProductos,total);
                    listaVentasPorFecha.add(ventaObjeto);
                }

            }catch (SQLException e){
                e.printStackTrace();
            }

            ObservableList<Venta> listaObservablePorFecha = FXCollections.observableArrayList(listaVentasPorFecha);
            tablaVentas.setItems(listaObservablePorFecha);

        }//Fin del else



    }

    private void filtrarPorRango(){
        //Se obtine la fecha del campo que se ha indicado para ingresar la fecha
        LocalDate rango1 = fecha1.getValue();
        LocalDate rango2 = fecha2.getValue();

        if (rango1 == null || rango2 == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("RELLENE LOS CAMPOS DE FECHAS");
            alert.showAndWait();

        }else {

            //Se crea una lista para ingresar los datos por fecha
            List<Venta> listaVentasPorRango = new ArrayList<>();
            String consultaPorRango = "SELECT idVenta, productos, fechaVenta, cantidadProducto, total "+
                    "FROM tbl_ventas "+
                    "WHERE fechaVenta BETWEEN '"+rango1+"' AND '"+rango2+"';";

            try {
                listaVentasPorRango.clear();

                estado = conexion.createStatement();
                resultado = estado.executeQuery(consultaPorRango);

                while (resultado.next())
                {
                    int idVenta = resultado.getInt("idVenta");
                    String productos = resultado.getString("productos");
                    String fechaVenta = resultado.getString("fechaVenta");
                    int cantidadProductos = resultado.getInt("cantidadProducto");
                    double total = resultado.getDouble("total");

                    Venta ventaPorRango = new Venta(idVenta,fechaVenta,productos,cantidadProductos,total);
                    listaVentasPorRango.add(ventaPorRango);
                }

            }catch (SQLException e){
                e.printStackTrace();
            }

            ObservableList<Venta> listaObservablePorRango = FXCollections.observableArrayList(listaVentasPorRango);
            tablaVentas.setItems(listaObservablePorRango);

        } //Fin del else


    }

}
