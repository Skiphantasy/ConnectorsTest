/**
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
