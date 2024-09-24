package com.example.demo.service.serviceImpl;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.demo.service.jenkinsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class jenkinsServiceImpl implements jenkinsService {

	@Value("${jenkinsurl}")
	private String jenkinsurl;

	@Value("${username}")
	private String username;

	@Value("${password}")
	private String password;

	@Value("${jenkinstoken}")
	private String token;

	boolean responsecode = false;

	RestTemplate restTemplate = new RestTemplate();

	public String triggerPipeline() {

		String authStr = username + ":" + password;
		String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth(base64Creds);

		String Jenkinsurl = jenkinsurl + "/job/s3-policies" + "/build?token=" + token;
		ResponseEntity<JsonNode> response = restTemplate.postForEntity(Jenkinsurl, new HttpEntity<>(headers),
				JsonNode.class);
		String locationHeader = response.getHeaders().getFirst("Location");
		String authHeaderValue = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		String buildNumber = RetrieveBuildNumber(locationHeader, authHeaderValue);
		responsecode = response.getStatusCode().is2xxSuccessful();

		if (responsecode) {
			System.out.println("Job triggered successfully");
			return waitForBuildCompletion(buildNumber, authHeaderValue);
		} else {
			System.out.println("Job has not been triggered");
			return "The job has not been triggered";
		}
	}

	public String RetrieveBuildNumber(String jobQueueUrl, String authHeaderValue) {
		try {
			int maxAttempts = 10;
			int pollingIntervalSeconds = 10;

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", authHeaderValue);

			for (int attempt = 1; attempt <= maxAttempts; attempt++) {
				String apiUrl = jobQueueUrl + "api/json";

				RestTemplate restTemplate = new RestTemplate();
				HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
				ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity,
						String.class);

				if (responseEntity.getStatusCode() == HttpStatus.OK) {
					String jsonResponse = responseEntity.getBody();
					String buildNumber = extractBuildNumberFromJson(jsonResponse);

					if (buildNumber != null) {
						return buildNumber;
					}
				}

				Thread.sleep(pollingIntervalSeconds * 1000);
			}

			return null;
		} catch (Exception e) {
			System.out.print("exception: " + e);
			return null;
		}
	}

	private String extractBuildNumberFromJson(String jsonResponse) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(jsonResponse);
			if (jsonNode.has("executable")) {
				JsonNode executableNode = jsonNode.get("executable");
				if (executableNode.has("number")) {
					int buildNumber = executableNode.get("number").asInt();
					return String.valueOf(buildNumber);
				}
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private String waitForBuildCompletion(String buildNumber, String authHeaderValue) {
		try {
			String apiUrl = jenkinsurl + "/job/kor/" + buildNumber + "/api/json";
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", authHeaderValue);

			while (true) {
				HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
				ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity,
						String.class);

				if (responseEntity.getStatusCode() == HttpStatus.OK) {
					String jsonResponse = responseEntity.getBody();
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode jsonNode = objectMapper.readTree(jsonResponse);

					// Check if the build is complete
					if (jsonNode.has("building") && !jsonNode.get("building").asBoolean()) {
						// Build has completed, return the result
						if (jsonNode.has("result")) {
							return buildNumber;
						}
					}
				}

				// Sleep for a few seconds before polling again

				Thread.sleep(5000);
			}

		} catch (Exception e) {
			return "Error occurred: " + e.getMessage();
		}
	}

}


//
//import java.util.Base64;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import com.example.demo.service.jenkinsService;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@Service
//public class jenkinsServiceImpl implements jenkinsService{
//
//	@Value("${jenkinsurl}")
//	private String jenkinsurl;
//
//	@Value("${username}")
//	private String username;
//
//	@Value("${password}")
//	private String password;
//
//	@Value("${jenkinstoken}")
//	private String token;
//
//	boolean responsecode = false;
//
//	RestTemplate restTemplate = new RestTemplate();
//	
//	public String triggerPipeline() {
//
//		String authStr = username + ":" + password;
//		String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
//
//		HttpHeaders headers = new HttpHeaders();
//
//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//		headers.setBasicAuth(base64Creds);
//		
//		String Jenkinsurl = jenkinsurl + "/job/kor" + "/build?token=" + token;
//		ResponseEntity<JsonNode> response = restTemplate.postForEntity(Jenkinsurl,
//				new HttpEntity<>(headers), JsonNode.class);
//		String locationHeader = response.getHeaders().getFirst("Location");
//		String authHeaderValue = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
//		String buildNumber = RetrieveBuildNumber(locationHeader, authHeaderValue);
//		responsecode = response.getStatusCode().is2xxSuccessful();
//		if (responsecode) {
//			System.out.println("Job triggered successfully");
//			return buildNumber;
//		}
//
//		else {
//			System.out.println("Job has not been triggered");
//			return "The job  has not been triggered";
//		}
//
//	}
//
//	public String RetrieveBuildNumber(String jobQueueUrl, String authHeaderValue) {
//		try {
//			int maxAttempts = 10;
//			int pollingIntervalSeconds = 10;
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.set("Authorization", authHeaderValue);
//
//			for (int attempt = 1; attempt <= maxAttempts; attempt++) {
//				String apiUrl = jobQueueUrl + "api/json";
//
//				RestTemplate restTemplate = new RestTemplate();
//				HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
//				ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity,
//						String.class);
//
//				if (responseEntity.getStatusCode() == HttpStatus.OK) {
//					String jsonResponse = responseEntity.getBody();
//					String buildNumber = extractBuildNumberFromJson(jsonResponse);
//
//					if (buildNumber != null) {
//						return buildNumber;
//					}
//				}
//
//				Thread.sleep(pollingIntervalSeconds * 1000);
//			}
//
//			return null;
//		} catch (Exception e) {
//			System.out.print("exception: " + e);
//			return null;
//		}
//	}
//
//	private String extractBuildNumberFromJson(String jsonResponse) {
//		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode jsonNode = objectMapper.readTree(jsonResponse);
//			if (jsonNode.has("executable")) {
//				JsonNode executableNode = jsonNode.get("executable");
//				if (executableNode.has("number")) {
//					int buildNumber = executableNode.get("number").asInt();
//					return String.valueOf(buildNumber);
//				}
//			}
//
//			return null;
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//}