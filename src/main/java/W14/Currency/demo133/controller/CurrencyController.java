package W14.Currency.demo133.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import W14.Currency.demo133.service.CurrencyService;

@Controller
public class CurrencyController {
    
    @Autowired
    private CurrencyService currSvc;

    @GetMapping
    public String showConverterForm(Model model) {
        currSvc.savingData(model); 
        // call a CurrencyService method to save the information required from the API url;
        // passed model in so that we can add the model attributes in the savingData method
        // ok go to savingData in the CurrencyService class
        return "index";
    }

    @PostMapping(path="/currency") 
    // index.html will find this method as th:action="@{/currency}""
    public String showRateConversion(@ModelAttribute("countryFrom") String countryFrom, 
            @ModelAttribute("countryTo") String countryTo, 
            @ModelAttribute("amount") Integer amt, Model model) {
        // th:name="countryTo", th:name="countryTo", th:name="amount"
        // th:value="AFN"       th:value="XCD"       value from input = 500
        // so String countryFrom will store "AFN", countryTo will be "XCD", amount=500

        currSvc.getRate(countryFrom, countryTo, amt, model);
        // call a CurrencyService method to get the conversion; passed in model so that we can
        // add the model attributes inside the getRate method
        // ok now go to getRate in the CurrencyService class
        return "showRate";
    }

}
