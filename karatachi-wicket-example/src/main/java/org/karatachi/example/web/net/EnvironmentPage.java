package org.karatachi.example.web.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.AbstractStringResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.auto.AutoResolveListView;
import org.seasar.framework.beans.util.BeanUtil;

public class EnvironmentPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public EnvironmentPage() {
        // HTTPヘッダ
        final HttpServletRequest request =
                (HttpServletRequest) getRequest().getContainerRequest();

        add(new AutoResolveListView<String>(
                "httpHeader",
                Collections.list((Enumeration<String>) request.getHeaderNames())) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<String> item) {
                String key = item.getModelObject();
                item.add(new Label("key", key));
                item.add(new Label("value", request.getHeader(key)));
            }
        });

        // Javascriptで取得したクライアント情報
        final Map<String, Object> clientInfo =
                BeanUtil.createProperties(((WebClientInfo) Session.get().getClientInfo()).getProperties());
        final List<String> clientInfoKeys =
                new ArrayList<String>(clientInfo.keySet());
        Collections.sort(clientInfoKeys);

        add(new AutoResolveListView<String>("clientInfo", clientInfoKeys) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<String> item) {
                String key = item.getModelObject();

                item.add(new Label("key", key));
                item.add(new Label("value", clientInfo.get(key).toString()));
            }
        });

        // 表示内容のダウンロード
        add(new Link<Void>("download") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                IResourceStream rs =
                        new AbstractStringResourceStream("text/csv") {
                            private static final long serialVersionUID = 1L;

                            @Override
                            protected String getString() {
                                StringBuilder ret = new StringBuilder();
                                for (String key : Collections.list((Enumeration<String>) request.getHeaderNames())) {
                                    ret.append(String.format("%s\t%s\n", key,
                                            request.getHeader(key)));
                                }
                                for (String key : clientInfoKeys) {
                                    ret.append(String.format("%s\t%s\n", key,
                                            clientInfo.get(key).toString()));
                                }
                                return ret.toString();
                            }
                        };
                getRequestCycle().scheduleRequestHandlerAfterCurrent(
                        new ResourceStreamRequestHandler(rs, "environment.tsv"));
            }
        });
    }
}
