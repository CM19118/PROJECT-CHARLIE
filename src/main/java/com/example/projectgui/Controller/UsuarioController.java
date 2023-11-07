package com.example.projectgui.Controller;

import com.example.projectgui.DatabaseConnection;
import com.example.projectgui.Models.Empleado;
import com.example.projectgui.Models.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UsuarioController implements Initializable {
    private Stage stage;

    //INSTANCIAS PARA LA BASE DE DATOS
    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;
    //ventana producto

    @FXML
    private TextField fieldUsuario, fieldPassword;
    @FXML
    private ComboBox listRoles, listEmpleados;
    public static boolean bandera;
    public String query;
    public static int idUsuario;

    private TblProductController tblproduct;
    //

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void aggUsuario() {
        bandera = false;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formUsuario.fxml"));
            Parent parent = loader.load();
            UsuarioController usuarioController = loader.getController(); // Obtener el controlador
            usuarioController.cargandoDatosListEmpleados();
            usuarioController.cargandoDatosListRoles();

            Scene scene = new Scene(parent);
            stage = new Stage();
            usuarioController.setStage(stage); // Pasar el Stage a la función para almacenar la ventana actual
            stage.setScene(scene); // Usa el Stage local
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void funcEditarUsuario(Usuario usuario) throws SQLException {
        setUpdate(true);
        System.out.println(bandera);
        // Obteniendo la conexión a la base de datos aquí
        connect = DatabaseConnection.getConnection();
        statement = connect.createStatement();

        idUsuario = usuario.getIdUsuario();

        Empleado empleado = obtenerEmpleadoPorId(usuario.getIdEmpleado());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formUsuario.fxml"));
            Parent parent = loader.load();
            UsuarioController usuarioController = loader.getController(); // Obtener el controlador

            String rol;
            if(usuario.getRol() == 0){
                rol = "Admin";
            } else if (usuario.getRol() == 1) {
                rol = "Empleado";
            }else{
                rol = "Tecnico";
            }
            usuarioController.fieldUsuario.setText(usuario.getUsuario());
            //usuarioController.fieldPassword.setText(usuario.getPassword());
            usuarioController.cargandoDatosListEmpleados();
            usuarioController.cargandoDatosListRoles();


            usuarioController.listRoles.setValue(rol);
            //System.out.println("EL NOMBRE COMPLETO DEL EMPLEADO USUARIO ES: "+empleado.getNombreCompleto());
            usuarioController.listEmpleados.setValue(empleado.getNombreCompleto());
            usuarioController.listEmpleados.setDisable(true);

            Scene scene = new Scene(parent);
            stage = new Stage();
            usuarioController.setStage(stage); // Pasar el Stage a la función para almacenar la ventana actual
            stage.setScene(scene); // Usa el Stage local
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Empleado obtenerEmpleadoPorId(int idEmpleado){
        Empleado empleado = null;
        Connection connection = null;  // Declarar la conexión fuera del try para poder cerrarla en el bloque finally
        //System.out.println("EL ID DEL EMPLEADO RECIBIDO ES: "+idEmpleado);
        try {
            connection = DatabaseConnection.getConnection();
            String proveedorQuery = "SELECT * FROM tbl_empleados WHERE idEmpleado = ?";
            PreparedStatement proveedorStatement = connection.prepareStatement(proveedorQuery);
            proveedorStatement.setInt(1, idEmpleado);
            ResultSet result = proveedorStatement.executeQuery();

            if (result.next()) {
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

                empleado = new Empleado(idEmpleado, nombres, apellidos, edad, dui, direccion, telefono, correo, cargo, estadoEmpleado, estadoUsuario, nombreCompleto);
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
        return empleado;
    }
    @FXML
    private void onCancelarButtonClick() {
        // Agregamos la funcionalidad que deseas ejecutar cuando se hace clic en "CANCELAR"
        System.out.println("presionando boton 'CANCELAR'.");
        // Cierra la ventana actual
        stage.close();
    }
    @FXML
    private void onAceptarButtonClick() throws SQLException {
        // Funcionalidad que hará cuando se le dé clic al botón ACEPTAR de la ventana productos
        System.out.println("Botón 'ACEPTAR' está siendo presionado.");
        Connection connect = null; // Declarar la conexión aquí

        try {
            connect = DatabaseConnection.getConnection(); // Abre la conexión

            if (!fieldUsuario.getText().isEmpty() && listEmpleados.getValue()!=null &&
                    listRoles.getValue()!=null) {
                // Obtener el ID del proveedor correspondiente al nombre seleccionado
                tblproduct = new TblProductController();
                int rolUser = determinarRol(listRoles.getValue().toString());
                // Obtener el ID del empleado correspondiente al nombre seleccionado en el ComboBox
                String nombreCompletoSeleccionado = listEmpleados.getValue().toString();
                int idEmpleadoSeleccionado = obtenerIdEmpleadoPorNombreCompleto(nombreCompletoSeleccionado);
                String passwordHash = BCrypt.hashpw(fieldPassword.getText(), BCrypt.gensalt());
                //System.out.println(passwordHash);

                //System.out.println(bandera);
                if(bandera){
                    if(fieldPassword.getText().isEmpty()){
                        query = "UPDATE `tbl_usuarios` SET "
                                + "`idEmpleado`=?,"
                                + "`usuario`=?,"
                                + "`rol`=? WHERE idUsuario = '"+idUsuario+"'";

                    }else{
                        query = "UPDATE `tbl_usuarios` SET "
                                + "`idEmpleado`=?,"
                                + "`usuario`=?,"
                                + "`password`=?,"
                                + "`rol`=? "
                                + "WHERE idUsuario = '"+idUsuario+"'";
                    }
                }else{
                    query = "INSERT INTO `tbl_usuarios`(`idEmpleado`, `usuario`, `password`, `rol`) VALUES (?,?,?,?)";
                }
                PreparedStatement preparedStatement = connect.prepareStatement(query);
                if(fieldPassword.getText().isEmpty()){
                    preparedStatement.setInt(1, idEmpleadoSeleccionado);
                    preparedStatement.setString(2, fieldUsuario.getText());
                    preparedStatement.setInt(3, rolUser);
                }else{
                    preparedStatement.setInt(1, idEmpleadoSeleccionado);
                    preparedStatement.setString(2, fieldUsuario.getText());
                    preparedStatement.setString(3, passwordHash);
                    preparedStatement.setInt(4, rolUser);
                }
                preparedStatement.execute();
                // Cierra la ventana de productos (si es necesario)
                if (stage != null) {
                    actualizarEstadoEmpleado(idEmpleadoSeleccionado,connect);
                    stage.close();
                }
                // Actualiza la tabla

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("POR FAVOR COMPLETE TODOS LOS DATOS!! :D");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores
        } finally {
            if (connect != null) {
                try {
                    connect.close(); // Cierra la conexión en el bloque finally
                } catch (SQLException e) {
                    e.printStackTrace(); // Manejo de errores
                }
            }
        }
    }
    private void actualizarEstadoEmpleado(int idEmpleado, Connection connect) throws SQLException {
        query = "UPDATE `tbl_empleados` SET "
                + "`estadoUsuario`=? WHERE idEmpleado = '"+idEmpleado+"'";
        PreparedStatement preparedStatement = connect.prepareStatement(query);
        preparedStatement.setInt(1, 1);
        preparedStatement.execute();
    }
    private int determinarRol(String rol){
        int numRol;
        if(rol.contentEquals("Admin")){
            numRol = 0;
        } else if (rol.contentEquals("Empleado")) {
            numRol = 1;
        }else{
            numRol = 2;
        }
        return numRol;
    }
    private void cargandoDatosListRoles(){
        List<String> listaRoles = new ArrayList<>();
        listaRoles.add("Empleado");
        listaRoles.add("Tecnico");
        listaRoles.add("Admin");
        ObservableList<String> items = FXCollections.observableArrayList(listaRoles);
        listRoles.setItems(items);
    }
    public void cargandoDatosListEmpleados() throws SQLException {
        // Obteniendo la conexión a la base de datos aquí
        connect = DatabaseConnection.getConnection();
        statement = connect.createStatement();
        result = statement.executeQuery("SELECT * FROM tbl_empleados WHERE estadoUsuario = 0");

        // Creamos una lista para almacenar los datos del proveedor y guardarlo en el combox
        List<String> nombresEmpleados = new ArrayList<>();

        while (result.next()) {
            String nombreEmpleado = result.getString("nombres");
            String apellidoEmpleado = result.getString("apellidos");
            String nombreCompleto = nombreEmpleado + " " + apellidoEmpleado;
            nombresEmpleados.add(nombreCompleto);
        }

        //Cerramos todos los recursos  utilizados de la base de datos
        result.close();
        statement.close();
        connect.close();

        ObservableList<String> items = FXCollections.observableArrayList(nombresEmpleados);
        listEmpleados.setItems(items);
    }
    private int obtenerIdEmpleadoPorNombreCompleto(String nombreCompleto) throws SQLException {
        Connection connection = null;
        int idEmpleado = -1; // Valor predeterminado en caso de que no se encuentre ningún empleado

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT idEmpleado FROM tbl_empleados WHERE CONCAT(nombres, ' ', apellidos) = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nombreCompleto);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                idEmpleado = resultSet.getInt("idEmpleado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

        return idEmpleado;
    }
    //funcion para guardar el stage actual (ventana principal)
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUpdate(boolean bandera){
        this.bandera = bandera;
    }
}
