package com.example.demo.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.s3Report;
import com.example.demo.exception.NoReportFoundException;
import com.example.demo.repository.s3Repository;
import com.example.demo.service.s3Service;

@Service
public class s3ServiceImpl implements s3Service {

	private s3Repository s3Repo;

	
	public s3ServiceImpl(s3Repository s3Repo) {
		super();
		this.s3Repo = s3Repo;
	}

	@Override
	public List<s3Report> getAllReports() {
		return s3Repo.findAll();
	}

	@Override
	public String saveReport(s3Report data) {
		s3Repo.save(data);
		return "Report Stored Successfully";
	}

	@Override
	public s3Report getReportById(int id) {
		Optional<s3Report> report = s3Repo.findByBuildNumber(id);
		if (report.isEmpty()) {
			throw new NoReportFoundException("Report does not exist !!! ");
		}
		return report.get();
	}

	@Override
	public String getReportUrl(int id) {
		Optional<s3Report> report = s3Repo.findByBuildNumber(id);
		if (report.isEmpty()) {
			throw new NoReportFoundException("Report does not exist !!! ");
		}
		return report.get().getUrl();
	}

	
}
