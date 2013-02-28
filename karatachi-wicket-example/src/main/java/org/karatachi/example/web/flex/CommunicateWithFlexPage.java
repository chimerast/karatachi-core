package org.karatachi.example.web.flex;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.auto.SelfResolveForm;
import org.karatachi.wicket.flex.FlexComponent;

public class CommunicateWithFlexPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    private static final ResourceReference SWF_FILE =
            new PackageResourceReference(CommunicateWithFlexPage.class,
                    "CommunicateWithFlex.swf");

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
                    target.appendJavaScript(String.format(
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
        public void renderHead(Component component, IHeaderResponse response) {
            super.renderHead(component, response);

            // addMessage()関数をブラウザに登録
            StringBuilder pattern = new StringBuilder();
            pattern.append("function addMessage(value) {");
            pattern.append("  var attributes = %s;");
            pattern.append("  attributes.ep = {'params': value};");
            pattern.append("  Wicket.Ajax.get(attributes);");
            pattern.append("}");

            response.render(JavaScriptHeaderItem.forScript(String.format(
                    pattern.toString(), renderAjaxAttributes(component)),
                    "swf-addmessage"));
        }

        @Override
        protected void respond(AjaxRequestTarget target) {
            // FlexからaddMessage()が呼ばれるとここが呼ばれる
            // パラメータでメッセージを受け取りメッセージログを更新
            String params =
                    getRequest().getRequestParameters().getParameterValue(
                            "params").toString();
            log = log + params + "\n";
            target.add(get("log"));
        }
    }
}
