import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * class handles all data storing and pulling for the project
 *
 * <p>Purdue University -- CS18000 -- Spring 2023</p>
 *
 * @author Neel Patel - pate1666
 * @version April 10th 2023
 */
// file format:
// SellerName
// productName; store; description; quantity; price; quantitySold;
// "*****************************************"

class DataBase {

    public static Object sellerSync = new Object();
    public static Object buyerSync = new Object();


    public static String regex = "*****************************************";
    public static String regex2 = "#&%";
    public void updateSellerData(String sellerName, ArrayList<Product> products) throws UserNotFound {

        synchronized (sellerSync) {

            String newSellerData = "";

            ArrayList<String[]> sellers = new ArrayList<>();
            //each sellers data is written to and stored in this string
            //first element is seller name, proceeding is each product
            boolean inDataBase = false; //is the searched seller in dataBase
            String[] sellersProducts = null; //used to rewrite into file


            for (int i = 0; i < products.size(); i++) {
                newSellerData = newSellerData + products.get(i).toString() + regex2;
            }

            File f = new File("SellerData.txt");
            PrintWriter pw = null;

            sellers = retriveSellerDataBase();

            //searches for seller (consider replacng with a method)
            for (int i = 0; i < sellers.size(); i++) {

                if (sellerName.equals(sellers.get(i)[0])) {
                    sellers.get(i)[1] = newSellerData;
                    inDataBase = true;
                }
            }

            if (inDataBase == false) {
                logNewSeller(sellerName, products);
            }

            try {
                FileWriter fw = new FileWriter(f);
                pw = new PrintWriter(fw);

                for (int i = 0; i < sellers.size(); i++) {
                    pw.println(sellers.get(i)[0]);
                    sellersProducts = sellers.get(i)[1].split(regex2);
                    for (int j = 0; j < sellersProducts.length; j++) {
                        pw.println(sellersProducts[j]);
                    }
                    pw.println(regex);
                } //reprints all the data back into the file updated

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pw != null) {
                        pw.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } //end finlly
        } //end method
    }

    public static ArrayList<String[]> retriveSellerDataBase() {
        String newSellerData = "";
        String line;
        ArrayList<String[]> sellers = new ArrayList<>();
        //each sellers data is written to and stored in this string
        //first element is seller name, proceeding is each product
        String[] sellerData = new String[2];
        String productString = ""; //used for data processing
        String oldSellerData;
        boolean inDataBase = false; //is the searched seller in dataBase
        String[] sellersProducts = null; //used to rewrite into file

        // file format: SellerName
        // productName; store; description; quantity; price; quantitySold;

        File f = new File("SellerData.txt");
        FileReader fr = null;
        BufferedReader br = null;
        PrintWriter pw = null;

        try {
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            line = br.readLine();
            while (line != null) {
                sellerData[0] = line;
                line = br.readLine();
                while (line != null && !line.equals(regex)) {
                    productString = productString + line + regex2;
                    line = br.readLine();
                }
                sellerData[1] = productString;
                sellers.add(sellerData);
                line = br.readLine();
                productString = "";


                sellerData = new String[2];
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } //end finlly
        //end reading in all file data, sorts it into an array list of sellers with their product data;

        return sellers;

    } //end method

    public void logNewSeller(String sellerName, ArrayList<Product> products) {

        synchronized (sellerSync) {

            String newSellerData = "";
            PrintWriter pw = null;

            if (products == null) {
                return;
            }


            File f = new File("SellerData.txt");

            try {
                if (!f.exists()) {
                    f.createNewFile();

                    try {
                        FileWriter fw = new FileWriter(f, true);
                        pw = new PrintWriter(fw);

                        pw.println(sellerName);
                        for (int i = 0; i < products.size(); i++) {
                            pw.println(products.get(i).toString());
                        }
                        pw.println(regex);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (pw != null) {
                                pw.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } //end finally

                } else {
                    try {
                        FileWriter fw = new FileWriter(f, true);
                        pw = new PrintWriter(fw);

                        pw.println(sellerName);
                        for (int i = 0; i < products.size(); i++) {
                            pw.println(products.get(i).toString());
                        }
                        pw.println(regex);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (pw != null) {
                                pw.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } //end finally
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    //use this method to easily create the array of products a seller has stored in file
    public Product[] getSellersProducts(String sellerName) throws UserNotFound {
        ArrayList<String[]> sellers = new ArrayList<>();
        sellers = retriveSellerDataBase();
        int sellersIndex = -1;
        String[] productString;
        String productName;
        String store;
        String description;
        int quantity;
        double price;
        int quantitySold;
        String[] productInfo = new String[6];
        Product[] products = null;

        for (int i = 0; i < sellers.size(); i++) {

            if (sellerName.equals(sellers.get(i)[0])) {
                i = sellersIndex;
            }
        }
        productString = sellers.get(sellersIndex)[1].split(regex2);
        products = new Product[productString.length];

        for (int i = 0; i < productString.length; i++) {
            productInfo = productString[i].split(";");

            productName = productInfo[0];
            store = productInfo[1];
            description = productInfo[2];
            quantity = Integer.parseInt(productInfo[3]);
            price = Double.parseDouble(productInfo[4]);
            quantitySold = Integer.parseInt(productInfo[5]);
            products[i] = new Product(productName, store, description, quantity, price);

            products[i].setQuantitySold(quantitySold);
        }

        return products;
    }

    public ArrayList<Product> importProduct(String fileName, String sellerName) throws FileNotFoundException {
        ArrayList<Product> products = new ArrayList<>();
        File f = new File(fileName);
        if (!f.exists()) {
            throw new FileNotFoundException("Error File Does not Exist");
        }

        FileReader fr = null;
        BufferedReader br = null;
        String line = null;
        String productName;
        String store;
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
                    store = productInfo[1];
                    description = productInfo[2];
                    quantity = Integer.parseInt(productInfo[3]);
                    price = Double.parseDouble(productInfo[4]);
                    products.add(new Product(productName, store, description, quantity, price));
                    line = br.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return products;
    }

    public static ArrayList<String[]> retriveCustomerDataBase() throws FileNotFoundException {
        String line;
        ArrayList<String[]> customers = new ArrayList<>();
        //each sellers data is written to and stored in this string
        //first element is seller name, proceeding is each product
        String[] customerData = new String[2];
        String productString = ""; //used for data processing

        // file format: SellerName
        // productName; store; description; quantity; price; quantitySold;

        File f = new File("CustomerData.txt");

        if (!f.exists()) {
            throw new FileNotFoundException();
        }

        FileReader fr = null;
        BufferedReader br = null;
        PrintWriter pw = null;

        try {
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            line = br.readLine();
            while (line != null) {
                customerData[0] = line;
                line = br.readLine();
                while (line != null && !line.equals(regex)) {
                    productString = productString + line + regex2;
                    line = br.readLine();
                }
                customerData[1] = productString;
                customers.add(customerData);
                line = br.readLine();
                productString = "";

                customerData = new String[2];
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } //end finlly
        //end reading in all file data, sorts it into an array list of sellers with their product data;
        return customers;
    } //end method

    public void updateCustomerData(String customerName, Product product, int quantity) throws UserNotFound {

        synchronized (buyerSync) {
            String newCustomerData = "";

            ArrayList<String[]> customers = new ArrayList<>();
            //each sellers data is written to and stored in this string
            //first element is seller name, proceeding is each product
            boolean inDataBase = false; //is the searched seller in dataBase
            String[] customerProducts = null; //used to rewrite into file

            File f = new File("CustomerData.txt");

            try {
                if (!f.exists()) {
                    f.createNewFile();
                }
            } catch (IOException io) {
                io.printStackTrace();
            }

            PrintWriter pw = null;

            try {
                customers = retriveCustomerDataBase();

                //searches for customer (consider replacng with a method)
                for (int i = 0; i < customers.size(); i++) {
                    if (customerName.equals(customers.get(i)[0])) {
                        //making change here from \n to regex
                        customers.get(i)[1] = customers.get(i)[1] + product.toString(quantity);
                        inDataBase = true;
                    }
                }

                if (inDataBase == false) {
                    throw new UserNotFound("Error User not found");
                }

                try {
                    FileWriter fw = new FileWriter(f);
                    pw = new PrintWriter(fw);

                    for (int i = 0; i < customers.size(); i++) {
                        pw.println(customers.get(i)[0]);
                        customerProducts = customers.get(i)[1].split(regex2);
                        for (int j = 0; j < customerProducts.length; j++) {
                            pw.println(customerProducts[j]);
                        }
                        pw.println(regex);
                    } //reprints all the data back into the file updated

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (pw != null) {
                            pw.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } //end finlly
            } catch (FileNotFoundException fne) {
                throw new UserNotFound("User not found");
            }
        }
    }

    public void logNewCustomer(String customerName, Product product, int quantity) {

        synchronized (buyerSync) {
            PrintWriter pw = null;
            File f = new File("CustomerData.txt");

            try {
                if (!f.exists()) {
                    f.createNewFile();
                }
            } catch (IOException io) {
                io.printStackTrace();
            }

            try {
                FileWriter fw = new FileWriter(f, true);
                pw = new PrintWriter(fw);

                pw.println(customerName);
                pw.println(product.toString(quantity));
                pw.println(regex);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pw != null) {
                        pw.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } //end finally
        }
    }

    public String exportCustomerHistory(String customerName) throws UserNotFound, FileNotFoundException {

        try {
            ArrayList<String[]> customers = retriveCustomerDataBase();
            boolean inDataBase = false;
            String[] customerProducts = null;
            String[] productInfo = null;
            String output = "";

            for (int i = 0; i < customers.size(); i++) {

                if (customerName.equals(customers.get(i)[0])) {
                    customerProducts = customers.get(i)[1].split(regex2);
                    inDataBase = true;
                }
            }

            if (inDataBase == false) {
                throw new UserNotFound("Error User Not Found");
            }

            output = output + customerName + "\n";
            for (int i = 0; i < customerProducts.length; i++) {
                productInfo = customerProducts[i].split(";");
                output = output + "Product: " + productInfo[0] + "\n";
                output = output + "Store: " + productInfo[1] + "\n";
                output = output + "Description: " + productInfo[2] + "\n";
                output = output + "Quantity: " + productInfo[3] + "\n";
                output = output + "Price of item: " + productInfo[4] + "\n";
                output = output + regex + "\n";
            }
            return output;
        } catch (FileNotFoundException fne) {
            throw new UserNotFound("error");
        }

    }

    public String viewStoreSales(String storeName) throws UserNotFound, FileNotFoundException {

        try {
            ArrayList<String[]> customers = retriveCustomerDataBase();

            boolean inDataBase = false;
            String[] customerProducts = null;
            String[] productInfo = null;
            String customerName;
            String output = "Sales for " + storeName + "\n";
            double revenu = 0;

            for (int i = 0; i < customers.size(); i++) {

                customerName = customers.get(i)[0];
                customerProducts = customers.get(i)[1].split(regex2);

                for (int j = 0; j < customerProducts.length; j++) {
                    productInfo = customerProducts[j].split(";");
                    if (productInfo[1].equals(storeName)) {
                        inDataBase = true;

                        output = output + "Customer Name: " + customerName + "\n";
                        output = output + "Product: " + productInfo[0] + "\n";
                        output = output + "Quantity: " + productInfo[3] + "\n";
                        output = output + "Price: " + productInfo[4] + "\n";
                        output = output + regex + "\n";
                        revenu = revenu + Double.parseDouble(productInfo[4]) * Integer.parseInt(productInfo[3]);
                    }
                }
            }

            if (inDataBase == false) {
                throw new UserNotFound("Error no purchase history");
            }

            output = output + "Total Revenue = " + revenu;

            return output;

        } catch (FileNotFoundException fne) {
            throw new UserNotFound("Error no purchase history");
        }
    }

    public void updateProductQuantity(Product product, int quantityBought) {

        synchronized (sellerSync) {

            ArrayList<String[]> sellers = retriveSellerDataBase();
            String[] sellerProducts = null;
            String[] oldProductInfo = null;
            String[] newProductInfo = product.toString().split(";");
            String[] sellersProducts = null;
            int oldQuantity;
            int newQuantity;
            String backToSellerProducts = "";
            PrintWriter pw = null;
            File f = new File("SellerData.txt");

            for (int i = 0; i < sellers.size(); i++) {
                sellerProducts = sellers.get(i)[1].split(regex2);
                for (int j = 0; j < sellerProducts.length; j++) {
                    oldProductInfo = sellerProducts[j].split(";");
                    if (newProductInfo[0].equals(oldProductInfo[0]) && newProductInfo[1].equals(oldProductInfo[1])) {
                        oldQuantity = Integer.parseInt(oldProductInfo[3]);
                        newQuantity = oldQuantity - quantityBought;
                        oldProductInfo[3] = Integer.toString(newQuantity);
                        //updates the product string
                        sellerProducts[j] = oldProductInfo[0] + ";" +
                                oldProductInfo[1] + ";" + oldProductInfo[2] + ";" + oldProductInfo[3] +
                                ";" + oldProductInfo[4] + ";" + oldProductInfo[5] + ";";
                    }
                }

                for (int j = 0; j < sellerProducts.length; j++) {
                    backToSellerProducts = backToSellerProducts + sellerProducts[j] + regex2;
                }
                sellers.get(i)[1] = backToSellerProducts;
                backToSellerProducts = "";
            }

            try {
                FileWriter fw = new FileWriter(f);
                pw = new PrintWriter(fw);

                for (int i = 0; i < sellers.size(); i++) {
                    pw.println(sellers.get(i)[0]);
                    sellersProducts = sellers.get(i)[1].split(regex2);
                    for (int j = 0; j < sellersProducts.length; j++) {
                        pw.println(sellersProducts[j]);
                    }
                    pw.println(regex);
                } //reprints all the data back into the file updated
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pw != null) {
                        pw.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } //end finlly
        }
    }
    public static Product stringToProduct(String line) {
        Product p = null;
        String productName;
        String store;
        String description;
        int quantity;
        double price;
        String[] productInfo = null;

        productInfo = line.split(";");
        productName = productInfo[0];
        store = productInfo[1];
        description = productInfo[2];
        quantity = Integer.parseInt(productInfo[3]);
        price = Double.parseDouble(productInfo[4]);
        p = new Product(productName, store, description, quantity, price);
        return p;
    }
}
//end class//