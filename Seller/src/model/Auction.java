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
    private boolean ended;

    public Auction(Integer id, Book item, float reservePrice, float increment, float startingPrice) {
        this.id = id;
        this.item = item;
        this.ended = false;
        item.removeStock();
        this.reservePrice = reservePrice;
        this.increment = increment;
        this.currentPrice = startingPrice;
        this.log = " <h2>Id: [" + this.id + "] Auction for the book: " + this.getItem().getTitle() + "</h2>\n" +
                "<h3> Started with a price of " + this.getCurrentPrice() + "</h3>\n" +
                "<h3> Increments of " + this.getIncrement() + "</h3>\n" +
                "<h3> And a reserve price of " + this.getReservePrice() + "</h3>\n";

    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public void endAuctionSuccess() {//TODO
        this.setEnded(true);
    }


    public void endAuctionFail() {
        this.setEnded(true);
        this.item.addStock();
    }

    public void addToLog(String msg) {
        this.log += ("<p>" + msg + "</p>\n");
    }

    public void makeIncrement() {
        this.currentPrice += this.increment;
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
