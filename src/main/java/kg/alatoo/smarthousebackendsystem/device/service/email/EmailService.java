package kg.alatoo.smarthousebackendsystem.device.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEnergyAnomalyAlert(
            String to,
            BigDecimal todayKwh,
            BigDecimal averageKwh,
            BigDecimal differencePercent
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Smart Home Energy Alert");
        message.setText("""
                Hello!

                Unusual energy consumption was detected in your smart home.

                Today's consumption: %s kWh
                Average daily consumption: %s kWh
                Difference: +%s%%

                Please check your connected devices.
                """.formatted(todayKwh, averageKwh, differencePercent));

        mailSender.send(message);
    }
}