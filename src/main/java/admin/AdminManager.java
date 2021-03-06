package admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import server.ServerManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminManager extends Application {

    private static Stage primaryStage;
    private static Scene serverStoppedScene, normalServerScene, maintenanceServerScene;

    private ServerState serverState = new ServerState();
    private ServerManager serverManager = new ServerManager();

    public void startProgram() {
        serverState.updateResources();
        launch();
        serverManager.closeServer();
    }

    public void startServer() {
        serverState.updateResources();
        serverManager.closeServer();
        serverManager.setHTMLFiles(serverState.getResourcesMap());
        serverManager.setServerOnNormalRunningMode();
        restoreErrorMessages();
        primaryStage.setScene(normalServerScene);
    }

    public void setServerOnMaintenanceMode() {
        serverManager.setServerOnMaintenanceMode();
        restoreErrorMessages();
        primaryStage.setScene(maintenanceServerScene);
    }

    public void setServerOnNormalMode() {
        serverManager.setServerOnNormalRunningMode();
        restoreErrorMessages();
        primaryStage.setScene(normalServerScene);
    }

    public void stopServer() {
        serverManager.closeServer();
        restoreErrorMessages();
        primaryStage.setScene(serverStoppedScene);
    }

    public void updatePort() {
        restoreErrorMessages();
        TextField portField = (TextField) serverStoppedScene.lookup("#serverPortField");
        String portVal = portField.getText();
        portField.setText("");
        if (!portIsValid(portVal)) {
            displayErrorMessage("The Value For The Port Is Not Valid");
            return;
        }
        int newPort = Integer.parseInt(portVal);
        serverManager.setPort(newPort);

        Label portLabel = (Label) normalServerScene.lookup("#normalModePortDisplay");
        Label portLabel2 = (Label) maintenanceServerScene.lookup("#maintenanceModePortDisplay");
        portLabel.setText(Integer.toString(newPort));
        portLabel2.setText(Integer.toString(newPort));

        TextField textField1 = (TextField) maintenanceServerScene.lookup("#nonEditableTextField1");
        TextField textField2 = (TextField) normalServerScene.lookup("#normalModePortField");
        textField1.setPromptText(Integer.toString(newPort));
        textField2.setPromptText(Integer.toString(newPort));
        portField.setPromptText(Integer.toString(newPort));
    }

    public void changeRootDirectory(boolean isFromServerStoppedScene) {
        restoreErrorMessages();
        TextField textField1 = (TextField) serverStoppedScene.lookup("#rootDirectoryField");
        TextField textField2 = (TextField) maintenanceServerScene.lookup("#rootDirectoryField");
        TextField textField3 = (TextField) normalServerScene.lookup("#normalModeRootDirectoryField");
        String newRootDirectory = isFromServerStoppedScene ? textField1.getText() : textField2.getText();
        textField1.setText("");
        textField2.setText("");
        String check = serverState.pathIsCorrect(newRootDirectory, true);
        if (!check.equals("OK")) {
            displayErrorMessage(check);
            return;
        }
        if (!newRootDirectory.endsWith("\\")) {
            newRootDirectory += "\\";
        }
        textField1.setPromptText(newRootDirectory);
        textField2.setPromptText(newRootDirectory);
        textField3.setPromptText(newRootDirectory);
        serverManager.setRootDirectory(newRootDirectory);
        serverState.setRootDirectory(newRootDirectory);
        serverState.updateResources();
        serverManager.setHTMLFiles(serverState.getResourcesMap());
    }

    public void changeMaintenanceDirectory(boolean isFromServerStoppedScene) {
        restoreErrorMessages();
        TextField textField1 = (TextField) serverStoppedScene.lookup("#maintenanceDirectoryField");
        TextField textField2 = (TextField) maintenanceServerScene.lookup("#maintenanceDirectoryField");
        TextField textField3 = (TextField) normalServerScene.lookup("#normalModeRootMaintenanceField");
        String newMaintenanceDirectory = isFromServerStoppedScene ? textField1.getText() : textField3.getText();
        textField1.setText("");
        textField3.setText("");
        String check = serverState.pathIsCorrect(newMaintenanceDirectory, false);
        if (!check.equals("OK")) {
            displayErrorMessage(check);
            return;
        }
        if (!newMaintenanceDirectory.endsWith("\\")) {
            newMaintenanceDirectory += "\\";
        }
        textField1.setPromptText(newMaintenanceDirectory);
        textField2.setPromptText(newMaintenanceDirectory);
        textField3.setPromptText(newMaintenanceDirectory);
        serverManager.setMaintenanceDirectory(newMaintenanceDirectory);
        serverState.setMaintenanceDirectory(newMaintenanceDirectory);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Java Servers");
        Parent rootStopped = FXMLLoader.load(getClass().getResource("serverStopped.fxml"));
        Parent rootNormalMode = FXMLLoader.load(getClass().getResource("serverNormalMode.fxml"));
        Parent rootMaintenanceMode = FXMLLoader.load(getClass().getResource("serverMaintenanceMode.fxml"));
        serverStoppedScene = new Scene(rootStopped);
        normalServerScene = new Scene(rootNormalMode);
        maintenanceServerScene = new Scene(rootMaintenanceMode);
        restoreErrorMessages();
        primaryStage.setScene(serverStoppedScene);
        primaryStage.show();
    }

    public void restoreErrorMessages() {
        Label errorMessage1 = (Label) serverStoppedScene.lookup("#errorMessageStoppedScene");
        Label errorMessage2 = (Label) maintenanceServerScene.lookup("#errorMessageMaintenanceModeScene");
        Label errorMessage3 = (Label) normalServerScene.lookup("#errorMessageNormalModeScene");

        errorMessage1.setText("");
        errorMessage2.setText("");
        errorMessage3.setText("");
    }

    public void displayErrorMessage(String message) {
        Label errorMessage1 = (Label) serverStoppedScene.lookup("#errorMessageStoppedScene");
        Label errorMessage2 = (Label) maintenanceServerScene.lookup("#errorMessageMaintenanceModeScene");
        Label errorMessage3 = (Label) normalServerScene.lookup("#errorMessageNormalModeScene");

        errorMessage1.setText(message);
        errorMessage2.setText(message);
        errorMessage3.setText(message);
    }

    public boolean portIsValid(String port) {
        try {
            int intValPort = Integer.parseInt(port);
            return (intValPort >= 1000 && intValPort <= 10000);
        } catch (NumberFormatException e) {
            return false; //String is not an Integer
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setMaintenanceServerScene(Scene maintenanceServerScene) {
        AdminManager.maintenanceServerScene = maintenanceServerScene;
    }

    public static void setNormalServerScene(Scene normalServerScene) {
        AdminManager.normalServerScene = normalServerScene;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        AdminManager.primaryStage = primaryStage;
    }

    public void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public void setServerState(ServerState serverState) {
        this.serverState = serverState;
    }

    public static void setServerStoppedScene(Scene serverStoppedScene) {
        AdminManager.serverStoppedScene = serverStoppedScene;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public static Scene getMaintenanceServerScene() {
        return maintenanceServerScene;
    }

    public static Scene getNormalServerScene() {
        return normalServerScene;
    }

    public static Scene getServerStoppedScene() {
        return serverStoppedScene;
    }
}
