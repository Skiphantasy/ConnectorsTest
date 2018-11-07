/**
 * This program asks the user for a dni and looks for it on the MySQL database, if not found, it asks again for a valid dni.
 * After that, it asks the path you want to create the local SQLite database and creates it, creating a table for penalties
 * and showing the current penalties for the dni entered at start and the total amount of money of penalties for that dni.
 * Then, the program asks if the user wants to delete or not a penalty, if a penalty is deleted, the list of all penalties left will
 * be showed again. Once the user has finished deleting penalties, the local database is deleted and MySQL database will be updated
 * (the penalties deleted will not be there anymore).
 * @author Tania
 * @date 31 oct. 2018
 * @version 1.0
 * @description 
 * 
 */
package peval2;


/**
 * Class Main
 */
public class Main {

	/**
	 * Method
	 * @name main
	 * @param args 
	 */
	public static void main(String[] args) {
		MYSQLDB mySql = new MYSQLDB();
		mySql.connectMYSQL();
	}

}
