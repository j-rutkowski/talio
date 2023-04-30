/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.records.ClientData;
import client.scenes.*;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Main extends Application{

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static ClientData clientData;

    /**
     * Runs a new client
     *
     * @param args console arguments
     * @throws URISyntaxException possible URI exception
     * @throws IOException possible IO exception
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        clientData = ClientData.getInstance();
        launch();
    }

    /**
     * Starts a new JavaFX application
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws IOException possible IO exception
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        var homeBoard = FXML.load(HomeBoardCtrl.class, "client", "scenes", "HomeBoard.fxml");
        var addCard = FXML.load(AddCardCtrl.class, "client", "scenes", "AddCard.fxml");
        var addList = FXML.load(AddListCtrl.class, "client", "scenes", "AddList.fxml");
        var chooseServer = FXML.load(ChooseServerCtrl.class,
                "client",
                "scenes",
                "ChooseServer.fxml");
        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        var multiBoard = FXML.load(MultiBoardCtrl.class, "client", "scenes", "MultiBoard.fxml");
        var addMultiBoard = FXML.load(AddBoardCtrl.class, "client", "scenes", "AddBoard.fxml");
        var tagOverview = FXML.load(TagOverviewCtrl.class, "client", "scenes", "TagOverview.fxml");
        var addTag = FXML.load(AddTagCtrl.class, "client", "scenes", "AddTag.fxml");
        var helper = FXML.load(HelperCtrl.class,"client","scenes","Helper.fxml");
        var cardOverview = FXML.load(CardOverviewCtrl.class,
                                            "client", "scenes", "CardOverview.fxml");
        mainCtrl.initialize(primaryStage,
                homeBoard,
                addCard,
                addList,
                chooseServer,
                multiBoard,
                addMultiBoard,
                tagOverview,
                addTag,
                helper,
                cardOverview
        );
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            exitWarning(primaryStage);
        });
    }
    /**
     * This method is meant to be used on closing button on the main stage.
     * It will pop a confirmation window before the user can safely quit.
     * @param stage this is the main stage
     */

    public void exitWarning(Stage stage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Closing the application");
        alert.setContentText("Are you sure you want to quit the application?");

        if(alert.showAndWait().get() == ButtonType.OK)
        {
            System.out.println("closed");
            clientData.saveToFile();
            stage.close();
        }
    }
}