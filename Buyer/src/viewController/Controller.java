package viewController;

import com.sun.javafx.stage.StageHelper;
import jade.BookBuyerAgent;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Auction;
import model.Book;

import java.util.Optional;

public class Controller {

    //GUI Elements
    //GUI Elements
    @FXML
    Button buttonTest;
    @FXML
    TextField textFieldTitle;
    @FXML
    TextField textFieldPriceToPay;
    @FXML
    Button buttonEnterInAuctions;
    @FXML
    ComboBox comboBoxAuctionSelected;
    @FXML
    ListView listViewListOfBooks;
    @FXML
    WebView webViewAuctionLog;



    //The Agent Class
    private static  BookBuyerAgent bookBuyerAgent;



    public void setAgent(BookBuyerAgent bookBuyerAgent) {
        this.bookBuyerAgent = bookBuyerAgent;
    }

    //DEBUG
    public void onActionButtonTest(){
    }
    //END DEBUG

    public void init() {
        this.initGUI();
        bookBuyerAgent.setController(this);
    }

    public void initGUI() {//TODO insert floats in the fields

        textFieldPriceToPay.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals("")) {
                    if (newValue.matches("\\d*")) {
                        int value = Integer.parseInt(newValue);
                    } else {
                        textFieldPriceToPay.setText(oldValue);
                    }
                }
            }
        });


        this.updateListOfBooks();
        this.updateListOfAuctions();

        comboBoxAuctionSelected.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Auction>() {
            @Override
            public void changed(ObservableValue<? extends Auction> arg0, Auction arg1, Auction arg2) {
                if (arg2 != null) {
                    //Show log in textArea
                    webViewAuctionLog.getEngine().loadContent(arg2.getLog());
                }
            }
        });
    }


    @FXML
    private void onClicButtonAddBook() {
        String title = this.textFieldTitle.getText();
        if (title == null || title.equals(""))
            return;

        if (this.textFieldPriceToPay.getText().equals(""))
            return;
        float price = Float.parseFloat(textFieldPriceToPay.getText());


        this.bookBuyerAgent.addWantedBook(title,price);
        //this.updateListOfBooks();
    }


    private void updateListOfBooks() {

        if (bookBuyerAgent== null || bookBuyerAgent.getBuyer() == null || bookBuyerAgent.getBuyer().getWantedBooks() == null)
            return;

        ObservableList<Book> myObservableList4 = FXCollections.observableList(bookBuyerAgent.getBuyer().getWantedBooks());
        listViewListOfBooks.setItems(myObservableList4);
        listViewListOfBooks.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
            @Override
            public ListCell<Book> call(ListView<Book> p) {
                ListCell<Book> cell = new ListCell<Book>() {
                    @Override
                    protected void updateItem(Book t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getTitle());
                        }
                    }
                };
                return cell;
            }
        });

    }

    private void updateListOfAuctions() {

        if (bookBuyerAgent == null || bookBuyerAgent.getBuyer() == null || bookBuyerAgent.getBuyer().getCurrentAuctions() == null)
            return;

        ObservableList<Auction> myObservableList4 = FXCollections.observableList(bookBuyerAgent.getBuyer().getCurrentAuctions());
        comboBoxAuctionSelected.setItems(myObservableList4);
        comboBoxAuctionSelected.setCellFactory(new Callback<ListView<Auction>, ListCell<Auction>>() {
            @Override
            public ListCell<Auction> call(ListView<Auction> p) {
                ListCell<Auction> cell = new ListCell<Auction>() {
                    @Override
                    protected void updateItem(Auction t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText( t.getItem().getTitle() + " with max price to pay of: " + t.getItem().getMaxPriceToPay());
                        }
                    }
                };
                return cell;
            }
        });

    }

    public void updateListOfBooksRemote(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateListOfBooks();
            }
        });
    }

    public void updateListOfAuctionsRemote(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateListOfBooks();
                updateListOfAuctions();
            }
        });
    }



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