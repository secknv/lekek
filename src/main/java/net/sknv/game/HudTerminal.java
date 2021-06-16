package net.sknv.game;

import net.sknv.engine.entities.TextItem;

import java.util.LinkedList;

public class HudTerminal {

    private TextItem terminalText;
    private String stored = "";
    private String suggestion = "";
    private LinkedList<String> history = new LinkedList();
    private int historyIndex = -1;

    public HudTerminal(TextItem terminalText) {
        this.terminalText = terminalText;
    }

    public TextItem getTextItem() {
        return terminalText;
    }

    public void setText(String text){
        terminalText.setText(text);
    }

    public String getText() {
        return terminalText.getText();
    }

    public void addText(String toAdd) {
        setText(getText().concat(toAdd));
        stored = getText();
    }

    public void backspace() {
        if(getText().length()>1){
            setText(getText().substring(0,getText().length()-1));
            stored = getText();
        }
    }

    public String enter() {
        history.push(getText());
        historyIndex = -1;

        String enter = getText().substring(1);
        stored = "";
        setText("/");
        return enter;
    }

    public void close() {
        stored = getText();
        setText("/");
    }

    public void open() {
        if(!stored.isEmpty()) setText(stored);
        else setText("/");

        suggestCompletion();
    }

    public void previous() {
        if(history.size()>0){
            if(++historyIndex>history.size()-1) historyIndex=history.size()-1;
            setText(history.get(historyIndex));
        }
    }

    public void recent() {
        if(history.size()>0){
            if(--historyIndex<0) historyIndex=0;
            setText(history.get(historyIndex));
        }
    }

    public void suggestCompletion(){
        if(!getText().isEmpty() && !getText().equals("/") && !getText().endsWith(" ")){

            String[] parsed = getText().substring(1).split(" ");
            if(parsed.length==0) return;

            String toMatch = parsed[parsed.length-1];
            System.out.println("expression to match ->" + toMatch);

            for (Command c : Command.values()){
                if(c.getCommandName().startsWith(toMatch)){
                    suggestion = c.getCommandName().replaceFirst(toMatch, "");
                }
            }
        }
    }
}
