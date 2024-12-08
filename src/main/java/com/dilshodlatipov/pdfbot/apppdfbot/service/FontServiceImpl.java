package com.dilshodlatipov.pdfbot.apppdfbot.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FontServiceImpl implements FontService {
    private final Map<String, InputStream> fonts = new HashMap<>();

    @Override
    public void loadFonts() {
        try {
            URL resource = this.getClass().getClassLoader().getResource("fonts");
            if (resource != null) {
                File file = new File(resource.getFile());
                Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(folder -> {
                    File[] files = folder.listFiles((dir, name) -> name.endsWith(".ttf") || name.endsWith(".otf"));
                    Arrays.stream(Objects.requireNonNull(files)).forEach(fontFile -> {
                        try {
                            String fontName = fontFile.getName();
                            InputStream fontStream = new FileInputStream(fontFile);
                            fontName = fontName.replaceAll("-", "")
                                    .replaceAll(" ", "")
                                    .replaceAll("\\[wdth,wght]", "regular")
                                    .replaceAll("\\[wght]", "regular")
                                    .replaceAll("\\[wdth]", "regular")
                                    .toLowerCase();
                            fontName = fontName.substring(0, fontName.length() - 4);
                            fonts.put(fontName, fontStream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public InputStream getFontInputStream(String name) {
        String key = name.toLowerCase().replaceAll("-", "").replaceAll(" ", "");
        InputStream inputStream = fonts.get(key);
        if (inputStream == null)
            inputStream = fonts.get(key + "regular");
        return inputStream;
    }

    @Override
    public boolean checkFont(String name) {
        String key = name.toLowerCase().replaceAll("-", "").replaceAll(" ", "");
        boolean b = fonts.containsKey(key);
        boolean b1 = fonts.containsKey(key + "regular");
        return b || b1;
    }
}
