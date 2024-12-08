package com.dilshodlatipov.pdfbot.apppdfbot.payload;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    private UUID documentId;
}
