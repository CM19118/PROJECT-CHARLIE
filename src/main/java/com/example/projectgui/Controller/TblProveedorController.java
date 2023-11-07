package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Producto;
import com.example.projectgui.Models.Proveedor;
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

public class TblProveedorController implements Initializable {
    @FXML
    private TableView<Proveedor> tablaProveedor;
    @FXML
    private TableColumn<Proveedor, Void> col_acciones;

    @FXML
    private TableColumn<Proveedor, Integer> col_idProv;
    @FXML
    private TableColumn<Proveedor, String> col_nombre;
    @FXML
    private TableColumn<Proveedor, String> col_direccion;
    @FXML
    private TableColumn<Proveedor, String> col_correo;
    @FXML
    private TableColumn<Proveedor, String> col_tipo;
    @FXML
    private TableColumn<Proveedor, String> col_telefono;
    @FXML
    private TableColumn<Proveedor, String> col_observaciones;

    //INSTANCIAS PARA LA BASE DE DATOS
    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;
    //ventana proveedor
    private ProveedorController proveedorController;
    private ObservableList<Proveedor> dataList = FXCollections.observableArrayList();
    @FXML
    private TextField fieldBarSearch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mostrarListaProveedor();
    }

    public ObservableList<Proveedor> listaProveedor() {
        connect = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM tbl_proveedor";
        try {
            dataList.clear();
            statement = connect.createStatement();
            result = statement.executeQuery(sql);
            Proveedor proveedor;

            while (result.next()) {
                int idProveedor = result.getInt("idProveedor");
                String nombreProveedor = result.getString("nombreProveedor");
                String direccion = result.getString("direccion");
                String correo = result.getString("correo");
                String tipo = result.getString("tipo");
                String telefono = result.getString("telefono");
                String observaciones = result.getString("observaciones");

                proveedor = new Proveedor(idProveedor, nombreProveedor, direccion, correo, tipo, telefono, observaciones);
                dataList.addAll(proveedor);
            }
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }


    public void mostrarListaProveedor(){
        ObservableList<Proveedor> mostrarProveedor = listaProveedor();

        col_idProv.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        col_direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_correo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        col_tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_observaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        // En este apartado configuramos la columna de acciones para mostrar botones
        // Los botones los agregaremos con codigo, porque directamente con el FXML no se puede
        col_acciones.setCellFactory(new Callback<TableColumn<Proveedor, Void>, TableCell<Proveedor, Void>>() {
            @Override
            public TableCell<Proveedor, Void> call(TableColumn<Proveedor, Void> param) {
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
                            Proveedor proveedor = getTableView().getItems().get(getIndex());
                            proveedorController = new ProveedorController();
                            try {
                                proveedorController.funcEditarProveedor(proveedor);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                        });

                        eliminarButton.setOnAction(event -> {
                            Proveedor proveedor = getTableView().getItems().get(getIndex());

                            // Creamos un cuadro de diálogo para l confirmacion d3el user
                            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmacion.setTitle("Confirmación");
                            confirmacion.setHeaderText("¿Estás seguro que deseas eliminar este proveedor?");

                            // Personaliza los botones del cuadro de diálogo
                            ButtonType botonAceptar = new ButtonType("Aceptar");
                            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());
                            confirmacion.getButtonTypes().setAll(botonAceptar, botonCancelar);

                            Optional<ButtonType> resultado = confirmacion.showAndWait();

                            if (resultado.isPresent() && resultado.get() == botonAceptar) {
                                // El usuario ha confirmado la eliminación
                                try (Connection connection = DatabaseConnection.getConnection();
                                     PreparedStatement statement = connection.prepareStatement(
                                             "DELETE FROM tbl_proveedor WHERE idProveedor=?")) {

                                    statement.setInt(1, proveedor.getIdProveedor());

                                    int filasAfectadas = statement.executeUpdate();

                                    if (filasAfectadas > 0) {
                                        // Eliminación exitosa, puedes actualizar la tabla
                                        tablaProveedor.getItems().remove(proveedor);
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

        tablaProveedor.setItems(mostrarProveedor);

        //SECCION DONDDE SE FILTRA LOS DATTOS DE LA TABLA
        FilteredList<Proveedor> filteredList = new FilteredList<>(mostrarProveedor, b -> true);
        fieldBarSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(Proveedor -> {
                if(newValue.trim().isEmpty() || newValue.isBlank() || newValue == null){
                    return true;
                }
                String busqueda = newValue.toLowerCase();

                // Verificar si el nombre, cantidad o precio del producto contiene la cadena de búsqueda
                return Proveedor.getNombreProveedor().toLowerCase().contains(busqueda) ||
                        String.valueOf(Proveedor.getTelefono()).toLowerCase().contains(busqueda) ||
                        String.valueOf(Proveedor.getDireccion()).toLowerCase().contains(busqueda) ||
                        String.valueOf(Proveedor.getCorreo()).toLowerCase().contains(busqueda);
            });
        });

        SortedList<Proveedor> ListSorted = new SortedList<>(filteredList);
        //Enlazando resultado ordenado con vista de tabla
        ListSorted.comparatorProperty().bind(tablaProveedor.comparatorProperty());
        //Aplicando datos filtrados y ordenados en la vista de tabla
        tablaProveedor.setItems(ListSorted);
    }

    @FXML
    public void btnAddProveedor(){
        ProveedorController proveedorController = new ProveedorController();
        proveedorController.aggProveedor();
    }
}
