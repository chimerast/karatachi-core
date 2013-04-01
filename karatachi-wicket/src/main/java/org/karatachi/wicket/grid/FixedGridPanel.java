package org.karatachi.wicket.grid;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestHandler;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.karatachi.wicket.flex.FlexComponent;
import org.karatachi.wicket.grid.FixedGrid.Position;
import org.karatachi.wicket.script.AjaxLibrariesReference;

public class FixedGridPanel extends Panel implements IHeaderContributor {
    private static final long serialVersionUID = 1L;

    private static final ResourceReference CSS = new CssResourceReference(
            FixedGridPanel.class, "karatachi-fixedgrid.css");
    private static final ResourceReference SCRIPT =
            new JavaScriptResourceReference(FixedGridPanel.class,
                    "karatachi-selectablegrid.js");

    private static final ResourceReference OLD_CSS = new CssResourceReference(
            FixedGridPanel.class, "old-karatachi-grid.css");
    private static final ResourceReference OLD_SCRIPT =
            new JavaScriptResourceReference(FixedGridPanel.class,
                    "old-karatachi-grid.js");

    private final WebMarkupContainer grid_tl;
    private final WebMarkupContainer grid_tr;
    private final WebMarkupContainer grid_bl;
    private final WebMarkupContainer grid_br;

    private int fixedRow = 0;
    private int fixedCol = 0;

    private int alignCol = 0;

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

        add(grid_tl = new GridContainer("grid_tl", Position.TOP_LEFT));
        add(grid_tr = new GridContainer("grid_tr", Position.TOP_RIGHT));
        add(grid_bl = new GridContainer("grid_bl", Position.BOTTOM_LEFT));
        add(grid_br = new GridContainer("grid_br", Position.BOTTOM_RIGHT));
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

    public FixedGridPanel align(int col) {
        this.alignCol = col;
        return this;
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

        add(new Behavior() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onConfigure(Component component) {
                String userAgent =
                        ((WebClientInfo) Session.get().getClientInfo()).getUserAgent();
                if (userAgent.contains("Trident/4.0")) {
                    AjaxRequestHandler handler =
                            getRequestCycle().find(AjaxRequestHandler.class);
                    if (handler == null) {
                        Response response = RequestCycle.get().getResponse();
                        WebResponse.class.cast(response).setHeader(
                                "X-UA-Compatible", "IE=7");
                    }
                }
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        WebClientInfo info = (WebClientInfo) getSession().getClientInfo();
        if (info.getUserAgent().indexOf("MSIE 6.0") != -1) {
            response.render(CssReferenceHeaderItem.forReference(OLD_CSS));
            response.render(JavaScriptHeaderItem.forReference(AjaxLibrariesReference.prototype));
            response.render(JavaScriptHeaderItem.forReference(OLD_SCRIPT));
            response.render(JavaScriptHeaderItem.forScript(
                    String.format("var %1$s; var %1$s_pos;", getMarkupId()),
                    null));
            response.render(OnDomReadyHeaderItem.forScript(String.format(
                    "%s = new KaratachiGrid('%s', '%s', '%s', '%s', '%s', %d)",
                    getMarkupId(), getMarkupId(), grid_br.getMarkupId(),
                    grid_tl.getMarkupId(), grid_bl.getMarkupId(),
                    grid_tr.getMarkupId(), alignCol)));
        } else {
            response.render(CssReferenceHeaderItem.forReference(CSS));
            response.render(JavaScriptHeaderItem.forReference(AjaxLibrariesReference.jquery));
            response.render(JavaScriptHeaderItem.forReference(AjaxLibrariesReference.jquery_textselection));
            response.render(JavaScriptHeaderItem.forReference(AjaxLibrariesReference.jquery_zclip));
            response.render(JavaScriptHeaderItem.forReference(FlexComponent.SWFOBJECT_JS));
            response.render(JavaScriptHeaderItem.forReference(SCRIPT));
            response.render(OnDomReadyHeaderItem.forScript(String.format(
                    "jQuery('#%s').fixedgrid({ zclip_swf : '%s', align : %d });",
                    getMarkupId(),
                    urlFor(AjaxLibrariesReference.jquery_zclip_swf, null),
                    alignCol)));
        }
    }
}
