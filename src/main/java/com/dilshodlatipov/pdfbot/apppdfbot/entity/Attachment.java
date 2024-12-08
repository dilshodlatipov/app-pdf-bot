package com.dilshodlatipov.pdfbot.apppdfbot.entity;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.template.AbsUUIDEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment extends AbsUUIDEntity {

    @Column(nullable = false)
    private String originalName;

    private long size;

    @Column(nullable = false)
    private String contentType;

    private String path;

    @ManyToOne(optional = false)
    private PDFDocument pdfDocument;

    @Column(nullable = false)
    private String telegramId;
}