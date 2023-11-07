package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Garantia;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GarantiaController {

    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos

    //------------------ Relacionamos lo elementos del fxml con el controlador ---------------------
    @FXML
    public ComboBox<String> comboEstadoGarantias;

    @FXML
    public TableView<Garantia> tablaGarantias;
    @FXML
    public TableColumn<Garantia, Integer> colIdGarantia;
    @FXML
    public TableColumn<Garantia, String> colFechaGarantia;
    @FXML
    public TableColumn<Garantia, String> colCliente;
    @FXML
    public TableColumn<Garantia, String> colEquipo;
    @FXML
    public TableColumn<Garantia, String> colDetalles;
    @FXML
    public TableColumn<Garantia, Double> colMonto;
    @FXML
    public TableColumn<Garantia, String> colFechaVencimiento;
    @FXML
    public TableColumn<Garantia, String> colEstadoGarantia;
    @FXML
    public TableColumn<Garantia, Void> colAcciones;

    List<Garantia> listaGarantias = new ArrayList<>();

    public void initialize()
    {
        colIdGarantia.setCellValueFactory(new PropertyValueFactory<>("idGarantia"));
        colFechaGarantia.setCellValueFactory(new PropertyValueFactory<>("fechaGarantia"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colEquipo.setCellValueFactory(new PropertyValueFactory<>("equipo"));
        colDetalles.setCellValueFactory(new PropertyValueFactory<>("detalles"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colFechaVencimiento.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
        colEstadoGarantia.setCellValueFactory(new PropertyValueFactory<>("estadoGrantia"));

        colAcciones.setCellFactory(column -> {
            return new TableCell<Garantia, Void>() {
                private final Button btnAplicarGarantia = new Button("Aplicar Garanatia");
                private final Button btnTerminarGarantia = new Button("Terminar Garantia");

                {
                    btnAplicarGarantia.setOnAction(event -> {
                        // Lógica para editar el elemento
                        Garantia selectedItem = getTableView().getItems().get(getIndex());

                        conexion = DatabaseConnection.getConnection();
                        String estadoGarantiaParametro = "En_Curso";
                        String consultaActulizarEstadoGarantia = "UPDATE tbl_facturareparaciones SET estadoGarantia = ? WHERE idFactura = ?";

                        try {

                            // Crear una PreparedStatement para la consulta
                            PreparedStatement preparedStatement = conexion.prepareStatement(consultaActulizarEstadoGarantia);

                            // Establecer los valores de los parámetros
                            preparedStatement.setString(1, estadoGarantiaParametro);
                            preparedStatement.setInt(2, selectedItem.getIdGarantia()); // Suponiendo que tengas un método getIdFactura en la clase Garantia

                            // Ejecutar la actualización
                            int filasActualizadas = preparedStatement.executeUpdate();

                            if (filasActualizadas > 0) {
                                cargarRegistroDeGarantias();
                            } else {
                                System.out.println("No se encontraron filas para actualizar.");
                            }

                        }catch (SQLException e)
                        {
                            e.printStackTrace();
                        }

                    });

                    btnTerminarGarantia.setOnAction(event -> {

                        // Lógica para editar el elemento
                        Garantia selectedItem = getTableView().getItems().get(getIndex());

                        conexion = DatabaseConnection.getConnection();
                        String estadoGarantiaParametro = "Aplicada";
                        String consultaActulizarEstadoGarantia = "UPDATE tbl_facturareparaciones SET estadoGarantia = ? WHERE idFactura = ?";

                        try {

                            // Crear una PreparedStatement para la consulta
                            PreparedStatement preparedStatement = conexion.prepareStatement(consultaActulizarEstadoGarantia);

                            // Establecer los valores de los parámetros
                            preparedStatement.setString(1, estadoGarantiaParametro);
                            preparedStatement.setInt(2, selectedItem.getIdGarantia()); // Suponiendo que tengas un método getIdFactura en la clase Garantia

                            // Ejecutar la actualización
                            int filasActualizadas = preparedStatement.executeUpdate();

                            if (filasActualizadas > 0) {
                                cargarRegistroDeGarantias();
                            } else {
                                System.out.println("No se encontraron filas para actualizar.");
                            }

                        }catch (SQLException e)
                        {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        Garantia estadoGarantiaParaElBoton =getTableView().getItems().get(getIndex());

                        //Verificar el estado del registro para aplicar el boton
                        if (estadoGarantiaParaElBoton.getEstadoGrantia().equals("Vigente"))
                        {
                            HBox buttonsContainer = new HBox(btnAplicarGarantia);
                            setGraphic(buttonsContainer);
                        }
                        if (estadoGarantiaParaElBoton.getEstadoGrantia().equals("En_Curso"))
                        {
                            HBox buttonsContainer = new HBox(btnTerminarGarantia);
                            setGraphic(buttonsContainer);
                        }
                    }
                }
            };
        });

        //Para el comboBox
        ObservableList<String> opciones = FXCollections.observableArrayList("Vigente", "En_Curso", "Aplicada", "Vencida");
        comboEstadoGarantias.setItems(opciones);

        comboEstadoGarantias.setOnAction(event -> filtrarTablaPorEstado());

        //Para verificar si ya ha vencido una garantia
        verificarVencimientoGarantia();

        //Se carga el registro de las garantrias a aplicar
        cargarRegistroDeGarantias();

    }

    private void filtrarTablaPorEstado() {
        String estadoSeleccionado = comboEstadoGarantias.getValue();

        // Verifica si se ha seleccionado un estado
        if (estadoSeleccionado != null) {
            List<Garantia> garantiasFiltradas = new ArrayList<>();

            for (Garantia garantia : listaGarantias) {
                if (garantia.getEstadoGrantia().equals(estadoSeleccionado)) {
                    garantiasFiltradas.add(garantia);
                }
            }
            // Crea una nueva lista observable con los elementos filtrados y actualiza la tabla
            ObservableList<Garantia> datosFiltrados = FXCollections.observableArrayList(garantiasFiltradas);
            tablaGarantias.setItems(datosFiltrados);
        }
    }

    private void cargarRegistroDeGarantias() {

        conexion = DatabaseConnection.getConnection();
        String consultaGarantias = "SELECT fr.idFactura, fr.fechaFactura, r.cliente, r.equipo, r.detalles, fr.monto, fr.fechaVencimiento, fr.estadoGarantia FROM tbl_facturareparaciones fr JOIN tbl_reparaciones r ON fr.idReparacion = r.idReparaciones;";

        try {
            listaGarantias.clear();
            estado = conexion.createStatement();
            resultado = estado.executeQuery(consultaGarantias);

            while (resultado.next())
            {
                int idGarantia = resultado.getInt("idFactura");
                String fechaInicioGarantia = resultado.getString("fechaFactura");
                String clienteGarantia = resultado.getString("cliente");
                String equipoGarantia = resultado.getString("equipo");
                String detalleEquipoGaratia = resultado.getString("detalles");
                double montoGarantia = resultado.getDouble("monto");
                String fechaVencimientoGarantia = resultado.getString("fechaVencimiento");
                String estadoGarantia = resultado.getString("estadoGarantia");

                Garantia garantia = new Garantia(idGarantia,fechaInicioGarantia,clienteGarantia,equipoGarantia,detalleEquipoGaratia,montoGarantia,fechaVencimientoGarantia,estadoGarantia);
                listaGarantias.add(garantia);

            }

            ObservableList<Garantia> listaObservableGarantia = FXCollections.observableArrayList(listaGarantias);
            tablaGarantias.setItems(listaObservableGarantia);

        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void verificarVencimientoGarantia()
    {
        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();

        // Conectar a la base de datos
        conexion = DatabaseConnection.getConnection();

        // Consulta para actualizar el estado de garantía vencida
        String consultaActualizarEstadoVencido = "UPDATE tbl_facturareparaciones SET estadoGarantia = 'Vencida' WHERE fechaVencimiento < ? AND estadoGarantia = 'Vigente'";

        try {
            // Crear una PreparedStatement para la consulta
            PreparedStatement preparedStatement = conexion.prepareStatement(consultaActualizarEstadoVencido);

            // Establecer la fecha actual como parámetro
            preparedStatement.setDate(1, Date.valueOf(fechaActual));

            // Ejecutar la actualización
            int filasActualizadas = preparedStatement.executeUpdate();

            if (filasActualizadas > 0) {
                System.out.println("Se actualizaron " + filasActualizadas + " garantías vencidas.");
                // Aquí puedes realizar cualquier otra acción necesaria
            } else {
                System.out.println("No se encontraron garantías vencidas para actualizar.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar la conexión y liberar recursos
            try {
                if (conexion != null) {
                    conexion.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




}

