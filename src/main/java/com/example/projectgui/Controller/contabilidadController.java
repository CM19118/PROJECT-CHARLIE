package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.EstadoResultados;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class contabilidadController {

    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos

    @FXML
    public Text inventarioTotal;
    @FXML
    public Text totalCuentasCobrar;
    @FXML
    public Text totalEfectivo;

    //----------------- Configuracion de las variables del fxml --------------------
    @FXML
    public Text totalCuentasPorPagar, deudasCortoPlazo, totalImpuestos, totalPasivosCirculantes, totalActivoCirculante,totalCapitalTrabajo;
    @FXML
    public Button btnAgregarDeudasImpuestos,btnCalcularCapitalTrabajo;
    @FXML
    public TextField txtDeudasCortoPlazo, txtImpuestos;

    //Configuracion del las varibles del estado de rresultados
    @FXML
    public TextField devolucionesVentas,descuentoVentas,costoDeVentas,gastosOperativos,GastosFinancieros,impuestosEstadoResultados;
    @FXML
    public Button btnGenerarEstadoDeResultados;
    @FXML
    public Button btnGenrarInforme;

    //Configurar la tabla
    @FXML
    public TableView<EstadoResultados> tablaEstadoDeResultados;

    public void initialize(){
        cargarTotales();
        btnAgregarDeudasImpuestos.setOnAction(event -> cargarImpuestoDeudas());
        cargarTotalActivoPasivo();
        btnCalcularCapitalTrabajo.setOnAction(event -> calculoCapitalTrabajo());

        //Boton para crear el estado de resultados
        btnGenerarEstadoDeResultados.setOnAction(event -> generarEstadoResultados());

        //boton para generar el pdf del informe de contabilidad
        btnGenrarInforme.setOnAction(event -> crearInforme());
    }

    private void crearInforme() {

        //Se le da fromato a la fecha dia-mes-año
        SimpleDateFormat formatoFecha1 = new SimpleDateFormat("dd-MM-yyyy");
        String fechaParaNombreArchivo = formatoFecha1.format(new Date());

        Random rand = new Random();
        int numeroAleatorio = rand.nextInt(10000);

        //Se Crea el nombre del archvio y se guarda en el directorio que deseamos
        String nombreArchivo = "InformeContabilidad/Informe" + fechaParaNombreArchivo +"_"+ numeroAleatorio + ".pdf";

        try {
            // Crea un nuevo documento PDF
            Document documento = new Document();
            FileOutputStream archivoPDF = new FileOutputStream(nombreArchivo);
            PdfWriter.getInstance(documento, archivoPDF);

            // Abre el documento para escribir
            documento.open();

            // Define la fuente y el tamaño de la letra para el encabezado
            Font fuenteEncabezado = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

            // Agrega el encabezado centrado
            Paragraph encabezado = new Paragraph("Reparaciones Kelly", fuenteEncabezado);
            encabezado.setAlignment(Paragraph.ALIGN_CENTER);
            documento.add(encabezado);
            documento.add(new Paragraph("\n"));

            Paragraph encabezado2 = new Paragraph("Informe de Contabilidad", fuenteEncabezado);
            encabezado.setAlignment(Paragraph.ALIGN_CENTER);
            documento.add(encabezado2);
            documento.add(new Paragraph("\n"));

            // Agrega la fecha
            //Determinamos la fecha y hora
            SimpleDateFormat formatoFechaHora = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
            String fechaHoraParaFactura = formatoFechaHora.format(new Date());
            Paragraph fechaParagraph = new Paragraph("Fecha: " + fechaHoraParaFactura);
            fechaParagraph.setAlignment(Element.ALIGN_RIGHT);
            documento.add(fechaParagraph);
            // Salto de línea
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));

            // Agrega el nombre del cliente, teléfono y DUI
            documento.add(new Paragraph("Activos Circulantes"));
            documento.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------------"));
            documento.add(new Paragraph("Total de Efectivo: $ "+ totalEfectivo.getText()));
            documento.add(new Paragraph("Cuentas por Cobrar: $ "+ totalCuentasCobrar.getText()));
            documento.add(new Paragraph("Total Inventario: $ "+ inventarioTotal.getText()));
            documento.add(new Paragraph("TOTAL ACTIVO CIRCULANTE: $ "+ totalActivoCirculante.getText()));
            documento.add(new Paragraph("\n"));


            // Agrega los productos, cantidad total, precio unitario y total gastado
            documento.add(new Paragraph("Pasivo Circulantes"));
            documento.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------------"));
            documento.add(new Paragraph("Cuentas por Pagar: $ "+ totalCuentasPorPagar.getText()));
            documento.add(new Paragraph("Deudas a Corto Plazo: $ "+ deudasCortoPlazo.getText()));
            documento.add(new Paragraph("Impuestos: $ "+ totalImpuestos.getText()));
            documento.add(new Paragraph("TOTAL PASIVO CIRCULANTE: $ "+ totalPasivosCirculantes.getText()));
            documento.add(new Paragraph("\n"));

            documento.add(new Paragraph("Capital de Trabajo"));
            documento.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------------"));
            documento.add(new Paragraph("Capital de Trabajo Neto (CNT): $ "+ totalCapitalTrabajo.getText()));
            documento.add(new Paragraph("\n"));

            Paragraph paragraph = new Paragraph("Estado de Resultados al " + fechaParaNombreArchivo);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            documento.add(paragraph);
            documento.add(new Paragraph("\n"));


            PdfPTable tabla = new PdfPTable(2);
            PdfPCell celda;
            // Agregar encabezados de columna con un color en RGB
            celda = new PdfPCell(new Paragraph());
            celda.setBackgroundColor(new BaseColor(173, 216, 230));
            tabla.addCell(celda);

            celda = new PdfPCell(new Paragraph());
            celda.setBackgroundColor(new BaseColor(173, 216, 230));
            tabla.addCell(celda);

            // Se agregan los datos a la tabla
            for (EstadoResultados estadoResultados : tablaEstadoDeResultados.getItems()) {
                tabla.addCell(estadoResultados.getConcepto().toString());
                tabla.addCell(estadoResultados.getMonto().toString());
            }

            documento.add(tabla);

            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));
            Paragraph paragraph2 = new Paragraph("© Mike's Inc. Developers for ANF™");
            paragraph2.setAlignment(Element.ALIGN_CENTER);
            documento.add(paragraph2);

            // Cierra el documento
            documento.close();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Informacion");
            alert.setHeaderText("Se ha creado el informe !!");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    private void generarEstadoResultados() {

        double resultadoVentas = Double.parseDouble(totalEfectivo.getText());
        double resultadoDevoluciones = Double.parseDouble(devolucionesVentas.getText());
        double resultadoDescuentoVentas = Double.parseDouble(descuentoVentas.getText());

        //--------------- Primera operacion del estado de resultados
        double resultadoVentasNetas = resultadoVentas - resultadoDevoluciones -resultadoDescuentoVentas;

        double resultadoCostoVentas = Double.parseDouble(costoDeVentas.getText());

        //----------------- Segunda operacion ------------------------------------
        double resultadoUtilidadBruta = resultadoVentasNetas - resultadoCostoVentas;

        double resultadoGastosOperativos = Double.parseDouble(gastosOperativos.getText());

        //------ Tercera operacion ------------------------------------------------------
        double rsultadoUtilidadOperativa = resultadoUtilidadBruta - resultadoGastosOperativos;

        double resultadoGastosFinancieros = Double.parseDouble(GastosFinancieros.getText());

        //-------------Cuarta Operacion -------------------------------------------
        double resultadoUtilidadAntesImpuestos = rsultadoUtilidadOperativa - resultadoGastosFinancieros;

        double resultadoImpuestos = Double.parseDouble(impuestosEstadoResultados.getText());

        //---------------- Quienta operacion ---------------------
        double resultadoUtilidadNeta = resultadoUtilidadAntesImpuestos -resultadoImpuestos;


        // Crear la lista de items para la tabla
        List<EstadoResultados> items = new ArrayList<>();
        items.add(new EstadoResultados("Ingreso Por Ventas", "$ "+resultadoVentas));
        items.add(new EstadoResultados("Devoluciones sobre ventas", "$ "+resultadoDevoluciones));
        items.add(new EstadoResultados("Descuento sobre ventas", "$ "+resultadoDescuentoVentas));
        items.add(new EstadoResultados("Ventas Netas", "$ "+resultadoVentasNetas));

        items.add(new EstadoResultados("Costo de ventas", "$ "+resultadoCostoVentas));
        items.add(new EstadoResultados("Utilidad Bruta", "$ "+resultadoUtilidadBruta));

        items.add(new EstadoResultados("Gastos operativos", "$ "+resultadoGastosOperativos));
        items.add(new EstadoResultados("Utilidad Operativa", "$ "+rsultadoUtilidadOperativa));

        items.add(new EstadoResultados("Gatos fianancieros", "$ "+resultadoGastosFinancieros));
        items.add(new EstadoResultados("Utilidad Antes de Impuestos", "$ "+resultadoUtilidadAntesImpuestos));

        items.add(new EstadoResultados("Impuestos", "$ "+resultadoImpuestos));
        items.add(new EstadoResultados("Utilidad Neta", "$ "+resultadoUtilidadNeta));

        // Configurar la tabla
        TableColumn<EstadoResultados, String> conceptoColumn = new TableColumn<>("Concepto");
        conceptoColumn.setCellValueFactory(new PropertyValueFactory<>("concepto"));

        TableColumn<EstadoResultados, Double> montoColumn = new TableColumn<>("Monto");
        montoColumn.setCellValueFactory(new PropertyValueFactory<>("monto"));

        tablaEstadoDeResultados.getColumns().setAll(conceptoColumn, montoColumn);

        // Agregar los items a la tabla
        tablaEstadoDeResultados.getItems().setAll(items);

    }

    private void calculoCapitalTrabajo() {

        double capitalDeTrabajo = Double.parseDouble(totalActivoCirculante.getText()) - Double.parseDouble(totalPasivosCirculantes.getText());
        DecimalFormat formatoDecimal = new DecimalFormat("#.00");
        String capitalFormateado = formatoDecimal.format(capitalDeTrabajo);
        totalCapitalTrabajo.setText(capitalFormateado);
    }

    private void cargarTotalActivoPasivo() {

        double ActivosCirculantesTotal = 0.0;
        double PasivoCirculanteTotal = 0.0;

        ActivosCirculantesTotal = Double.parseDouble(totalEfectivo.getText()) + Double.parseDouble(totalCuentasCobrar.getText()) + Double.parseDouble(inventarioTotal.getText());
        PasivoCirculanteTotal = Double.parseDouble(totalCuentasPorPagar.getText()) + Double.parseDouble(deudasCortoPlazo.getText()) + Double.parseDouble(totalImpuestos.getText());

        // Formatear con dos decimales
        DecimalFormat formatoDecimal = new DecimalFormat("#0.00");
        String activosCirculantesFormateado = formatoDecimal.format(ActivosCirculantesTotal);
        String pasivoCirculanteFormateado = formatoDecimal.format(PasivoCirculanteTotal);

        totalActivoCirculante.setText(activosCirculantesFormateado);
        totalPasivosCirculantes.setText(pasivoCirculanteFormateado);
    }

    private void cargarImpuestoDeudas() {

        double deudasCortoPlazoCatidad = 0.0;
        double impuestos = 0.0;

        try {
            if (!txtDeudasCortoPlazo.getText().isEmpty()) {
                deudasCortoPlazoCatidad = Double.parseDouble(txtDeudasCortoPlazo.getText());
                deudasCortoPlazo.setText(String.valueOf(deudasCortoPlazoCatidad));
            }
            else {
                deudasCortoPlazo.setText(String.valueOf(deudasCortoPlazoCatidad));
            }

            if (!txtImpuestos.getText().isEmpty()) {
                impuestos = Double.parseDouble(txtImpuestos.getText());
                totalImpuestos.setText(String.valueOf(impuestos));
            }
            else {
                totalImpuestos.setText(String.valueOf(impuestos));
            }

        } catch (NumberFormatException e) {
            // Mostrar alerta y limpiar campos en caso de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Dato invalido");
            alert.showAndWait();
        }
        cargarTotalActivoPasivo();
    }

    private void cargarTotales(){

        try {
            // Realizar la conexión a la base de datos
            conexion = DatabaseConnection.getConnection();
            estado = conexion.createStatement();

            String query1 = "SELECT totalCuentaCobrar FROM tbl_cuentascobrar ORDER BY idCuentaCobrar DESC LIMIT 1";
            resultado = estado.executeQuery(query1);
            if (resultado.next()) {
                totalCuentasCobrar.setText(resultado.getString("totalCuentaCobrar"));
            }

            String query2 = "SELECT totalEfectivo FROM tbl_efectivoventas ORDER BY idEfectivo DESC LIMIT 1";
            resultado = estado.executeQuery(query2);
            if (resultado.next()) {
                totalEfectivo.setText(resultado.getString("totalEfectivo"));
            }

            String query3 = "SELECT inventarioTotal FROM tbl_inventariototal ORDER BY idInventario DESC LIMIT 1";
            resultado = estado.executeQuery(query3);
            if (resultado.next()) {
                inventarioTotal.setText(resultado.getString("inventarioTotal"));
            }

            String query4 = "SELECT totalCompras FROM tbl_totalcompras ORDER BY idCompra DESC LIMIT 1";
            resultado = estado.executeQuery(query4);
            if (resultado.next()) {
                totalCuentasPorPagar.setText(resultado.getString("totalCompras"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (resultado != null) resultado.close();
                if (estado != null) estado.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
