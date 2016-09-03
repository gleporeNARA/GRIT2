package grit;

import javax.swing.*;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.DefaultTableModel;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.mbox.MboxParser;
import org.apache.tika.parser.mbox.OutlookPSTParser;
import org.apache.tika.parser.microsoft.JackcessParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.rtf.RTFParser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * This program is used to find Generalized Retriever of Information Tool.
 * 
 * @author Gautam Mehta (gxmehta@gmail.com), Duy L Nguyen (duyl3nguy3n@gmail.com)
 * @version 0.0.4
 * Version 0.0.4
 * - Revised Input Regex boxes to ONE
 * - Added Input button for regex file to be read (Currently Not-Functional)
 * 
 * Version 0.0.3
 * - Other Match mode activated
 * - All 5 text boxes can search "text" in a single line. Multi-line search has issues.
 * 
 * Version 0.0.2
 * - Consolidated SSN Regex and modes.
 * - Prepared Text fields for additional regex
 * 
 * Version 0.0.1:
 * - Basic GUI interface.
 * - Basic functionality.
 */

public class Main extends JFrame
{
    public static final String PROGRAM_TITLE = "GRIT";
    public static final String PROGRAM_VERSION = "0.0.4a";
    public static final int WIN_WIDTH = 1200;
    public static final int WIN_HEIGHT = 950;
    
    // SYSTEM COMPONENTS (invisible system)
    private static final String NL = "\n";

    private File userInput;
    private File textFileInput;
    private File outputFileHTML;
    private File outputFileCSV;

    private static JProgressBar JPBStatus;
    private static JFileChooser textFileChooser;
    private static JFileChooser fileChooser;
    private static JFileChooser fileSaver;
    private static FileNameExtensionFilter webpageFilter;
    private static FileNameExtensionFilter csvFilter;
    private static Scanner fileReader;
    private static FileWriter fileWriter;			// write in small chunk
    private static BufferedWriter bufferedWriter;	// write in large chunk
    private static HTMLWriter htmlWriter;
    private static CSVWriter csvWriter;
    private static DefaultTableModel JBTableModel;
    private static DefaultTableModel JBTFileExtModel;
    private static DefaultTableModel JBTCatModel;

    private SearchTask searchTask;
    private int totalFiles;
    private int fileCounter;
    private int readCounter;
    private int matchCounter;
    private int textCounter;
    private int ssnCounter;
    private int dobCounter;
    private int pobCounter;
    private int maidenCounter;
    private int alienCounter;
    private ExtensionCounter extCounter;
    private Date startSearch;
    private Date endSearch;
    
    private String textHTML;
    private String ssnHTML;
    private String dobHTML;
    private String pobHTML;
    private String maidenHTML;
    private String alienHTML;
    private String postHtmlResult;
    
    private String textCSV;
    private String ssnCSV;
    private String dobCSV;
    private String pobCSV;
    private String maidenCSV;
    private String alienCSV;
    private String postCSVResult;

    private List<Pattern> regexText;
    private List<Pattern> regexSSN;
    private List<Pattern> regexDoBs;
    private List<Pattern> regexPoBs;
    private List<Pattern> regexMaidens;
    private List<Pattern> regexAliens;
    
    // GUI COMPONENTS (visible interface)
    private JCheckBox JCBCheckAll;
    private JCheckBox JCBSSN;
    private JCheckBox JCBPoB; 
    private JCheckBox JCBDoB;
    private JCheckBox JCBMaiden;
    private JCheckBox JCBAlien;
    
    private JTextField JTField;
    private JButton JBTextUpload;
    
    private JCheckBox JCBAutoParser;

    private JRadioButton JRBDirectory;	
    private JRadioButton JRBFile;
    
    private JButton JBRemoveDuplicates;

    private JButton JBInput;
    private JButton JBRun;
    private JTextField JTAProgressLog;
    private JButton JBCancel;
    private JButton JBExport;

    private JTextArea JTAResultLog;
    private JTable JBTable;
    private JTable JBTFileExt;
    private JTable JBTCat;
    
    private JPanel JPMain;
    private JScrollPane row3;
    private JPanel row4;
    private JPanel row5;
    private JScrollPane matchPane;
    private JScrollPane extPane;
    private JScrollPane catPane;
    
    private ArrayList<Match> resultTextList;
    private HashSet<Match> resultTextListUnique;
    private ArrayList<Match> resultTextListUniqueFinal;
    private ArrayList<Match> resultSSNList;
    private HashSet<Match> resultSSNListUnique;
    private ArrayList<Match> resultSSNListUniqueFinal;
    private ArrayList<Match> resultOtherMatchList;
    private ArrayList<File> skipFiles;
    private HashSet<String> skipExtensions;
    //private JButton JBClear;

    public Main()
    {
        initSystemComponents();
        initGUIComponents();
    }

    private void initSystemComponents()
    {
        userInput = null;
        textFileInput = null;
        outputFileHTML = null;
        outputFileCSV = null;

        fileChooser = null;
        textFileChooser = null;
        fileSaver = null;
        fileReader = null;
        fileWriter = null;
        bufferedWriter = null;
        htmlWriter = new HTMLWriter();
        csvWriter = new CSVWriter();
        
        JBTableModel = new DefaultTableModel(TableWriter.table_data, TableWriter.table_header)
        {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    case 3:
                        return String.class;
                    case 4:
                        return String.class;
                    case 5:
                        return File.class;
                    case 6:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        JBTFileExtModel = new DefaultTableModel(TableWriter.table_ext_data, TableWriter.table_ext_header);
        JBTCatModel = new DefaultTableModel(TableWriter.table_cat_data, TableWriter.table_cat_header);

        searchTask = null;
        totalFiles = 0;
        fileCounter = 0;
        readCounter = 0;
        matchCounter = 0;
        textCounter = 0;
        ssnCounter = 0;
        dobCounter = 0;
        maidenCounter = 0;
        alienCounter = 0;
        extCounter = new ExtensionCounter();
        startSearch = null;
        endSearch = null;
        textHTML = "";
        ssnHTML = "";
        dobHTML = "";
        pobHTML = "";
        maidenHTML = "";
        alienHTML = "";

        regexText = new ArrayList<Pattern>();
        regexSSN = new ArrayList<Pattern>();
        regexDoBs = new ArrayList<Pattern>();
        regexPoBs = new ArrayList<Pattern>();
        regexMaidens = new ArrayList<Pattern>();
        regexAliens = new ArrayList<Pattern>();
                
        resultTextList = new ArrayList<Match>();
        resultTextListUnique = new HashSet<Match>();        
        resultTextListUniqueFinal = new ArrayList<Match>(); 
        resultSSNList = new ArrayList<Match>();
        resultSSNListUnique = new HashSet<Match>();        
        resultSSNListUniqueFinal = new ArrayList<Match>(); 
        resultOtherMatchList = new ArrayList<Match>();
        skipFiles = new ArrayList<File>();
        skipExtensions = new HashSet<String>();
        
        //Prepare Skipped Extensions:
        skipExtensions.add("mp3");
        skipExtensions.add("mp4");
        skipExtensions.add("ogg");
        skipExtensions.add("flac");
        skipExtensions.add("png");
        skipExtensions.add("gif");
        skipExtensions.add("bmp");
        skipExtensions.add("jpg");
        skipExtensions.add("jpeg");
        skipExtensions.add("avi");
        skipExtensions.add("mpg");
        skipExtensions.add("mpeg");
        skipExtensions.add("tar");
        skipExtensions.add("zip");
        skipExtensions.add("tz");
        skipExtensions.add("gz");
        skipExtensions.add("tif");
        skipExtensions.add("tiff");
        
        // build regex lists
        
        // perfect old format ssn with hyphens, followed by anything other than a number, dash, or slash
        addRegexToList("(\\b(?!000)(?!666)(?:[0-6]\\d{2}|7[0-2][0-9]|73[0-3]|7[5-6][0-9]|77[0-2]))-((?!00)\\d{2})-((?!0000)\\d{4})([^0-9-/]|)", regexSSN);
        // same as above but with a newline in front
        addRegexToList("\\s?^?SSN?\\s?#\\s?[0-9]", regexSSN); //Combined this one with the above regex
        //look for a space, the letters SSN, a possible space, and any number
        addRegexToList("\\sSSN\\s?[0-9]", regexSSN);
        // SSN or SSA plus the letters NO, plus a number within 5 spaces
        addRegexToList(" SSN?A?\\s?No\\s?.{0,5}[0-9]", regexSSN);
        // group of 3, 2, 4 separated by a space, bounded by a word boundary
        addRegexToList("(\\b|^)\\d{3} \\d{2} \\d{4}(\\b|$)", regexSSN);
        // group of 3, 2, 4 separated by a . a / or - bounded by something other than a number, hyphen or slash
        addRegexToList("([^0-9.-/]|^)\\d{3}[./-]\\d{2}[./-]\\d{4}([^0-9-/]|$)", regexSSN);
        
        //"birth" or "born" or "DOB" within 5 words of mm/dd/yy, mm-dd-yy, mm.dd.yy, mm dd yy, mm/dd/yyyy, mm-dd-yyyy ,mm.dd.yyyy ,mm dd yyyy
        addRegexToList("\\b(birth|born|DOB)\\W*(?:\\w*\\W*){1,5}((\\D+|^)(?:(1[0-2]|0?[1-9])([- /.]+)(3[01]|[12][0-9]|0?[1-9])|(3[01]|[12][0-9]|0?[1-9])([- /.]+)(1[0-2]|0?[1-9]))([- /.]+)(?:19|20)?\\d\\d)", regexDoBs);
        //"birth" or "born" or "DOB" within 5 words of yyyy/mm/dd, yyyy-mm-dd, yyyy.mm.dd, yyyy mm dd
        addRegexToList("\\b(birth|born|DOB)\\W*(?:\\w*\\W*){1,5}((19|20)\\d\\d([- /.]+)(0[1-9]|1[012])([- /.]+)(0[1-9]|[12][0-9]|3[01]))", regexDoBs);
        //"birth" or "born" or "DOB" within 5 words of a month spelled out date, with or without period, allows for 1st, 2nd, 3rd, 4th, etc.
        addRegexToList("\\b(birth|born|DOB)\\W*(?:\\w*\\W*){1,5}((?:Jan\\.?(?:uary)?|Feb\\.?(?:ruary)?|Mar\\.?(?:ch)?|Apr\\.?(?:il)?|May|Jun\\.?(?:e)?|Jul\\.(?:y)?|Aug\\.?(?:ust)?|Sep\\.?(?:t\\.?(?:ember)?)?|Oct\\.?(?:ober)?|Nov\\.?(?:ember)?|Dec\\.?(?:ember)?)[ ][0-3]?\\d(?:st|rd|nd|th)?,?[ ](?:19|20)\\d\\d)", regexDoBs);
        //"birth" or "born" or "DOB" within 5 words of a numeric day and a month spelled out (i.e. born on 31 December
        addRegexToList("\\b(birth|born|DOB)\\W*(?:\\w*\\W*){1,5}(0?[1-9]|[12][0-9]|3[01]) (?:Jan\\.?(?:uary)?|Feb\\.?(?:ruary)?|Mar\\.?(?:ch)?|Apr\\.?(?:il)?|May|Jun\\.?(?:e)?|Jul\\.(?:y)?|Aug\\.?(?:ust)?|Sep\\.?(?:t\\.?(?:ember)?)?|Oct\\.?(?:ober)?|Nov\\.?(?:ember)?|Dec\\.?(?:ember)?)", regexDoBs);

        addRegexToList("(POB|Place of Birth|birth place|birthplace|born in|born at|bornin|bornat|place ofbirth)", regexPoBs); 

        //mother's maiden name or nee
        addRegexToList("(maiden name|mother'?s? maiden name|\\bnee\\s)", regexMaidens);
        
        //Alien number regex from healthcare.gov
        addRegexToList("(\\b|^)(A|a)(-?[0-9]){9}(\\b|$)|(\\b|^)(A|a)(-?[0-9]){7}(\\b|$)", regexAliens);

        // setting for file chooser
        textFileChooser = new JFileChooser();
        textFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);	// set default selection mode
        textFileChooser.setMultiSelectionEnabled(false);	
        
        // setting for file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);	// set default selection mode
        fileChooser.setMultiSelectionEnabled(false);						// disable multi-selection
        
        // setting for file saver
        fileSaver = new JFileChooser();
        fileSaver.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileSaver.setMultiSelectionEnabled(false);
        
        webpageFilter = new FileNameExtensionFilter("HTML Webpage", "*.html");
        csvFilter = new FileNameExtensionFilter("Comma/Delimiter Separated Value File", "*.csv");
 
        fileSaver.setMultiSelectionEnabled(false);
        fileSaver.addChoosableFileFilter(csvFilter);
        fileSaver.addChoosableFileFilter(webpageFilter);
        fileSaver.removeChoosableFileFilter(fileSaver.getAcceptAllFileFilter());
        //fileSaver.setFileFilter(csvFilter);
    }
    
    private void initGUIComponents()
    {
        
        JPBStatus = new JProgressBar(0,100);
        
        JCBCheckAll = new JCheckBox();
        JCBSSN = new JCheckBox();
        JCBDoB = new JCheckBox();
        JCBMaiden = new JCheckBox();
        JCBPoB = new JCheckBox();
        JCBAlien = new JCheckBox();
        
        JTField = new JTextField();
        JBTextUpload = new JButton();
        
        JCBAutoParser = new JCheckBox();

        JRBDirectory = new JRadioButton();
        JRBFile = new JRadioButton(); 
        
        JBRemoveDuplicates = new JButton();
                
        JBInput = new JButton();
        JBRun = new JButton();
        JTAProgressLog = new JTextField();
        JBCancel = new JButton();
        JBExport = new JButton();
        
        JBTable = new JTable();
        JBTFileExt = new JTable();
        JBTCat = new JTable();
        
        
        //Row1: Elements
        JCBCheckAll.setText("Check All Options");
        JCBCheckAll.setToolTipText("(All Options Activated)");
        JCBSSN.setText("SSN Match"); JCBSSN.setSelected(true);
        JCBSSN.setToolTipText("Matches (SSN#, SS#, SSN, 555-55-5555). Most likely to match SSNs. Fewest false positives.");
        JCBDoB.setText("Date of Birth");
        JCBDoB.setToolTipText("(Birth, Born, DOB with a date) Matches terms related to date of birth.");
        JCBPoB.setText("Place of Birth");
        JCBPoB.setToolTipText("(POB, Place of Birth, birth place, birthplace, born in, born at) Matches terms related to place of birth");
        JCBMaiden.setText("Mother's Maiden Name or Nee");
        JCBMaiden.setToolTipText("Matches terms related to maiden names.");
        JCBAlien.setText("Alien Registration Number");
        JCBAlien.setToolTipText("Matches terms to Alien Registration Numbers.");
        
        JBTextUpload.setText("Upload Regex");
        JBTextUpload.setToolTipText("Upload Regex Patterns.");
        
        JCBAutoParser.setText("Read Additional Formats");
        JCBAutoParser.setToolTipText("The program will attempt to read additional file formats.");
        
        JRBDirectory.setText("One Directory");
        JRBDirectory.setSelected(true);
        JRBDirectory.setToolTipText("Searches all files under a directory.");

        JRBFile.setText("One File");
        JRBFile.setToolTipText("Single file search.");
        ButtonGroup BGReadMode = new ButtonGroup();
        BGReadMode.add(JRBDirectory);
        BGReadMode.add(JRBFile);
        
        JBRemoveDuplicates.setText("Remove Duplicates"); JBRemoveDuplicates.setEnabled(false);
        JBRemoveDuplicates.setToolTipText("Remove Duplicate Results");

        JBInput.setText(" Input ");
        JBInput.setToolTipText("Browses for directory or file to search.");
        JBRun.setText("  Run  ");
        JBRun.setToolTipText("Starts search.");
        JBCancel.setText("Cancel Search");  JBCancel.setEnabled(false);
        JBCancel.setToolTipText("Cancels running search. Results can still be saved.");
        JBExport.setText("Export Result");  JBExport.setEnabled(false);
        JBExport.setToolTipText("Saves last searched results as HTML or CSV file.");
        
        //Row2: Elements
        JTAProgressLog.setText("");
        JTAProgressLog.setEditable(false);
        JTAProgressLog.setHorizontalAlignment(JTextField.CENTER);
        JTAProgressLog.setBackground(new Color(250, 250, 241));
        JTAProgressLog.setMargin(new Insets(5, 5, 5, 5));
        JTAProgressLog.setToolTipText("Displays the current number of processed files.");
        
        //Row3: Elements
        JTAResultLog = new JTextArea(getTutorial());
        JTAResultLog.setEditable(false);
        JTAResultLog.setMargin(new Insets(5, 5, 5, 5));
        JTAResultLog.setLineWrap(true);
        JTAResultLog.setWrapStyleWord(true);
        
        //Row2: Elements
        JPBStatus.setValue(0);
        JPBStatus.setStringPainted(false);
        JPBStatus.setIndeterminate(true);
        JPBStatus.setVisible(false);
        JPBStatus.setBackground(Color.black);
        JPBStatus.setForeground(new Color(129,218,245));
	
        JPBStatus.setUI(new BasicProgressBarUI() {
              @Override
	      protected Color getSelectionBackground() { return new Color(129,218,245); }
              @Override
	      protected Color getSelectionForeground() { return Color.black; }
        });

        //Row1: Panel1: Elements Added
        JPanel panel1 = new JPanel();
        panel1.setBorder(BorderFactory.createTitledBorder("PII Match Modes"));
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
        panel1.add(JCBCheckAll);
        panel1.add(JCBSSN);
        panel1.add(JCBDoB);
        panel1.add(JCBMaiden);
        panel1.add(JCBPoB);
        panel1.add(JCBAlien);

        //Row1: Panel2: Elements Added
        JPanel panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createTitledBorder("Other Match Mode"));
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));
        panel2.setLayout(new GridLayout(2, 2, 0, 0));
        panel2.add(JTField);
        panel2.add(JBTextUpload);

        //Row1: Panel3: Elements Added
        JPanel panel3 = new JPanel();
        panel3.setBorder(BorderFactory.createTitledBorder("Read Mode"));
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));
        panel3.add(JRBDirectory);
        panel3.add(JRBFile);
        panel3.add(JCBAutoParser);
        panel3.add(JBRemoveDuplicates);

        //Row1: Panel4: Elements Added
        JPanel panel4 = new JPanel();
        panel4.setBorder(BorderFactory.createTitledBorder("Run Mode"));
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.LINE_AXIS));
        panel4.setBackground(new Color(224,242,247));
        panel4.setLayout(new GridLayout(2, 2, 0, 0));
        panel4.add(JBInput);
        panel4.add(JBRun);
        panel4.add(JBCancel);
        panel4.add(JBExport);

        //Row2: Panel5: Elements Added
        JPanel panel5 = new JPanel();
        panel5.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel5.setLayout(new BoxLayout(panel5, BoxLayout.PAGE_AXIS));
        panel5.setMinimumSize(new Dimension(Integer.MAX_VALUE, 15));
        panel5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel5.add(JTAProgressLog);
        panel5.add(JPBStatus);
        
        //Row1: Elements Populated
        JPanel row1 = new JPanel();
        row1.setMinimumSize(new Dimension(Integer.MAX_VALUE, 100));
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        row1.setLayout(new GridLayout(0, 4));
        row1.add(panel1);
        row1.add(panel2);
        row1.add(panel3);
        row1.add(panel4);
        
        //Row2: Elements Populated
        JPanel row2 = new JPanel();
        row2.setMinimumSize(new Dimension(Integer.MAX_VALUE, 30));
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row2.setLayout(new GridLayout(1, 1));
        row2.add(panel5);
        
        //Row3: Elements Populated
        row3 = new JScrollPane(JTAResultLog);
        row3.setPreferredSize(new Dimension(0, 400));
        row3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Result Set", 
                TitledBorder.CENTER, TitledBorder.TOP));
        
        //Row4: Elements Populated
        matchPane = new JScrollPane(JBTable);
        matchPane.setPreferredSize(new Dimension(0, 600));
        JBTable.setModel(JBTableModel);        
        JBTable.setAutoCreateRowSorter(false);
        
        row4 = new JPanel();
        row4.setMinimumSize(new Dimension(Integer.MAX_VALUE, 30));
        row4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row4.setLayout(new GridLayout(1, 1));
        row4.add(matchPane);
        
        //Row5: Elements Populated
        extPane = new JScrollPane(JBTFileExt);
        extPane.setPreferredSize(new Dimension(0, 150));
        JBTFileExt.setModel(JBTFileExtModel);
        JBTFileExt.setAutoCreateRowSorter(false);
        
        catPane = new JScrollPane(JBTCat);
        catPane.setPreferredSize(new Dimension(0, 150));
        JBTCat.setModel(JBTCatModel);
        JBTCat.setAutoCreateRowSorter(false);

        row5 = new JPanel();
        row5.setMinimumSize(new Dimension(Integer.MAX_VALUE, 30));
        row5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row5.setLayout(new GridLayout(1, 2));
        row5.add(extPane);
        row5.add(catPane);
        
        //Main: Setting Panel
        JPMain = new JPanel();
        JPMain.setLayout(new BoxLayout(JPMain, BoxLayout.PAGE_AXIS));
        JPMain.add(row1);
        JPMain.add(row2);
        JPMain.add(row3);
       
        
        // setting for main frame
        this.setTitle(PROGRAM_TITLE + " " + PROGRAM_VERSION);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setPreferredSize(new Dimension(WIN_WIDTH, WIN_HEIGHT));
        this.setMaximumSize(new Dimension(WIN_WIDTH,WIN_HEIGHT));
        this.setContentPane(JPMain);


        // setup action listeners
        JCBCheckAll.addActionListener(new CheckAllOptionsListener());
        
        JRBFile.addActionListener(new MyRunModeListener());
        JRBDirectory.addActionListener(new MyRunModeListener());
        
        JBRemoveDuplicates.addActionListener(new CleanResultsListener());
        
        JBTextUpload.addActionListener(new MyIOTextListener());
        JBInput.addActionListener(new MyIOListener());
        JBRun.addActionListener(new MySearchTaskListener());
        JBCancel.addActionListener(new MySearchTaskListener());
        JBExport.addActionListener(new MyIOListener());
        pack();
    }

    private String getTutorial()
    {
        String tutorial = "";
        tutorial += "*** " + PROGRAM_TITLE + " version " + PROGRAM_VERSION + " ***" + NL;
        tutorial += NL;
        tutorial += "THIS PROGRAM MAINLY SUPPORTS THE FOLLOWING FILE FORMATS:" + NL;
        tutorial += "1. TXT: plain text file." + NL;
        tutorial += "2. DOC: old Microsoft Word format." + NL;
        tutorial += "3. DOCX: new Microsoft Word format." + NL;
        tutorial += "4. XLS: old Microsoft Excel format." + NL;
        tutorial += "5. XLSX: new Microsoft Excel format." + NL;
        tutorial += "6. MSG: Microsoft Outlook format." + NL;
        tutorial += "7. HTML: standard web page format." + NL;
        tutorial += "8. XML: extensible web page format" + NL;
        tutorial += "9. RTF: rich text format" + NL;
        tutorial += "10. MBOX: Apple Mail format" + NL;
        tutorial += "11. PST: Microsoft Outlook format" + NL;
        tutorial += "12. MDB: Microsoft Access format" + NL;
        tutorial += "13. PDF: Portable Document format" + NL;
        tutorial += NL;
        tutorial += "HOW TO USE:" + NL;
        tutorial += "1. Select the approriate match mode(s). See below for detailed descriptions of matching algorithms." + NL;
        tutorial += "2. Select the approriate read mode." + NL;
        tutorial += "3. Browse to the directory/file you want to search by clicking Input." + NL;
        tutorial += "4. Hit Run to start the search." + NL;
        tutorial += "5. Once the search is done, you can save search results by clicking Save Results as HTML or CSV." + NL;
        tutorial += "   (search results are discarded if they are not saved upon exit for secure reasons.)" + NL;
        tutorial += NL;
        tutorial += "DETAILED DESCRIPTION OF REGULAR EXPRESSIONS:" + NL;
        tutorial += "   (all searches are case insensitive)" + NL;
        tutorial += NL;
        tutorial += "1. High Probability matches match perfectly formatted Social Security Numbers (555-55-5555) with the traditional numbering rules." + NL;
        tutorial += "In addition, the following formats are matched:" + NL;
        tutorial += "    a. SSN# and SSN # and SS # and SS# followed by a number" + NL;
        tutorial += "    b. a space, the letters SSN, a possible space, and a number" + NL;
        tutorial += "    c. group of 3, 2, 4 numbers separated by a space, bounded by a word boundary (non-alpha character)" + NL;
        tutorial += "    d. the letters SSN or SSA plus the letters NO, plus a number within 5 spaces" + NL;
        tutorial += NL;
        tutorial += "2. Medium probability matches searches for:" + NL;
        tutorial += "    a. # sign, three numbers matching rules, anything, then 6 more numbers" + NL;
        tutorial += "    b. three numbers matching rules, anything, then 6 more numbers WITH word boundaries, started by newline" + NL;
        tutorial += "    c. the phrase 'social security n' plus three numbers" + NL;
        tutorial += "    d. the letters ' SSA ' (with spaces around them) plus a number within 5 spaces" + NL;
        tutorial += NL;
        tutorial += "3. Low probability matches searches for:" + NL;
        tutorial += "    a. all rules but with forward slashes (also matches a lot of dates)" + NL;
        tutorial += "    b. any nine numbers bracketed by something other than a number, hyphen or slash" + NL;
        tutorial += "    c. group of 3, 2, 4 separated by a / or - bounded by something other than a number, hyphen or slash" + NL;
        tutorial += "    d. group of 3, 2, 4 separated by a period, bounded by something other than a number, hyphen or slash" + NL;
        tutorial += NL;
        tutorial += "4. Date of birth search matches:" + NL;
        tutorial += "    a. the words 'birth', 'born', 'DPOB', 'PDOB' or 'DOB' within 6 words of a date" + NL;
        tutorial += "    b. the word 'Date' within 1 to 6 words of 'Birth' or vice versa." + NL;
        tutorial += NL;
        tutorial += "5. Maiden name search matches:" + NL;
        tutorial += "    a. The phrase 'Mother's maiden name' with or without the apostrophe and the 's'" + NL;
        tutorial += "    b. The word 'nee'" + NL;
        tutorial += NL;
        tutorial += "6. Alien Registration Number matches:" + NL;
        tutorial += "    a. matches either a word boundary or the start of a line, then a capital A or lowercase a, " + NL;
        tutorial += "       then an optional dash, then either 9 or 7 numbers in a row, with any amount of other dashes " + NL;
        tutorial += "       in between, followed by a word boundary or the end of the line." + NL;
        tutorial += NL;
        tutorial += "NOTES:" + NL;
        tutorial += "This PIIFinder does not search for Social Security Numbers which follow the new formatting rules introduced in 2011." + NL;

        return tutorial;
    }

//######################################## GUI ACTION LISTENERS SECTION ##########################################//
    
    private class CleanResultsListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            if (event.getSource() == JBRemoveDuplicates)
            {
                clearOldExport();
                JBTableModel.setRowCount(0);

                searchTask.cleanTextResults(resultTextListUnique);
                searchTask.cleanSSNResults(resultSSNListUnique);
                searchTask.getOtherResults(resultOtherMatchList);
                JBTableModel.fireTableDataChanged();
                JBRemoveDuplicates.setEnabled(false);
                JBRemoveDuplicates.setText("Duplicates Removed");
                
                searchTask.getConfidenceTable();
                
                searchTask.buildHtmlResult();
                searchTask.buildCSVResult();
            }
        }
    }
    
    /**
     * This internal class listens for user's interaction with check all option.
     */
    private class CheckAllOptionsListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            // DIRECTORY ONLY MODE
            if (event.getSource() == JCBCheckAll)
            {
                if(JCBCheckAll.isSelected() == true) {
                    JCBSSN.setSelected(true);
                    JCBDoB.setSelected(true);
                    JCBMaiden.setSelected(true);
                    JCBPoB.setSelected(true);
                    JCBAlien.setSelected(true);
               } else {
            	   	JCBSSN.setSelected(false);
                    JCBDoB.setSelected(false);
                    JCBMaiden.setSelected(false);
                    JCBPoB.setSelected(false);
                    JCBAlien.setSelected(false);
                }
            }
        }
    }
    /**
     * This internal class listens for user's interaction with run mode.
     */
    private class MyRunModeListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            // DIRECTORY ONLY MODE
            if (event.getSource() == JRBDirectory)
            {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }

            // FILE ONLY MODE
            else if (event.getSource() == JRBFile)
            {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }
        }
    }

    
    /**
     * This internal class listens for user's input/output
     */
    private class MyIOTextListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            // INPUT BUTTON
            if (event.getSource() == JBTextUpload) 
            {
                // open browse directory/file dialog
                int userRespond = textFileChooser.showOpenDialog(Main.this);
 
                // user select a directory/file
                if (userRespond == JFileChooser.APPROVE_OPTION)
                {
                	textFileInput = textFileChooser.getSelectedFile();
                    JBTextUpload.setText("Input: "+textFileInput);
                }
            }
        }
    }
    
    /**
     * This internal class listens for user's input/output
     */
    private class MyIOListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            // INPUT BUTTON
            if (event.getSource() == JBInput) 
            {
                // open browse directory/file dialog
                int userRespond = fileChooser.showOpenDialog(Main.this);
 
                // user select a directory/file
                if (userRespond == JFileChooser.APPROVE_OPTION)
                {
                    userInput = fileChooser.getSelectedFile();
                    String msg = "Input: " + userInput + NL;
                    printToProgress(msg);
                }
            }
 
            // HTML SAVE BUTTON
            else if (event.getSource() == JBExport)
            {
                // get today date
                Calendar cal = Calendar.getInstance();
                String month = String.valueOf(cal.get(Calendar.MONTH)+1);
                String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
                String year = String.valueOf(cal.get(Calendar.YEAR));
 
                // open save file dialog with a default file name
                String filename = "piifinder_result_" + month + "_" + day + "_" + year + "";
                fileSaver.setSelectedFile(new File(filename));
 
                int userRespond1 = fileSaver.showSaveDialog(Main.this);
 
                // user enter a save file
                if (userRespond1 == JFileChooser.APPROVE_OPTION)
                {
                    if (fileSaver.getFileFilter().equals(webpageFilter)) 
                    {
                        outputFileHTML = new File(fileSaver.getSelectedFile()+".html");
                        if (outputFileHTML != null && outputFileHTML.exists())
                        {
                            String msg = "The file " + outputFileHTML.getName() + " already exists. Do you want to replace the existing file?";
                            String title = "Ovewrite file?";
                            int userRespond2 = JOptionPane.showConfirmDialog(Main.this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
 
                            // user choose NO
                            if (userRespond2 != JOptionPane.YES_OPTION)
                            {
                                return; // stop here
                            }
                        }
 
                        String path = fileSaver.getSelectedFile().toString();
                        if (!path.endsWith(".html")) {
                            path = path + ".html";
 
                            // try to write text to file writer
                            try
                            {
                                fileWriter = new FileWriter(path, false);
                                bufferedWriter = new BufferedWriter(fileWriter);
                                bufferedWriter.write(postHtmlResult);
 
                                bufferedWriter.close();
                                fileWriter.close();
 
                                printToProgress("Result has been saved: " + outputFileHTML + NL);
                                printToLog("*Result has been saved: " + outputFileHTML + NL);
                            } 
                            catch (IOException e) 
                            {
                                JOptionPane.showMessageDialog(Main.this, "ERROR: Invalid output file.");
                            }
                        }
 
                    } else if (fileSaver.getFileFilter().equals(csvFilter)) 
                    {
                        outputFileCSV = new File(fileSaver.getSelectedFile()+".csv");
                        if (outputFileCSV != null && outputFileCSV.exists()) 
                        {
                            String msg = "The file " + outputFileCSV.getName() + " already exists. Do you want to replace the existing file?";
                            String title = "Ovewrite file?";
                            int userRespond2 = JOptionPane.showConfirmDialog(Main.this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
 
                            // user choose NO
                            if (userRespond2 != JOptionPane.YES_OPTION)
                            {
                                return; // stop here
                            }
 
                        }
 
                        String path = fileSaver.getSelectedFile().toString();
                        if (!path.endsWith(".csv")) {
                            path = path + ".csv";
 
                            // try to write text to file writer
                            try
                            {
                                fileWriter = new FileWriter(path, false);
                                bufferedWriter = new BufferedWriter(fileWriter);
                                bufferedWriter.write(postCSVResult);
 
                                bufferedWriter.close();
                                fileWriter.close();
 
                                printToProgress("Result has been saved: " + outputFileCSV + NL);
                                printToLog("*Result has been saved: " + outputFileCSV + NL);
                            } 
                            catch (IOException e) 
                            {
                                JOptionPane.showMessageDialog(Main.this, "ERROR: Invalid output file.");
                            }
                        }
                    }
                }
                // user cancel save
                else
                {
                    return; // stop here
                }
            }
        }
    }


    /**
     * This internal class listens for user's interaction with run button.
     */
    private class MySearchTaskListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent event)
        {
            // RUN BUTTON
            if (event.getSource() == JBRun) 
            {
                // check if a match mode is selected
                if (!JCBSSN.isSelected() && !JCBPoB.isSelected() && !JCBDoB.isSelected() && !JCBMaiden.isSelected() && !JCBAlien.isSelected())
                {
                    JOptionPane.showMessageDialog(Main.this, "ERROR: No match mode is selected.");
                    return; // stop here
                }

                // check if there is an input file/directory
                if (userInput == null)
                {
                    JOptionPane.showMessageDialog(Main.this, "ERROR: No input file/directory.");
                    return; // stop here
                }

                // read mode: directory only
                if (fileChooser.getFileSelectionMode() == JFileChooser.DIRECTORIES_ONLY)
                {
                    clearOldSearch();
                    searchTask = new SearchTask();
                    searchTask.execute();
                    JBRun.setEnabled(false);
                    JBCancel.setEnabled(true);
                }

                // read mode: file only
                else if (fileChooser.getFileSelectionMode() == JFileChooser.FILES_ONLY)
                {	
                    clearOldSearch();
                    searchTask = new SearchTask();
                    searchTask.execute();
                    JBRun.setEnabled(false);
                    JBCancel.setEnabled(true);
                }

                JPMain.remove(row3);
                JPMain.add(row4);
                JPMain.add(row5);
                JPMain.validate();
                JPMain.repaint();
            }

            // CANCEL BUTTON
            else if (event.getSource() == JBCancel)
            {
                searchTask.cancel(true);
                //System.exit(0);
            }
        }
    }


//######################################## SEARCH TASK SECTION #################################################//
    
    private class SearchTask extends SwingWorker<Void, String>
    {
        /**
         * This method takes a given directory and find SSNs for all the files reachable from that directory.
         * 
         * @param dir - directory that need to be processed
         */
        public void recursiveSearch(File dir)
        {
            // handle interrupted (cancel)
            if (Thread.currentThread().isInterrupted())
            {
                return;
            }

            // build list of input files
            List<File> inputFiles = new ArrayList<File>();
            if (fileChooser.getFileSelectionMode() == JFileChooser.FILES_ONLY)
            {
                inputFiles.add(dir);
            }
            else {
                inputFiles = Arrays.asList(dir.listFiles());
            }

            // update counter
            totalFiles += inputFiles.size();
           
            // process file by file
            for (File file: inputFiles)
            {
                if (file.isDirectory())
                {
                    totalFiles --;
                    recursiveSearch(file);
                }
                else {
                    
                    InputStream input = null;
                    
                    try 
                    {
                        String fileName = file.getName();
                        String fileExtension = "txt";
                        int i = fileName.lastIndexOf(".");
                        if (i > 0)
                        {
                            fileExtension = fileName.substring(i+1);
                        }
                        
                        if (fileExtension.equals("txt"))
                        {
                            ContentHandler handler = new BodyContentHandler(-1);
                            input = new FileInputStream(file);
                            Metadata metadata = new Metadata();
                            TXTParser TXTParser = new TXTParser();
                            ParseContext context = new ParseContext();

                            TXTParser.parse(input, handler, metadata, context);

                            fileReader = new Scanner(handler.toString());
                        } 
                        else if (fileExtension.equals("docx"))
                        {
                            OPCPackage pkg = OPCPackage.open(file);
                            XWPFDocument docx = new XWPFDocument(OPCPackage.open(file));
                            XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
                            fileReader = new Scanner(extractor.getText());
                            pkg.close();
                        }
                        else if (fileExtension.equals("doc"))
                        {
                            NPOIFSFileSystem doc = new NPOIFSFileSystem(file);
                            WordExtractor extractor = new WordExtractor(doc.getRoot());
                            fileReader = new Scanner(WordExtractor.stripFields(extractor.getText()));
                            doc.close();
                        }
                        else if (fileExtension.equals("xlsx"))
                        {
                            OPCPackage pkg = OPCPackage.open(file);
                            XSSFWorkbook wb = new XSSFWorkbook(pkg);
                            XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
                            extractor.setFormulasNotResults(true);
                            extractor.setIncludeSheetNames(false);
                            fileReader = new Scanner(extractor.getText());
                            pkg.close();
                        }
                        else if (fileExtension.equals("xls"))
                        {
                            NPOIFSFileSystem xls = new NPOIFSFileSystem(file);
                            HSSFWorkbook wb = new HSSFWorkbook(xls.getRoot(), false);
                            ExcelExtractor extractor = new ExcelExtractor(wb);
                            extractor.setFormulasNotResults(true);
                            extractor.setIncludeSheetNames(false);
                            fileReader = new Scanner(extractor.getText());
                            xls.close();
                        }
                        else if (fileExtension.equals("msg"))
                        {
                            MAPIMessage msg = new MAPIMessage(file.getAbsolutePath());
                            fileReader = new Scanner(msg.getTextBody());
                        }
                        else if ((fileExtension.equals("htm"))||(fileExtension.equals("html")))
                        {
                            ContentHandler handler = new BodyContentHandler(-1);
                            input = new FileInputStream(file);
                            Metadata metadata = new Metadata();
                            HtmlParser HTMLParser = new HtmlParser();
                            ParseContext context = new ParseContext();

                            HTMLParser.parse(input, handler, metadata, context);

                            fileReader = new Scanner(handler.toString());
                        }
                        else if (fileExtension.equals("rtf"))
                        {
                            ContentHandler handler = new BodyContentHandler(-1);
                            input = new FileInputStream(file);
                            Metadata metadata = new Metadata();
                            RTFParser RTFParser = new RTFParser();
                            ParseContext context = new ParseContext();

                            RTFParser.parse(input, handler, metadata, context);

                            fileReader = new Scanner(handler.toString());
                        }
                        else if (fileExtension.equals("mbox"))
                        {
                            ContentHandler handler = new BodyContentHandler(-1);
                            input = new FileInputStream(file);
                            Metadata metadata = new Metadata();
                            MboxParser MBOXParser = new MboxParser();
                            ParseContext context = new ParseContext();

                            MBOXParser.parse(input, handler, metadata, context);

                            fileReader = new Scanner(handler.toString());
                        }
                        else if (fileExtension.equals("pst"))
                        {
                            ContentHandler handler = new BodyContentHandler(-1);
                            input = new FileInputStream(file);
                            Metadata metadata = new Metadata();
                            OutlookPSTParser OutlookPSTParser = new OutlookPSTParser();
                            ParseContext context = new ParseContext();

                            OutlookPSTParser.parse(input, handler, metadata, context);

                            fileReader = new Scanner(handler.toString());
                        }
                        else if (fileExtension.equals("mdb"))
                        {
                            ContentHandler handler = new BodyContentHandler(-1);
                            input = new FileInputStream(file);
                            Metadata metadata = new Metadata();
                            JackcessParser JackcessParser = new JackcessParser();
                            ParseContext context = new ParseContext();

                            JackcessParser.parse(input, handler, metadata, context);

                            fileReader = new Scanner(handler.toString());
                        }
                        else if (fileExtension.equals("pdf"))
                        {
                            ContentHandler handler = new BodyContentHandler(-1);
                            input = new FileInputStream(file);
                            Metadata metadata = new Metadata();
                            PDFParser PDFParser = new PDFParser();
                            ParseContext context = new ParseContext();

                            PDFParser.parse(input, handler, metadata, context);

                            fileReader = new Scanner(handler.toString());
                        }
                        else if (fileExtension.isEmpty()) 
                        {
                            fileReader = new Scanner(file);
                        }
                        else 
                        {
                            if (JCBAutoParser.isSelected())
                            {
                                if (skipExtensions.contains(fileExtension))
                                {
                                    System.out.println("Skipped");
                                    continue;
                                } else {
                                    ContentHandler handler = new BodyContentHandler(-1);
                                    input = new FileInputStream(file);
                                    Metadata metadata = new Metadata();
                                    AutoDetectParser parser = new AutoDetectParser();

                                    parser.parse(input, handler, metadata);

                                    fileReader = new Scanner(handler.toString());
                                }
                                
                            } else {
                                continue;
                            }
                        }
                        
                        // find matching regex in current processing file
                        matchRegex(file, fileExtension);
                        /*
                        throw new DataFormatException("DataFormatException");
                        
                    } catch (DataFormatException e) {
                        System.out.println("DFE "+e);
                        skipFiles.add(file);*/
                    } catch (NullPointerException e) {
                        System.out.println("NULLPE "+e);
                        skipFiles.add(file);
                    } catch (OutOfMemoryError e) {
                        System.out.println("OOME "+e);
                        skipFiles.add(file);
                    } catch (ZipException e) {
                        System.out.println("ZipE "+e);
                        skipFiles.add(file);
                    } catch (EOFException e) {
                        System.out.println("EOF "+e);
                        skipFiles.add(file);
                    } catch (FileNotFoundException e) {
                        System.out.println("FNFE "+e);
                        skipFiles.add(file);
                    } catch (ChunkNotFoundException e) {
                        System.out.println("CNFE "+e);
                        skipFiles.add(file);
                    } catch (InvalidFormatException e) {
                        System.out.println("IFE "+e);
                        skipFiles.add(file);
                    } catch (IOException e) {
                        System.out.println("IO "+e);
                        skipFiles.add(file);
                    } catch (TikaException e) {
                        System.out.println("TIKA "+e);
                        skipFiles.add(file);
                    } catch (SAXException e) {
                        System.out.println("SAX "+e);
                        skipFiles.add(file);
                    } finally {
                        if (input != null) {
                            try {
                                input.close();
                            } catch(IOException e) {
                                System.out.println("IOE "+e);
                            }
                        }
                    }
                }
            }
        }


        /**
         * This method does the regular expression matching.
         * Results will be output to the GUI and save in HTML format.
         * 
         * @param file - file that will be processed
         */
        private void matchRegex(File file, String fileExtension)
        {
            // init line counter
            int lineNum = 1;
            String lineA = "";
            
            regexText.clear();
            
            addTextToRegex(JTField.getText());
            
            System.out.println(regexText);

            // check if file is readable
            if (fileReader.hasNext())
            {
                readCounter ++;
                extCounter.count(fileExtension);
                lineA = fileReader.nextLine();				

            } 
            else {
                System.out.println(file.getName() + " ext: "+fileExtension);
            }

//////IF THERE ARE MULTIPLE LINES IN THE FILE////////////IF THERE ARE MULTIPLE LINES IN THE FILE//////
//////IF THERE ARE MULTIPLE LINES IN THE FILE////////////IF THERE ARE MULTIPLE LINES IN THE FILE//////
//////IF THERE ARE MULTIPLE LINES IN THE FILE////////////IF THERE ARE MULTIPLE LINES IN THE FILE//////		
			
            // use global file reader with file's text already loaded
            while(fileReader.hasNext())
            {
                String lineB = fileReader.nextLine();
                String line = lineA + lineB;
                Matcher patternMatcher = null;
                
                if (!(JTField.getText().isEmpty()))
                {
                    for (Pattern regexTexti : regexText)
                    {
                        patternMatcher = regexTexti.matcher(line.toLowerCase());
	            		while (patternMatcher.find())
	                    {
	            			textCounter++;
	                    	resultTextList.add(new Match(textCounter, "Text", JTField.getText(), line, fileExtension, file, lineNum));
	                        resultTextListUnique.add(new Match(textCounter, "Text", JTField.getText(), line, fileExtension, file, lineNum));
	                        
	                        JBTableModel.addRow(new Object[]{textCounter, "Text", JTField.getText(), line, fileExtension, file, lineNum});
	                    }	
                    }
                }
				
                if (JCBSSN.isSelected())
                {
                    for (Pattern regexSSNi : regexSSN)
                    {
                        patternMatcher = regexSSNi.matcher(line);
                        while (patternMatcher.find())
                        {
                            ssnCounter++;
                            resultSSNList.add(new Match(ssnCounter, "SSN", patternMatcher.group(), line, fileExtension, file, lineNum));
                            resultSSNListUnique.add(new Match(ssnCounter, "SSN", patternMatcher.group(), line, fileExtension, file, lineNum));
                            
                            JBTableModel.addRow(new Object[]{ssnCounter, "SSN", patternMatcher.group(), line, fileExtension, file, lineNum});
                        }
                    }
                }

                if (JCBDoB.isSelected())
                {
                    for (Pattern regexDoB: regexDoBs)
                    {
                        patternMatcher = regexDoB.matcher(line);
                        while (patternMatcher.find())
                        {
                            matchCounter ++;
                            dobCounter ++;

                            resultOtherMatchList.add(new Match(dobCounter, "DoB", patternMatcher.group(), line, fileExtension, file, lineNum));

                            JBTableModel.addRow(new Object[]{dobCounter, "DoB", patternMatcher.group(), line, fileExtension, file, lineNum});
                        }
                    }
                }

                if (JCBPoB.isSelected())
                {
                    for (Pattern regexPoB: regexPoBs)
                    {
                        patternMatcher = regexPoB.matcher(line);
                        while (patternMatcher.find())
                        {
                            matchCounter ++;
                            pobCounter ++;

                            resultOtherMatchList.add(new Match(pobCounter, "PoB", patternMatcher.group(), line, fileExtension, file, lineNum));

                            JBTableModel.addRow(new Object[]{pobCounter, "PoB", patternMatcher.group(), line, fileExtension, file, lineNum});
                        }
                    }
                }

                if (JCBMaiden.isSelected())
                {
                    for (Pattern regexMaiden: regexMaidens)
                    {
                        patternMatcher = regexMaiden.matcher(line);
                        while (patternMatcher.find())
                        {
                            matchCounter ++;
                            maidenCounter ++;

                            resultOtherMatchList.add(new Match(maidenCounter, "Maiden", patternMatcher.group(), line, fileExtension, file, lineNum));

                            JBTableModel.addRow(new Object[]{maidenCounter, "Maiden", patternMatcher.group(), line, fileExtension, file, lineNum});
                        }
                    }
                }
                
                if (JCBAlien.isSelected())
                {
                    for (Pattern regexAlien: regexAliens)
                    {
                        patternMatcher = regexAlien.matcher(line);
                        while (patternMatcher.find())
                        {
                            matchCounter ++;
                            alienCounter ++;

                            resultOtherMatchList.add(new Match(alienCounter, "Alien", patternMatcher.group(), line, fileExtension, file, lineNum));

                            JBTableModel.addRow(new Object[]{alienCounter, "Alien", patternMatcher.group(), line, fileExtension, file, lineNum});
                        }
                    }
                }
                lineNum ++;
                lineA = lineB;
            }
			
//////IF MATCH ON LAST LINE OR ONLY ONE LINE////////////IF MATCH ON LAST LINE OR ONLY ONE LINE//////
//////IF MATCH ON LAST LINE OR ONLY ONE LINE////////////IF MATCH ON LAST LINE OR ONLY ONE LINE//////
//////IF MATCH ON LAST LINE OR ONLY ONE LINE////////////IF MATCH ON LAST LINE OR ONLY ONE LINE//////			
			
            if( !(fileReader.hasNext()) )
            {

                Matcher patternMatcher = null;
                
                if (!(JTField.getText().isEmpty()))
                {
                    for (Pattern regexTexti : regexText)
                    {
                        patternMatcher = regexTexti.matcher(lineA.toLowerCase());
	            		while (patternMatcher.find())
	                    {
	                    	textCounter++;
	                    	resultTextList.add(new Match(textCounter, "Text", JTField.getText(), lineA, fileExtension, file, lineNum));
	                        resultTextListUnique.add(new Match(textCounter, "Text", JTField.getText(), lineA, fileExtension, file, lineNum));
	                        
	                        JBTableModel.addRow(new Object[]{textCounter, "Text", JTField.getText(), lineA, fileExtension, file, lineNum});
	                    }	
                    }
                }

                if (JCBSSN.isSelected())
                {
                    for (Pattern regexSSNi : regexSSN)
                    {
                        patternMatcher = regexSSNi.matcher(lineA);
                        while (patternMatcher.find())
                        {
                            ssnCounter++;
                            resultSSNList.add(new Match(ssnCounter, "SSN", patternMatcher.group(), lineA, fileExtension, file, lineNum));
                            resultSSNListUnique.add(new Match(ssnCounter, "SSN", patternMatcher.group(), lineA, fileExtension, file, lineNum));

                            JBTableModel.addRow(new Object[]{ssnCounter, "SSN", patternMatcher.group(), lineA, fileExtension, file, lineNum});
                        }
                    }
                }

                if (JCBDoB.isSelected())
                {
                    for (Pattern regexDoB: regexDoBs)
                    {
                        patternMatcher = regexDoB.matcher(lineA);
                        while (patternMatcher.find())
                        {
                            matchCounter ++;
                            dobCounter ++;

                            resultOtherMatchList.add(new Match(dobCounter, "DoB", patternMatcher.group(), lineA, fileExtension, file, lineNum));

                            JBTableModel.addRow(new Object[]{dobCounter, "DoB", patternMatcher.group(), lineA, fileExtension, file, lineNum});
                        }
                    }
                }

                if (JCBPoB.isSelected())
                {
                    for (Pattern regexPoB: regexPoBs)
                    {
                        patternMatcher = regexPoB.matcher(lineA);
                        while (patternMatcher.find())
                        {
                            matchCounter ++;
                            pobCounter ++;

                            resultOtherMatchList.add(new Match(pobCounter, "PoB", patternMatcher.group(), lineA, fileExtension, file, lineNum));

                            JBTableModel.addRow(new Object[]{pobCounter, "PoB", patternMatcher.group(), lineA, fileExtension, file, lineNum});
                        }
                    }
                }

                if (JCBMaiden.isSelected())
                {
                    for (Pattern regexMaiden: regexMaidens)
                    {
                        patternMatcher = regexMaiden.matcher(lineA);
                        while (patternMatcher.find())
                        {
                            matchCounter ++;
                            maidenCounter ++;

                            resultOtherMatchList.add(new Match(maidenCounter, "Maiden", patternMatcher.group(), lineA, fileExtension, file, lineNum));

                            JBTableModel.addRow(new Object[]{maidenCounter, "Maiden", patternMatcher.group(), lineA, fileExtension, file, lineNum});
                        }
                    }
                }
                if (JCBAlien.isSelected())
                {
                    for (Pattern regexAlien: regexAliens)
                    {
                        patternMatcher = regexAlien.matcher(lineA);
                        while (patternMatcher.find())
                        {
                            matchCounter ++;
                            alienCounter ++;

                            resultOtherMatchList.add(new Match(alienCounter, "Alien", patternMatcher.group(), lineA, fileExtension, file, lineNum));

                            JBTableModel.addRow(new Object[]{alienCounter, "Alien", patternMatcher.group(), lineA, fileExtension, file, lineNum});
                        }
                    }
                }
                lineNum ++;
            }

            // tidy up and update progress
            fileReader.close();
            publish("printCurrentProgress");
            fileCounter ++;
            //System.out.println("Search Ended");
            
        }
        
        
        private ArrayList getOtherResults(ArrayList<Match> elf)
        {
            for (Match pr : resultOtherMatchList)
            {
                JBTableModel.addRow(new Object[]{pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum()});
                if(pr.getConfidence().matches("Text")){
                    textHTML += htmlWriter.addTableRow(pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum());
                    textCSV += csvWriter.addTableRow(pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum());
                }
                if(pr.getConfidence().matches("PoB")){
                    pobHTML += htmlWriter.addTableRow(pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum());
                    pobCSV += csvWriter.addTableRow(pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum());
                }    
                if(pr.getConfidence().matches("DoB")){
                    dobHTML += htmlWriter.addTableRow(pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum());
                    dobCSV += csvWriter.addTableRow(pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum());
                }
                if(pr.getConfidence().matches("Maiden")){
                    maidenHTML += htmlWriter.addTableRow(pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum());
                    maidenCSV += csvWriter.addTableRow(pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum());
                }
                if(pr.getConfidence().matches("Alien")){
                    alienHTML += htmlWriter.addTableRow(pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum());
                    alienCSV += csvWriter.addTableRow(pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum());
                }
            }
            return resultOtherMatchList;
        }
        
        private ArrayList cleanTextResults(HashSet<Match> elf)
        {            
            for(Match pr : elf)
            {
                if(elf.contains(pr))
                {
                    resultTextListUniqueFinal.add(pr);
                }
            }
            
            Collections.sort(resultTextListUniqueFinal, new Comparator<Match>() 
            {
                @Override
                public int compare(Match z1, Match z2) 
                {
                    if (z1.getID() > z2.getID()) { return 1; }
                    if (z1.getID() < z2.getID()) { return -1; }
                    return 0;
                }
            });
            
            int i = 1;
            for (Match pr : resultTextListUniqueFinal)
            {
                JBTableModel.addRow(new Object[]{ pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() });
                textHTML += htmlWriter.addTableRow( pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() );
                textCSV += csvWriter.addTableRow( pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() );
                i++;
            }
            textCounter = resultTextListUniqueFinal.size();
            return resultTextListUniqueFinal;
        }
        
        private ArrayList cleanSSNResults(HashSet<Match> elf)
        {            
            for(Match pr : elf)
            {
                if(elf.contains(pr))
                {
                    resultSSNListUniqueFinal.add(pr);
                }
            }
            
            Collections.sort(resultSSNListUniqueFinal, new Comparator<Match>() 
            {
                @Override
                public int compare(Match z1, Match z2) 
                {
                    if (z1.getID() > z2.getID()) { return 1; }
                    if (z1.getID() < z2.getID()) { return -1; }
                    return 0;
                }
            });
            
            int i = 1;
            for (Match pr : resultSSNListUniqueFinal)
            {
                JBTableModel.addRow(new Object[]{ pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() });
                ssnHTML += htmlWriter.addTableRow( pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() );
                ssnCSV += csvWriter.addTableRow( pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() );
                i++;
            }
            ssnCounter = resultSSNListUniqueFinal.size();
            return resultSSNListUniqueFinal;
        }
        
        
        private ArrayList<Match> getTextResults(ArrayList<Match> elf)
        {
            int i = 1;
            for (Match pr : elf)
            {
                JBTableModel.addRow(new Object[]{ pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() });
                textHTML += htmlWriter.addTableRow( pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() );
                textCSV += csvWriter.addTableRow( pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() );
                i++;
            }
            textCounter = resultTextList.size();
            return resultTextList;
        }
        
        private ArrayList<Match> getSSNResults(ArrayList<Match> elf)
        {
            int i = 1;
            for (Match pr : elf)
            {
                JBTableModel.addRow(new Object[]{ pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() });
                ssnHTML += htmlWriter.addTableRow( pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() );
                ssnCSV += csvWriter.addTableRow( pr.setID(i), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() );
                i++;
            }
            ssnCounter = resultSSNList.size();
            return resultSSNList;
        }
        
        private void buildCSVResult()
        {
            postCSVResult += csvWriter.addTableHeader();
            if (!(JTField.getText().isEmpty()))
            {
                postCSVResult += textCSV;
            }
            if (JCBSSN.isSelected())
            {
                postCSVResult += ssnCSV;
            }
            if (JCBDoB.isSelected())
            {
                postCSVResult += dobCSV;
            }
            if (JCBPoB.isSelected())
            {
                postCSVResult += pobCSV;
            }			
            if (JCBMaiden.isSelected())
            {
                postCSVResult += maidenCSV;
            }
            if (JCBAlien.isSelected())
            {
                postCSVResult += alienCSV;
            }
        }

        /**
         * This method prepares search results in html format which can be saved later.
         */
        private void buildHtmlResult()
        {
            postHtmlResult += htmlWriter.addOpenHTMLTag();
            postHtmlResult += htmlWriter.addStyleSection();

            postHtmlResult += htmlWriter.addAnchorTopLink("top", "National Archives and Records Administration");
            postHtmlResult += htmlWriter.addResultTitle(startSearch);

            postHtmlResult += htmlWriter.addOpenCenterTag();
            postHtmlResult += htmlWriter.addOpenNavTag();
            postHtmlResult += htmlWriter.addOpenNavULTag();
            
            if (!(JTField.getText().isEmpty()))
            {
                postHtmlResult += htmlWriter.addOpenNavLITag();
                postHtmlResult += htmlWriter.addCounter(textCounter);
                postHtmlResult += htmlWriter.addTextLink("textResults", "Text Matches") + "";
                postHtmlResult += htmlWriter.addCloseNavLITag();
            }
            if (JCBSSN.isSelected())
            {
                postHtmlResult += htmlWriter.addOpenNavLITag();
                postHtmlResult += htmlWriter.addCounter(ssnCounter);
                postHtmlResult += htmlWriter.addTextLink("ssnResults", "SSN Matches") + "";
                postHtmlResult += htmlWriter.addCloseNavLITag();
            }
            if (JCBDoB.isSelected())
            {
                postHtmlResult += htmlWriter.addOpenNavLITag();
                postHtmlResult += htmlWriter.addCounter(dobCounter);
                postHtmlResult += htmlWriter.addTextLink("dobResults", "DoB Matches") + "";
                postHtmlResult += htmlWriter.addCloseNavLITag();
            }
            if (JCBPoB.isSelected())
            {
                postHtmlResult += htmlWriter.addOpenNavLITag();
                postHtmlResult += htmlWriter.addCounter(pobCounter);
                postHtmlResult += htmlWriter.addTextLink("pobResults", "PoB Matches") + "";
                postHtmlResult += htmlWriter.addCloseNavLITag();
            }			
            if (JCBMaiden.isSelected())
            {
                postHtmlResult += htmlWriter.addOpenNavLITag();
                postHtmlResult += htmlWriter.addCounter(maidenCounter);
                postHtmlResult += htmlWriter.addTextLink("maidenResults", "Maiden Matches") + "";
                postHtmlResult += htmlWriter.addCloseNavLITag();
            }
            if (JCBAlien.isSelected())
            {
                postHtmlResult += htmlWriter.addOpenNavLITag();
                postHtmlResult += htmlWriter.addCounter(alienCounter);
                postHtmlResult += htmlWriter.addTextLink("alienResults", "Alien Matches") + "";
                postHtmlResult += htmlWriter.addCloseNavLITag();
            }
            postHtmlResult += htmlWriter.addCloseNavULTag();
            postHtmlResult += htmlWriter.addCloseNavTag();
            postHtmlResult += htmlWriter.addCloseCenterTag();
            
            postHtmlResult += htmlWriter.addOpenCenterTag();
            postHtmlResult += htmlWriter.addOpenNavTag();
            postHtmlResult += htmlWriter.addOpenNavULTag();
            postHtmlResult += htmlWriter.addResultNote(skipFiles.size(), readCounter, totalFiles, textCounter + ssnCounter + matchCounter, calculateElapsedTime());
            postHtmlResult += htmlWriter.addExtNote(extCounter);
            postHtmlResult += htmlWriter.addCloseNavULTag();
            postHtmlResult += htmlWriter.addCloseNavTag();
            postHtmlResult += htmlWriter.addCloseCenterTag();

            if ((!(JTField.getText().isEmpty())) && (textCounter > 0))
            {
                postHtmlResult += htmlWriter.addOpenPanelTag();
                postHtmlResult += htmlWriter.addAnchorLink("textResults", "Text Found Results");
                postHtmlResult += htmlWriter.addOpenTableTag("textResultTable");
                postHtmlResult += htmlWriter.addTableHeader();
                postHtmlResult += textHTML;
                postHtmlResult += htmlWriter.addCloseTableTag();
                postHtmlResult += htmlWriter.addBackToTopLink("top", "Back to Top");
                postHtmlResult += htmlWriter.addClosePanelTag();
            }
            
            if (JCBSSN.isSelected() && (ssnCounter > 0))
            {
                postHtmlResult += htmlWriter.addOpenPanelTag();
                postHtmlResult += htmlWriter.addAnchorLink("ssnResults", "SSN Found Results");
                postHtmlResult += htmlWriter.addOpenTableTag("ssnResultTable");
                postHtmlResult += htmlWriter.addTableHeader();
                postHtmlResult += ssnHTML;
                postHtmlResult += htmlWriter.addCloseTableTag();
                postHtmlResult += htmlWriter.addBackToTopLink("top", "Back to Top");
                postHtmlResult += htmlWriter.addClosePanelTag();
            }

            if (JCBDoB.isSelected() && (dobCounter > 0))
            {
                postHtmlResult += htmlWriter.addOpenPanelTag();
                postHtmlResult += htmlWriter.addAnchorLink("dobResults", "DoB Found Results");
                postHtmlResult += htmlWriter.addOpenTableTag("dobResultTable");
                postHtmlResult += htmlWriter.addTableHeader();
                postHtmlResult += dobHTML;
                postHtmlResult += pobHTML;
                postHtmlResult += htmlWriter.addCloseTableTag();
                postHtmlResult += htmlWriter.addBackToTopLink("top", "Back to Top");
                postHtmlResult += htmlWriter.addClosePanelTag();
            }

            if (JCBPoB.isSelected() && (pobCounter > 0))
            {
                postHtmlResult += htmlWriter.addOpenPanelTag();
                postHtmlResult += htmlWriter.addAnchorLink("pobResults", "PoB Found Results");
                postHtmlResult += htmlWriter.addOpenTableTag("pobResultTable");
                postHtmlResult += htmlWriter.addTableHeader();
                postHtmlResult += dobHTML;
                postHtmlResult += pobHTML;
                postHtmlResult += htmlWriter.addCloseTableTag();
                postHtmlResult += htmlWriter.addBackToTopLink("top", "Back to Top");
                postHtmlResult += htmlWriter.addClosePanelTag();
            }

            if (JCBMaiden.isSelected() && (maidenCounter > 0))
            {
                postHtmlResult += htmlWriter.addOpenPanelTag();
                postHtmlResult += htmlWriter.addAnchorLink("maidenResults", "Maiden Name Found Results");
                postHtmlResult += htmlWriter.addOpenTableTag("maidenResultTable");
                postHtmlResult += htmlWriter.addTableHeader();
                postHtmlResult += maidenHTML;
                postHtmlResult += htmlWriter.addCloseTableTag();
                postHtmlResult += htmlWriter.addBackToTopLink("top", "Back to Top");
                postHtmlResult += htmlWriter.addClosePanelTag();
            }
            
            if (JCBAlien.isSelected() && (alienCounter > 0))
            {
                postHtmlResult += htmlWriter.addOpenPanelTag();
                postHtmlResult += htmlWriter.addAnchorLink("alienResults", "Alien Found Results");
                postHtmlResult += htmlWriter.addOpenTableTag("alienResultTable");
                postHtmlResult += htmlWriter.addTableHeader();
                postHtmlResult += alienHTML;
                postHtmlResult += htmlWriter.addCloseTableTag();
                postHtmlResult += htmlWriter.addBackToTopLink("top", "Back to Top");
                postHtmlResult += htmlWriter.addClosePanelTag();
            }
            
            if(skipFiles.size() > 0)
            {
                postHtmlResult += htmlWriter.addOpenPanelTag();
                postHtmlResult += htmlWriter.addAnchorLink("skippedResults", "Unread Files");
                postHtmlResult += htmlWriter.addOpenTableTag("unreadFilesTable");
                postHtmlResult += htmlWriter.addAltTableHeader();
                for (File f : skipFiles)
                {
                    postHtmlResult += htmlWriter.addAltTableRow(f.toString());
                }
                postHtmlResult += htmlWriter.addCloseTableTag();
                postHtmlResult += htmlWriter.addBackToTopLink("top", "Back to Top");
                postHtmlResult += htmlWriter.addClosePanelTag();
            }

            postHtmlResult += htmlWriter.addCloseHTMLTag();
        }


        @Override
        protected Void doInBackground() throws Exception
        {
            startSearch = new Date();

            recursiveSearch(userInput);
            
            return null;
        }


        @Override
        protected void process(List<String> msgList)
        {
            if (isCancelled())
            {
                return;
            }

            for (String msg : msgList)
            {
                if (msg.equals("printCurrentProgress"))
                {
                    JPBStatus.setVisible(true);
                    printToProgress("Completed " + fileCounter + " / " + totalFiles + " files." + " Results: " + (textCounter + ssnCounter + matchCounter) );
                }
                else
                {
                    printToLog(msg);
                }
            }
            
        }
        
        private void getConfidenceTable()
        {
            JBTCatModel.setRowCount(0);
            JBTCatModel.addRow(new Object[]{"Text Matches",textCounter});
            JBTCatModel.addRow(new Object[]{"SSN Matches",ssnCounter});
            JBTCatModel.addRow(new Object[]{"Date of Birth",dobCounter});
            JBTCatModel.addRow(new Object[]{"Place of Birth",pobCounter});
            JBTCatModel.addRow(new Object[]{"Maiden Names",maidenCounter});
            JBTCatModel.addRow(new Object[]{"Alien Registration Numbers",alienCounter});
            JBTCatModel.addRow(new Object[]{"Total Matches",textCounter + ssnCounter + matchCounter});
        }
        
        private void getExtensionTable()
        {
            for (String s : extCounter.extList){
                int i = extCounter.extList.indexOf(s);
                int c = extCounter.extCount.get(i);
                JBTFileExtModel.addRow(new Object[]{s,c});
            }
        }
        
        @Override
        protected void done()
        {            
            System.out.println(skipFiles.toString());
            // notify
            Toolkit.getDefaultToolkit().beep();
            JPBStatus.setVisible(false);
            JPBStatus.setValue(0);

            // update
            getTextResults(resultTextList);
            getSSNResults(resultSSNList);
            getOtherResults(resultOtherMatchList);
            getExtensionTable();
            getConfidenceTable();
            
            endSearch = new Date();
            JBRemoveDuplicates.setEnabled(true);
            JBRemoveDuplicates.setText("Remove Duplicates");
            JBRun.setEnabled(true);
            JBCancel.setEnabled(false);
            JBTable.setAutoCreateRowSorter(true);
            JBTFileExt.setAutoCreateRowSorter(true);
            JBTCat.setAutoCreateRowSorter(true);
            
            // build result messages
            String msg = "*Readable: " + readCounter + " files / " + totalFiles + " files."+ NL;
            msg += "*Found: " + (textCounter + ssnCounter + matchCounter) + " matches." + NL;
            msg += "*Elapsed Time: " + calculateElapsedTime() + NL;

            if (isCancelled())
            {
                String title = "Search is cancelled." + NL;
                printToProgress(title);
                printToLog("*" + title);
                printToLog(msg);
                JOptionPane.showMessageDialog(Main.this, msg, title, JOptionPane.INFORMATION_MESSAGE);
            }
            else if (isDone())
            {
                String title = "Search is done." + NL;
                printToProgress(title);
                printToLog("*" + title);
                printToLog(msg);
                JOptionPane.showMessageDialog(Main.this, msg, title, JOptionPane.INFORMATION_MESSAGE);
            }

            // prepare result in html format
            buildHtmlResult();
            buildCSVResult();

            // enable save after html result has been prepared
            JBExport.setEnabled(true);
        }
    }


//######################################## HELPER METHODS SECTION ################################################//

    /**
     * This method takes a given regex in its string form, compiles it to pattern, and adds to a given pattern list.
     * 
     * @param regex - regex in string form
     * @param regexList - pattern list where regex will be added to
     */
    private void addRegexToList(String regex, List<Pattern> regexList)
    {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        regexList.add(pattern);
    }
    
    private void addTextToRegex(String text)
    {
    	ArrayList<String> tempTextList = new ArrayList<>();
    	tempTextList.clear();
    	
    	String[] tempText = text.split("(,)|(\\|)");
    	for (int i = 0; i < tempText.length; i++)
		{
    		tempTextList.add(tempText[i].toLowerCase());
		}
    	System.out.println("List: "+tempTextList);
    	Pattern pattern = Pattern.compile("\\b("+StringUtils.join(tempTextList,"|")+")\\b");
        regexText.add(pattern);
    }

    private void clearOldExport()
    {
        textHTML = "";
        ssnHTML = "";
        dobHTML = "";
        pobHTML = "";
        maidenHTML = "";
        alienHTML = "";
        postHtmlResult = "";
        textCSV = "";
        ssnCSV = "";
        dobCSV = "";
        pobCSV = "";
        maidenCSV = "";
        alienCSV = "";
        postCSVResult = "";
    }
    /**
     * This method resets all system components that is used for search.
     */
    private void clearOldSearch()
    {
        //JTAResultLog.setText("*Input: " + userInput + NL);
        JBTableModel.setNumRows(0);
        JBTFileExtModel.setNumRows(0);
        JBTCatModel.setRowCount(0);
        skipFiles.clear();
        resultTextList.clear();
        resultTextListUnique.clear();
        resultTextListUniqueFinal.clear();
        resultSSNList.clear();
        resultSSNListUnique.clear();
        resultSSNListUniqueFinal.clear();
        resultOtherMatchList.clear();
        regexText.clear();
        totalFiles = 0;
        fileCounter = 0;
        readCounter = 0;
        matchCounter = 0;
        textCounter = 0;
        ssnCounter = 0;
        dobCounter = 0;
        pobCounter = 0;
        maidenCounter = 0;
        alienCounter = 0;
        extCounter = new ExtensionCounter();
        startSearch = null;
        endSearch = null;
        clearOldExport();
    }


    /**
     * This method calculates the elapsed time of a search.
     * 
     * @return elapsedTime - string presentation of elapsed time. 
     */
    private String calculateElapsedTime()
    {
        long start = startSearch.getTime();
        long end = endSearch.getTime();
        long diff = end - start;

        // time unit in millisecond
        final long SECONDUNIT = 1000;
        final long MINUTEUNIT = SECONDUNIT * 60;
        final long HOURUNIT = MINUTEUNIT * 60;
        //final long DAYUNIT = HOURUNIT * 24;

        // calculate elapsed time
        //String days = String.valueOf(diff / DAYUNIT);
        //diff = diff % DAYUNIT;
        String hours = String.valueOf(diff / HOURUNIT);
        diff = diff % HOURUNIT;
        String minutes = String.valueOf(diff / MINUTEUNIT);
        diff = diff % MINUTEUNIT;
        String seconds = String.valueOf(diff / SECONDUNIT);
        String elapsedTime = hours + "h:" + minutes + "m:" + seconds + "s";

        return elapsedTime;
    }

    /**
     * This method prints a given message to the progress log.
     * 
     * @param msg - message that need to be displayed.
     */
    private void printToProgress(String msg)
    {
        JTAProgressLog.setText(msg.trim());
    }


    /**
     * This method prints a given message to the result log.
     * 
     * @param msg - message that need to be displayed.
     */
    private void printToLog(String msg)
    {
        //JTAResultLog.append(msg);
        //JTAResultLog.setCaretPosition(JTAResultLog.getDocument().getLength());
    }

//######################################## MAIN FUNCTION SECTION ################################################//	

    /**
     * This is the main function that run this program/main class.
     */
    public static void main(String args[])
    {
        EventQueue.invokeLater(new Runnable() 
        {
            public void run()
            {
                new Main().setVisible(true);                
            }
        });
    }
}
