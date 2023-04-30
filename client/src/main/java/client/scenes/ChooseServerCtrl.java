package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChooseServerCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane anchor;

    @FXML
    private TextField input;

    /**
     * The constructer for the controller
     * @param server the server
     * @param mainCtrl the mainContoller
     */
    @Inject
    public ChooseServerCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Connects to the server specified in the input field
     */
    public void connectToServer() {
        String address = input.getText();

        if (address.equals("")) {
            handleError("It looks like you didn't enter an address");
        } else {
            if (testConnection(address)) {
                System.out.println("Address ok");
                server.setServerAddress(address);
                mainCtrl.initWebsockets();
                this.mainCtrl.showOverview();
            } else {
                handleError("It looks like the server is not reachable");
            }
        }


    }

    /**
     * Displays an error message to the user
     *
     * @param message the message to be displayed in the error message
     */
    private static void handleError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("There was an error while trying to connect to the server");
        alert.setContentText(message);

        alert.showAndWait();
    }

    /**
     * tests if the address is reachable
     *
     * @param address the address to test
     * @return true if the address is reachable, false otherwise
     */
    private static boolean testConnection(String address) {
        try {
            URL url = new URL("http://" + address + "/cards");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            return HttpURLConnection.HTTP_OK == urlConnection.getResponseCode();
        } catch (IOException e) {
            return false;
        }
    }
}

