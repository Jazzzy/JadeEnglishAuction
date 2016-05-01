package viewController;

import com.sun.javafx.stage.StageHelper;
import jade.BookBuyerAgent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Optional;

public class Controller {

    //GUI Elements



    //The Agent Class
    private static  BookBuyerAgent bookBuyerAgent;



    public void setAgent(BookBuyerAgent bookBuyerAgent) {
        this.bookBuyerAgent = bookBuyerAgent;
    }

    //DEBUG

    public void onActionButtonTest(){
    }

    //END DEBUG

    //Functions for showing info and errors to the user
    public final static void showError(String message) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(message);

                Stage scene = StageHelper.getStages().get(0);
                double x = scene.getX() + (scene.getWidth() / 2d - 200);
                double y = scene.getY() + (scene.getHeight() / 2d - 75);
                alert.setX(x);
                alert.setY(y);

                alert.showAndWait();

            }
        });
    }

    public final static void showInfo(String message) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Info");
                alert.setHeaderText(null);
                alert.setContentText(message);

                Stage scene = StageHelper.getStages().get(0);
                double x = scene.getX() + (scene.getWidth() / 2d - 200);
                double y = scene.getY() + (scene.getHeight() / 2d - 75);
                alert.setX(x);
                alert.setY(y);

                alert.showAndWait();
            }
        });
    }

    public final static boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }


}