package ru.conditer.services.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.conditer.services.WeatherForecastService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Log4j
@Service
public class WeatherForecastServiceImpl implements WeatherForecastService {

    String urlString = "https://world-weather.ru/pogoda/russia/krasnodar/7days/";

    public Document getPage() throws IOException, URISyntaxException {

        URL url = new URI(urlString).toURL();
        return Jsoup.parse(url, 10000);
    }
    @Override
    public String whatIsTheWeather(){
        Document page = null;
        try {
            page = getPage();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        var days = page.select("div[class=weather-short]");
        var city = page.select("h1").first();
        String a1 = null;
        String a2 = null;
        String a3 = null;
        String a4 = null;
        String a5 = null;
        StringBuilder some = new StringBuilder();
        for (var day : days) {
            var date = day.select("div[class=dates short-d]");
            var dateRed = day.select("div[class=dates short-d red]");
            var nightsText = day.select("tr[class=night] td[class=weather-day]");
            var nightTemp = day.select("tr[class=night] td[class=weather-temperature]");
            var nightPressure = day.select("tr[class=night] td[class=weather-pressure]");
            var nightWind = day.select("tr[class=night] td[class=weather-wind]");
            var nightHumidity = day.select("tr[class=night] td[class=weather-humidity]");

            var morningText = day.select("tr[class=morning] td[class=weather-day]");
            var morningTemp = day.select("tr[class=morning] td[class=weather-temperature]");
            var morningPressure = day.select("tr[class=morning] td[class=weather-pressure]");
            var morningWind = day.select("tr[class=morning] td[class=weather-wind]");
            var morningHumidity = day.select("tr[class=morning] td[class=weather-humidity]");

            var dayText = day.select("tr[class=day] td[class=weather-day]");
            var dayTemp = day.select("tr[class=day] td[class=weather-temperature]");
            var dayPressure = day.select("tr[class=day] td[class=weather-pressure]");
            var dayWind = day.select("tr[class=day] td[class=weather-wind]");
            var dayHumidity = day.select("tr[class=day] td[class=weather-humidity]");

            var eveningText = day.select("tr[class=evening] td[class=weather-day]");
            var eveningTemp = day.select("tr[class=evening] td[class=weather-temperature]");
            var eveningPressure = day.select("tr[class=evening] td[class=weather-pressure]");
            var eveningWind = day.select("tr[class=evening] td[class=weather-wind]");
            var eveningHumidity = day.select("tr[class=evening] td[class=weather-humidity]");

            a1 = date.text() + dateRed.text();
            a2 =
                    "  "+nightsText.text()+"        "+nightTemp.text()+"        "+nightPressure.text()+"       "+nightWind.text()+"      "+nightHumidity.text();
            a3 =
                    "  "+morningText.text()+"        "+morningTemp.text()+"        "+morningPressure.text()+"       "+morningWind.text()+"      "+morningHumidity.text();
            a4 =
                    "  "+dayText.text()+"        "+dayTemp.text()+"       "+dayPressure.text()+"       "+dayWind.text()+"      "+dayHumidity.text();
            a5 =
                    "  "+eveningText.text()+"       "+eveningTemp.text()+"        "+eveningPressure.text()+"       "+eveningWind.text()+"      "+eveningHumidity.text();
            assert false;
            String a = "Время  t-C  Давление  Ветер   Влажность";
            some.append(a1).append("\n").append(a).append("\n").append(a2).append("\n").append(a3).append("\n").append(a4).append("\n").append(a5).append("\n").append("\n");
        }
        return ("Прогноз погоды в Краснодаре на 7 дней:\n" + some);
    }
}
