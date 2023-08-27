import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
/**
 * Server class
 *
 * <p>Purdue University -- CS18000 -- Spring 2023</p>
 *
 * @author Neel Patel - pate1666
 * @version April 10th 2023
 */
public class Server implements Runnable {
    /***server protocol
     * 1. read market and send array list to client
     * 2. recieves response on buying or selling
     */

    Socket socket;

    public Server(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) {



        try {

            int port = 4242;
            ServerSocket serverSocket = null;
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                Server server = new Server(socket);
                new Thread(server).start();


            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }



    } //end main

    @Override
    public void run() {

        ArrayList<Product> market = new ArrayList<>();
        BufferedReader br = null;
        PrintWriter pw = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        DataBase db = new DataBase();
        boolean sort;

        int buySell = -1;

        try {

            Server s = new Server(socket);
            System.out.println("Client Connected");
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            int decision;

            /***
             innitialization of readers
             ***/
            market = s.importProducts(market);

            oos.writeObject(market); //writes the innitial list of products to market class
            oos.flush();
            //check for buying or selling
            buySell = Integer.parseInt(br.readLine());

            if (buySell == 1) {
                String customerName;
                customerName = br.readLine();
                //scan in name

                //check if buyer picks to view purchase history, or sort
                decision = Integer.parseInt(br.readLine());

                if (decision == 3) {
                    String customerHistroy = "";
                    try {
                        customerHistroy = db.exportCustomerHistory(customerName);
                        pw.print(customerHistroy);
                        pw.flush();
                    } catch (UserNotFound unf) {
                        pw.println(11);
                        pw.flush();
                    } catch (FileNotFoundException fne) {
                        pw.println(11);
                        pw.flush();
                    }

                }

                //checks if uses wants to see any info about a product
                decision = 1;

                while (decision == 1) {

                    if (decision == 1) {
                        //user is now interacting with the market
                        decision = Integer.parseInt(br.readLine());

                        if (decision == 1) {
                            //user buying product
                            int inStock;
                            decision = Integer.parseInt(br.readLine());
                            inStock = Integer.parseInt(br.readLine());
                            if (decision == 1 && inStock == 1) {
                                //user buys the product
                                int quantity;
                                quantity = Integer.parseInt(br.readLine());
                                if (quantity > 0) {
                                    Product boughtP;
                                    try {
                                        boughtP = (Product) ois.readObject();

                                        try {
                                            db.updateCustomerData(customerName, boughtP, quantity);
                                            db.updateProductQuantity(boughtP, quantity);

                                        } catch (UserNotFound unf) {
                                            db.logNewCustomer(customerName, boughtP, quantity);
                                            db.updateProductQuantity(boughtP, quantity);
                                        }
                                    } catch (ClassNotFoundException cnf) {
                                        cnf.printStackTrace();
                                    }
                                }
                            }
                        } else if (decision == 4) {
                            String customerHistroy = "";
                            try {
                                customerHistroy = db.exportCustomerHistory(customerName);
                                pw.println(customerHistroy);
                                pw.flush();
                            } catch (UserNotFound unf) {
                                pw.println(11);
                                pw.flush();
                            } catch (FileNotFoundException fne) {
                                pw.println(11);
                                pw.flush();
                            }
                            try {
                                br.readLine();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (decision == 3) {
                            decision = -1;
                            //indicates the user would like to view product diescription
                        }

                    } else {
                        break;
                    }

                    if (decision == -1) {
                        decision = 1;
                    }

                    sort = Boolean.parseBoolean(br.readLine());


                    if (!sort){

                        market.clear();
                        market = s.importProducts(market);
                        oos.writeObject(new ArrayList<Product>(market)); //refreshes market
                        oos.flush();
                        System.out.println("Market Refreshed");

                    } //end

                    decision = 1;


                }




            } else { //start seller class

                String sellerName;
                String storeName;
                ArrayList<Product> sellerList = new ArrayList<>();
                sellerName = br.readLine();
                storeName = br.readLine();
                int storeExists;
                boolean run = true;
                String choice;

                //does user have store?
                storeExists = Integer.parseInt(br.readLine());

                if (storeExists == -1) {
                    //we use -1 because that is the error flag if there is no seller match
                    //user has option to make a new store
                    decision = Integer.parseInt(br.readLine());

                    if (decision == 1) {
                        //seller has choosen to add themselves to the market

                        if (sellerList != null) {
                            sellerList.clear();
                        }


                        try {
                            sellerList.addAll((ArrayList<Product>) ois.readObject());
                            db.logNewSeller(sellerName, sellerList);
                        } catch (ClassNotFoundException cnf) {
                            cnf.printStackTrace();
                        }
                    } else {
                        run = false;
                    }
                }



                while (run == true) {
                    decision = Integer.parseInt(br.readLine());

                    if (decision == 1) {


                        //manual vs file import
                        decision = Integer.parseInt(br.readLine());

                        if (decision == 3) { //sends 3 if manual

                            //add product to market yes/no yes == 0

                            choice = br.readLine();

                            if (choice.equals("yes")) {
                                try {
                                    if (sellerList != null) {
                                        sellerList.clear();
                                    }

                                    sellerList.addAll((ArrayList<Product>) ois.readObject());


                                    try {
                                        db.updateSellerData(sellerName, sellerList);
                                    } catch (UserNotFound unf) {
                                        db.logNewSeller(sellerName, sellerList);
                                    }
                                } catch (ClassNotFoundException cnf) {
                                    cnf.printStackTrace();
                                }
                            }
                            // Import Via File
                        } else {
                            decision = Integer.parseInt(br.readLine());
                            // Check if user wants to add products
                            if (decision == 0) {
                                try {
                                    if (sellerList != null) {
                                        sellerList.clear();
                                    }

                                    sellerList.addAll((ArrayList<Product>) ois.readObject());


                                    try {
                                        db.updateSellerData(sellerName, sellerList);
                                    } catch (UserNotFound unf) {
                                        db.logNewSeller(sellerName, sellerList);
                                    }
                                } catch (ClassNotFoundException cnf) {
                                    cnf.printStackTrace();
                                }
                            }
                        }

                    } else if (decision == 2 || decision == 3) {


                        //add product to market yes/no
                        decision = Integer.parseInt(br.readLine());

                        if (decision == 0) {
                            //yes = 0

                            try {

                                if (sellerList != null) {
                                    sellerList.clear();
                                }

                                sellerList.addAll((ArrayList<Product>) ois.readObject());


                                try {
                                    db.updateSellerData(sellerName, sellerList);
                                } catch (UserNotFound unf) {
                                    db.logNewSeller(sellerName, sellerList);
                                }
                            } catch (ClassNotFoundException cnf) {
                                cnf.printStackTrace();
                            }
                        }

                    } else if (decision == 4) {
                        String sales;

                        try {
                            sales = db.viewStoreSales(storeName);
                            pw.println(sales);
                            pw.flush();


                        } catch (UserNotFound unf) {
                            pw.println("No Sales");
                            pw.flush();
                        } catch (FileNotFoundException fne) {
                            pw.println("No Sales");
                            pw.flush();
                        }

                    } else if (decision == 5) {
                        break;
                    }


                } //end run

            } //end seller
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public ArrayList<Product> importProducts(ArrayList<Product> market) {
        DataBase db = new DataBase();
        String[] sellerProducts;
        ArrayList<String[]> sellerDataBase = db.retriveSellerDataBase();
        for (int i = 0; i < sellerDataBase.size(); i++) {
            sellerProducts = sellerDataBase.get(i)[1].split(DataBase.regex2);
            for (int j = 0; j < sellerProducts.length; j++) {
                market.add(db.stringToProduct(sellerProducts[j]));
            }

        }
        return market;
    }

} //end classloc