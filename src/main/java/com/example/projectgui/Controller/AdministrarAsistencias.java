package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Asistencias;
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

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AdministrarAsistencias {

    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos

    //Boton para imprimir el informe de asistencias
    @FXML
    public Button btnImprimirInformeAsistencias;

    @FXML
    public TableView<Asistencias> tablaAsistencias;
    @FXML
    public TableColumn<Asistencias, Integer> colIdAsistencia;
    @FXML
    public TableColumn<Asistencias, String> colNombreEmpleado;
    @FXML
    public TableColumn<Asistencias, String> colApellidosEmpleado;
    @FXML
    public TableColumn<Asistencias, String> colFechaAsistencia;
    @FXML
    public TableColumn<Asistencias, String> colHoraEntrada;
    @FXML
    public TableColumn<Asistencias, String> colHoraSalida;

    public void initialize()
    {
        colIdAsistencia.setCellValueFactory(new PropertyValueFactory<>("idAsistencia"));
        colNombreEmpleado.setCellValueFactory(new PropertyValueFactory<>("nombresEmpleado"));
        colApellidosEmpleado.setCellValueFactory(new PropertyValueFactory<>("apellidosEmpleado"));
        colFechaAsistencia.setCellValueFactory(new PropertyValueFactory<>("fechaAsistencia"));
        colHoraEntrada.setCellValueFactory(new PropertyValueFactory<>("horaEntrada"));
        colHoraSalida.setCellValueFactory(new PropertyValueFactory<>("horaSalida"));

        cargarRegistroDeAsistencias();

        //Se hace la funcion del boton para imprimir
        btnImprimirInformeAsistencias.setOnAction(accion -> imprimirInformeAsistecnias());
    }

    //Fucnion para poder imprimir el informe, no importa si la tabla está filtrada
    private void imprimirInformeAsistecnias() {

        //Se le da fromato a la fecha dia-mes-año
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        String fechaFormateada = formatoFecha.format(new Date());

        // Generar un número aleatorio par añadir a la fecha y que no se repitan los archivos
        Random rand = new Random();
        int numeroAleatorio = rand.nextInt(10000);

        //Se Crea el nombre del archvio y se guarda en el directorio que deseamos
        String nombreArchivo = "InformeAsistencias/Asistencia_" + fechaFormateada + "_" + numeroAleatorio + ".pdf";

        try {

            // Se crea el documento pdf
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));

            //EN esta linea se abre el documento para escribir sobre él
            documento.open();

            //Se agrega el encabezado
            Paragraph encabezado = new Paragraph("Informe de Asistencias", FontFactory.getFont(FontFactory.HELVETICA_BOLD,18));
            encabezado.setAlignment(Element.ALIGN_CENTER);
            documento.add(encabezado);

            // Salto de línea
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));

            SimpleDateFormat fechaYhora = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
            String fechaDeLaReparacion = fechaYhora.format(new java.util.Date());

            //Se agrega la fecha y la hora al doccumento
            Paragraph fechaAgregar = new Paragraph("Fecha y hora de emisión: "+ fechaDeLaReparacion);
            documento.add(fechaAgregar);

            //Salto de linea
            documento.add(new Paragraph("\n"));

            // Crear una tabla para la información de asistencias
            PdfPTable tabla = new PdfPTable(6); // 6 columnas para tus datos

            // Establecer el ancho de las columnas (puedes ajustar estos valores según tus necesidades)
            float[] columnWidths = {2, 4, 4, 3, 3, 3};
            tabla.setWidths(columnWidths);

            // Agregar los datos de las asistencias a la tabla
            PdfPCell cell;
            // Agregar encabezados de columna
            cell = new PdfPCell(new Phrase("ID Asistencia"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Color de fondo para encabezados
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Nombre Empleado"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Apellidos Empleado"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Fecha Asistencia"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Hora Entrada"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(cell);
            cell = new PdfPCell(new Phrase("Hora Salida"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(cell);

            // Agregar los datos de las asistencias desde tu tabla
            for (Asistencias asistencia : tablaAsistencias.getItems()) {
                tabla.addCell(Integer.toString(asistencia.getIdAsistencia()));
                tabla.addCell(asistencia.getNombresEmpleado());
                tabla.addCell(asistencia.getApellidosEmpleado());
                tabla.addCell(asistencia.getFechaAsistencia());
                tabla.addCell(asistencia.getHoraEntrada());
                tabla.addCell(asistencia.getHoraSalida());
            }

            // Agregar la tabla al documento
            documento.add(tabla);

            // Cerrar el documento
            documento.close();

            Alert alertaConfirmar = new Alert(Alert.AlertType.INFORMATION);
            alertaConfirmar.setTitle("Confirmacion");
            alertaConfirmar.setHeaderText("Se ha creado el informe");
            alertaConfirmar.showAndWait();

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //Funcion para poder cargar el registro de datos de la tbl_asistencias a la vista fxml
    private void cargarRegistroDeAsistencias() {

        List<Asistencias> listaAsistencias = new ArrayList<>();

        conexion = DatabaseConnection.getConnection();
        String consultaAsistencias = "SELECT A.idAsistencia, E.nombres, E.apellidos, A.fecha, A.horaEntrada, A.horaSalida FROM tbl_asistencias A INNER JOIN tbl_empleados E ON A.idEmpleado = E.idEmpleado;";

        try {
            estado = conexion.createStatement();
            resultado = estado.executeQuery(consultaAsistencias);
            while (resultado.next())
            {
                int idAsistencia = resultado.getInt("idAsistencia");
                String nombresEmpleado = resultado.getString("nombres");
                String apellidosEmpleado = resultado.getString("apellidos");
                String fechaAsistencia = resultado.getString("fecha");
                String horaEntrada = resultado.getString("horaEntrada");
                String horaSalida = resultado.getString("horaSalida");

                Asistencias objetoAsistencias = new Asistencias(idAsistencia,nombresEmpleado,apellidosEmpleado,fechaAsistencia,horaEntrada,horaSalida);
                listaAsistencias.add(objetoAsistencias);
            }

            ObservableList<Asistencias> listaObsevableAsistencias = FXCollections.observableArrayList(listaAsistencias);
            tablaAsistencias.setItems(listaObsevableAsistencias);

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

    }
}
