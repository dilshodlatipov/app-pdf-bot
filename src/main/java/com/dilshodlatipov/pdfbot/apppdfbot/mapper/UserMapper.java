package com.dilshodlatipov.pdfbot.apppdfbot.mapper;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {
    //    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", source = "id")
    @Mapping(target = "username", source = "userName")
    UserEntity toUserEntity(org.telegram.telegrambots.meta.api.objects.User user);
}
