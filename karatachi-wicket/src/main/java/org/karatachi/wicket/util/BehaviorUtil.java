package org.karatachi.wicket.util;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

public class BehaviorUtil {
    public static <T extends Component> T setSelfUpdate(T component, int sec) {
        component.setOutputMarkupId(true);
        component.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(sec)));
        return component;
    }

    public static <T extends Component> T addStyle(T component, String style) {
        component.add(new AttributeAppender("style", new Model<String>(style),
                ";"));
        return component;
    }

    public static <T extends Component> T addStyle(T component,
            IModel<String> model) {
        component.add(new AttributeAppender("style", model, ";"));
        return component;
    }
}
