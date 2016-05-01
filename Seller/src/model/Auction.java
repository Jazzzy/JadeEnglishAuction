package model;

/**
 * Created by gladi on 27/04/2016.
 */
public class Auction {

    private Integer id;
    private Book item;
    private float reservePrice;
    private float currentPrice;
    private float increment;
    private String log;

    public Auction(Integer id, Book item, float reservePrice, float increment, float startingPrice) {
        this.id = id;
        this.item = item;
        item.removeStock();
        this.reservePrice = reservePrice;
        this.increment = increment;
        this.currentPrice = startingPrice;
        this.log = " <h2>Id: [" + this.id + "] Auction for the book: " + this.getItem().getTitle() + "</h2>\n" +
                "<h3> Started with a price of " + this.getCurrentPrice() + "</h3>\n" +
                "<h3> Increments of " + this.getIncrement() + "</h3>\n" +
                "<h3> And a reserve price of " + this.getReservePrice() + "</h3>\n";

    }

    public void endAuctionSuccess() {//TODO

    }

    public void endAuctionFail() {
        this.item.addStock();
    }

    public void addToLog(String msg) {
        this.log += ("<p>" + msg + "</p>\n");
    }

    public float getIncrement() {
        return increment;
    }

    public String getLog() {
        return log;
    }

    public Integer getId() {
        return id;
    }

    public Book getItem() {
        return item;
    }

    public float getReservePrice() {
        return reservePrice;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }
}
