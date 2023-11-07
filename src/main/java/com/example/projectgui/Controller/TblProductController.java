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


public class TblProductController implements Initializable {
    @FXML
    private TableView<Producto> tablaProducto;
    @FXML
    private TableColumn<Producto, Void> col_acciones;

    @FXML
    private TableColumn<Producto, String> col_cantidad;

    @FXML
    private TableColumn<Producto, Integer> col_idProd;

    @FXML
    private TableColumn<Producto, String> col_nombre;

    @FXML
    private TableColumn<Producto, String> col_precio;

    @FXML
    private TableColumn<Producto, String> col_proveedor;
    //INSTANCIAS PARA LA BASE DE DATOS
    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;
    //ventana producto

    private ProductoController productocontroller;
    private ObservableList<Producto> dataList = FXCollections.observableArrayList();
    @FXML
    private TextField fieldBarSearch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mostrarListaProducto();
    }

    public ObservableList<Producto> listaProducto() {
        connect = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM tbl_productos";
        try {
            dataList.clear();
            statement = connect.createStatement();
            result = statement.executeQuery(sql);
            Producto producto;

            while (result.next()) {
                int idProducto = result.getInt("idProducto");
                String nombreProducto = result.getString("nombreProducto");
                int cantidad = result.getInt("cantidad");
                double precio = result.getDouble("precio");
                int idProveedor = result.getInt("idProveedor");

                // Aquí cargamos los datos del proveedor
                Proveedor proveedor = obtenerProveedorPorId(idProveedor);

                producto = new Producto(idProducto, nombreProducto, cantidad, precio, proveedor.getIdProveedor());
                dataList.addAll(producto);
            }
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private Proveedor obtenerProveedorPorId(int idProveedor) {
        Proveedor proveedor = null;
        Connection connection = null;  // Declarar la conexión fuera del try para poder cerrarla en el bloque finally

        try {
            connection = DatabaseConnection.getConnection();
            String proveedorQuery = "SELECT * FROM tbl_proveedor WHERE idProveedor = ?";
            PreparedStatement proveedorStatement = connection.prepareStatement(proveedorQuery);
            proveedorStatement.setInt(1, idProveedor);
            ResultSet proveedorResult = proveedorStatement.executeQuery();

            if (proveedorResult.next()) {
                int id = proveedorResult.getInt("idProveedor");
                String nombre = proveedorResult.getString("nombreProveedor");
                String direccion = proveedorResult.getString("direccion");
                String correo = proveedorResult.getString("correo");
                String tipo = proveedorResult.getString("tipo");
                String telefono = proveedorResult.getString("telefono");
                String observaciones = proveedorResult.getString("observaciones");

                proveedor = new Proveedor(id, nombre, direccion, correo, tipo, telefono, observaciones);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();  // Cierra la conexión en el bloque finally
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return proveedor;
    }


    int obtenerIdProveedorPorNombre(String nombreProveedor) {
        int idProveedor = -1; // Valor por defecto si no se encuentra el proveedor

        try {
            connect = DatabaseConnection.getConnection();
            String sql = "SELECT idProveedor FROM tbl_proveedor WHERE nombreProveedor = ?";
            PreparedStatement preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, nombreProveedor);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                idProveedor = resultSet.getInt("idProveedor");
            }

            preparedStatement.close();
            resultSet.close();
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idProveedor;
    }


    public void mostrarListaProducto(){
        dataList.clear();
        ObservableList<Producto> mostrarProducto = listaProducto();

        col_idProd.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        col_cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        col_precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        col_proveedor.setCellValueFactory(new PropertyValueFactory<>("idProveedor"));

        // En este apartado configuramos la columna de acciones para mostrar botones
        // Los botones los agregaremos con codigo, porque directamente con el FXML no se puede
        col_acciones.setCellFactory(new Callback<TableColumn<Producto, Void>, TableCell<Producto, Void>>() {
            @Override
            public TableCell<Producto, Void> call(TableColumn<Producto, Void> param) {
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
                            Producto producto2 = getTableView().getItems().get(getIndex());
                            productocontroller = new ProductoController();
                            try {
                                Proveedor proveedor = obtenerProveedorPorId(producto2.getIdProveedor());
                                productocontroller.funcEditarProducto(producto2,proveedor.getNombreProveedor());
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                        });

                        eliminarButton.setOnAction(event -> {
                            Producto producto = getTableView().getItems().get(getIndex());

                            // Creamos un cuadro de diálogo para l confirmacion d3el user
                            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmacion.setTitle("Confirmación");
                            confirmacion.setHeaderText("¿Estás seguro que deseas eliminar este producto?");

                            // Personaliza los botones del cuadro de diálogo
                            ButtonType botonAceptar = new ButtonType("Aceptar");
                            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());
                            confirmacion.getButtonTypes().setAll(botonAceptar, botonCancelar);

                            Optional<ButtonType> resultado = confirmacion.showAndWait();

                            if (resultado.isPresent() && resultado.get() == botonAceptar) {
                                // El usuario ha confirmado la eliminación
                                try (Connection connection = DatabaseConnection.getConnection();
                                     PreparedStatement statement = connection.prepareStatement(
                                             "DELETE FROM tbl_productos WHERE idProducto=?")) {

                                    statement.setInt(1, producto.getIdProducto());

                                    int filasAfectadas = statement.executeUpdate();

                                    if (filasAfectadas > 0) {
                                        // Eliminación exitosa, puedes actualizar la tabla
                                        dataList.remove(producto);
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

        tablaProducto.setItems(mostrarProducto);

        //SECCION DONDDE SE FILTRA LOS DATTOS DE LA TABLA
        FilteredList<Producto> filteredList = new FilteredList<>(mostrarProducto, b -> true);
        fieldBarSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(Producto -> {
                if(newValue.trim().isEmpty() || newValue.isBlank() || newValue == null){
                    return true;
                }
                String busqueda = newValue.toLowerCase();

                // Verificar si el nombre, cantidad o precio del producto contiene la cadena de búsqueda
                return Producto.getNombreProducto().toLowerCase().contains(busqueda) ||
                        String.valueOf(Producto.getCantidad()).toLowerCase().contains(busqueda) ||
                        String.valueOf(Producto.getPrecio()).toLowerCase().contains(busqueda);
            });
        });

        SortedList<Producto> ListSorted = new SortedList<>(filteredList);
        //Enlazando resultado ordenado con vista de tabla
        ListSorted.comparatorProperty().bind(tablaProducto.comparatorProperty());
        //Aplicando datos filtrados y ordenados en la vista de tabla
        tablaProducto.setItems(ListSorted);
    }

    @FXML
    public void btnaddProducto(){
        ProductoController productControlador = new ProductoController();
        productControlador.aggProducto();
    }

}
