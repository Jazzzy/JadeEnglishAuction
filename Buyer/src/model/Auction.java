package model;

/**
 * Created by gladi on 27/04/2016.
 */
public class Auction {

    private String conversationId;
    private Book item;
    private String log;
    private boolean ended;
    private boolean won;
    //TODO add bolean ended here

    public Auction(String conversationId, Book item) {
        this.conversationId = conversationId;
        this.item = item;
        this.ended = false;
        this.won = false;
        this.log = " <h2>Conversation Id: [" + this.conversationId + "] Auction for the book: " + this.getItem().getTitle() + "</h2>\n" +
                "<h3> My maximum price to pay is " + this.item.getMaxPriceToPay() + "</h3>\n";
    }

    public boolean isWon() {
        return won;
    }

    public void endAuctionSuccess() {//TODO
        this.won = true;
    }

    public void endAuctionFail() {
        this.won = false;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
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
