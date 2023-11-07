package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Reparacion;
import com.example.projectgui.Models.ReparacionTecnico;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;


import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ReparacionesController {
    //Comentario Para el pull
    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos

    //Boton para abrir el form donde se registrará la reparacion
    @FXML
    public Button btnAgregarReparacion;


    //Configuracion de la tabla para mostrar los datos de las reparaciones
    @FXML
    public TableView<Reparacion> tablaReparaciones;
    @FXML
    public TableColumn<Reparacion, Integer> colIdReparacion;
    @FXML
    public TableColumn<Reparacion, String> colServicio;

    @FXML TableColumn<Reparacion, Double> colPrecioUnitario;
    @FXML
    public TableColumn<Reparacion, String> colFechaInicio;
    @FXML
    public TableColumn<Reparacion, String> colCliente;
    @FXML
    public TableColumn<Reparacion, String> colTelefono;

    @FXML TableColumn<Reparacion, String> colEquipo;
    @FXML
    public TableColumn<Reparacion, String> colDetalles;
    @FXML
    public TableColumn<Reparacion, String> colEstado;
    @FXML
    public TableColumn<Reparacion, String> colEstadoPago;
    @FXML
    public TableColumn<Reparacion, String> colDetallesCostosAdicionales;
    @FXML
    public TableColumn<Reparacion, Double> colMontoCostosAdicionales;
    @FXML
    public TableColumn<Reparacion, Void> colAcciones;
    @FXML
    public TableColumn<Reparacion, Void> colAccionesImpri;


    public void initialize()
    {
        //Configuracion de las columnas
        colIdReparacion.setCellValueFactory(new PropertyValueFactory<>("idReparacion"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicio"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("numTelefono"));
        colEquipo.setCellValueFactory(new PropertyValueFactory<>("equipo"));
        colDetalles.setCellValueFactory(new PropertyValueFactory<>("detalles"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstadoPago.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
        colDetallesCostosAdicionales.setCellValueFactory(new PropertyValueFactory<>("detalleCostAdicional"));
        colMontoCostosAdicionales.setCellValueFactory(new PropertyValueFactory<>("montoCostAdicional"));

        colAcciones.setCellFactory(new Callback<TableColumn<Reparacion, Void>, TableCell<Reparacion, Void>>() {
            @Override
            public TableCell<Reparacion, Void> call(TableColumn<Reparacion, Void> param) {
                return new TableCell<>() {
                    private final Button btnFinalizarReparacion = new Button("Finalizar Reparacion");
                    {
                        btnFinalizarReparacion.setOnAction(actionEvent -> {

                            Reparacion reparacionEquipo = getTableView().getItems().get(getIndex());

                            // Construye la alerta de confirmación
                            Alert alertaFinalizarCompra = new Alert(Alert.AlertType.CONFIRMATION);
                            alertaFinalizarCompra.setTitle("Confirmar");
                            alertaFinalizarCompra.setHeaderText("¿Desea finalizar e imprimir factura?");

                            // Agrega botones "Aceptar" y "Cancelar"
                            ButtonType botonAceptar = new ButtonType("Aceptar");
                            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                            alertaFinalizarCompra.getButtonTypes().setAll(botonAceptar, botonCancelar);

                            // Muestra la alerta y espera a que el usuario seleccione una opción
                            Optional<ButtonType> resultado = alertaFinalizarCompra.showAndWait();

                            // Verifica la respuesta del usuario
                            if (resultado.isPresent() && resultado.get() == botonAceptar) {

                                int idProductoActulizar = reparacionEquipo.getIdReparacion();
                                String consultaActulizar = "UPDATE tbl_reparaciones SET estadoPago = 'Cancelado' WHERE idReparaciones = ?";

                                try(PreparedStatement preparedStatement = conexion.prepareStatement(consultaActulizar)){

                                    preparedStatement.setInt(1, idProductoActulizar);

                                    int rowsUpdated = preparedStatement.executeUpdate();

                                    if (rowsUpdated > 0) {

                                        //------------- Generar el archivo de la factura ------------------------
                                        //Se le da fromato a la fecha dia-mes-año
                                        SimpleDateFormat fechaReparacion = new SimpleDateFormat("dd-MM-yyyy");
                                        String fechaReparacionFactura = fechaReparacion.format(new java.util.Date());
                                        Random rand = new Random();
                                        int numeroAleatorio = rand.nextInt(10000);

                                        //Se Crea el nombre del archvio y se guarda en el directorio que deseamos
                                        String nombreReparacionFactura = "FacturasReparaciones/Reparacion_" + fechaReparacionFactura +""+reparacionEquipo.getCliente()+ "" + numeroAleatorio + ".pdf";

                                        //Para el monto total de la reparacion, es decir el precio unitario mas la reparacion
                                        double totalPagarPorReparacion = reparacionEquipo.getPrecioUnitario() + reparacionEquipo.getMontoCostAdicional();

                                        try {
                                            //Se Crea el docuemnto pdf de la factura de reparaciones
                                            Document documentoPdf = new Document();
                                            FileOutputStream archivoPDF = new FileOutputStream(nombreReparacionFactura);
                                            PdfWriter.getInstance(documentoPdf, archivoPDF);

                                            documentoPdf.open();

                                            // Define la fuente y el tamaño de la letra para el encabezado
                                            Font fuenteEncabezado = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

                                            // Agrega el encabezado centrado
                                            Paragraph encabezado = new Paragraph("Reparaciones Keilly", fuenteEncabezado);
                                            encabezado.setAlignment(Paragraph.ALIGN_CENTER);
                                            documentoPdf.add(encabezado);
                                            documentoPdf.add(new Paragraph("\n"));

                                            //Segundo encaabezado de como subtitulo indicando el tirulo del informe
                                            Paragraph encabezadoSecundario = new Paragraph("Factura de Reparacion", FontFactory.getFont(FontFactory.HELVETICA_BOLD,15));
                                            encabezadoSecundario.setAlignment(Element.ALIGN_CENTER);
                                            documentoPdf.add(encabezadoSecundario);

                                            //Salto de linea
                                            documentoPdf.add(new Paragraph("\n"));

                                            SimpleDateFormat fechaYhora = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                                            String fechaDeLaReparacion = fechaYhora.format(new java.util.Date());

                                            //Se agrega la fecha y la hora al doccumento
                                            Paragraph fechaAgregar = new Paragraph("Fecha y hora de emisión: "+ fechaDeLaReparacion);
                                            documentoPdf.add(fechaAgregar);

                                            //Salto de linea
                                            documentoPdf.add(new Paragraph("\n"));

                                            // Agrega el nombre del cliente, teléfono y DUI
                                            documentoPdf.add(new Paragraph("Datos Generales"));
                                            documentoPdf.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------------"));
                                            documentoPdf.add(new Paragraph("Nombre del Cliente: " + reparacionEquipo.getCliente()));
                                            documentoPdf.add(new Paragraph("Teléfono: " + reparacionEquipo.getNumTelefono()));
                                            documentoPdf.add(new Paragraph("Fecha de inicio de reparacion: " + reparacionEquipo.getFechaInicio()));
                                            documentoPdf.add(new Paragraph("\n"));
                                            documentoPdf.add(new Paragraph("\n"));

                                            documentoPdf.add(new Paragraph("Detalle del Servicio"));
                                            documentoPdf.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------------"));
                                            documentoPdf.add(new Paragraph("Servicio brindado: "+reparacionEquipo.getServicio()));
                                            documentoPdf.add(new Paragraph("Equipo reparado: "+reparacionEquipo.getEquipo()));
                                            documentoPdf.add(new Paragraph("Precio del servicio: $ "+reparacionEquipo.getPrecioUnitario()));
                                            documentoPdf.add(new Paragraph("Detalles: "+reparacionEquipo.getDetalles()));
                                            documentoPdf.add(new Paragraph("\n"));
                                            documentoPdf.add(new Paragraph("\n"));

                                            documentoPdf.add(new Paragraph("Costos Adicionales"));
                                            documentoPdf.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------------"));
                                            documentoPdf.add(new Paragraph("Detalle de costos adicionales: "+reparacionEquipo.getDetalleCostAdicional()));
                                            documentoPdf.add(new Paragraph("Monto de costos adicionales: $ "+reparacionEquipo.getMontoCostAdicional()));
                                            documentoPdf.add(new Paragraph("\n"));
                                            documentoPdf.add(new Paragraph("\n"));

                                            documentoPdf.add(new Paragraph("Total a pagar: $ "+totalPagarPorReparacion));
                                            documentoPdf.add(new Paragraph("\n"));
                                            documentoPdf.add(new Paragraph("\n"));

                                            // Agrega un mensaje de agradecimiento
                                            documentoPdf.add(new Paragraph("Gracias por su preferencia"));

                                            documentoPdf.close();

                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                        //Para insertar en la tabla tbl_facturaReparaciones las facturas
                                        String insertFacturaReparaciones = "INSERT INTO tbl_facturareparaciones (fechaFactura, monto, fechaVencimiento, estadoGarantia, idReparacion) VALUES (?, ?, ?, ?, ?)";
                                        String insertarEstadoGarantia = "Vigente";
                                        LocalDate fechaActual = LocalDate.now();
                                        LocalDate fecha30DiasDespues = fechaActual.plusDays(30);

                                        try {
                                            PreparedStatement insertarTblFacReparaciones = conexion.prepareStatement(insertFacturaReparaciones);
                                            // Establecer los parámetros en la sentencia SQL para tbl_facturasReparaciones

                                            insertarTblFacReparaciones.setDate(1, Date.valueOf(fechaActual));
                                            insertarTblFacReparaciones.setDouble(2, totalPagarPorReparacion);
                                            insertarTblFacReparaciones.setDate(3, Date.valueOf(fecha30DiasDespues));
                                            insertarTblFacReparaciones.setString(4, insertarEstadoGarantia);
                                            insertarTblFacReparaciones.setInt(5, reparacionEquipo.getIdReparacion());

                                            int resultadoConsulta = insertarTblFacReparaciones.executeUpdate();

                                            if (resultadoConsulta > 0 )
                                            {
                                                System.out.println("Se inserto en la tbl_reparacionesFactras");
                                            }
                                            else
                                            {
                                                System.out.println("NO Se insertyro en la tbl_reparacionesFactras");
                                            }


                                        }catch (SQLException e)
                                        {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setContentText("No se pudo cancelar !!");
                                        alert.showAndWait();
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                            }//Fin del if donde se verifica si se ha presionado el boton de aceptar de la alerta

                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {

                            Reparacion reparacionEquipo = getTableView().getItems().get(getIndex());

                            if (reparacionEquipo.getEstado().equals("Completado") && reparacionEquipo.getEstadoPago().equals("No cancelado")) {
                                setGraphic(btnFinalizarReparacion);
                            } else {
                                setGraphic(null); // Oculta el botón en otras filas
                            }

                        }
                    }

                }; //Fin del return

            } //Fin de la configuraion de la celda

        });//Fin de la configuracion de la columna

        colAccionesImpri.setCellFactory(columna ->{
            return new TableCell<Reparacion, Void>(){

                private final Button btnImprimirReparacionEnCurso = new Button("Impr. Reparacion_En_Curso");

                {
                    btnImprimirReparacionEnCurso.setOnAction(event -> {

                        //Para obtener datos del registro a imprimir
                        Reparacion obtnerInfoReparacion = getTableView().getItems().get(getIndex());

                        SimpleDateFormat fechaReparacion = new SimpleDateFormat("dd-MM-yyyy");
                        String fechaReparacionFactura = fechaReparacion.format(new java.util.Date());
                        Random rand = new Random();
                        int numeroAleatorio = rand.nextInt(10000);

                        //Se Crea el nombre del archvio y se guarda en el directorio que deseamos
                        String nombreReparacionFactura = "ReparacionEnCurso/Rep-En_Curso_" + fechaReparacionFactura +"_"+obtnerInfoReparacion.getCliente()+"_" + numeroAleatorio + ".pdf";

                        try {

                            //Se Crea el docuemnto pdf de la factura de reparaciones
                            Document documentoPdf = new Document();
                            FileOutputStream archivoPDF = new FileOutputStream(nombreReparacionFactura);
                            PdfWriter.getInstance(documentoPdf, archivoPDF);

                            documentoPdf.open();

                            // Define la fuente y el tamaño de la letra para el encabezado
                            Font fuenteEncabezado = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

                            // Agrega el encabezado centrado
                            Paragraph encabezado = new Paragraph("Reparaciones Keilly", fuenteEncabezado);
                            encabezado.setAlignment(Paragraph.ALIGN_CENTER);
                            documentoPdf.add(encabezado);
                            documentoPdf.add(new Paragraph("\n"));

                            //Segundo encaabezado de como subtitulo indicando el tirulo del informe
                            Paragraph encabezadoSecundario = new Paragraph("Comprobante de Reparacion En Curso", FontFactory.getFont(FontFactory.HELVETICA_BOLD,15));
                            encabezadoSecundario.setAlignment(Element.ALIGN_CENTER);
                            documentoPdf.add(encabezadoSecundario);

                            //Salto de linea
                            documentoPdf.add(new Paragraph("\n"));

                            SimpleDateFormat fechaYhora = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                            String fechaDeLaReparacion = fechaYhora.format(new java.util.Date());

                            //Se agrega la fecha y la hora al doccumento
                            Paragraph fechaAgregar = new Paragraph("Fecha y hora de emisión: "+ fechaDeLaReparacion);
                            documentoPdf.add(fechaAgregar);

                            //Salto de linea
                            documentoPdf.add(new Paragraph("\n"));

                            // Agrega el nombre del cliente, teléfono y DUI
                            documentoPdf.add(new Paragraph("Datos Generales"));
                            documentoPdf.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------------"));
                            documentoPdf.add(new Paragraph("Nombre del Cliente: " + obtnerInfoReparacion.getCliente()));
                            documentoPdf.add(new Paragraph("Teléfono: " + obtnerInfoReparacion.getNumTelefono()));
                            documentoPdf.add(new Paragraph("Fecha de inicio de reparacion: " + obtnerInfoReparacion.getFechaInicio()));
                            documentoPdf.add(new Paragraph("\n"));
                            documentoPdf.add(new Paragraph("\n"));

                            documentoPdf.add(new Paragraph("Servicio brindado: "+obtnerInfoReparacion.getServicio()));
                            documentoPdf.add(new Paragraph("Equipo reparado: "+obtnerInfoReparacion.getEquipo()));
                            documentoPdf.add(new Paragraph("Precio del servicio: $ "+obtnerInfoReparacion.getPrecioUnitario()));
                            documentoPdf.add(new Paragraph("Detalles: "+obtnerInfoReparacion.getDetalles()));
                            documentoPdf.add(new Paragraph("\n"));
                            documentoPdf.add(new Paragraph("\n"));

                            documentoPdf.add(new Paragraph("Costos Adicionales:"));
                            if (obtnerInfoReparacion.getDetalleCostAdicional() == null) {
                                documentoPdf.add(new Paragraph("Detalle de costos adicionales: No se han registrado"));
                            } else {
                                documentoPdf.add(new Paragraph("Detalle de costos adicionales: " + obtnerInfoReparacion.getDetalleCostAdicional()));
                            }
                            documentoPdf.add(new Paragraph("Monto de costos adicionales: $ "+obtnerInfoReparacion.getMontoCostAdicional()));
                            documentoPdf.add(new Paragraph("\n"));
                            documentoPdf.add(new Paragraph("\n"));

                            // Agrega un mensaje de agradecimiento
                            documentoPdf.add(new Paragraph("Gracias por su preferencia"));

                            documentoPdf.close();

                            Alert alertaConfimar = new Alert(Alert.AlertType.INFORMATION);
                            alertaConfimar.setTitle("Confirmacion");
                            alertaConfimar.setContentText("SE HA CREADO EL COMPROBANTE");
                            alertaConfimar.showAndWait();

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }); //Fin de la fucnion del boton

                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {

                        Reparacion obtenerDatosDelRegistro = getTableView().getItems().get(getIndex());

                        //Verofocacion de los estados para que aparezca el boton correspondiente
                        if (obtenerDatosDelRegistro.getEstado().equals("En_Curso")) //Para iniciar la reparacion
                        {
                            HBox buttonsContainer = new HBox(btnImprimirReparacionEnCurso);
                            setGraphic(buttonsContainer);
                        }

                    }
                }

            };
        });

        cargarReparaciones();

        btnAgregarReparacion.setOnAction(actionEvent -> agrgarReparacion());

    }//Fin de la funcion de inicializacion


    private void cargarReparaciones() {

        List<Reparacion> listaReparaciones = new ArrayList<>();

        conexion = DatabaseConnection.getConnection();

        //consulta a la base de datos para traer la informacion
        String consultaReparaciones = "SELECT r.idReparaciones AS IdReparacion, s.nombreServicio AS NombreServicio, r.precioUnit AS precioUnitario, r.fechaInicio AS FechaInicio,"+
                "r.cliente AS Cliente, r.telefono AS Telefono, r.correo AS Correo, r.equipo AS Equipo, r.detalles AS Detalles, r.estado AS Estado, r.estadoPago AS EstadoPago,"+
                "r.detalleCostAdicionales AS DetalleCostAdicionales, r.montoCostAdicionales AS MontoCostAdicionales "+
                "FROM tbl_reparaciones r INNER JOIN tbl_servicios s ON r.idServicio = s.idServicio;";

        try {
            listaReparaciones.clear();
            estado = conexion.createStatement();
            resultado = estado.executeQuery(consultaReparaciones);

            //Se recorre el resultado si existen los datos
            while (resultado.next())
            {
                int idReparacion = resultado.getInt("IdReparacion");
                String servicio = resultado.getString("NombreServicio");
                double precioUnitario = resultado.getDouble("precioUnitario");
                String fechaIncio = resultado.getString("FechaInicio");
                String cliente = resultado.getString("Cliente");
                String telefono = resultado.getString("Telefono");
                String equipo = resultado.getString("Equipo");
                String detalles = resultado.getString("Detalles");
                String estado = resultado.getString("Estado");
                String estadoPago = resultado.getString("EstadoPago");
                String detalleCost = resultado.getString("DetalleCostAdicionales");
                double montoCostA = resultado.getDouble("MontoCostAdicionales");

                //Crear objeto para pasarlo a la tabla
                Reparacion reparacion = new Reparacion(idReparacion,servicio,precioUnitario,fechaIncio,cliente,telefono,equipo,detalles,estado,estadoPago,detalleCost,montoCostA);
                listaReparaciones.add(reparacion);

            }


        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        ObservableList<Reparacion> listaObservableReparaciones = FXCollections.observableArrayList(listaReparaciones);
        tablaReparaciones.setItems(listaObservableReparaciones);
    }

    private void agrgarReparacion() {

        Stage stage = new Stage();

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formReparacion.fxml"));
            Parent root = loader.load();

            stage.setTitle("Registro Reparacion");
            stage.setScene(new Scene(root, 600, 400));
            // Mostrar el escenario (ventana) con el formulario
            stage.show();

            //Llenado del combo con los servicos que se ofrecen
            conexion = DatabaseConnection.getConnection();

            ComboBox listaServicios = (ComboBox) root.lookup("#comboReparacionServicio");
            ObservableList<String> opciones = FXCollections.observableArrayList();

            try {
                String sql = "SELECT nombreServicio FROM tbl_servicios";
                estado = conexion.createStatement();
                resultado = estado.executeQuery(sql);
                while (resultado.next())
                {
                    String nombreServicoDeLaTabla = resultado.getString("nombreServicio");
                    opciones.add(nombreServicoDeLaTabla);
                }

            }catch (SQLException e)
            {
                e.printStackTrace();
            }

            listaServicios.setItems(opciones);

            DatePicker fechaRegistro = (DatePicker) root.lookup("#calendarioReparacionFecha");

            TextField campoCliente = (TextField) root.lookup("#campoReparacionCliente");
            TextField campoTelefono = (TextField) root.lookup("#campoReparacionTelefono");
            TextField campoCorreo = (TextField) root.lookup("#campoReparacionCorreo");
            TextField campoEquipo = (TextField) root.lookup("#campoRaparacionEquipo");
            TextField campoDetalle = (TextField) root.lookup("#campoReparacionDetalle");
            TextField campoPrecioUnit = (TextField) root.lookup("#campoReparacionPrecioUnit");

            Button btnGuardarReparacion = (Button) root.lookup("#btnReparacionGuardar");
            Button btnCancelarRaparacion = (Button) root.lookup("#btnReparacionCancelar");


            String estadoReparacion = "En espera";
            String estadoDePago = "No cancelado";

            btnGuardarReparacion.setOnAction(actionEvent -> {

                // Crear una sentencia SQL de inserción para insertar a la tabla tbl_reparaciones
                String insertarDatosDeRaparaciones = "INSERT INTO tbl_reparaciones (idServicio, precioUnit, fechaInicio, cliente, telefono, correo, equipo, detalles, estado, estadoPago) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


                try {
                    PreparedStatement preparedStatement = conexion.prepareStatement(insertarDatosDeRaparaciones);


                    // Obtener los valores desde tus controles
                    int insertIdServicio = listaServicios.getSelectionModel().getSelectedIndex(); // Obtener el índice del servicio seleccionado
                    LocalDate insertFechaInicio = fechaRegistro.getValue();
                    String insertCliente = campoCliente.getText().trim();
                    String insertTelefono = campoTelefono.getText().trim();
                    String insertCorreo = campoCorreo.getText().trim();
                    String insertEquipo = campoEquipo.getText().trim();
                    String insertDetalles = campoDetalle.getText().trim();

                    double insertPrecioUnit = Double.valueOf(campoPrecioUnit.getText().toString().trim());

                    // Establecer los parámetros en la sentencia SQL para tbl_reparaciones
                    preparedStatement.setInt(1,(insertIdServicio+1));
                    preparedStatement.setDouble(2,insertPrecioUnit);
                    preparedStatement.setDate(3, java.sql.Date.valueOf(insertFechaInicio));
                    preparedStatement.setString(4, insertCliente);
                    preparedStatement.setString(5, insertTelefono);
                    preparedStatement.setString(6, insertCorreo);
                    preparedStatement.setString(7, insertEquipo);
                    preparedStatement.setString(8, insertDetalles);
                    preparedStatement.setString(9, estadoReparacion);
                    preparedStatement.setString(10, estadoDePago);

                    // Ejecutar la inserción
                    int filasAfectadas = preparedStatement.executeUpdate();

                    if (filasAfectadas > 0) {
                        System.out.println("Reparación registrada exitosamente.");
                    } else {
                        System.err.println("Error al registrar la reparación.");
                    }

                    stage.close();

                } catch (SQLException e)
                {
                    e.printStackTrace();

                }//Fin de la funcion de insertar a la tabla tbl_reparaciones

            }); //Fin del boton para registrar los datodos de la venta

            btnCancelarRaparacion.setOnAction(actionEvent -> {
                stage.close();
            });



        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}