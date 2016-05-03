package jade;

/*****************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * <p>
 * GNU Lesser General Public License
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 *****************************************************************/


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Auction;
import model.Book;
import model.Seller;
import viewController.BookSellerGUI;
import viewController.Controller;

import java.util.ArrayList;
import java.util.Hashtable;


public class BookSellerAgent extends Agent { //TODO:  Meter no id de conversacion book-selling.IDDEAuction
    // The catalogue of books for sale (maps the title of a book to its price)
    private Hashtable catalogue;
    // The GUI by means of which the user can add books in the catalogue
    private static BookSellerGUI myGui;

    private Controller controller;

    private Seller seller;

    public Seller getSeller() {
        return seller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    // Put agent initializations here
    protected void setup() {

        this.seller = new Seller();

        // Create the catalogue
        catalogue = new Hashtable();

        // Create and show the GUI
        myGui = new BookSellerGUI();
        myGui.setBookSellerAgent(this);
        new Thread() {
            @Override
            public void run() {
                myGui.launchThis();
            }
        }.start();


        // Register the book-selling service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-auctioning");
        sd.setName("JADE-book-auctioning");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add the behaviour serving queries from buyer agents
        addBehaviour(new AskingForBook());

    }

    // Put agent clean-up operations here
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Close the GUI
        myGui.dispose();
        // Printout a dismissal message
        System.out.println("Seller-agent " + getAID().getName() + " terminating.");
    }

    /**
     * This is invoked by the GUI when the user adds a new book for sale
     */
    public void addBookToCatalog(final String title) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                seller.addBook(new Book(title));
                System.out.println(title + " inserted into catalogue");
                getController().updateListOfBooksRemote();
            }
        });
    }

    public void addBookToAuction(final String title, float reservePrice, float increment, float startingPrice) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                Book book = seller.getBookByName(title);
                if (book == null) {
                    Controller.showError("Not such book with that name registered, you need to add it first");
                    return;
                }
                if (book.getStock() <= 0) {
                    Controller.showError("Not enough stock for that book");
                    return;
                }
                if (seller.addAuction(book, reservePrice, increment, startingPrice)) {
                    getController().updateListOfAuctionsRemote();
                    myAgent.addBehaviour(new Auctioning(myAgent, seller.getAuctionByTitle(book.getTitle())));
                }

                return;
            }
        });
    }

    /**
     * Inner class OfferRequestsServer.
     * This is the behaviour used by Book-seller agents to serve incoming requests
     * for offer from buyer agents.
     * If the requested book is in the local catalogue the seller agent replies
     * with a PROPOSE message specifying the price. Otherwise a REFUSE message is
     * sent back.
     */
    private class AskingForBook extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // CFP Message received. Process it
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();
                Auction auction = seller.getCurrentAuctionByTitle(title);
                System.out.println("Looking for an auction for " + title);
                if (auction != null ) {
                    // The requested book is available for sale. Reply with the price
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(Float.toString(auction.getCurrentPrice()) + "%auction-" + auction.getId() + "%" + auction.getItem().getTitle());
                } else {
                    // The requested book is NOT available for sale.
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }  // End of inner class OfferRequestsServer


    private class Auctioning extends TickerBehaviour {

        public Auction auction;

        public AID winner = null;

        public Auctioning(Agent agent, Auction auction) {
            super(agent, 10000);
            this.auction = auction;
        }

        public void finishAuction() {


            if (winner == null) {
                this.auction.addToLog("On tick " + this.getTickCount() + " we have finished the auction with no winner");
                auction.endAuctionFail();
                this.stop();
                return;
            }

            if (auction.getCurrentPrice() < auction.getReservePrice()) {
                this.auction.addToLog("On tick " + this.getTickCount() + " we have finished the auction with no winner because we didn't surpass the reserve price");
                auction.endAuctionFail();
                this.stop();
                return;
            }

            ACLMessage informWin = new ACLMessage(ACLMessage.CONFIRM);
            informWin.addReceiver(this.winner);
            informWin.setConversationId("auction-" + auction.getId());
            informWin.setContent("You won%" + auction.getItem() + "%for the price%" + auction.getCurrentPrice());
            myAgent.send(informWin);

            this.auction.addToLog("On tick " + this.getTickCount() + " we have finished the auction with [" + this.winner.getLocalName() + "] as a winner and he paid " + auction.getCurrentPrice());
            auction.endAuctionSuccess();
            this.stop();
            return;

        }


        public void onTick() {

            ArrayList<AID> listOfBuyers = new ArrayList<>();

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE).MatchConversationId("auction-" + auction.getId());

            ACLMessage msg = myAgent.receive(mt);
            if (msg == null) { //No proposes for this auction
                if (getTickCount() > 2) { //If the auction had not a lot of ticks, we do not end it
                    finishAuction();
                } else {
                    this.auction.addToLog("On tick " + this.getTickCount() + " we didn't have any replies, we dont end the auction because it just started");
                }
                return;
            } else {

                this.winner = msg.getSender();//We pick the first and take it as the current winner

                listOfBuyers.add(msg.getSender()); //We add it here in case we have to send another call for proposal

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContent(String.valueOf(auction.getCurrentPrice()));
                myAgent.send(reply); //We tell him he is the current winner

                this.auction.addToLog("On tick " + this.getTickCount() + " we have selected [" + this.winner.getLocalName() + "] as the current winner since he sent the first proposal");

                msg = myAgent.receive(mt); //We keep getting all the other proposes
                if (msg == null) { //if we only had one propose, this will be the winner and we finish the auction
                    this.auction.addToLog("On tick " + this.getTickCount() + " we only had one reply");
                    finishAuction();
                    return;
                } else {
                    while (msg != null) { //And responding telling them that they are not in the first place
                        reply = msg.createReply();
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        reply.setContent(String.valueOf(auction.getCurrentPrice()));
                        myAgent.send(reply);
                        listOfBuyers.add(msg.getSender());
                        this.auction.addToLog("On tick " + this.getTickCount() + " we have received a proposal from [" + this.winner.getLocalName() + "] but it was not the first one");
                        msg = myAgent.receive(mt);
                    }

                }

                this.auction.makeIncrement();
                //Now we send the next cfp to all the current buyers
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                cfp.setContent(Float.toString(auction.getCurrentPrice()));
                cfp.setConversationId("auction-" + auction.getId());
                //cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value

                for (AID aid : listOfBuyers) {
                    cfp.addReceiver(aid);
                }
                myAgent.send(cfp);

            }

            controller.updateListOfAuctionsRemote();

        }
    }

    /**
     * Inner class PurchaseOrdersServer.
     * This is the behaviour used by Book-seller agents to serve incoming
     * offer acceptances (i.e. purchase orders) from buyer agents.
     * The seller agent removes the purchased book from its catalogue
     * and replies with an INFORM message to notify the buyer that the
     * purchase has been sucesfully completed.
     */
    private class PurchaseOrdersServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // ACCEPT_PROPOSAL Message received. Process it
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();

                Integer price = (Integer) catalogue.remove(title);
                if (price != null) {
                    reply.setPerformative(ACLMessage.INFORM);
                    System.out.println(title + " sold to agent " + msg.getSender().getName());
                } else {
                    // The requested book has been sold to another buyer in the meanwhile .
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }  // End of inner class OfferRequestsServer
}
