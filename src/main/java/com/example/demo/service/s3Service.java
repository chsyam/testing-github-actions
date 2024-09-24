package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.s3Report;

@Service
public interface s3Service {

	List<s3Report> getAllReports();

	String saveReport(s3Report data);

	s3Report getReportById(int id);

	String getReportUrl(int id);

}
