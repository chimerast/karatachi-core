package org.karatachi.wicket.grid;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.karatachi.wicket.flex.FlexComponent;
import org.karatachi.wicket.grid.FixedGrid.Position;
import org.karatachi.wicket.script.AjaxLibrariesReference;

public class FixedGridPanel extends Panel implements IHeaderContributor {
    private static final long serialVersionUID = 1L;

    private static final ResourceReference CSS = new ResourceReference(
            FixedGridPanel.class, "karatachi-fixedgrid.css");
    private static final JavascriptResourceReference SCRIPT =
            new JavascriptResourceReference(FixedGridPanel.class,
                    "karatachi-selectablegrid.js");

    private int fixedRow = 0;
    private int fixedCol = 0;

    private Map<Integer, Integer> widths = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> heights = new HashMap<Integer, Integer>();

    private int defalutWidth = 100;
    private int defalutHeight = 20;

    public FixedGridPanel(String id, Cells cells) {
        this(id, new Model<Cells>(cells));
    }

    public FixedGridPanel(String id, IModel<Cells> cells) {
        super(id, cells);

        setOutputMarkupId(true);

        add(new GridContainer("grid_tl", Position.TOP_LEFT));
        add(new GridContainer("grid_tr", Position.TOP_RIGHT));
        add(new GridContainer("grid_bl", Position.BOTTOM_LEFT));
        add(new GridContainer("grid_br", Position.BOTTOM_RIGHT));
        add(new MenuContainer("menu"));
    }

    @SuppressWarnings("unchecked")
    public IModel<Cells> getModel() {
        return (IModel<Cells>) getDefaultModel();
    }

    public FixedGridPanel fix(int row, int col) {
        this.fixedRow = row;
        this.fixedCol = col;
        return this;
    }

    public int getFixedRow() {
        return fixedRow;
    }

    public int getFixedCol() {
        return fixedCol;
    }

    public void setDefalutWidth(int defalutWidth) {
        this.defalutWidth = defalutWidth;
    }

    public void setDefalutHeight(int defalutHeight) {
        this.defalutHeight = defalutHeight;
    }

    public int getWidth(int col) {
        return widths.containsKey(col) ? widths.get(col) : defalutWidth;
    }

    public void setWidth(int col, int width) {
        widths.put(col, width);
    }

    public int getHeight(int row) {
        return heights.containsKey(row) ? heights.get(row) : defalutHeight;
    }

    public void setHeight(int row, int height) {
        heights.put(row, height);
    }

    private class GridContainer extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public GridContainer(String id, Position position) {
            super(id);
            setOutputMarkupId(true);
            setMarkupId(FixedGridPanel.this.getMarkupId() + "_"
                    + position.getCode());
            add(new FixedGrid(FixedGridPanel.this.getModel(),
                    FixedGridPanel.this, position));
        }
    }

    private class MenuContainer extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        public MenuContainer(String id) {
            super(id);
            setOutputMarkupId(true);
            setMarkupId(FixedGridPanel.this.getMarkupId() + "_menu");
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new AbstractBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onConfigure(Component component) {
                String userAgent =
                        ((WebClientInfo) RequestCycle.get().getClientInfo()).getUserAgent();
                if (userAgent.contains("Trident/4.0")) {
                    ((WebRequestCycle) RequestCycle.get()).getWebResponse().setHeader(
                            "X-UA-Compatible", "IE=7");
                }
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(CSS);
        response.renderJavascriptReference(AjaxLibrariesReference.jquery);
        response.renderJavascriptReference(AjaxLibrariesReference.jquery_textselection);
        response.renderJavascriptReference(AjaxLibrariesReference.jquery_zclip);
        response.renderJavascriptReference(FlexComponent.SWFOBJECT_JS);
        response.renderJavascriptReference(SCRIPT);
        response.renderOnDomReadyJavascript(String.format(
                "jQuery('#%s').fixedgrid({ zclip_swf : '%s' });",
                getMarkupId(), urlFor(AjaxLibrariesReference.jquery_zclip_swf)));
    }
}
