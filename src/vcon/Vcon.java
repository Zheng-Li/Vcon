package vcon;

public class Vcon {
	private String cusip;
	private String type;
	private String d_name;
	private String d_class;
	private double price;
	private String date;
	private double p_value;
	private double acc;
	private double total;
	private String c_party;
	
	public Vcon() {}
	
	public Vcon(String cusip, String type, String d_name, String d_class, 
			double price, String date, double p_value, double acc, double total, String c_party ) {
		this.cusip = cusip;
		this.type = type;
		this.d_name = d_name;
		this.d_class = d_class;
		this.price = price;
		this.date = date;
		this.p_value = p_value;
		this.acc = acc;
		this.total = total;
		this.c_party = c_party;
	}
	
	public void setCusip(String cusip) {
		this.cusip = cusip;
	}
	
	public String getCusip() {
		return cusip;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public void setDealName(String d_name) {
		this.d_name = d_name;
	}
	
	public String getDealName() {
		return d_name;
	}
	
	public void setDealClass(String d_class) {
		this.d_class = d_class;
	}
	
	public String getDealClass() {
		return d_class;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setValue(double p_value) {
		this.p_value = p_value;
	}
	
	public double getValue() {
		return p_value;
	}
	
	public void setAccrual(double acc) {
		this.acc = acc;
	}
	
	public double getAccrual() {
		return acc;
	}
	
	public void setTotalFunds(double total) {
		this.total = total;
	}
	
	public double getTotalFunds() {
		return total;
	}
	
	public void setCounterParty(String c_party) {
		this.c_party = c_party;
	}
	
	public String getCounterParty() {
		return c_party;
	}
	
	public String getPrimaryKey() {
		String key;
		key = "<"+ this.cusip + "," + this.date + ">";
		return key;
	}

}
