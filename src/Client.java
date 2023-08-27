import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.*;

import static javax.swing.JOptionPane.YES_OPTION;

/**
 * Client class that shows buyers and sellers info about the market,
 * also allows them to input wants, all done with GUI.
 *
 * <p>Purdue University -- CS18000 -- Spring 2023</p>
 *
 * @author Travis Whidden - twhidden
 * @version April 10th 2023
 */
public class Client {
    public ArrayList<Product> products;
    public Client(ArrayList<Product> products) {
        this.products = products;
    }
    public Client() {
        this.products = new ArrayList<>();
    }
    public void addProduct(Product p) {
        this.products.add(p);
    }
    public void removeProduct(Product p) {
        this.products.remove(p);
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
    public static String formatPrice(double price) {
        // Create NumberFormat instance with desired locale and currency format
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);

        // Format price as currency string
        String formattedPrice = nf.format(price);

        return formattedPrice;
    }
    public static void main(String[] args) {
        int x = 0;
        Scanner sc = new Scanner(System.in);
        ArrayList<String> productMarket = new ArrayList<>();
        //Product a = new Product("shoes", "Walmart", "protect feet", 10, 49.99);
        //Product b = new Product("mouse", "BestBuy", "navigate computer", 5, 25.00);
        //Product c = new Product("pencils", "OfficeDepot", "write on paper", 8, 2.99);
        //ArrayList<Product> starter = new ArrayList<>();
        //starter.add(a);
        //starter.add(b);
        //starter.add(c);
        Client market = new Client();
        String want;
        String want1 = "";
        String buyWant;
        int invalid = 0;
        String productSearch = null;
        boolean searchResult = false;
        String productWant;
        int wantCounter = 0;
        int productWantNumber;
        int productNumber = 0;
        int quantityBought = 0;
        Product updateProduct;
        String hostPort;
        do {
            hostPort = JOptionPane.showInputDialog(null, "Please Enter Host Name And Port Number", "Port Info",
                    JOptionPane.QUESTION_MESSAGE);
            if (!hostPort.contains(" ")) {
                JOptionPane.showMessageDialog(null,
                        "Please enter the two values separated with one space!" +
                                " \nThe First value should be the host name and the " +
                                "Second value should be the port number!", "Port Info",
                        JOptionPane.ERROR_MESSAGE);
            }
        } while (!hostPort.contains(" "));

        String[] split = hostPort.split(" ");
        String hostName = split[0];
        int portNumber1 = Integer.parseInt(split[1]);
        boolean status = true;
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try {
            socket = new Socket(hostName, portNumber1);
            JOptionPane.showMessageDialog(null, "Connection Successfully Established!", "Connection Message",
                    JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection Not Successful!", "Connection Message",
                    JOptionPane.ERROR_MESSAGE);
            status = false;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            market.setProducts((ArrayList<Product>) ois.readObject());

            String[] userType = {"Buyer", "Seller"};
            JOptionPane.showMessageDialog(null, "Welcome to the Market!", "Market",
                    JOptionPane.PLAIN_MESSAGE);
            do {
                want = (String) JOptionPane.showInputDialog(null, "Are you a Buyer or Seller?", "Market",
                        JOptionPane.PLAIN_MESSAGE, null, userType, null);
                if (want.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please Select One", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            } while (want.isEmpty());
            int serverWant = 0;
            if (want.equals("Buyer")) {
                serverWant = 1;
            }

            writer.println(serverWant);
            writer.flush();


            if (want.equals("Buyer")) {
                String customerName;
                JOptionPane.showMessageDialog(null, "Welcome to the Buyer Interface!", "Buyer Interface",
                        JOptionPane.PLAIN_MESSAGE);
                do {
                    customerName = JOptionPane.showInputDialog(null, "Please Enter Your Name", "Buyer Interface",
                            JOptionPane.QUESTION_MESSAGE);
                    if (customerName.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Name Cannot be Empty!", "Buyer Interface",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                } while (customerName.isEmpty());

                writer.println(customerName);
                writer.flush();

                String[] options = {"See Current list of Products", "Sort Products by Price",
                        "Sort Products by Quantity", "View Purchase History"};
                want = (String) JOptionPane.showInputDialog(null,
                        "What would you like to do Next?", "Buyer Interface",
                        JOptionPane.PLAIN_MESSAGE, null, options, null);
                int serverWant1 = 0;
                if (want.equals("View Purchase History")) {
                    serverWant1 = 3;
                }

                writer.println(serverWant1);
                writer.flush();

                if (want.equals("Sort Products by Price")) {
                    //trying to sort market by price
                    int n = market.getProducts().size();
                    Product placeHolder = null;
                    for (int i = 0; i < n - 1; i++) {
                        for (int j = 0; j < n - i - 1; j++) {
                            if (market.getProducts().get(j).getPrice() >
                                    market.getProducts().get(j + 1).getPrice()) {
                                placeHolder = market.getProducts().get(j);
                                market.getProducts().set(j, market.getProducts().get(j + 1));
                                market.getProducts().set(j + 1, placeHolder);
                            }
                        }
                    }
                }
                if (want.equals("Sort Products by Quantity")) {
                    //trying to sort market by quantity
                    int n = market.getProducts().size();
                    Product placeHolder = null;
                    for (int i = 0; i < n - 1; i++) {
                        for (int j = 0; j < n - i - 1; j++) {
                            if (market.getProducts().get(j).getQuantity() >
                                    market.getProducts().get(j + 1).getQuantity()) {
                                placeHolder = market.getProducts().get(j);
                                market.getProducts().set(j, market.getProducts().get(j + 1));
                                market.getProducts().set(j + 1, placeHolder);
                            }
                        }
                    }
                }
                if (want.equals("View Purchase History")) {
                    JOptionPane.showMessageDialog(null, "Printing purchase history...", "Buyer Interface",
                            JOptionPane.PLAIN_MESSAGE);
                    String customerHistory = "";
                    String line;
                    line = reader.readLine();
                    if (!line.equals("ERROR")) {
                        while (reader.ready()) {
                            customerHistory = customerHistory + line + "\n";
                            line = reader.readLine();
                        }
                    }
                    try {
                        if (line.equals("11")) {
                            throw new UserNotFound("error");
                        } else {
                            int yesNo;
                            String exportFileName;
                            JOptionPane.showMessageDialog(null,
                                    customerHistory, "Buyer Interface",
                                    JOptionPane.PLAIN_MESSAGE);
                            yesNo = JOptionPane.showConfirmDialog(null,
                                    "Would you like to export the purchase history to a file?",
                                    "Buyer Interface", JOptionPane.YES_NO_OPTION);
                            if (yesNo == 0) {
                                exportFileName = JOptionPane.showInputDialog(null,
                                        "Please Enter a file name:", "Buyer Interface",
                                        JOptionPane.QUESTION_MESSAGE);
                                File customerHistoryFile = new File(exportFileName);
                                try {
                                    if (!customerHistoryFile.exists()) {
                                        customerHistoryFile.createNewFile();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                FileWriter fw = new FileWriter(customerHistoryFile);
                                PrintWriter historyWriter = new PrintWriter(fw);

                                historyWriter.println(customerHistory);
                                historyWriter.close();
                            }
                        }

                    } catch (UserNotFound unf) {
                        JOptionPane.showMessageDialog(null, "No purchase history for this customer!", "Buyer Interface",
                                JOptionPane.PLAIN_MESSAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                boolean isSorting = false;

                do {
                    do {
                        isSorting = false;
                        productMarket = new ArrayList<>();
                        for (int i = 0; i < market.getProducts().size(); i++) {
                            productMarket.add(market.getProducts().get(i).getStore());
                            productMarket.add(market.getProducts().get(i).getProduct());
                            productMarket.add(String.valueOf(market.getProducts().get(i).getPrice()));
                        }
                        String[] productDropDown = new String[((productMarket.size() / 3) + 1)];
                        for (int i = 0; i < (productDropDown.length - 1); i++) {
                            String dropDownList = productMarket.get((i * 3)) +
                                    ": " + productMarket.get((i * 3) + 1) + ": $" +
                                    productMarket.get((i * 3) + 2);
                            productDropDown[i] = dropDownList;
                        }
                        productDropDown[(productMarket.size() / 3)] = "None";
                        want = (String) JOptionPane.showInputDialog(
                                null,
                                "Which Product would you like to See More About?",
                                "Buyer Interface",
                                JOptionPane.PLAIN_MESSAGE, null, productDropDown,
                                null);
                        for (int i = 0; i < productDropDown.length - 1; i++) {
                            if (want.contains(market.getProducts().get(i).getProduct())) {
                                productNumber = i;
                            }
                        }
                    } while (want.isEmpty());
                    if (want.equals("None")) {
                        JOptionPane.showMessageDialog(null,
                                "Thank You For Visiting the Market!", "Buyer Interface",
                                JOptionPane.PLAIN_MESSAGE);
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Product Details\n" + "Description: " + market.getProducts().get(
                                        productNumber).getDescription() + "\n" +
                                        "Quantity Available: " + market.getProducts().get(
                                        productNumber).getQuantity(), "Buyer Interface",
                                JOptionPane.PLAIN_MESSAGE);
                        do {
                            String[] purchaseOptions = {"Buy Product",
                                    "Review Purchase History",
                                    "View Another Product Description", "Sort Products by Price",
                                    "Sort Products by Quantity", "Leave"};
                            want1 = (String) JOptionPane.showInputDialog(null,
                                    "What would you like to do Next?", "Buyer Interface",
                                    JOptionPane.PLAIN_MESSAGE, null, purchaseOptions,
                                    null);
                            if (want1.isEmpty()) {
                                JOptionPane.showMessageDialog(null,
                                        "Please select an Option from the Dropdown Menu!",
                                        "Buyer Interface",
                                        JOptionPane.PLAIN_MESSAGE);
                            }
                        } while (want1.isEmpty());
                        int serverWant2 = 10;
                        if (want1.equals("Buy Product")) {
                            serverWant2 = 1;
                        }
                        if (want1.equals("Leave")) {
                            serverWant2 = 3;
                        }
                        if (want1.equals("Review Purchase History")) {
                            serverWant2 = 4;
                        }

                        writer.println(serverWant2);
                        writer.flush();

                        if (want1.equals("Buy Product")) {
                            do {
                                String[] buyNow = {"Buy Now", "Cancel"};
                                buyWant = (String) JOptionPane.showInputDialog(null,
                                        "What would you like to do Next?", "Buyer Interface",
                                        JOptionPane.PLAIN_MESSAGE, null,
                                        buyNow, null);
                                if (buyWant.isEmpty()) {
                                    JOptionPane.showMessageDialog(null,
                                            "Please select an Option from the Dropdown Menu!",
                                            "Buyer Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                }
                            } while (buyWant.isEmpty());
                            if (buyWant.equals("Buy Now")) {
                                writer.println(1);
                                writer.flush();

                                if (market.getProducts().get(productNumber).getQuantity() <= 0) {
                                    JOptionPane.showMessageDialog(null,
                                            "Item out of Stock!", "Buyer Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                    writer.println(0);
                                    writer.flush();
                                } else {
                                    writer.println(1);
                                    writer.flush();
                                    do {
                                        invalid = 0;
                                        try {
                                            quantityBought = Integer.parseInt(JOptionPane.showInputDialog(
                                                    null, "Quantity Available: " +
                                                            market.getProducts().get(
                                                                    productNumber).getQuantity() +
                                                            "\n" + "How many would you like to Buy?",
                                                    "Buyer Interface",
                                                    JOptionPane.QUESTION_MESSAGE));
                                            if (quantityBought > market.getProducts().get(
                                                    productNumber).getQuantity()) {
                                                JOptionPane.showMessageDialog(null,
                                                        "Requested Quantity Exceeds Quantity in Stock!" +
                                                                " Please enter valid Quantity!", "Buyer Interface",
                                                        JOptionPane.PLAIN_MESSAGE);
                                            }
                                            if (quantityBought <= 0) {
                                                JOptionPane.showMessageDialog(null,
                                                        "You really don't want any? That's fine.", "Buyer Interface",
                                                        JOptionPane.PLAIN_MESSAGE);
                                            }
                                        } catch (Exception e) {
                                            JOptionPane.showMessageDialog(null,
                                                    "Invalid Input! Please enter a Number!" +
                                                            "\nIf you don't want any, Enter 0!", "Buyer Interface",
                                                    JOptionPane.PLAIN_MESSAGE);
                                            invalid = 1;
                                        }
                                    } while (quantityBought > market.getProducts().get(
                                            productNumber).getQuantity() || invalid == 1);
                                    if (quantityBought > 0) {
                                        writer.println(quantityBought);
                                        writer.flush();
                                        JOptionPane.showMessageDialog(null,
                                                "Total: " + formatPrice((
                                                        quantityBought *
                                                                market.getProducts().get(
                                                                        productNumber).getPrice())),
                                                "Buyer Interface",
                                                JOptionPane.PLAIN_MESSAGE);
                                        JOptionPane.showMessageDialog(null,
                                                "Item Bought!", "Buyer Interface",
                                                JOptionPane.PLAIN_MESSAGE);
                                        // Customer buys product (productNumber) in products arrayList
                                        // Add (productNumber) to purchase history
                                        market.getProducts().get(productNumber).setQuantity(
                                                market.getProducts().get(
                                                        productNumber).getQuantity() - quantityBought);
                                        updateProduct = (market.getProducts().get(productNumber));
                                        oos.writeObject(updateProduct);
                                        oos.flush();

                                    } else {
                                        writer.println(quantityBought);
                                        writer.flush();
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "Thank You For Visiting the Market!", "Buyer Interface",
                                        JOptionPane.PLAIN_MESSAGE);
                                break;
                            }
                            if (buyWant.equals("Cancel")) {
                                writer.println(0);
                                writer.flush();
                                JOptionPane.showMessageDialog(null,
                                        "Sale Canceled!", "Buyer Interface",
                                        JOptionPane.PLAIN_MESSAGE);
                            }

                        }
                        if (want1.equals("Review Purchase History")) {
                            JOptionPane.showMessageDialog(null,
                                    "Printing purchase history...", "Buyer Interface",
                                    JOptionPane.PLAIN_MESSAGE);
                            String customerHistory = "";
                            String line;
                            line = reader.readLine();
                            if (!line.equals("ERROR") && !line.equals("11")) {
                                while (reader.ready()) {
                                    customerHistory = customerHistory + line + "\n";
                                    line = reader.readLine();
                                    if (line == null) {
                                        break;
                                    }
                                }
                            }
                            writer.println("Finished Reading");
                            writer.flush();

                            try {
                                if (line.equals("11")) {
                                    throw new UserNotFound("error");
                                } else {
                                    int yesNo;
                                    String exportFileName;
                                    JOptionPane.showMessageDialog(null,
                                            customerHistory, "Buyer Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                    yesNo = JOptionPane.showConfirmDialog(null,
                                            "Would you like to export the purchase history to a file?",
                                            "Buyer Interface", JOptionPane.YES_NO_OPTION);
                                    if (yesNo == 0) {
                                        exportFileName = JOptionPane.showInputDialog(null,
                                                "Please Enter a file name:", "Buyer Interface",
                                                JOptionPane.QUESTION_MESSAGE);
                                        File customerHistoryFile = new File(exportFileName);
                                        try {
                                            if (!customerHistoryFile.exists()) {
                                                customerHistoryFile.createNewFile();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        FileWriter fw = new FileWriter(customerHistoryFile);
                                        PrintWriter historyWriter = new PrintWriter(fw);

                                        historyWriter.println(customerHistory);
                                        historyWriter.close();
                                    }
                                }

                            } catch (UserNotFound unf) {
                                JOptionPane.showMessageDialog(null,
                                        "No purchase history for this customer!", "Buyer Interface",
                                        JOptionPane.PLAIN_MESSAGE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (want1.equals("Sort Products by Price")) {
                            isSorting = true;
                            //trying to sort market by price
                            int n = market.getProducts().size();
                            Product placeHolder = null;
                            for (int i = 0; i < n - 1; i++) {
                                for (int j = 0; j < n - i - 1; j++) {
                                    if (market.getProducts().get(j).getPrice() >
                                            market.getProducts().get(j + 1).getPrice()) {
                                        placeHolder = market.getProducts().get(j);
                                        market.getProducts().set(j, market.getProducts().get(j + 1));
                                        market.getProducts().set(j + 1, placeHolder);
                                    }
                                }
                            }
                        }
                        if (want1.equals("Sort Products by Quantity")) {
                            isSorting = true;
                            //trying to sort market by quantity
                            int n = market.getProducts().size();
                            Product placeHolder = null;
                            for (int i = 0; i < n - 1; i++) {
                                for (int j = 0; j < n - i - 1; j++) {
                                    if (market.getProducts().get(j).getQuantity() >
                                            market.getProducts().get(j + 1).getQuantity()) {
                                        placeHolder = market.getProducts().get(j);
                                        market.getProducts().set(j, market.getProducts().get(j + 1));
                                        market.getProducts().set(j + 1, placeHolder);
                                    }
                                }
                            }
                        }
                        if (want1.equals("Leave")) {
                            JOptionPane.showMessageDialog(null,
                                    "Thank You For Visiting the Market!", "Buyer Interface",
                                    JOptionPane.PLAIN_MESSAGE);
                            break;
                        }
                    }


                    writer.println(isSorting);
                    writer.flush();

                    if (!isSorting) {
                        market.products.clear();
                        market.setProducts((ArrayList<Product>) ois.readObject());
                        System.out.println("Market Refreshed!");
                        //refreshes page
                    }



                } while(!want1.equals("Leave") && !want.equals("None"));


                //buyers interface

                //list available products

                //show store, product name, and price

                //customer can select a specific product, that will take
                //them to product page, which shows description and quantity

                //when items are purchased, the quantity available decreases

            } else {
                String store;
                String sellerName;
                String sellerStoreWant = "";
                String newSellerStore;
                String newSellerProduct;
                String newSellerDescription;
                int newSellerQuantity = 0;
                double newSellerPrice = 0;

                JOptionPane.showMessageDialog(null,
                        "Welcome to the Seller Interface!", "Seller Interface",
                        JOptionPane.PLAIN_MESSAGE);
                do {
                    sellerName = JOptionPane.showInputDialog(null,
                            "Please Enter your Name: ", "Seller Interface",
                            JOptionPane.QUESTION_MESSAGE);
                    if (sellerName.isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                                "Seller Name Cannot be Empty!", "Seller Interface",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                } while (sellerName.isEmpty());

                do {
                    store = JOptionPane.showInputDialog(null,
                            "Please Enter your Store Name: ", "Seller Interface",
                            JOptionPane.QUESTION_MESSAGE);
                    if (store.isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                                "Store Name Cannot be Empty!", "Seller Interface",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                } while (store.isEmpty());
                writer.println(sellerName);
                writer.flush();
                writer.println(store);
                writer.flush();

                int storeIndexNumber = -1;
                String sellerWant = "";
                int newWant;
                String editProduct = "";
                int editProduct1 = 0;

                String deleteWant;
                int deleteWant1 = 0;
                int deleteConfirm;
                int productsIndex = -1;
                int counter = 1;
                int checker = 0;
                int checker1 = 0;

                ArrayList<Product> sellerList = new ArrayList<>();
                ArrayList<String> sellerList1 = new ArrayList<>();
                for (int i = 0; i < market.getProducts().size(); i++) {
                    if (market.getProducts().get(i).getStore().equalsIgnoreCase(store)) {
                        // Product# = counter
                        counter = counter + 1;
                        productsIndex = i;
                        storeIndexNumber = i;
                        sellerList1.add("Product name: " + market.getProducts().get(i).getProduct());
                        sellerList.add(market.getProducts().get(i));
                        store = market.getProducts().get(storeIndexNumber).getStore();
                    }
                }

                writer.println(productsIndex);
                writer.flush();

                if (productsIndex == -1) {
                    JOptionPane.showMessageDialog(null,
                            "None of our Stores match that Name!", "Seller Interface",
                            JOptionPane.PLAIN_MESSAGE);
                    do {
                        String[] addStore = {"Add new Store", "Leave"};
                        sellerStoreWant = (String) JOptionPane.showInputDialog(null,
                                "What would you like to do Next?", "Seller Interface",
                                JOptionPane.PLAIN_MESSAGE, null, addStore, null);

                        if (sellerStoreWant.isEmpty()) {
                            JOptionPane.showMessageDialog(null,
                                    "Select a choice from Drop Down Menu!", "Seller Interface",
                                    JOptionPane.PLAIN_MESSAGE);
                        }
                    } while (sellerStoreWant.isEmpty());

                    int makeStore = 10;

                    if (sellerStoreWant.equals("Add new Store")) {
                        makeStore = 1;
                    }

                    writer.println(makeStore);
                    writer.flush();

                    if (sellerStoreWant.equals("Add new Store")) {
                        newSellerStore = store;
                        newSellerProduct = JOptionPane.showInputDialog(null,
                                "What is your new Product's Name?", "Seller Interface",
                                JOptionPane.QUESTION_MESSAGE);
                        do {
                            checker = 0;
                            try {
                                newSellerPrice = Double.parseDouble(JOptionPane.showInputDialog(
                                        null, "What is your new Product's Price?",
                                        "Seller Interface",
                                        JOptionPane.QUESTION_MESSAGE));
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null,
                                        "Invalid Input! Enter Double!", "Seller Interface",
                                        JOptionPane.PLAIN_MESSAGE);
                                checker = 1;
                            }
                        } while (checker == 1);
                        do {
                            checker1 = 0;
                            try {
                                newSellerQuantity = Integer.parseInt(JOptionPane.showInputDialog(
                                        null, "What is your new Product's Quantity?",
                                        "Seller Interface",
                                        JOptionPane.QUESTION_MESSAGE));
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null,
                                        "Invalid Input! Enter Integer!", "Seller Interface",
                                        JOptionPane.PLAIN_MESSAGE);
                                checker1 = 1;
                            }
                        } while (checker1 == 1);
                        newSellerDescription = JOptionPane.showInputDialog(
                                null, "What is your new Product's Description? ",
                                "Seller Interface",
                                JOptionPane.QUESTION_MESSAGE);

                        Product d = new Product(
                                newSellerProduct, newSellerStore,
                                newSellerDescription, newSellerQuantity, newSellerPrice);
                        market.products.add(d);

                        sellerList.add(d);

                        oos.writeObject(sellerList);
                        oos.flush();

                        for (int i = 0; i < market.getProducts().size(); i++) {
                            if (market.getProducts().get(i).getStore().equalsIgnoreCase(store)) {
                                storeIndexNumber = i;
                            }
                        }
                        store = newSellerStore;
                    }
                    if (sellerStoreWant.equals("Leave")) {
                        JOptionPane.showMessageDialog(null,
                                "Thank You for Visiting the Market!", "Seller Interface",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                }
                if (!sellerStoreWant.equals("Leave")) {
                    do {
                        String[] changeProduct = {"Add new Product",
                                "Edit existing Product", "Delete existing Product", "View store Sales",
                                "Leave"};
                        sellerWant = (String) JOptionPane.showInputDialog(null,
                                "What would you like to do Next?", "Seller Interface",
                                JOptionPane.PLAIN_MESSAGE, null, changeProduct, null);

                        if (sellerWant.isEmpty()) {
                            JOptionPane.showMessageDialog(null,
                                    "Select a choice from Drop Down Menu!", "Seller Interface",
                                    JOptionPane.PLAIN_MESSAGE);
                        }
                        int productDecision = 5;

                        if (sellerWant.equals("Add new Product")) {
                            productDecision = 1;
                            writer.println(productDecision);
                            writer.flush();
                            String importWant;
                            do {
                                String[] importDecision = {"Manually", "File Import"};
                                importWant = (String) JOptionPane.showInputDialog(null,
                                        "Would you like to add the file Manually or Via File Import?",
                                        "Seller Interface",
                                        JOptionPane.PLAIN_MESSAGE, null,
                                        importDecision, null);

                                if (importWant.isEmpty()) {
                                    JOptionPane.showMessageDialog(null,
                                            "Select a choice from Drop Down Menu!",
                                            "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                }
                            } while (importWant.isEmpty());
                            int productAddition = 10;




                            if (importWant.equals("Manually")) {
                                writer.println(3);
                                writer.flush();

                                String nameNew;
                                double priceNew = 0;
                                int quantityNew = 0;
                                String descriptionNew;

                                nameNew = JOptionPane.showInputDialog(null,
                                        "What is your new Product's Name?", "Seller Interface",
                                        JOptionPane.QUESTION_MESSAGE);
                                do {
                                    checker = 0;
                                    try {
                                        priceNew = Double.parseDouble(JOptionPane.showInputDialog(
                                                null, "What is your new Product's Price?",
                                                "Seller Interface",
                                                JOptionPane.QUESTION_MESSAGE));
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null,
                                                "Invalid Input! Enter Double!",
                                                "Seller Interface",
                                                JOptionPane.PLAIN_MESSAGE);
                                        checker = 1;
                                    }
                                } while (checker == 1);
                                do {
                                    checker1 = 0;
                                    try {
                                        quantityNew = Integer.parseInt(JOptionPane.showInputDialog(
                                                null, "What is your new Product's Quantity?",
                                                "Seller Interface",
                                                JOptionPane.QUESTION_MESSAGE));
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null,
                                                "Invalid Input! Enter Integer!", "Seller Interface",
                                                JOptionPane.PLAIN_MESSAGE);
                                        checker1 = 1;
                                    }
                                } while (checker1 == 1);
                                descriptionNew = JOptionPane.showInputDialog(null,
                                        "What is your new Product's Description? ",
                                        "Seller Interface",
                                        JOptionPane.QUESTION_MESSAGE);

                                Product newProduct = new Product(
                                        nameNew, store, descriptionNew, quantityNew, priceNew);

                                newWant = JOptionPane.showConfirmDialog(null,
                                        "Would you like to add " +
                                                nameNew + " to the Market?", "Seller Interface",
                                        JOptionPane.YES_NO_OPTION);


                                String choice = "";
                                if (newWant == 0) {

                                    choice = "yes";
                                }

                                writer.println(choice);
                                writer.flush();

                                if (newWant == 0) {

                                    sellerList.add(newProduct);

                                    oos.writeObject(new ArrayList<Product>(sellerList));
                                    oos.flush();

                                    market.addProduct(newProduct);

                                    JOptionPane.showMessageDialog(null,
                                            "New Product has been Added to the Market!",
                                            "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);


                                }
                                if (newWant == 1) {


                                    JOptionPane.showMessageDialog(null,
                                            "New Product has Not been Added to the Market!",
                                            "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                            if (importWant.equals("File Import")) {
                                writer.println(11);
                                writer.flush();
                                String fileName;
                                boolean importCheck = true;
                                int addFileProducts = 0;
                                ArrayList<Product> importedProducts = new ArrayList<>();

                                fileName = JOptionPane.showInputDialog(null,
                                        "Please enter the File Name containing One or More Products." +
                                                " (enter filename)", "Seller Interface",
                                        JOptionPane.QUESTION_MESSAGE);
                                try {
                                    File f = new File(fileName);
                                    if (!f.exists()) {
                                        throw new FileNotFoundException("Error File Does not Exist");
                                    }


                                    FileReader fr = null;
                                    BufferedReader br = null;
                                    String line = null;
                                    String productName;
                                    String importStore;
                                    String description;
                                    int quantity;
                                    double price;
                                    String[] productInfo = null;

                                    try {
                                        fr = new FileReader(f);
                                        br = new BufferedReader(fr);
                                        line = br.readLine();
                                        while (line != null) {

                                            try {
                                                productInfo = line.split(",");
                                                productName = productInfo[0];
                                                importStore = productInfo[1];
                                                description = productInfo[2];
                                                quantity = Integer.parseInt(productInfo[3]);
                                                price = Double.parseDouble(productInfo[4]);
                                                importedProducts.add(new Product(productName,
                                                        store, description, quantity, price));
                                                line = br.readLine();
                                            } catch (Exception e) {
                                                JOptionPane.showMessageDialog(null,
                                                        "File was not Formatted correctly!",
                                                        "Seller Interface",
                                                        JOptionPane.PLAIN_MESSAGE);
                                            }
                                        }
                                    } catch (IOException ioe) {
                                        throw new UserNotFound("Error");
                                    }
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(null,
                                            "Error with File", "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                    importCheck = false;
                                    addFileProducts = 2;
                                }
                                if (importCheck) {
                                    for (int i = 0; i < importedProducts.size(); i++) {
                                        JOptionPane.showMessageDialog(null,
                                                "New Product #: " + (i + 1) + "\n" +
                                                        "Product name: " +
                                                        importedProducts.get(i).getProduct() + "\n" +
                                                        "Description: " +
                                                        importedProducts.get(i).getDescription() + "\n" +
                                                        "Price: " + importedProducts.get(i).getPrice() +
                                                        "\n" +
                                                        "Quantity: " + importedProducts.get(i).getQuantity(),
                                                "Seller Interface",
                                                JOptionPane.PLAIN_MESSAGE);
                                    }

                                    addFileProducts = JOptionPane.showConfirmDialog(null,
                                            "Would you like to add these New Products to the Market?",
                                            "Seller Interface", JOptionPane.YES_NO_OPTION);

                                    writer.println(addFileProducts);
                                    writer.flush();

                                    if (addFileProducts == 0) {
                                        for (int i = 0; i < importedProducts.size(); i++) {
                                            market.addProduct(importedProducts.get(i));
                                            sellerList.add(importedProducts.get(i));
                                        }
                                        oos.writeObject(new ArrayList<Product>(sellerList));
                                        oos.flush();

                                        JOptionPane.showMessageDialog(null,
                                                "New Products added to Market!",
                                                "Seller Interface",
                                                JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                if (addFileProducts == 1) {
                                    JOptionPane.showMessageDialog(null,
                                            "New Products Not added to Market!",
                                            "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                        }

                        if (sellerWant.equals("Edit existing Product")) {
                            writer.println(2);
                            writer.flush();
                            String nameEdit;
                            double priceEdit = 0;
                            int quantityEdit = 0;
                            String descriptionEdit;
                            int sellerIndex;

                            counter = 1;
                            ArrayList<String> editList = new ArrayList<>();
                            for (int i = 0; i < market.getProducts().size(); i++) {
                                if (market.getProducts().get(i).getStore().equalsIgnoreCase(store)) {
                                    // Product # = counter
                                    counter = counter + 1;
                                    editList.add(market.getProducts().get(i).getProduct());
                                }
                            }
                            String[] editList1 = new String[editList.size() + 1];
                            for (int i = 0; i < (editList1.length - 1); i++) {
                                editList1[i] = editList.get(i);
                            }
                            editList1[(editList.size())] = "None";

                            editProduct = (String) JOptionPane.showInputDialog(null,
                                    "Which Product would you like to Edit?", "Seller Interface",
                                    JOptionPane.PLAIN_MESSAGE, null, editList1, null);


                            if (!editProduct.equals("None")) {
                                for (int i = 0; i < sellerList.size(); i++) {
                                    if (editProduct.equals(sellerList.get(i).getProduct())) {
                                        editProduct1 = i;
                                    }
                                }

                                nameEdit = JOptionPane.showInputDialog(null,
                                        "What is your Product's updated Name?", "Seller Interface",
                                        JOptionPane.QUESTION_MESSAGE);
                                do {
                                    checker = 0;
                                    try {
                                        priceEdit = Double.parseDouble(JOptionPane.showInputDialog(
                                                null, "What is your Product's updated Price?",
                                                "Seller Interface",
                                                JOptionPane.QUESTION_MESSAGE));
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null,
                                                "Invalid Input! Enter Double!", "Seller Interface",
                                                JOptionPane.PLAIN_MESSAGE);
                                        checker = 1;
                                    }
                                } while (checker == 1);
                                do {
                                    checker1 = 0;
                                    try {
                                        quantityEdit = Integer.parseInt(JOptionPane.showInputDialog(
                                                null, "What is your Product's updated Quantity?",
                                                "Seller Interface",
                                                JOptionPane.QUESTION_MESSAGE));
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null,
                                                "Invalid Input! Enter Integer!", "Seller Interface",
                                                JOptionPane.PLAIN_MESSAGE);
                                        checker1 = 1;
                                    }
                                } while (checker1 == 1);
                                descriptionEdit = JOptionPane.showInputDialog(null,
                                        "What is your Product's updated Description? ", "Seller Interface",
                                        JOptionPane.QUESTION_MESSAGE);


                                Product editedProduct = new Product(nameEdit, store,
                                        descriptionEdit, quantityEdit, priceEdit);

                                newWant = JOptionPane.showConfirmDialog(null,
                                        "Would you like to save your product changes to the Market? ",
                                        "Seller Interface", JOptionPane.YES_NO_OPTION);

                                writer.println(newWant);
                                writer.flush();

                                if (newWant == 0) {

                                    sellerList.set((editProduct1), editedProduct);

                                    oos.writeObject(new ArrayList<Product>(sellerList));
                                    oos.flush();

                                    counter = 0;
                                    for (int i = 0; i < market.getProducts().size(); i++) {
                                        if (market.getProducts().get(i).getStore().equalsIgnoreCase(store)) {
                                            if (counter == editProduct1) {
                                                market.products.set(i, editedProduct);
                                            }
                                            counter = counter + 1;
                                        }
                                    }

                                    JOptionPane.showMessageDialog(null,
                                            "Product edits have been Updated to the Market!",
                                            "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);

                                }
                                if (newWant == 1) {
                                    JOptionPane.showMessageDialog(null,
                                            "New Product was Not Added!", "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                        }

                        if (sellerWant.equals("Delete existing Product")) {
                            writer.println(3);
                            writer.flush();
                            counter = 1;
                            ArrayList<String> deleteList = new ArrayList<>();
                            for (int i = 0; i < market.getProducts().size(); i++) {
                                if (market.getProducts().get(i).getStore().equalsIgnoreCase(store)) {
                                    // Product # = counter
                                    counter = counter + 1;
                                    deleteList.add(market.getProducts().get(i).getProduct());
                                }
                            }
                            String[] deleteList1 = new String[deleteList.size() + 1];
                            for (int i = 0; i < (deleteList1.length - 1); i++) {
                                deleteList1[i] = deleteList.get(i);
                            }
                            deleteList1[(deleteList.size())] = "None";

                            deleteWant = (String) JOptionPane.showInputDialog(null,
                                    "Which Product would you like to Delete?", "Seller Interface",
                                    JOptionPane.PLAIN_MESSAGE, null, deleteList1, null);


                            if (!deleteWant.equals("None")) {
                                for (int i = 0; i < sellerList.size(); i++) {
                                    if (deleteWant.equals(sellerList.get(i).getProduct())) {
                                        deleteWant1 = i;
                                    }
                                }

                                deleteConfirm = JOptionPane.showConfirmDialog(null,
                                        "Are you sure you want to delete "  + deleteWant +
                                                " from the Market?",
                                        "Seller Interface", JOptionPane.YES_NO_OPTION);
                                writer.println(deleteConfirm);
                                writer.flush();

                                if (deleteConfirm == 0) {
                                    sellerList.remove((deleteWant1));

                                    oos.writeObject(new ArrayList<Product>(sellerList));
                                    oos.flush();

                                    counter = 0;
                                    for (int i = 0; i < market.getProducts().size(); i++) {
                                        if (market.getProducts().get(i).getStore().equalsIgnoreCase(store)) {
                                            if (counter == deleteWant1) {
                                                market.products.remove(i);
                                            }
                                            counter = counter + 1;
                                        }
                                    }
                                    JOptionPane.showMessageDialog(null,
                                            "Product Deleted!", "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);

                                }
                                if (deleteConfirm == 1) {
                                    JOptionPane.showMessageDialog(null,
                                            "Deletion Cancelled!", "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                        }
                        if (sellerWant.equals("View store Sales")) {
                            writer.println(4);
                            writer.flush();
                            JOptionPane.showMessageDialog(null,
                                    "Printing store Sales...", "Seller Interface",
                                    JOptionPane.PLAIN_MESSAGE);
                            String line;
                            String storeSalesString = "";
                            try {

                                line = reader.readLine();

                                while (reader.ready()) {
                                    storeSalesString = storeSalesString + line + "\n";
                                    line =  reader.readLine();
                                }

                                if (line.equals("No Sales") || line == null) {
                                    JOptionPane.showMessageDialog(null,
                                            "No sales for this store!", "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                } else {

                                    JOptionPane.showMessageDialog(null,
                                            storeSalesString, "Seller Interface",
                                            JOptionPane.PLAIN_MESSAGE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                        if (sellerWant.equals("Leave")) {
                            JOptionPane.showMessageDialog(null,
                                    "Thank you for Visiting the Market!", "Seller Interface",
                                    JOptionPane.PLAIN_MESSAGE);
                        }
                    } while (!sellerWant.equals("Leave"));
                }

                //sellers interface

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}