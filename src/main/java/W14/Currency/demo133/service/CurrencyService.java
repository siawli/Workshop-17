package W14.Currency.demo133.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import W14.Currency.demo133.model.CurrencyModel;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class CurrencyService {
    private String CURRENCY_API;
    private static Logger logger = 
        Logger.getLogger(CurrencyService.class.getName());

        List<CurrencyModel> objs = new ArrayList<CurrencyModel>();
        /* 
        This List<CurrencyModel> must be a variable outside of both methods (i.e savingData & gettingData)
        so that data stored in it can be used for both methods. If instantiate in the method, then the other
        method cannot used the data stored in it. So must instantiate it as a instance variable and not a 
        local variable.

        An instance variable is a variable that is declared in a class but outside a method while the 
        local variable is a variable declared within a method or a constructor.
        */

        @PostConstruct
        public void init() {
            this.CURRENCY_API = System.getenv("CURRENCY_API");
        }
        /* 
        In your command prompt, type "set CURRENCY_API=yourAPIkey" without the quotation marks
        Then use this @PostConstruct bean and public void init() to allow our IDE (visual studio)
        to get the APIKEY from our system environment
        */

    public void savingData(Model model) {
        String url = "https://free.currconv.com/api/v7/countries";
        url = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("apiKey", CURRENCY_API)
                .toUriString();
        // will get the following url https://free.currconv.com/api/v7/countries?apiKey=e35bc0197c66535a43f0

        JsonObject resultValue = gettingData(url);
        /* call the gettingData method (see below) to get the JsonObject from the API
        will passed back the below JsonObject

        resultValue is as follows:
        {"results":{"AF":{"alpha3":"AFG",
                        "currencyId":"AFN",
                        "currencyName":"Afghan afghani",
                        "currencySymbol":"؋",
                        "id":"AF",
                        "name":"Afghanistan"},
                    "AI":{"alpha3":"AIA",
                        "currencyId":"XCD",
                        "currencyName":"East Caribbean dollar",
                        "currencySymbol":"$",
                        "id":"AI",
                        "name":"Anguilla"},......}
        
        */

        JsonObject diffCountries = resultValue.getJsonObject("results");
        /* 
        getJsonObject(key) -> our key here is "results" because we want to obtain the value of "results"
        "reuslts" is the key while the value is everything after the :
        {"results":{"AF":{"alpha3":"AFG",
                        "currencyId":"AFN",
                        "currencyName":"Afghan afghani",
                        "currencySymbol":"؋",
                        "id":"AF",
                        "name":"Afghanistan"},
                    "AI":{"alpha3":"AIA",
                        "currencyId":"XCD",
                        "currencyName":"East Caribbean dollar",
                        "currencySymbol":"$",
                        "id":"AI",
                        "name":"Anguilla"},......}
        and we getJsonObject because it is a JsonObject (and not a JsonArray)
        so diffCountries will be a JsonObject that contains
        "AF":{"alpha3":"AFG",
                "currencyId":"AFN",
                "currencyName":"Afghan afghani",
                "currencySymbol":"؋",
                "id":"AF",
                "name":"Afghanistan"},
        "AI":{"alpha3":"AIA",
                "currencyId":"XCD",
                "currencyName":"East Caribbean dollar",
                "currencySymbol":"$",
                "id":"AI",
                "name":"Anguilla"},......
        */

        Set<String> countries = diffCountries.keySet();
        // Obtaining all the keys from the JsonObject diffCountries will get us "AF, AI, ..."

        for (String country : countries) {  // looping through each key obtained in the Set
            JsonObject countryValue = diffCountries.getJsonObject(country);
            /* differentCountries = JsonObject as follows
            "AI" is the key, {...} is the value
            "AF":{"alpha3":"AFG",
                "currencyId":"AFN",
                "currencyName":"Afghan afghani",
                "currencySymbol":"؋",
                "id":"AF",
                "name":"Afghanistan"},
            "AI":{"alpha3":"AIA",
                "currencyId":"XCD",
                "currencyName":"East Caribbean dollar",
                "currencySymbol":"$",
                "id":"AI",
                "name":"Anguilla"},......
            Set<String> countries = "AF, AI, ..."
            JsonObject countryValue = differentCountries.getJsonObject(country = "AI")
            so now you will get the value of the key "AI", which will be a JsonObject
            so countryValue = JsonObject of  
            {"alpha3":"AFG",
            "currencyId":"AFN",
            "currencyName":"Afghan afghani",
            "currencySymbol":"؋",
            "id":"AF",
            "name":"Afghanistan"}
            */ 

            CurrencyModel currencyModel = new CurrencyModel();
            /*
            instantiate a CurrencyModel object that contains of 
            1) private String currencyID;
            2) private String currencyAndSymbol;
            3) private String symbol;
            4) private String currencyName
            */

            String currency = countryValue.getString("currencyName") + " "
                    + countryValue.getString("currencySymbol");
            currencyModel.setCurrencyAndSymbol(currency);
            /*
            countryValue = JsonObject of  
            {"alpha3":"AFG",
            "currencyId":"AFN",
            "currencyName":"Afghan afghani",
            "currencySymbol":"؋",
            "id":"AF",
            "name":"Afghanistan"}
            so countryValue.getString(key="currencyName") will get "Afghan afghani"
            countryValue.getString(key="currencySymbol") will get "؋"
            then I combined them into String currency to form "Afghan afghani ؋"
            then set currency into your currencyModel that you previously instantiated
            so that currencyModel.setCurrencyAndSymbol = Afghan afghani ؋
            */
            currencyModel.setCurrencyName(countryValue.getString("currencyName"));
            currencyModel.setSymbol(countryValue.getString("currencySymbol"));

            currencyModel.setCurrencyID(countryValue.getString("currencyId"));
            // "currencyId":"AFN", so setting currencyModel.currencyID as "AFN"

            objs.add(currencyModel);
            // add this currencyModel into my list that was instantiated above 
            // so now obj contain of this currencyModel for this country

            /* 
            now loop through the Set<String>, so next country would be 
            "AI":{"alpha3":"AIA",
                "currencyId":"XCD",
                "currencyName":"East Caribbean dollar",
                "currencySymbol":"$",
                "id":"AI",
                "name":"Anguilla"},......
            */
        }
        model.addAttribute("countries", objs);
        /* 
        add the List<CurrencyModel> containing the different currencyModel 
        that we had added while looping as a model attribute so that we can use
        this list to get our dropdown list
        
        We are able to use model.addAttribute here because we passed in 
        the Model from the controller into this method as a parameter
        */

        // void method so we return nothing to the Controller --> ok back to 
        // the controller where we return index page
        // ok go to index html
    }

    public JsonObject gettingData(String url) {
        RequestEntity<Void> req = RequestEntity
                                    .get(url)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .build();
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);
        JsonObject resultValue = null;

        try(InputStream is = new ByteArrayInputStream(resp.getBody().getBytes())) {
            JsonReader reader = Json.createReader(is);
            resultValue = reader.readObject();

        } catch (IOException e) {
            logger.warning(e.getMessage());
            System.exit(1);
        }

        return resultValue;
    }

    public void getRate(String countryFrom, String countryTo, 
            Integer amt, Model model) {
        String url = "https://free.currconv.com/api/v7/convert";
        String conversion = countryFrom + "_" + countryTo;
        // coversion will get AFN_XCD
        url = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("q", conversion)
                .queryParam("compact", "ultra")
                .queryParam("apiKey", CURRENCY_API)
                .toUriString();
        // https://free.currconv.com/api/v7/convert?q=AFN_JPN&compact=ultra&apiKey=e35bc0197c66535a43f0
        
        JsonObject resultValue = gettingData(url);
        /* 
        call the gettingData method (see above) to get
        JsonObject {"AFN_XCD":0.030887} from the API
        so resultValue = {"AFN_XCD":0.030887}
        System.out.println(">>>> resultValue " + resultValue.toString()) to check;
        */
        
        Integer conversionRate = resultValue.getInt(conversion);
        Integer amountAfterConversion = conversionRate * amt;
        /* 
        conversion set above = AFN_XCD
        resultValue.getInt(key="AFN_XCD") which is your key
        {"AFN_XCD":0.030887} key:value
        getInt because you are getting an integer
         so conversionRate = 0.030887
        */
        CurrencyModel countryToObj = null;
        CurrencyModel countryFromObj = null;
        for (CurrencyModel country : objs) {
            /* 
            remember our instance varaible List<CurrencyModel> objs that we instantiated
            at the start and used to store our different currencyModel in the gettingData
            method? Now we are still able to use this same list with the stored data
            to loop to find the currencyModel that contains our country
            */
            if (country.getCurrencyID().equals(countryTo)) {
                countryToObj = country;
                /*
                e.g. our CurrencyModel country in the loop contains the following
                    1) private String currencyID = AFN
                    2) private String currencyAndSymbol = Afghan afghani ؋;
                    3) private String symbol = ؋;
                    4) private String currencyName = Afghanistan
                country.getCurrencyID.equals(countryTo) -->
                    country.getCurrencyID = AFN
                    .equals(countryTo=AFN) --> equal to
                then store it into the CurrencyModel countryToObj -> countryToObj = country
                    so now countryToObj will contain all 1), 2), 3), 4)
                if country.getCurrencyID = XCD; .equals(countryTo=AFN) --> 
                    then it will not be stored in countryToObj because wrong country
                */
            }
            if (country.getCurrencyID().equals(countryFrom)) {
                countryFromObj = country;
                // same as above; find the CurrencyModel object that contains
                // the country that you are finding
            }
        }

        // model was passed as a parameter in, so can call add attribute to the model here
        model.addAttribute("countryToObj", countryToObj); 
        // add attribute of CurrencyModel countryToObj to the model

        model.addAttribute("countryFromObj", countryFromObj);
        // add attribute of CurrencyModel countryToObj to the model

        model.addAttribute("amountAfterConversion", amountAfterConversion);
        model.addAttribute("amount", amt);
        
        // this method returns void
        // ok, now go to the showRate page

    }
}
