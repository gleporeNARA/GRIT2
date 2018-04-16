package grit;

import javax.swing.*;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.apache.tika.detect.DefaultDetector;
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
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.odf.OpenDocumentParser;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipException;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileReader;
import java.io.LineNumberReader;

/**
 * This program is used to find pre-defined, free text,and wildcard searches in a variety of files. Need to update versioning...
 *
 * @author Tam Tran (tranthientam@comcast.net), Gautam Mehta (gxmehta@gmail.com), Duy L Nguyen (duyl3nguy3n@gmail.com)
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
 *
 * Notes: subroutine methods are created to reduce redundant codes. multiple values passed to subroutine needed to 
 * be modified with persistancy, thus, method returns would not be feasible for this purpose. to achieve data
 * persistancy passed to subroutine, immutable data such as stings and integers are wrapped in class objects and pass
 * as reference to void return type subroutine for handling.
 */

public class Main extends JFrame {
	private static final String PROGRAM_TITLE = "GRIT";
	private static final String PROGRAM_VERSION = "0.0.10";
	private static final int WIN_WIDTH = 1200;
	private static final int WIN_HEIGHT = 850;

	private File userInput;
	private File outputFileHTML;
	private File outputFileCSV;

	private static JProgressBar JPBStatus;
	private static JProgressBar JPBStatus2;
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
	private int progressCounter;	// helper counter to update progress bar
	private int progressCounter2;	// helper counter to update progress bar

	private ExtensionCounter extCounter;
	private Date startSearch;
	private Date endSearch;

	private StringBuilder postHtmlResult;
	private StringBuilder postCSVResult;

	// GUI COMPONENTS (visible interface)
	private JCheckBox JCBCheckAll;
	private JCheckBox JCBAutoParser;

	private JRadioButton JRBDirectory;
	private JRadioButton JRBFile;
	private JRadioButton regexButton;
	private JRadioButton wildcardButton;
	private JRadioButton plainTextButton;
	//private ButtonGroup searchSelectGroup;

	//private JButton JBRemoveDuplicates;
	private JButton JBInput;
	private JButton JBRun;
	private JButton ClearButton;
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

	private ArrayList <Match> resultOtherMatchList;
	private ArrayList <File> skipFiles;
	private HashSet <String> skipExtensions;
	private HashMap <String, Component> HMComponents;



	/**
	 * The Main class constructor
	 */
	public Main() {
		initSystemComponents();
		initGUIComponents();
	}

	private void initSystemComponents() {
		userInput = null;
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

		JBTableModel = new DefaultTableModel(TableWriter.table_data, TableWriter.table_header) {
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

		skipFiles = new ArrayList<File>();
		resultOtherMatchList = new ArrayList <Match>();

		/* HashMap <String, Component>();
		 *
		 * creates a hash map of search Components. 'T' is creates a text box, 'C' creates a check box
		 * See java class "Component" for more details on methods and attributes.
		 */
		HMComponents = new HashMap<>();
		//  <Key(String),  Component>
		HMComponents.put ("TextSearchArea", new Component ('T', "Text", "", "Enter your own search text here"));
		HMComponents.put ("SSN", new Component ('C', "SSN", "SSN Match", "Matches (SSN#, SS#, SSN, 555-55-5555). Most likely to match SSNs. Fewest false positives."));
		HMComponents.put ("DoB", new Component ('C', "DoB", "Date of Birth", "(Birth, Born, DOB with a date) Matches terms related to date of birth."));
		HMComponents.put ("Maiden", new Component ('C', "Maiden", "Mother's Maiden Name or Nee", "Matches terms related to maiden names."));
		HMComponents.put ("PoB", new Component ('C', "PoB", "Place of Birth", "(POB, Place of Birth, birth place, birthplace, born in, born at) Matches terms related to place of birth"));
		HMComponents.put ("Alien", new Component ('C', "Alien", "Alien Registration Number", "Matches terms to Alien Registration Numbers."));
		HMComponents.put ("GrandJury", new Component ('C', "Grand Jury", "Grand Jury", "Find all matches term Grand Jury"));
		HMComponents.put ("FBIInfoFile", new Component ('C', "FBI Info File", "FBI Info Files", "FBI information files beginning with numbers beginning on 134, 137, 170"));
		HMComponents.put ("FBISource", new Component ('C', "FBI Source", "FBI Sources", "Find matches for protect identity, informant, psi, si, reliable, confidential"));
		HMComponents.put ("FBISourceCode", new Component ('C', "FBI Source Code", "FBI Source Codes", "AL,AQ,AX,AN,AT,BA,BH,BS,BQ,BU,BT,CE,CG,CI,CV,CO,DL,DN,DE,EP,HN,HO,IP,JN,JK,KC,KX,LV,LR,LA,LS,ME,MM,MI,MP,MO,NK,NH,NO,NR,NY,NF,OC,OM,PH,PX,PG,PD,RH,SC,SL,SU,SA,SD,SF,SJ,SV,SE,SI,TP,WFO,BER,BOG,BON,HON,LON,MAN,MEX,OTT,PAN,PAR,ROM,TOK, followed by a dash or space, and between 1 and 5 numbers."));
		//HMComponents.put ("WildCard", new Component( 'C',"WildCard","Wild card searching","Allow for Wild card searching using * and ?  Example *.doc  w??d.txt"));
		//HMComponents.put ("Regex", new Component())

		//Prepare Skipped Extensions:
		String skpExtLst [] = {"mp3", "mp4", "ogg", "flac", "png", "gif", "bmp", "jpg", "jpeg", "avi", "mpg", "mpeg", "tar", "zip", "tz", "gz", "tif", "tiff", "wav"};
		skipExtensions = new HashSet<String>();
		for (String s : skpExtLst)
			skipExtensions.add (s);

		/* *****************************************************************************************************************
		 Build Regex List													*
		 */
		// perfect old format ssn with hyphens, followed by anything other than a number, dash, or slash
		addRegexToList("(\\b(?!000)(?!666)(?:[0-6]\\d{2}|7[0-2][0-9]|73[0-3]|7[5-6][0-9]|77[0-2]))-((?!00)\\d{2})-((?!0000)\\d{4})([^0-9-/]|)", HMComponents.get("SSN").regex);
		// same as above but with a newline in front
		addRegexToList("(?i:\\s?^?SSN?\\s?#\\s?[0-9])", HMComponents.get("SSN").regex); //Combined this one with the above regex
		//look for a space, the letters SSN, a possible space, and any number
		addRegexToList("(?i:\\sSSN\\s?[0-9])", HMComponents.get("SSN").regex);
		// SSN or SSA plus the letters NO, plus a number within 5 spaces
		addRegexToList("(?i: SSN?A?\\s?No\\s?.{0,5}[0-9])", HMComponents.get("SSN").regex);
		// group of 3, 2, 4 separated by a space, bounded by a word boundary
		addRegexToList("(\\b|^)\\d{3} \\d{2} \\d{4}(\\b|$)", HMComponents.get("SSN").regex);
		// group of 3, 2, 4 separated by a . a / or - bounded by something other than a number, hyphen or slash
		addRegexToList("([^0-9.-/]|^)\\d{3}[./-]\\d{2}[./-]\\d{4}([^0-9-/]|$)", HMComponents.get("SSN").regex);
		//"birth" or "born" or "DOB" within 5 words of mm/dd/yy, mm-dd-yy, mm.dd.yy, mm dd yy, mm/dd/yyyy, mm-dd-yyyy ,mm.dd.yyyy ,mm dd yyyy
		addRegexToList("\\b(?i:(birth|born|DOB))\\W*(?:\\w*\\W*){1,5}((\\D+|^)(?:(1[0-2]|0?[1-9])([- /.]+)(3[01]|[12][0-9]|0?[1-9])|(3[01]|[12][0-9]|0?[1-9])([- /.]+)(1[0-2]|0?[1-9]))([- /.]+)(?:19|20)?\\d\\d)", HMComponents.get("DoB").regex);
		//"birth" or "born" or "DOB" within 5 words of yyyy/mm/dd, yyyy-mm-dd, yyyy.mm.dd, yyyy mm dd
		addRegexToList("\\b(?i:(birth|born|DOB))\\W*(?:\\w*\\W*){1,5}((19|20)\\d\\d([- /.]+)(0[1-9]|1[012])([- /.]+)(0[1-9]|[12][0-9]|3[01]))", HMComponents.get("DoB").regex);
		//"birth" or "born" or "DOB" within 5 words of a month spelled out date, with or without period, allows for 1st, 2nd, 3rd, 4th, etc.
		addRegexToList("\\b(?i:(birth|born|DOB)\\W*(?:\\w*\\W*){1,5}((?:Jan\\.?(?:uary)?|Feb\\.?(?:ruary)?|Mar\\.?(?:ch)?|Apr\\.?(?:il)?|May|Jun\\.?(?:e)?|Jul\\.(?:y)?|Aug\\.?(?:ust)?|Sep\\.?(?:t\\.?(?:ember)?)?|Oct\\.?(?:ober)?|Nov\\.?(?:ember)?|Dec\\.?(?:ember)?)[ ][0-3]?\\d(?:st|rd|nd|th)?,?[ ](?:19|20)\\d\\d))", HMComponents.get("DoB").regex);
		//"birth" or "born" or "DOB" within 5 words of a numeric day and a month spelled out (i.e. born on 31 December)
		addRegexToList("\\b(?i:(birth|born|DOB)\\W*(?:\\w*\\W*){1,5}(0?[1-9]|[12][0-9]|3[01]) (?:Jan\\.?(?:uary)?|Feb\\.?(?:ruary)?|Mar\\.?(?:ch)?|Apr\\.?(?:il)?|May|Jun\\.?(?:e)?|Jul\\.(?:y)?|Aug\\.?(?:ust)?|Sep\\.?(?:t\\.?(?:ember)?)?|Oct\\.?(?:ober)?|Nov\\.?(?:ember)?|Dec\\.?(?:ember)?))", HMComponents.get("DoB").regex);
		//Place of Birth
		addRegexToList("(?i:(POB|Place of Birth|birth place|birthplace|born in|born at|bornin|bornat|place ofbirth))", HMComponents.get("PoB").regex);
		//mother's maiden name or nee
		addRegexToList("(?i:(maiden name|mother'?s? maiden name|\\bnee\\s))", HMComponents.get("Maiden").regex);
		//Alien number regex from healthcare.gov, modified to allow for hyphens, spaces, or dots as separators, and between 7 and 9 numbers
		addRegexToList("(\\b|^)(A|a)(([- .]+)?[0-9]){7}(\\b|$)|(\\b|^)(A|a)(([- .]+)?[0-9]){8}(\\b|$)|(\\b|^)(A|a)(([- .]+)?[0-9]){9}(\\b|$)", HMComponents.get("Alien").regex);
		//addRegexToList("(\\b|^)(A|a)(-?[0-9]){9}(\\b|$)|(\\b|^)(A|a)(-?[0-9]){7}(\\b|$)", HMComponents.get("Alien").regex);
		//Grand Jury
		addRegexToList("(?i:Grand Jury)", HMComponents.get("GrandJury").regex);
		//FBI Sources terms for protect identity, informant, psi, si, reliable, confidential
		addRegexToList("\\b(protect identity|informant|psi|si|reliable|confidential)\\b", HMComponents.get("FBISource").regex);
		//Find FBI information files beginning with numbers beginning with 134, 137, 170, followed by a dash and more numbers
		addRegexToList("\\b(134-\\d*|137-\\d*|170-\\d*)\\b", HMComponents.get("FBIInfoFile").regex);
		//FBI source codes
		addRegexToList("\\b(AL|AQ|AX|AN|AT|BA|BH|BS|BQ|BU|BT|CE|CG|CI|CV|CO|DL|DN|DE|EP|HN|HO|IP|JN|JK|KC|KX|LV|LR|LA|LS|ME|MM|MI|MP|MO|NK|NH|NO|NR|NY|NF|OC|OM|PH|PX|PG|PD|RH|SC|SL|SU|SA|SD|SF|SJ|SV|SE|SI|TP|WFO|BER|BOG|BON|HON|LON|MAN|MEX|OTT|PAN|PAR|ROM|TOK)(\\s|-)\\d{1,5}\\b", HMComponents.get("FBISourceCode").regex);

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

		initNewSearch(); 				//<====================initialize search helper variables here
	}

	/**
	 * setting up the visual components for the application.
	 * note: this could potentially be the view component for mvc
	 */
	private void initGUIComponents() {
		//Row1: Elements

		JPanel row1 = new JPanel();
		buildRow_1(row1);



		//JBRemoveDuplicates = new JButton("Remove Duplicates");
		//JBRemoveDuplicates.setToolTipText("Remove Duplicate Results");
		//JBRemoveDuplicates.setEnabled(false);


		//Row2: Elements
		JTAProgressLog = new JTextField("");
		JTAProgressLog.setEditable(false);
		JTAProgressLog.setHorizontalAlignment(JTextField.CENTER);
		JTAProgressLog.setBackground(new Color(250, 250, 241));
		JTAProgressLog.setMargin(new Insets(2, 2, 2, 2));
		JTAProgressLog.setToolTipText("Displays the current number of processed files");

		JPBStatus = new JProgressBar(0,100);
		JPBStatus.setBorderPainted(false);
		JPBStatus.setVisible(false);
		JPBStatus.setForeground(new Color(129,218,245));
		JPBStatus.setMinimumSize(new Dimension(Integer.MAX_VALUE, 3));
		JPBStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 3));

		JPBStatus2 = new JProgressBar(0,100);
		JPBStatus2.setBorderPainted(false);
		JPBStatus2.setVisible(false);
		JPBStatus2.setForeground(new Color(129,218,245));
		JPBStatus2.setMinimumSize(new Dimension(Integer.MAX_VALUE, 3));
		JPBStatus2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 3));

		//Row3: Elements
		JTAResultLog = new JTextArea(getTutorial());
		JTAResultLog.setEditable(false);
		JTAResultLog.setMargin(new Insets(5, 5, 5, 5));
		JTAResultLog.setLineWrap(true);
		JTAResultLog.setWrapStyleWord(true);


		row1.setMinimumSize(new Dimension(Integer.MAX_VALUE, 200));
		row1.setPreferredSize((new Dimension(WIN_WIDTH,210)));
		row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300)); //NOTE: here
		row1.setLayout(new GridLayout(0, 4));
		//row1.setLayout(new FlowLayout(FlowLayout.LEFT));


		//Row2: Panel5: Elements Added
		JPanel panel5 = new JPanel();
		panel5.setLayout(new BoxLayout(panel5, BoxLayout.PAGE_AXIS));
		panel5.setMinimumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		panel5.add(JTAProgressLog);
		panel5.add(JPBStatus2);
		panel5.add(JPBStatus);

		//Row2: Elements Populated
		JPanel row2 = new JPanel();
		row2.setMinimumSize(new Dimension(Integer.MAX_VALUE, 30));
		row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		row2.setLayout(new GridLayout(1, 1));
		row2.add(panel5);

		//Row3: Elements Populated
		row3 = new JScrollPane(JTAResultLog);
		row3.setPreferredSize(new Dimension(0, 400));
		row3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Result Set", TitledBorder.CENTER, TitledBorder.TOP));

		//Row4: Elements Populated
		JBTable = new JTable();
		JBTable.setModel(JBTableModel);
		JBTable.setAutoCreateRowSorter(false);
		matchPane = new JScrollPane(JBTable);
		matchPane.setPreferredSize(new Dimension(0, 600));

		row4 = new JPanel();
		row4.setMinimumSize(new Dimension(Integer.MAX_VALUE, 30));
		row4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		row4.setLayout(new GridLayout(1, 1));
		row4.add(matchPane);

		//Row5: Elements Populated
		JBTFileExt = new JTable();
		JBTFileExt.setModel(JBTFileExtModel);
		JBTFileExt.setAutoCreateRowSorter(false);
		extPane = new JScrollPane(JBTFileExt);
		extPane.setPreferredSize(new Dimension(0, 150));

		JBTCat = new JTable();
		JBTCat.setModel(JBTCatModel);
		JBTCat.setAutoCreateRowSorter(false);
		catPane = new JScrollPane(JBTCat);
		catPane.setPreferredSize(new Dimension(0, 150));

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

		//JBRemoveDuplicates.addActionListener(new CleanResultsListener());

		JBInput.addActionListener(new MyIOListener());
		JBRun.addActionListener(new MySearchTaskListener());
		JBCancel.addActionListener(new MySearchTaskListener());
		JBExport.addActionListener(new MyIOListener());
		pack();
	}  //end initGUIComponents()

	/** buildRow_1()
	 *  This function calls other sub functions to build all the panels and content in Row1
	 *  The purpose is just to aid in code folding and making the Main class easier to navigate.
	 *
	 * @param input - Panel to add content too.
	 */
	private void buildRow_1(JPanel input) {
		build_PII_1(input); //panel 1
		build_PII_2(input); //panel 2
		buildTextSearch(input);//panel 3
		//buildReadMode(input); //old panel 3
		buildPan_4(input); //Read + Run mode panels

	}

	/** build_PII_1()
	 *  A utility function that builds and adds all content to PII Match Mode 1 Panel
	 *
	 * @param input - The panel to add content to
	 */
	private void build_PII_1(JPanel input) {
		JPanel panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createTitledBorder("PII Match Modes"));
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
		JCBCheckAll = new JCheckBox("Check All Options");
		JCBCheckAll.setToolTipText("(All Options Activated)");
		panel1.add(JCBCheckAll);
		panel1.add(HMComponents.get("SSN").checkBox);
		panel1.add(HMComponents.get("DoB").checkBox);
		panel1.add(HMComponents.get("Maiden").checkBox);
		panel1.add(HMComponents.get("PoB").checkBox);
		panel1.add(HMComponents.get("Alien").checkBox);
		input.add(panel1);

	}

	/** buildTextSearch()
	 *
	 * This function builds all needed items and sub-panels to manage the layout for the User Text Search panel.
	 *
	 * @param input - Panel to add content to
	 */
	private void buildTextSearch(JPanel input) {
		JPanel panel = new JPanel();	//two sub panels are placed inside of panel_2 using grid layout
		panel.setLayout(new GridLayout(2,2,0,0));

		panel.setBorder(BorderFactory.createTitledBorder("Text Search Mode"));

		panel.add(HMComponents.get("TextSearchArea").text);



		regexButton = new JRadioButton("Regex");
		regexButton.setToolTipText("Search with regular expressions");
		wildcardButton = new JRadioButton("Wildcard");
		wildcardButton.setToolTipText("Search using * and ?  Example *.doc  w??d.txt\"");
		plainTextButton = new JRadioButton("Plain Text");
		plainTextButton.setToolTipText("Search using exact matching text");

		ButtonGroup searchSelectGroup = new ButtonGroup();
		searchSelectGroup.add(regexButton);
		searchSelectGroup.add(wildcardButton);
		searchSelectGroup.add(plainTextButton);

		//JPanel sub_pan1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel sub_pan1 = new JPanel();
		JPanel radioButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		sub_pan1.setLayout(new BoxLayout(sub_pan1,BoxLayout.Y_AXIS));

		radioButtonPanel.add(plainTextButton);
		radioButtonPanel.add(wildcardButton);
		radioButtonPanel.add(regexButton);
		plainTextButton.setSelected(true);

		ClearButton = new JButton("Clear");
		sub_pan1.add(radioButtonPanel);
		sub_pan1.add(ClearButton);
		ClearButton.addActionListener(new MySearchTaskListener());

		panel.add(sub_pan1);
		input.add(panel);
	}
	private void build_PII_2(JPanel input) {

		JPanel sub1 = new JPanel();	//to get proper alignment of new check boxes above "Other Match mode"
		sub1.setPreferredSize(new Dimension(WIN_WIDTH/4,125));
		sub1.setMaximumSize(new Dimension(WIN_WIDTH/4,125));

		//JPanel sub2 = new JPanel();	//two sub panels are placed inside of panel_2 using grid layout

		//sub2.setPreferredSize(new Dimension(WIN_WIDTH/4, 74));


		sub1.setBorder(BorderFactory.createTitledBorder("PII Match Modes"));
		sub1.setLayout(new BoxLayout(sub1, BoxLayout.PAGE_AXIS));
		sub1.add(HMComponents.get("GrandJury").checkBox);
		sub1.add(HMComponents.get("FBIInfoFile").checkBox);
		sub1.add(HMComponents.get("FBISource").checkBox);
		sub1.add(HMComponents.get("FBISourceCode").checkBox);


		JPanel panel_2 = new JPanel();
		//panel_2.setLayout(new GridLayout(2,1,0,10));
		//panel_2.setLayout(new BoxLayout(panel_2,BoxLayout.Y_AXIS));
		//panel_2.setMaximumSize(new Dimension(WIN_WIDTH/4,125));

		panel_2.add(sub1);
		//panel_2.add(sub2);
		input.add(panel_2);

	}

	private void buildReadMode(JPanel input) {
		JPanel panel_3 = new JPanel();
		JRBDirectory = new JRadioButton("One Directory");
		JRBDirectory.setToolTipText("Searches all files under a directory.");
		JRBDirectory.setSelected(true);

		JRBFile = new JRadioButton("One File");
		JRBFile.setToolTipText("Single file search");

		JCBAutoParser = new JCheckBox("Read Additional Formats");
		JCBAutoParser.setToolTipText("The program will attempt to read additional file formats.");

		ButtonGroup BGReadMode = new ButtonGroup();		//adding radio button to group
		BGReadMode.add(JRBDirectory);
		BGReadMode.add(JRBFile);

		panel_3.setBorder(BorderFactory.createTitledBorder("Read Mode"));
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.PAGE_AXIS));
		panel_3.add(JRBDirectory);
		panel_3.add(JRBFile);
		panel_3.add(JCBAutoParser);

		input.add(panel_3);
	}

	private void buildPan_4(JPanel input) {
		JPanel panel_4 = new JPanel();
		panel_4.setLayout(new GridLayout(2, 1, 0, 0));
		JPanel sub1 = new JPanel();

		sub1.setLayout(new GridLayout(2,2,0,0));
		sub1.setBorder(BorderFactory.createTitledBorder("Run Mode"));


		JBInput = new JButton(" Input ");
		JBInput.setToolTipText("Browses for directory or file to search");

		JBRun = new JButton("  Run  ");
		JBRun.setToolTipText("Starts search");

		JBCancel = new JButton("Cancel Search");
		JBCancel.setToolTipText("Cancels running search. Results can still be saved");
		JBCancel.setEnabled(false);

		JBExport = new JButton("Export Result");
		JBExport.setToolTipText("Saves last searched results as HTML or CSV file");
		JBExport.setEnabled(false);
		sub1.add(JBInput);
		sub1.add(JBRun);
		sub1.add(JBCancel);
		sub1.add(JBExport);

//		panel_4.setBorder(BorderFactory.createTitledBorder("Run Mode"));
//		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.LINE_AXIS));
//		panel_4.add(JBInput);
//		panel_4.add(JBRun);
//		panel_4.add(JBCancel);
//		panel_4.add(JBExport);
		panel_4.add(sub1);
		buildReadMode(panel_4); // re-using old pan3 function.  pan3 is new sub1
		input.add(panel_4);
	}
	/**
	 * the help tutorial method, this is the information
	 * text that is displayed in the result set window when the app starts up
	 */
	private String getTutorial() {
		return "*** " + PROGRAM_TITLE + " version " + PROGRAM_VERSION + " ***\n\n" + Help.showHelp ();
	}

	/**
	 * listens for user's interaction with check all option.
	 */
	private class CheckAllOptionsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			// DIRECTORY ONLY MODE
			if (event.getSource() == JCBCheckAll) {
				if(JCBCheckAll.isSelected()) {
					for (Component comp : HMComponents.values ())
						if (comp.TYPE == 'C')
							comp.checkBox.setSelected(true);
				} else {
					for (Component comp : HMComponents.values ())
						if (comp.TYPE == 'C')
							comp.checkBox.setSelected(false);
				}
			}
		}
	}

	/**
	 * listens for user's interaction with run mode.
	 */
	private class MyRunModeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == JRBDirectory)				// DIRECTORY ONLY MODE
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			else if (event.getSource() == JRBFile)				// FILE ONLY MODE
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
	}

	/**
	 * listens for user's input and export button
	 */
	private class MyIOListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == JBInput) {					// INPUT BUTTON
				int userRespond = fileChooser.showOpenDialog(Main.this);	// open browse directory/file dialog
				if (userRespond == JFileChooser.APPROVE_OPTION) {	// user select a directory/file
					userInput = fileChooser.getSelectedFile();
					System.out.println ("In MyIOListerner: " + userInput);			//<================ for debug
					printToProgress("Input: " + userInput + "\n");
				}
			} else if (event.getSource() == JBExport) {				// HTML SAVE BUTTON
				Calendar cal = Calendar.getInstance ();		// get today date
				// open save file dialog with a default file name
				StringBuilder filename = new StringBuilder ("pii_finder_result_" + (cal.get(Calendar.MONTH) + 1) + "_" + cal.get(Calendar.DAY_OF_MONTH) + "_" + cal.get(Calendar.YEAR));
				fileSaver.setSelectedFile(new File(filename.toString ()));

				int userRespond1 = fileSaver.showSaveDialog(Main.this);

				if (userRespond1 == JFileChooser.APPROVE_OPTION) {	// user enter a save file
					if (fileSaver.getFileFilter().equals(webpageFilter)) {
						outputFileHTML = new File(fileSaver.getSelectedFile() + ".html");
						if (outputFileHTML != null && outputFileHTML.exists()) {
							StringBuilder msg = new StringBuilder ("The file " + outputFileHTML.getName() + " already exists. Do you want to replace the existing file?");
							StringBuilder title = new StringBuilder ("Overwrite file?");
							int userRespond2 = JOptionPane.showConfirmDialog(Main.this, msg.toString (), title.toString (), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

							if (userRespond2 != JOptionPane.YES_OPTION)	// user choose NO
								return; // stop here
						}

						String path = fileSaver.getSelectedFile().toString();
						if (!path.endsWith(".html")) {
							path += ".html";

							try	{	// try to write text to file writer
								fileWriter = new FileWriter(path, false);
								bufferedWriter = new BufferedWriter(fileWriter);
								bufferedWriter.write(postHtmlResult.toString ());

								bufferedWriter.close();
								fileWriter.close();

								printToProgress("Result has been saved: " + outputFileHTML + "\n");
								printToLog("*Result has been saved: " + outputFileHTML + "\n");
							} catch (IOException e) {
								JOptionPane.showMessageDialog(Main.this, "ERROR: Invalid output file");
							}
						}

					} else if (fileSaver.getFileFilter().equals(csvFilter)) {
						outputFileCSV = new File(fileSaver.getSelectedFile() + ".csv");
						if (outputFileCSV != null && outputFileCSV.exists()) {
							String msg = "The file " + outputFileCSV.getName() + " already exists. Do you want to replace the existing file?";
							String title = "Ovewrite file?";
							int userRespond2 = JOptionPane.showConfirmDialog(Main.this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

							if (userRespond2 != JOptionPane.YES_OPTION)	// user choose NO
								return; // stop here
						}

						String path = fileSaver.getSelectedFile().toString();
						if (!path.endsWith(".csv")) {
							path += ".csv";

							try { // try to write text to file writer
								fileWriter = new FileWriter(path, false);
								bufferedWriter = new BufferedWriter(fileWriter);
								bufferedWriter.write(postCSVResult.toString ());

								bufferedWriter.close();
								fileWriter.close();

								printToProgress("Result has been saved: " + outputFileCSV + "\n");
								printToLog("*Result has been saved: " + outputFileCSV + "\n");
							} catch (IOException e) {
								JOptionPane.showMessageDialog(Main.this, "ERROR: Invalid output file.");
							}
						}
					}
				} else		// user cancel save
					return; // stop here
			}
		}
	}

	/**
	 * listens for user interaction with RUN and CANCEL button
	 */
	private class MySearchTaskListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == JBRun) {				// RUN BUTTON
				boolean noneSelected = true;
				for (Component comp : HMComponents.values ()) {        //iterate over all search elements and check
					if (comp.TYPE == 'T' && !comp.text.getText().isEmpty()) {   //if any is of them selected
						//validate and add User Text to regex list in HMComponents
						List<Pattern> temp = buildTextRegexList(HMComponents.get("TextSearchArea").text.getText());
						if(temp == null) {
							// temp is null only if buildTextRegexList() had a regex error
							// buildTextRegexList() should display error
							// need to quit due to invalid text

							return; // stop here
						}else {
							if(temp.isEmpty() ) {
								//  buildTextRegexList() returns empty array if only whitespace was entered in box.
								JOptionPane.showMessageDialog(Main.this, "ERROR: Please enter non-whitespace characters in Text Search Area or use 'Clear' button.");
								return;
							}else {
								// user entered a valid pattern/text
								HMComponents.get("TextSearchArea").regex = temp;
							}
						}
						noneSelected = false;
					}
					else if (comp.TYPE == 'C' && comp.checkBox.isSelected()) {
						noneSelected = false;
					}
				}//end iteration

				if (noneSelected) {	// check if no match mode is selected, show an error and stop
					JOptionPane.showMessageDialog(Main.this, "ERROR: No match mode is selected");
					return; // stop here
				}

				if (userInput == null) {		// check if there is an input file/directory
					JOptionPane.showMessageDialog(Main.this, "ERROR: No input file/directory");
					return; // stop here
				}

				initNewSearch();
				searchTask.execute();
				JBRun.setEnabled(false);
				JBCancel.setEnabled(true);

				JPMain.remove(row3);
				JPMain.add(row4);
				JPMain.add(row5);
				JPMain.validate();
				JPMain.repaint();
			} else if (event.getSource() == JBCancel) {		// CANCEL BUTTON
				searchTask.cancel(true);
				//System.exit(0);
			} else if (event.getSource() == ClearButton) {
				HMComponents.get("TextSearchArea").text.setText("");
			}
		}
	}

	/* *******************************************************************************************************************
	 *											The Search Task Section													*
	 ********************************************************************************************************************/
	private class SearchTask extends SwingWorker<Void, String> {
		/**
		 * This method takes a given directory and find SSNs for all the files reachable from that directory.
		 * @param dir - directory that need to be processed
		 */
		public void runSearch(File dir) {
			List <File> inputFiles = new ArrayList<>();		// build list of input files

			if (fileChooser.getFileSelectionMode() == JFileChooser.FILES_ONLY)	// if a FILE
				inputFiles.add(dir);	// add that file to list
			else if (fileChooser.getFileSelectionMode() == JFileChooser.DIRECTORIES_ONLY)	// if a DIRECTORY
				inputFiles = (List <File>) FileUtils.listFiles(dir, null, true);	// parse all in dir and sub dirs
			else
				return;

			totalFiles += inputFiles.size();	// update counter
			JPBStatus.setMaximum (totalFiles);	//sets progress bar maximum to relative num of files to process

			//buildTextRegexList(); //Done in JBRun action listener
			for (File file: inputFiles) {		// process file by file
				InputStream input;
				ContentHandler handler;

				try {
					input = new FileInputStream(file);
					handler = new BodyContentHandler(-1);
					String fileName = file.getName();
					String fileExtension = "";
					int i = fileName.lastIndexOf(".");
					//System.out.println("file is " + fileName);

					if  (i > 0)
						fileExtension = fileName.substring(i + 1);
					
					if (skipExtensions.contains(fileExtension.toLowerCase())) //skip any files that's in the skip extensions list
						continue;

					if (file.length() <= 0)	{	//skip zero byte file and add it to list
						skipFiles.add(file);
						continue;
					}

					if (fileExtension.equals("txt") || fileExtension.equals("csv")) { //explicitly add csv files to native Java
						fileReader = new Scanner(input);	//for txt files we will let java read them natively instead of Tika parser
					} else if (fileExtension.equals("msg")) {
						MAPIMessage msg = new MAPIMessage(file.getAbsolutePath());
						fileReader = new Scanner(msg.getTextBody());
					} else if ((fileExtension.equals("htm"))||(fileExtension.equals("html"))) {
						HtmlParser htmlParser = new HtmlParser();
						htmlParser.parse(input, handler, new Metadata(), new ParseContext());
						fileReader = new Scanner(handler.toString());
					} else if (fileExtension.equals("rtf")) {
						RTFParser rtfParser = new RTFParser();
						rtfParser.parse(input, handler, new Metadata(), new ParseContext());
						fileReader = new Scanner(handler.toString());
					} else if (fileExtension.equals("mbox")) {
						MboxParser mboxParser = new MboxParser();
						mboxParser.parse(input, handler, new Metadata(), new ParseContext());
						fileReader = new Scanner(handler.toString());
					} else if (fileExtension.equals("pst")) {
						OutlookPSTParser OutlookPSTParser = new OutlookPSTParser();
						OutlookPSTParser.parse(input, handler, new Metadata(), new ParseContext());
						fileReader = new Scanner(handler.toString());
					} else if (fileExtension.equals("mdb")) {
						JackcessParser jackcessParser = new JackcessParser();
						jackcessParser.parse(input, handler, new Metadata(), new ParseContext());
						fileReader = new Scanner(handler.toString());
					} else if (fileExtension.equals("pdf")) {
						PDFParser pdfParser = new PDFParser();
						pdfParser.parse(input, handler, new Metadata(), new ParseContext());
						fileReader = new Scanner(handler.toString());
					} else if (fileExtension.equals("odt")) {
						OpenDocumentParser openDocumentParser = new OpenDocumentParser();
						openDocumentParser.parse(input, handler, new Metadata(), new ParseContext());
						fileReader = new Scanner(handler.toString());
					} else if (fileExtension.equals("doc")) {
						Parser parser = new AutoDetectParser(new DefaultDetector());
						parser.parse(input, handler, new Metadata(), new ParseContext());
						fileReader = new Scanner(handler.toString());
					} else if (fileExtension.equals("docx")) {
						OPCPackage opcpkg = OPCPackage.open(file);
						XWPFDocument docx = new XWPFDocument(opcpkg);
						XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
						fileReader = new Scanner(extractor.getText());
						opcpkg.close();
					} else if (fileExtension.equals("xlsx")) {
						OPCPackage opcpkg = OPCPackage.open(file);
						XSSFWorkbook wb = new XSSFWorkbook(opcpkg);
						XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
						extractor.setFormulasNotResults(true);
						extractor.setIncludeSheetNames(false);
						fileReader = new Scanner(extractor.getText());
						opcpkg.close();
					} else if (fileExtension.equals("xls")) {
						NPOIFSFileSystem npoifs = new NPOIFSFileSystem(file);
						HSSFWorkbook wb = new HSSFWorkbook(npoifs.getRoot(), false);
						ExcelExtractor extractor = new ExcelExtractor(wb);
						extractor.setFormulasNotResults(true);
						extractor.setIncludeSheetNames(false);
						fileReader = new Scanner(extractor.getText());
						npoifs.close();
					} else if (fileExtension.isEmpty()) {
						AutoDetectParser parser = new AutoDetectParser();
						parser.parse(input, handler, new Metadata(), new ParseContext());
						fileReader = new Scanner(handler.toString());
					} else {
						if (JCBAutoParser.isSelected()) {
							AutoDetectParser parser = new AutoDetectParser();
							parser.parse(input, handler, new Metadata(), new ParseContext());
							fileReader = new Scanner(handler.toString());
						} else { //files added here contains extensions not supported by grit and "Read Additional Format" was not selected
							skipFiles.add(file);
							continue;
						}
					}

					matchRegex(file, fileExtension);	// find matching regex in current processing file
					input.close();	//here we close the fileInputStream using the handler reference, to avoid memory leaks!
				} catch (NullPointerException e) {
					System.out.println("NULLPE " + e);
					skipFiles.add(file);
				} catch (OutOfMemoryError e) {
					System.out.println("OOME " + e);
					skipFiles.add(file);
				} catch (ZipException e) {
					System.out.println("ZipE " + e);
					skipFiles.add(file);
				} catch (EOFException e) {
					System.out.println("EOF " + e);
					skipFiles.add(file);
				} catch (FileNotFoundException e) {
					System.out.println("FNFE " + e);
					skipFiles.add(file);
				} catch (ChunkNotFoundException e) {
					System.out.println("CNFE " + e);
					skipFiles.add(file);
				} catch (InvalidFormatException e) {
					System.out.println("IFE " + e);
					skipFiles.add(file);
				} catch (IOException e) {
					System.out.println("IO " + e);
					skipFiles.add(file);
				} catch (TikaException e) {
					System.out.println("TIKA " + e);
					skipFiles.add(file);
				} catch (SAXException e) {
					System.out.println("SAX " + e);
					skipFiles.add(file);
				} catch (ConcurrentModificationException e) {
					System.out.println ("ConcurrentModificationException: " + e);
				}

				JPBStatus.setValue(++progressCounter);	// update progress bar for many files search, directory search
			}
		}

		/**
		 * This method does the regular expression matching.
		 * Results will be output to the GUI and save in HTML format.
		 * @param file - file that will be processed
		 *
		 * Note! redundant issue where previous line is read twice. find ways to fix!
		 *
		 * Note! issue of double counting when match is found between lines. previously added current line gets counted
		 * again when it becomes the previous of the current iteration, where there is match found in between the
		 * jointed lines. it look like one line look ahead is insufficient, perhaps implement two lines look ahead
		 *
		 * Note! we look ahead one line to check if match is found in between lines, if a match is found in between lines then
		 * we get match results of the combined current line and next line. but if no match is found in between, then we just get
		 * match result from current line and sets the next line to current line and repeat the look ahead process. this also
		 * results in the current line unable to reach the last line in file because last line was already read by next line and
		 * the loop couldn't come around to set next line to current line. a oneExtraRun is implemented to allow the loop to run
		 * one extra time to set next line to current line for normal matching function.
		 *
		 * Note! this algorithm considers higher precedence for matches found in between line than matches found on current line,
		 * since matches found in between lines also contains matches on current line. thus this should avoid double counting
		 * of current line matches when a between match occurs.
		 */
		private void matchRegex(File file, String fileExtension) {
			int lineNum = 1;		// init line counter
			int oneExtraRun = 1;	//causes the line reader to run one extra time when last line in file is reached so that
			//next line can be reassign to current line for matching on current line

			StringBuilder currLine = new StringBuilder ();	//between the end of the previous line and start at the beginning of the next line
			StringBuilder nextLine = new StringBuilder ();	//look ahead line check if match is found between joined line to avoid double counting
			StringBuilder combLine = new StringBuilder ();	//this is the combine line of the previous line and the current line

			JPBStatus2.setMaximum (countLines (file));	//sets progress bar max to relative num of lines in file
			JPBStatus2.setValue (0);	// reset line progress bar
			progressCounter2 = 0;

			//removed, this calls numerous times and duplicates entries in the regex list
			//causing longer search times.
			//addTextToRegex(HMComponents.get("TextSearchArea").text.getText()); //<<< possible redundancy >>> adding the same user input regex to list on each file searched

			if (fileReader.hasNext()) {			// check if file is readable
				++readCounter;
				extCounter.count(fileExtension);
			} else { //if file is not readable, add it to skipList
				skipFiles.add(file);
				fileReader.close();
				return;
			}

			//current line fetches a new line from file only once, for every other time it get its line from the next line string
			Main.this.setString (currLine, new StringBuilder (fileReader.nextLine ())); //get new line from file and set to current line

			while (fileReader.hasNext() || oneExtraRun-- > 0) { //walk over each line in file
				if (Thread.currentThread().isInterrupted())	// handle interrupted (cancel button)
					return;

				if (fileReader.hasNext())
					Main.this.setString (nextLine, new StringBuilder (fileReader.nextLine ()));	//get the next line if it exist
				else
					nextLine = new StringBuilder ();	//if no more line to read, set empty string to next line

				Main.this.setString (combLine, currLine, new StringBuilder (" "), nextLine); //combine current line with previous line into single line

				for (Component comp : HMComponents.values ()) { //for each line check whether each active regex search component contains a match

					if (comp.isActive ()) {
						//iterate through arrayList 'regex' in Component class.
						if(comp.TYPE == 'T') {
							System.out.println("textbox");
						}
						for (Pattern regex : comp.regex) {
							int crrMchCnt, nxtMchCnt, cmbMchCnt;

							Matcher crrMchr = regex.matcher (currLine.toString ());	//these three blocks counts number of matches found on particular line
							crrMchCnt = getMatchCount (crrMchr);					//it is needed to determine if a match occur in between line endings

							Matcher nxtMchr = regex.matcher (nextLine.toString ());
							nxtMchCnt = getMatchCount (nxtMchr);

							Matcher cmbMchr = regex.matcher (combLine.toString ());
							cmbMchCnt = getMatchCount (cmbMchr);

							if (cmbMchCnt > crrMchCnt + nxtMchCnt) {	//if there is a match in between lines, we get result from the combine line
								while (cmbMchr.find ())
									doResult (comp, combLine, cmbMchr, fileExtension, file, lineNum);
								break;	//if a match is found on this line, no need to check remaining regex pattern in list, avoid duplicates match result, this break out of the regex for loop above
							} else if (crrMchCnt > 0) {	//if no match is found in between line, then just get results from the current line
								while (crrMchr.find ())
									doResult (comp, currLine, crrMchr, fileExtension, file, lineNum);
								break;	//if a match is found on this line, no need to check remaining regex pattern in list, avoid duplicates match result, this break out of the regex for loop above
							}
						}
					}
				}
				Main.this.setString (currLine, nextLine); //set next line to current line
				JPBStatus2.setValue(++progressCounter2);	// update progress bar for single file search, count lines
				++lineNum;
			}

			fileReader.close();				// tidy up and update progress
			publish("printCurrentProgress");
			fileCounter ++;
		}

		/**
		 * This method is only used by the user entered regex (Text) and the SSN search
		 * This method is only called from the done() method
		 */
		private ArrayList <Match> getResults(Component comp) {
			int i = 1;
			for (Match pr : comp.resultList)
				Main.this.addToAllRow (false, true, i++, pr, comp.html, comp.csv);

			comp.counter = comp.resultList.size();
			return comp.resultList;
		}

		private ArrayList getOtherResults(ArrayList<Match> elf) {
			for (Match pr : resultOtherMatchList)
				for (Component comp : HMComponents.values ())	//iterate over the hashTable to match every symbols
					if(pr.getConfidence().matches(comp.SYM))
						Main.this.addToAllRow (false, false, 0, pr, comp.html, comp.csv);

			return resultOtherMatchList;
		}

		/**
		 * This method is only used by the user entered regex (Text) and the SSN search
		 * This method is only called from the CleanResultsListener listener to remove duplicates
		 */
		private ArrayList cleanResults(Component comp) {
			HashSet <Match> uniqList = comp.resultListUnique;		// get reference handler to resultListUnique
			ArrayList <Match> fnlList = comp.resultListUniqueFinal;	// get reference handler to resultListUniqueFinal

			for(Match pr : uniqList)	//move contents from unique list to final list
				fnlList.add(pr);

			Collections.sort(fnlList, new Comparator <Match> () {
				@Override
				public int compare(Match z1, Match z2) {
					if (z1.getID() > z2.getID()) { return 1; }
					if (z1.getID() < z2.getID()) { return -1; }
					return 0;
				}
			});

			int i = 1;
			for (Match pr : fnlList)
				Main.this.addToAllRow (true, true, i++, pr, comp.html, comp.csv);

			comp.counter = fnlList.size ();
			return fnlList;
		}

		private void getConfidenceTable() {
			JBTCatModel.setRowCount(0);

			for (Component comp : HMComponents.values ())
				JBTCatModel.addRow(new Object[]{comp.LABEL, comp.counter});

			JBTCatModel.addRow(new Object[]{"Total Matches", HMComponents.get ("TextSearchArea").counter + HMComponents.get ("SSN").counter + matchCounter});
		}

		private void getExtensionTable() {
			for (String s : extCounter.extList) {
				int i = extCounter.extList.indexOf(s);
				int c = extCounter.extCount.get(i);
				JBTFileExtModel.addRow(new Object [] {s, c});
			}
		}

		private void buildCSVResult() {
			postCSVResult.append (csvWriter.addTableHeader());
			for (Component comp : HMComponents.values ())
				if (comp.isActive ())
					postCSVResult.append (comp.csv.toString ());
		}

		/* *
		 * This method prepares search results in html format which can be saved later.
		 */
		private void buildHtmlResult() {
			postHtmlResult.append (htmlWriter.addOpenHTMLTag());
			postHtmlResult.append (htmlWriter.addStyleSection());

			postHtmlResult.append (htmlWriter.addAnchorTopLink("top", "National Archives and Records Administration"));
			postHtmlResult.append (htmlWriter.addResultTitle(startSearch));

			postHtmlResult.append (htmlWriter.addOpenCenterTag());
			postHtmlResult.append (htmlWriter.addOpenNavTag());
			postHtmlResult.append (htmlWriter.addOpenNavULTag());

			for (Component comp : HMComponents.values ()) {
				if (comp.isActive ()) {
					StringBuilder link = new StringBuilder (comp.SYM.replaceAll ("\\s+", ""));
					StringBuilder lnkLabel = new StringBuilder (comp.SYM + " Matches");
					Main.this.buildHTMLNav (comp.counter, link, lnkLabel);
				}
			}

			postHtmlResult.append (htmlWriter.addCloseNavULTag());
			postHtmlResult.append (htmlWriter.addCloseNavTag());
			postHtmlResult.append (htmlWriter.addCloseCenterTag());

			postHtmlResult.append (htmlWriter.addOpenCenterTag());
			postHtmlResult.append (htmlWriter.addOpenNavTag());
			postHtmlResult.append (htmlWriter.addOpenNavULTag());	// ********* !! possible bug !! why line below only considers ssnCounter and textCounter? **********
			postHtmlResult.append (htmlWriter.addResultNote(skipFiles.size(), readCounter, totalFiles, HMComponents.get ("TextSearchArea").counter + HMComponents.get ("SSN").counter + matchCounter, calculateElapsedTime()));
			postHtmlResult.append (htmlWriter.addExtNote(extCounter));
			postHtmlResult.append (htmlWriter.addCloseNavULTag());
			postHtmlResult.append (htmlWriter.addCloseNavTag());
			postHtmlResult.append (htmlWriter.addCloseCenterTag());

			for (Component comp : HMComponents.values ()) {
				if (comp.isActive ()) {
					StringBuilder link = new StringBuilder (comp.SYM.replaceAll ("\\s+", ""));
					StringBuilder lnkLabel = new StringBuilder (comp.SYM + " Found Results");
					StringBuilder tableTagId = new StringBuilder (comp.SYM.replaceAll ("\\s+", "") + "ResultTable");
					StringBuilder html = new StringBuilder (comp.html.toString ());
					Main.this.buildHTMLPanel (link, lnkLabel, tableTagId, html);
				}
			}

			if(skipFiles.size() > 0) {
				postHtmlResult.append (htmlWriter.addOpenPanelTag());
				postHtmlResult.append (htmlWriter.addAnchorLink("skippedResults", "This program does not search the following file formats: " +
						"mp3, mp4, ogg, flac, png, gif, bmp, jpg, jpeg, avi, mpg, mpeg, tar, zip, tz, gz, tif, tiff.<br>Additionally, the following files could not be read:"));
				postHtmlResult.append (htmlWriter.addOpenTableTag("unreadFilesTable"));
				postHtmlResult.append (htmlWriter.addAltTableHeader());

				for (File f : skipFiles)
					postHtmlResult.append (htmlWriter.addAltTableRow(f.toString()));

				postHtmlResult.append (htmlWriter.addCloseTableTag());
				postHtmlResult.append (htmlWriter.addBackToTopLink("top", "Back to Top"));
				postHtmlResult.append (htmlWriter.addClosePanelTag());
			}

			postHtmlResult.append (htmlWriter.addCloseHTMLTag());
		}

		@Override
		protected Void doInBackground() throws Exception {
			startSearch = new Date();
			JPBStatus.setValue(0);
			JPBStatus2.setVisible(true);
			JPBStatus.setVisible(true);
			runSearch(userInput);
			return null;
		}

		@Override
		protected void process(List<String> msgList) {
			if (isCancelled())
				return;

			for (String msg : msgList)
				if (msg.equals("printCurrentProgress"))
					printToProgress("Completed " + fileCounter + " / " + totalFiles + " files." + " Results: " + (HMComponents.get ("TextSearchArea").counter + HMComponents.get ("SSN").counter + matchCounter) );
				else
					printToLog(msg);
		}

		@Override
		protected void done() {
			//System.out.println(skipFiles.toString());			//<=========== for debug

			Toolkit.getDefaultToolkit().beep();		// notify
			JPBStatus.setVisible(false);
			JPBStatus2.setVisible(false);

			JBTableModel.setRowCount(0);	//<========= for debug, remove later ! this line removes live search result from table and display result stored from list
			getResults(HMComponents.get ("TextSearchArea"));		// update
			getResults(HMComponents.get ("SSN"));
			getOtherResults(resultOtherMatchList);
			getExtensionTable();
			getConfidenceTable();

			endSearch = new Date();
			//JBRemoveDuplicates.setEnabled(true);
			//JBRemoveDuplicates.setText("Remove Duplicates");
			JBRun.setEnabled(true);
			JBCancel.setEnabled(false);
			JBTable.setAutoCreateRowSorter(true);
			JBTFileExt.setAutoCreateRowSorter(true);
			JBTCat.setAutoCreateRowSorter(true);

			// build result messages
			StringBuilder msg = new StringBuilder ("*Readable: " + readCounter + " files / " + totalFiles + " files.\n" +
					"*Found: " + (HMComponents.get ("TextSearchArea").counter + HMComponents.get ("SSN").counter + matchCounter) + " matches.\n" +
					"*Elapsed Time: " + calculateElapsedTime() + "\n");

			if (isCancelled()) {
				String title = "Search is cancelled\n";
				printToProgress(title);
				printToLog("*" + title);
				printToLog(msg.toString ());
				JOptionPane.showMessageDialog(Main.this, msg.toString (), title, JOptionPane.INFORMATION_MESSAGE);
			} else if (isDone()) {
				String title = "Search is done\n";
				printToProgress(title);
				printToLog("*" + title);
				printToLog(msg.toString ());
				JOptionPane.showMessageDialog(Main.this, msg.toString (), title, JOptionPane.INFORMATION_MESSAGE);
			}

			// prepare result in html format and csv format
			buildHtmlResult();
			buildCSVResult();

			// enable save after html result has been prepared
			JBExport.setEnabled(true);


		}
	}


/* ******************************************************************************************************************
 Miscellaneous Helper Method and Classes Section									*
 */


	/**
	 * this method is use to determine the number of lines in a file, it is initially created as helper
	 * method for the progress bar when searching through a single large file. a precise max value is needed
	 * initialize the progress bar to count the progress accurately.
	 * !note: since we don't know how many lines in is a large file and counting through each one is inefficient,
	 * we skip the largest number of lines possible and the lineNumberReader will tell us how many lines it
	 * actually has, this saves computational time in counting through all the lines in between.
	 */
	public int countLines(File file) {
		int lines = 0;

		try {
			LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
			lineNumberReader.skip(Long.MAX_VALUE);	// skip the largest number to get the last line in the file
			lines = lineNumberReader.getLineNumber();
			lineNumberReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException Occurred: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException Occurred: " + e.getMessage());
		}

		return lines + 1;	// compensate for last line since it count \n and last line doesn't have \n
	}

	/**
	 * This subroutine method is use for writing found regex matches to result list
	 *
	 * Note! originally TextField and SSN results are added to List and ListUnique linkedList, for everything else
	 * gets added to otherMatchList. be mindful of how these two fields are treated differently than others matches
	 */
	private void doResult (Component comp, StringBuilder line, Matcher patternMatcher, String fileExt, File file, int lineNum) {
		comp.counter ++;

		//enable this to see live result updates while searching, but disable the same one in addAllToRow() method to avoid showing duplicate results
		JBTableModel.addRow(new Object[]{comp.counter, comp.SYM, patternMatcher.group(), line.toString(), fileExt, file, lineNum});

		//NOTE: switched from comp.SYM == "SSN" to line below
		if (comp.SYM.equals("SSN") || comp.SYM.equals("Text")) {
			comp.resultList.add(new Match(comp.counter, comp.SYM, patternMatcher.group(), line.toString(), fileExt, file, lineNum));
			comp.resultListUnique.add(new Match(comp.counter, comp.SYM, patternMatcher.group(), line.toString(), fileExt, file, lineNum));
		} else {
			matchCounter ++; // use for other matches only, not for ssn and textField
			resultOtherMatchList.add(new Match(comp.counter, comp.SYM, patternMatcher.group(), line.toString(), fileExt, file, lineNum));
		}
	}

	/**
	 * This subroutine method can be called from getOtherResults, cleanResults, and getResults.
	 * this method is created to reduce code redundancy. This method can act as either getter or
	 * setter, which is determine by the setIdSwitch parameter. if setIdSwitch is enable, then index i argument can be
	 * used.
	 * @param addToTableModel - switch to add a new row to JBTableModel
	 * @param setIdSwitch - switch on for pr.setID(), off for pr.getID()
	 * @param i - use for pr.setID() when setIdSwitch is on
	 */
	private void addToAllRow (boolean addToTableModel, boolean setIdSwitch, int i, Match pr, StringBuilder html, StringBuilder csv) {
		//if (addToTableModel) {
		JBTableModel.addRow (new Object[] {setIdSwitch ? pr.setID(i) : pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() });
		//}

		html.append (htmlWriter.addTableRow (setIdSwitch ? pr.setID(i) : pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum()));
		csv.append (csvWriter.addTableRow (setIdSwitch ? pr.setID(i) : pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum()));
	}

	/**
	 * This subroutine method is used for the buildHtmlResult inside the SearchTask class
	 */
	private void buildHTMLNav (int val, StringBuilder lnk, StringBuilder lnkLabel) {
		postHtmlResult.append (htmlWriter.addOpenNavLITag());
		postHtmlResult.append (htmlWriter.addCounter(val));
		postHtmlResult.append (htmlWriter.addTextLink(lnk.toString (), lnkLabel.toString ()));
		postHtmlResult.append (htmlWriter.addCloseNavLITag());
	}

	/**
	 * This subroutine method is used for the buildHtmlResult inside the SearchTask class
	 */
	private void buildHTMLPanel (StringBuilder link, StringBuilder lnkLabel, StringBuilder tblTagId, StringBuilder html) {
		postHtmlResult.append (htmlWriter.addOpenPanelTag());
		postHtmlResult.append (htmlWriter.addAnchorLink(link.toString (), lnkLabel.toString ()));
		postHtmlResult.append (htmlWriter.addOpenTableTag(tblTagId.toString ()));
		postHtmlResult.append (htmlWriter.addTableHeader());
		postHtmlResult.append (html.toString ());
		postHtmlResult.append (htmlWriter.addCloseTableTag());
		postHtmlResult.append (htmlWriter.addBackToTopLink("top", "Back to Top"));
		postHtmlResult.append (htmlWriter.addClosePanelTag());
	}

	/**
	 * This method takes a given regex in its string form, compiles it to pattern, and adds to a given pattern list.
	 * @param regex - regex in string form
	 * @param regexList - pattern list where regex will be added to
	 */
	private void addRegexToList(String regex, List <Pattern> regexList) {
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		regexList.add(pattern);
	}


	/** buildTextRegexList()
	 *
	 * This function processes input text from User Search Area.  If a pattern can not be compiled
	 * a pop up error message is displayed showing the error.
	 *
	 * If "Regex" is selected, the string is processed as one entry
	 * If "Plain Text" is selected, the string is tokenized by commas ',' and a list is returned
	 * If "Wild Card" is selected, the list is tokenized by commas, and the ? and * are replaced with \w and \w+
	 *
	 * Upon error, a NULL pointer is returned.  Upon success a list of Regex patterns is returned.
	 *
	 * @param input - Text to convert to a regex search pattern
	 * @return - List of regex search patterns
	 */
	private List<Pattern> buildTextRegexList(String input) {
		List<Pattern> result = new ArrayList<>();
		Pattern pattern = null;
		String[] tempText = input.split(","); //split text entry on commas
		int type = -1;
		input.trim();
		//try to parse string into a List
		try {
			if (regexButton.isSelected()) {
				type = 0;
				pattern = Pattern.compile(input);
				result.add(pattern);
			}
			else if (wildcardButton.isSelected()) {
				type = 1;
			}else if(plainTextButton.isSelected()) {
				type = 2;
			}else {
				System.out.println("fatal logic error @addTextToRegex()\nButtonSelected = " + type + " \tInput:\n" + input);
			}//end radio button check

			//begin parsing Plain & Wilcard types
			if(type != 0) {

				for (int i = 0; i < tempText.length; i++) {
					//check for empty indexes
					tempText[i] = tempText[i].trim();
					if (!tempText[i].matches("")) {

						if (type == 1) {
							String temp = tempText[i];
							temp = temp.replaceAll("\\?", "\\\\w");
							temp = temp.replaceAll("\\*", "\\\\w+");
							pattern = Pattern.compile(temp);
						} else if (type == 2) {
							pattern = Pattern.compile(tempText[i], Pattern.LITERAL);
						} else {
							System.out.println("logic error, @addTextToRegex - parsing tempText\nType: " + type);
						}
						result.add(pattern);

					}//end if empty
				}//end tempText iteration
			}//end if type != 0
		}catch (PatternSyntaxException e){
			JOptionPane.showMessageDialog(Main.this, "ERROR in Search Pattern\n" + e.getDescription());
			result = null;
		}
		return result;
	}
	/** addTextToRegex()
	 *
	 * This method is used for handling user input regex. parses user regex input into pattern
	 * and adds it to the regexText list. the regexText list is cleared on every new search
	 *
	 * @param input - the text to add to regex list
	 */
	private void addTextToRegex(String input) {
		//removed because this code was

	}

	/**
	 * This method resets all strings used for exports.
	 */
	private void initNewExport() {
		for (Component comp : HMComponents.values ())
			comp.clrExport ();

		postCSVResult = new StringBuilder ();
		postHtmlResult = new StringBuilder ();
	}

	/**
	 * This method resets all system components that is used for search.
	 */
	private void initNewSearch() {
		//JTAResultLog.setText("*Input: " + userInput + "\n");
		JBTableModel.setNumRows(0);
		JBTFileExtModel.setNumRows(0);
		JBTCatModel.setRowCount(0);
		skipFiles.clear();
		resultOtherMatchList.clear();
		searchTask = new SearchTask();
		extCounter = new ExtensionCounter();
		startSearch = null;
		endSearch = null;
		totalFiles = 0;
		fileCounter = 0;
		readCounter = 0;
		matchCounter = 0;
		progressCounter = 0;
		progressCounter2 = 0;

		for (Component comp : HMComponents.values ())
			comp.initValues ();

		initNewExport();
	}

	/**
	 * This method calculates the elapsed time of a search.
	 * @return elapsedTime - string presentation of elapsed time.
	 */
	private String calculateElapsedTime() {
		long start = startSearch.getTime();
		long end = endSearch.getTime();
		long diff = end - start;

		// time unit in millisecond
		final long SECOND_UNIT = 1000;
		final long MINUTE_UNIT = SECOND_UNIT * 60;
		final long HOUR_UNIT = MINUTE_UNIT * 60;
		//final long DAYUNIT = HOUR_UNIT * 24;

		// calculate elapsed time
		//String days = String.valueOf(diff / DAYUNIT);
		//diff = diff % DAYUNIT;
		String hours = String.valueOf(diff / HOUR_UNIT);
		diff = diff % HOUR_UNIT;
		String minutes = String.valueOf(diff / MINUTE_UNIT);
		diff = diff % MINUTE_UNIT;
		String seconds = String.valueOf(diff / SECOND_UNIT);
		//String elapsedTime = hours + "h:" + minutes + "m:" + seconds + "s";

		return hours + "h:" + minutes + "m:" + seconds + "s";
	}

	/**
	 * This method prints a given message to the progress log.
	 * @param msg - message that need to be displayed.
	 */
	private void printToProgress(String msg) {
		JTAProgressLog.setText(msg.trim());
	}

	/**
	 * This method prints a given message to the result log.
	 * @param msg - message that need to be displayed.
	 */
	private void printToLog(String msg) {
		//JTAResultLog.append(msg);
		//JTAResultLog.setCaretPosition(JTAResultLog.getDocument().getLength());
	}

	/**
	 * this method clears a targeted handler stringBuilder object internal array buffer
	 * Note! the first vararg element is the handler string, all later elements is the concatenating string
	 */
	private void setString (StringBuilder... args) {
		if (args.length == 0)
			return; //if no arguments is passed in, do nothing

		boolean firstElem = true; //switch used to clear the handler string, the first element in the varargs
		StringBuilder tempStr = null; //use to temporary hold the handler string to perform concatenation

		for (StringBuilder arg : args)
			if (firstElem) {
				tempStr = arg; //get the handler string and clears it for concatenation
				tempStr.setLength (0);
				tempStr.trimToSize (); //trim the internal StringBuilder buffer array
				firstElem = false;
			} else
				tempStr.append (arg);
	}

	/**
	 * this method counts the number of matches found on a string
	 */
	private int getMatchCount (Matcher matcher) {
		int count = 0;
		while (matcher.find ())
			++count;
		matcher.reset ();
		return count;
	}

	/**
	 * This is the main function that run this program/main class.
	 */
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Main().setVisible(true);
			}
		});
	}
}