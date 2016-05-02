package viewController;

import jade.BookBuyerAgent;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class BookBuyerGUI extends Application {

    private static BookBuyerAgent bookBuyerAgent;
    private Controller controller;

    public void setBookSellerAgent(BookBuyerAgent bsa){
        this.bookBuyerAgent = bsa;
    }

    public static void launchThis() {
        launch(new String[1]);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("principal.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Buyer: "+bookBuyerAgent.getLocalName());
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.setResizable(false);
        primaryStage.show();
        this.controller =loader.getController();
        this.controller.setAgent(this.bookBuyerAgent);
        this.controller.init();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                bookBuyerAgent.doDelete();
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
