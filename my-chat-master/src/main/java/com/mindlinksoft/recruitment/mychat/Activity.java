package com.mindlinksoft.recruitment.mychat;

//Usage of the final keyword means it is closed for modification
public final class Activity implements Comparable<Activity> {
	
	public String sender;
	
	public int count;
	//This means it is open for extension
	public Activity(String sender,int count) {
		this.sender = sender;
		this.count = count;
	}
	
	public int getCount() {
		return this.count;
	}
	
	@Override 
	public int compareTo(Activity comparestu) {
		int compareage=((Activity)comparestu).getCount();
		return compareage - this.count;
	}
}