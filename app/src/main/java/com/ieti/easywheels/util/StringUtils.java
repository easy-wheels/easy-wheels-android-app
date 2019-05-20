package com.ieti.easywheels.util;

public class StringUtils {

    public static String getNameFromEmail(String email) {
        String name;
        int i = email.indexOf('-');
        if (i == -1) i = email.indexOf('@');
        name = email.substring(0, i);
        String[] nameAndLast = name.split("\\.");
        nameAndLast[0] = nameAndLast[0].substring(0, 1).toUpperCase() + nameAndLast[0].substring(1);
        nameAndLast[1] = nameAndLast[1].substring(0, 1).toUpperCase() + nameAndLast[1].substring(1);
        name = nameAndLast[0] + " " + nameAndLast[1];
        return name;
    }
}
