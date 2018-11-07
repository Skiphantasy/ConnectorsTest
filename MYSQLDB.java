/**
 * @author Tania
 * @date 31 oct. 2018
 * @version 1.0
 * @description 
 * 
 */

package peval2;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Class MYSQLDB
 */
public class MYSQLDB {
	SQLiteDB sqLite;
	/**
	 * @variable_name connection
	 * @type Connection
	 */
	private Connection connection;
	/**
	 * @variable_name statement
	 * @type Statement
	 */
	private Statement statement;
	/**
	 * @variable_name resultSet
	 * @type ResultSet
	 */
	private ResultSet resultSet;
	/**
	 * @variable_name dataBasePath
	 * @type String
	 */
	private String dataBasePath;
	/**
	 * @variable_name answer
	 * @type String
	 */
	private String answer;

	/**
	 * Class MYSQLDB Constructor
	 */
	public MYSQLDB() {
	}

	/**
	 * Method that connects to MYSQL database and asks for the dni checking if it is correct. 
	 * If not, it asks again till it is found the database.
	 * 
	 * @name connectMYSQL
	 */
	public void connectMYSQL() {
		Scanner kb = new Scanner(System.in);
		boolean foundDNI = false;

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.print("Error al cargar el driver");
			//e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/sanciones", "root", "");
			statement = connection.createStatement();
			System.out.println("Introduzca el DNI del conductor: \n");
			do {
				String dni = kb.nextLine();
				String sql = "SELECT dni FROM conductores WHERE dni LIKE '" + dni + "'";
				resultSet = statement.executeQuery(sql);

				if (!resultSet.next()) {
					System.err.println("\nNo existe un conductor con el dni introducido.\n");
					System.err.flush();
					System.out.println("Por favor, vuelva a introducir el DNI: \n");
				} else {
					foundDNI = true;
					resultSet.close();
					statement.close();
					sql = "SELECT dni, vehiculo, sancion, nombre, fecha_sancion, importe\r\n"
							+ "FROM sanciones, conductores, vehiculos\r\n" + "WHERE dni LIKE '" + dni + "'\r\n"
							+ "                   AND matricula = vehiculo \r\n"
							+ "                   AND dni = conductor";
					this.dataBasePath = "./sanciones.db";
					sqLite = new SQLiteDB(this, dataBasePath, DriverManager.getConnection("jdbc:sqlite:" + dataBasePath));
					sqLite.connectSQLite();
					statement = connection.createStatement();
					resultSet = statement.executeQuery(sql);

					while (resultSet.next()) {
						sqLite.insertRows(resultSet);
					}

					resultSet.close();
					statement.close();
					connection.close();
					sqLite.queryData();
				}
			} while (!foundDNI);

			resultSet.close();
			statement.close();
			connection.close();

		} catch (SQLException e) {
			System.err.print("No se ha podido conectar a la base de datos");
			//e.printStackTrace();
		}
	}

	/**
	 * Method that update MySQL database once you have finished deleting penalties
	 * 
	 * @name updateDataBase
	 * @param penalties
	 */
	public void updateDataBase(ArrayList<Integer> penalties) {
		String penalty = "";
		Scanner kb = new Scanner(System.in);

		if (penalties.size() != 0) {
			for (int i = 0; i < penalties.size(); i++) {
				if (i == (penalties.size() - 1)) {
					penalty += penalties.get(i);
				} else {
					penalty += penalties.get(i) + ",";
				}
			}

			try {
				connection = DriverManager.getConnection("jdbc:mysql://localhost/sanciones", "root", "");
				statement = connection.createStatement();
				String sql = "SELECT * FROM SANCIONES WHERE SANCION IN (" + penalty + ")";
				resultSet = statement.executeQuery(sql);
				String deleteSql = "DELETE FROM SANCIONES WHERE SANCION IN (" + penalty + ")";

				if (resultSet.next()) {
					statement.executeUpdate(deleteSql);
					System.out.println("La base de datos MYSQL se ha actualizado correctamente.");
				} else {
					System.out.println("No se ha encontrado ningún registro con ese número de sanción");
				}

			} catch (SQLException e) {
				System.err.print("\nNo se ha podido conectar a la base de datos");
				//e.printStackTrace();
			}
		} else {
			System.out.println("No se ha modificado la base de datos MySQL porque no se ha anulado ninguna sanción");
		}

		do {

			System.out.println("\n¿Desea volver a consultar alguna sanción de algún conductor? SI/NO\n");
			answer = kb.nextLine();
			answer = answer.toUpperCase();

			if ((!answer.equals("SI") && !answer.equals("SÍ")) && !answer.equals("NO")) {
				System.err.print("\nError. Sólo puede introducir SI/NO\n");
				System.err.flush();
			}
		} while ((!answer.equals("SI") && !answer.equals("SÍ")) && !answer.equals("NO"));

		if (answer.equals("SI") || answer.equals("SÍ")) {
			connectMYSQL();
		} else {
			System.out.println("\nFin del programa");
		}
	}
}
