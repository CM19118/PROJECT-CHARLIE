package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Empleado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class TblEmpleadoController implements Initializable {
    @FXML
    private TableView<Empleado> tablaEmpleado;
    @FXML
    private TableColumn<Empleado, Void> col_acciones;
    @FXML
    private TableColumn<Empleado, String> col_nombre;
    @FXML
    private TableColumn<Empleado, String> col_edad;
    @FXML
    private TableColumn<Empleado, String> col_dui;
    @FXML
    private TableColumn<Empleado, String> col_direccion;
    @FXML
    private TableColumn<Empleado, String> col_telefono;
    @FXML
    private TableColumn<Empleado, String> col_correo;
    @FXML
    private TableColumn<Empleado, String> col_cargo;
    @FXML
    private TableColumn<Empleado, String> col_estado;



    //INSTANCIAS PARA LA BASE DE DATOS
    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;
    //ventana Empleado

    private EmpleadoController Empleadocontroller;
    private ObservableList<Empleado> dataList = FXCollections.observableArrayList();
    @FXML
    private TextField fieldBarSearch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mostrarListaEmpleado();
    }

    public ObservableList<Empleado> listaEmpleado() {
        connect = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM tbl_empleados";
        try {
            dataList.clear();
            statement = connect.createStatement();
            result = statement.executeQuery(sql);
            Empleado Empleado;

            while (result.next()) {
                int idEmpleado = result.getInt("idEmpleado");
                String nombres = result.getString("nombres");
                String apellidos = result.getString("apellidos");
                int edad = result.getInt("edad");
                String dui = result.getString("dui");
                String direccion = result.getString("direccion");
                String telefono = result.getString("telefono");
                String correo = result.getString("correo");
                String cargo = result.getString("cargo");
                int estadoEmpleado = result.getInt("estadoEmpleado");
                int estadoUsuario = result.getInt("estadoUsuario");
                String nombreCompleto = nombres + " " + apellidos;


                Empleado = new Empleado(idEmpleado,nombres,apellidos,edad,dui,direccion,telefono,correo,cargo,estadoEmpleado,estadoUsuario, nombreCompleto);
                dataList.addAll(Empleado);
            }
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }


    public void mostrarListaEmpleado(){
        ObservableList<Empleado> mostrarEmpleado = listaEmpleado();

        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        col_edad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        col_dui.setCellValueFactory(new PropertyValueFactory<>("dui"));
        col_direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_correo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        col_cargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        col_estado.setCellValueFactory(new PropertyValueFactory<>("estadoEmpleado"));

        // En este apartado configuramos la columna de acciones para mostrar botones
        // Los botones los agregaremos con codigo, porque directamente con el FXML no se puede
        col_acciones.setCellFactory(new Callback<TableColumn<Empleado, Void>, TableCell<Empleado, Void>>() {
            @Override
            public TableCell<Empleado, Void> call(TableColumn<Empleado, Void> param) {
                return new TableCell<>() {
                    private final Button editarButton = new Button();
                    private final Button eliminarButton = new Button();
                    private final HBox box = new HBox(editarButton, eliminarButton);
                    {
                        ImageView editarIcono = new ImageView(new Image(getClass().getResourceAsStream("/icons/edit.png")));                        editarIcono.setFitHeight(16); // Establece el tamaño de la imagen
                        editarIcono.setFitWidth(16);
                        editarButton.setGraphic(editarIcono);
                        editarButton.setStyle("-fx-background-color: #4CAF50;"); // Un fondo de color verde para el botón de editar

                        ImageView eliminarIcono = new ImageView(new Image(getClass().getResourceAsStream("/icons/delete.png")));
                        eliminarIcono.setFitWidth(16);
                        eliminarIcono.setFitHeight(16);
                        eliminarButton.setGraphic(eliminarIcono);
                        eliminarButton.setStyle("-fx-background-color: #f44336;"); // Un fondo de color rojo para el botón de eliminar

                        editarButton.setOnAction(event -> {
                            Empleado Empleado2 = getTableView().getItems().get(getIndex());
                            Empleadocontroller = new EmpleadoController();
                            try {

                                Empleadocontroller.funcEditarEmpleado(Empleado2);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                        });

                        eliminarButton.setOnAction(event -> {
                            Empleado Empleado = getTableView().getItems().get(getIndex());

                            // Creamos un cuadro de diálogo para l confirmacion d3el user
                            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmacion.setTitle("Confirmación");
                            confirmacion.setHeaderText("¿Estás seguro que deseas eliminar este Empleado?");

                            // Personaliza los botones del cuadro de diálogo
                            ButtonType botonAceptar = new ButtonType("Aceptar");
                            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());
                            confirmacion.getButtonTypes().setAll(botonAceptar, botonCancelar);

                            Optional<ButtonType> resultado = confirmacion.showAndWait();

                            if (resultado.isPresent() && resultado.get() == botonAceptar) {
                                // El usuario ha confirmado la eliminación
                                try (Connection connection = DatabaseConnection.getConnection();
                                     PreparedStatement statement = connection.prepareStatement(
                                             "DELETE FROM tbl_empleados WHERE idEmpleado=?")) {

                                    statement.setInt(1, Empleado.getIdEmpleado());

                                    int filasAfectadas = statement.executeUpdate();

                                    if (filasAfectadas > 0) {
                                        // Eliminación exitosa, puedes actualizar la tabla
                                        dataList.remove(Empleado);
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    // Manejo de errores
                                }
                            }
                        });
                        // Configurar alineación horizontal y vertical del HBox
                        box.setAlignment(Pos.CENTER);
                        box.setSpacing(5); // Espaciado entre los botones
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(box);
                        }
                    }
                };
            }
        });

        tablaEmpleado.setItems(mostrarEmpleado);

        //SECCION DONDDE SE FILTRA LOS DATTOS DE LA TABLA
        FilteredList<Empleado> filteredList = new FilteredList<>(mostrarEmpleado, b -> true);
        fieldBarSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(Empleado -> {
                if(newValue.trim().isEmpty() || newValue.isBlank() || newValue == null){
                    return true;
                }
                String busqueda = newValue.toLowerCase();

                // Verificar si el nombre, cantidad o precio del Empleado contiene la cadena de búsqueda
                return Empleado.getApellidos().toLowerCase().contains(busqueda) ||
                        String.valueOf(Empleado.getDui()).toLowerCase().contains(busqueda) ||
                        String.valueOf(Empleado.getCargo()).toLowerCase().contains(busqueda);
            });
        });

        SortedList<Empleado> ListSorted = new SortedList<>(filteredList);
        //Enlazando resultado ordenado con vista de tabla
        ListSorted.comparatorProperty().bind(tablaEmpleado.comparatorProperty());
        //Aplicando datos filtrados y ordenados en la vista de tabla
        tablaEmpleado.setItems(ListSorted);
    }

    @FXML
    public void btnaddEmpleado(){
        EmpleadoController productControlador = new EmpleadoController();
        productControlador.aggEmpleado();
    }
}
