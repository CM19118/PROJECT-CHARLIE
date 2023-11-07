package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Garantia;
import com.example.projectgui.Models.ReparacionTecnico;
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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReparacionesTecnicoController {

    //Comentario Para el pull
    //--------------- Variables para hacer la conexion a la base de datos --------------------------
    private Connection conexion;
    private Statement estado; //Para el estado de la conexion
    private ResultSet resultado; //Para el resultado de la base de datos

    //Configuracion de la tabla para mostrar los datos de las reparaciones
    // se agreg Tec a acada variable para hacer referecnia que es la vista del tecnico la que se está trabajando
    @FXML
    public TableView<ReparacionTecnico> tablaReparacionesTecnico;
    @FXML
    public TableColumn<ReparacionTecnico, Integer> colIdReparacionTec;
    @FXML
    public TableColumn<ReparacionTecnico, String> colServicioTec;
    @FXML
    public TableColumn<ReparacionTecnico, Double> colPrecioUnitarioTec;
    @FXML
    public TableColumn<ReparacionTecnico, String> colFechaInicioTec;
    @FXML
    public TableColumn<ReparacionTecnico, String> colClienteTec;
    @FXML
    public TableColumn<ReparacionTecnico, String> colTelefonoTec;
    @FXML
    public TableColumn<ReparacionTecnico, String> colCorreoTec;
    @FXML
    public TableColumn<ReparacionTecnico, String> colEquipoTec;
    @FXML
    public TableColumn<ReparacionTecnico, String> colDetallesTec;
    @FXML
    public TableColumn<ReparacionTecnico, String> colEstadoTec;
    @FXML
    public TableColumn<ReparacionTecnico, String> colDetallesCostosAdicionalesTec;
    @FXML
    public TableColumn<ReparacionTecnico, Double> colMontoCostosAdicionalesTec;
    @FXML
    public TableColumn<ReparacionTecnico, Void> colAcciones1Tec;
    @FXML
    public TableColumn<ReparacionTecnico, Void> coloAcciones2Tec;

    public void initialize()
    {
        //Configuracion de las columnas
        colIdReparacionTec.setCellValueFactory(new PropertyValueFactory<>("idReparacion"));
        colServicioTec.setCellValueFactory(new PropertyValueFactory<>("servicio"));
        colPrecioUnitarioTec.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colFechaInicioTec.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colClienteTec.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colTelefonoTec.setCellValueFactory(new PropertyValueFactory<>("numTelefono"));
        colCorreoTec.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEquipoTec.setCellValueFactory(new PropertyValueFactory<>("equipo"));
        colDetallesTec.setCellValueFactory(new PropertyValueFactory<>("detalles"));
        colEstadoTec.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colDetallesCostosAdicionalesTec.setCellValueFactory(new PropertyValueFactory<>("detalleCostAdicional"));
        colMontoCostosAdicionalesTec.setCellValueFactory(new PropertyValueFactory<>("montoCostAdicional"));

        colAcciones1Tec.setCellFactory(columna1 -> {
            return new TableCell<ReparacionTecnico, Void>()
            {
                private final Button btnComenzarReparacion = new Button("Comenzar Raparacion");
                private final Button btnTerminarReparacion = new Button("Terminar Reparacion");

                //LLave de apertura donde tinen que ir el codigo de los botnes y la logica de ellos
                {
                    //Boton para iniciar la reparacion
                    btnComenzarReparacion.setOnAction(actionEvent -> {
                        ReparacionTecnico obtnerIdReparacion = getTableView().getItems().get(getIndex());

                        String consultaComenzarReparacion = "UPDATE tbl_reparaciones SET estado = 'En_Curso' WHERE idReparaciones = ?";

                        try {
                            PreparedStatement consultaPreparada =conexion.prepareStatement(consultaComenzarReparacion);
                            consultaPreparada.setInt(1, obtnerIdReparacion.getIdReparacion());

                            int resultadoConsulta = consultaPreparada.executeUpdate();
                            if (resultadoConsulta > 0 )
                            {
                                System.out.println("Se ha acutilizado el estado a En_Curso de la reparacion con id: "+obtnerIdReparacion.getIdReparacion());
                            }else {
                                System.out.println("No se pudo actulizar el estado a En_Curso de la reparcion con id: "+obtnerIdReparacion.getIdReparacion());
                            }

                        }catch (SQLException e)
                        {
                            e.printStackTrace();
                        }

                        //Para refrescar la tabla y se note el cambio de estado
                        cargarReparacionesTecnico();

                    }); //Fin del bton

                    //---------------------------------------------------------------------------------------------
                    btnTerminarReparacion.setOnAction(actionEvent -> {

                        ReparacionTecnico obtnerIdReparacion = getTableView().getItems().get(getIndex());

                        String consultaComenzarReparacion = "UPDATE tbl_reparaciones SET estado = 'Completado' WHERE idReparaciones = ?";

                        try {
                            PreparedStatement consultaPreparada =conexion.prepareStatement(consultaComenzarReparacion);
                            consultaPreparada.setInt(1, obtnerIdReparacion.getIdReparacion());

                            int resultadoConsulta = consultaPreparada.executeUpdate();
                            if (resultadoConsulta > 0 )
                            {
                                System.out.println("Se ha acutilizado el estado a Completado de la reparacion con id: "+obtnerIdReparacion.getIdReparacion());
                            }else {
                                System.out.println("No se pudo actulizar el estado a Completado de la reparcion con id: "+obtnerIdReparacion.getIdReparacion());
                            }

                        }catch (SQLException e)
                        {
                            e.printStackTrace();
                        }
                        //Para refrescar la tabla y se note el cambio de estado
                        cargarReparacionesTecnico();
                    });

                } //LLave de cierre

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {

                        ReparacionTecnico verEstadoReparacion = getTableView().getItems().get(getIndex());

                        //Verofocacion de los estados para que aparezca el boton correspondiente
                        if (verEstadoReparacion.getEstado().equals("En espera")) //Para iniciar la reparacion
                        {
                            HBox buttonsContainer = new HBox(btnComenzarReparacion);
                            setGraphic(buttonsContainer);
                        }
                        if (verEstadoReparacion.getEstado().equals("En_Curso")) //Para terminar la reparacion
                        {
                            HBox buttonsContainer = new HBox(btnTerminarReparacion);
                            setGraphic(buttonsContainer);
                        }
                    }
                }

            };
        });

        coloAcciones2Tec.setCellFactory(columna2 ->{
            return new TableCell<ReparacionTecnico, Void>()
            {
                private final Button btnAgregarCostosAdicionales = new Button(" Agregar Costos Adicionales");

                //LLave de apertura de la logica de los botones
                {
                    btnAgregarCostosAdicionales.setOnAction(actionEvent -> {
                        ReparacionTecnico obtnerInfomacionDelRegistro = getTableView().getItems().get(getIndex());

                        Stage stage = new Stage();

                        try {

                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formCostosAdicionales.fxml"));
                            Parent root = loader.load();

                            stage.setTitle("Costos Adicionales");
                            stage.setScene(new Scene(root, 600, 400));
                            stage.show();

                            TextArea textAreaDetalleCostoAdicional = (TextArea) root.lookup("#areaDetalleCostosAdicionales");
                            TextField txtMontoCostoAdicional = (TextField) root.lookup("#txtFormMontoCostoAdicional");

                            Button btnGuardarCostosAdicionales = (Button) root.lookup("#btnGuardarCostosAdicionales");
                            Button btnCancelarCostosAdicionales = (Button) root.lookup("#btnCancelarCostosAdicionales");

                            btnGuardarCostosAdicionales.setOnAction(actionEvent1 ->{

                                conexion = DatabaseConnection.getConnection();
                                String consultaAgregarCostosAdicionales = "UPDATE tbl_reparaciones SET detalleCostAdicionales = ?, montoCostAdicionales = ? WHERE idReparaciones = ?";

                                try {

                                    PreparedStatement preparacionConslta = conexion.prepareStatement(consultaAgregarCostosAdicionales);
                                    preparacionConslta.setString(1, textAreaDetalleCostoAdicional.getText().trim());
                                    preparacionConslta.setString(2, txtMontoCostoAdicional.getText().trim());
                                    preparacionConslta.setInt(3, obtnerInfomacionDelRegistro.getIdReparacion());

                                    if (preparacionConslta.executeUpdate() > 0)
                                    {
                                        System.out.println("Se ha ingresado costos adicionales");
                                    }
                                    else
                                    {
                                        System.out.println("No se pudo ingresar los costos adicionales");
                                    }
                                }catch (SQLException e)
                                {
                                    e.printStackTrace();
                                }

                                //Se cargan los datos para ver lo cambios
                                cargarReparacionesTecnico();
                                //Se cierra el form
                                stage.close();
                            });

                            //---------------------------------------------------------------------------
                            btnCancelarCostosAdicionales.setOnAction(evento -> {
                                //Fucnion para cerrar el fomr nada mas
                                stage.close();

                            });

                        }catch (Exception e)
                        {
                            e.printStackTrace();

                        }

                    });

                }//cerre de las llaves de logica

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {

                        ReparacionTecnico verEstadoReparacion = getTableView().getItems().get(getIndex());

                        //Verofocacion de los estados para que aparezca el boton correspondiente
                        if (verEstadoReparacion.getEstado().equals("En_Curso")) //Para iniciar la reparacion
                        {
                            HBox buttonsContainer = new HBox(btnAgregarCostosAdicionales);
                            setGraphic(buttonsContainer);
                        }

                    }
                }

            };
        });

        cargarReparacionesTecnico();
    }


    private void cargarReparacionesTecnico() {

        List<ReparacionTecnico> listaReparacionesTecnico = new ArrayList<>();

        conexion = DatabaseConnection.getConnection();

        //consulta a la base de datos para traer la informacion
        String consultaReparaciones = "SELECT r.idReparaciones AS IdReparacion, s.nombreServicio AS NombreServicio, r.precioUnit AS precioUnitario, r.fechaInicio AS FechaInicio,"+
                "r.cliente AS Cliente, r.telefono AS Telefono, r.correo AS Correo, r.equipo AS Equipo, r.detalles AS Detalles, r.estado AS Estado,"+
                "r.detalleCostAdicionales AS DetalleCostAdicionales, r.montoCostAdicionales AS MontoCostAdicionales "+
                "FROM tbl_reparaciones r INNER JOIN tbl_servicios s ON r.idServicio = s.idServicio;";

        try {
            listaReparacionesTecnico.clear();
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
                String correo = resultado.getString("Correo");
                String equipo = resultado.getString("Equipo");
                String detalles = resultado.getString("Detalles");
                String estado = resultado.getString("Estado");
                String detalleCost = resultado.getString("DetalleCostAdicionales");
                double montoCostA = resultado.getDouble("MontoCostAdicionales");

                //Crear objeto para pasarlo a la tabla
                ReparacionTecnico reparacionTecnico = new ReparacionTecnico(idReparacion,servicio,precioUnitario,fechaIncio,cliente,telefono,correo,equipo,detalles,estado,detalleCost,montoCostA);
                listaReparacionesTecnico.add(reparacionTecnico);

            }


        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        ObservableList<ReparacionTecnico> listaObservableReparacionesTecnico = FXCollections.observableArrayList(listaReparacionesTecnico);
        tablaReparacionesTecnico.setItems(listaObservableReparacionesTecnico);
    }

}
