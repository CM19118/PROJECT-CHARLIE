package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Carrito;

import com.example.projectgui.Models.ModeloCompra;
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
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CarritoController {

    public  List<ModeloCompra> listaProcuctosAComprar = new ArrayList<>();

    public Button btnFinalizarCompra;

    //------------ Varibles para la conexion a la base de datos ------------------------
    private Connection conexion;
    private Statement estado;
    private ResultSet resultado;


    //------ Campos para la tabla temporal donde se agregan los productos a comprar ---------------
    @FXML
    public TableView<ModeloCompra> tablaTemporalCarrito;
    @FXML
    public TableColumn<ModeloCompra, String> col_nombreProducto;
    @FXML
    public TableColumn<ModeloCompra, Integer> col_CantidadComprar;
    @FXML
    public TableColumn<ModeloCompra, Double> col_PrecioUnitario;
    @FXML
    public TableColumn<ModeloCompra, Double> col_total;
    @FXML
    public TableColumn<ModeloCompra, Void> col_accion;


    // ---------- Campos para la tala donde del inventario, es decir los productos disponibles para la venta ---------
    @FXML
    public TableView<Carrito> tablaProductosDisponibles;
    @FXML
    public TableColumn<Carrito, Integer> columIdProducto;
    @FXML
    public TableColumn<Carrito, String> columNombreProducto;
    @FXML
    public TableColumn<Carrito, Integer> columCantidad;
    @FXML
    public TableColumn<Carrito, Double> columPrecio;
    @FXML
    public TableColumn<Carrito, Void> columAcciones;

    public void initialize()
    {
       //------- Confifuracion de los campos de la tabla para mostrar los productos disponibles para la venta
        columIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        columNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        columCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        columAcciones.setCellFactory(new Callback<TableColumn<Carrito, Void>, TableCell<Carrito, Void>>() {
            @Override
            public TableCell<Carrito, Void> call(TableColumn<Carrito, Void> param) {
                return new TableCell<>() {
                    private final Button btnAgregarAlCarrito = new Button("Agregar al Carrito");
                    {
                        // Configurar la acción del botón
                        btnAgregarAlCarrito.setOnAction(event -> {

                            //Para acceder a la fila del producto seleccionado
                            Carrito carrito = getTableView().getItems().get(getIndex());

                            TextInputDialog dialog = new TextInputDialog("1");
                            dialog.setTitle("Agregar al carrito");
                            dialog.setHeaderText("Ingrese la cantidad:");
                            dialog.setContentText("Cantidad:");

                            // Obtener el resultado del diálogo
                            AtomicReference<Optional<String>> resultado = new AtomicReference<>(dialog.showAndWait());

                            //Las opciones de aceptar o cancelar
                            resultado.get().ifPresent(cantidad -> {
                                try {

                                    //Se obtine la cantidad que desea comprar el cliente
                                    int cantidadDelCliente = Integer.parseInt(cantidad);

                                    //Se obtiene cuanto hay disponible en nuestro inventario
                                    int stockDisponible = carrito.getCantidad();

                                    if (cantidadDelCliente > stockDisponible)
                                    {
                                        //Alerta si no hay suficiente inventario
                                        Alert alertaNoHayInventario = new Alert(Alert.AlertType.INFORMATION);
                                        alertaNoHayInventario.setTitle("No hay suficiente inventario");
                                        alertaNoHayInventario.setHeaderText(null);
                                        alertaNoHayInventario.setContentText("No hay suficiente inventario disponible para esta cantidad.");
                                        alertaNoHayInventario.showAndWait();
                                    }
                                    else {

                                        //Se obtienen los datos para pasarlos a la tabla temporal
                                        String nombreProducto = carrito.getNombreProducto();
                                        double precioProducto = carrito.getPrecio();
                                        double total = cantidadDelCliente * precioProducto;

                                        //Obejeto con los datos del producto que el cliente quiero comprar
                                        ModeloCompra prodcutoA_Agregar = new ModeloCompra(nombreProducto,cantidadDelCliente,precioProducto,total);
                                        listaProcuctosAComprar.add(prodcutoA_Agregar);

                                        cargarEnTablaTemporal(); // se llama a esta funcion de cargar la tabal temporal

                                        //Actulizacion del inventario. Se actuliza la tabla de productos asiganando el nuevo inventario
                                        int idActulizar = carrito.getIdProducto(); //id del producto a actulizar
                                        int nuevaCantidadInventario = stockDisponible - cantidadDelCliente; //nueva cantidad a actulizar

                                        //Se hace la consulta a la base de datos
                                        conexion = DatabaseConnection.getConnection();
                                        String consultaActulizarInventario = "UPDATE tbl_productos SET cantidad = ? WHERE idProducto = ?";
                                        PreparedStatement preparacionDeConsulta = conexion.prepareStatement(consultaActulizarInventario);
                                        preparacionDeConsulta.setInt(1, nuevaCantidadInventario);
                                        preparacionDeConsulta.setInt(2, idActulizar);

                                        tablaProductosDisponibles.refresh();
                                        tablaProductosDisponibles.refresh();
                                        tablaProductosDisponibles.requestFocus();

                                        // Ejecuta la actualización
                                        int filasActualizadas = preparacionDeConsulta.executeUpdate();


                                        if (filasActualizadas > 0) {
                                            System.out.println("Inventario actualizado exitosamente.");
                                        } else {
                                            System.out.println("No se encontró un producto con el ID especificado.");
                                        }

                                    }


                                }catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnAgregarAlCarrito);
                        }
                    }

                };//Fin del return
            }//Fin de otra funcion
        }); //Fin de la configuracion de la columna

        tablaTemporalCarrito.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        cargarDatosTablaCarrito();

        //Se hace la llamada a la funcion de finalizar la compra
        btnFinalizarCompra.setOnAction(actionEvent -> finalizarCompra());

    }//Fin de la funcion initialize

    private void cargarDatosTablaCarrito() {

        // Se crea un arrayList para almacenar los datos obtenidos
        List<Carrito> listaCarrito = new ArrayList<>();

        conexion = DatabaseConnection.getConnection();
        //Consulta a la base de datos
        String consulta = "SELECT idProducto, nombreProducto, cantidad, precio FROM tbl_productos;";

        //Bloque try para manejar las excepciones
        try {
            listaCarrito.clear();
            estado = conexion.createStatement();
            resultado = estado.executeQuery(consulta);

            //Se recorre la tabla
            while (resultado.next())
            {
                int idProducto = resultado.getInt("idProducto");
                String nombreProducto = resultado.getString("nombreProducto");
                int cantidad = resultado.getInt("cantidad");
                double precio = resultado.getDouble("precio");

                Carrito carrito = new Carrito(idProducto, nombreProducto, cantidad, precio);
                listaCarrito.add(carrito);
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        //Convertir la lista en una lista observable
        ObservableList<Carrito> listaCarritoObservable = FXCollections.observableArrayList(listaCarrito);

        //Se establce la lista observable como el modelo de datos para la tabla
        tablaProductosDisponibles.setItems(listaCarritoObservable);

    }

    private void cargarEnTablaTemporal() {
        col_nombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        col_CantidadComprar.setCellValueFactory(new PropertyValueFactory<>("cantidadAComprar"));
        col_PrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        col_total.setCellValueFactory(new PropertyValueFactory<>("total"));

        //---------------------- Agregar el boton de eliminar del carrito ---------------------------------------------
        col_accion.setCellFactory(new Callback<TableColumn<ModeloCompra, Void>, TableCell<ModeloCompra, Void>>() {
            @Override
            public TableCell<ModeloCompra, Void> call(TableColumn<ModeloCompra, Void> param) {
                return new TableCell<>() {

                    private final Button btnElimnarDeCarrito = new Button("Eliminar");

                    //Llave donde se implenetan los eventos de los botones
                    {
                        btnElimnarDeCarrito.setOnAction(actionEvent -> {

                            //Alerta para eliminar el producto
                            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmacion.setTitle("Confirmacion");
                            confirmacion.setHeaderText("¿Estás seguro que deseas quitar de la lista de compra?");

                            // Se perzonalizan lo botones
                            ButtonType botonAceptar = new ButtonType("Aceptar");
                            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());
                            confirmacion.getButtonTypes().setAll(botonAceptar, botonCancelar);

                            Optional<ButtonType> resultado = confirmacion.showAndWait();

                            if (resultado.isPresent() && resultado.get() == botonAceptar) {

                                TableRow<ModeloCompra> row = (TableRow<ModeloCompra>) btnElimnarDeCarrito.getParent().getParent();

                                if (row != null) {

                                    // Se obtine el elemento a eliminar
                                    ModeloCompra productoEliminar = row.getItem();

                                    if (productoEliminar != null) {
                                        // Elimina el elemento de la lista
                                        listaProcuctosAComprar.remove(productoEliminar);
                                        // Actualiza la tabla para reflejar los cambios
                                        tablaTemporalCarrito.getItems().remove(productoEliminar);

                                        //Parte para devolver los datos a la tabla de inventario para es decir volver actulizar la tabla

                                        String nombreProductoActulizar = productoEliminar.getNombreProducto(); //Para el nombre del producto al cual se le va a delvolver las existencias
                                        int cantidadDevolver = productoEliminar.getCantidadAComprar();

                                        conexion = DatabaseConnection.getConnection();
                                        String consultaDevolverExistencias = "UPDATE tbl_productos SET cantidad = cantidad + ? WHERE nombreProducto = ?";
                                        try {
                                            PreparedStatement preparacioDevolverInv = conexion.prepareStatement(consultaDevolverExistencias);
                                            preparacioDevolverInv.setInt(1, cantidadDevolver);
                                            preparacioDevolverInv.setString(2, nombreProductoActulizar);


                                            //Para verificar que si se ha actulizado la tabla
                                            int actulizacionCorrecta = preparacioDevolverInv.executeUpdate();


                                            if (actulizacionCorrecta > 0)
                                            {
                                                System.out.println("Se ha devuelto las exstencaias revisar SQL");
                                            }
                                            else
                                            {
                                                System.out.println("Fracasamos !!!!");
                                            }



                                        }catch (SQLException e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                }

                            }

                        }); //Fin del evento eliminar porducto del carrito

                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnElimnarDeCarrito);
                        }
                    }

                }; //Fin del return
            }
        });


        // Configura la lista observable y asigna a la tabla
        ObservableList<ModeloCompra> listaObser = FXCollections.observableArrayList(listaProcuctosAComprar);
        tablaTemporalCarrito.setItems(listaObser);
    }

    private void finalizarCompra()
    {
        Stage stage = new Stage();

        try {
            // Cargar el archivo FXML que contiene el formulario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formCliente.fxml"));
            Parent root = loader.load();

            // Configurar el escenario con el contenido del formulario
            stage.setTitle("Formulario de Cliente");
            stage.setScene(new Scene(root, 600, 400));
            // Mostrar el escenario (ventana) con el formulario
            stage.show();

            /*
            Una vez abierto el fomulario se procede a obtner los datos para guardarlos en
            la base datos, en la tabla ventas
             */

            // Variables para almacenar la información
            List<String> nombresProductos = new ArrayList<>();

            //Estas dos varibles sirven para captar los nuemeros de la tabla temporal
            int cantidadTotalProductos1 = 0;
            double precioTotalGastado1 = 0.0;

            TextField campoNombreCliente = (TextField) root.lookup("#campoNombreCliente");
            TextField campoTelefonoCliente = (TextField) root.lookup("#campoTelefonoCliente");
            TextField campoDuiCliente = (TextField) root.lookup("#campoDuiCliente");


            // Recorre la lista de productos en la tabla
            for (ModeloCompra producto : listaProcuctosAComprar) {
                nombresProductos.add(producto.getNombreProducto()); // Agrega el nombre del producto
                cantidadTotalProductos1 += producto.getCantidadAComprar(); // Suma la cantidad
                precioTotalGastado1 += producto.getTotal(); // Suma el precio total gastado
            }

            String nombreProductosString = String.join(", ", nombresProductos);

            // Obtener referencias a los botones del formulario
            Button btnGuardarDatosCliente = (Button) root.lookup("#btnGuardarDatosCliente");
            Button btnCancelarDatosCliente = (Button) root.lookup("#btnCancelarDatosCliente");

            //Varibles de tipo final para poder meterlas al evento del boton
            final int cantidadTotalProductos2 = cantidadTotalProductos1;
            final double precioTotalGastado2 = precioTotalGastado1;


            // ------------- Configurar acciones de los botones -------------------------
            btnGuardarDatosCliente.setOnAction(actionEvent -> {
                //La fecha de la venta
                // Obtener la fecha actual utilizando Calendar
                Calendar calendario = Calendar.getInstance();
                java.util.Date fechaActual = calendario.getTime();

                // Convertir la fecha actual a java.sql.Date
                java.sql.Date fechaDeLaVenta = new java.sql.Date(fechaActual.getTime());

                conexion = DatabaseConnection.getConnection();
                String insersionVenta = "INSERT INTO tbl_ventas (productos, nombreCliente, fechaVenta, telefonoCliente, cantidadProducto, total, duiCliente) VALUES (?, ?, ?, ?, ?, ?, ?)";

                try {
                    //se hace una consulta preparada
                    PreparedStatement consultaPreparada = conexion.prepareStatement(insersionVenta);

                    //Se establecesn los valores

                    consultaPreparada.setString(1,nombreProductosString);
                    consultaPreparada.setString(2, campoNombreCliente.getText().trim());
                    consultaPreparada.setDate(3, fechaDeLaVenta);
                    consultaPreparada.setString(4, campoTelefonoCliente.getText().trim());
                    consultaPreparada.setInt(5, cantidadTotalProductos2);
                    consultaPreparada.setDouble(6, precioTotalGastado2);
                    consultaPreparada.setString(7, campoDuiCliente.getText().trim());

                    consultaPreparada.executeUpdate();

                    //----------- Para ingresar la factura -----------------------------

                    int idVentaInsertada = -1; // Inicializa con un valor predeterminado

                    String consultaUltimoID = "SELECT LAST_INSERT_ID() AS last_id";
                    try (PreparedStatement consultaID = conexion.prepareStatement(consultaUltimoID);
                         ResultSet resultado = consultaID.executeQuery()) {
                        if (resultado.next()) {
                            idVentaInsertada = resultado.getInt("last_id");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    String insersionFactura = "INSERT INTO tbl_facturas (idVenta, fechaFactura, montoTotal) VALUES (?, ?, ?)";

                    try {
                        PreparedStatement consultaFactura = conexion.prepareStatement(insersionFactura);
                        consultaFactura.setInt(1, idVentaInsertada); // Aquí utilizas el ID de la venta insertada
                        consultaFactura.setDate(2, fechaDeLaVenta); // La fecha de la factura
                        consultaFactura.setDouble(3, precioTotalGastado2); // El monto total de la factura
                        consultaFactura.executeUpdate();


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    conexion.close();

                    //---------- Generar el archivo de la factura ------------------------

                    //Se le da fromato a la fecha dia-mes-año
                    SimpleDateFormat formatoFecha1 = new SimpleDateFormat("dd-MM-yyyy");
                    String fechaParaNombreFactura = formatoFecha1.format(new Date());

                    Random rand = new Random();
                    int numeroAleatorio = rand.nextInt(10000);

                    //Se Crea el nombre del archvio y se guarda en el directorio que deseamos
                    String nombreArchivo = "Facturas/Factura_" + fechaParaNombreFactura +"_"+campoNombreCliente.getText().trim()+ "_" + numeroAleatorio + ".pdf";

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

                        // Agrega la fecha
                        //Determinamos la fecha y hora
                        SimpleDateFormat formatoFechaHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String fechaHoraParaFactura = formatoFechaHora.format(new Date());
                        Paragraph fechaParagraph = new Paragraph("Fecha: " + fechaHoraParaFactura);
                        fechaParagraph.setAlignment(Element.ALIGN_RIGHT);
                        documento.add(fechaParagraph);
                        // Salto de línea
                        documento.add(new Paragraph("\n"));
                        documento.add(new Paragraph("\n"));

                        // Agrega el nombre del cliente, teléfono y DUI
                        documento.add(new Paragraph("Datos del cliente"));
                        documento.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------------"));
                        documento.add(new Paragraph("Nombre del Cliente: " + campoNombreCliente.getText().trim()));
                        documento.add(new Paragraph("Teléfono: " + campoTelefonoCliente.getText().trim()));
                        documento.add(new Paragraph("DUI del Cliente: " + campoDuiCliente.getText().trim()));
                        documento.add(new Paragraph("\n"));
                        documento.add(new Paragraph("\n"));

                        // Agrega los productos, cantidad total, precio unitario y total gastado
                        documento.add(new Paragraph("Productos"));
                        documento.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------------"));
                        for (ModeloCompra producto : listaProcuctosAComprar) {
                            String detalleProducto = producto.getNombreProducto() + " - Cantidad: " + producto.getCantidadAComprar() +
                                    " - Precio Unitario: $" + producto.getPrecioUnitario() + " - Total: $" + producto.getTotal();
                            documento.add(new Paragraph(detalleProducto));
                        }

                        documento.add(new Paragraph("\n"));
                        documento.add(new Paragraph("Cantidad Total de Productos: " + cantidadTotalProductos2));
                        documento.add(new Paragraph("Total Gastado: $" + precioTotalGastado2));
                        documento.add(new Paragraph("\n"));
                        documento.add(new Paragraph("\n"));

                        // Agrega un mensaje de agradecimiento
                        documento.add(new Paragraph("Gracias por su compra"));

                        // Cierra el documento
                        documento.close();

                        System.out.println("Factura generada con éxito.");
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }catch (SQLException e)
                {
                    e.printStackTrace();
                }

            });

            btnCancelarDatosCliente.setOnAction(actionEvent -> {
                //para cerrar el form
                Stage cerrarFormDesdeBtnCerrar = (Stage) btnCancelarDatosCliente.getScene().getWindow();
                cerrarFormDesdeBtnCerrar.close();

            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
