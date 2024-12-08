package com.dilshodlatipov.pdfbot.apppdfbot.service.bot;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.PDFDocument;
import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import com.dilshodlatipov.pdfbot.apppdfbot.payload.Order;
import org.apache.pdfbox.pdmodel.font.PDFont;

public interface AsyncConverter {

    void processDocument(Order order, UserEntity user);

    void convertImagesToPdf(UserEntity user, PDFDocument document);

    void convertTextToPDF(UserEntity user, PDFDocument document);

    void convertFilesToPDF(UserEntity user, PDFDocument document);
}
