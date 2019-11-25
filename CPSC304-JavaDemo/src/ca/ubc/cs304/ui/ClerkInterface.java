package ca.ubc.cs304.ui;

import java.util.ArrayList;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

import ca.ubc.cs304.delegates.ClerkInterfaceDelegate;
import ca.ubc.cs304.delegates.ClientInterfaceDelegate;
import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.DailyRentalReport;
import ca.ubc.cs304.model.DailyRentalReportBranch;
import ca.ubc.cs304.model.DailyReturnReport;
import ca.ubc.cs304.model.DailyReturnReportBranch;
import ca.ubc.cs304.model.RentalReceipt;
import ca.ubc.cs304.model.ReservationReceipt;
import ca.ubc.cs304.model.ReturnReceipt;
import ca.ubc.cs304.model.VehicleModel;

import javax.swing.JFormattedTextField;
import javax.swing.border.EmptyBorder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * The class is only responsible for displaying and handling the login GUI. 
 */
public class ClerkInterface extends JFrame{

	private static final int TEXT_FIELD_WIDTH = 10;

	JTextField confField;
	JTextField location;
	JTextField cardName;
	JTextField cardNo;
	JTextField startTimestamp;
	JTextField endTimestamp;
	JTextField expDate;
	JTextField vtname;
	JTextField dlicense; 

	JTextField rid; 
	JTextField returnTimestamp;
	JTextField endOdometer;
	JTextField fullTank;


	// delegate
	private ClerkInterfaceDelegate delegate;
	JPanel contentPane;

    private MaskFormatter createFormatter(String s){
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
	}
	


	public void showFrame(ClerkInterfaceDelegate delegate) {

		this.delegate = delegate;
		
		JButton rentButton = new JButton("Rent a Vehicle");
		JButton returnButton = new JButton("Return a Vehicle");
		JButton report1Button = new JButton("Generate Daily Report- Car Rentals");
		JButton report2Button = new JButton("Generate Daily Report- Car Returns");
		contentPane = new JPanel();
		this.setContentPane(contentPane);
	
		contentPane.add(rentButton);
		contentPane.add(returnButton);
		contentPane.add(report1Button);
		contentPane.add(report2Button);
        
		// set Box Layout 
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		// register button with action event handler
		rentButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                openRent();
			}
		 });
		 
		returnButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                openReturn();
			}
         });

		report1Button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                openReport();
			}
		 });
		 report2Button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                openReport2();
			}
         });
		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);
	}
	
	public void openReport(){
		
		contentPane.removeAll();
		JPanel rowPane0 = new JPanel();
		rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
	
		JButton homeButton = new JButton("Home");
		rowPane0.add(homeButton);
		contentPane.add(rowPane0);
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				dispose();
				delegate.home();
			}
		 });

		 
		DailyRentalReport report = delegate.getDailyRentals();
		
		JLabel totalReturns = new JLabel("Total Rentals: " + report.totalVehicles);
	
		JPanel row = new JPanel();
		row.setLayout(new FlowLayout(FlowLayout.LEADING));
		row.add(totalReturns);
		contentPane.add(row);
		contentPane.add(row);
	
		for (Map.Entry<String,DailyRentalReportBranch> entry : report.branchReports.entrySet()){  
			//create entry with branch and rental detail
			JButton detailBtn = new JButton("View Detail");
			JPanel rowPane = new JPanel();
			JPanel rowPane2 = new JPanel();
			rowPane.setLayout(new FlowLayout(FlowLayout.LEADING));
			rowPane.add(new JLabel(entry.getKey()));
			rowPane.add(detailBtn);
			contentPane.add(rowPane);
			contentPane.add(rowPane2);
			
			
			detailBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//display details for branch
					System.out.println(entry.getKey());
					DailyRentalReportBranch dailyRentalsBranch = delegate.getDailyRentalsBranch(entry.getKey());
					openRentalsBranch(dailyRentalsBranch, rowPane2);
			}
		});
		} 

		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);

	}

	public void openReport2(){
		
		contentPane.removeAll();
		JPanel rowPane0 = new JPanel();
		rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
	
		JButton homeButton = new JButton("Home");
		rowPane0.add(homeButton);
		contentPane.add(rowPane0);
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				dispose();
				delegate.home();
			}
		 });

		 
		DailyReturnReport report = delegate.getDailyReturns();
		
		JLabel totalReturns = new JLabel("Total Rentals: " + report.totalVehicles);
		JLabel totalRev = new JLabel("Total Revenue: " + report.totalRevenue);
		JPanel row = new JPanel();
		row.setLayout(new FlowLayout(FlowLayout.LEADING));
		row.add(totalReturns);
		row.add(totalRev);
		contentPane.add(row);
		contentPane.add(row);
	
		for (Map.Entry<String,DailyReturnReportBranch> entry : report.branchReports.entrySet()){  
			//create entry with branch and rental detail
			JButton detailBtn = new JButton("View Detail");
			JPanel rowPane = new JPanel();
			JPanel rowPane2 = new JPanel();
			rowPane.setLayout(new FlowLayout(FlowLayout.LEADING));
			rowPane.add(new JLabel(entry.getKey()));
			rowPane.add(detailBtn);
			contentPane.add(rowPane);
			contentPane.add(rowPane2);
			
			detailBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//display details for branch
					System.out.println(entry.getKey());
					DailyReturnReportBranch dailyReturnBranch = delegate.getDailyReturnsBranch(entry.getKey());
					openReturnBranch(dailyReturnBranch, rowPane2);
			}
		});
		} 

		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);

	}

	public void openReturnBranch(DailyReturnReportBranch dailyReturnBranch, JPanel rowPane){
	
		for (Map.Entry<String,Integer> sEntry : dailyReturnBranch.numVehicles.entrySet()){
			rowPane.setLayout(new FlowLayout(FlowLayout.LEADING));
			System.out.println(sEntry.getKey());
			rowPane.add(new JLabel(sEntry.getKey()));
			rowPane.add(new JLabel(String.valueOf(sEntry.getValue())));
		}
		for (Map.Entry<String,Double> sEntry : dailyReturnBranch.revenue.entrySet()){
			rowPane.setLayout(new FlowLayout(FlowLayout.LEADING));
			System.out.println(sEntry.getKey());
			rowPane.add(new JLabel(sEntry.getKey()));
			rowPane.add(new JLabel(String.valueOf(sEntry.getValue())));
		}
		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );
		this.setVisible(true);
	}
	
	public void openRentalsBranch(DailyRentalReportBranch dailyRentalsBranch, JPanel rowPane){
		System.out.println( dailyRentalsBranch);
		for (Map.Entry<String,Integer> sEntry : dailyRentalsBranch.numVehicles.entrySet()){
			rowPane.setLayout(new FlowLayout(FlowLayout.LEADING));
			System.out.println(sEntry.getKey());
			rowPane.add(new JLabel(sEntry.getKey()));
			rowPane.add(new JLabel(String.valueOf(sEntry.getValue())));
		}
		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );
		this.setVisible(true);
	}
	//open rent page
	public void openRent(){
		contentPane.removeAll();
		JPanel rowPane0 = new JPanel();
		rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
	
		JButton homeButton = new JButton("Home");
		rowPane0.add(homeButton);
		contentPane.add(rowPane0);
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				dispose();
				delegate.home();
			}
		 });
		JButton withRes = new JButton("Have Reservation");
		JButton noRes = new JButton("No Reservation");
	
		withRes.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                openWithRes();
			}
		 });
		noRes.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                openNoRes();
			}
		 });
		contentPane.add(withRes);
		contentPane.add(noRes);
		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);
	}

	//page: rent with reservation
	public void openWithRes(){
		contentPane.removeAll();
		JPanel rowPane0 = new JPanel();
		rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
	
		JButton homeButton = new JButton("Home");
		rowPane0.add(homeButton);
		contentPane.add(rowPane0);
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				dispose();
				delegate.home();
			}
		 });
		JLabel confNoL = new JLabel("Enter confirmation number: ");
		confField = new JTextField(TEXT_FIELD_WIDTH);
		JButton rentBtn = new JButton("Rent");
		JPanel rowPane = new JPanel();
		rowPane.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane.add(confNoL);
		rowPane.add(confField);
		
		JPanel rowPane2 = new JPanel();
		rowPane2.setLayout(new FlowLayout(FlowLayout.CENTER));
		rowPane2.add(rentBtn);
		contentPane.add(rowPane);
		contentPane.add(rowPane2);
		//add action to btn
		rentBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

				try {
					RentalReceipt receipt = delegate.createRentalWithRes(Integer.valueOf(confField.getText()), Instant.now());
					if (receipt == null){
						JOptionPane.showMessageDialog(new JFrame(), "Confirmation number not found"); //Popup error
						openWithRes();
					}
					else{
						openRentalReceipt(receipt);
					}
				} catch (Exception err) {
					JOptionPane.showMessageDialog(new JFrame(), "Input text not properly formatted"); //Popup error
					openWithRes();
				}
			}
		 });

		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);
	}

	//page: rent without reservation
	public void openNoRes(){
		contentPane.removeAll();
		JPanel rowPane0 = new JPanel();
		rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
	
		JButton homeButton = new JButton("Home");
		rowPane0.add(homeButton);
		contentPane.add(rowPane0);
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				dispose();
				delegate.home();
			}
		 });
		JLabel locationL = new JLabel("Location: ");
		location = new JTextField(TEXT_FIELD_WIDTH);
		JLabel cardNameL = new JLabel("Card Name: ");
		cardName = new JTextField(TEXT_FIELD_WIDTH);
		JLabel cardNoL = new JLabel("Card No: ");
		cardNo = new JTextField(TEXT_FIELD_WIDTH);
		JLabel expDateL = new JLabel("Card Expiration Date (yyyy-MM-dd): "); 
		expDate = new JFormattedTextField(createFormatter("####-##-##"));
		expDate.setColumns(TEXT_FIELD_WIDTH); 
		JLabel vtnameL = new JLabel("Vehicle Type: ");
		vtname = new JTextField(TEXT_FIELD_WIDTH);
		JLabel dlicenseL = new JLabel("Drivers License: ");
		dlicense = new JTextField(TEXT_FIELD_WIDTH);
		JLabel startTimestampL = new JLabel("Start (yyyy-MM-dd HH:mm): "); 
		startTimestamp =  new JFormattedTextField(createFormatter("####-##-## ##:##"));
		startTimestamp.setColumns(TEXT_FIELD_WIDTH); 
		JLabel endTimestampL = new JLabel("End (yyyy-MM-dd HH:mm): "); 
		endTimestamp =  new JFormattedTextField(createFormatter("####-##-## ##:##"));
		endTimestamp.setColumns(TEXT_FIELD_WIDTH); 
		JButton rentBtn = new JButton("Rent");
		JPanel rowPane = new JPanel();
		JPanel rowPane1 = new JPanel();
		JPanel rowPane2 = new JPanel();
		JPanel rowPane3 = new JPanel();
		JPanel rowPane4 = new JPanel();
		JPanel rowPane5 = new JPanel();
		JPanel rowPane6 = new JPanel();
		JPanel rowPane7 = new JPanel();
		JPanel rowPane8 = new JPanel();
		rowPane.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane.add(locationL);
		rowPane.add(location);

		rowPane1.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane1.add(dlicenseL);
		rowPane1.add(dlicense);

		rowPane2.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane2.add(startTimestampL);
		rowPane2.add(startTimestamp);
		
		rowPane3.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane3.add(endTimestampL);
		rowPane3.add(endTimestamp);

		rowPane4.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane4.add(vtnameL);
		rowPane4.add(vtname);

		rowPane5.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane5.add(cardNoL);
		rowPane5.add(cardNo);

		rowPane6.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane6.add(cardNameL);
		rowPane6.add(cardName);
		
		rowPane7.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane7.add(expDateL);
		rowPane7.add(expDate);
		rowPane8.setLayout(new FlowLayout(FlowLayout.CENTER));
		rowPane8.add(rentBtn);

		contentPane.add(rowPane);
		contentPane.add(rowPane1);
		contentPane.add(rowPane2);
		contentPane.add(rowPane3);
		contentPane.add(rowPane4);
		contentPane.add(rowPane5);
		contentPane.add(rowPane6);
		contentPane.add(rowPane7);
		contentPane.add(rowPane8);
		
		rentBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				Date dateStart=null;
				Date dateEnd=null;
				Date dateExp = null;
				Instant startInstant = null;
				Instant endInstant = null;
				Instant expInstant =  null;
				try {
					dateStart = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(String.valueOf(startTimestamp.getText()));
					dateEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(String.valueOf(endTimestamp.getText()));
					dateExp= new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(expDate.getText()));
					startInstant = dateStart.toInstant();
					endInstant = dateEnd.toInstant();
					expInstant = dateExp.toInstant();
					RentalReceipt receipt = delegate.createRentalNoRes(String.valueOf(location.getText()), Instant.now(), String.valueOf(cardName.getText()), String.valueOf(cardNo.getText()), expInstant, String.valueOf(vtname.getText()), String.valueOf(dlicense.getText()), startInstant, endInstant);
					if (receipt != null){
						openRentalReceipt(receipt);
					}
					else{
						JOptionPane.showMessageDialog(new JFrame(), "No matching vehicles found"); //Popup error
						openNoRes();
					}
				} catch (ParseException err) {
					JOptionPane.showMessageDialog(new JFrame(), "Input text not properly formatted"); //Popup error
					openNoRes();
				}
			}
		 });
		
		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);
	}
	
	public void openReturn(){
		contentPane.removeAll();
		JPanel rowPane0 = new JPanel();
		rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
	
		JButton homeButton = new JButton("Home");
		rowPane0.add(homeButton);
		contentPane.add(rowPane0);
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				dispose();
				delegate.home();
			}
		 });
		JLabel ridL = new JLabel("Rental ID: ");
		rid = new JTextField(TEXT_FIELD_WIDTH);
		JLabel endOdometerL = new JLabel("End Odometer: ");
		endOdometer = new JTextField(TEXT_FIELD_WIDTH);
		JLabel fullTankL = new JLabel("Full Tank (true or false): ");
		fullTank = new JTextField(TEXT_FIELD_WIDTH);
		JButton returnBtn = new JButton("Return Vehicle");

		JPanel rowPane = new JPanel();
		JPanel rowPane1 = new JPanel();
		JPanel rowPane2 = new JPanel();
		JPanel rowPane3 = new JPanel();
		
		rowPane.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane.add(ridL);
		rowPane.add(rid);
		
		rowPane1.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane1.add(endOdometerL);
		rowPane1.add(endOdometer);

		rowPane2.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane2.add(fullTankL);
		rowPane2.add(fullTank);
		
		rowPane3.setLayout(new FlowLayout(FlowLayout.CENTER));
		rowPane3.add(returnBtn);

		contentPane.add(rowPane);
		contentPane.add(rowPane1);
		contentPane.add(rowPane2);
		contentPane.add(rowPane3);

		returnBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();

				try{
					int parsedRid = Integer.valueOf(rid.getText());
					double parsedEndOdometer = Double.valueOf(endOdometer.getText());
					boolean parsedFullTank = Boolean.valueOf(fullTank.getText());
					ReturnReceipt receipt = delegate.returnVehicle(parsedRid, Instant.now(), parsedEndOdometer, parsedFullTank); //This throws exception if receipt is null
					if (receipt == null){
						JOptionPane.showMessageDialog(new JFrame(), "Rental id does not exist"); //Popup error
						openReturn();
					}
					else{
						openReturnReceipt(receipt);
					}
				} catch (Exception err){
					JOptionPane.showMessageDialog(new JFrame(), "Input text not properly formatted"); //Popup error
					openReturn();
				}
			}
		 });
		
		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);
	}
	
	//Assumes receipt is NOT NULL
	public void openReturnReceipt(ReturnReceipt receipt){
		JPanel rowPane0 = new JPanel();
		rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
	
		JButton homeButton = new JButton("Home");
		rowPane0.add(homeButton);
		contentPane.add(rowPane0);
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				dispose();
				delegate.home();
			}
		 });
		JLabel confno = new JLabel("Confirmation Number: "+receipt.getConfNo());
		JLabel hourlyRate = new JLabel("Hourly Rate: "+receipt.getHourlyRate());					
		JLabel hoursRented = new JLabel("Hours Rented: "+receipt.getHoursRented());
		JLabel hourlyTotal = new JLabel("Hourly Subtotal: "+receipt.getHourlyTotal());
		JLabel kiloRate = new JLabel("Kilo Rate: "+receipt.getKiloRate());
		JLabel gasTotal = new JLabel("Gas Subtotal: "+receipt.getGasTotal());
		JLabel distTotal = new JLabel("Distance Subtotal: "+receipt.getDistTotal());
		JLabel finalTotal = new JLabel("Final Cost: "+receipt.getFinalTotal());

		contentPane.add(confno);
		contentPane.add(hourlyRate);
		contentPane.add(kiloRate);
		contentPane.add(hoursRented);
		contentPane.add(hourlyTotal);
		contentPane.add(gasTotal);
		contentPane.add(distTotal);
		contentPane.add(finalTotal);
		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);
	}

	//Assumes receipt is NOT NULL
	public void openRentalReceipt(RentalReceipt receipt) {
		JPanel rowPane0 = new JPanel();
		rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
	
		JButton homeButton = new JButton("Home");
		rowPane0.add(homeButton);
		contentPane.add(rowPane0);
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				dispose();
				delegate.home();
			}
		});

		JLabel rid = new JLabel("Rental ID: "+receipt.getRid());
		JLabel confno = new JLabel("Reservation Confirmation Number: "+receipt.getConfNo());
		JLabel location = new JLabel("Location: "+receipt.getLocation());					
		JLabel vtype = new JLabel("Vehicle Type: "+receipt.getVehicleType());
		JLabel start = new JLabel("Start Time (yyyy-MM-dd HH:mm): "+receipt.getStartTimestamp());
		JLabel license= new JLabel("License: "+receipt.getDlicense()); 
		JLabel odometer = new JLabel("Odometer: "+receipt.getStartOdometer() + " KM");

		contentPane.add(rid);
		contentPane.add(confno);
		contentPane.add(vtype);
		contentPane.add(location);
		contentPane.add(start);
		contentPane.add(license);
		contentPane.add(odometer);

		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		this.setVisible(true);

	}
}
