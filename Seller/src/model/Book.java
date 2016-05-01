package model;

/**
 * Created by gladi on 27/04/2016.
 */
public class Book {

    private String title;
    private Integer stock;
    private float startingPrice;

    public Book(String title) {
        this.title = title;
        this.stock = 1;
    }

    public synchronized void addStock() {
        this.stock++;
    }

    public synchronized void removeStock() {
        this.stock--;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStock() {
        return stock;
    }
}
