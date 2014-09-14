package vcon;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class MyGUI extends JFrame{
	private String[] lines; //user's data
	private ArrayList<Vcon> tickets; //parsed result
	private ArrayList<String> cusip;
	
	private JPanel textPanel;
	private JLabel inputLabel;
	private JTextArea input;
	private JScrollPane scroll;
	
	private JPanel first_button_panel;
	private JButton b_ok;
	private JButton b_skip;
	private JButton b_clear;
	
	private JPanel resultPanel;
	private JScrollPane pane;
	private JTable table;
	private DefaultTableModel model;
	
	private JPanel second_button_panel;
	private JButton b_save;
	private JButton b_back;
	private JButton b_close;
	
	public MyGUI() {
		tickets = new ArrayList<Vcon>(); //parsed result
		cusip = new ArrayList<String>();
		
		//Default settings
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Vcon Ticket Parser");
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		
		//First Text Panel
		textPanel = new JPanel(new BorderLayout());
		inputLabel = new JLabel ("Please paste ticket info here: ");
		input = new JTextArea(15,60);
		scroll = new JScrollPane(input, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		//First Button Panel
		first_button_panel = new JPanel();
		first_button_panel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		b_ok = new JButton("OK");	
		b_skip = new JButton("Skip");
		b_clear = new JButton("Clear");
		first_button_panel.add(b_ok);
		first_button_panel.add(b_skip);
		first_button_panel.add(b_clear);
		
		//Nesting 2 panels
		textPanel.add(inputLabel, BorderLayout.PAGE_START);
		textPanel.add(scroll, BorderLayout.CENTER);
		textPanel.add(first_button_panel, BorderLayout.SOUTH);
		add(textPanel);
		textPanel.setVisible(true);
		pack();
		
		//Second table panel
		resultPanel = new JPanel(new BorderLayout());
		pane = new JScrollPane();
		table = new JTable();
		pane.setViewportView(table);
		model = new DefaultTableModel(
				new Object[]{"CUSIP", "Type", "Deal Name", "Deal Class", "Price", 
						"Settle Date", "Principal Value", "Accrual", "Total Funds",
						"Counter Party"}, 0);
		table.setModel(model);
		table.setColumnSelectionAllowed(true);
		
		//Second Button Panel
		second_button_panel = new JPanel();
		second_button_panel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		b_save = new JButton("Save");
		b_back = new JButton("Back");
		b_close = new JButton("Close");
		second_button_panel.add(b_save);
		second_button_panel.add(b_back);
		second_button_panel.add(b_close);
		
		//Nesting two panel
		resultPanel.add(pane, BorderLayout.CENTER);
		resultPanel.add(second_button_panel, BorderLayout.SOUTH);
		add(resultPanel);
		resultPanel.setVisible(false);
		
		b_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vcon ticket = new Vcon();
				lines = input.getText().split("\\n");
				ticket = process(lines);
				if(ticket.getCusip()!=null && !cusip.contains(ticket.getCusip())) {
					int count = model.getRowCount()+1;
					model.addRow(new Object[]{ticket.getCusip(), ticket.getType(), 
                		ticket.getDealName(), ticket.getDealClass(), ticket.getPrice(),
                		ticket.getDate(), ticket.getValue(), ticket.getAccrual(),
                		ticket.getTotalFunds(), ticket.getCounterParty()});
					textPanel.setVisible(!textPanel.isVisible());
					resultPanel.setVisible(!resultPanel.isVisible());
					cusip.add(ticket.getCusip());
					tickets.add(ticket);
				} else if(cusip.contains(ticket.getCusip())) {
						JOptionPane.showMessageDialog(null, 
							"Duplicate record!" , "Errors", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null,
							"Data not valid...", "Errors", JOptionPane.ERROR_MESSAGE);
				}	
			}
		});
		
		b_skip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				Test txt input from certain folder
//				test();
				
				textPanel.setVisible(!textPanel.isVisible());
				resultPanel.setVisible(!resultPanel.isVisible());
			}
		});
		
		b_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setText(null);
			}
		});
		
		b_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
//					//Save data to excel file, not database
//					savingToExcel(); 
					
					//Save the data into the database
					SQL sql = new SQL();
					sql.connect();
					sql.check();
					sql.save(tickets);
					sql.disconnect();
					
					JOptionPane.showMessageDialog(null,
							"Data Saved!", "Saving...", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Unable to write to file...");
					ex.printStackTrace();
				}
			}
		});
		
		b_back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setText(null);
				textPanel.setVisible(!textPanel.isVisible());
				resultPanel.setVisible(!resultPanel.isVisible());
			}
		});
		
		b_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(null, "Are you sure to Exit? " +
						"All data will be lost if not saved...", "Exit", 
					    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
					    == JOptionPane.YES_OPTION) {
					dispose();
				} else {}
			}
		});
	}
	
	private Vcon process(String[] in) {
		Vcon tic = new Vcon();
		ArrayList<String> x = new ArrayList<String>();
		for(int i=0; i<in.length; i++)
			x.add(in[i]);
		try {
			tic = (new Parser()).Parse(x);
		} catch (StringIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(null, 
					"Data not valid, please remove the comment notes and try again..." ,
					"Errors", JOptionPane.ERROR_MESSAGE);
		}
		return tic;
	}
	
	public void savingToExcel() throws IOException {
		Workbook wb;
		Sheet sheet;
		String fileName = "Vcon-" + new SimpleDateFormat("MM-dd-yy").format(new Date()) + ".xls";
		File f = new File(fileName);
	
		if(f.exists()) {
			FileInputStream fis = new FileInputStream(fileName); 
			wb = new HSSFWorkbook(fis);
			FileOutputStream fileOut = new FileOutputStream(fileName);
			sheet = wb.getSheetAt(0);
			int row_num = sheet.getPhysicalNumberOfRows();
			System.out.println(row_num);
			for(int i=0; i<model.getRowCount(); i++) {
				Row row = sheet.createRow((short) row_num+i);
				for(int j=0; j<model.getColumnCount(); j++) {
					Cell cell = row.createCell((short) j);
					cell.setCellValue(model.getValueAt(i,j).toString());
				}
			}
			wb.write(fileOut);
			fileOut.close();
			
		} else {
			wb = new HSSFWorkbook();
			FileOutputStream fileOut = new FileOutputStream(fileName);
			sheet = wb.createSheet("VCON");
			Row headerRow = sheet.createRow(0);
			for(int x=0; x<model.getColumnCount(); x++) {
				Cell headerCell = headerRow.createCell((short) x);
				headerCell.setCellValue(table.getColumnName(x));
			}
			for(int i=0; i<model.getRowCount(); i++) {
				Row row = sheet.createRow((short) i+1);
				for(int j=0; j<model.getColumnCount(); j++) {
					Cell cell = row.createCell((short) j);
					cell.setCellValue(model.getValueAt(i,j).toString());
				}
			}
			wb.write(fileOut);
			fileOut.close();
		}
	}
	
	//Test for txt input with certain folder
	public void test() {
		Parser ps;
		Vcon ticket;
		String path = "\\\\ApolloLP.com\\ApolloRoot\\Home-Americas\\zli\\Desktop\\ticket\\VCON";
		for(int i=1; i<21; i++){
			ps = new Parser();
			ticket = new Vcon();
			String p = path + i + ".txt";
			System.out.println(p);
			try {
				ArrayList<String>  al = ps.readFromFile(p);
				ticket = ps.Parse(al);
				tickets.add(ticket);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}

