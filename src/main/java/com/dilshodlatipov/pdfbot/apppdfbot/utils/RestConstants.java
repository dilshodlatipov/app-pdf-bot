package com.dilshodlatipov.pdfbot.apppdfbot.utils;

public interface RestConstants {
    float FONT_SIZE = 14;
    long TWENTY_MB = 1024 * 1024 * 20;
    String SUPPORTED_IMAGE_TYPES_REGEX = "^image\\/(png|jpg|jpeg|gif|bmp|tiff|tif)$";
    String PDF_MIME_REGEX = "^application/pdf$";
    String TIMES_NEW_ROMAN = "timesnewroman";

    String USER_CANCEL = "user.cancel";
    String INVALID_INPUT = "invalid.input";

    String IMAGE_NOT_FOUND = "image.notfound";
    String IMAGE_UNSUPPORTED = "image.unsupported";
    String IMAGE_START = "image.start";
    String REMOVE_LAST_IMAGE = "image.remove";
    String IMAGE_LIST = "image.list";
    String IMAGE_WAIT = "image.wait";
    String TO_PDF = "pdf.convert";

    String MERGE_LIMIT = "merge.limit";
    String MERGE_LIST = "merge.list";
    String MERGE_START = "merge.start";
    String REMOVE_LAST_PDF = "merge.remove";
    String MERGE_WAIT = "merge.wait";
    String PDF_NOT_FOUND = "merge.notfound";
    String MERGE_PDF = "merge.pdf";

    String TEXT_START = "text.start";

    String TEXT_FONT = "text.font";

    String TEXT_SKIP = "text.skip";
    String TEXT_WAIT = "text.wait";
}
