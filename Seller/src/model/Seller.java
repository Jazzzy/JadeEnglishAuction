package model;

import viewController.Controller;

import java.util.ArrayList;

/**
 * Created by gladi on 27/04/2016.
 */
public class Seller {

    private Integer idIterator;
    private ArrayList<Book> bookStock;
    private ArrayList<Auction> currentAuctions;

    public Seller() {
        this.bookStock = new ArrayList<>();
        this.currentAuctions = new ArrayList<>();
        idIterator = 1;
    }

    public ArrayList<Book> getBookStock() {
        return bookStock;
    }

    public ArrayList<Auction> getCurrentAuctions() {
        return currentAuctions;
    }

    private synchronized Integer getAndAddIdIterator() {
        this.idIterator++;
        return (this.idIterator - 1);
    }

//Functions to work with the current auctions taking place

    public synchronized boolean removeAuctionById(Integer id) {
        Auction aux = this.getAuctionById(id);
        if (aux != null) {
            this.currentAuctions.remove(aux);
            Controller.showInfo("Auction removed successfully");
            return true;
        } else {
            Controller.showError("There is no auction with this id to be removed");
            return false;
        }
    }

    public synchronized boolean addAuction(Book book, float reservePrice, float increment, float startingPrice) {
        if (isThereAuctionFor(book)) {
            Controller.showInfo("You are creating an auction for a book that is already in another auction");
            return false;
        }
        Auction auction = new Auction(this.getAndAddIdIterator(), book, reservePrice, increment, startingPrice);
        this.currentAuctions.add(auction);
        Controller.showInfo("Auction added successfully");
        return true;
    }

    public boolean isThereAuctionFor(Book book) {
        for (Auction a : this.currentAuctions) {
            if (a.getItem().getTitle().equals(book.getTitle()) && !a.isEnded()) {
                return true;
            }
        }
        return false;
    }

    public Auction getAuctionById(Integer id) {
        for (Auction a : this.currentAuctions) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    public Auction getAuctionByTitle(String title) {
        for (Auction a : this.currentAuctions) {
            if (a.getItem().getTitle().equals(title)) {
                return a;
            }
        }
        return null;
    }


//Functions to work with the stock of books in the seller

    public boolean removeBook(Book book) {
        if (isThereBook(book)) {
            Book aux = this.getBookByName(book.getTitle());

            if (aux.getStock() <= 1) {
                this.bookStock.remove(aux);
                Controller.showInfo("Removed the book from the stock because it had one unit left");
            } else {
                aux.removeStock();
                Controller.showInfo("Removed one unit from the stock");
            }
            return true;
        } else {
            Controller.showError("The book cannot be removed because it does not exist");
            return false;
        }
    }

    public boolean addBook(Book book) {
        if (isThereBook(book)) {
            this.getBookByName(book.getTitle()).addStock();
            Controller.showInfo("A book with this name is already registered, added one unit to stock");
            return true;
        }
        this.bookStock.add(book);
        Controller.showInfo("Book added to the stock of books");
        return true;
    }

    public boolean isThereBook(Book book) {
        for (Book b : this.bookStock) {
            if (b.getTitle().equals(book.getTitle())) {
                return true;
            }
        }
        return false;
    }

    public Book getBookByName(String name) {
        for (Book b : this.bookStock) {
            if (b.getTitle().equals(name)) {
                return b;
            }
        }
        return null;
    }


}
