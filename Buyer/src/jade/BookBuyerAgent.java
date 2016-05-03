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
import model.Buyer;
import viewController.BookBuyerGUI;
import viewController.Controller;

import java.util.ArrayList;

public class BookBuyerAgent extends Agent {

    // The list of known seller agents
    private AID[] sellerAgents = null;

    private Controller controller;

    private static BookBuyerGUI myGui;

    private Buyer buyer;

    public Buyer getBuyer() {
        return buyer;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    // Put agent initializations here
    protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! Buyer-agent " + getAID().getName() + " is ready.");

        this.buyer = new Buyer();

        // Create and show the GUI
        myGui = new BookBuyerGUI();
        myGui.setBookSellerAgent(this);
        new Thread() {
            @Override
            public void run() {
                myGui.launchThis();
            }
        }.start();


        // Add a TickerBehaviour that updates seller agents every minute
        addBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                // Update the list of seller agents
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("book-auctioning");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    System.out.println("Found the following seller agents:");
                    sellerAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        sellerAgents[i] = result[i].getName();
                        System.out.println(sellerAgents[i].getName());
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });

        addBehaviour(new AskForAuctions(this));

        addBehaviour(new LookingForAuctions());

        addBehaviour(new respondToProposals());

    }


    // Put agent clean-up operations here
    protected void takeDown() {
        // Printout a dismissal message
        myGui.dispose();
        System.out.println("Buyer-agent " + getAID().getName() + " terminating.");
    }

    public void addWantedBook(final String title, final float maxPrice) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                buyer.addBook(new Book(title, maxPrice));
                System.out.println(title + " inserted into wanted books");
                getController().updateListOfBooksRemote();

                //TODO ask here for books

            }
        });
    }


    private class AskForAuctions extends TickerBehaviour {

        public AskForAuctions(Agent agent) {
            super(agent, 500);
        }

        @Override
        protected void onTick() {

            ArrayList<Book> list = buyer.getBooksWithNoAuction();
            if (list == null)
                return;

            if (sellerAgents == null || sellerAgents.length <= 0)
                return;

            for (Book book : list) {

                System.out.println("Looking for " + book.getTitle());

                ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
                msg.setContent(book.getTitle());
                for (int i = 0; i < sellerAgents.length; i++) {
                    System.out.println("Asking to " + sellerAgents[i]);
                    msg.addReceiver(sellerAgents[i]);
                }
                myAgent.send(msg);
            }
        }


    }

    private class LookingForAuctions extends CyclicBehaviour {

        public void action() {

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {

                System.out.println("Receiver inform from " + msg.getSender());
                System.out.println("Said: " + msg.getContent());

                String content = msg.getContent();
                String[] fragments = content.split("%");
                float price = Float.parseFloat(fragments[0]);
                String conversationId = fragments[1];
                String title = fragments[2];

                Book book = buyer.getBookByName(title);
                if (!buyer.isThereAuctionFor(book)) {
                    if (price < book.getMaxPriceToPay()) {
                        buyer.addAuction(book, conversationId);
                        Auction auction = buyer.getAuctionByConversationId(conversationId);

                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setConversationId(conversationId);
                        myAgent.send(reply);

                        auction.addToLog("Sended a propose to " + msg.getSender().getLocalName() + " Saying we accept the price: " + price);
                    }
                }

                controller.updateListOfAuctionsRemote();

            } else {
                block();
            }
        }
    }

    private class respondToProposals extends CyclicBehaviour {

        public void action() {

            MessageTemplate mtc = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
            ACLMessage confirm = myAgent.receive(mtc);
            if (confirm != null) {
                Auction auction = buyer.getAuctionByConversationId(confirm.getConversationId());
                if (auction != null) {
                    auction.addToLog("Received this from seller [" + confirm.getSender().getLocalName() + "]: " + confirm.getContent().split("%"));
                    String item = confirm.getContent().split("%")[1];
                    float priceToPay = Float.parseFloat(confirm.getContent().split("%")[3]);
                    auction.addToLog("We have won the auction for " + item + " and we need to pay " + priceToPay);

                    //buyer.removeAuctionByConversationId(confirm.getConversationId());
                    auction.setEnded(true);
                    auction.endAuctionSuccess();
                    buyer.removeBook(auction.getItem());

                }
                controller.updateListOfAuctionsRemote();
                return;
            }

            //System.out.println("Executing respond to proposals----------------------------------------------------------");

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                //System.out.println("Executing respond to proposals not null msg----------------------------------------------------------");
                Auction auction = buyer.getAuctionByConversationId(msg.getConversationId());
                //System.out.println(msg.getConversationId()+"----------------------------------------------------------");

                if (auction != null) {

                    String content = msg.getContent();
                    Float proposedPrice = Float.parseFloat(content);

                    auction.addToLog("Received a call for proposal from " + msg.getSender().getLocalName() + " with the price: " + proposedPrice);

                    if (proposedPrice <= auction.getItem().getMaxPriceToPay()) { //We can accept the proposal
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setConversationId(auction.getConversationId());
                        myAgent.send(reply);
                        auction.addToLog("Sended a propose to " + msg.getSender().getLocalName() + " Saying we accept the price: " + proposedPrice);
                    } else {
                        auction.addToLog("We do not accept the price of " + proposedPrice + " so we do not send a propose");
                        auction.setEnded(true);
                        auction.endAuctionFail();
                    }
                }

                controller.updateListOfAuctionsRemote();

            } else {
                block();
            }
        }
    }


    /**
     * Inner class RequestPerformer.
     * This is the behaviour used by Book-buyer agents to request seller
     * agents the target book.
     */
    /*
    private class RequestPerformer extends Behaviour {
        private AID bestSeller; // The agent who provides the best offer
        private int bestPrice;  // The best offered price
        private int repliesCnt = 0; // The counter of replies from seller agents
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;

        public void action() {
            switch (step) {
                case 0:
                    // Send the cfp to all sellers
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < sellerAgents.length; ++i) {
                        cfp.addReceiver(sellerAgents[i]);
                    }
                    cfp.setContent(targetBookTitle);
                    cfp.setConversationId("book-trade");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    // Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Reply received
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            // This is an offer
                            int price = Integer.parseInt(reply.getContent());
                            if (bestSeller == null || price < bestPrice) {
                                // This is the best offer at present
                                bestPrice = price;
                                bestSeller = reply.getSender();
                            }
                        }
                        repliesCnt++;
                        if (repliesCnt >= sellerAgents.length) {
                            // We received all replies
                            step = 2;
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
                    // Send the purchase order to the seller that provided the best offer
                    ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    order.addReceiver(bestSeller);
                    order.setContent(targetBookTitle);
                    order.setConversationId("book-trade");
                    order.setReplyWith("order" + System.currentTimeMillis());
                    myAgent.send(order);
                    // Prepare the template to get the purchase order reply
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                    step = 3;
                    break;
                case 3:
                    // Receive the purchase order reply
                    reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Purchase order reply received
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            // Purchase successful. We can terminate
                            System.out.println(targetBookTitle + " successfully purchased from agent " + reply.getSender().getName());
                            System.out.println("Price = " + bestPrice);
                            myAgent.doDelete();
                        } else {
                            System.out.println("Attempt failed: requested book already sold.");
                        }

                        step = 4;
                    } else {
                        block();
                    }
                    break;
            }
        }

        public boolean done() {
            if (step == 2 && bestSeller == null) {
                System.out.println("Attempt failed: " + targetBookTitle + " not available for sale");
            }
            return ((step == 2 && bestSeller == null) || step == 4);
        }
    }  // End of inner class RequestPerformer

    */
}
