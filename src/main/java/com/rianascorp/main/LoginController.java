package com.rianascorp.main;

import com.rianascorp.utils.Shower;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.kordamp.ikonli.javafx.FontIcon;
import rianaLibraries.validation.Language;
import rianaLibraries.validation.RianaValidationRegex;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField userNameTextBox;
    @FXML private PasswordField passWordTextBox;
    @FXML private Label loginErrorLabel;
    @FXML private Hyperlink serverConnectionParameterLink;
    @FXML private FontIcon gearFontIcon;
    @FXML private Button CloseButton;
    @FXML private Button OkButton;




    // @FXML private FontIcon myFontIcon;
    public static  String USER;



    public static EntityManagerFactory emf;

    public static volatile EntityManagerFactory ENTITYMANAGERFACTORY;
    public static volatile StandardServiceRegistry registry;
    public static final Object lock = new Object(); // For thread-safe initialization

    ServerIpAddress serverConnection;

    private static final String JDBC_URL_TEMPLATE;
    private static final String DEFAULT_HOST = "localhost";
    private Shower shower;

    // Static initializer - runs ONCE when class is loaded
    static {
        String dbName = null;
        String template = null;

        try {
            // Load Hibernate configuration
            org.hibernate.cfg.Configuration config = new org.hibernate.cfg.Configuration().configure();
            String fullUrl = config.getProperty("hibernate.connection.url");

            // Validate URL exists
            if (fullUrl == null || fullUrl.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "hibernate.connection.url not found in hibernate.cfg.xml"
                );
            }

            // Extract database name
            if (!fullUrl.contains("/")) {
                throw new IllegalArgumentException(
                        "Invalid URL format (no '/' found): " + fullUrl
                );
            }

            String[] parts = fullUrl.split("/");
            dbName = parts[parts.length - 1].split("\\?")[0]; // Remove query params

            if (dbName == null || dbName.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Could not extract database name from URL: " + fullUrl
                );
            }

            // Create the template
            template = "jdbc:postgresql://%s/" + dbName;

        } catch (Exception e) {
            // Create a detailed error message
            String errorMsg = String.format(
                    "==========================================================\n" +
                            "FATAL CONFIGURATION ERROR - APPLICATION CANNOT START\n" +
                            "==========================================================\n" +
                            "Failed to read database configuration from hibernate.cfg.xml\n\n" +
                            "Error: %s\n\n" +
                            "Please ensure:\n" +
                            "1. hibernate.cfg.xml exists in src/main/resources/\n" +
                            "2. It contains a valid <property name=\"connection.url\">\n" +
                            "3. The URL format is correct (jdbc:postgresql://host/database)\n" +
                            "==========================================================",
                    e.getMessage()
            );

            // Log to console
            System.err.println(errorMsg);
            e.printStackTrace();

            // Show GUI alert if possible
            if (javafx.application.Platform.isFxApplicationThread()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("FATAL CONFIGURATION ERROR");
                alert.setHeaderText("Cannot read database configuration");
                alert.setContentText(errorMsg);
                alert.showAndWait();
            }

            // ABORT! Throw an error that prevents the class from loading
            throw new ExceptionInInitializerError(errorMsg);
        }

        // Assign to final fields (only reached if no exception)
        JDBC_URL_TEMPLATE = template;
    }




    private static String lastUsedUrl = null;  // ← Track full URL

    public void initializeo(){
        serverConnection=new ServerIpAddress();
        shower =new Shower();
    }

    public void validation(Validator  validator){
        OkButton.disableProperty().bind(validator.containsErrorsProperty());
        RianaValidationRegex rianaValidationRegex=new RianaValidationRegex();
        rianaValidationRegex.createCheckBlank("userName",userNameTextBox,loginErrorLabel, Language.MALAGASY,validator);
        rianaValidationRegex.createCheckBlank("passWord",passWordTextBox,loginErrorLabel, Language.MALAGASY,validator);
    }


    public void OKButtonAction() {
        Validator validator = new Validator();
        validation(validator);

        if (!validator.validate()) {
            return; // don't proceed if validation fails
        }

        try {
            // --- DYNAMIC CONNECTION SETTINGS ---
            String host = DEFAULT_HOST;
            if (serverConnection != null && serverConnection.getIp() != null
                    && !serverConnection.getIp().trim().isEmpty()) {
                host = serverConnection.getIp().trim();
            }

            String jdbcUrl = String.format(JDBC_URL_TEMPLATE, host);
            buildEntityManagerFactory(jdbcUrl,userNameTextBox.getText(),passWordTextBox.getText());



            EntityManager em = ENTITYMANAGERFACTORY.createEntityManager();


            em.getTransaction().begin();


            Query loginQuery = em.createNativeQuery(
                    "SELECT usename, passwd FROM pg_catalog.pg_user WHERE usename = :user", Login.class);
            loginQuery.setParameter("user", userNameTextBox.getText().trim());

            Login login = (Login) loginQuery.getSingleResult();
            em.getTransaction().commit();
            em.close();

            USER = login.getUsername();

            FXMLLoader homeLoader = shower.showEdit("/main/cat.fxml");
            CatsController cc = homeLoader.getController();
            cc.initializeo();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de connexion");
            alert.setHeaderText("Impossible de se connecter à la base de données");
            alert.setContentText(ex.getMessage());
            alert.show();
            ex.printStackTrace();
        }
    }

    private void buildEntityManagerFactory(String jdbcUrl, String username, String password) {
        synchronized (lock) {
            // --- OPTIMIZATION: Reuse if URL hasn't changed ---
            if (ENTITYMANAGERFACTORY != null &&
                    jdbcUrl != null &&
                    jdbcUrl.equals(lastUsedUrl)) {
                return; // Reuse existing factory
            }

            // Close old
            if (ENTITYMANAGERFACTORY != null && ENTITYMANAGERFACTORY.isOpen()) {
                ENTITYMANAGERFACTORY.close();
            }
            if (registry != null) {
                StandardServiceRegistryBuilder.destroy(registry);
            }

            // --- HIBERNATE 6: Build registry ---
            registry = new StandardServiceRegistryBuilder()
                    .configure() // loads src/main/resources/hibernate.cfg.xml
                    .applySetting("hibernate.connection.url", jdbcUrl)
                    .applySetting("hibernate.connection.username", username)
                    .applySetting("hibernate.connection.password", password)
                    .build();

            MetadataSources sources = new MetadataSources(registry);

            // --- Build factory ---
            ENTITYMANAGERFACTORY = sources.buildMetadata()
                    .getSessionFactoryBuilder()
                    .build()
                    .unwrap(EntityManagerFactory.class);

            lastUsedUrl = jdbcUrl;  // ← Remember for next time
        }
    }


    public void CloseButtonAction() {
        synchronized (lock) {
            if (ENTITYMANAGERFACTORY != null && ENTITYMANAGERFACTORY.isOpen()) {
                ENTITYMANAGERFACTORY.close();
                ENTITYMANAGERFACTORY = null;
            }
            if (registry != null) {
                StandardServiceRegistryBuilder.destroy(registry);
                registry = null;
            }
        }
        Stage stage=(Stage)CloseButton.getScene().getWindow();
        stage.close();
    }

    public void serverConnectionParameterLinkAction() throws IOException {

        FXMLLoader editorLoader=new FXMLLoader(getClass().getResource("/main/server_config.fxml"));
        Stage stage=new Stage();
        Parent root=editorLoader.load();
        Scene scene=new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Paramètres de connexion");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        stage.centerOnScreen();
        ServerConnectionController sc=editorLoader.getController();
        sc.initializeo(serverConnection);

    }

}
