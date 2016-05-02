package model;

/**
 * Created by gladi on 27/04/2016.
 */
public class Auction {

    private Integer id;
    private Book item;
    private String log;

    public Auction(Integer id, Book item) {
        this.id = id;
        this.item = item;
        this.log = " <h2>Id: [" + this.id + "] Auction for the book: " + this.getItem().getTitle() + "</h2>\n" +
                "<h3> My maximum price to pay is " + this.item.getMaxPriceToPay() + "</h3>\n";

    }

    public void endAuctionSuccess() {//TODO

    }

    public void endAuctionFail() {

    }

    public void addToLog(String msg) {
        this.log += ("<p>" + msg + "</p>\n");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Book getItem() {
        return item;
    }

    public void setItem(Book item) {
        this.item = item;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
