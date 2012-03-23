package org.karatachi.example.web.grid;

import org.apache.wicket.model.Model;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.panel.CheckTreeNode;
import org.karatachi.wicket.panel.CheckTreePanel;

public class CheckTreePage extends WebBasePage {
    public CheckTreePage() {
        CheckTreeNode<String> root = new CheckTreeNode<String>("ROOT");
        for (int i = 0; i < 10; ++i) {
            CheckTreeNode<String> child =
                    new CheckTreeNode<String>("node." + i);
            root.addChild(child);
            for (int j = 0; j < 10; ++j) {
                CheckTreeNode<String> temp =
                        new CheckTreeNode<String>("node." + i + "." + j);
                temp.setLeaf(true);
                child.addChild(temp);
            }
        }
        add(new CheckTreePanel<String>("tree",
                new Model<CheckTreeNode<String>>(root)));
    }
}
