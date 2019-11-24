package ca.ubc.cs304.delegates;

import java.time.Instant;

import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.RentalReceipt;
import ca.ubc.cs304.model.ReservationReceipt;
import ca.ubc.cs304.model.ReturnReceipt;

public interface ClerkInterfaceDelegate{
    public void home();
    public ReturnReceipt returnVehicle(int rid, Instant returnTimestamp, double endOdometer, boolean fullTank);
    public RentalReceipt createRentalWithRes(int confNo, Instant now);
	public RentalReceipt createRentalNoRes(String location, Instant now, String cardName, String cardNo, Instant expDate, String vtname, String dlicense, Instant startTimestamp, Instant endTimestamp);
}
