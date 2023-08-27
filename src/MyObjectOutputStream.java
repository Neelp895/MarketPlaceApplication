import java.io.*;
/**
 * Handles objects between client and server
 *
 * <p>Purdue University -- CS18000 -- Spring 2023</p>
 *
 * @author Neel Patel - pate1666
 * @version April 10th 2023
 */
public class MyObjectOutputStream extends ObjectOutputStream {

    // Constructor of this class
    // 1. Default
    MyObjectOutputStream() throws IOException
    {

        // Super keyword refers to parent class instance
        super();
    }

    // Constructor of this class
    // 1. Parameterized constructor
    MyObjectOutputStream(OutputStream o) throws IOException
    {
        super(o);
    }

    // Method of this class
    public void writeStreamHeader() throws IOException
    {
        return;
    }
}