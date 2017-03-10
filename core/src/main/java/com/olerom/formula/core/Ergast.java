package com.olerom.formula.core;

import com.olerom.formula.core.objects.Circuit;
import com.olerom.formula.core.objects.Constructor;
import com.olerom.formula.core.objects.Driver;
import com.olerom.formula.core.objects.Season;
import com.olerom.formula.core.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Date: 08.03.17
 *
 * @author olerom
 */
public class Ergast {

    private final static String USER_AGENT = "Mozilla/5.0";
    private final static String DRIVERS_REQ = "http://ergast.com/api/{SERIES}/{SEASON}/drivers.json?limit={LIMIT}&offset={OFFSET}";
    private final static String CIRCUITS_REQ = "http://ergast.com/api/{SERIES}/{SEASON}/circuits.json?limit={LIMIT}&offset={OFFSET}";
    private final static String CONSTRUCTORS_REQ = "http://ergast.com/api/{SERIES}/{SEASON}/constructors.json?limit={LIMIT}&offset={OFFSET}";
    private final static String SEASONS_REQ = "http://ergast.com/api/{SERIES}/{SEASON}/seasons.json?limit={LIMIT}&offset={OFFSET}";

    /**
     * @param season season which you want to get, (-1) if you want to get all the seasons.
     * @param limit  the number of results that are returned. up to a maximum value of 1000.
     *               Please use the smallest value that your application needs. If (-1), the default value is 30.
     * @param offset specifies an offset into the result set.
     * @return list of drivers that satisfy your query
     */
    public List<Driver> getDrivers(int season, int limit, int offset) throws IOException {
        String url = getUrl(DRIVERS_REQ, season, limit, offset);
        String json = getJson(url);
        return new Parser().parse(json, "DriverTable", "Drivers", Driver.class);
    }

    /**
     * @param season season which you want to get, (-1) if you want to get all the seasons.
     * @param limit  the number of results that are returned. up to a maximum value of 1000.
     *               Please use the smallest value that your application needs. If (-1), the default value is 30.
     * @param offset specifies an offset into the result set.
     * @return list of circuits that satisfy your query
     */
    public List<Circuit> getCircuits(int season, int limit, int offset) throws IOException {
        String url = getUrl(CIRCUITS_REQ, season, limit, offset);
        String json = getJson(url).replaceAll("long", "lng");
        return new Parser().parse(json, "CircuitTable", "Circuits", Circuit.class);
    }

    /**
     * @param season season which you want to get, (-1) if you want to get all the seasons.
     * @param limit  the number of results that are returned. up to a maximum value of 1000.
     *               Please use the smallest value that your application needs. If (-1), the default value is 30.
     * @param offset specifies an offset into the result set.
     * @return list of seasons that satisfy your query
     */
    public List<Season> getSeasons(int season, int limit, int offset) throws IOException {
        String url = getUrl(SEASONS_REQ, season, limit, offset);
        String json = getJson(url);
        return new Parser().parse(json, "SeasonTable", "Seasons", Season.class);
    }

    /**
     * @param season season which you want to get, (-1) if you want to get all the seasons.
     * @param limit  the number of results that are returned. up to a maximum value of 1000.
     *               Please use the smallest value that your application needs. If (-1), the default value is 30.
     * @param offset specifies an offset into the result set.
     * @return list of constructors that satisfy your query
     */
    public List<Constructor> getConstructors(int season, int limit, int offset) throws IOException {
        String url = getUrl(CONSTRUCTORS_REQ, season, limit, offset);
        String json = getJson(url);
        return new Parser().parse(json, "ConstructorTable", "Constructors", Constructor.class);
    }

    private String getUrl(String url, int season, int limit, int offset) {
        return url.
                replace("{SERIES}", "f1").
                replace("{SEASON}/", season == -1 ? "" : String.valueOf(season) + "/").
                replace("{LIMIT}", limit == -1 ? "30" : String.valueOf(limit)).
                replace("{OFFSET}", offset == -1 ? "0" : String.valueOf(offset));
    }

    private String getJson(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();

        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}