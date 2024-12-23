package com.dilshodlatipov.pdfbot.apppdfbot.service;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.InputStream;
import java.util.List;

public interface PDFService {

    InputStream createDocument(String text, InputStream fontInputStream, float fontSize);

    InputStream createDocument(List<InputStream> images);

    InputStream merge(InputStream first, InputStream second);

    InputStream merge(List<InputStream> documents);
}
