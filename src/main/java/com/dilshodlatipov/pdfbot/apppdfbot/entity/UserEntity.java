package com.dilshodlatipov.pdfbot.apppdfbot.entity;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.template.AbsUUIDEntity;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.BotStatus;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.Language;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity extends AbsUUIDEntity {
    @Column(unique = true, nullable = false)
    private Long telegramId;
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private BotStatus status;
    @Enumerated(EnumType.STRING)
    private Language language;
}
