package info.androidhive.slidingmenu.model;

public class LowaCard {
	
	private String name;
	private String phone;
	private String address;
	private String id;
	
	public LowaCard(){
		id = "";
		name = "";
		phone = "";
		address = "";
	}
	
    public LowaCard(String id, String name, String phone, String address){
    	this.id = id;
    	this.name = name;
    	this.phone = phone;
    	this.address = address;
	}
    
    public int getId(){
    	return Integer.parseInt(this.id);
    }
		
	public void setName(String name){
		this.name = name;
	}
	
	public void setPhone(String phone){
		this.phone = phone;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getPhone(){
		return this.phone;
	}
	
	public String getAddress(){
		return this.address;
	}

}
