package com.sapient.weatherforecast.service.impl;

import java.net.URI;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.sapient.weatherforecast.WeatherForecastProperties;
import com.sapient.weatherforecast.exception.ForecastServiceException;
import com.sapient.weatherforecast.pojo.OpenWeatherAPIResp;
import com.sapient.weatherforecast.service.WeatherForecastService;

@Service("OWMWeatherForecastService")
public class OWMWeatherForecastServiceImpl implements WeatherForecastService {
	private static final String REST_CALL_EX = "Exception occured when making rest call to Open-Weather-ApI";
	private static Logger logger = LoggerFactory.getLogger(OWMWeatherForecastServiceImpl.class);
	private RestTemplate restTemplate;
	private final String owmEndpoint;
	private final String appId;
	
	public OWMWeatherForecastServiceImpl(RestTemplateBuilder restBuilder, WeatherForecastProperties weatherForecastProperties){
		this.restTemplate = restBuilder.build();	
		this.appId = weatherForecastProperties.getAppID();
		this.owmEndpoint = weatherForecastProperties.getEndpoint();
	}
	
	

	@Override
	public OpenWeatherAPIResp getFroecast(String city, String country) throws ForecastServiceException {
		OpenWeatherAPIResp openWeatherAPIResp;
		try{			
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(owmEndpoint)
			        .queryParam("appid",appId)
			        .queryParam("q", city+","+country);
			URI uri = builder.build().toUri();
			logger.info("Request Forecast for city: {} , URL:{}",city,uri.toString());
			ResponseEntity<OpenWeatherAPIResp>  openWeatherResp = restTemplate.getForEntity(uri, OpenWeatherAPIResp.class);
			openWeatherAPIResp = openWeatherResp.getBody();
			
		}catch(RestClientException restException){
			logger.error(REST_CALL_EX, restException);
			throw new ForecastServiceException(REST_CALL_EX,restException);
		}		
		return openWeatherAPIResp;
	}

}
