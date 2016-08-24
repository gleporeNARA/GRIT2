package grit;

import java.io.File;

public class CSVWriter {
    
    private static final String DELIMITER = "|";
    private static final String NL = "\n";
    
    public CSVWriter()
    {
            // default constructor
    }
    
    public String addTableHeader()
    {
		
        String header = "Match#" + DELIMITER;
        header += "Confidence" + DELIMITER;
        header += "Matching Text" + DELIMITER;
        header += "Full Line" + DELIMITER;
        header += "File Type" + DELIMITER;
        header += "File" + DELIMITER;
        header += "Line#" + NL;

        return header;
    }
	
    public String addTableRow(int matchNum, String matchMode, String matchText, String fullLine, String fileExtension, File filePath, int lineNum)
    {
        //String lineStripped = fullLine.replace(",", " ");
        
        String row = matchNum + DELIMITER;
        row += matchMode + DELIMITER;
        row += matchText + DELIMITER;
        row += fullLine + DELIMITER;
        row += fileExtension + DELIMITER;
        row += filePath + DELIMITER;
        row += lineNum + NL;

        return row;
    }
}