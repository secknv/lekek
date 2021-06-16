package net.sknv.game;

import net.sknv.engine.entities.TextItem;

import java.util.LinkedList;

public class HudTerminal {

    private TextItem terminalText;
    private String stored = "";
    private String suggestion = "";
    private static final int HISTORY_SIZE = 100;
    private LinkedList<String> history = new LinkedList();

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
        if(history.size()>HISTORY_SIZE) history.removeLast();

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
        if(history.peek()!=null){
            while (history.peek().equals(getText())) history.add(history.removeFirst());
            setText(history.peek());
            history.add(history.removeFirst());
        }
    }

    public void recent() {
        if(history.peek()!=null){
            while (history.peekLast().equals(getText())) history.push(history.removeLast());
            setText(history.peekLast());
            history.push(history.removeLast());
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
