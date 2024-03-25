package ru.conditer.services;

import java.io.IOException;
import java.net.URISyntaxException;

public interface WeatherForecastService {
    String whatIsTheWeather() throws IOException, URISyntaxException;
}
