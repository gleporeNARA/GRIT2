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
		int hashcode = Math.abs ((confidence + line_num + line + file.toString()).hashCode());
		return hashcode;
	}
     
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Match) {
			Match that = (Match) obj;
			return this.confidence.equals (that.confidence) && 
				   this.line_num == that.line_num && 
				   this.line.equals (that.line) && 
				   this.file.toString ().equals (that.file.toString ());
		} else
			return false;
	}
}