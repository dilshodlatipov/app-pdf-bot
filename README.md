# App PDF Bot

This repository contains the implementation of a Telegram bot that provides various functionalities for creating, merging, and manipulating PDF files directly through the Telegram interface.

## Features
- **PDF Creation**: Create PDFs from images or text with support for Google Fonts and embedded Times New Roman.
- **PDF Merging**: Merge multiple PDF files into one.
- **Language Support**: Choose between English, Russian, and Uzbek interfaces via inline buttons.
- **User-Friendly Interface**: Powered by Telegram inline keyboards and commands.

## Quick Start Guide

### **1. Getting Your Bot Token**
1. Open Telegram and search for **BotFather**.
2. Send the command `/newbot` to BotFather and follow the instructions:
    - Provide a name for your bot.
    - Choose a unique username (must end with "bot").
3. Once the bot is created, BotFather will provide an **API token**. Copy this token for later use.

### **2. Setting Up Your Chat IDs**
1. Create two Telegram chats:
    - **Logging Chat**: For logging bot activities.
    - **File Storage Chat**: To store PDF files.
2. Add your bot to both chats.
3. Use a tool like [RawDataBot](https://t.me/RawDataBot) to get the chat IDs:
    - Add RawDataBot to your chats.
    - Type `/start` in each chat to receive the chat ID.

### **3. Configuration**
The bot uses different configurations for development and production environments.

#### **[application.yaml](src/main/resources/application.yaml)**
```yaml  
spring:  
  application:  
    name: app-pdf-bot  
  profiles:  
    active: dev  
server:  
  port: 80  

telegram:  
  api-url: "https://api.telegram.org"  
  webhook-path: "YOUR_BOT_WEBHOOK"  
  bot-name: "YOUR_BOT_USERNAME"  
  bot-token: "YOUR_BOT_TOKEN"  
  chat-id:  
    log: CHAT_ID_FOR_LOGGING  
    file-store: CHAT_ID_FOR_STORING  

logging:  
  level:  
    root: warn  
  file:  
    name: app-pdf-bot.log  
  logback:  
    rollingpolicy:  
      max-file-size: 10MB  
      max-history: 10  
  pattern:  
    console: "%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n"  
```  

#### **[application-prod.yaml](src/main/resources/application-prod.yaml)**
```yaml  
spring:  
  datasource:  
    url: jdbc:postgresql://localhost:5432/postgres  
    username: postgres  
    password: root1234  
  jpa:  
    hibernate:  
      ddl-auto: update  
    show-sql: true  
  servlet:  
    multipart:  
      max-request-size: 100MB  
      max-file-size: 100MB  
```  

#### **[application-dev.yaml](src/main/resources/application-dev.yaml)**
```yaml  
spring:  
  datasource:  
    url: jdbc:h2:mem:mydb  
    username: sa  
    password: root1234  
    driver-class-name: org.h2.Driver  
  jpa:  
    hibernate:  
      ddl-auto: update  
    show-sql: true  
    database-platform: org.hibernate.dialect.H2Dialect  
  servlet:  
    multipart:  
      max-request-size: 100MB  
      max-file-size: 100MB  
  h2:  
    console:  
      enabled: true  
```  

### **4. Running the Bot**
1. Clone the repository:
   ```bash  
   git clone <repository_url>  
   cd app-pdf-bot  
   ```  

2. Update the `application.yml` file:
    - Replace `YOUR_BOT_WEBHOOK`, `YOUR_BOT_USERNAME`, and `YOUR_BOT_TOKEN` with your bot's information.
    - Set the `CHAT_ID_FOR_LOGGING` and `CHAT_ID_FOR_STORING` with the IDs obtained earlier.

3. Build and run the bot:
   ```bash  
   ./mvnw clean package  
   java -jar target/app-pdf-bot.jar  
   ```

Your bot is now ready to process commands!

### **Embedding Fonts**
The repository includes:

- [Google Fonts](https://fonts.google.com/): A collection of freely available fonts for use with the bot.
- Times New Roman: Included as the default font for users who skip font selection.

Fonts are dynamically loaded by the bot using the [FontService](src/main/java/com/dilshodlatipov/pdfbot/apppdfbot/service/FontService.java). Users can easily add or remove fonts by managing the files in the [fonts](src/main/resources/fonts) directory. This flexibility allows customization to meet specific font requirements without additional configuration.

### **Disclaimer**
The methods and tools described in this project may become outdated as technologies evolve. Please refer to the official documentation of the technologies used for the latest updates and configurations.

### **References and Documentation**
- [Spring Framework](https://spring.io/projects/spring-framework)
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [Apache PDFBox](https://pdfbox.apache.org/)
- [H2 Database](http://www.h2database.com/)
- [Java Telegram Bot API (telegrambots)](https://github.com/rubenlagus/TelegramBots)
- [Google Fonts](https://fonts.google.com/)

## Logging

Logs are saved in `app-pdf-bot.log`. Customize logging settings in `application.yaml` under the `logging` section.
Ensure you set up the logging group as described above.

---

## Contributing

Contributions are welcome! Feel free to fork the repository and submit pull requests.

---

## License

This project is licensed under the MIT License.