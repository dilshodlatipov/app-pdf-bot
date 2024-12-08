package com.dilshodlatipov.pdfbot.apppdfbot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@AllArgsConstructor
@Getter
public enum Language {
    UZBEK(new Locale("uz")),
    RUSSIAN(new Locale("ru")),
    ENGLISH(new Locale("en"));

    private final Locale locale;
}
