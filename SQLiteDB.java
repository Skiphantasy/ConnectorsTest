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
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import java.io.*;

/**
 * Class SQLiteDB
 */
public class SQLiteDB {
	/**
	 * @variable_name mySQL
	 * @type MYSQLDB
	 */
	MYSQLDB mySQL;
	/**
	 * @variable_name sql
	 * @type String
	 */
	String sql;
	/**
	 * @variable_name penalties
	 * @type ArrayList<Integer>
	 */
	ArrayList<Integer> penalties;
	/**
	 * @variable_name connection
	 * @type Connection
	 */
	Connection connection;
	/**
	 * @variable_name dataBasePath
	 * @type String
	 */
	String dataBasePath;
	/**
	 * @variable_name statement
	 * @type Statement
	 */
	Statement statement;

	/**
	 * Class SQLiteDB Constructor
	 */
	public SQLiteDB(MYSQLDB mySQL, String dataBasePath, Connection connection) {
		this.connection = connection;
		this.mySQL = mySQL;
		this.dataBasePath = dataBasePath;
		penalties = new ArrayList<Integer>();
	}

	/**
	 * Method that creates (if not exist) and connects to a local SQLite database
	 * and creates SANCION_DETALLES table
	 * 
	 * @name connectSQLite
	 */
	public void connectSQLite() {
		ResultSet resultSet;

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			System.err.println("Error al cargar el driver");
			//e1.printStackTrace();
		}
		try {
			statement = connection.createStatement();
			System.out.println("\n---Base de datos local creada correctamente---");
			statement.execute("CREATE TABLE IF NOT EXISTS SANCION_DETALLES " + "(dni varchar(9) NOT NULL,"
					+ "	vehiculo varchar(7) NOT NULL," + "	sancion tinyint PRIMARY KEY NOT NULL,"
					+ " nombre varchar(40)," + " fecha_sancion NOT NULL," + " importe tinyint)");
			statement.close();
		} catch (SQLException e) {
			System.err.print("\nNo se ha podido conectar a la base de datos");
			System.err.flush();
			//e.printStackTrace();
		}
	}

	/**
	 * Method that inserts into table SANCION_DETALLES all penalties found for the
	 * dni you entered at the start of the program
	 * 
	 * @name insertRows
	 * @param resultSet
	 */
	public void insertRows(ResultSet resultSet) {
		try {
			statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO SANCION_DETALLES values" + "('" + resultSet.getString(1) + "', '"
					+ resultSet.getString(2) + "', '" + resultSet.getInt(3) + "', '" + resultSet.getString(4) + "', '"
					+ resultSet.getString(5) + "', '" + resultSet.getInt(6) + "')");
			statement.close();
		} catch (SQLException e) {
			System.err.print("\nNo se ha podido conectar a la base de datos");
			System.err.flush();
			//e.printStackTrace();
		}
	}

	/**
	 * Method that shows all the penalties found for the dni you entered at the
	 * start of the program and the total amount of money of all penalties
	 * 
	 * @name queryData
	 */
	public void queryData() {
		try {
			int totalMultas = 0;
			statement = connection.createStatement();
			String sql = "SELECT * FROM SANCION_DETALLES";
			ResultSet resultSet = statement.executeQuery(sql);
			if(resultSet.next()) {
				System.out.println("\nDNI\t   VEHÍCULO  SANCIÓN NOMBRE\t   FECHA     IMPORTE");	
				totalMultas += resultSet.getInt(6);
				System.out.println(resultSet.getString(1) + "   " + resultSet.getString(2) + "\t" + resultSet.getInt(3)
						+ "    " + resultSet.getString(4) + "   " + resultSet.getString(5) + "   " + resultSet.getInt(6)
						+ "\n");
			} else {
				System.out.println("\n El usuario no tiene ninguna sanción");	
			}

			while (resultSet.next()) {
				totalMultas += resultSet.getInt(6);
				System.out.println(resultSet.getString(1) + "   " + resultSet.getString(2) + "\t" + resultSet.getInt(3)
						+ "    " + resultSet.getString(4) + "   " + resultSet.getString(5) + "   " + resultSet.getInt(6)
						+ "\n");
			}
			
			if(totalMultas != 0) {
				System.out.println("TOTAL MULTAS.........................." + totalMultas);					
			}
			
			resultSet.close();
			statement.close();
			deletePenalties();

		} catch (SQLException e) {
			System.err.print("\nNo se ha podido conectar a la base de datos");
			System.err.flush();
			//e.printStackTrace();
		}
	}

	/**
	 * Method that allows to delete penalties
	 * @name deletePenalties 
	 */
	public void deletePenalties() {
		String answer;
		int penalty;
		String deleteSql;
		Scanner kb = new Scanner(System.in);
		ResultSet resultSet;

		do {
			System.out.println("\n¿Desea anular alguna sanción? SI/NO\n");
			answer = kb.nextLine();
			answer = answer.toUpperCase();

			if ((!answer.equals("SI") && !answer.equals("SÍ")) && !answer.equals("NO")) {
				System.err.println("\nError. Sólo puede introducir SI/NO");
				System.err.flush();
			}

		} while ((!answer.equals("SI") && !answer.equals("SÍ")) && !answer.equals("NO"));
		try {
			if (answer.equals("SI") || answer.equals("SÍ")) {
				do {
					try {
					System.out.println("\nIntroduzca número de sanción \n(El número debe ser mayor que 0)\n");
					penalty = Integer.parseInt((kb.nextLine()));
					} catch (NumberFormatException nf) {
						System.err.println("\nError. No ha introducido un número");
						System.err.flush();
						penalty = 0;
						//nf.printStackTrace();
					} catch (InputMismatchException im) {
						System.err.println("\nError. No ha introducido un número");
						System.err.flush();
						penalty = 0;
						//im.printStackTrace();
					}
				} while (penalty <= 0);

				sql = "SELECT * FROM SANCION_DETALLES WHERE sancion LIKE '" + penalty + "'";
				deleteSql = "DELETE FROM SANCION_DETALLES WHERE sancion LIKE '" + penalty + "'";
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);

				if (resultSet.next()) {
					penalties.add(penalty);
					statement.executeUpdate(deleteSql);
					System.out.println("\n---La sanción ha sido eliminada correctamente---");

				} else {
					System.out.println("\n---La sanción no existe o ha sido eliminada---");
				}
				resultSet.close();
				statement.close();
				queryData();
			} else {
				connection.close();

				File file = new File(dataBasePath);
				File[] files = file.listFiles();

				for (int i = 0; i < files.length; i++) {
					File file2 = new File(file, files[i].getName());

					if (file2.getName().equals("sanciones.db")) {
						if (file2.delete()) {
							System.out.println("\n---Base de datos local borrada---\n");
						}
					}
				}
				mySQL.updateDataBase(penalties);
			}
		} catch (SQLException e) {
			System.err.print("\nNo se ha podido conectar a la base de datos");
			System.err.flush();
			//e.printStackTrace();
		}

	}
}
