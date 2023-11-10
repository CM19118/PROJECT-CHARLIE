package com.example.projectgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Main implements Initializable {
    @FXML
    private Text textoMain;
    @FXML
    private Button btnCarrito, btnFacturas, btnInventario, btnProductos, btnVentas, btnProveedores, btnReparaciones, btnGarantias, btnReparacionesTecnico, btnCompras;
    @FXML
    private MenuItem opcCrearEmpleado, opcCrearUsuarios, opcAsistencia, opcGestionarServicios;
    @FXML
    private StackPane centerPane;
    @FXML
    private SplitMenuButton splitMenuBtn;
    //INSTANCIAS PARA LA BASE DE DATOS
    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;
    private Map<Button, String> buttonViewMap = new HashMap<>();
    private Button activeButton = null;


    ////////////////////

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonViewMap.put(btnProductos, "Producto.fxml");
        buttonViewMap.put(btnInventario, "Inventario.fxml");
        buttonViewMap.put(btnCarrito, "Carrito.fxml");
        buttonViewMap.put(btnFacturas, "Factura.fxml");
        buttonViewMap.put(btnVentas, "Ventas.fxml");
        buttonViewMap.put(btnProveedores, "Proveedor.fxml");
        buttonViewMap.put(btnReparaciones, "Reparaciones.fxml");
        buttonViewMap.put(btnGarantias, "Garantias.fxml");
        buttonViewMap.put(btnReparacionesTecnico, "RaparacionesTecnico.fxml");
        buttonViewMap.put(btnCompras,"Compras.fxml");

        // Obtenemos la instancia de UserSession desde SesionManager
        UserSession userSession = SessionManager.getUsuarioSesion();
        // Verificamos si la sesión existe y obtenemos datos
        if (userSession != null) {
            String username = userSession.getUsername();
            int rol = Integer.parseInt(userSession.getRol());
            manejodeVistas(rol);
            int idUsuario = userSession.getIdUsuario();
            int idEmpleado = userSession.getIdEmpleado();
        } else {
            // No hay sesión activa
        }
    }
    @FXML
    private void handleClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String fxmlFile = buttonViewMap.get(clickedButton);

        // Deshabilitar todos los botones mientras se procesa un clic
        for (Button button : buttonViewMap.keySet()) {
            button.setDisable(true);
        }

        // Restaurar el estilo predeterminado para el botón activo anterior (si existe)
        if (activeButton != null) {
            activeButton.getStyleClass().remove("button-active");
            activeButton.getStyleClass().add("sidebar-button");
        }

        // Aplicar estilo al botón activo
        clickedButton.getStyleClass().remove("sidebar-button");
        clickedButton.getStyleClass().add("button-active");

        if (fxmlFile != null) {
            loadView(fxmlFile);
            // Actualizar el texto en tu etiqueta o título
            textoMain.setText(clickedButton.getText().toLowerCase());

            // Guardar el botón activo actual
            activeButton = clickedButton;
        }

        // Habilitar todos los botones después de completar el procesamiento del clic
        for (Button button : buttonViewMap.keySet()) {
            button.setDisable(false);
        }
    }

    private void manejodeVistas(int rol){
        if(rol == 0){
            loadView("Producto.fxml");
            splitMenuBtn.setText("Admin");
        }else if(rol == 1){
            loadView("Producto.fxml");
            splitMenuBtn.setText("Empleado");
            opcCrearEmpleado.setVisible(false);
            opcCrearUsuarios.setVisible(false);
            opcAsistencia.setVisible(false);
            opcGestionarServicios.setVisible(false);
            btnProveedores.setVisible(false);
            btnReparacionesTecnico.setVisible(false);

        }else{
            loadView("RaparacionesTecnico.fxml");
            splitMenuBtn.setText("Tecnico");
            opcCrearEmpleado.setVisible(false);
            opcCrearUsuarios.setVisible(false);
            opcAsistencia.setVisible(false);
            opcGestionarServicios.setVisible(false);
            btnProveedores.setVisible(false);
            btnCarrito.setVisible(false);
            btnVentas.setVisible(false);
            btnProductos.setVisible(false);
            btnInventario.setVisible(false);
            btnFacturas.setVisible(false);
            btnGarantias.setVisible(false);
            btnReparaciones.setVisible(false);
            btnCompras.setVisible(false);
        }
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/" + fxmlFile));
            Parent view = loader.load();

            // Obtengo el controlador secundario desde el FXMLLoader para obtener los datos del diseno fmxl.
            Object controller = loader.getController();

            // Esto reemplazaa el contenido del centro del StackPane (panel principal del main donde se muestra la informacion)
            centerPane.getChildren().clear();
            centerPane.getChildren().add(view);


        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores al cargar la vista
        }
    }

    @FXML
    private void openFormAsistencia(){
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectgui/formAsistencia.fxml"));
            Parent parent = loader.load();

            Scene scene = new Scene(parent);
            stage.setScene(scene); // Usa el Stage local
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openFormEmpleados(){
        loadView("Empleado.fxml");
    }
    @FXML
    private void openFormUsuarios(){
        loadView("Usuario.fxml");
    }
    @FXML
    private void  openVistasAsistencia()
    {
        loadView("VistaAsistencias.fxml");
    }
    public void openVistaServicios() {
        loadView("Servicios.fxml");
    }
}
