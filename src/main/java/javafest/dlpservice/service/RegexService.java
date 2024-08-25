package javafest.dlpservice.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class RegexService {

    public boolean doesStringMatchRegex(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input).matches();
    }
}
