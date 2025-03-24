package me.daniel1385.moneysystem.apis;

import java.sql.*;
import java.util.UUID;

public class MySQL
{
	private String host;
	private int port;
	private String db;
	private String user;
	private String pass;
	private String server;
	private Connection con;

	public MySQL(String host, int port, String db, String user, String pass, String server) {
		this.host = host;
		this.port = port;
		this.db = db;
		this.user = user;
		this.pass = pass;
		this.server = server;
	}

	private void connect() throws SQLException {
		if (con == null || con.isClosed()) {
			con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db, this.user, this.pass);
		}
	}

	public void init() throws SQLException {
		connect();
		con.prepareStatement("CREATE TABLE IF NOT EXISTS `bank` (" +
				"  `uuid` varchar(36) NOT NULL," +
				"  `money` double NOT NULL," +
				"  PRIMARY KEY (`uuid`)" +
				");").execute();
		con.prepareStatement("CREATE TABLE IF NOT EXISTS `banklogs` (" +
				"  `uuid` varchar(36) NOT NULL," +
				"  `amount` double NOT NULL," +
				"  `server` text NOT NULL," +
				"  `old` double NOT NULL," +
				"  `new` double NOT NULL" +
				");").execute();
		con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + server + "_money` (" +
				"  `uuid` varchar(36) NOT NULL," +
				"  `balance` double NOT NULL," +
				"  PRIMARY KEY (`uuid`)" +
				");").execute();
		con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + server + "_transactions` (" +
				"  `uuid` varchar(36) NOT NULL," +
				"  `amount` double NOT NULL," +
				"  `reason` text NOT NULL," +
				"  `old` double NOT NULL," +
				"  `new` double NOT NULL" +
				");").execute();
	}

	public double getBank(UUID uuid) throws SQLException {
		connect();
		ResultSet set = this.con.prepareStatement("SELECT * FROM `bank` WHERE `uuid`='" + uuid.toString() + "'").executeQuery();
		if(set.next()) {
            return set.getDouble("money");
		} else {
			return 0;
		}
	}

	public void setBank(UUID uuid, double money) throws SQLException {
		connect();
		double old;
		ResultSet set = this.con.prepareStatement("SELECT * FROM `bank` WHERE `uuid`='" + uuid.toString() + "'").executeQuery();
		if(set.next()) {
			old = set.getDouble("money");
			this.con.prepareStatement("UPDATE `bank` SET `money` = '" + money + "' WHERE `uuid`='" + uuid.toString() + "';").execute();
		} else {
			old = 0;
			con.prepareStatement("INSERT INTO `bank` (`uuid`, `money`) VALUES ('" + uuid.toString() + "', '" + money + "')").execute();
		}
		double diff = money - old;
		PreparedStatement stat = con.prepareStatement("INSERT INTO banklogs (uuid, amount, server, old, new) VALUES (?, ?, ?, ?, ?)");
		stat.setString(1, uuid.toString());
		stat.setDouble(2, diff);
		stat.setString(3, server);
		stat.setDouble(4, old);
		stat.setDouble(5, money);
		stat.execute();
	}

	public double getMoney(UUID uuid) throws SQLException {
		connect();
		ResultSet set = this.con.prepareStatement("SELECT * FROM `" + server + "_money` WHERE `uuid`='" + uuid.toString() + "'").executeQuery();
		if(set.next()) {
            return set.getDouble("balance");
		} else {
			double start = 1000;
			setMoney(uuid, start, "Startguthaben");
			return start;
		}
	}

	public boolean hasEnough(UUID uuid, double betrag) throws SQLException {
		return getMoney(uuid) >= betrag;
	}

	public void addMoney(UUID uuid, double betrag, String reason) throws SQLException {
		setMoney(uuid, getMoney(uuid) + betrag, reason);
	}

	public boolean removeMoney(UUID uuid, double betrag, String reason) throws SQLException {
		if(hasEnough(uuid, betrag)) {
			setMoney(uuid, getMoney(uuid) - betrag, reason);
			return true;
		} else {
			return false;
		}
	}

	public void setMoney(UUID uuid, double balance, String reason) throws SQLException {
		connect();
		double old;
		ResultSet set = this.con.prepareStatement("SELECT * FROM `" + server + "_money` WHERE `uuid`='" + uuid.toString() + "'").executeQuery();
		if(set.next()) {
			old = set.getDouble("balance");
			this.con.prepareStatement("UPDATE `" + server + "_money` SET `balance` = '" + balance + "' WHERE `uuid`='" + uuid.toString() + "';").execute();
		} else {
			old = 0;
			con.prepareStatement("INSERT INTO `" + server + "_money` (`uuid`, `balance`) VALUES ('" + uuid.toString() + "', '" + balance + "')").execute();
		}
		double diff = balance - old;
		PreparedStatement stat = con.prepareStatement("INSERT INTO " + server + "_transactions (uuid, amount, reason, old, new) VALUES (?, ?, ?, ?, ?)");
		stat.setString(1, uuid.toString());
		stat.setDouble(2, diff);
		stat.setString(3, reason);
		stat.setDouble(4, old);
		stat.setDouble(5, balance);
		stat.execute();
	}

	public void disconnect() throws SQLException {
		if (con == null || con.isClosed()) {
			return;
		}
		con.close();
	}
}
