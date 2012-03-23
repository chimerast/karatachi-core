package org.karatachi.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mailer {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static String DEFAULT_CHARSET = "ISO-2022-JP";

    private final String hostname;
    private final int port;
    private final String username;
    private final String password;

    private final String from;
    private final String name;

    private Map<String, String> template = new HashMap<String, String>();

    public Mailer(String hostname, String from, String name) {
        this(hostname, 25, null, null, from, name);
    }

    public Mailer(String hostname, int port, String username, String password,
            String from, String name) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.from = from;
        this.name = name;
    }

    public void sendmail(String to, String subject, String text)
            throws MessagingException {
        sendmail(to, null, null, subject, text, null);
    }

    public void sendmail(String to, String cc, String bcc, String subject,
            String text) throws MessagingException {
        sendmail(to, cc, bcc, subject, text, null);
    }

    public void sendmail(String to, String key, Map<String, String> variables)
            throws MessagingException {
        sendmail(to, null, null, key, variables, null);
    }

    public void sendmail(String to, String cc, String bcc, String key,
            Map<String, String> variables) throws MessagingException {
        sendmail(to, cc, bcc, key, variables, null);
    }

    public void sendmail(String to, String key, Map<String, String> variables,
            DataSource data) throws MessagingException {
        sendmail(to, null, null, key, variables, data);
    }

    public void sendmail(String to, String cc, String bcc, String key,
            Map<String, String> variables, DataSource data)
            throws MessagingException {
        String subject = "";
        String text = template.get(key);

        if (text.startsWith("Subject:")) {
            int end = text.indexOf("\n");
            if (end < 0) {
                throw new MessagingException("Illeagal format: " + text);
            }
            subject = text.substring("Subject:".length(), end);

            int begin = text.indexOf("\n\n");
            if (begin < 0) {
                throw new MessagingException("Illeagal format: " + text);
            }
            text = text.substring(begin + 2);
        }
        subject = replaceVariables(subject, variables);
        text = replaceVariables(text, variables);
        sendmail(to, cc, bcc, subject, text, data);
    }

    public void sendmail(String to, String cc, String bcc, String subject,
            String text, DataSource data) throws MessagingException {
        Session session = newSession();

        MimeMessage msg = new MimeMessage(session);

        msg.setSentDate(new Date());

        try {
            msg.setFrom(new InternetAddress(from, name, DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException ignore) {
            throw new IllegalStateException();
        }

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        if (StringUtils.isNotEmpty(cc)) {
            msg.setRecipients(Message.RecipientType.CC, cc);
        }
        if (StringUtils.isNotEmpty(bcc)) {
            msg.setRecipients(Message.RecipientType.BCC, bcc);
        }

        msg.setSubject(subject, DEFAULT_CHARSET);
        if (data == null) {
            msg.setText(text, DEFAULT_CHARSET);
        } else {
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(text, DEFAULT_CHARSET);

            MimeBodyPart dataPart = new MimeBodyPart();
            dataPart.setDataHandler(new DataHandler(data));
            try {
                dataPart.setFileName(MimeUtility.encodeWord(data.getName(),
                        DEFAULT_CHARSET, "B"));
            } catch (UnsupportedEncodingException ignore) {
                throw new IllegalStateException();
            }

            Multipart multiPart = new MimeMultipart();
            multiPart.addBodyPart(textPart);
            multiPart.addBodyPart(dataPart);
            msg.setContent(multiPart);
        }

        Transport.send(msg);
    }

    private Session newSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", hostname);
        props.put("mail.smtp.port", Integer.toString(port));
        props.put("mail.debug", "false");

        if (port == 465) {
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.socketFactory.port", "465");
        }

        if (username != null) {
            props.put("mail.smtp.auth", "true");
            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            return Session.getInstance(props);
        }
    }

    public void loadTemplate(InputStream stream) {
        try {
            LineIterator itr = IOUtils.lineIterator(stream, "UTF-8");

            String key = null;
            StringBuilder value = new StringBuilder();
            while (itr.hasNext()) {
                String line = itr.nextLine();
                if (line.startsWith(">>")) {
                    key = line.substring(2).trim();
                    value.setLength(0);
                } else if (line.startsWith("<<")) {
                    if (key != null) {
                        template.put(key, value.toString());
                        logger.info("template loaded: NAME=" + key);
                    }
                    key = null;
                    value.setLength(0);
                } else {
                    value.append(line);
                    value.append("\n");
                }
            }
        } catch (IOException e) {
            logger.error("fail to load template.", e);
        }
    }

    private String replaceVariables(String text, Map<String, String> variables) {
        for (String var : variables.keySet()) {
            String value = variables.get(var);
            if (value == null) {
                value = "";
            }
            text = text.replaceAll("\\$" + var, value);
        }
        return text;
    }
}
