package com.suman.foodexpress.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MailService {

  private static final Logger log = LoggerFactory.getLogger(MailService.class);
  private static final String LOG_PREFIX = "[VERIFY OTP]";

  private final JavaMailSender mailSender;
  private final String from;
  private final boolean smtpConfigured;

  public MailService(
      ObjectProvider<JavaMailSender> mailSenderProvider,
      @Value("${app.mail.from}") String from,
      @Value("${spring.mail.host:}") String mailHost) {
    this.mailSender = mailSenderProvider.getIfAvailable();
    this.from = from;
    this.smtpConfigured = StringUtils.hasText(mailHost) && mailSender != null;
  }

  public void sendVerificationCode(String to, String otp) {
    if (!smtpConfigured) {
      logVerificationCode(to, otp);
      return;
    }
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(from);
      message.setTo(to);
      message.setSubject("Your foodExpress verification code");
      message.setText(
          "Your foodExpress email verification code is " + otp + ". It expires in 10 minutes.");
      mailSender.send(message);
    } catch (MailException ex) {
      log.error("Failed to send verification code to {}; falling back to logging it", to, ex);
      logVerificationCode(to, otp);
    }
  }

  private static void logVerificationCode(String to, String otp) {
    log.info("{} for {}: {}", LOG_PREFIX, to, otp);
  }
}
