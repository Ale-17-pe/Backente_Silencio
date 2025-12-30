package com.example.ElSilencio.modules.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void enviarCodigo2FA(String destinatario, String codigo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(destinatario);
        message.setSubject("Codigo de Verificacion - Hotel El Silencio");
        message.setText("Su codigo de verificacion es: " + codigo +
                "\n\nEste codigo expira en 10 minutos." +
                "\n\nSi no solicito este codigo, ignore este mensaje.");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error enviando email: " + e.getMessage());
        }
    }

    public void enviarConfirmacionReserva(String destinatario, String codigoReserva, String detalles) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(destinatario);
        message.setSubject("Confirmacion de Reserva - Hotel El Silencio");
        message.setText("Su reserva ha sido confirmada." +
                "\n\nCodigo de Reserva: " + codigoReserva +
                "\n\n" + detalles +
                "\n\nGracias por elegirnos!");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error enviando email: " + e.getMessage());
        }
    }
}
