package com.example.demo.Services;

import com.example.demo.Entities.Lector;
import com.example.demo.Entities.Secretary;
import com.example.demo.Entities.Student;
import com.example.demo.Repositories.LectorRepository;
import com.example.demo.Repositories.SecretaryRepositary;
import com.example.demo.Repositories.StudentRepository;
import javax.mail.*;
import javax.mail.internet.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Properties;

@Service
public class AuthRegService {
    private SecretaryRepositary secretaryRepositary;
    private StudentRepository studentRepository;
    private LectorRepository lectorRepository;

    @Autowired
    public AuthRegService(SecretaryRepositary secretaryRepositary, StudentRepository studentRepository, LectorRepository lectorRepository) {
        this.secretaryRepositary = secretaryRepositary;
        this.lectorRepository = lectorRepository;
        this.studentRepository = studentRepository;
    }

    public Object authorize(String login, String password) {
        try {
            Optional<Secretary> optSec = secretaryRepositary.findById(login);
            if (optSec.isPresent()) {
                if (BCrypt.checkpw(password,optSec.get().getPassword()) && optSec.get().getStatus() == 1)
                    return optSec.get();
            }
            Optional<Lector> optLec = lectorRepository.findByLogin(login);
            if (optLec.isPresent()) {
                if (BCrypt.checkpw(password,optLec.get().getPassword()))
                    return optLec.get();
            }
            Optional<Student> optStud = studentRepository.findByLogin(login);
            if (optStud.isPresent()) {
                System.out.println("is student");
                if (BCrypt.checkpw(password,optStud.get().getPassword()))
                    return optStud.get();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Неправильный логин или пароль";
        }
        return "Неправильный логин или пароль";
    }

    public int register(String login, String password, int role, String email) {
        int pass_check = checkPassword(password);
        if (pass_check!=0) return 10;
        switch (role) {
            case 0:
                if (!studentRepository.existsByEmail(email)) return 8;
                else {
                    if (studentRepository.findByEmail(email).get().getPassword().equals("")) {
                        if (studentRepository.existsBylogin(login) || lectorRepository.existsByLogin(login) || secretaryRepositary.existsById(login))
                            return 5;
                        else {
                            Student stud = studentRepository.findByEmail(email).get();
                            stud.setLogin(login);
                            String salt = BCrypt.gensalt();
                            String hashedPassword = BCrypt.hashpw(password, salt);
                            stud.setPassword(hashedPassword);
                            studentRepository.save(stud);
                            return 0;
                        }
                    } else {
                        System.out.println(studentRepository.findByEmail(email).get().getPassword());
                        return 9;
                    }
                }
            case 1:
                if (!lectorRepository.existsByEmail(email)) return 8;
                else {
                    if (lectorRepository.findByEmail(email).get().getPassword() == null) {
                        if (studentRepository.existsBylogin(login) || lectorRepository.existsByLogin(login) || secretaryRepositary.existsById(login))
                            return 5;
                        else {
                            Lector lec = lectorRepository.findByEmail(email).get();
                            lec.setLogin(login);
                            String salt = BCrypt.gensalt();
                            String hashedPassword = BCrypt.hashpw(password, salt);
                            lec.setPassword(hashedPassword);
                            lectorRepository.save(lec);
                            return 0;
                        }
                    }
                    return 9;
                }
            case 2:
                if (secretaryRepositary.existsById(login)) return 5;
                else {String salt = BCrypt.gensalt();
                    String hashedPassword = BCrypt.hashpw(password, salt);
                    Secretary sec = new Secretary(login, hashedPassword, 0);
                    if (secretaryRepositary.count() == 0) sec.setStatus(1);
                    secretaryRepositary.save(sec);
                    return 0;
                }
            default:
                return 7;
        }
    }
    public int checkPassword(String password){
        if (password.length() < 8 || password.length() > 50) return 10;
        boolean num = false;
        for (int k = 0; k < password.length(); k++) {
            if (Character.isDigit(password.charAt(k))) {
                num = true;break;
            };
        }
        if (!num) return 10;
        return 0;
    }
    public void sendMail(String reciever,int code){
        final String username = "polina.va.00@mail.ru"; // Замените на свой адрес
        final String password = "baEaV80ampqv8SnN8Rru"; // Замените на свой пароль

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.mail.ru");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // Создаем сессию
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(reciever));
            message.setFrom(new InternetAddress(username));
            message.setSubject("Код подтверждения UNIVER");
            message.setText("Ваш код подтверждения: "+code);
            Transport.send(message);
            System.out.println("Сообщение отправлено успешно!");
        } catch (MessagingException|ClassCastException e) {
            e.printStackTrace();
        }
    }
}
