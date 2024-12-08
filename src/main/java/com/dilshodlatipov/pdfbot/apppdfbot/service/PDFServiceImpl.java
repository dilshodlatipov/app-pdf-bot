package com.dilshodlatipov.pdfbot.apppdfbot.service;

import lombok.AllArgsConstructor;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
@AllArgsConstructor
public class PDFServiceImpl implements PDFService {
    @Override
    public InputStream createDocument(String text, PDFont font, InputStream fontInputStream, float fontSize) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                if (font != null)
                    contentStream.setFont(font, fontSize);
                else {
                    PDType0Font loadedFont = PDType0Font.load(document, fontInputStream);
                    contentStream.setFont(loadedFont, fontSize);
                }
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText(text);
                contentStream.endText();
            }

            document.save(outputStream);
            document.close();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream createDocument(List<InputStream> images) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {

            images.forEach(image -> {
                try {
                    PDImageXObject imageXObject = PDImageXObject.createFromByteArray(document, image.readAllBytes(), null);
                    PDPage page = new PDPage(new PDRectangle(imageXObject.getWidth(), imageXObject.getHeight()));
                    document.addPage(page);

                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        contentStream.drawImage(imageXObject, 0, 0, imageXObject.getWidth(), imageXObject.getHeight());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            document.save(outputStream);
            document.close();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream merge(InputStream first, InputStream second) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
            pdfMergerUtility.addSource(RandomAccessReadBuffer.createBufferFromStream(first));
            pdfMergerUtility.addSource(RandomAccessReadBuffer.createBufferFromStream(second));
            pdfMergerUtility.setDestinationStream(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream merge(List<InputStream> documents) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
            documents.forEach(document -> {
                try {
                    pdfMergerUtility.addSource(RandomAccessReadBuffer.createBufferFromStream(document));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            pdfMergerUtility.setDestinationStream(out);
            pdfMergerUtility.mergeDocuments(null);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
