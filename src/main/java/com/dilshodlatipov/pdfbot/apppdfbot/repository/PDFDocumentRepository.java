package com.dilshodlatipov.pdfbot.apppdfbot.repository;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.PDFDocument;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;
import java.util.UUID;

public interface PDFDocumentRepository extends JpaRepository<PDFDocument, UUID> {

    @Async
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM pdfdocument WHERE created_by_id=:createdBy")
    void deleteByIdOfUser(UUID createdBy);

    Optional<PDFDocument> findByIdAndDeletedFalseAndStatus(UUID uuid, DocumentStatus status);

    Optional<PDFDocument> findFirstByCreatedByIdAndStatusOrderByCreatedAtDesc(UUID createdBy, DocumentStatus status);

    Optional<PDFDocument> findFirstByIdOrderByCreatedAtDesc(UUID id);
}