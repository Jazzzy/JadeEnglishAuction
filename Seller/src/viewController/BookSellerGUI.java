package viewController;

import jade.BookSellerAgent;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.concurrent.CountDownLatch;

public class BookSellerGUI extends Application {

    private static BookSellerAgent bookSellerAgent;
    private Controller controller;

    public void setBookSellerAgent(BookSellerAgent bsa){
        this.bookSellerAgent = bsa;
    }

    public static void launchThis() {
        launch(new String[1]);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("principal.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Seller: "+bookSellerAgent.getLocalName());
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.setResizable(false);
        primaryStage.show();
        this.controller =loader.getController();

        this.controller.setAgent(this.bookSellerAgent);


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                bookSellerAgent.doDelete();
            }
        });

    }

    public Controller getController(){
        return this.controller;
    }



    public void dispose() {
        //TODO
    }


}
