package ca.ubc.cs304.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.time.Instant;
import java.time.Duration;

import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.VehicleModel;
import ca.ubc.cs304.model.ReturnReceipt;

/**
 * This class handles all database related transactions
 */
public class DatabaseConnectionHandler {
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	
	private Connection connection = null;
	
	public DatabaseConnectionHandler() {
		try {
			// Load the Oracle JDBC driver
			// Note that the path could change for new drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
	
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}


	/* OUR ADDED CODE HERE */

	/*
	Returns a list of vehicles matching query parameters
	*/
	public VehicleModel[] getVehicles(String vtname, String location, Instant startTimestamp, Instant endTimestamp){

		ArrayList<VehicleModel> result = new ArrayList<VehicleModel>();

		try {
			String query = "SELECT v.* FROM Vehicles v WHERE v.status = ?";
			if (vtname != null){
				query += " AND v.vtname = ?";
			}
			if (location != null){
				query += " AND v.location = ?";
			}

			if (startTimestamp != null && endTimestamp != null){ 
				query += " AND NOT EXISTS (SELECT * FROM Vehicles v2, reservations res WHERE v2.vid = v.vid AND res.vid = v2.vid AND res.startTimestamp < ? AND res.endTimestamp > ?)";
				// we assume that returnDate must be in the past and r.start, r.end must be in the future
			}
			PreparedStatement ps = connection.prepareStatement(query);

			query += " AND NOT EXISTS (SELECT * FROM Vehicles v2, reservations res WHERE v2.vid = v.vid AND res.vid = v2.vid AND res.startTimestamp < ? AND res.endTimestamp > ?)";

			/* Insert queries dependent on which ones aren't null */
			int argInd = 1;
			ps.setString(argInd++, "Available");
			if (vtname != null)
				ps.setString(argInd++, vtname);
			if (location != null)
				ps.setString(argInd++, location);
			if (startTimestamp != null && endTimestamp != null){
				ps.setTimestamp(argInd++, Timestamp.from(endTimestamp));
				ps.setTimestamp(argInd++, Timestamp.from(startTimestamp));
			}

			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				VehicleModel model = new VehicleModel(rs.getInt("vid"),
													rs.getString("vlicense"),
													rs.getString("make"),
													rs.getInt("year"),
													rs.getString("color"),
													rs.getDouble("odometer"),
													rs.getInt("status"),
													rs.getString("vtname"),
													rs.getString("location"));
				result.add(model);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}

		return result.toArray(new VehicleModel[result.size()]);
	}

	/*
	Creates a new customer account entry
	*/
	public boolean createCustomerAccount(String dlicense, String cellphone, String name, String address){

		try {
			String update = "INSERT INTO Customers VALUES (?,?,?,?)";

			PreparedStatement ps = connection.prepareStatement(update);
			ps.setString(1, dlicense);
			ps.setString(2, cellphone);
			ps.setString(3, name);
			ps.setString(4, address);

			ps.executeUpdate();
			connection.commit();
			ps.close();
			return true;
		} catch (SQLException e) { //Invalid query or duplicate dlicense
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			return false;
		}
	}

	/*
	Creates a new reservation entry and return the confirmation id
	Returns -1 if error occured
	*/
	public int createReservation(String vtname, String location, String dlicense, Instant startTimestamp, Instant endTimestamp, String cardName, String cardNo, Instant expDate){

		try {

			//Find a suitable vehicle
			VehicleModel[] matches = getVehicles(vtname, location, startTimestamp, endTimestamp);
			int vid;
			if (matches.length > 0){
				vid = matches[0].getVid();
			}
			else{ // No vehicles available for rental
				return -1;
			}

			String update = "INSERT INTO Reservations VALUES (?,?,?,?,?,?,?)"; //confNo autogenerated

			PreparedStatement ps = connection.prepareStatement(update);
			//Auto increment key should handle creating the confNo for us
			ps.setInt(1, vid);
			ps.setString(2, dlicense);
			ps.setTimestamp(3, Timestamp.from(startTimestamp));
			ps.setTimestamp(4, Timestamp.from(endTimestamp));
			ps.setString(5, cardName);
			ps.setString(6, cardNo);
			ps.setTimestamp(7, Timestamp.from(expDate));

			ps.executeUpdate();
			connection.commit();

			ResultSet rs = ps.getGeneratedKeys();
			ps.close();

			if (rs.next()){
				int confNo = rs.getInt(1); //Gets confNo or key
				rs.close();
				return confNo;
			}
			else{
				rs.close();
				return -1;
			}
		} catch (SQLException e) { //Invalid query
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			return -1;
		}
	}

	public int createRentalNoRes(String location, String cardName, String cardNo, Instant expDate, String vtname, String dlicense, Instant startTimestamp, Instant endTimestamp){

		int confNo = createReservation(vtname, location, dlicense, startTimestamp, endTimestamp, cardName, cardNo, expDate);
		if (confNo == -1){
			return -1;
		}
		return createRentalWithRes(confNo);
	}

	/*
	Creates a new rental entry (requires a reservation beforehand)
	*/
	public int createRentalWithRes(int confNo){

		try {
			// Confirm that reservation exists
			String query = "SELECT * FROM Reservations WHERE confNo = ?";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, confNo);
			ResultSet rs = ps.executeQuery();
			int vid;
			if (rs.next()){ //Reservation info
				vid = rs.getInt(2);
			}
			else{ //Res does not exist
				return -1;
			}
			ps.close();
			rs.close();

			//Sanity check that vehicle is still available and get odometer if it is
			query = "SELECT * FROM Vehicles WHERE vid = ?";
			ps = connection.prepareStatement(query);
			ps.setInt(1, vid);
			rs = ps.executeQuery();
			double startOdometer;
			if (rs.next() && rs.getString(8).equals("Available")){ //Vehicle exists / status
				startOdometer = rs.getInt(7);
			}
			else{ //Res does not exist
				return -1;
			}
			ps.close();
			rs.close();
			

			String update = "INSERT INTO Rentals VALUES (?,?,NULL,NULL,NULL,NULL)"; //last 4 NULLs represent return info

			//need vid, dlicense, start, end, odometer, cardname, cardno, expdate, confno
			ps = connection.prepareStatement(update);
			ps.setInt(1, confNo);
			ps.setDouble(2, startOdometer);

			ps.executeUpdate();
			connection.commit();

			rs = ps.getGeneratedKeys();
			ps.close();

			if (rs.next()){
				int rid = rs.getInt(1); //Gets rental id
				rs.close();
				return rid; //TODO: might want more information than just the rental id
			}
			else{
				rs.close();
				return -1;
			}
		} catch (SQLException e) { //Invalid query
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			return -1;
		}
	}

	/*
	Peforms a vehicle return.
	Returns a "receipt" of the cost breakdown
	If an error occurs, return null
	*/
	public ReturnReceipt returnVehicle(int rid, Instant returnTimestamp, double endOdometer, boolean fullTank){
		try {

			// Get original rental info
			String query = "SELECT rent.startOdometer, res.vid, res.startTimestamp, res.endTimestamp, res.confNo FROM Rentals rent, Reservations res WHERE rent.rid = ? AND rent.confNo = res.confNo";
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, rid);

			ResultSet rs = ps.executeQuery();
			double startOdometer;
			int vid;
			Instant startTimestamp;
			Instant endTimestamp;
			int confNo;
			if (rs.next()){ //Rental info
				startOdometer = rs.getInt(1);
				vid = rs.getInt(2);
				startTimestamp = rs.getTimestamp(3).toInstant();
				endTimestamp = rs.getTimestamp(4).toInstant();
				confNo = rs.getInt(5);
			}
			else{ //Rental does not exist
				ps.close();
				rs.close();
				return null;
			}
			ps.close();
			rs.close();

			// Get rates for vehicle type
			query = "SELECT vt.hourlyRate, vt.kiloRate, vt.kiloLimitPerHour, vt.tankRefillFee FROM Vehicles v, VehicleTypes vt WHERE v.vid = ? AND v.vtname = vt.vtname";
			ps = connection.prepareStatement(query);
			ps.setInt(1, vid);

			rs = ps.executeQuery();
			double hourlyRate;
			double kiloRate; //Only applied if exceed allotted amount
			double kiloLimitPerHour;
			double tankRefillFee;
			if (rs.next()){ //Rental info
				hourlyRate = rs.getDouble(1);
				kiloRate = rs.getDouble(2); 
				kiloLimitPerHour = rs.getDouble(3);
				tankRefillFee = rs.getDouble(4);
			}
			else{ //Vehicle type does not exist
				ps.close();
				rs.close();
				return null;
			}
			ps.close();
			rs.close();

			String update = "UPDATE Rentals SET returnTimestamp = ?, endOdometer = ?, fullTank = ?, finalCost = ? WHERE rid = ?";

			// Rate calculation
			int hoursElapsed = Math.max((int)Duration.between(startTimestamp, endTimestamp).toHours(), (int)Duration.between(startTimestamp, returnTimestamp).toHours()); //Customer must pay for at least the time reserved
			double kilosAllowed = hoursElapsed * kiloLimitPerHour;
			double kilosUsed = endOdometer - startOdometer;
			double kilosOverAllowed = Math.max(0, kilosAllowed - kilosUsed);
			double hourlyTotal = hoursElapsed * hourlyRate;
			double distTotal = kilosOverAllowed * kiloRate;
			double gasTotal = fullTank ? 0 : tankRefillFee;
			double finalCost = hourlyTotal + distTotal + gasTotal;

			ps = connection.prepareStatement(update);
			ps.setTimestamp(1, Timestamp.from(returnTimestamp));
			ps.setDouble(2, endOdometer);
			ps.setBoolean(3, fullTank);
			ps.setDouble(4, finalCost);
			ps.setInt(5, rid);

			ps.executeUpdate();
			connection.commit();
			ps.close();

			return new ReturnReceipt(confNo, rid, hourlyRate, hoursElapsed, hourlyTotal, kiloRate, kilosOverAllowed,distTotal, gasTotal, finalCost);
		} catch (SQLException e) { //Invalid query or duplicate dlicense
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			return null;
		}
		
	}

	/* OUR ADDED CODE ENDS HERE */

	public void deleteBranch(int branchId) {
		try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM branch WHERE branch_id = ?");
			ps.setInt(1, branchId);
			
			int rowCount = ps.executeUpdate(); //ResultSet rs = stmt.executeQuery("SELECT * FROM branch");
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + " Branch " + branchId + " does not exist!");
			}
			
			connection.commit();
	
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}
	
	public void insertBranch(BranchModel model) {
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO branch VALUES (?,?,?,?,?)");
			ps.setInt(1, model.getId());
			ps.setString(2, model.getName());
			ps.setString(3, model.getAddress());
			ps.setString(4, model.getCity());
			if (model.getPhoneNumber() == 0) {
				ps.setNull(5, java.sql.Types.INTEGER);
			} else {
				ps.setInt(5, model.getPhoneNumber());
			}

			ps.executeUpdate();
			connection.commit();

			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}
	
	public BranchModel[] getBranchInfo() {
		ArrayList<BranchModel> result = new ArrayList<BranchModel>();
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM branch");
		
//    		// get info on ResultSet
//    		ResultSetMetaData rsmd = rs.getMetaData();
//
//    		System.out.println(" ");
//
//    		// display column names;
//    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
//    			// get column name and print it
//    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
//    		}
			
			while(rs.next()) {
				BranchModel model = new BranchModel(rs.getString("branch_addr"),
													rs.getString("branch_city"),
													rs.getInt("branch_id"),
													rs.getString("branch_name"),
													rs.getInt("branch_phone"));
				result.add(model);
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}	
		
		return result.toArray(new BranchModel[result.size()]);
	}
	
	public void updateBranch(int id, String name) {
		try {
		  PreparedStatement ps = connection.prepareStatement("UPDATE branch SET branch_name = ? WHERE branch_id = ?");
		  ps.setString(1, name);
		  ps.setInt(2, id);
		
		  int rowCount = ps.executeUpdate();
		  if (rowCount == 0) {
		      System.out.println(WARNING_TAG + " Branch " + id + " does not exist!");
		  }
	
		  connection.commit();
		  
		  ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}	
	}
	
	public boolean login(String username, String password) {
		try {
			if (connection != null) {
				connection.close();
			}
	
			connection = DriverManager.getConnection(ORACLE_URL, username, password);
			connection.setAutoCommit(false);
	
			System.out.println("\nConnected to Oracle!");
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			return false;
		}
	}

	private void rollbackConnection() {
		try  {
			connection.rollback();	
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
}
