package com.ks1dotnet.jewelrystore.service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.ks1dotnet.jewelrystore.dto.MailStructure;
import com.ks1dotnet.jewelrystore.entity.AssignCountersForStaff;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.entity.Invoice;
import com.ks1dotnet.jewelrystore.entity.InvoiceDetail;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IInvoiceRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAssignShiftForStaffService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.utils.Utils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
        @Autowired
        private JavaMailSender mailSender;
        @Value("${spring.mail.username}")
        private String fromMail;
        @Autowired
        private Utils mailUtils;
        @Autowired
        private IInvoiceRepository invoiceRepository;
        @Autowired
        private IAssignShiftForStaffService assignShiftForStaffService;
        @Autowired
        private IEmployeeService employeeService;

        public void sendMail(String mail, MailStructure mailStructure) {
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setFrom(fromMail);
                simpleMailMessage.setSubject(mailStructure.getSubject());
                simpleMailMessage.setText(mailStructure.getMessage());
                simpleMailMessage.setTo(mail);
                mailSender.send(simpleMailMessage);
        }

        public ResponseData sendAccountForEmployee(String mail, String username, String password,
                        String EmployName) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                try {
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                        helper.setFrom(fromMail);
                        helper.setTo(mail);
                        helper.setSubject("Thông tin tài khoản làm việc của bạn");

                        StringBuilder message = new StringBuilder();
                        message.append("<p>Kính gửi ").append(EmployName).append(",</p>");
                        message.append("<p>Chúng tôi xin trân trọng thông báo tài khoản làm việc của Quý vị đã được tạo thành công.</p>");
                        message.append("<p>Dưới đây là thông tin tài khoản của Quý vị:</p>");
                        message.append("<p><strong>Tài khoản: ").append(username)
                                        .append("</strong></p>");
                        message.append("<p><strong>Mật khẩu: ").append(password)
                                        .append("</strong></p>");
                        message.append("<p>Quý vị vui lòng không chia sẻ tài khoản và mật khẩu này với bất kỳ ai để đảm bảo an toàn và bảo mật thông tin cá nhân.</p>");
                        message.append("<p>Xin vui lòng đăng nhập và thay đổi mật khẩu để bảo mật thông tin của mình.</p>");
                        message.append("<p>Trân trọng,</p>");
                        message.append("<p>Phòng Nhân Sự</p>");

                        helper.setText(message.toString(), true);
                        mailSender.send(mimeMessage);
                } catch (MessagingException e) {
                        e.printStackTrace();
                        return new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Failed to send email to " + username, null);
                }
                return new ResponseData(HttpStatus.OK,
                                "Send account to employee have id " + username + " Successfully",
                                null);
        }

        public ResponseData sendOtpEmail(String email, String username, String otp) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                try {
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                        helper.setFrom(fromMail);
                        helper.setTo(email);
                        helper.setSubject("Mã OTP xác nhận đổi mật khẩu");

                        StringBuilder message = new StringBuilder();
                        message.append("<html><body>");

                        // Banner
                        message.append("<div style='text-align: center; background-color: #f2f2f2; padding: 20px;'>");
                        message.append("<h1 style='color: #333;'>Công ty 2ks1dotnet</h1>");
                        message.append("<p style='color: #666;'>Dịch vụ trang sức hàng đầu</p>");
                        message.append("</div>");

                        // Nội dung chính
                        message.append("<div style='padding: 20px; text-align: center;'>");
                        message.append("<p>Kính gửi ").append(username).append(",</p>");
                        message.append("<p>Để xác nhận đổi mật khẩu, vui lòng sử dụng mã OTP sau:</p>");
                        message.append("<p><strong>").append(otp).append("</strong></p>");
                        message.append("<p>Mã này sẽ hết hạn sau 5 phút.</p>");
                        message.append("<p>Trân trọng,</p>");
                        message.append("<p>Đội ngũ hỗ trợ kỹ thuật</p>");
                        message.append("</div>");

                        // Footer
                        message.append("<div style='text-align: center; background-color: #f2f2f2; padding: 20px;'>");
                        message.append("<p style='color: #666;'>Công ty 2ks1dotnet</p>");
                        message.append("<p style='color: #666;'>Địa chỉ: Số 123, Đường ABC, Thành phố XYZ</p>");
                        message.append("<p style='color: #666;'>Email: support@2ks1dotnet.com | Điện thoại: 0123 456 789</p>");
                        message.append("</div>");

                        message.append("</body></html>");

                        helper.setText(message.toString(), true);
                        mailSender.send(mimeMessage);
                        return new ResponseData(HttpStatus.OK,
                                        "OTP sent to " + username + " successfully", null);
                } catch (MessagingException e) {
                        e.printStackTrace();
                        return new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Failed to send OTP to " + username, null);
                }
        }

        public ResponseData sendInvoiceEmail(String email, String username) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                try {
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                        helper.setFrom("your-email@example.com");
                        helper.setTo(email);
                        helper.setSubject("Hóa đơn thanh toán của bạn");

                        StringBuilder message = new StringBuilder();
                        message.append("<html><body>");

                        // Banner
                        message.append("<div style='text-align: center; background-color: #f2f2f2; padding: 20px;'>");
                        message.append("<h1 style='color: #333;'>Công ty 2ks1dotnet</h1>");
                        message.append("<p style='color: #666;'>Dịch vụ trang sức hàng đầu</p>");
                        message.append("</div>");

                        // Nội dung chính
                        message.append("<div style='padding: 20px; text-align: center;'>");
                        message.append("<p>Kính gửi ").append(username).append(",</p>");
                        message.append("<p>Dưới đây là chi tiết hóa đơn thanh toán cho dịch vụ của bạn:</p>");

                        // Thêm nội dung hóa đơn HTML tại đây
                        message.append("<table style='width:100%;'>");
                        message.append("<tr><th>Mặt hàng</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th></tr>");
                        message.append("<tr><td>Trang sức ngọc trai</td><td>1</td><td>$200</td><td>$200</td></tr>");
                        message.append("</table>");

                        message.append("<p>Trân trọng,</p>");
                        message.append("<p>Đội ngũ hỗ trợ kỹ thuật</p>");
                        message.append("</div>");

                        // Footer
                        message.append("<div style='text-align: center; background-color: #f2f2f2; padding: 20px;'>");
                        message.append("<p style='color: #666;'>Công ty 2ks1dotnet</p>");
                        message.append("<p style='color: #666;'>Địa chỉ: Số 123, Đường ABC, Thành phố XYZ</p>");
                        message.append("<p style='color: #666;'>Email: support@2ks1dotnet.com | Điện thoại: 0123 456 789</p>");
                        message.append("</div>");

                        message.append("</body></html>");

                        helper.setText(message.toString(), true);

                        mailSender.send(mimeMessage);
                        return new ResponseData(HttpStatus.OK,
                                        "Invoice sent to " + username + " successfully", null);
                } catch (MessagingException e) {
                        e.printStackTrace();
                        return new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Failed to send invoice to " + username, null);
                }
        }

        public ResponseData sendInvoiceEmail(String email, String username, int invoiceId) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                Invoice invoiceData = invoiceRepository.findById(invoiceId).get();
                try {
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                        helper.setFrom("your-email@example.com");
                        helper.setTo(email);
                        helper.setSubject("Hóa đơn thanh toán của bạn");

                        StringBuilder message = new StringBuilder();
                        message.append("<html><body>");

                        // Email content
                        message.append("<div style='background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); padding: 20px; max-width: 1000px; margin: auto;'>");
                        message.append("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;'>");
                        message.append("<div style='display: flex; align-items: center;'>");
                        message.append("<img style='width: 170px; height: auto;  margin-right: 8px;' src='https://storage.googleapis.com/jewelrystore-2ks1dotnet.appspot.com/User/c3b3e699-1466-48cf-b1ae-1db13264e44e_2024-07-12' alt='Logo' />");
                        message.append("</div>");
                        message.append("<div style='color: #4a4a4a; text-align: right;'>");
                        message.append("<div style='font-weight: bold; font-size: 24px; margin-bottom: 8px;'>HÓA ĐƠN</div>");
                        message.append("<div style='font-size: 14px;'>Ngày: ")
                                        .append(invoiceData.getDate()).append("</div>");
                        message.append("<div style='font-size: 14px;'>Số hóa đơn: ")
                                        .append(invoiceData.getId()).append("</div>");
                        message.append("</div>");
                        message.append("</div>");

                        // Customer and Employee Information
                        message.append("<div style='border-bottom: 2px solid #dcdcdc; padding-bottom: 20px; margin-bottom: 20px;'>");
                        message.append("<h2 style='font-size: 24px; font-weight: bold; margin-bottom: 16px;'>Thông Tin Khách Hàng và Nhân Viên</h2>");
                        message.append("<div  style='display: flex; justify-content: space-between;  margin-bottom: 20px;'>");
                        message.append("<div>");
                        message.append("<div style='color: #4a4a4a; margin-bottom: 8px;'><strong>Khách Hàng:</strong> ")
                                        .append(invoiceData.getUserInfo().getFullName())
                                        .append("</div>");
                        message.append("<div style='color: #4a4a4a; margin-bottom: 8px;'><strong>ID:</strong> ")
                                        .append(invoiceData.getUserInfo().getId()).append("</div>");
                        message.append("</div>");
                        message.append("<div>");
                        message.append("<div style='color: #4a4a4a; margin-bottom: 8px;'><strong>Nhân Viên:</strong> ")
                                        .append(invoiceData.getEmployee().getFirstName())
                                        .append(" ").append(invoiceData.getEmployee().getLastName())
                                        .append("</div>");
                        message.append("<div style='color: #4a4a4a; margin-bottom: 8px;'><strong>ID:</strong> ")
                                        .append(invoiceData.getEmployee().getId()).append("</div>");
                        message.append("</div>");
                        message.append("</div>");
                        message.append("</div>");

                        // Product Details Table
                        message.append("<table style='width: 100%; text-align: left; margin-bottom: 20px;'>");
                        message.append("<thead><tr>");
                        message.append("<th style='color: #4a4a4a; font-weight: bold; text-transform: uppercase; padding: 8px;'>Sản phẩm</th>");
                        message.append("<th style='color: #4a4a4a; font-weight: bold; text-transform: uppercase; padding: 8px;'>Mã sản phẩm</th>");
                        message.append("<th style='color: #4a4a4a; font-weight: bold; text-transform: uppercase; padding: 8px;'>Số lượng</th>");
                        message.append("<th style='color: #4a4a4a; font-weight: bold; text-transform: uppercase; padding: 8px;'>Tổng giá</th>");
                        message.append("</tr></thead>");
                        message.append("<tbody>");

                        for (InvoiceDetail item : invoiceData.getListOrderInvoiceDetail()) {
                                message.append("<tr>");
                                message.append("<td style='padding: 16px; color: #4a4a4a;'>")
                                                .append(item.getProduct().getName())
                                                .append("</td>");
                                message.append("<td style='padding: 16px; color: #4a4a4a;'>")
                                                .append(item.getProduct().getProductCode())
                                                .append("</td>");
                                message.append("<td style='padding: 16px; color: #4a4a4a;'>")
                                                .append(item.getQuantity()).append("</td>");
                                message.append("<td style='padding: 16px; color: #4a4a4a;'>")
                                                .append(formatCurrency(item.getTotalPrice()))
                                                .append("</td>");
                                message.append("</tr>");
                        }

                        message.append("</tbody></table>");

                        // Pricing Information
                        message.append("<div style='display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px;'>");
                        message.append("<div style='color: #4a4a4a;'>Tổng giá gốc:</div>");
                        message.append("<div style='color: #4a4a4a; text-align: right;'>")
                                        .append(formatCurrency(invoiceData.getTotalPriceRaw()))
                                        .append("</div>");
                        message.append("<div style='color: #4a4a4a;'>Giá giảm:</div>");
                        message.append("<div style='color: #4a4a4a; text-align: right;'>")
                                        .append(formatCurrency(invoiceData.getDiscountPrice()))
                                        .append("</div>");
                        message.append("<div style='color: #4a4a4a; font-weight: bold; font-size: 24px;'>Tổng giá:</div>");
                        message.append("<div style='color: #4a4a4a; font-weight: bold; font-size: 24px; text-align: right;'>")
                                        .append(formatCurrency(invoiceData.getTotalPrice()))
                                        .append("</div>");
                        message.append("</div>");

                        message.append("</div>");

                        message.append("</body></html>");

                        helper.setText(message.toString(), true);

                        mailSender.send(mimeMessage);
                        return new ResponseData(HttpStatus.OK,
                                        "Hóa đơn đã được gửi đến " + username + " thành công",
                                        null);
                } catch (MessagingException e) {
                        e.printStackTrace();
                        return new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Gửi hóa đơn đến " + username + " thất bại", null);
                }
        }

        private String formatCurrency(double amount) {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                return formatter.format(amount);
        }

        public ResponseData sendScheduleEmail(List<String> userIds, Date startDate, Date endDate) {
                for (String userId : userIds) {
                        // Tìm employee bằng userId
                        Employee employee = employeeService.findById(userId);
                        if (employee != null) {
                                String email = employee.getEmail();
                                String username = employee.getFirstName() + " " + employee.getLastName();
                                try {
                                        MimeMessage mimeMessage = mailSender.createMimeMessage();
                                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                                        helper.setFrom("your-email@example.com");
                                        helper.setTo(email);
                                        helper.setSubject("Lịch Làm Việc Của Bạn");

                                        // Lấy lịch làm việc
                                        List<AssignCountersForStaff> schedules = assignShiftForStaffService
                                                        .getShiftsByUserId(startDate, endDate, userId);

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        StringBuilder message = new StringBuilder();
                                        message.append("<html><body>");

                                        // Thêm nội dung email
                                        message.append("<div style='display: flex; align-items: center;'>");
                                        message.append("<img style='width: 170px; height: auto;  margin-right: 8px;' src='https://storage.googleapis.com/jewelrystore-2ks1dotnet.appspot.com/User/c3b3e699-1466-48cf-b1ae-1db13264e44e_2024-07-12' alt='Logo' />");
                                        message.append("</div>");
                                        message.append("<div style='background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); padding: 20px; max-width: 1000px; margin: auto;'>");
                                        message.append("<br style='display: flex; justify-content: space-between; align-items: center;'>");
                                        message.append("</br>");
                                        message.append("<div style='color: #4a4a4a; text-align: center;'>");
                                        message.append("<div style='font-weight: bold; font-size: 24px; margin-bottom: 8px;'>LỊCH LÀM VIỆC</div>");
                                        message.append("<div style='font-size: 14px;'>Ngày gửi: ")
                                                        .append(LocalDate.now().toString()).append("</div>");
                                        message.append("</div>");
                                        message.append("</div>");

                                        message.append("<div style='font-size: 14px;'>Lịch làm việc của <span style='font-weight: bold;'>")
                                                        .append(username)
                                                        .append("</span> từ <span style='font-weight: bold;'>")
                                                        .append(dateFormat.format(startDate))
                                                        .append("</span> đến <span style='font-weight: bold;'>")
                                                        .append(dateFormat.format(endDate)).append("</span>:</div>");

                                        Calendar calendar = Calendar.getInstance();
                                        calendar.setTime(startDate);

                                        // Khởi tạo ngày bắt đầu
                                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                                        for (int i = 0; i < 7; i++) {
                                                String dayOfWeek = new SimpleDateFormat("EEEE")
                                                                .format(calendar.getTime());
                                                String formattedDate = dateFormat.format(calendar.getTime());

                                                message.append("<li class='events-group mb-4'>");
                                                message.append("<div class='top-info py-2 px-4 bg-gray-300'>");
                                                message.append("<span>").append(dayOfWeek).append(" ")
                                                                .append(formattedDate).append("</span>");
                                                message.append("<span class='date'></span>");
                                                message.append("</div>");
                                                message.append("<ul class='flex overflow-x-scroll mr-3'>");

                                                for (AssignCountersForStaff schedule : schedules) {
                                                        Calendar scheduleCalendar = Calendar.getInstance();
                                                        scheduleCalendar.setTime(schedule.getAssignShiftForStaff()
                                                                        .getDTO().getDate());
                                                        int scheduleDayOfWeek = scheduleCalendar
                                                                        .get(Calendar.DAY_OF_WEEK);

                                                        if (scheduleDayOfWeek == calendar.get(Calendar.DAY_OF_WEEK)) {
                                                                message.append("<li class='single-event' data-start='")
                                                                                .append(schedule.getAssignShiftForStaff()
                                                                                                .getCheckIn() != null
                                                                                                                ? schedule.getAssignShiftForStaff()
                                                                                                                                .getCheckIn()
                                                                                                                                .toString()
                                                                                                                : "")
                                                                                .append("' data-end='")
                                                                                .append(schedule.getAssignShiftForStaff()
                                                                                                .getCheckOut() != null
                                                                                                                ? schedule.getAssignShiftForStaff()
                                                                                                                                .getCheckOut()
                                                                                                                                .toString()
                                                                                                                : "")
                                                                                .append("' data-content='event-abs-circuit' data-event='event-1'>");
                                                                message.append("<a href='#0'>");
                                                                message.append("<em class='event-name'>")
                                                                                .append(schedule.getAssignShiftForStaff()
                                                                                                .getEmployee()
                                                                                                .getFirstName())
                                                                                .append(" ")
                                                                                .append(schedule.getAssignShiftForStaff()
                                                                                                .getEmployee()
                                                                                                .getLastName())
                                                                                .append(" - ")
                                                                                .append(schedule.getCounter().getName())
                                                                                .append("</em>");
                                                                message.append("</a>");
                                                                message.append("</li>");
                                                        }
                                                }

                                                message.append("</ul>");
                                                message.append("</li>");

                                                // Tăng ngày lên 1
                                                calendar.add(Calendar.DATE, 1);
                                        }

                                        message.append("</ul>");
                                        message.append("</div>");
                                        message.append("</div>");

                                        message.append("</div>");
                                        message.append("<div style='font-size: 12px; font-style: italic; margin-top: 20px;'>");
                                        message.append("Công ty gửi bạn lịch làm việc. Cảm ơn bạn đã chú ý đến lịch làm việc. Bạn không thể đăng nhập vào hệ thống ngoài giờ làm việc của mình. Cảm ơn.");
                                        message.append("</div>");

                                        message.append("<div style='text-align: center; background-color: #f2f2f2; padding: 20px;'>");
                                        message.append("<p style='color: #666;'>Công ty 2ks1dotnet</p>");
                                        message.append("<p style='color: #666;'>Địa chỉ: Số 123, Đường ABC, Thành phố XYZ</p>");
                                        message.append("<p style='color: #666;'>Email: support@2ks1dotnet.com | Điện thoại: 0123 456 789</p>");
                                        message.append("</div>");
                                        message.append("</body></html>");

                                        helper.setText(message.toString(), true);

                                        mailSender.send(mimeMessage);
                                } catch (MessagingException e) {
                                        e.printStackTrace();
                                        return new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Send failed work schedule to " + username, null);
                                }
                        }
                }
                return new ResponseData(HttpStatus.OK, "The work schedule has been sent successfully.", null);
        }

}
