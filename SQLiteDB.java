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
import java.io.*;
/**
 * Class SQLiteDB
 */
public class SQLiteDB {
	MYSQLDB mySQL;
	String sql;
	ArrayList<Integer> penalties;
	Connection connection;
	String dataBasePath;
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
	
	public void connectSQLite() {
		ResultSet resultSet;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			System.err.println("Error al cargar el driver");
		} 
		try {		
			statement = connection.createStatement(); 
			System.out.println ( "\nBase de datos creada correctamente");			
			statement.execute("CREATE TABLE IF NOT EXISTS SANCION_DETALLES "
					+ "(dni varchar(9) NOT NULL,"
	                + "	vehiculo varchar(7) NOT NULL,"
	                + "	sancion tinyint PRIMARY KEY NOT NULL,"
	                + " nombre varchar(40),"
	                + " fecha_sancion NOT NULL,"
	                + " importe tinyint)");
			statement.close();			
		} catch (SQLException e) {
			System.err.print("No se ha podido conectar a la base de datos");			
		}
	}

	public void insertRows(ResultSet resultSet) {
		try {
			statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO SANCION_DETALLES values"
					+ "('" + resultSet.getString(1) + "', '" + resultSet.getString(2) + "', '"
					+ resultSet.getInt(3) + "', '"+ resultSet.getString(4) + "', '"
					+ resultSet.getString(5) + "', '" + resultSet.getInt(6) + "')");
			statement.close();			
		} catch (SQLException e) {
			System.err.print("No se ha podido conectar a la base de datos");
		}
	}
	
	public void queryData() {
		try {
			int totalMultas = 0;
			statement = connection.createStatement();
			String sql = "SELECT * FROM SANCION_DETALLES";
			ResultSet resultSet = statement.executeQuery(sql);
			System.out.println("\nDNI\t   VEHÍCULO  SANCIÓN NOMBRE\t   FECHA     IMPORTE");
			
			while (resultSet.next()) {				
				totalMultas += resultSet.getInt(6);
				System.out.println(resultSet.getString(1) 
				+ "   " + resultSet.getString(2) 
				+ "\t" + resultSet.getInt(3) 
				+ "    " + resultSet.getString(4) 
				+ "   " + resultSet.getString(5) 
				+ "   " + resultSet.getInt(6) +"\n");
			}
			
			System.out.println("TOTAL MULTAS.........................." + totalMultas);
			resultSet.close();			
			statement.close();
			deletePenalties();
			
		} catch (SQLException e) {
			System.err.print("No se ha podido conectar a la base de datos");
		}
	}
	
	public void deletePenalties() {
		Scanner kb = new Scanner(System.in);
		String answer;
		int penalty;
		String deleteSql;
		ResultSet resultSet;
		
		do {
			System.out.println("\n ¿Desea anular alguna sanción? SI/NO");
			answer = kb.nextLine();
			answer = answer.toUpperCase();
			
			if((!answer.equals("SI") && !answer.equals("SÍ"))  && !answer.equals("NO")) {
				System.err.println("Error. Sólo puede introducir SI/NO");
			}
			
		} while ((!answer.equals("SI") && !answer.equals("SÍ"))  && !answer.equals("NO"));
		try {
			if(answer.equals("SI") || answer.equals("SÍ")) {
				do {
					try {
						do {
							System.out.println("Introduzca número de sanción \n(El número debe ser mayor que 0)\n");
							penalty = kb.nextInt();											
						} while (penalty <= 0);
					} catch(NumberFormatException nf) {
						System.err.println("Error. No ha introducido un número");
						penalty = 0;
					}					
				} while(penalty == 0);
				
				sql = "SELECT * FROM SANCION_DETALLES WHERE sancion LIKE '" + penalty + "'";
				deleteSql = "DELETE FROM SANCION_DETALLES WHERE sancion LIKE '" + penalty + "'";
				statement = connection.createStatement();
				resultSet = statement.executeQuery(sql);
				
				if(resultSet.next()) {
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

					if(file2.getName().equals("sanciones.db")) {
						if(file2.delete()) {
							System.out.println("Base de datos local borrada");
						};
					}
				}
				mySQL.updateDataBase(penalties);
			}
		} catch (SQLException e) {
			System.err.print("No se ha podido conectar a la base de datos");
		}
		
	}
}
