package org.karatachi.example.web.net;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.util.resource.PackageResourceStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.karatachi.mail.Mailer;
import org.karatachi.wicket.dialog.ProgressDialog;
import org.karatachi.wicket.dialog.ProgressDialogParams;
import org.seasar.framework.beans.util.BeanUtil;

public class DownloadTestPage extends WebPage {
    private static final long serialVersionUID = 1L;

    private ProgressDialog progress;

    public DownloadTestPage() {
        Session.get().getClientInfo();

        add(progress = new ProgressDialog("progress"));
        add(new FeedbackPanel("feedback"));

        add(new Link<Void>("sendmail") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                Mailer mailer =
                        new Mailer("localhost", "tomcat@world.chimera.st",
                                "Karatachi Wicket");
                try {
                    mailer.sendmail("chimera@world.chimera.st",
                            "Wicket Environment Check", getClientInfo());
                    info("環境が送信されました。");
                } catch (MessagingException e) {
                    error("環境の送信に失敗しました。" + e.getMessage());
                }
            }
        });

        add(new Link<Void>("download1") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                PackageResourceStream rs =
                        new PackageResourceStream(DownloadTestPage.class,
                                "test.pdf");
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(rs, "test.pdf"));
            }
        });

        add(new AjaxLink<Void>("download2") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new DownloadDataDirectPage());
            }
        });

        add(new AjaxLink<Void>("download3") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new DownloadDataIndirectPage());
            }
        });

        add(new AjaxLink<Void>("download4") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                progress.show(target, new ProgressDialogParams() {
                    @Override
                    public void execute() throws InterruptedException {
                        for (int i = 0; i < 10; ++i) {
                            setProgress(i / 10.0);
                            Thread.sleep(200);
                        }
                    }

                    @Override
                    public void onSuccess(AjaxRequestTarget target) {
                        setResponsePage(new DownloadDataDirectPage());
                    }
                });
            }
        });

        add(new AjaxLink<Void>("download5") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                progress.show(target, new ProgressDialogParams() {
                    @Override
                    public void execute() throws InterruptedException {
                        for (int i = 0; i < 10; ++i) {
                            setProgress(i / 10.0);
                            Thread.sleep(200);
                        }
                    }

                    @Override
                    public void onSuccess(AjaxRequestTarget target) {
                        setResponsePage(new DownloadDataIndirectPage());
                    }
                });
            }
        });
    }

    @SuppressWarnings("unchecked")
    private String getClientInfo() {
        HttpServletRequest request =
                (HttpServletRequest) getRequest().getContainerRequest();
        Map<String, Object> clientInfo =
                new TreeMap<String, Object>(
                        BeanUtil.createProperties(((WebClientInfo) Session.get().getClientInfo()).getProperties()));
        StringBuilder ret = new StringBuilder();
        ret.append("* Header Info\n");
        for (String key : Collections.list((Enumeration<String>) request.getHeaderNames())) {
            ret.append(String.format("%s: %s\n", key, request.getHeader(key)));
        }
        ret.append("* Client Info\n");
        for (String key : clientInfo.keySet()) {
            ret.append(String.format("%s: %s\n", key,
                    clientInfo.get(key).toString()));
        }
        return ret.toString();
    }
}
