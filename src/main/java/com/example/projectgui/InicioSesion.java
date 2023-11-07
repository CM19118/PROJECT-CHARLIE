package com.example.projectgui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.mindrot.jbcrypt.BCrypt;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class InicioSesion extends Application {

    @FXML
    private TextField txtPass, txtUser, txtPassword1, txtPassword2;
    private static final String CONTRASENA = "reparaciones_123";
    private static final String CORREO_USUARIO = "reparacioneskelly2@outlook.com";
    private static String email;
    private Stage primaryStage;
    private static final String CARACTERES_PERMITIDOS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom random = new SecureRandom();
    String codigo_verificacion;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/projectgui/InicioSesion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1110, 640);
        InicioSesion controller = fxmlLoader.getController();
        stage.initStyle(StageStyle.UTILITY);
        controller.setPrimaryStage(stage);

        stage.setTitle("INICIO DE SESION");
        stage.setScene(scene);
        stage.show();
    }
    private void showLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/projectgui/InicioSesion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1110, 640);
        Stage stage = new Stage();
        setPrimaryStage(stage); // Pasar el Stage a la función para almacenar la ventana actual
        stage.setScene(scene); // Usa el Stage local
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }
    @FXML
    protected void onBtnLoginClicked() throws IOException {
        String username = txtUser.getText().toString();
        String password = txtPass.getText().toString();

        if (!password.isEmpty() && !username.isEmpty()) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "SELECT idUsuario, idEmpleado, password FROM tbl_usuarios WHERE usuario = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String storedHashFromDatabase = resultSet.getString("password");
                    int idUsuario = resultSet.getInt("idUsuario");
                    int idEmpleado = resultSet.getInt("idEmpleado");

                    if (BCrypt.checkpw(password, storedHashFromDatabase)) {
                        // Autenticación exitosa
                        // Almacenamos los datos del usuario que ha iniciado sesion (Manejo de sesiones)
                        String rol = obtenerRolDeLaBaseDeDatos(connection, idUsuario); // Obtén el rol del usuario
                        UserSession userSession = new UserSession(username, rol, idUsuario, idEmpleado);
                        SessionManager.setUsuarioSesion(userSession);

                        // Abrimos la nueva ventana, la ventana principal del sistema(el main)!
                        //primaryStage.close();
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/projectgui/Main.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());
                        Stage stage = new Stage();
                        setPrimaryStage(stage);
                        //primaryStage.initStyle(StageStyle.UTILITY);
                        stage.setTitle("MENU PRINCIPAL");
                        stage.setScene(scene);
                        stage.setFullScreen(true);
                        //stage.setFullScreenExitKeyCombination(KeyCombination.ALT_ANY);
                        stage.show();
                    } else {
                        mostrarError("CREDENCIALES ERRÓNEAS");
                    }
                } else {
                    mostrarError("CREDENCIALES ERRÓNEAS");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            mostrarError("HAY CAMPOS VACÍOS");
        }
    }
    // Función para mostrar un mensaje de error en un cuadro de diálogo
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    // Función para obtener el rol del usuario desde la base de datos
    private String obtenerRolDeLaBaseDeDatos(Connection connection, int idUsuario) throws SQLException {
        String rol = "RolPorDefecto"; // Establece un valor por defecto si no puedes obtener el rol desde la base de datos
        String query = "SELECT rol FROM tbl_usuarios WHERE idUsuario = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, idUsuario);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            rol = resultSet.getString("rol");
        }
        return rol;
    }
    public void ShowEmailDialog(){
        // Creamos un campo de texto para que ingreseeel correo electrónico
        TextField emailTextField = new TextField();
        codigo_verificacion = "";
        emailTextField.setPromptText("Ingrese su correo electrónico");

        // Creamos el diseño de vista apilados hacia abajo
        VBox vbox = new VBox();
        vbox.getChildren().addAll(emailTextField);

        // Creamos el cuadro de diálogo donde se mostrara la alerta
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Recuperación de Contraseña");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(vbox);

        // Añadimos los botones enviar y cancelar al cuadro de diálogo
        ButtonType sendCodeButton = new ButtonType("Enviar Código");
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());
        alert.getButtonTypes().setAll(sendCodeButton, cancelButton);

        // Obtenemos el resultado del cuadro de diálogo, dependiendo del boton que de click
        alert.showAndWait().ifPresent(response -> {
            if (response == sendCodeButton) {
                if(!emailTextField.getText().isEmpty()){
                    // Aqu implementamos la lógica para enviar el código de recuperación al correo electrónico proporcionado
                    codigo_verificacion = generarCodigoVerificacion(6);
                    email = emailTextField.getText();
                    //Invocamos funcion para insertar el codigo en el email del usuario
                    actualizarCodigoVerificacion(email,codigo_verificacion);
                    enviarCodigoVerificacion(email,codigo_verificacion);
                    System.out.println("Enviando código de recuperación a: " + email);
                    ShowVerificationCodeDialog(emailTextField.getText());
                }else{
                    mostrarError("EL CAMPO EMAIL ESTA VACIO!!");
                    ShowEmailDialog();
                }
            } else {
                // Si el usuario hace clic en Cancelar, cierra el cuadro de diálogo
                alert.close();
            }
        });
    }
    public void ShowVerificationCodeDialog(String email){
        // Creamos un campo de texto para que ingreseeel correo electrónico
        TextField codeTextField = new TextField();
        codeTextField.setPromptText("Ingrese el codigo de verificacion");

        // Creamos el diseño de vista apilados hacia abajo
        VBox vbox = new VBox();
        vbox.getChildren().addAll(codeTextField);

        // Creamos el cuadro de diálogo donde se mostrara la alerta
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Recuperación de Contraseña");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(vbox);

        // Añadimos los botones confirmar y cancelar al cuadro de diálogo
        ButtonType CodeButton = new ButtonType("Confirmar");
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());
        alert.getButtonTypes().setAll(CodeButton, cancelButton);
        //////////////////////////////////////////////////////////////
        // Obtenemos el resultado del cuadro de diálogo, dependiendo del boton que de click
        alert.showAndWait().ifPresent(response -> {
            if (response == CodeButton) {
                if (!codeTextField.getText().isEmpty()) {
                    String codigo = codeTextField.getText();
                    boolean isValidCode = comprobarCodigoVerificacion(email, codigo);
                    if (isValidCode) {
                        // Código y correo electrónico válidos, por lo tanto pasamos a pedir la nueva contra
                        try {
                            showLayoutPassword();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        mostrarError("El código de verificación no es válido o ha expirado.");
                        ShowVerificationCodeDialog(email);
                    }
                } else {
                    mostrarError("El campo del código de verificación está vacío o no es válido.");
                    ShowVerificationCodeDialog(email);
                }
            } else {
                // Si el usuario hace clic en Cancelar, cierra el cuadro de diálogo
                alert.close();
            }
        });
    }
    public boolean comprobarCodigoVerificacion(String email, String codigo) {
        String query = "SELECT * FROM tbl_empleados WHERE correo = ? AND codigoVerificacion = ? AND TIMESTAMPDIFF(MINUTE, fechaGeneracion, NOW()) <= 10;";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, codigo);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Si hay un resultado, el correo y el código coinciden en la base de datos
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores
            return false; // En caso de error, consideramos que la validación falló
        }
    }
    public static String generarCodigoVerificacion(int longitud) {
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            int indice = random.nextInt(CARACTERES_PERMITIDOS.length());
            sb.append(CARACTERES_PERMITIDOS.charAt(indice));
        }
        return sb.toString();
    }
    public static void actualizarCodigoVerificacion(String email, String codigo) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE tbl_empleados SET codigoVerificacion = ?, fechaGeneracion = NOW() WHERE correo = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, codigo);
                statement.setString(2, email);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores
        }
    }
    public static void enviarCodigoVerificacion(String destino, String codigo) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.outlook.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(CORREO_USUARIO, CONTRASENA);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(CORREO_USUARIO));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            message.setSubject("Código de Verificación");
            message.setText("Tu código de verificación es: " + codigo);

            Transport.send(message);
            System.out.println("Correo electrónico enviado correctamente.");

        } catch (MessagingException e) {
            e.printStackTrace(); // Manejo de errores
        }
    }
    public void showLayoutPassword() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/projectgui/RecuperacionPass.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        setPrimaryStage(primaryStage);
        primaryStage.setTitle("INICIO DE SESION");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void actualizarPassUser(){
        if(!txtPassword1.getText().isEmpty() && !txtPassword2.getText().isEmpty()){
            if(txtPassword1.getText().equals(txtPassword2.getText())){
                try (Connection connection = DatabaseConnection.getConnection()) {
                    int idEmpleado = getIdEmpleadoByCorreo(email);
                    String passwordHash = BCrypt.hashpw(txtPassword2.getText(), BCrypt.gensalt());
                    String query = "UPDATE tbl_usuarios SET password = ? WHERE idEmpleado = ?";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setString(1, passwordHash);
                        statement.setInt(2, idEmpleado);
                        statement.executeUpdate();
                    }
                    //primaryStage.close();
                    System.out.println(txtPassword1.getText().toString());
                    System.out.println(idEmpleado);
                    showLogin();
                } catch (SQLException e) {
                    e.printStackTrace(); // Manejo de errores
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                mostrarError("LA CONTRASENA NO COINCIDEN");
            }
        }else{
            mostrarError("CAMPOS VACIOS");
        }
    }
    public int getIdEmpleadoByCorreo(String correo) throws SQLException {
        String query = "SELECT idEmpleado FROM tbl_empleados WHERE correo = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, correo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("idEmpleado");
            } else {
                return -1;  // Retorna un valor negativo si no se encuentra el empleado
            }
        }
    }
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

}


