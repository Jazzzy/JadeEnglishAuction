package model;

/**
 * Created by gladi on 27/04/2016.
 */
public class Auction {

    private Integer id;
    private Book item;
    private float reservePrice;
    private float currentPrice;


    public Auction(Integer id, Book item, float reservePrice) {
        this.id = id;
        this.item = item;
        this.reservePrice = reservePrice;
        this.currentPrice = 0f;
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
