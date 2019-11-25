package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.ClerkInterfaceDelegate;
import ca.ubc.cs304.delegates.ClientInterfaceDelegate;
import ca.ubc.cs304.delegates.GenericInterfaceDelegate;
import ca.ubc.cs304.ui.ClerkInterface;
import ca.ubc.cs304.ui.ClientInterface;
import ca.ubc.cs304.ui.GenericInterface;
import ca.ubc.cs304.ui.LoginWindow;
import java.time.Instant;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import ca.ubc.cs304.model.VehicleModel;
<<<<<<< HEAD
import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.DailyRentalReport;
import ca.ubc.cs304.model.DailyRentalReportBranch;
import ca.ubc.cs304.model.DailyReturnReport;
import ca.ubc.cs304.model.DailyReturnReportBranch;
=======
>>>>>>> upstream/master
import ca.ubc.cs304.model.RentalReceipt;
import ca.ubc.cs304.model.ReservationReceipt;
import ca.ubc.cs304.model.ReturnReceipt;

/**
 * This is the main controller class that will orchestrate everything.
 */
public class SuperRent
		implements LoginWindowDelegate, ClientInterfaceDelegate, GenericInterfaceDelegate, ClerkInterfaceDelegate {
	private DatabaseConnectionHandler dbHandler = null;
	private LoginWindow loginWindow = null;
	private ClientInterface clientInterface = null;
	private ClerkInterface clerkInterface = null;
	private GenericInterface genericInterface = null;

	// private ClerkInterface clerkInterface = null;
	public SuperRent() {
		dbHandler = new DatabaseConnectionHandler();
	}

	private void start() {
<<<<<<< HEAD
		login("ora_rjia", "a33550161");
=======
		login("ora_wgu","a31875164");
>>>>>>> upstream/master
	}

	/**
	 * LoginWindowDelegate Implementation
	 * 
	 * connects to Oracle database with supplied username and password
	 */
	public void login(String username, String password) {
		boolean didConnect = dbHandler.login(username, password);

		if (didConnect) {
			// Once connected, remove login window and start text transaction flow
			genericInterface = new GenericInterface();
			genericInterface.showFrame(this);

		} else {
			loginWindow.handleLoginFailed();

			if (loginWindow.hasReachedMaxLoginAttempts()) {
				loginWindow.dispose();
				System.out.println("You have exceeded your number of allowed attempts");
				System.exit(-1);
			}
		}
	}

	public void home() {
		genericInterface.showFrame(this);
	}

	public void openChoice(int id) {
		if (id == 1) {
			clientInterface = new ClientInterface();
			clientInterface.showFrame(this);
		}
		if (id == 2) {
			clerkInterface = new ClerkInterface();
			clerkInterface.showFrame(this);
		}
	}

	public void getVehicles(String type, String location, String startTime, String endTime) {
		Date dateStart = null;
		Date dateEnd = null;
		VehicleModel[] vehicles;
		String typeParse = null;
		String locationParse = null;
		Instant startInstant = null;
		Instant endInstant = null;
		if (location.trim().length() != 0) {
			locationParse = location;
		}
		if (type.trim().length() != 0) {
			typeParse = type;
		}
		try {
			dateStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
			dateEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
			startInstant = dateStart.toInstant();
			endInstant = dateEnd.toInstant();
		} catch (ParseException e) {
		}

		vehicles = dbHandler.getVehicles(typeParse, locationParse, startInstant, endInstant);
		clientInterface.addVehicle(vehicles, vehicles.length);
		return;

	}

	public void getCustomerAccount(String license, String cellphone, String name, String address) {
		dbHandler.createCustomerAccount(license, cellphone, name, address);
	}

	public ReservationReceipt createReservation(String vtname, String location, String dlicense, Instant startInstant,
			Instant endInstant, String cardName, String cardNo, Instant expInstant) {
		ReservationReceipt receipt = dbHandler.createReservation(vtname, location, dlicense, startInstant, endInstant,
				cardName, cardNo, expInstant);
		return receipt;
	}

	public RentalReceipt createRentalNoRes(String location, Instant now, String cardName, String cardNo,
			Instant expDate, String vtname, String dlicense, Instant startTimestamp, Instant endTimestamp) {
		RentalReceipt receipt = dbHandler.createRentalNoRes(location, now, cardName, cardNo, expDate, vtname, dlicense,
				startTimestamp, endTimestamp);
		return receipt;
	}

	public RentalReceipt createRentalWithRes(int confNo, Instant now) {
		RentalReceipt receipt = dbHandler.createRentalWithRes(confNo, now);
		return receipt;
	}
	
	public ReturnReceipt returnVehicle(int rid, Instant returnTimestamp, double endOdometer, boolean fullTank){
		ReturnReceipt receipt = dbHandler.returnVehicle(rid, returnTimestamp, endOdometer, fullTank);
		return receipt;
	}

	public DailyRentalReport getDailyRentals() {
		DailyRentalReport report = dbHandler.getDailyRentals();
		return report;
	}

	public DailyRentalReportBranch getDailyRentalsBranch(String branch) {
		DailyRentalReportBranch report = dbHandler.getDailyRentalsBranch(branch);
		return report;
	}

	public DailyReturnReportBranch getDailyReturns(String branch) {
		DailyReturnReportBranch report = dbHandler.getDailyReturnsBranch(branch);
		return report;
	}
	
	// main method
	public static void main(String args[]) {
		SuperRent superRent = new SuperRent();
		superRent.start();
	}
}
