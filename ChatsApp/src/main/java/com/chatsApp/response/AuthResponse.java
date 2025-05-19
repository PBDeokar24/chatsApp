package com.chatsApp.response;

public class AuthResponse {
	
	private String jwtString;
	private boolean isAuth;
	
	public AuthResponse(String jwtString, boolean isAuth) {
		super();
		this.jwtString = jwtString;
		this.isAuth = isAuth;
	}

	public String getJwtString() {
		return jwtString;
	}

	public void setJwtString(String jwtString) {
		this.jwtString = jwtString;
	}

	public boolean isAuth() {
		return isAuth;
	}

	public void setAuth(boolean isAuth) {
		this.isAuth = isAuth;
	}
	
	

}
