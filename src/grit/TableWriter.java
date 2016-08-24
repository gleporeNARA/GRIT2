package grit;

import javax.swing.table.DefaultTableModel;

public class TableWriter extends DefaultTableModel {

    public static Object[] table_header = { "Match#", "Confidence", "Matching Text", "Full Line", "File Type", "File", "Line#" };
    public static Object[] table_ext_header = { "Extension", "Count" };
    public static Object[] table_cat_header = { "Confidence", "Count" };
    
    public static Object[][] table_data = {{}};
    public static Object[][] table_ext_data = {{}};
    public static Object[][] table_cat_data = {{}};
    
    public TableWriter (Object[][] table_data, Object[] table_header) {
        
        super();
        TableWriter.table_header = table_header;
        TableWriter.table_data = table_data;
    }
}