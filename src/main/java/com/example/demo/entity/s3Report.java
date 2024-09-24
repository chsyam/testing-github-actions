package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class s3Report {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	private String timestamp;
	
	private int buildNumber;
	
	private String url;

	public s3Report() {
		super();
	}

	public s3Report(String timestamp, int buildNumber, String url) {
		super();
		this.timestamp = timestamp;
		this.buildNumber = buildNumber;
		this.url = url;
	}

	public s3Report(int id, String timestamp, int buildNumber, String url) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.buildNumber = buildNumber;
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "s3Report [id=" + id + ", timestamp=" + timestamp + ", buildNumber=" + buildNumber + ", url=" + url
				+ "]";
	}

	

	
	
	
	
	
}
