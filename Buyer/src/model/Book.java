package model;

/**
 * Created by gladi on 27/04/2016.
 */
public class Book {

    private String title;
    private float maxPriceToPay;

    public Book(String title, float maxPriceToPay) {
        this.title = title;
        this.maxPriceToPay = maxPriceToPay;
    }

    public String getTitle() {
        return title;
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
