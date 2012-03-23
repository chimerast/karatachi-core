package org.karatachi.example.web;

import org.karatachi.wicket.script.SyntaxHilighterLabel;

public class IndexPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public IndexPage() {
        // syntaxhilighterを読み込みたいがためだけに追加
        add(new SyntaxHilighterLabel("url", "Xml", "Eclipse",
                "http://repo.karatachi.org/mvn/org/karatachi/"));
    }
}
