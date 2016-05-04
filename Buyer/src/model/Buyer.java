package model;

import viewController.Controller;

import java.util.ArrayList;

/**
 * Created by gladi on 27/04/2016.
 */
public class Buyer {

    private ArrayList<Book> wantedBooks;

    public Buyer() {
        this.wantedBooks = new ArrayList<>();
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
