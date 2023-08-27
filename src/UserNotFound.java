/**
 * User not found exception used when user not found error is given
 *
 * <p>Purdue University -- CS18000 -- Spring 2023</p>
 *
 * @author Neel Patel - pate1666
 * @version April 10th 2023
 */
public class UserNotFound extends Exception { //class is an exception when a buyer or seller cannot be find
    public UserNotFound(String msg) {
        super(msg);
    }
}