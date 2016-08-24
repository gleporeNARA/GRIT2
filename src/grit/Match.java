package grit;

import java.io.File;

public class Match {
	
    private int id;
    private String confidence;
    private String text;
    private String line;
    private String type;
    private File file;
    private int line_num;
    
    public Match(){}

    public Match(int id, String confidence, String text, String line, String type, File file, int line_num) {
        super();
        this.id = id;
        this.confidence = confidence;
        this.text = text;
        this.line = line;
        this.type = type;
        this.file = file;
        this.line_num = line_num;
    }
    
    public Match(String text, int line_num){
        this.text = text;
        this.line_num = line_num;
    }

    public int getID() {
        return id;
    }
    public String getConfidence() {
        return confidence;
    }
    public String getText() {
        return text;
    }
    public String getLine() {
        return line;
    }
    public String getType() {
        return type;
    }
    public File getFile() {
        return file;
    }
    public int getLineNum() {
        return line_num;
    }
    
    public int setID(int i) {
        return id = i;
    }
    @Override
    public int hashCode(){
        int hashcode = 0;
        //hashcode = 30;
        //hashcode = line_num + 30;
        hashcode += text.hashCode();
        return hashcode;
    }
     
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Match) {
            Match pp = (Match) obj;
            //return (pp.text.equals(this.text));
            return (pp.text.equals(this.text) && pp.file.equals(this.file));
        } else {
            return false;
        }
    }
}