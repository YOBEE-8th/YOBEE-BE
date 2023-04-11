package com.example.yobee.user.service;

import com.example.yobee.user.repository.UserRepository;
import com.example.yobee.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@PropertySource("classpath:application.properties")
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    private final RedisUtil redisUtil;


    //인증번호 생성
    //private final String ePw = createKey();

    @Value("${spring.mail.username}")
    private String id;

    public MimeMessage createMessage(String to, String ePw)throws MessagingException, UnsupportedEncodingException {


        log.info("보내는 대상 : "+ to);
        log.info("인증 번호 : " + ePw);
        MimeMessage  message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to); // to 보내는 대상
        message.setSubject("요비 회원가입 인증 코드: "); //메일 제목

        // 메일 내용 메일의 subtype을 html로 지정하여 html문법 사용 가능
        String msg="";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 회원가입 화면에서 입력해주세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += ePw;
        msg += "</td></tr></tbody></table></div>";

        message.setText(msg, "utf-8", "html"); //내용, charset타입, subtype
        message.setFrom(new InternetAddress(id,"요비 매니저")); //보내는 사람의 메일 주소, 보내는 사람 이름

        return message;
    }

    public MimeMessage createPasswordMessage(String to, String ePw)throws MessagingException, UnsupportedEncodingException {


        log.info("보내는 대상 : "+ to);
        log.info("인증 번호 : " + ePw);
        MimeMessage  message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to); // to 보내는 대상
        message.setSubject("요비 임시 비밀번호 생성안내: "); //메일 제목

        // 메일 내용 메일의 subtype을 html로 지정하여 html문법 사용 가능
        String msg="";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">임시 비밀번호 생성</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">새로운 임시 비밀번호 생성 안내를 위해\n" +
                "발송된 메일입니다. 임시 비밀번호는 아래와 같습니다.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += ePw;
        msg += "</td></tr></tbody></table></div>";

        message.setText(msg, "utf-8", "html"); //내용, charset타입, subtype
        message.setFrom(new InternetAddress(id,"요비 매니저")); //보내는 사람의 메일 주소, 보내는 사람 이름

        return message;
    }

    // 인증코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    /*
        메일 발송
        sendSimpleMessage의 매개변수로 들어온 to는 인증번호를 받을 메일주소
        MimeMessage 객체 안에 내가 전송할 메일의 내용을 담아준다.
        bean으로 등록해둔 javaMailSender 객체를 사용하여 이메일 send
     */
    public String sendSimpleMessage(String target) {

        // 임의의 authKey 생성
        Random random = new Random();
        String ePw = String.valueOf(random.nextInt(888888) + 111111); // 범위 : 111111 ~ 999999


        MimeMessage message = null;
        try {
            message = createMessage(target, ePw);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        try {
            redisUtil.setDataExpire(ePw, target, 60 * 5L); // 유효 시간 설정하여 Redis에 저장(유효시간 5분)
            javaMailSender.send(message); // 메일 발송
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw; // 메일로 보냈던 인증 코드를 서버로 리턴
    }

    public String sendTemporaryPassword(String target) {
        // 임의의 authKey 생성
        int leftLimit = 33; // '!'
        int rightLimit = 126; // '~'
        int targetStringLength = 9;
        Random random = new Random();

        int number = 0;
        int alpha = 0;
        int special = 0;
        String ePw = "";
        while (number + alpha + special < 3) {

            number = 0;
            alpha = 0;
            special = 0;

            ePw = random.ints(leftLimit,rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            for (int i = 0; i < targetStringLength; i ++) {
                int now = (int)ePw.toCharArray()[i];
                if ( now > 47 && now < 58){
                    number = 1;
                } else if ((now > 64 && now < 91) || (now > 96 && now < 123)) {
                    alpha = 1;
                } else if ("~`!@#$%\\^&*()-".indexOf(now) > -1){
                    special = 1;
                } else {
                    number = 0;
                    alpha= 0;
                    special = 0;
                    break;
                }
            }
        }

        MimeMessage message = null;
        try {
            message = createPasswordMessage(target, ePw);
            javaMailSender.send(message); // 메일 발송
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return ePw; // 메일로 보낸 임시 비밀번호를 서버로 리턴
    }

    public String verifyEmail(String key) throws ChangeSetPersister.NotFoundException {
        String memberEmail = redisUtil.getData(key);
        if (memberEmail == null) {
            throw new ChangeSetPersister.NotFoundException();
        }
        redisUtil.deleteData(key);
        return key;
    }
}