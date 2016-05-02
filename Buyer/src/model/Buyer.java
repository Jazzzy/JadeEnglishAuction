package model;

import viewController.Controller;

import java.util.ArrayList;

/**
 * Created by gladi on 27/04/2016.
 */
public class Buyer {

    private Integer idIterator;
    private ArrayList<Book> wantedBooks;
    private ArrayList<Auction> currentAuctions;

    public Buyer() {
        this.wantedBooks = new ArrayList<>();
        this.currentAuctions = new ArrayList<>();
        idIterator = 1;
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

    public synchronized boolean addAuction(Book book) {
        if (isThereAuctionFor(book)) {
            Controller.showInfo("You are creating an auction for a book that is already in another auction");
        }
        Auction auction = new Auction(this.getAndAddIdIterator(), book);
        this.currentAuctions.add(auction);
        Controller.showInfo("Auction added successfully");
        return true;
    }

    public boolean isThereAuctionFor(Book book) {
        for (Auction a : this.currentAuctions) {
            if (a.getItem().getTitle().equals(book.getTitle())) {
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


//Functions to work with the stock of books in the seller

    public boolean removeBook(Book book) {
        if (isThereBook(book)) {
            Book aux = this.getBookByName(book.getTitle());
            this.wantedBooks.remove(aux);
            return true;
        } else {
            Controller.showError("The book cannot be removed because it does not exist");
            return false;
        }
    }

    public boolean addBook(Book book) {
        if (isThereBook(book)) {
            Controller.showInfo("A book with this name is already registered");
            return false;
        }
        this.wantedBooks.add(book);
        Controller.showInfo("Book added to the stock of books");
        return true;
    }

    public ArrayList<Book> getWantedBooks() {
        return wantedBooks;
    }


    public boolean isThereBook(Book book) {
        for (Book b : this.wantedBooks) {
            if (b.getTitle().equals(book.getTitle())) {
                return true;
            }
        }
        return false;
    }

    public Book getBookByName(String name) {
        for (Book b : this.wantedBooks) {
            if (b.getTitle().equals(name)) {
                return b;
            }
        }
        return null;
    }


}
