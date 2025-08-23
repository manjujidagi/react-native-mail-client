package com.mailclient;

import com.facebook.react.bridge.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class MailClientModule extends ReactContextBaseJavaModule {
    private String imapHost;
    private int imapPort;
    private String imapUsername;
    private String imapPassword;
    private String smtpHost;
    private int smtpPort;
    private String smtpUsername;
    private String smtpPassword;

    public MailClientModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "MailClientModule";
    }

    @ReactMethod
    public void connectIMAP(ReadableMap config, Promise promise) {
        imapHost = config.getString("host");
        imapPort = config.getInt("port");
        imapUsername = config.getString("username");
        imapPassword = config.getString("password");
        promise.resolve("IMAP Connected");
    }

    @ReactMethod
    public void connectSMTP(ReadableMap config, Promise promise) {
        smtpHost = config.getString("host");
        smtpPort = config.getInt("port");
        smtpUsername = config.getString("username");
        smtpPassword = config.getString("password");
        promise.resolve("SMTP Connected");
    }

    @ReactMethod
    public void sendEmail(ReadableMap mail, Promise promise) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", smtpHost);
                props.put("mail.smtp.port", String.valueOf(smtpPort));
                props.put("mail.smtp.ssl.trust", smtpHost);

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpUsername, smtpPassword);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(mail.getString("from")));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getString("to")));
                message.setSubject(mail.getString("subject"));
                message.setText(mail.getString("body"));

                Transport.send(message);
                promise.resolve("Email sent");
            } catch (Exception e) {
                promise.reject("SMTP_ERROR", e.getMessage());
            }
        }).start();
    }

    @ReactMethod
    public void fetchInbox(Promise promise) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.store.protocol", "imaps");
                Session session = Session.getInstance(props, null);
                Store store = session.getStore();
                store.connect(imapHost, imapPort, imapUsername, imapPassword);

                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);
                Message[] messages = inbox.getMessages(Math.max(1, inbox.getMessageCount() - 19), inbox.getMessageCount());

                WritableArray mails = Arguments.createArray();
                for (int i = messages.length - 1; i >= 0; i--) {
                    Message msg = messages[i];
                    WritableMap mail = Arguments.createMap();
                    mail.putString("subject", msg.getSubject());
                    mail.putString("from", msg.getFrom() != null && msg.getFrom().length > 0 ? msg.getFrom()[0].toString() : "");
                    mail.putString("date", msg.getSentDate() != null ? msg.getSentDate().toString() : "");
                    mail.putString("snippet", getSnippet(msg));
                    mails.pushMap(mail);
                }
                inbox.close(false);
                store.close();
                promise.resolve(mails);
            } catch (Exception e) {
                promise.reject("IMAP_ERROR", e.getMessage());
            }
        }).start();
    }

    private String getSnippet(Message msg) {
        try {
            Object content = msg.getContent();
            if (content instanceof String) {
                String body = (String) content;
                return body.length() > 100 ? body.substring(0, 100) : body;
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
