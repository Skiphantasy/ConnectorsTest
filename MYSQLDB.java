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
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private boolean correctPath = false;
	private String dataBasePath;
	private String answer;
	
	/**
	 * Class MYSQLDB Constructor
	 */
	public MYSQLDB() {		
	}

	public void connectMYSQL() {
		Scanner kb = new Scanner(System.in);		
		boolean foundDNI = false;
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.print("Error al cargar el driver");
		}
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/sanciones", "root", "");
			statement = connection.createStatement(); 
			System.out.println("Introduzca el DNI del conductor: ");
			do{
				String dni = kb.nextLine();
				String sql = "SELECT dni FROM conductores WHERE dni LIKE '" + dni + "'";
				resultSet = statement.executeQuery(sql);
				
				if(!resultSet.next()) {	
					System.err.print("\nNo existe un conductor con el dni introducido.\n");
					System.out.print("Por favor, vuelva a introducir el DNI: \n");
				} else {
					System.out.println("DNI: " + resultSet.getString(1));
					foundDNI = true;
					resultSet.close();
					statement.close();
					sql = "SELECT dni, vehiculo, sancion, nombre, fecha_sancion, importe\r\n" + 
							"FROM sanciones, conductores, vehiculos\r\n" + 
							"WHERE dni LIKE '" + dni + "'\r\n" + 
							"                   AND matricula = vehiculo \r\n" + 
							"                   AND dni = conductor";
					
					this.dataBasePath = checkConnectionSQLite();					
					
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
		}		
	}

	public void updateDataBase(ArrayList<Integer> penalties) {
		String penalty = "";
		Scanner kb = new Scanner(System.in);
		
		if(penalties.size() != 0) {
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
					
					if(resultSet.next()) {
						statement.executeUpdate(deleteSql);	
						System.out.println("La base de datos MYSQL se ha actualizado correctamente.");
					} else {						
						System.out.println("No se ha encontrado ningún registro con ese número de sanción");					
					}
					
			} catch (SQLException e) {
				System.err.print("\nNo se ha podido conectar a la base de datos");
			}	
		} else {
			System.out.println("No se ha modificado la base de datos porque no se ha anulado ninguna sanción");
		}
			
		do {
			
			System.out.println("\n¿Desea volver a consultar alguna sanción de algún conductor? SI/NO");
			answer = kb.nextLine();
			answer = answer.toUpperCase();
			
			if((!answer.equals("SI") && !answer.equals("SÍ"))  && !answer.equals("NO")) {
				System.err.println("Error. Sólo puede introducir SI/NO");
			}			
		} while ((!answer.equals("SI") && !answer.equals("SÍ"))  && !answer.equals("NO"));

		if(answer.equals("SI") || answer.equals("SÍ")) {
			connectMYSQL();
		} else {
			System.out.println("\nFin del programa");
		}	
	}
	
	public String enterPath() {
		Scanner kb1 = new Scanner(System.in);
		System.out.println("\nIntroduzca la ruta en la que desea crear la base de datos SQLite");
		System.out.println("Ejemplo de ruta: C:/Users/Tania/Documents");
		return kb1.nextLine();
	}
	
	public String checkConnectionSQLite() {
		do {
			dataBasePath = enterPath();
			try {
				sqLite = new SQLiteDB(this, dataBasePath, DriverManager.getConnection("jdbc:sqlite:" + dataBasePath + "/sanciones.db"));
				sqLite.connectSQLite();
				correctPath = true;
			} catch (SQLException e) {
				System.err.print("\nRuta incorrecta. No se ha podido conectar a la base de datos");
				correctPath = false;
			}			
		} while (correctPath == false);
		return dataBasePath;
	}

}
