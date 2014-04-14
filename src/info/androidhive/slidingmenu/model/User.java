package info.androidhive.slidingmenu.model;

public class User {
	
	private String token;
	private String email;
	
	public User(String email, String token){
		
		this.token = token;
		this.email = email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getEmail(){
		return this.email;
	}
	
	public void setToken(String token){
		this.token = token;
	}
	
	public String getToken(){
		return this.token;
	}

}
