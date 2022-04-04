package earthquakelister.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import earthquakelister.model.Earthquake;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@RestController
@CrossOrigin
public class EarthquakeController {

    @GetMapping("/list")
    public List<Earthquake> listEarthquake(@Valid @RequestParam int daysCount, String Country) throws ParseException, IOException {
        String endtime = LocalDate.now().toString();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(formater.parse(endtime));
        daysCount = -1 * daysCount;
        c.add(Calendar.DATE, daysCount);
        String starttime = formater.format(c.getTime());

        URL url = new URL("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + starttime + "&endtime=" + endtime);
        URLConnection eqconn = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(eqconn.getInputStream()));
        String inputLine;
        StringBuilder sb = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        in.close();

        String response = sb.toString(); //earthquake api response in string format

        ObjectMapper mapper = new ObjectMapper();
        JsonNode eqJson = mapper.readTree(response); // api response converted json

        int eqCount = Integer.parseInt(String.valueOf(eqJson.get("metadata").get("count")));

        List<Earthquake> eqArray = new ArrayList<Earthquake>();
        if(eqCount > 0) { // check is there any eq
            for (int i = 0; i < eqCount; i++) {
                //if the input country mentioned in earthquake report
                if (String.valueOf(eqJson.get("features").get(i).get("properties").get("place")).toLowerCase(Locale.ENGLISH).contains(Country.toLowerCase(Locale.ENGLISH))) {

                    double longitude = Double.parseDouble(String.valueOf(eqJson.get("features").get(i).get("geometry").get("coordinates").get(0)));
                    double latitude = Double.parseDouble(String.valueOf(eqJson.get("features").get(i).get("geometry").get("coordinates").get(1)));

                    long milisec = Long.parseLong(String.valueOf(eqJson.get("features").get(i).get("properties").get("time")));
                    Date EQdate = new Date(milisec);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String eqDate = sdf.format(EQdate);
                    String place = String.valueOf(eqJson.get("features").get(i).get("properties").get("place"));
                    double mag = Double.parseDouble(String.valueOf(eqJson.get("features").get(i).get("properties").get("mag")));

                    String reqUrl = "https://api.openweathermap.org/geo/1.0/reverse?lat=" + latitude + "&lon=" + longitude + "&appid=7a00a4f5154ed94d308565673f4a7d54";
                    //api for find the country by longitude and latitude
                    //to find out exactly which country the earthquake occurred in
                    URL url2 = new URL(reqUrl);
                    eqconn = url2.openConnection();
                    in = new BufferedReader(new InputStreamReader(eqconn.getInputStream()));
                    StringBuilder sb2 = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        sb2.append(inputLine);
                    }
                    in.close();

                    String countryResp = sb2.toString(); //geocoder api response in string format

                    JsonNode countryJson = mapper.readTree(countryResp);
                    String country;
                    if (countryJson.get(0) != null) { //if api can not find the country return undefined
                        country = countryJson.get(0).get("name") + " - " + countryJson.get(0).get("country");
                    } else {
                        country = "undefined";
                    }
                    place = place.replace("\"", "");
                    country = country.replace("\"", "");
                    Earthquake eq = new Earthquake();
                    eq.setCountry(country);
                    eq.setPlace(place);
                    eq.setMagnitude(mag);
                    eq.setDateTime(eqDate);
                    eqArray.add(eq);
                }
            }
        }
        return eqArray;
    }

}
