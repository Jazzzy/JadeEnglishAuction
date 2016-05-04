package model;

/**
 * Created by gladi on 27/04/2016.
 */
public class Book {

    private String title;
    private float maxPriceToPay;
    private String log;

    public Book(String title, float maxPriceToPay) {
        this.title = title;
        this.maxPriceToPay = maxPriceToPay;
        this.log = " <h2>Auction for the book: " + this.getTitle() + "</h2>\n" +
                "<h3> My maximum price to pay is " + this.getMaxPriceToPay() + "</h3>\n";
    }

    public void addToLog(String msg) {
        this.log += ("<p>" + msg + "</p>\n");
    }

    public String getTitle() {
        return title;
    }

    public String getLog() {
        return log;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getMaxPriceToPay() {
        return maxPriceToPay;
    }

    public void setMaxPriceToPay(float maxPriceToPay) {
        this.maxPriceToPay = maxPriceToPay;
    }
}
