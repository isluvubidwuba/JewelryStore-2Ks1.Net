package com.ks1dotnet.jewelrystore.utils;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class Utils {
    public static String generateOtp() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999);
        String output = Integer.toString(randomNumber);
        while (output.length() < 6)
            output = "0" + output;
        return output;
    }

}
