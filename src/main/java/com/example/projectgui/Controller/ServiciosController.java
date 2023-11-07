package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Garantia;
import com.example.projectgui.Models.Servicio;
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

import java.nio.Buffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiciosController {

    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos

    @FXML
    public Button btnAgregarUnNuevoServicio;

    @FXML
    public TableView<Servicio> tablaServicio;
    @FXML
    public TableColumn<Servicio, Integer> colIdServicio;
    @FXML
    public TableColumn<Servicio, String> colNombreServicio;
    @FXML
    public TableColumn<Servicio, Void> colAcciones;


    public  void initialize()
    {
        colIdServicio.setCellValueFactory(new PropertyValueFactory<>("idServicio"));
        colNombreServicio.setCellValueFactory(new PropertyValueFactory<>("nombreServicio"));

        colAcciones.setCellFactory(columna -> {
            return new TableCell<Servicio, Void>() {
                private final Button btnAgregarServicio = new Button("Editar Servicio");
                {
                    btnAgregarServicio.setOnAction(accion -> {

                        Servicio obtenerInfoServicio = getTableView().getItems().get(getIndex());

                        Stage stage = new Stage();

                        try {

                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formServicio.fxml"));
                            Parent root = loader.load();

                            stage.setTitle("Editar Servicio");
                            stage.setScene(new Scene(root, 600, 400));
                            // Mostrar el escenario (ventana) con el formulario
                            stage.show();

                            TextField campoNombreServicioEditar = (TextField) root.lookup("#nombreDelServicioSeleccionado");
                            Button btnGuardarCambiosServicio = (Button) root.lookup("#btnGuardarCambiosServicio");
                            Button btnCancelarCambiosServicio = (Button) root.lookup("#btnCancelarCambiosServicio");

                            campoNombreServicioEditar.setText(obtenerInfoServicio.getNombreServicio());

                            btnGuardarCambiosServicio.setOnAction(cargarDatos -> {

                                try {
                                    conexion = DatabaseConnection.getConnection();
                                    String nuevoNombreServicio = campoNombreServicioEditar.getText();

                                    // Realizar la consulta de actualización
                                    String consultaActualizacion = "UPDATE tbl_servicios SET nombreServicio = ? WHERE idServicio = ?";
                                    PreparedStatement preparedStatement = conexion.prepareStatement(consultaActualizacion);
                                    preparedStatement.setString(1, nuevoNombreServicio);
                                    preparedStatement.setInt(2, obtenerInfoServicio.getIdServicio());

                                    int filasActualizadas = preparedStatement.executeUpdate();

                                    // Verificar si la actualización fue exitosa
                                    if (filasActualizadas > 0) {
                                        System.out.println("Servicio actualizado con éxito.");
                                    } else {
                                        System.out.println("No se pudo actualizar el servicio.");
                                    }

                                    // Cerrar la conexión y la ventana
                                    preparedStatement.close();
                                    conexion.close();

                                    stage.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                //Cargar datos para notar el cambio
                                cargarRegistroDeServicios();

                            });

                            btnCancelarCambiosServicio.setOnAction(accionCancelar -> {
                                stage.close();
                            });

                        }catch (Exception e)
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
                        HBox buttonsContainer = new HBox(btnAgregarServicio);
                        setGraphic(buttonsContainer);
                    }
                }

            }; //Fin del return

        });//Fin de la llave de configuracion

        btnAgregarUnNuevoServicio.setOnAction(eventoAgregarServicio -> agregarServicio());

        cargarRegistroDeServicios();

    }

    private void agregarServicio() {

        Stage stage = new Stage();

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formNewServicio.fxml"));
            Parent root = loader.load();

            stage.setTitle("Editar Servicio");
            stage.setScene(new Scene(root, 600, 400));
            // Mostrar el escenario (ventana) con el formulario
            stage.show();

            TextField campoNombreServicioNuevo = (TextField) root.lookup("#txtNuevoServicio");
            Button btnGuardarServicio = (Button) root.lookup("#btnGuardarNewServicio");
            Button btnCancelarServicio = (Button) root.lookup("#btnCancelarNewServicio");

            btnGuardarServicio.setOnAction(event -> {

                conexion = DatabaseConnection.getConnection();

                // Crea la consulta de inserción
                String consultaInsercion = "INSERT INTO tbl_servicios (nombreServicio) VALUES (?)";
                try {
                    PreparedStatement preparedStatement = conexion.prepareStatement(consultaInsercion);

                    // Configura los parámetros de la consulta con los datos del nuevo servicio
                    preparedStatement.setString(1, campoNombreServicioNuevo.getText().trim());

                    // Ejecuta la consulta de inserción
                    int filasInsertadas = preparedStatement.executeUpdate();

                    // Verifica si la inserción fue exitosa
                    if (filasInsertadas > 0) {
                        System.out.println("Servicio insertado con éxito.");
                    } else {
                        System.out.println("No se pudo insertar el servicio.");
                    }
                    // Cierra la conexión y realiza cualquier otra operación necesaria
                    preparedStatement.close();
                    conexion.close();

                }catch (SQLException e) {
                    e.printStackTrace();
                }

                //Para refrescar la tabla
                cargarRegistroDeServicios();

                stage.close();

            });

            //------------------------------------------------------------
            btnCancelarServicio.setOnAction(event -> {
                stage.close();
            });


        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private void cargarRegistroDeServicios() {

        List<Servicio> listaServicios = new ArrayList<>();
        conexion = DatabaseConnection.getConnection();

        try {
            String consultaServicios = "SELECT * FROM tbl_servicios";
            estado = conexion.createStatement();
            resultado = estado.executeQuery(consultaServicios);

            while (resultado.next()) {
                int idServicio = resultado.getInt("idServicio");
                String nombreServicio = resultado.getString("nombreServicio");

                Servicio servicio = new Servicio(idServicio, nombreServicio);
                listaServicios.add(servicio);
            }

            // Cerrar el estado y la conexión
            resultado.close();
            estado.close();
            conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObservableList<Servicio> listaObservableServicios = FXCollections.observableArrayList(listaServicios);
        tablaServicio.setItems(listaObservableServicios);

    }


}
