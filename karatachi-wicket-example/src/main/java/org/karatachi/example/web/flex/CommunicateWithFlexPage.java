package org.karatachi.example.web.flex;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.auto.SelfResolveForm;
import org.karatachi.wicket.flex.FlexComponent;

public class CommunicateWithFlexPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    private static final ResourceReference SWF_FILE = new ResourceReference(
            CommunicateWithFlexPage.class, "CommunicateWithFlex.swf");

    // swfobject.jsをラップしたコンポーネント
    private FlexComponent swf;

    // メッセージログを保存する
    private String log = "";

    public CommunicateWithFlexPage() {
        add(swf = new FlexComponent("swf", SWF_FILE));

        add(new ToFlexAlertForm("form"));
        add(new FromFlexMessageBehavior());
        add(new MultiLineLabel("log", new PropertyModel<String>(this, "log")).setOutputMarkupId(true));
    }

    /**
     * Flexへメッセージを送信するフォーム
     */
    private class ToFlexAlertForm extends SelfResolveForm {
        private static final long serialVersionUID = 1L;

        private String message;

        public ToFlexAlertForm(String id) {
            super(id);

            add(new AjaxButton("alert") {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    // FlexのcallAlert()をJavascript経由で呼び出す
                    // Wicket.$()はprototype.jsやjQueryの$()とほぼ同等
                    target.appendJavascript(String.format(
                            "Wicket.$('%s').callAlert('%s')",
                            swf.getSwfMarkupId(), message));
                }
            });
        }
    }

    /**
     * Flexからのメッセージを受け取るためのBehavior
     */
    private class FromFlexMessageBehavior extends AbstractDefaultAjaxBehavior {
        private static final long serialVersionUID = 1L;

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);

            // addMessage()関数をブラウザに登録
            String pattern =
                    "function addMessage(value) {"
                            + "wicketAjaxGet('%s&params='+encodeURIComponent(value));"
                            + "}";
            response.renderJavascript(String.format(pattern, getCallbackUrl()),
                    "swf-addmessage");
        }

        @Override
        protected void respond(AjaxRequestTarget target) {
            // FlexからaddMessage()が呼ばれるとここが呼ばれる
            // パラメータでメッセージを受け取りメッセージログを更新
            log = log + getRequest().getParameter("params") + "\n";
            target.addComponent(get("log"));
        }
    }
}
