package viewController;

import com.sun.javafx.stage.StageHelper;
import jade.BookSellerAgent;
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
    @FXML
    Button buttonTest;
    @FXML
    TextField textFieldTitle;
    @FXML
    TextField textFieldReservePrice;
    @FXML
    TextField textFieldStartingPrice;
    @FXML
    TextField textFieldIncrement;
    @FXML
    Button buttonAddAuction;
    @FXML
    ComboBox comboBoxBookSelected;
    @FXML
    ListView listViewListOfBooks;
    @FXML
    Button buttonAddBook;
    @FXML
    WebView webViewAuctionLog;


    //The Agent Class
    private static BookSellerAgent bookSellerAgent;

    public void setAgent(BookSellerAgent bookSellerAgent) {
        this.bookSellerAgent = bookSellerAgent;
    }

    //-------------------------------------------------------------DEBUG-------------------------------------------------------------
    public void onActionButtonTest() {
        this.updateListOfBooks();
        this.updateListOfAuctions();
        this.initGUI();
    }
    //-------------------------------------------------------------END DEBUG-------------------------------------------------------------

    public void init() {
        this.initGUI();

        bookSellerAgent.setController(this);
    }

    public void initGUI() {//TODO insert floats in the fields

        textFieldReservePrice.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals("")) {
                    if (newValue.matches("\\d*")) {
                        int value = Integer.parseInt(newValue);
                    } else {
                        textFieldReservePrice.setText(oldValue);
                    }
                }
            }
        });

        textFieldStartingPrice.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals("")) {
                    if (newValue.matches("\\d*")) {
                        int value = Integer.parseInt(newValue);
                    } else {
                        textFieldStartingPrice.setText(oldValue);
                    }
                }
            }
        });

        textFieldIncrement.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals("")) {
                    if (newValue.matches("\\d*")) {
                        int value = Integer.parseInt(newValue);
                    } else {
                        textFieldIncrement.setText(oldValue);
                    }
                }
            }
        });

        this.updateListOfBooks();
        this.updateListOfAuctions();

        comboBoxBookSelected.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Auction>() {
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

        this.bookSellerAgent.addBookToCatalog(title);
        this.updateListOfBooks();
    }

    @FXML
    private void onClicButtonAddAuction() {
        String title = this.textFieldTitle.getText();
        if (title == null || title.equals("")) {//We get the name from the selected item on the listView
            Book aux = (Book) listViewListOfBooks.getSelectionModel().getSelectedItem();
            if (aux == null)
                return;
            title = aux.getTitle();
        }


        if (this.textFieldReservePrice.getText().equals(""))
            return;
        float reservePrice = Float.parseFloat(textFieldReservePrice.getText());


        if (this.textFieldIncrement.getText().equals(""))
            return;
        float increment = Float.parseFloat(textFieldIncrement.getText());

        if (this.textFieldStartingPrice.getText().equals(""))
            return;
        float startingPrice = Float.parseFloat(textFieldStartingPrice.getText());

        this.bookSellerAgent.addBookToAuction(title, reservePrice, increment, startingPrice);

        this.updateListOfAuctions();
    }

    private void updateListOfBooks() {

        if (bookSellerAgent == null || bookSellerAgent.getSeller() == null || bookSellerAgent.getSeller().getBookStock() == null)
            return;

        ObservableList<Book> myObservableList4 = FXCollections.observableList(bookSellerAgent.getSeller().getBookStock());
        listViewListOfBooks.setItems(myObservableList4);
        listViewListOfBooks.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
            @Override
            public ListCell<Book> call(ListView<Book> p) {
                ListCell<Book> cell = new ListCell<Book>() {
                    @Override
                    protected void updateItem(Book t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getTitle() + " (" + t.getStock() + " in Stock)");
                        }
                    }
                };
                return cell;
            }
        });

    }

    private void updateListOfAuctions() {

        if (bookSellerAgent == null || bookSellerAgent.getSeller() == null || bookSellerAgent.getSeller().getCurrentAuctions() == null)
            return;

        ObservableList<Auction> myObservableList4 = FXCollections.observableList(bookSellerAgent.getSeller().getCurrentAuctions());
        comboBoxBookSelected.setItems(myObservableList4);
        comboBoxBookSelected.setCellFactory(new Callback<ListView<Auction>, ListCell<Auction>>() {
            @Override
            public ListCell<Auction> call(ListView<Auction> p) {
                ListCell<Auction> cell = new ListCell<Auction>() {
                    @Override
                    protected void updateItem(Auction t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText("Id: [" + t.getId() + "] " + t.getItem().getTitle() + " at price: " + t.getCurrentPrice());
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

    static boolean ret = false;

    public final static boolean showConfirmation(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setContentText(message);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ret = true;
                } else {
                    ret = false;
                }
            }
        });
        return ret;
    }


}