package model;

import viewController.Controller;

import java.util.ArrayList;

/**
 * Created by gladi on 27/04/2016.
 */
public class Buyer {

    private ArrayList<Book> wantedBooks;
    private ArrayList<Auction> currentAuctions;

    public Buyer() {
        this.wantedBooks = new ArrayList<>();
        this.currentAuctions = new ArrayList<>();
    }

    public ArrayList<Auction> getCurrentAuctions() {
        return currentAuctions;
    }


    public ArrayList<Book> getBooksWithNoAuction() {
        ArrayList<Book> list = new ArrayList<>();

        for (Book book : this.getWantedBooks()) {
            if (!isThereAuctionFor(book)) {
                list.add(book);
            }
        }
        if (list.size() == 0) {
            return null;
        } else {
            return list;
        }

    }

//Functions to work with the current auctions taking place

    public synchronized boolean removeAuctionByConversationId(String id) {
        Auction aux = this.getAuctionByConversationId(id);
        if (aux != null) {
            this.currentAuctions.remove(aux);
            Controller.showInfo("Auction removed successfully");
            return true;
        } else {
            Controller.showError("There is no auction with this id to be removed");
            return false;
        }
    }

    public synchronized boolean addAuction(Book book, String conversationId) {
        if (isThereAuctionFor(book)) {
            Controller.showInfo("You are creating an auction for a book that is already in another auction");
            return false;
        }
        Auction auction = new Auction(conversationId, book);
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

    public Auction getAuctionByConversationId(String conversationId) {
        for (Auction a : this.currentAuctions) {
            if (a.getConversationId().equals(conversationId)) {
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

    public boolean removeBookByTitle(String book) {

        Book aux = this.getBookByName(book);
        if (aux != null) {
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
