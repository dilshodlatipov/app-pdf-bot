package com.dilshodlatipov.pdfbot.apppdfbot.repository;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.Attachment;
import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    List<Attachment> findAllByPdfDocument_IdAndDeletedFalse(UUID documentId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE attachment SET deleted = true
            WHERE id = (
                SELECT id
                FROM attachment
                WHERE created_by_id = :createdBy
                AND deleted = false
                ORDER BY created_at DESC
                LIMIT 1
            );
            """)
    void deleteLastOne(UUID createdBy);

    @Async
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM attachment WHERE created_by_id=:createdBy")
    Future<Void> deleteAllByUser(UUID createdBy);
}
