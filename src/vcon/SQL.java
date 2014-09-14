package vcon;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

public class SQL {
	private String server;
	private String port;
	private String database;
	private String instance;
	private String user;
	private String password;
	
	private Connection conn;
	private Statement stmt;
	
	public SQL() {
		getProperty();
	}
	
	public void getProperty() {
		Properties prop = new Properties();
		InputStream in = null;
		
		try {
			in = new FileInputStream("config.properties");
			prop.load(in);
			
			this.server = prop.getProperty("host");
			this.port = prop.getProperty("port");
			this.instance = prop.getProperty("instance");
			this.database = prop.getProperty("database");
			this.user = prop.getProperty("user");
			this.password = prop.getProperty("password");
			
			System.out.println(server);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void connect() {
		String con = server + ":" + port + "/" 
			+ database + ";instance=" + instance + ";user=" + user 
			+ ";password=" + password;
	
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			conn = DriverManager.getConnection (con);
			System.out.println ("Connection successful!!!");   
			stmt = conn.createStatement();
		} catch (Exception e ) {
			System.err.println ("Cannot connect to database server");
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			conn.close();
		} catch (Exception e) {
			System.err.println("Cannot stop connection");
			e.printStackTrace();
		}
		
	}
	
	public void check() {
		String sql = "IF (NOT EXISTS (SELECT * " +
				"FROM INFORMATION_SCHEMA.TABLES " +
				"WHERE TABLE_SCHEMA = 'dbo' AND " +
				"TABLE_NAME = 'TradeComfirmations')) " +
				"BEGIN " +
				"CREATE TABLE TradeComfirmations (" +
				"CUSIP	varchar(10) not null," +
				"C_TYPE	varchar(5) not null," +
				"DEAL_NAME	varchar(20) not null," +
				"DEAL_CLASS	varchar(5) not null," +
				"PRICE	numeric(15,10) not null," +
				"SETTLE_DATE	varchar(20) not null," +
				"PRINCIPAL_VALUE	numeric(15,2) not null," +
				"ACCURAL	numeric(15,2) not null," +
				"TOTAL_FUNDS	numeric(15,2) not null," +
				"COUNTER_PARTY varchar(20) not null," +
				"PRIMARY KEY (CUSIP, SETTLE_DATE))" +
				"END";
		try {
			stmt.execute(sql);
		} catch (Exception e) {
			System.err.println("WTF!!!");
			e.printStackTrace();
		}
		
	}
	
	public void save(ArrayList<Vcon> v) {
		for(int i=0; i<v.size(); i++) {
			Vcon tic = v.get(i);
			String sql = "INSERT INTO TradeComfirmations(" +
				"CUSIP, C_TYPE, DEAL_NAME, DEAL_CLASS, PRICE, SETTLE_DATE, PRINCIPAL_VALUE, " +
				"ACCURAL, TOTAL_FUNDS, COUNTER_PARTY) VALUES (" +
				"'" + tic.getCusip() + "', " + 
				"'" + tic.getType() + "', " + 
				"'" + tic.getDealName()+ "', " + 
				"'" + tic.getDealClass() + "', " + 
				tic.getPrice() + ", " + 
				"'" + tic.getDate() + "', " + 
				tic.getValue() + ", " + 
				tic.getAccrual() + ", " +
				tic.getTotalFunds() + ", " +
				"'" + tic.getCounterParty() + "'" +
				")";
		try {
			stmt.executeUpdate(sql);
			} catch (Exception e) {
			System.err.println("WTF!!! Something is wrong!!!");
			JOptionPane.showMessageDialog(null, e.getMessage(), "WTF!!!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			}
		}
	}
} 
