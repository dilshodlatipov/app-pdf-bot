package com.dilshodlatipov.pdfbot.apppdfbot.service;

import com.dilshodlatipov.pdfbot.apppdfbot.entity.UserEntity;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.BotStatus;
import com.dilshodlatipov.pdfbot.apppdfbot.enums.Language;
import com.dilshodlatipov.pdfbot.apppdfbot.mapper.UserMapper;
import com.dilshodlatipov.pdfbot.apppdfbot.repository.AttachmentRepository;
import com.dilshodlatipov.pdfbot.apppdfbot.repository.PDFDocumentRepository;
import com.dilshodlatipov.pdfbot.apppdfbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PDFDocumentRepository documentRepository;
    private final AttachmentRepository attachmentRepository;

    @Override
    public UserEntity save(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        UserEntity userEntity = userMapper.toUserEntity(telegramUser);
        userEntity.setLanguage(Language.ENGLISH);
        userEntity.setStatus(BotStatus.PAGE_ZERO);
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity checkUser(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        Optional<UserEntity> userOptional = userRepository.findByTelegramId(telegramUser.getId());
        return userOptional.orElseGet(() -> save(telegramUser));
    }

    @Override
    @Transactional
    public void setStatus(UserEntity user, BotStatus status) {
        setStatus(user.getId(), status);
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public void setStatus(UUID userId, BotStatus botStatus) {
        userRepository.updateUser(userId, botStatus);
        /*if (Objects.equals(botStatus, BotStatus.PAGE_ZERO))
            clean(userId);*/
    }

    void clean(UUID userId) {
        attachmentRepository.deleteAllByUser(userId);
        documentRepository.deleteByIdOfUser(userId);
    }
}
