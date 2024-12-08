package com.dilshodlatipov.pdfbot.apppdfbot.service;

import java.io.InputStream;

public interface FontService {
    void loadFonts();

    InputStream getFontInputStream(String name);

    boolean checkFont(String name);
}
