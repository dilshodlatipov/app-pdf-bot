package com.dilshodlatipov.pdfbot.apppdfbot.entity.template;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class AbsUUIDNotUserAuditEntity extends AbsDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
