package com.example.markapp.models;

public class Users {
	
	String nickname, first_name, last_name, password;

	public Users() {
		
	}
	
	public Users(String nickname, String first_name, String last_name,
			String password) {
		this.nickname = nickname;
		this.first_name = first_name;
		this.last_name = last_name;
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	
}
