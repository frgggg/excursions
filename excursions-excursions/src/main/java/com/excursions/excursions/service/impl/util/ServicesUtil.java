package com.excursions.excursions.service.impl.util;

import java.util.List;

public class ServicesUtil {
    public static boolean isListNotNullNotEmpty(List list) {
        if(list == null)
            return false;
        if(list.size() < 1)
            return false;
        return true;
    }
}
