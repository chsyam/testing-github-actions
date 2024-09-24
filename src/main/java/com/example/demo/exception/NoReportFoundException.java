package com.example.demo.exception;

public class NoReportFoundException extends RuntimeException {
 
	private static final long serialVersionUID = 1L;
	private String message;
 
    public NoReportFoundException() {}
 
    public NoReportFoundException(String msg)
    {
        super(msg);
        this.message = msg;
    }
}