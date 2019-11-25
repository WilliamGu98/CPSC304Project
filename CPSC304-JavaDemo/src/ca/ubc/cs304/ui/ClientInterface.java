package ca.ubc.cs304.ui;

import java.util.ArrayList;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import ca.ubc.cs304.delegates.ClientInterfaceDelegate;
import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.ReservationReceipt;
import ca.ubc.cs304.model.ReturnReceipt;
import ca.ubc.cs304.model.VehicleModel;

import javax.swing.JFormattedTextField;
import javax.swing.border.EmptyBorder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * The class is only responsible for displaying and handling the GUI. 
 */
public class ClientInterface extends JFrame implements ActionListener {

	private static final int TEXT_FIELD_WIDTH = 10;
	// components of the window
	private JTextField carType;
	private JTextField location;
	private JFormattedTextField formatDateStart;
	private JFormattedTextField formatDateEnd;
	JTextField license;
	JTextField name;
	JTextField phone;
	JTextField address;
	JTextField cardName;

	JTextField cardNo;
	JTextField expDate;
	JTextField start;
	JTextField end;

	// delegate
	private ClientInterfaceDelegate delegate;
	JPanel contentPane;

    private MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
	}
	


	public void showFrame(ClientInterfaceDelegate delegate) {
		formatDateStart = new JFormattedTextField(createFormatter("####-##-## ##:##")); 
		formatDateStart.setColumns(TEXT_FIELD_WIDTH);

		formatDateEnd = new JFormattedTextField(createFormatter("####-##-## ##:##")); 
		formatDateEnd.setColumns(TEXT_FIELD_WIDTH);

		this.delegate = delegate;

		JLabel typeLabel = new JLabel("Vehicle Type: ");
		JLabel locationLabel = new JLabel("Location: ");
		JLabel dateLabelStart = new JLabel("Start (yyyy-MM-dd HH:mm): ");
		JLabel dateLabelEnd = new JLabel("End (yyyy-MM-dd HH:mm): ");

		carType = new JTextField(TEXT_FIELD_WIDTH);
		location = new JTextField(TEXT_FIELD_WIDTH);
		
		JButton searchButton = new JButton("Search");

		contentPane = new JPanel();
		this.setContentPane(contentPane);
		
		
		JPanel rowPane1 = new JPanel();
		rowPane1.setLayout(new FlowLayout(FlowLayout.TRAILING));
        rowPane1.add(typeLabel);
		rowPane1.add(carType);
	
		JPanel rowPane2 = new JPanel();
		rowPane2.setLayout(new FlowLayout(FlowLayout.TRAILING));
        rowPane2.add(locationLabel);
		rowPane2.add(location);
	
		JPanel rowPane3 = new JPanel();
		rowPane3.setLayout(new FlowLayout(FlowLayout.TRAILING));
        rowPane3.add(dateLabelStart);
		rowPane3.add(formatDateStart);

		JPanel rowPane4 = new JPanel();
		rowPane4.setLayout(new FlowLayout(FlowLayout.TRAILING));
        rowPane4.add(dateLabelEnd);
		rowPane4.add(formatDateEnd);
		
		
		JPanel rowPane5 = new JPanel();
		rowPane5.add(searchButton);
		rowPane5.setLayout(new FlowLayout(FlowLayout.CENTER));

		contentPane.add(rowPane1);
		contentPane.add(rowPane2);
		contentPane.add(rowPane3);
		contentPane.add(rowPane4);
		contentPane.add(rowPane5);
        
		// set Box Layout 
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		// register search button with action event handler
		searchButton.addActionListener(this);

		// anonymous inner class for closing the window
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.pack();
		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);

		// place the cursor in the text field for the username
		carType.requestFocus();
	}

	public void addVehicle(VehicleModel[] vArr, int count){
		// layout components using the GridBag layout manager
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel countL = new JLabel("total available: "+count);
		JButton moreButton = new JButton("View Details");
		// register search button with action event handler
		moreButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				 viewDetails(vArr,c,gb);
			}
		 });

		// place interval label
		JPanel row = new JPanel();
		row.add(countL);
		row.add(moreButton);
		row.setLayout(new FlowLayout(FlowLayout.CENTER));
		contentPane.add(row);

		this.pack();

		// center the frame
		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);
		
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		contentPane.removeAll();
		JPanel rowPane = new JPanel();
		rowPane.setLayout(new FlowLayout(FlowLayout.CENTER));
	
		JButton homeButton = new JButton("Home");
		rowPane.add(homeButton);
		contentPane.add(rowPane);
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				dispose();
				delegate.home();
			}
		 });

		delegate.getVehicles(String.valueOf(carType.getText()), String.valueOf(location.getText()),String.valueOf(formatDateStart.getText()),String.valueOf(formatDateEnd.getText()));
	}

	//show details of car
	public void viewDetails(VehicleModel[] vlist,GridBagConstraints c, GridBagLayout gb ) {
		contentPane.removeAll();
		JButton homeButton = new JButton("Home");
		JPanel rowPane0 = new JPanel();
		rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
		rowPane0.add(homeButton);
		contentPane.add(rowPane0);
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				contentPane.removeAll();
				dispose();
				delegate.home();
			}
		 });
		for(VehicleModel v : vlist){
			JPanel rowPane = new JPanel();
			rowPane.setLayout(new FlowLayout(FlowLayout.LEADING));
			JPanel rowPane2 = new JPanel();
			rowPane2.setLayout(new FlowLayout(FlowLayout.LEADING));
			JPanel rowPane3 = new JPanel();
			rowPane3.setLayout(new FlowLayout(FlowLayout.CENTER));
			System.out.println(v.getColor());
			JLabel makeL = new JLabel("Make: "+v.getMake());
			JLabel vlicenseL = new JLabel("License: "+v.getVlicense());
			JLabel yearL = new JLabel("Year: "+v.getYear());
			JLabel colorL = new JLabel("Color: "+v.getColor());
			JLabel odometerL = new JLabel("Odometer: "+v.getOdometer());
			JLabel statusL = new JLabel("Status: "+v.getStatus());
			JLabel vtnameL = new JLabel("Type: "+v.getVtname());
			JLabel locationL = new JLabel("Location: "+v.getLocation());
			
			rowPane.add(makeL);
			rowPane.add(vlicenseL);
			rowPane.add(yearL);
			rowPane.add(colorL);
			rowPane.add(odometerL);
			rowPane2.add(statusL);
			rowPane2.add(vtnameL);
			rowPane2.add(locationL);
			contentPane.add(rowPane);
			contentPane.add(rowPane2);
			JButton reserveBtn = new JButton("Reserve");
			rowPane3.add(reserveBtn);
			contentPane.add(rowPane3);
			reserveBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					contentPane.removeAll();
					JButton homeButton = new JButton("Home");

					JPanel rowPane0 = new JPanel();
					rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
					rowPane0.add(homeButton);
					contentPane.add(rowPane0);
			
					homeButton.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							contentPane.removeAll();
							dispose();
							delegate.home();
						}
					});
					showAccount(v);
				}
			 });
		}
		this.pack();

		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		 this.setVisible(true);
	}

	public void showAccount(VehicleModel v){
		
		JLabel typeLabel = new JLabel("Drivers License: ");
		license = new JTextField(TEXT_FIELD_WIDTH);
		JLabel nameL = new JLabel("Name: ");
		name = new JTextField(TEXT_FIELD_WIDTH);

		JLabel phoneL = new JLabel("Phone Number: ");
		phone = new JTextField(TEXT_FIELD_WIDTH);

		JLabel addressL = new JLabel("Address: ");
		address = new JTextField(TEXT_FIELD_WIDTH);

		JLabel cnameLabel = new JLabel("Card Name: ");
		cardName = new JTextField(TEXT_FIELD_WIDTH);

		JLabel cnoLabel = new JLabel("Card No: ");
		cardNo = new JTextField(TEXT_FIELD_WIDTH);

		JLabel expLabel = new JLabel("Card Exp (yyyy-MM-dd): ");
		expDate = new JFormattedTextField(createFormatter("####-##-##"));
		expDate.setColumns(TEXT_FIELD_WIDTH);

		JLabel startLabel = new JLabel("Start (yyyy-MM-dd HH:mm): ");
		start = new JFormattedTextField(createFormatter("####-##-## ##:##"));
		start.setColumns(TEXT_FIELD_WIDTH);

		JLabel endLabel = new JLabel("End (yyyy-MM-dd HH:mm): ");
		end = new JFormattedTextField(createFormatter("####-##-## ##:##")); 
		end.setColumns(TEXT_FIELD_WIDTH);

		JPanel rowPane = new JPanel();
		rowPane.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane.add(typeLabel);
		rowPane.add(license);
		

		JPanel row1 = new JPanel();
		row1.setLayout(new FlowLayout(FlowLayout.TRAILING));
		row1.add(nameL);
		row1.add(name);

		JPanel row2 = new JPanel();
		row2.setLayout(new FlowLayout(FlowLayout.TRAILING));
		row2.add(phoneL);
		row2.add(phone);

		JPanel row3 = new JPanel();
		row3.setLayout(new FlowLayout(FlowLayout.TRAILING));
		row3.add(addressL);
		row3.add(address);
		
		JPanel rowPane2 = new JPanel();
		rowPane2.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane2.add(cnameLabel);
		rowPane2.add(cardName);
		
		JPanel rowPane3 = new JPanel();
		rowPane3.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane3.add(cnoLabel);
		rowPane3.add(cardNo);

		JPanel rowPane4 = new JPanel();
		rowPane4.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane4.add(expLabel);
		rowPane4.add(expDate);
		
		JPanel rowPane5 = new JPanel();
		rowPane5.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane5.add(startLabel);
		rowPane5.add(start);
		JPanel rowPane6 = new JPanel();
		rowPane6.setLayout(new FlowLayout(FlowLayout.TRAILING));
		rowPane6.add(endLabel);
		rowPane6.add(end);

		contentPane.add(rowPane);

		contentPane.add(row1);
		contentPane.add(row2);
		contentPane.add(row3);

		contentPane.add(rowPane2);
		contentPane.add(rowPane3);
		contentPane.add(rowPane4);
		contentPane.add(rowPane5);
		contentPane.add(rowPane6);


		JButton confirmBtn = new JButton("Confirm");
		contentPane.add(confirmBtn);

		//parse time
		
		confirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				confirmAction(v);
		}
		});

		this.pack();

		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		this.setVisible(true);
	}

	public void confirmAction(VehicleModel v){
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		Date dateStart=null;
		Date dateEnd=null;
		Date exp = null;
		Instant startInstant = null;
		Instant endInstant = null;
		Instant expInstant = null;
		try {
			dateStart = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(String.valueOf(end.getText()));
			dateEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(String.valueOf(start.getText()));	
			exp = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(expDate.getText()));	
			startInstant = dateStart.toInstant();
			endInstant = dateEnd.toInstant();
			expInstant = exp.toInstant();
			

		}catch (ParseException err) {
			System.out.println("fail parse");
		}
		//check all fields present
		if(v.getVtname().length() < 1 || v.getLocation().length()<1 || String.valueOf(license.getText()).length()<1||startInstant==null || endInstant ==null || String.valueOf(cardName.getText()).length() <1 || String.valueOf(cardNo.getText()).length() <1 ||expInstant==null ||String.valueOf(license.getText()).length()<1 || String.valueOf(phone.getText()).length()<1|| String.valueOf(name.getText()).length()<1 || String.valueOf(address.getText()).length()<1 ){
			JLabel errLabel = new JLabel("ERROR: Can not have empty fields");
			contentPane.add(errLabel);
			this.pack();

			Dimension d = this.getToolkit().getScreenSize();
			Rectangle r = this.getBounds();
			this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

			// make the window visible
			this.setVisible(true);
			return;
		}

		delegate.getCustomerAccount(String.valueOf(license.getText()), String.valueOf(phone.getText()),String.valueOf(name.getText()), String.valueOf(address.getText()));
		ReservationReceipt receipt = delegate.createReservation(v.getVtname(), v.getLocation(), String.valueOf(license.getText()), startInstant, endInstant, String.valueOf(cardName.getText()), String.valueOf(cardNo.getText()), expInstant);
		contentPane.removeAll();
		JButton homeButton = new JButton("Home");

		JPanel rowPane0 = new JPanel();
		rowPane0.setLayout(new FlowLayout(FlowLayout.CENTER));
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
		JLabel vtype = new JLabel("Vehicle Type: "+receipt.getVehicleType());
		JLabel location = new JLabel("Location: "+receipt.getLocation());					
		JLabel startL = new JLabel("Start Time (yyyy-MM-dd HH:mm): "+receipt.getstartTimestamp());
		JLabel endL = new JLabel("End Time (yyyy-MM-dd HH:mm): "+receipt.getendTimestamp());

		contentPane.add(confno);
		contentPane.add(vtype);
		contentPane.add(location);
		contentPane.add(startL);
		contentPane.add(endL);
		
		this.pack();

		Dimension d = this.getToolkit().getScreenSize();
		Rectangle r = this.getBounds();
		this.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		this.setVisible(true);
	}
	
}
