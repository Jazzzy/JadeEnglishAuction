package model;

/**
 * Created by gladi on 27/04/2016.
 */
public class Auction {

    private String conversationId;
    private Book item;
    private String log;
    //TODO add bolean ended here

    public Auction(String conversationId, Book item) {
        this.conversationId = conversationId;
        this.item = item;
        this.log = " <h2>Conversation Id: [" + this.conversationId + "] Auction for the book: " + this.getItem().getTitle() + "</h2>\n" +
                "<h3> My maximum price to pay is " + this.item.getMaxPriceToPay() + "</h3>\n";
    }

    public void endAuctionSuccess() {//TODO

    }

    public void endAuctionFail() {

    }

    public void addToLog(String msg) {
        this.log += ("<p>" + msg + "</p>\n");
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
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
