package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Usuario;
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

public class TblUsuarioController implements Initializable {
    @FXML
    private TableView<Usuario> tablaUsuario;
    @FXML
    private TableColumn<Usuario, Void> col_acciones;
    @FXML
    private TableColumn<Usuario, String> col_idUsuario;
    @FXML
    private TableColumn<Usuario, String> col_idEmpleado;
    @FXML
    private TableColumn<Usuario, String> col_usuario;
    @FXML
    private TableColumn<Usuario, String> col_password;
    @FXML
    private TableColumn<Usuario, String> col_rol;


    //INSTANCIAs PARA LA BASE DE DATOS
    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;
    //ventana Usuario

    private UsuarioController Usuariocontroller;
    private ObservableList<Usuario> dataList = FXCollections.observableArrayList();
    @FXML
    private TextField fieldBarSearch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mostrarListaUsuario();
    }

    public ObservableList<Usuario> listaUsuario() {
        connect = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM tbl_usuarios";
        try {
            dataList.clear();
            statement = connect.createStatement();
            result = statement.executeQuery(sql);
            Usuario Usuario;

            while (result.next()) {
                int idUsuario = result.getInt("idUsuario");
                int idEmpleado = result.getInt("idEmpleado");
                String usuario = result.getString("usuario");
                String password = result.getString("password");
                int rol = result.getInt("rol");


                Usuario = new Usuario(idUsuario,idEmpleado,usuario,password,rol);
                dataList.addAll(Usuario);
            }
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }


    public void mostrarListaUsuario(){
        ObservableList<Usuario> mostrarUsuario = listaUsuario();

        col_idUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        col_idEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        col_usuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        col_password.setCellValueFactory(new PropertyValueFactory<>("password"));
        col_rol.setCellValueFactory(new PropertyValueFactory<>("rol"));


        // En este apartado configuramos la columna de acciones para mostrar botones
        // Los botones los agregaremos con codigo, porque directamente con el FXML no se puede
        col_acciones.setCellFactory(new Callback<TableColumn<Usuario, Void>, TableCell<Usuario, Void>>() {
            @Override
            public TableCell<Usuario, Void> call(TableColumn<Usuario, Void> param) {
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
                            Usuario Usuario2 = getTableView().getItems().get(getIndex());
                            Usuariocontroller = new UsuarioController();
                            try {
                                Usuariocontroller.funcEditarUsuario(Usuario2);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                        });

                        eliminarButton.setOnAction(event -> {
                            Usuario Usuario = getTableView().getItems().get(getIndex());

                            // Creamos un cuadro de diálogo para l confirmacion d3el user
                            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmacion.setTitle("Confirmación");
                            confirmacion.setHeaderText("¿Estás seguro que deseas eliminar este Usuario?");

                            // Personaliza los botones del cuadro de diálogo
                            ButtonType botonAceptar = new ButtonType("Aceptar");
                            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());
                            confirmacion.getButtonTypes().setAll(botonAceptar, botonCancelar);

                            Optional<ButtonType> resultado = confirmacion.showAndWait();

                            if (resultado.isPresent() && resultado.get() == botonAceptar) {
                                // El usuario ha confirmado la eliminación
                                try (Connection connection = DatabaseConnection.getConnection();
                                     PreparedStatement statement = connection.prepareStatement(
                                             "DELETE FROM tbl_usuarios WHERE idUsuario=?")) {

                                    statement.setInt(1, Usuario.getIdUsuario());

                                    int filasAfectadas = statement.executeUpdate();

                                    if (filasAfectadas > 0) {
                                        // Eliminación exitosa, actualizar la tabla
                                        dataList.remove(Usuario);
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

        tablaUsuario.setItems(mostrarUsuario);

        //SECCION DONDDE SE FILTRA LOS DATTOS DE LA TABLA
        FilteredList<Usuario> filteredList = new FilteredList<>(mostrarUsuario, b -> true);
        fieldBarSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(Usuario -> {
                if(newValue.trim().isEmpty() || newValue.isBlank() || newValue == null){
                    return true;
                }
                String busqueda = newValue.toLowerCase();

                // Verificar si el nombre, cantidad o precio del Usuario contiene la cadena de búsqueda
                return Usuario.getUsuario().toLowerCase().contains(busqueda) ||
                        String.valueOf(Usuario.getRol()).toLowerCase().contains(busqueda) ||
                        String.valueOf(Usuario.getIdEmpleado()).toLowerCase().contains(busqueda);
            });
        });

        SortedList<Usuario> ListSorted = new SortedList<>(filteredList);
        //Enlazando resultado ordenado con vista de tabla
        ListSorted.comparatorProperty().bind(tablaUsuario.comparatorProperty());
        //Aplicando datos filtrados y ordenados en la vista de tabla
        tablaUsuario.setItems(ListSorted);
    }

    @FXML
    public void btnaddUsuario(){
        UsuarioController productControlador = new UsuarioController();
        productControlador.aggUsuario();
    }


}
