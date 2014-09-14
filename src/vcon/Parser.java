package vcon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Parser {
	private Vcon vcon_ticket;
	
	public Parser() {
		vcon_ticket = new Vcon();
	}
	
	//	Parse the data
	public Vcon Parse(ArrayList<String> ar) {
		
		parseCusip(ar);
		parseType(ar);
		parseDeal(ar);
		parsePrice(ar);
		parseDate(ar);
		parseValue(ar);
		parseAccrual(ar);
		parseTotalFunds(ar);
		//Counter Party may not be included in this vcon_ticket;
		
		return vcon_ticket;
	}
	
	//Trader inclued when parsing cusip
	public void parseCusip(ArrayList<String> ar) {
		int start, end;	
		for(int i=0; i<ar.size(); i++) {
			String tmp = ar.get(i);
			if(tmp.contains("CUSIP:")) {
				start = tmp.indexOf("CUSIP:") + 7;
				vcon_ticket.setCusip(tmp.substring(start));
			}
			if(tmp.contains("TRADER:")) {
				start = tmp.indexOf("TRADER:") + 8;
				end = tmp.indexOf("CUSIP:");
				vcon_ticket.setCounterParty(tmp.substring(start, end).trim());
			}
		}
	}
	
	public void parseType(ArrayList<String> ar) {
		
		for(int i=0; i<ar.size(); i++) {
			String tmp = ar.get(i);
			if(tmp.contains("BUYS:")) 
				vcon_ticket.setType("BUY");
			else if (tmp.contains("SELLS:"))
				vcon_ticket.setType("SELL");
		}
	}
	
	public void parseDeal(ArrayList<String> ar) {
		int start, end;
		for(int i=0; i<ar.size(); i++) {
			String tmp = ar.get(i);
			if(tmp.contains("of")) {
				start = tmp.indexOf("of") + 3;
				end = tmp.indexOf(".") - 3;
				String deal = tmp.substring(start, end).trim();
				int x = deal.lastIndexOf(" ");
				vcon_ticket.setDealName(deal.substring(0,x));
				vcon_ticket.setDealClass(deal.substring(x+1));
			}
		}
		
	}
	
	public void parsePrice(ArrayList<String> ar) {
			int start, end;	
			for(int i=0; i<ar.size(); i++) {
				String tmp = ar.get(i);
				if(tmp.contains("PRICE:")) {
					start = tmp.indexOf("PRICE:")+ 7;
					end = tmp.substring(start).trim().indexOf(" ");
					tmp = tmp.substring(start).trim();
					String price = tmp.substring(0, end);
					vcon_ticket.setPrice(Double.parseDouble(price));
			}
		}
	}
	
	//Assuming there's always ended by a "]"
	public void parseDate(ArrayList<String> ar) {
		int start, end;	
		for(int i=0; i<ar.size(); i++) {
			String tmp = ar.get(i);
			if(tmp.contains("SETTLE: ")) {
				start = tmp.indexOf("SETTLE: ") + 8;
				end = tmp.indexOf("[");
				vcon_ticket.setDate(tmp.substring(start,end).trim());
			}
		}
	}

	public void parseValue(ArrayList<String> ar) {
		int start, end;
		String value;
			for(int i=0; i<ar.size(); i++) {
			String tmp = ar.get(i);
			if(tmp.contains("PRINCIPAL VAL")) {
				start = tmp.indexOf("$") + 1;
				if(tmp.substring(start).trim().contains(" ")) {
					String x = tmp.substring(start).trim();
					end = x.indexOf(" ");
					value = x.substring(0, end).replaceAll(",", "");
				}	else {
					value = tmp.substring(start).trim().replaceAll(",", "");
				}
				vcon_ticket.setValue(Double.parseDouble(value));
			}
			}
	}
	
	public void parseAccrual(ArrayList<String> ar) {
		int start, end;	
		String acc;
			for(int i=0; i<ar.size(); i++) {
			String tmp = ar.get(i);
			if(tmp.contains("ACC")) {
				start = tmp.indexOf("$") + 1;
				if(tmp.substring(start).trim().contains(" ")) {
					String x = tmp.substring(start).trim();
					end = x.indexOf(" ");
					acc = x.substring(0, end).replaceAll(",", "");
				}	else {
					acc = tmp.substring(start).trim().replaceAll(",", "");
				}
				vcon_ticket.setAccrual(Double.parseDouble(acc));
			}
			}
	}

	public void parseTotalFunds(ArrayList<String> ar) {
		int start, end;	
		String total;
		for(int i=0; i<ar.size(); i++) {
			String tmp = ar.get(i);
			if(tmp.contains("TOTAL")) {
				start = tmp.indexOf("$") + 1;
				if(tmp.substring(start).trim().contains(" ")) {
					String x = tmp.substring(start).trim();
					end = x.indexOf(" ");
					total = x.substring(0, end).replaceAll(",", "");
				}	else {
					total = tmp.substring(start).trim().replaceAll(",", "");
				}
				vcon_ticket.setTotalFunds(Double.parseDouble(total));
			}
		}
	}

	//Read data from file
	public static ArrayList<String> readFromFile (String file_path) {
		ArrayList<String> result = new ArrayList<String>();
		try 
		{
			FileReader fr = new FileReader(file_path);
			BufferedReader br = new BufferedReader(fr);
			
			String tmp = null;
			while((tmp = br.readLine()) != null) {
				result.add(tmp);
//				System.out.println(tmp);
			}
		br.close();		
		}	catch (IOException e) {
			e.printStackTrace();
		}	
		return result;
	}
	
//	//Read data from keyboard
//	public static ArrayList<String> readFromScanner() {
//		Scanner scan = new Scanner(System.in);
//		ArrayList<String> result = new ArrayList<String>();	
//		while(scan.hasNextLine())	{
//			result.add(scan.nextLine());
//		}
//		return result;
//	}
//	
}
