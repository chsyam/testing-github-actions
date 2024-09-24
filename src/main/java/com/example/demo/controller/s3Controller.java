package com.example.demo.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.s3Report;
import com.example.demo.service.jenkinsService;
import com.example.demo.service.s3Service;
import com.example.demo.service.s3Service;

@RestController
public class s3Controller {

	@Value("${username}")
	private String username;

	@Value("${password}")
	private String password;

	private s3Service s3Ser;

	private jenkinsService jenkinsSer;

	RestTemplate restTemplate = new RestTemplate();

	public s3Controller(s3Service s3Ser, jenkinsService jenkinsSer) {
		super();
		this.s3Ser = s3Ser;
		this.jenkinsSer = jenkinsSer;
	}

	@GetMapping("/reports")
	private List<s3Report> getAllReports() {
		return s3Ser.getAllReports();
	}

	@PostMapping("/saveReport")
	private String saveReport(@RequestBody s3Report data) {
		System.out.println(data);
		return s3Ser.saveReport(data);
	}

//	@GetMapping("/getReportById/{id}")
//	private s3Report getReportByBuildNumber(@PathVariable int id) {
//		return s3Ser.getReportById(id);
//	}
//
//	@GetMapping("/getReportUrl/{id}")
//	public String getReportUrlByBuildNumber(@PathVariable int id) {
//		return s3Ser.getReportUrl(id);
//	}

	@PostMapping("/generateReport")
	public ResponseEntity<String> generateReport() {
		int buildNumber = Integer.valueOf(jenkinsSer.triggerPipeline());
		String url = s3Ser.getReportUrl(buildNumber);
		String authStr = username + ":" + password;
		String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth(base64Creds);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
		return responseEntity;
	}

	@PostMapping("/createEnv")
	public Map<String, Object> generates3Report() {
		int buildNumber = Integer.valueOf(jenkinsSer.triggerPipeline());
		String url = s3Ser.getReportUrl(buildNumber);
		Map<String, Object> data = new HashMap<>();
        data.put("url", url); // The "url" is null as per the requirement

        // Create the full response map
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("message", "Successfully Geneated Kor Report");
        response.put("status", "Success");


		return response;
	}
}
