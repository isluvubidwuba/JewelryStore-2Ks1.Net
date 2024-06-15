package com.ks1dotnet.jewelrystore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.MailStructure;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendMail(String mail, MailStructure mailStructure) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject(mailStructure.getSubject());
        simpleMailMessage.setText(mailStructure.getMessage());
        simpleMailMessage.setTo(mail);
        mailSender.send(simpleMailMessage);
    }

    public ResponseData sendAccountForEmployee(String mail, String username, String password, String EmployName) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromMail);
            helper.setTo(mail);
            helper.setSubject("Thông tin tài khoản làm việc của bạn");

            StringBuilder message = new StringBuilder();
            message.append("<p>Kính gửi ").append(EmployName).append(",</p>");
            message.append(
                    "<p>Chúng tôi xin trân trọng thông báo tài khoản làm việc của Quý vị đã được tạo thành công.</p>");
            message.append("<p>Dưới đây là thông tin tài khoản của Quý vị:</p>");
            message.append("<p><strong>Tài khoản: ").append(username).append("</strong></p>");
            message.append("<p><strong>Mật khẩu: ").append(password).append("</strong></p>");
            message.append(
                    "<p>Quý vị vui lòng không chia sẻ tài khoản và mật khẩu này với bất kỳ ai để đảm bảo an toàn và bảo mật thông tin cá nhân.</p>");
            message.append("<p>Xin vui lòng đăng nhập và thay đổi mật khẩu để bảo mật thông tin của mình.</p>");
            message.append("<p>Trân trọng,</p>");
            message.append("<p>Phòng Nhân Sự</p>");

            helper.setText(message.toString(), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            return new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email to " + username, null);
        }
        return new ResponseData(HttpStatus.OK, "Send account to employee have id " + username + " Successfully", null);
    }

    public ResponseData sendOtpEmail(String email, String otp) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("Xác minh OTP");

            // Nội dung email
            StringBuilder message = new StringBuilder();
            message.append("<p>Kính gửi Quý khách,</p>");
            message.append("<p>Để hoàn tất việc xác minh tài khoản của bạn, vui lòng sử dụng mã OTP dưới đây:</p>");
            message.append("<p><strong>").append(otp).append("</strong></p>");
            message.append(
                    "<p>Mã này sẽ hết hạn sau 5 phút, vui lòng không chia sẻ mã này với bất kỳ ai để đảm bảo an toàn và bảo mật thông tin cá nhân.</p>");
            message.append("<p>Trân trọng,</p>");
            message.append("<p>Đội ngũ Hỗ trợ Khách hàng</p>");

            mimeMessageHelper.setText(message.toString(), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
            return new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Send OTP to " + email + " failed", null);
        }
        return new ResponseData(HttpStatus.OK, "Send OTP to " + email + " successfully", null);
    }

}
