package com.dilshodlatipov.pdfbot.apppdfbot.entity;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.template.AbsUUIDEntity;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.DocumentStatus;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class PDFDocument extends AbsUUIDEntity {

    private String telegramUniqueId;

    private String telegramId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "pdfDocument")
    @SQLRestriction(value = "deleted = false")
    @ToString.Exclude
    private List<Attachment> attachments;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String documentText;

    private String font;

    private DocumentStatus status;
}
