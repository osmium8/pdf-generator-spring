package com.example.pdfgenerator.util;

public class ConverterUtil {

    private ConverterUtil(){}

    public static String convertByteArrayToString(byte[] array) {
        return new String(array);
    }

    public static byte[] convertStringToByteArray(String string) {
        return string.getBytes();
    }
}
