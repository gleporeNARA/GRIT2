package grit;

import javax.swing.*;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.DefaultTableModel;

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
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;
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
 * be modified with persistency, thus, specific return type method would not be feasable for this purpose. to achieve data
 * persistency passed to subroutine, immutable data such as stings and integers are wrapped in class objects and pass
 * as referenceto void return type subroutine for handling.
 */

public class Main extends JFrame {	
	public static final String PROGRAM_TITLE = "GRIT";
	public static final String PROGRAM_VERSION = "0.0.4a";
	public static final int WIN_WIDTH = 1200;
	public static final int WIN_HEIGHT = 950;

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
	/*
	private int textCounter;	//## remove ##
	private int ssnCounter;	//## remove ##
	private int dobCounter;	//## remove ##
	private int pobCounter;	//## remove ##
	private int maidenCounter;	//## remove ##
	private int alienCounter;	//## remove ##
	private int grandJuryCounter;	//## remove ##
	private int FBIInfoFileCounter;	//## remove ##
	private int FBISourceCounter;	//## remove ##
	private int FBISourceCodeCounter;	//## remove ##
	*/
	private ExtensionCounter extCounter;
	private Date startSearch;
	private Date endSearch;
	
	/*
	private StringBuilder textHTML;	//## remove ##
	private StringBuilder ssnHTML;	//## remove ##
	private StringBuilder dobHTML;	//## remove ##
	private StringBuilder pobHTML;	//## remove ##
	private StringBuilder maidenHTML;	//## remove ##
	private StringBuilder alienHTML;	//## remove ##
	private StringBuilder grandJuryHTML;	//## remove ##
	private StringBuilder FBIInfoFileHTML;	//## remove ##
	private StringBuilder FBISourceHTML;	//## remove ##
	private StringBuilder FBISourceCodeHTML;	//## remove ##
	*/
	private StringBuilder postHtmlResult;
	
	/*
	private StringBuilder textCSV;	//## remove ##
	private StringBuilder ssnCSV;	//## remove ##
	private StringBuilder dobCSV;	//## remove ##
	private StringBuilder pobCSV;	//## remove ##
	private StringBuilder maidenCSV;	//## remove ##
	private StringBuilder alienCSV;	//## remove ##
	private StringBuilder grandJuryCSV;	//## remove ##
	private StringBuilder FBIInfoFileCSV;	//## remove ##
	private StringBuilder FBISourceCSV;	//## remove ##
	private StringBuilder FBISourceCodeCSV;	//## remove ##
	*/
	private StringBuilder postCSVResult;
	
	/*
	private List<Pattern> regexText;	//## remove ##		// only this list should be clear on each new search
	private List<Pattern> regexSSN;	//## remove ##				//<=========== declaring list of regex patterns for check boxes
	private List<Pattern> regexDoBs;	//## remove ##
	private List<Pattern> regexPoBs;	//## remove ##
	private List<Pattern> regexMaidens;	//## remove ##
	private List<Pattern> regexAliens;	//## remove ##
	private List<Pattern> regexGrandJuries;	//## remove ##
	private List<Pattern> regexFBIInfoFiles;	//## remove ##
	private List<Pattern> regexFBISources;	//## remove ##
	private List<Pattern> regexFBISourceCodes;	//## remove ##
	*/
	
	// GUI COMPONENTS (visible interface)
	private JCheckBox JCBCheckAll;
	/*
	private JTextArea JTField;	//## remove ##			// user input regex. should be clear on every new search
	private JCheckBox JCBSSN;	//## remove ##
	private JCheckBox JCBPoB; 	//## remove ##
	private JCheckBox JCBDoB;	//## remove ##
	private JCheckBox JCBMaiden;	//## remove ##
	private JCheckBox JCBAlien;	//## remove ##
	private JCheckBox JCBGrandJury;	//## remove ##
	private JCheckBox JCBFBISources;	//## remove ##
	private JCheckBox JCBFBIInfoFiles;	//## remove ##
	private JCheckBox JCBFBISourceCodes;	//## remove ##
	*/
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
	
	/*
	private ArrayList<Match> resultTextList;	//## remove ##
	private HashSet<Match> resultTextListUnique;	//## remove ##
	private ArrayList<Match> resultTextListUniqueFinal;	//## remove ##
	private ArrayList<Match> resultSSNList;	//## remove ##
	private HashSet<Match> resultSSNListUnique;	//## remove ##
	private ArrayList<Match> resultSSNListUniqueFinal;	//## remove ##
	*/
	private ArrayList <Match> resultOtherMatchList;
	private ArrayList <File> skipFiles;
	private HashSet <String> skipExtensions;
	private HashMap <String, Component> HMComponents;
	
	//private JButton JBClear;
	
	/**
	 * The Main class constructor
	 */
	public Main() {
		initSystemComponents();
		initGUIComponents();
		//System.exit(0);			//<=============== for debug 
	}

	private void initSystemComponents() {
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
		
		resultOtherMatchList = new ArrayList<Match>();
		
		/**
		 * creates a hash map of search components. 'T' is creates a text box, 'C' creates a check box  
		 */
		HMComponents = new HashMap <String, Component> ();
		HMComponents.put ("TxtField", new Component ('T', "Text", "", "Enter your own regular expression here"));
		HMComponents.put ("SSN", new Component ('C', "SSN", "SSN Match", "Matches (SSN#, SS#, SSN, 555-55-5555). Most likely to match SSNs. Fewest false positives."));
		HMComponents.put ("DoB", new Component ('C', "DoB", "Date of Birth", "(Birth, Born, DOB with a date) Matches terms related to date of birth."));
		HMComponents.put ("Maiden", new Component ('C', "Maiden", "Mother's Maiden Name or Nee", "Matches terms related to maiden names."));
		HMComponents.put ("PoB", new Component ('C', "PoB", "Place of Birth", "(POB, Place of Birth, birth place, birthplace, born in, born at) Matches terms related to place of birth"));
		HMComponents.put ("Alien", new Component ('C', "Alien", "Alien Registration Number", "Matches terms to Alien Registration Numbers."));
		HMComponents.put ("GrandJury", new Component ('C', "Grand Jury", "Grand Jury", "Find all matches term Grand Jury"));
		HMComponents.put ("FBIInfoFile", new Component ('C', "FBI Info File", "FBI Info Files", "FBI information files beginning with numbers beginning on 134, 137, 170"));
		HMComponents.put ("FBISource", new Component ('C', "FBI Source", "FBI Sources", "Find matches for protect identity, informant, psi, si, reliable, confidential"));
		HMComponents.put ("FBISourceCode", new Component ('C', "FBI Source Code", "FBI Source Codes", "AL,AQ,AX,AN,AT,BA,BH,BS,BQ,BU,BT,CE,CG,CI,CV,CO,DL,DN," +
																				   "DE,EP,HN,HO,IP,JN,JK,KC,KX,LV,LR,LA,LS,ME,MM,MI,MP,MO,NK," +
																				   "NH,NO,NR,NY,NF,OC,OM,PH,PX,PG,PD,RH,SC,SL,SU,SA,SD,SF,SJ,SV," +
																				   "SE,SI,TP,WFO,BER,BOG,BON,HON,LON,MAN,MEX,OTT,PAN,PAR,ROM,TOK"));
		
		
		/*
		resultTextList = new ArrayList<Match>();	//## remove ##
		resultTextListUnique = new HashSet<Match>();	//## remove ##
		resultTextListUniqueFinal = new ArrayList<Match>();	//## remove ##
		
		resultSSNList = new ArrayList<Match>();	//## remove ##
		resultSSNListUnique = new HashSet<Match>();	//## remove ##
		resultSSNListUniqueFinal = new ArrayList<Match>();	//## remove ##
		*/
		
		// these lists below used for searches and should not get cleared after each use
		/*
		regexText = new ArrayList<Pattern>();	//## remove ##
		regexSSN = new ArrayList<Pattern>();	//## remove ##
		regexDoBs = new ArrayList<Pattern>();	//## remove ##
		regexPoBs = new ArrayList<Pattern>();	//## remove ##
		regexMaidens = new ArrayList<Pattern>();	//## remove ##
		regexAliens = new ArrayList<Pattern>();	//## remove ##
		regexGrandJuries = new ArrayList<Pattern>();	//## remove ##		//<=========== create regex list for check box match pattern
		regexFBIInfoFiles = new ArrayList<Pattern>();	//## remove ##
		regexFBISources = new ArrayList<Pattern>();	//## remove ##
		regexFBISourceCodes = new ArrayList<Pattern>();	//## remove ##
		*/
		
		//Prepare Skipped Extensions:
		skipExtensions = new HashSet<String>();
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
		
/********************************************************************************************************************
*												Built Regex List													*
********************************************************************************************************************/
		// perfect old format ssn with hyphens, followed by anything other than a number, dash, or slash
		addRegexToList("(\\b(?!000)(?!666)(?:[0-6]\\d{2}|7[0-2][0-9]|73[0-3]|7[5-6][0-9]|77[0-2]))-((?!00)\\d{2})-((?!0000)\\d{4})([^0-9-/]|)", HMComponents.get("SSN").regex);
		// same as above but with a newline in front
		addRegexToList("\\s?^?SSN?\\s?#\\s?[0-9]", HMComponents.get("SSN").regex); //Combined this one with the above regex
		//look for a space, the letters SSN, a possible space, and any number
		addRegexToList("\\sSSN\\s?[0-9]", HMComponents.get("SSN").regex);
		// SSN or SSA plus the letters NO, plus a number within 5 spaces
		addRegexToList(" SSN?A?\\s?No\\s?.{0,5}[0-9]", HMComponents.get("SSN").regex);
		// group of 3, 2, 4 separated by a space, bounded by a word boundary
		addRegexToList("(\\b|^)\\d{3} \\d{2} \\d{4}(\\b|$)", HMComponents.get("SSN").regex);
		// group of 3, 2, 4 separated by a . a / or - bounded by something other than a number, hyphen or slash
		addRegexToList("([^0-9.-/]|^)\\d{3}[./-]\\d{2}[./-]\\d{4}([^0-9-/]|$)", HMComponents.get("SSN").regex);
		
		//"birth" or "born" or "DOB" within 5 words of mm/dd/yy, mm-dd-yy, mm.dd.yy, mm dd yy, mm/dd/yyyy, mm-dd-yyyy ,mm.dd.yyyy ,mm dd yyyy
		addRegexToList("\\b(birth|born|DOB)\\W*(?:\\w*\\W*){1,5}((\\D+|^)(?:(1[0-2]|0?[1-9])([- /.]+)(3[01]|[12][0-9]|0?[1-9])|(3[01]|[12][0-9]|0?[1-9])([- /.]+)(1[0-2]|0?[1-9]))([- /.]+)(?:19|20)?\\d\\d)", HMComponents.get("DoB").regex);
		//"birth" or "born" or "DOB" within 5 words of yyyy/mm/dd, yyyy-mm-dd, yyyy.mm.dd, yyyy mm dd
		addRegexToList("\\b(birth|born|DOB)\\W*(?:\\w*\\W*){1,5}((19|20)\\d\\d([- /.]+)(0[1-9]|1[012])([- /.]+)(0[1-9]|[12][0-9]|3[01]))", HMComponents.get("DoB").regex);
		//"birth" or "born" or "DOB" within 5 words of a month spelled out date, with or without period, allows for 1st, 2nd, 3rd, 4th, etc.
		addRegexToList("\\b(birth|born|DOB)\\W*(?:\\w*\\W*){1,5}((?:Jan\\.?(?:uary)?|Feb\\.?(?:ruary)?|Mar\\.?(?:ch)?|Apr\\.?(?:il)?|May|Jun\\.?(?:e)?|Jul\\.(?:y)?|Aug\\.?(?:ust)?|Sep\\.?(?:t\\.?(?:ember)?)?|Oct\\.?(?:ober)?|Nov\\.?(?:ember)?|Dec\\.?(?:ember)?)[ ][0-3]?\\d(?:st|rd|nd|th)?,?[ ](?:19|20)\\d\\d)", HMComponents.get("DoB").regex);
		//"birth" or "born" or "DOB" within 5 words of a numeric day and a month spelled out (i.e. born on 31 December
		addRegexToList("\\b(birth|born|DOB)\\W*(?:\\w*\\W*){1,5}(0?[1-9]|[12][0-9]|3[01]) (?:Jan\\.?(?:uary)?|Feb\\.?(?:ruary)?|Mar\\.?(?:ch)?|Apr\\.?(?:il)?|May|Jun\\.?(?:e)?|Jul\\.(?:y)?|Aug\\.?(?:ust)?|Sep\\.?(?:t\\.?(?:ember)?)?|Oct\\.?(?:ober)?|Nov\\.?(?:ember)?|Dec\\.?(?:ember)?)", HMComponents.get("DoB").regex);
		//Place of Birth
		addRegexToList("(POB|Place of Birth|birth place|birthplace|born in|born at|bornin|bornat|place ofbirth)", HMComponents.get("PoB").regex); 
		//mother's maiden name or nee
		addRegexToList("(maiden name|mother'?s? maiden name|\\bnee\\s)", HMComponents.get("Maiden").regex);
		//Alien number regex from healthcare.gov
		addRegexToList("(\\b|^)(A|a)(-?[0-9]){9}(\\b|$)|(\\b|^)(A|a)(-?[0-9]){7}(\\b|$)", HMComponents.get("Alien").regex);
		//Grand Jury
		addRegexToList("(Grand Jury)", HMComponents.get("GrandJury").regex);
		//FBI Sources terms for protect identity, informant, psi, si, reliable, confidential
		addRegexToList("(protect identity|informant|psi|si|reliable|confidential)", HMComponents.get("FBISource").regex);
		//Find FBI information files beginning with numbers beginning on 134, 137, 170
		addRegexToList("\\b(134-\\d*|137-\\d*|170-\\d*)\\b", HMComponents.get("FBIInfoFile").regex);
		//FBI source codes
		addRegexToList("\\b(AL|AQ|AX|AN|AT|BA|BH|BS|BQ|BU|BT|CE|CG|CI|CV|CO|DL|DN|DE|EP|HN|HO|IP|JN|JK|KC|KX|LV|LR|LA|LS|ME|MM|MI|MP|MO|NK|NH|NO|NR|NY|NF|OC|OM|PH|PX|PG|PD|RH|SC|SL|SU|SA|SD|SF|SJ|SV|SE|SI|TP|WFO|BER|BOG|BON|HON|LON|MAN|MEX|OTT|PAN|PAR|ROM|TOK)\\s+\\b", HMComponents.get("FBISourceCode").regex);
		
		/*			// old codes
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
		//Place of Birth
		addRegexToList("(POB|Place of Birth|birth place|birthplace|born in|born at|bornin|bornat|place ofbirth)", regexPoBs); 
		//mother's maiden name or nee
		addRegexToList("(maiden name|mother'?s? maiden name|\\bnee\\s)", regexMaidens);
		//Alien number regex from healthcare.gov
		addRegexToList("(\\b|^)(A|a)(-?[0-9]){9}(\\b|$)|(\\b|^)(A|a)(-?[0-9]){7}(\\b|$)", regexAliens);
		//Grand Jury
		addRegexToList("(Grand Jury)",regexGrandJuries);
		//FBI Sources terms for protect identity, informant, psi, si, reliable, confidential
		addRegexToList("(protect identity|informant|psi|si|reliable|confidential)", regexFBISources);
		//Find FBI information files beginning with numbers beginning on 134, 137, 170
		addRegexToList("\\b(134-\\d*|137-\\d*|170-\\d*)\\b", regexFBIInfoFiles);
		//FBI source codes
		addRegexToList("\\b(AL|AQ|AX|AN|AT|BA|BH|BS|BQ|BU|BT|CE|CG|CI|CV|CO|DL|DN|DE|EP|HN|HO|IP|JN|JK|KC|KX|LV|LR|LA|LS|ME|MM|MI|MP|MO|NK|NH|NO|NR|NY|NF|OC|OM|PH|PX|PG|PD|RH|SC|SL|SU|SA|SD|SF|SJ|SV|SE|SI|TP|WFO|BER|BOG|BON|HON|LON|MAN|MEX|OTT|PAN|PAR|ROM|TOK)\\s+\\b", regexFBISourceCodes);
		*/
		
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
		
		//System.exit(0);			//<=============== for debug 
	}
	
	/**
	 * setting up the visual components for the application.
	 * note: this could potentially be the view component for mvc
	 */
	private void initGUIComponents() {
		//Row1: Elements
		JCBCheckAll = new JCheckBox("Check All Options");
		JCBCheckAll.setToolTipText("(All Options Activated)");
		/*
		JTField = new JTextArea("");	//## remove ##
		JTField.setLineWrap(true);	//## remove ##
		JTField.setWrapStyleWord(true);	//## remove ##
		JCBSSN = new JCheckBox("SSN Match"); JCBSSN.setSelected(true);	//## remove ##
		JCBSSN.setToolTipText("Matches (SSN#, SS#, SSN, 555-55-5555). Most likely to match SSNs. Fewest false positives.");	//## remove ##
		JCBDoB = new JCheckBox("Date of Birth");	//## remove ##
		JCBDoB.setToolTipText("(Birth, Born, DOB with a date) Matches terms related to date of birth.");	//## remove ##
		JCBMaiden = new JCheckBox("Mother's Maiden Name or Nee");	//## remove ##
		JCBMaiden.setToolTipText("Matches terms related to maiden names.");	//## remove ##
		JCBPoB = new JCheckBox("Place of Birth");	//## remove ##
		JCBPoB.setToolTipText("(POB, Place of Birth, birth place, birthplace, born in, born at) Matches terms related to place of birth");	//## remove ##
		JCBAlien = new JCheckBox("Alien Registration Number");	//## remove ##
		JCBAlien.setToolTipText("Matches terms to Alien Registration Numbers.");	//## remove ##
		JCBGrandJury = new JCheckBox("Grand Jury");	//## remove ##		//<=========== initializing and defining new checkboxes and tool tip
		JCBGrandJury.setToolTipText("Find all matches term Grand Jury");	//## remove ##
		JCBFBIInfoFiles = new JCheckBox("FBI Info Files");	//## remove ##
		JCBFBIInfoFiles.setToolTipText("FBI information files beginning with numbers beginning on 134, 137, 170");	//## remove ##
		JCBFBISources = new JCheckBox("FBI Sources");	//## remove ##
		JCBFBISources.setToolTipText("Find matches for protect identity, informant, psi, si, reliable, confidential");	//## remove ##
		JCBFBISourceCodes = new JCheckBox("FBI Source Codes");	//## remove ##
		JCBFBISourceCodes.setToolTipText("AL,AQ,AX,AN,AT,BA,BH,BS,BQ,BU,BT,CE,CG,CI,CV,CO,DL,DN," +
										 "DE,EP,HN,HO,IP,JN,JK,KC,KX,LV,LR,LA,LS,ME,MM,MI,MP,MO,NK," +
										 "NH,NO,NR,NY,NF,OC,OM,PH,PX,PG,PD,RH,SC,SL,SU,SA,SD,SF,SJ,SV," +
										 "SE,SI,TP,WFO,BER,BOG,BON,HON,LON,MAN,MEX,OTT,PAN,PAR,ROM,TOK");	//## remove ##
		*/
		
		JCBAutoParser = new JCheckBox("Read Additional Formats");
		JCBAutoParser.setToolTipText("The program will attempt to read additional file formats.");
		
		JRBDirectory = new JRadioButton("One Directory");
		JRBDirectory.setToolTipText("Searches all files under a directory.");
		JRBDirectory.setSelected(true);
		
		JRBFile = new JRadioButton("One File");
		JRBFile.setToolTipText("Single file search");
		
		ButtonGroup BGReadMode = new ButtonGroup();		//adding radio button to group
		BGReadMode.add(JRBDirectory);
		BGReadMode.add(JRBFile);
		
		JBRemoveDuplicates = new JButton("Remove Duplicates");
		JBRemoveDuplicates.setToolTipText("Remove Duplicate Results");
		JBRemoveDuplicates.setEnabled(false);
		
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
			
		//Row2: Elements
		JTAProgressLog = new JTextField("");
		JTAProgressLog.setEditable(false);
		JTAProgressLog.setHorizontalAlignment(JTextField.CENTER);
		JTAProgressLog.setBackground(new Color(250, 250, 241));
		JTAProgressLog.setMargin(new Insets(5, 5, 5, 5));
		JTAProgressLog.setToolTipText("Displays the current number of processed files");
		
		JPBStatus = new JProgressBar(0,100);
		JPBStatus.setValue(0);
		JPBStatus.setStringPainted(false);
		JPBStatus.setIndeterminate(true);
		JPBStatus.setVisible(false);
		JPBStatus.setBackground(Color.black);
		JPBStatus.setForeground(new Color(129,218,245));
	
		JPBStatus.setUI(new BasicProgressBarUI() {
			@Override
			protected Color getSelectionBackground() { 
				return new Color(129,218,245);
			}
			
			@Override
			protected Color getSelectionForeground() { 
				return Color.black;
			}
		});
		
		//Row3: Elements
		JTAResultLog = new JTextArea(getTutorial());
		JTAResultLog.setEditable(false);
		JTAResultLog.setMargin(new Insets(5, 5, 5, 5));
		JTAResultLog.setLineWrap(true);
		JTAResultLog.setWrapStyleWord(true);

		//Row1: Panel1: Elements Added
		JPanel panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createTitledBorder("PII Match Modes"));
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
		panel1.add(JCBCheckAll);
		panel1.add(HMComponents.get("SSN").checkBox);
		panel1.add(HMComponents.get("DoB").checkBox);
		panel1.add(HMComponents.get("Maiden").checkBox);
		panel1.add(HMComponents.get("PoB").checkBox);
		panel1.add(HMComponents.get("Alien").checkBox);
		/*
		panel1.add(JCBSSN);	//** modify **
		panel1.add(JCBDoB);	//** modify **
		panel1.add(JCBMaiden);	//** modify **
		panel1.add(JCBPoB);	//** modify **
		panel1.add(JCBAlien);	//** modify **
		*/
		//Row1: Panel2: Elements Added
		JPanel panel2_sub1 = new JPanel();	//to get proper alignment of new check boxes above "Other Match mode"
		JPanel panel2_sub2 = new JPanel();	//two sub panels are placed inside of panel2 using grid layout
		
		panel2_sub1.setBorder(BorderFactory.createTitledBorder("PII Match Modes"));
		panel2_sub1.setLayout(new BoxLayout(panel2_sub1, BoxLayout.PAGE_AXIS));
		panel2_sub1.add(HMComponents.get("GrandJury").checkBox);
		panel2_sub1.add(HMComponents.get("FBIInfoFile").checkBox);
		panel2_sub1.add(HMComponents.get("FBISource").checkBox);
		panel2_sub1.add(HMComponents.get("FBISourceCode").checkBox);
		/*
		panel2_sub1.add(JCBGrandJury);	//** modify **
		panel2_sub1.add(JCBFBIInfoFiles);	//** modify **
		panel2_sub1.add(JCBFBISources);	//** modify **
		panel2_sub1.add(JCBFBISourceCodes);	//** modify **
		*/
		panel2_sub2.setBorder(BorderFactory.createTitledBorder("Other Match Mode"));
		panel2_sub2.setLayout(new BoxLayout(panel2_sub2, BoxLayout.PAGE_AXIS));
		panel2_sub2.add(HMComponents.get("TxtField").text);
		//panel2_sub2.add(JTField);	//** modify **
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(0,1));
		panel2.add(panel2_sub1);
		panel2.add(panel2_sub2);
		
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
		
		//Row1: Elements Populated
		JPanel row1 = new JPanel();
		row1.setMinimumSize(new Dimension(Integer.MAX_VALUE, 100));
		row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
		row1.setLayout(new GridLayout(0, 4));
		row1.add(panel1);
		row1.add(panel2);
		row1.add(panel3);
		row1.add(panel4);
		
		//Row2: Panel5: Elements Added
		JPanel panel5 = new JPanel();
		panel5.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		panel5.setLayout(new BoxLayout(panel5, BoxLayout.PAGE_AXIS));
		panel5.setMinimumSize(new Dimension(Integer.MAX_VALUE, 15));
		panel5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		panel5.add(JTAProgressLog);
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
		
		JBRemoveDuplicates.addActionListener(new CleanResultsListener());
		
		JBInput.addActionListener(new MyIOListener());
		JBRun.addActionListener(new MySearchTaskListener());
		JBCancel.addActionListener(new MySearchTaskListener());
		JBExport.addActionListener(new MyIOListener());
		pack();

		//System.exit(0);			//<=============== for debug 
	}
	
	/**
	 * the help tutorial method, this is the information 
	 * text that is displayed in the result set window when the app starts up
	 */
	private String getTutorial() {
		return "*** " + PROGRAM_TITLE + " version " + PROGRAM_VERSION + " ***\n\n" + Help.showHelp ();		
	}

/********************************************************************************************************************
*										GUI Action Listeners Class Section											*
********************************************************************************************************************/
	/**
	 * This internal class listens for user's interaction with the remove duplicates button.
	 */
	private class CleanResultsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == JBRemoveDuplicates) {
				initNewExport();
				JBTableModel.setRowCount(0);
				
				searchTask.cleanTextResults(HMComponents.get("TxtField").resultListUnique);	//*** modify ***
				searchTask.cleanSSNResults(HMComponents.get("SSN").resultListUnique);	//*** modify ***
				/*
				searchTask.cleanTextResults(resultTextListUnique);	//*** modify ***
				searchTask.cleanSSNResults(resultSSNListUnique);	//*** modify ***
				*/
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
	private class CheckAllOptionsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			// DIRECTORY ONLY MODE
			if (event.getSource() == JCBCheckAll) {
				if(JCBCheckAll.isSelected() == true) {
					for (Component comp : HMComponents.values ())
						if (comp.TYPE == 'C')
							comp.checkBox.setSelected(true);
					/*
					JCBSSN.setSelected(true);	//** modify **
					JCBDoB.setSelected(true);	//** modify **
					JCBMaiden.setSelected(true);	//** modify **
					JCBPoB.setSelected(true);	//** modify **
					JCBAlien.setSelected(true);	//** modify **
					JCBFBIInfoFiles.setSelected(true);	//** modify **	//<=========== sets all checkbox to true
					JCBGrandJury.setSelected(true);	//** modify **
					JCBFBISources.setSelected(true);	//** modify **
					JCBFBISourceCodes.setSelected(true);	//** modify **
					*/
			   } else {
					for (Component comp : HMComponents.values ())
						if (comp.TYPE == 'C')
							comp.checkBox.setSelected(false);
					/*
					JCBSSN.setSelected(false);	//** modify **
					JCBDoB.setSelected(false);	//** modify **
					JCBMaiden.setSelected(false);	//** modify **
					JCBPoB.setSelected(false);	//** modify **
					JCBAlien.setSelected(false);	//** modify **
					JCBFBIInfoFiles.setSelected(false);	//** modify **	//<=========== sets all check box to false
					JCBGrandJury.setSelected(false);	//** modify **
					JCBFBISources.setSelected(false);	//** modify **
					JCBFBISourceCodes.setSelected(false);	//** modify **
					*/
				}
			}
		}
	}
	
	/**
	 * This internal class listens for user's interaction with run mode.
	 */
	private class MyRunModeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == JRBDirectory) {				// DIRECTORY ONLY MODE
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			} else if (event.getSource() == JRBFile) { 				// FILE ONLY MODE
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
		}
	}

	/**
	 * This internal class listens for user's input/output
	 */
	private class MyIOListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == JBInput) {					// INPUT BUTTON
				// open browse directory/file dialog
				int userRespond = fileChooser.showOpenDialog(Main.this);
				
				if (userRespond == JFileChooser.APPROVE_OPTION) {	// user select a directory/file
					userInput = fileChooser.getSelectedFile();
					String msg = "Input: " + userInput + "\n";
					printToProgress(msg);
				}
			} else if (event.getSource() == JBExport) {				// HTML SAVE BUTTON
				// get today date
				Calendar cal = Calendar.getInstance();
				String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
				String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
				String year = String.valueOf(cal.get(Calendar.YEAR));
 
				// open save file dialog with a default file name
				String filename = "pii_finder_result_" + month + "_" + day + "_" + year + "";
				fileSaver.setSelectedFile(new File(filename));
 
				int userRespond1 = fileSaver.showSaveDialog(Main.this);
 
				// user enter a save file
				if (userRespond1 == JFileChooser.APPROVE_OPTION) {
					if (fileSaver.getFileFilter().equals(webpageFilter)) {
						outputFileHTML = new File(fileSaver.getSelectedFile()+".html");
						if (outputFileHTML != null && outputFileHTML.exists()) {
							String msg = "The file " + outputFileHTML.getName() + " already exists. Do you want to replace the existing file?";
							String title = "Ovewrite file?";
							int userRespond2 = JOptionPane.showConfirmDialog(Main.this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
							
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
	 * This internal class listens for user's interaction with run button.
	 */
	private class MySearchTaskListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {			
			if (event.getSource() == JBRun) {			// RUN BUTTON
				// check if a match mode is selected
				boolean noneSelected = true;				
				for (Component comp : HMComponents.values ())
					if (comp.TYPE == 'T' && !comp.text.getText().isEmpty())
						noneSelected = false;
					else if (comp.TYPE == 'C' && comp.checkBox.isSelected())
						noneSelected = false;
					
				/*
				if (HMComponents.get("TxtField").text.getText().isEmpty() && 
					!HMComponents.get("SSN").checkBox.isSelected() && 
					!HMComponents.get("PoB").checkBox.isSelected() && 
					!HMComponents.get("DoB").checkBox.isSelected() && 
					!HMComponents.get("Maiden").checkBox.isSelected() && 
					!HMComponents.get("Alien").checkBox.isSelected() && 
					!HMComponents.get("GrandJury").checkBox.isSelected() && 
					!HMComponents.get("FBISource").checkBox.isSelected() && 
					!HMComponents.get("FBISourceCode").checkBox.isSelected() && 
					!HMComponents.get("FBIInfoFile").checkBox.isSelected()) {
				*/
				if (noneSelected) {
					JOptionPane.showMessageDialog(Main.this, "ERROR: No match mode is selected");
					return; // stop here	
				}
				
				/*				//******** modify if statement below **********
				if (JTField.getText().isEmpty() && !JCBSSN.isSelected() && !JCBPoB.isSelected() && !JCBDoB.isSelected() && !JCBMaiden.isSelected() && !JCBAlien.isSelected() && !JCBGrandJury.isSelected() && !JCBFBISources.isSelected() && !JCBFBISourceCodes.isSelected() && !JCBFBIInfoFiles.isSelected()) {
					JOptionPane.showMessageDialog(Main.this, "ERROR: No match mode is selected");
					return; // stop here	
				}
				*/
				
				if (userInput == null) {		// check if there is an input file/directory
					JOptionPane.showMessageDialog(Main.this, "ERROR: No input file/directory");
					return; // stop here
				}
				
				if (fileChooser.getFileSelectionMode() == JFileChooser.DIRECTORIES_ONLY) {	// read mode: directory only
					initNewSearch();
					searchTask.execute();
					JBRun.setEnabled(false);
					JBCancel.setEnabled(true);
				} else if (fileChooser.getFileSelectionMode() == JFileChooser.FILES_ONLY) {	// read mode: file only
					initNewSearch();
					searchTask.execute();
					JBRun.setEnabled(false);
					JBCancel.setEnabled(true);
				}

				JPMain.remove(row3);
				JPMain.add(row4);
				JPMain.add(row5);
				JPMain.validate();
				JPMain.repaint();
			} else if (event.getSource() == JBCancel) {		// CANCEL BUTTON
				searchTask.cancel(true);
				//System.exit(0);
			}
		}
	}
	
/********************************************************************************************************************
*											The Search Task Section													*
********************************************************************************************************************/
	private class SearchTask extends SwingWorker<Void, String> {
		/**
		 * This method takes a given directory and find SSNs for all the files reachable from that directory.
		 * @param dir - directory that need to be processed
		 */
		public void recursiveSearch(File dir) {
			if (Thread.currentThread().isInterrupted())	// handle interrupted (cancel)
				return;
			
			List<File> inputFiles = new ArrayList<File>();		// build list of input files
			
			if (fileChooser.getFileSelectionMode() == JFileChooser.FILES_ONLY)
				inputFiles.add(dir);
			else
				inputFiles = Arrays.asList(dir.listFiles());
			
			totalFiles += inputFiles.size();	// update counter
			
			for (File file: inputFiles) {		// process file by file
				if (file.isDirectory()) {
					totalFiles --;
					recursiveSearch(file);
				} else {
					InputStream input = null;
					
					try {
						String fileName = file.getName();
						String fileExtension = "txt";
						int i = fileName.lastIndexOf(".");
						if (i > 0) {
							fileExtension = fileName.substring(i+1);
						}
						
						if (fileExtension.equals("txt")) {
							ContentHandler handler = new BodyContentHandler(-1);
							input = new FileInputStream(file);
							Metadata metadata = new Metadata();
							TXTParser TXTParser = new TXTParser();
							ParseContext context = new ParseContext();

							TXTParser.parse(input, handler, metadata, context);

							fileReader = new Scanner(handler.toString());
						} else if (fileExtension.equals("docx")) {
							OPCPackage pkg = OPCPackage.open(file);
							XWPFDocument docx = new XWPFDocument(OPCPackage.open(file));
							XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
							fileReader = new Scanner(extractor.getText());
							pkg.close();
						} else if (fileExtension.equals("doc")) {
							NPOIFSFileSystem doc = new NPOIFSFileSystem(file);
							WordExtractor extractor = new WordExtractor(doc.getRoot());
							fileReader = new Scanner(WordExtractor.stripFields(extractor.getText()));
							doc.close();
						} else if (fileExtension.equals("xlsx")) {
							OPCPackage pkg = OPCPackage.open(file);
							XSSFWorkbook wb = new XSSFWorkbook(pkg);
							XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
							extractor.setFormulasNotResults(true);
							extractor.setIncludeSheetNames(false);
							fileReader = new Scanner(extractor.getText());
							pkg.close();
						} else if (fileExtension.equals("xls")) {
							NPOIFSFileSystem xls = new NPOIFSFileSystem(file);
							HSSFWorkbook wb = new HSSFWorkbook(xls.getRoot(), false);
							ExcelExtractor extractor = new ExcelExtractor(wb);
							extractor.setFormulasNotResults(true);
							extractor.setIncludeSheetNames(false);
							fileReader = new Scanner(extractor.getText());
							xls.close();
						} else if (fileExtension.equals("msg")) {
							MAPIMessage msg = new MAPIMessage(file.getAbsolutePath());
							fileReader = new Scanner(msg.getTextBody());
						} else if ((fileExtension.equals("htm"))||(fileExtension.equals("html"))) {
							ContentHandler handler = new BodyContentHandler(-1);
							input = new FileInputStream(file);
							Metadata metadata = new Metadata();
							HtmlParser HTMLParser = new HtmlParser();
							ParseContext context = new ParseContext();

							HTMLParser.parse(input, handler, metadata, context);

							fileReader = new Scanner(handler.toString());
						} else if (fileExtension.equals("rtf")) {
							ContentHandler handler = new BodyContentHandler(-1);
							input = new FileInputStream(file);
							Metadata metadata = new Metadata();
							RTFParser RTFParser = new RTFParser();
							ParseContext context = new ParseContext();

							RTFParser.parse(input, handler, metadata, context);

							fileReader = new Scanner(handler.toString());
						} else if (fileExtension.equals("mbox")) {
							ContentHandler handler = new BodyContentHandler(-1);
							input = new FileInputStream(file);
							Metadata metadata = new Metadata();
							MboxParser MBOXParser = new MboxParser();
							ParseContext context = new ParseContext();

							MBOXParser.parse(input, handler, metadata, context);

							fileReader = new Scanner(handler.toString());
						} else if (fileExtension.equals("pst")) {
							ContentHandler handler = new BodyContentHandler(-1);
							input = new FileInputStream(file);
							Metadata metadata = new Metadata();
							OutlookPSTParser OutlookPSTParser = new OutlookPSTParser();
							ParseContext context = new ParseContext();

							OutlookPSTParser.parse(input, handler, metadata, context);

							fileReader = new Scanner(handler.toString());
						} else if (fileExtension.equals("mdb")) {
							ContentHandler handler = new BodyContentHandler(-1);
							input = new FileInputStream(file);
							Metadata metadata = new Metadata();
							JackcessParser JackcessParser = new JackcessParser();
							ParseContext context = new ParseContext();

							JackcessParser.parse(input, handler, metadata, context);

							fileReader = new Scanner(handler.toString());
						} else if (fileExtension.equals("pdf")) {
							ContentHandler handler = new BodyContentHandler(-1);
							input = new FileInputStream(file);
							Metadata metadata = new Metadata();
							PDFParser PDFParser = new PDFParser();
							ParseContext context = new ParseContext();

							PDFParser.parse(input, handler, metadata, context);

							fileReader = new Scanner(handler.toString());
						} else if (fileExtension.isEmpty()) {
							fileReader = new Scanner(file);
						} else {
							if (JCBAutoParser.isSelected()) {
								if (skipExtensions.contains(fileExtension)) {
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
						
						matchRegex(file, fileExtension);	// find matching regex in current processing file
						/*
						throw new DataFormatException("DataFormatException");
						
					} catch (DataFormatException e) {
						System.out.println("DFE "+e);
						skipFiles.add(file);*/
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
					} finally {
						if (input != null) {
							try {
								input.close();
							} catch(IOException e) {
								System.out.println("IOE " + e);
							}
						}
					}
				}
			}
		}
		
		/**
		 * This method does the regular expression matching.
		 * Results will be output to the GUI and save in HTML format.
		 * @param file - file that will be processed
		 */
		private void matchRegex(File file, String fileExtension) {
			int lineNum = 1;		// init line counter
			String lineA = "";
			
			addTextToRegex(HMComponents.get("TxtField").text.getText());
			//addTextToRegex(JTField.getText());						//** modify **
			
			System.out.println ("regexText is " + HMComponents.get("TxtField").regex);			//<================ for debug
			//System.out.println("regexText is " + regexText);			//<================ for debug
			
			if (fileReader.hasNext()) {			// check if file is readable
				readCounter ++;
				extCounter.count(fileExtension);
				lineA = fileReader.nextLine();
			} else
				System.out.println(file.getName() + " ext: " + fileExtension);

//////IF THERE ARE MULTIPLE LINES IN THE FILE////////////IF THERE ARE MULTIPLE LINES IN THE FILE//////
//////IF THERE ARE MULTIPLE LINES IN THE FILE////////////IF THERE ARE MULTIPLE LINES IN THE FILE//////
//////IF THERE ARE MULTIPLE LINES IN THE FILE////////////IF THERE ARE MULTIPLE LINES IN THE FILE//////		
			
			// use global file reader with file's text already loaded
			while(fileReader.hasNext()) {
				String lineB = fileReader.nextLine();
				String line = lineA + lineB;
				//Matcher patternMatcher = null;
				
				for (Component comp : HMComponents.values ()) {			// perhaps impliments the true false stuff directly into each individual
					if (comp.TYPE == 'T' && !comp.text.getText().isEmpty()) {// objects rather than using method parameters like this
						doResult (comp, line, fileExtension, file, lineNum, false, true, true, false);
					} else if (comp.TYPE == 'C' && comp.checkBox.isSelected()) {
						if (comp.SYM == "SSN")
							doResult (comp, line, fileExtension, file, lineNum, false, true, true, false);
						else
							doResult (comp, line, fileExtension, file, lineNum, true, false, false, true);
					}
				}
				
				/*
				if (!(JTField.getText().isEmpty())) {					//** modify **
					for (Pattern regexTexti : regexText) {					//** modify **
						patternMatcher = regexTexti.matcher(line);
						while (patternMatcher.find()) {
							textCounter++;									//********** modify ***********
							
							resultTextList.add(new Match(textCounter, "Text", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **
							resultTextListUnique.add(new Match(textCounter, "Text", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **
							
							JBTableModel.addRow(new Object[]{textCounter, "Text", patternMatcher.group(), line, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBSSN.isSelected()) {	//** modify **
					for (Pattern regexSSNi : regexSSN) {	//** modify **
						patternMatcher = regexSSNi.matcher(line);
						while (patternMatcher.find()) {
							ssnCounter++;	//** modify **
							
							resultSSNList.add(new Match(ssnCounter, "SSN", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **
							resultSSNListUnique.add(new Match(ssnCounter, "SSN", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **
							
							JBTableModel.addRow(new Object[]{ssnCounter, "SSN", patternMatcher.group(), line, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				
				if (JCBDoB.isSelected()) {	//** modify **
					for (Pattern regexDoB: regexDoBs) {	//** modify **
						patternMatcher = regexDoB.matcher(line);
						while (patternMatcher.find()) {
							matchCounter ++;
							dobCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(dobCounter, "DoB", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{dobCounter, "DoB", patternMatcher.group(), line, fileExtension, file, lineNum});	//** modify **
						}
					}
				}

				if (JCBPoB.isSelected()) {	//** modify **
					for (Pattern regexPoB: regexPoBs) {	//** modify **
						patternMatcher = regexPoB.matcher(line);
						while (patternMatcher.find()) {
							matchCounter ++;
							pobCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(pobCounter, "PoB", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{pobCounter, "PoB", patternMatcher.group(), line, fileExtension, file, lineNum});	//** modify **
						}
					}
				}

				if (JCBMaiden.isSelected()) {	//** modify **
					for (Pattern regexMaiden: regexMaidens) {	//** modify **
						patternMatcher = regexMaiden.matcher(line);
						while (patternMatcher.find()) {
							matchCounter ++;
							maidenCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(maidenCounter, "Maiden", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{maidenCounter, "Maiden", patternMatcher.group(), line, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBAlien.isSelected()) {	//** modify **
					for (Pattern regexAlien: regexAliens) {	//** modify **
						patternMatcher = regexAlien.matcher(line);
						while (patternMatcher.find()) {
							matchCounter ++;
							alienCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(alienCounter, "Alien", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{alienCounter, "Alien", patternMatcher.group(), line, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBGrandJury.isSelected()) {	//** modify **		//<==================== check box active varified here !!
					for (Pattern regexGrandJury: regexGrandJuries) {	//** modify **
						patternMatcher = regexGrandJury.matcher(line);
						while (patternMatcher.find()) {
							matchCounter ++;
							grandJuryCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(grandJuryCounter, "Grand Jury", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{grandJuryCounter, "Grand Jury", patternMatcher.group(), line, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBFBIInfoFiles.isSelected()) {	//** modify **
					for (Pattern regexFBIInfoFile: regexFBIInfoFiles) {	//** modify **
						patternMatcher = regexFBIInfoFile.matcher(line);
						while (patternMatcher.find()) {
							matchCounter ++;
							FBIInfoFileCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(FBIInfoFileCounter, "FBI Information File", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{FBIInfoFileCounter, "FBI Information File", patternMatcher.group(), line, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBFBISources.isSelected()) {	//** modify **
					for (Pattern regexFBISource: regexFBISources) {	//** modify **
						patternMatcher = regexFBISource.matcher(line);
						while (patternMatcher.find()) {
							matchCounter ++;
							FBISourceCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(FBISourceCounter, "FBI Source", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{FBISourceCounter, "FBI Source", patternMatcher.group(), line, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBFBISourceCodes.isSelected()) {	//** modify **
					for (Pattern regexFBISourceCode: regexFBISourceCodes) {	//** modify **
						patternMatcher = regexFBISourceCode.matcher(line);
						while (patternMatcher.find()) {
							matchCounter ++;
							FBISourceCodeCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(FBISourceCodeCounter, "FBI Source Code", patternMatcher.group(), line, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{FBISourceCodeCounter, "FBI Source Code", patternMatcher.group(), line, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				*/
				
				lineNum ++;
				lineA = lineB;
			}
			
//////IF MATCH ON LAST LINE OR ONLY ONE LINE////////////IF MATCH ON LAST LINE OR ONLY ONE LINE//////
//////IF MATCH ON LAST LINE OR ONLY ONE LINE////////////IF MATCH ON LAST LINE OR ONLY ONE LINE//////
//////IF MATCH ON LAST LINE OR ONLY ONE LINE////////////IF MATCH ON LAST LINE OR ONLY ONE LINE//////
			if( !(fileReader.hasNext()) ) {
				//Matcher patternMatcher = null;
				
				for (Component comp : HMComponents.values ()) {			// perhaps impliments the true false stuff directly into each individual
					if (comp.TYPE == 'T' && !comp.text.getText().isEmpty()) {// objects rather than using method parameters like this
						doResult (comp, lineA, fileExtension, file, lineNum, false, true, true, false);
					} else if (comp.TYPE == 'C' && comp.checkBox.isSelected()) {
						if (comp.SYM == "SSN")
							doResult (comp, lineA, fileExtension, file, lineNum, false, true, true, false);
						else
							doResult (comp, lineA, fileExtension, file, lineNum, true, false, false, true);
					}
				}
				
				/*
				if (!(JTField.getText().isEmpty())) {	//** modify **
					for (Pattern regexTexti : regexText) {	//** modify **
						patternMatcher = regexTexti.matcher(lineA);
						while (patternMatcher.find()) {
							textCounter++;	//** modify **
							resultTextList.add(new Match(textCounter, "Text", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **
							resultTextListUnique.add(new Match(textCounter, "Text", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **
							
							JBTableModel.addRow(new Object[]{textCounter, "Text", patternMatcher.group(), lineA, fileExtension, file, lineNum});	//** modify **
						}	
					}
				}

				if (JCBSSN.isSelected()) {	//** modify **
					for (Pattern regexSSNi : regexSSN) {	//** modify **
						patternMatcher = regexSSNi.matcher(lineA);
						while (patternMatcher.find()) {
							ssnCounter++;	//** modify **
							resultSSNList.add(new Match(ssnCounter, "SSN", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **
							resultSSNListUnique.add(new Match(ssnCounter, "SSN", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{ssnCounter, "SSN", patternMatcher.group(), lineA, fileExtension, file, lineNum});	//** modify **
						}
					}
				}

				if (JCBDoB.isSelected()) {	//** modify **
					for (Pattern regexDoB: regexDoBs) {	//** modify **
						patternMatcher = regexDoB.matcher(lineA);
						while (patternMatcher.find()) {
							matchCounter ++;
							dobCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(dobCounter, "DoB", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{dobCounter, "DoB", patternMatcher.group(), lineA, fileExtension, file, lineNum});	//** modify **
						}
					}
				}

				if (JCBPoB.isSelected()) {	//** modify **
					for (Pattern regexPoB: regexPoBs) {	//** modify **
						patternMatcher = regexPoB.matcher(lineA);
						while (patternMatcher.find()) {
							matchCounter ++;
							pobCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(pobCounter, "PoB", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{pobCounter, "PoB", patternMatcher.group(), lineA, fileExtension, file, lineNum});	//** modify **
						}
					}
				}

				if (JCBMaiden.isSelected()) {	//** modify **
					for (Pattern regexMaiden: regexMaidens) {	//** modify **
						patternMatcher = regexMaiden.matcher(lineA);
						while (patternMatcher.find()) {
							matchCounter ++;
							maidenCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(maidenCounter, "Maiden", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{maidenCounter, "Maiden", patternMatcher.group(), lineA, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBAlien.isSelected()) {	//** modify **
					for (Pattern regexAlien: regexAliens) {	//** modify **
						patternMatcher = regexAlien.matcher(lineA);
						while (patternMatcher.find()) {
							matchCounter ++;
							alienCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(alienCounter, "Alien", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{alienCounter, "Alien", patternMatcher.group(), lineA, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBGrandJury.isSelected()) {	//** modify **	//<==================== check box active varified here !!
					for (Pattern regexGrandJury: regexGrandJuries) {	//** modify **
						patternMatcher = regexGrandJury.matcher(lineA);
						while (patternMatcher.find()) {
							matchCounter ++;
							grandJuryCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(grandJuryCounter, "Grand Jury", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{grandJuryCounter, "Grand Jury", patternMatcher.group(), lineA, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBFBIInfoFiles.isSelected()) {	//** modify **
					for (Pattern regexFBIInfoFile: regexFBIInfoFiles) {	//** modify **
						patternMatcher = regexFBIInfoFile.matcher(lineA);
						while (patternMatcher.find()) {
							matchCounter ++;
							FBIInfoFileCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(FBIInfoFileCounter, "FBI Information File", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{FBIInfoFileCounter, "FBI Information File", patternMatcher.group(), lineA, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBFBISources.isSelected()) {	//** modify **
					for (Pattern regexFBISource: regexFBISources) {	//** modify **
						patternMatcher = regexFBISource.matcher(lineA);
						while (patternMatcher.find()) {
							matchCounter ++;
							FBISourceCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(FBISourceCounter, "FBI Source", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{FBISourceCounter, "FBI Source", patternMatcher.group(), lineA, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				
				if (JCBFBISourceCodes.isSelected()) {	//** modify **
					for (Pattern regexFBISourceCode: regexFBISourceCodes) {	//** modify **
						patternMatcher = regexFBISourceCode.matcher(lineA);
						while (patternMatcher.find()) {
							matchCounter ++;
							FBISourceCodeCounter ++;	//** modify **

							resultOtherMatchList.add(new Match(FBISourceCodeCounter, "FBI Source Code", patternMatcher.group(), lineA, fileExtension, file, lineNum));	//** modify **

							JBTableModel.addRow(new Object[]{FBISourceCodeCounter, "FBI Source Code", patternMatcher.group(), lineA, fileExtension, file, lineNum});	//** modify **
						}
					}
				}
				*/
				lineNum ++;
			}
			
			fileReader.close();				// tidy up and update progress
			publish("printCurrentProgress");
			fileCounter ++;
			//System.out.println("Search Ended");		//<========= for debugging
		}
		
		private ArrayList getOtherResults(ArrayList<Match> elf) {
			for (Match pr : resultOtherMatchList) {
				JBTableModel.addRow(new Object[]{pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum()});
				
				if(pr.getConfidence().matches("Text"))
					Main.this.addToAllRow (false, false, 0, pr, HMComponents.get ("TxtField").html, HMComponents.get ("TxtField").csv);
					//Main.this.addToAllRow (false, false, 0, pr, textHTML, textCSV);	//** modify **
					
				if(pr.getConfidence().matches("PoB"))
					Main.this.addToAllRow (false, false, 0, pr, HMComponents.get ("PoB").html, HMComponents.get ("PoB").csv);
					//Main.this.addToAllRow (false, false, 0, pr, pobHTML, pobCSV);	//** modify **
				
				if(pr.getConfidence().matches("DoB"))
					Main.this.addToAllRow (false, false, 0, pr, HMComponents.get ("DoB").html, HMComponents.get ("DoB").csv);
					//Main.this.addToAllRow (false, false, 0, pr, dobHTML, dobCSV);	//** modify **
				
				if(pr.getConfidence().matches("Maiden"))
					Main.this.addToAllRow (false, false, 0, pr, HMComponents.get ("Maiden").html, HMComponents.get ("Maiden").csv);
					//Main.this.addToAllRow (false, false, 0, pr, maidenHTML, maidenCSV);	//** modify **
				
				if(pr.getConfidence().matches("Alien"))
					Main.this.addToAllRow (false, false, 0, pr, HMComponents.get ("Alien").html, HMComponents.get ("Alien").csv);
					//Main.this.addToAllRow (false, false, 0, pr, alienHTML, alienCSV);	//** modify **
				
				if(pr.getConfidence().matches("Grand Jury"))
					Main.this.addToAllRow (false, false, 0, pr, HMComponents.get ("Grand Jury").html, HMComponents.get ("Grand Jury").csv);
					//Main.this.addToAllRow (false, false, 0, pr, grandJuryHTML, grandJuryCSV);	//** modify **
				
				if(pr.getConfidence().matches("FBI Information File"))
					Main.this.addToAllRow (false, false, 0, pr, HMComponents.get ("FBIInfoFile").html, HMComponents.get ("FBIInfoFile").csv);
					//Main.this.addToAllRow (false, false, 0, pr, FBIInfoFileHTML, FBIInfoFileCSV);	//** modify **
				
				if(pr.getConfidence().matches("FBI Source"))
					Main.this.addToAllRow (false, false, 0, pr, HMComponents.get ("FBISource").html, HMComponents.get ("FBISource").csv);
					//Main.this.addToAllRow (false, false, 0, pr, FBISourceHTML, FBISourceCSV);	//** modify **
				
				if(pr.getConfidence().matches("FBI Source Code"))
					Main.this.addToAllRow (false, false, 0, pr, HMComponents.get ("FBISourceCode").html, HMComponents.get ("FBISourceCode").csv);
					//Main.this.addToAllRow (false, false, 0, pr, FBISourceCodeHTML, FBISourceCodeCSV);	//** modify **
			}
			
			return resultOtherMatchList;
		}
		
		private ArrayList cleanTextResults(HashSet<Match> elf) {            
			Component tmpComp = HMComponents.get ("TxtField");	// get reference handler to TextField object component 
			ArrayList <Match> tmpList = tmpComp.resultListUniqueFinal;	// get reference handler to resultTextListUniqueFinal
			
			for(Match pr : elf)
				if(elf.contains(pr))
					tmpList.add(pr);
					//resultTextListUniqueFinal.add(pr);	//** modify **
			
			Collections.sort(tmpList, new Comparator <Match> () {
			//Collections.sort(resultTextListUniqueFinal, new Comparator<Match>() {	//** modify **
				@Override
				public int compare(Match z1, Match z2) {
					if (z1.getID() > z2.getID()) { return 1; }
					if (z1.getID() < z2.getID()) { return -1; }
					return 0;
				}
			});
			
			int i = 1;
			for (Match pr : tmpList) {
			//for (Match pr : resultTextListUniqueFinal) {	//** modify **
				Main.this.addToAllRow (true, true, i, pr, tmpComp.html, tmpComp.csv);
				//Main.this.addToAllRow (true, true, i, pr, textHTML, textCSV);	//** modify **
				i++;
			}
			
			tmpComp.counter = tmpList.size ();
			return tmpList;
			//textCounter = resultTextListUniqueFinal.size();	//** modify **
			//return resultTextListUniqueFinal;	//** modify **
		}
		
		private ArrayList cleanSSNResults(HashSet<Match> elf) {            
			Component tmpComp = HMComponents.get ("SSN");	// get reference handler to SSN object component 
			ArrayList <Match> tmpList = tmpComp.resultListUniqueFinal;	// get reference handler to resultSSNListUniqueFinal
			
			for(Match pr : elf)
				if(elf.contains(pr))
					tmpList.add (pr);
					//resultSSNListUniqueFinal.add(pr);	//** modify **
			
			Collections.sort(tmpList, new Comparator<Match>() {
			//Collections.sort(resultSSNListUniqueFinal, new Comparator<Match>() {	//** modify **
				@Override
				public int compare(Match z1, Match z2) {
					if (z1.getID() > z2.getID()) { return 1; }
					if (z1.getID() < z2.getID()) { return -1; }
					return 0;
				}
			});
			
			int i = 1;
			for (Match pr : tmpList) {
			//for (Match pr : resultSSNListUniqueFinal) {	//** modify **
				Main.this.addToAllRow (true, true, i, pr, tmpComp.html, tmpComp.csv);
				//Main.this.addToAllRow (true, true, i, pr, ssnHTML, ssnCSV);	//** modify **
				i++;
			}
			
			tmpComp.counter = tmpList.size ();
			return tmpList;
			//ssnCounter = resultSSNListUniqueFinal.size();	//** modify **
			//return resultSSNListUniqueFinal;	//** modify **
		}
		
		private ArrayList<Match> getTextResults(ArrayList<Match> elf) {
			Component tmpComp = HMComponents.get ("TxtField");	// get reference handler to TextField object component 
			ArrayList <Match> tmpList = tmpComp.resultList;	// get reference handler to resultTextList
			
			int i = 1;
			for (Match pr : elf) {
				Main.this.addToAllRow (true, true, i, pr, tmpComp.html, tmpComp.csv);
				//Main.this.addToAllRow (true, true, i, pr, textHTML, textCSV);	//** modify **
				i++;
			}
			
			tmpComp.counter = tmpList.size();
			return tmpList;
			//textCounter = resultTextList.size();	//** modify **
			//return resultTextList;	//** modify **
		}
		
		private ArrayList<Match> getSSNResults(ArrayList<Match> elf) {
			Component tmpComp = HMComponents.get ("SSN");	// get reference handler to SSN object component 
			ArrayList <Match> tmpList = tmpComp.resultList;	// get reference handler to resultSSNList
			
			int i = 1;
			for (Match pr : elf) {
				Main.this.addToAllRow (true, true, i, pr, tmpComp.html, tmpComp.csv);
				//Main.this.addToAllRow (true, true, i, pr, ssnHTML, ssnCSV);	//** modify **
				i++;
			}
			
			tmpComp.counter = tmpList.size ();
			return tmpList;
			//ssnCounter = resultSSNList.size();	//** modify **
			//return resultSSNList;	//** modify **
		}
		
		private void buildCSVResult() {
			postCSVResult.append (csvWriter.addTableHeader());
			
			for (Component comp : HMComponents.values ())
				if (comp.TYPE == 'T' && !comp.text.getText().isEmpty())
					postCSVResult.append (comp.csv.toString ());
				else if (comp.TYPE == 'C' && comp.checkBox.isSelected ())
					postCSVResult.append (comp.csv.toString ());
			
			/*
			if (!(JTField.getText().isEmpty()))	//** modify **
				postCSVResult.append (textCSV.toString ());	//** modify **
			
			if (JCBSSN.isSelected())	//** modify **
				postCSVResult.append (ssnCSV.toString ());	//** modify **
			
			if (JCBDoB.isSelected())	//** modify **
				postCSVResult.append (dobCSV.toString ());	//** modify **
			
			if (JCBPoB.isSelected())	//** modify **
				postCSVResult.append (pobCSV.toString ());	//** modify **
			
			if (JCBMaiden.isSelected())	//** modify **
				postCSVResult.append (maidenCSV.toString ());	//** modify **
			
			if (JCBAlien.isSelected())	//** modify **
				postCSVResult.append (alienCSV.toString ());	//** modify **
			
			if (JCBGrandJury.isSelected())	//** modify **
				postCSVResult.append (grandJuryCSV.toString ());	//** modify **
			
			if (JCBFBIInfoFiles.isSelected())	//** modify **
				postCSVResult.append (FBIInfoFileCSV.toString ());	//** modify **
			
			if (JCBFBISources.isSelected())	//** modify **
				postCSVResult.append (FBISourceCSV.toString ());	//** modify **
			
			if (JCBFBISourceCodes.isSelected())	//** modify **
				postCSVResult.append (FBISourceCodeCSV.toString ());	//** modify **
			*/
		}

		/**
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
				StringBuilder link = new StringBuilder (comp.SYM.replaceAll ("\\s+", ""));
				StringBuilder lnkLabel = new StringBuilder (comp.SYM + " Matches");
				
				if (comp.TYPE == 'T' && !comp.text.getText().isEmpty())
					Main.this.buildHTMLNav (comp.counter, link, lnkLabel);
				
				if (comp.TYPE == 'C' && comp.checkBox.isSelected ())
					Main.this.buildHTMLNav (comp.counter, link, lnkLabel);
			}
			
			/*
			if (!(JTField.getText().isEmpty()))	//** modify **
				Main.this.buildHTMLNav (textCounter, "textResults", "Text Matches");	//** modify **
			
			if (JCBSSN.isSelected())	//** modify **
				Main.this.buildHTMLNav (ssnCounter, "ssnResults", "SSN Matches");	//** modify **
			
			if (JCBDoB.isSelected())	//** modify **
				Main.this.buildHTMLNav (dobCounter, "dobResults", "DoB Matches");	//** modify **
			
			if (JCBPoB.isSelected())	//** modify **
				Main.this.buildHTMLNav (pobCounter, "pobResults", "PoB Matches");	//** modify **
			
			if (JCBMaiden.isSelected())	//** modify **
				Main.this.buildHTMLNav (maidenCounter, "maidenResults", "Maiden Matches");	//** modify **
			
			if (JCBAlien.isSelected())	//** modify **
				Main.this.buildHTMLNav (alienCounter, "alienResults", "Alien Matches");	//** modify **
			
			if (JCBGrandJury.isSelected())	//** modify **
				Main.this.buildHTMLNav (grandJuryCounter, "grandJuryResults", "Grand Jury Matches");	//** modify **
			
			if (JCBFBIInfoFiles.isSelected())	//** modify **
				Main.this.buildHTMLNav (FBIInfoFileCounter, "FBIInfoFilesResults", "FBI Information Files Matches");	//** modify **
			
			if (JCBFBISources.isSelected())	//** modify **
				Main.this.buildHTMLNav (FBISourceCounter, "FBISourceResults", "FBI Source Matches");	//** modify **
			
			if (JCBFBISourceCodes.isSelected())	//** modify **
				Main.this.buildHTMLNav (FBISourceCodeCounter, "FBISourceCodeResults", "FBI Source Code Matches");	//** modify **
			*/
			
			postHtmlResult.append (htmlWriter.addCloseNavULTag());
			postHtmlResult.append (htmlWriter.addCloseNavTag());
			postHtmlResult.append (htmlWriter.addCloseCenterTag());
			
			postHtmlResult.append (htmlWriter.addOpenCenterTag());
			postHtmlResult.append (htmlWriter.addOpenNavTag());
			postHtmlResult.append (htmlWriter.addOpenNavULTag());	// ********* !! possible bug !! why line below only considers ssnCounter and textCounter? **********
			//postHtmlResult.append (htmlWriter.addResultNote(skipFiles.size(), readCounter, totalFiles, textCounter + ssnCounter + matchCounter, calculateElapsedTime()));	//** modify **
			postHtmlResult.append (htmlWriter.addResultNote(skipFiles.size(), readCounter, totalFiles, HMComponents.get ("TxtField").counter + HMComponents.get ("SSN").counter + matchCounter, calculateElapsedTime()));
			postHtmlResult.append (htmlWriter.addExtNote(extCounter));
			postHtmlResult.append (htmlWriter.addCloseNavULTag());
			postHtmlResult.append (htmlWriter.addCloseNavTag());
			postHtmlResult.append (htmlWriter.addCloseCenterTag());
			
			for (Component comp : HMComponents.values ()) {
				StringBuilder link = new StringBuilder (comp.SYM.replaceAll ("\\s+", ""));
				StringBuilder lnkLabel = new StringBuilder (comp.SYM + " Found Results");
				StringBuilder tableTagId = new StringBuilder (comp.SYM.replaceAll ("\\s+", "") + "ResultTable");
				StringBuilder html = new StringBuilder (comp.html.toString ());
				
				if (comp.TYPE == 'T' && !comp.text.getText().isEmpty())
					Main.this.buildHTMLPanel (link, lnkLabel, tableTagId, html);
				else if (comp.TYPE == 'C' && comp.checkBox.isSelected ())
					Main.this.buildHTMLPanel (link, lnkLabel, tableTagId, html);
			}
			
			/*
			if ((!(JTField.getText().isEmpty())) && (textCounter > 0))	//** modify **
				Main.this.buildHTMLPanel ("textResults", "Text Found Results", "textResultTable", textHTML.toString ());	//** modify **
			
			if (JCBSSN.isSelected() && (ssnCounter > 0))	//** modify **
				Main.this.buildHTMLPanel ("ssnResults", "SSN Found Results", "ssnResultTable", ssnHTML.toString ());	//** modify **

			if (JCBDoB.isSelected() && (dobCounter > 0))	//** modify **
				Main.this.buildHTMLPanel ("dobResults", "DoB Found Results", "dobResultTable", dobHTML.toString ());	//** modify **

			if (JCBPoB.isSelected() && (pobCounter > 0))	//** modify **
				Main.this.buildHTMLPanel ("pobResults", "PoB Found Results", "pobResultTable", pobHTML.toString ());	//** modify **

			if (JCBMaiden.isSelected() && (maidenCounter > 0))	//** modify **
				Main.this.buildHTMLPanel ("maidenResults", "Maiden Name Found Results", "maidenResultTable", maidenHTML.toString ());	//** modify **
			
			if (JCBAlien.isSelected() && (alienCounter > 0))	//** modify **
				Main.this.buildHTMLPanel ("alienResults", "Alien Found Results", "alienResultTable", alienHTML.toString ());	//** modify **
			
			if (JCBGrandJury.isSelected() && (grandJuryCounter > 0))	//** modify **
				Main.this.buildHTMLPanel ("grandJuryResults", "Grand Jury Found Results", "grandJuryResultTable", grandJuryHTML.toString ());	//** modify **
			
			if (JCBFBISources.isSelected() && (FBISourceCounter > 0))	//** modify **
				Main.this.buildHTMLPanel ("FBISourceResults", "FBI Source Found Results", "FBISourceResultTable", FBISourceHTML.toString ());	//** modify **
			
			if (JCBFBIInfoFiles.isSelected() && (FBIInfoFileCounter > 0))	//** modify **
				Main.this.buildHTMLPanel ("FBIInfoFileResults", "FBI Information File Found Results", "FBIInfoFileResultTable", FBIInfoFileHTML.toString ());	//** modify **
			
			if (JCBFBISourceCodes.isSelected() && (FBISourceCodeCounter > 0))	//** modify **
				Main.this.buildHTMLPanel ("FBISourceCodeResults", "FBI Source Code Found Results", "FBISourceCodeResultTable", FBISourceCodeHTML.toString ());	//** modify **
			*/
			
			if(skipFiles.size() > 0) {
				postHtmlResult.append (htmlWriter.addOpenPanelTag());
				postHtmlResult.append (htmlWriter.addAnchorLink("skippedResults", "Unread Files"));
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
			recursiveSearch(userInput);
			return null;
		}
		
		@Override
		protected void process(List<String> msgList) {
			if (isCancelled())
				return;

			for (String msg : msgList) {
				if (msg.equals("printCurrentProgress")) {
					JPBStatus.setVisible(true);
					printToProgress("Completed " + fileCounter + " / " + totalFiles + " files." + " Results: " + (HMComponents.get ("TxtField").counter + HMComponents.get ("SSN").counter + matchCounter) );
					//printToProgress("Completed " + fileCounter + " / " + totalFiles + " files." + " Results: " + (textCounter + ssnCounter + matchCounter) );	//** modify **
				} else
					printToLog(msg);
			}
		}
		
		private void getConfidenceTable() {
			JBTCatModel.setRowCount(0);
			
			for (Component comp : HMComponents.values ()) {
				JBTCatModel.addRow(new Object[]{comp.LABEL, comp.counter});
			}
			
			/*
			JBTCatModel.addRow(new Object[]{"Text Matches", textCounter});	//** modify **
			JBTCatModel.addRow(new Object[]{"SSN Matches", ssnCounter});	//** modify **
			JBTCatModel.addRow(new Object[]{"Date of Birth", dobCounter});	//** modify **
			JBTCatModel.addRow(new Object[]{"Place of Birth", pobCounter});	//** modify **
			JBTCatModel.addRow(new Object[]{"Maiden Names", maidenCounter});	//** modify **
			JBTCatModel.addRow(new Object[]{"Alien Registration Numbers", alienCounter});	//** modify **
			JBTCatModel.addRow(new Object[]{"Grand Jury", grandJuryCounter});	//** modify **
			JBTCatModel.addRow(new Object[]{"FBI Information File", FBIInfoFileCounter});	//** modify **
			JBTCatModel.addRow(new Object[]{"FBI Source", FBISourceCounter});	//** modify **
			JBTCatModel.addRow(new Object[]{"FBI Source Code", FBISourceCodeCounter});	//** modify **
			*/
			
			JBTCatModel.addRow(new Object[]{"Total Matches", HMComponents.get ("TxtField").counter + HMComponents.get ("SSN").counter + matchCounter});
			//JBTCatModel.addRow(new Object[]{"Total Matches", textCounter + ssnCounter + matchCounter});	//** modify **
			
		}
		
		private void getExtensionTable() {
			for (String s : extCounter.extList) {
				int i = extCounter.extList.indexOf(s);
				int c = extCounter.extCount.get(i);
				JBTFileExtModel.addRow(new Object [] {s, c});
			}
		}
		
		@Override
		protected void done() {            
			System.out.println(skipFiles.toString());			//<=========== for debug
			
			Toolkit.getDefaultToolkit().beep();		// notify
			JPBStatus.setVisible(false);
			JPBStatus.setValue(0);
			
			getTextResults(HMComponents.get ("TxtField").resultList);		// update
			getSSNResults(HMComponents.get ("SSN").resultList);
			//getTextResults(resultTextList);		// update
			//getSSNResults(resultSSNList);
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
			StringBuilder msg = new StringBuilder ("*Readable: " + readCounter + " files / " + totalFiles + " files.\n" +
						 "*Found: " + (HMComponents.get ("TxtField").counter + HMComponents.get ("SSN").counter + matchCounter) + " matches.\n" +
						 //"*Found: " + (textCounter + ssnCounter + matchCounter) + " matches.\n" +	//** modify **
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

/********************************************************************************************************************
*									Miscelaneous Helper Method and Classes Section									*
********************************************************************************************************************/
	/**
	 * This class use for contructing check boxes and user enter text boxes and related data content
	 * used for search function. this class was created in effort to reduce code redundancy
	 * @param type - 
	 * @param sym - 
	 * @param label - 
	 * @param tip - 
	 */
	private class Component {
		final char TYPE;	//C = check box, T = text box
		final String SYM;
		final String LABEL;
		JCheckBox checkBox;
		JTextArea text;
		int counter;
		StringBuilder html;
		StringBuilder csv;
		
		List <Pattern> regex;
		ArrayList <Match> resultList;
		HashSet <Match> resultListUnique;
		ArrayList <Match> resultListUniqueFinal;
		
		public Component (char type, String sym, String label, String tip) {
			this.TYPE = type;
			this.SYM = sym;
			this.LABEL = label;
			
			if (type == 'C') {
				checkBox = new JCheckBox (label);
				checkBox.setToolTipText(tip);
			} else if (type == 'T') {
				text = new JTextArea (label);
				text.setToolTipText(tip);
				text.setLineWrap(true);
				text.setWrapStyleWord(true);
			}
			
			regex = new ArrayList<Pattern>();
			resultList = new ArrayList<Match>();
			resultListUnique = new HashSet<Match>();
			resultListUniqueFinal = new ArrayList<Match>();	
			
			initValues ();
			clrExport ();
		}
		
		void initValues () {
			counter = 0;
			if (this.TYPE == 'T')		//we only want to clear the user input regex content of the text box,
				regex.clear ();			//all other regex contents should remain intact after each search
			resultList.clear ();
			resultListUnique.clear ();
			resultListUniqueFinal.clear ();
		}
		
		void clrExport () {
			html = new StringBuilder ();
			csv = new StringBuilder ();
		}
	}
	
	/**
	 * This subroutine method is use for writing found regex matches to result list 
	 */
	private void doResult (Component comp, String line, String fileExt, File file, int lineNum, boolean cntMatch, boolean lst, boolean lstUnique, boolean lstOther) {
		for (Pattern regex : comp.regex) {
			Matcher patternMatcher = regex.matcher(line);
			
			while (patternMatcher.find()) {
				comp.counter ++;
				JBTableModel.addRow(new Object[]{comp.counter, comp.SYM, patternMatcher.group(), line, fileExt, file, lineNum});
				
				if (cntMatch) 
					matchCounter ++;
				
				if (lst)
					comp.resultList.add(new Match(comp.counter, comp.SYM, patternMatcher.group(), line, fileExt, file, lineNum));
				
				if (lstUnique)
					comp.resultListUnique.add(new Match(comp.counter, comp.SYM, patternMatcher.group(), line, fileExt, file, lineNum));
				
				if (lstOther)
					resultOtherMatchList.add(new Match(comp.counter, comp.SYM, patternMatcher.group(), line, fileExt, file, lineNum));
			}
		}
	}
	
	/**
	 * This subroutine method can be called from getOtherResults, cleanTextResults, cleanSSNResults, getTextResults,
	 * and getSSNResults. this method is created to reduce code redundancy. This method can act as either getter or 
	 * setter, which is determine by the setIdSwitch parameter. if setIdSwitch is enable, then index i argument can be
	 * used.
	 * @param addJBTable - switch to add a new row to JBTableModel
	 * @param setIdSwitch - switch on for pr.setID(), off for pr.getID()
	 * @param i - use for pr.setID() when setIdSwitch is on
	 */
	private void addToAllRow (boolean addJBTable, boolean setIdSwitch, int i, Match pr, StringBuilder html, StringBuilder csv) {
		if (addJBTable)
			JBTableModel.addRow (new Object[] {setIdSwitch ? pr.setID(i) : pr.getID(), pr.getConfidence(), pr.getText(), pr.getLine(), pr.getType(), pr.getFile(), pr.getLineNum() });
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
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		regexList.add(pattern);
	}
	
	/**
	 * This method is used for handling user input regex. parses user regex input into pattern
	 * and adds it to the regexText list. the regexText list is cleared on every new search 
	 */
	private void addTextToRegex(String text) {
		HashSet<String> tempTextList = new HashSet<>();
		tempTextList.clear();
		
		String[] tempText = text.split("(,)|(\\|)"); //split text entry on commas|(\\s), pipes or blank spaces (including line breaks)
		for (int i = 0; i < tempText.length; i++) {
			System.out.println("tempText[i] is " + tempText[i]);		// <======== for debugging
			
			if (!tempText[i].matches("")) {
				tempText[i] = tempText[i].trim();
				System.out.println("adding " + tempText[i]);		// <======== for debugging
				tempTextList.add(tempText[i]);
			}
		}

		Pattern pattern = Pattern.compile("\\b(" + StringUtils.join(tempTextList,"|") + ")\\b", Pattern.DOTALL);
		//System.out.println("pattern is x" + pattern + " x");		// <======== for debugging
		System.out.println("List: " + tempTextList);			// <======== for debugging
		HMComponents.get ("TxtField").regex.add(pattern);
		//regexText.add(pattern);	//** modify **
	}
	
	/**
	 * This method resets all strings used for exports.
	 */
	private void initNewExport() {
		/*
		textHTML = new StringBuilder ();	//** modify **
		ssnHTML = new StringBuilder ();	//** modify **
		dobHTML = new StringBuilder ();	//** modify **
		pobHTML = new StringBuilder ();	//** modify **
		maidenHTML = new StringBuilder ();	//** modify **
		alienHTML = new StringBuilder ();	//** modify **
		grandJuryHTML = new StringBuilder ();	//** modify **
		FBIInfoFileHTML = new StringBuilder ();	//** modify **
		FBISourceHTML = new StringBuilder ();	//** modify **
		FBISourceCodeHTML = new StringBuilder ();	//** modify **
		textCSV = new StringBuilder ();	//** modify **
		ssnCSV = new StringBuilder ();	//** modify **
		dobCSV = new StringBuilder ();	//** modify **
		pobCSV = new StringBuilder ();	//** modify **
		maidenCSV = new StringBuilder ();	//** modify **
		alienCSV = new StringBuilder ();	//** modify **
		grandJuryCSV = new StringBuilder ();	//** modify **
		FBIInfoFileCSV = new StringBuilder ();	//** modify **
		FBISourceCSV = new StringBuilder ();	//** modify **
		FBISourceCodeCSV = new StringBuilder ();		//** modify **
		*/
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
		/*
		regexText.clear();		//** modify **
		resultTextList.clear();		//** modify **
		resultTextListUnique.clear();		//** modify **
		resultTextListUniqueFinal.clear();		//** modify **
		resultSSNList.clear();		//** modify **
		resultSSNListUnique.clear();		//** modify **
		resultSSNListUniqueFinal.clear();		//** modify **
		*/
		resultOtherMatchList.clear();
		searchTask = new SearchTask();
		extCounter = new ExtensionCounter();
		startSearch = null;
		endSearch = null;
		totalFiles = 0;
		fileCounter = 0;
		readCounter = 0;
		matchCounter = 0;
		
		for (Component comp : HMComponents.values ())
			comp.initValues ();
		
		initNewExport();
		/*
		textCounter = 0;		//** modify **
		ssnCounter = 0;		//** modify **
		dobCounter = 0;		//** modify **
		pobCounter = 0;		//** modify **
		maidenCounter = 0;		//** modify **
		alienCounter = 0;		//** modify **
		grandJuryCounter = 0;		//** modify **
		FBIInfoFileCounter = 0;		//** modify **
		FBISourceCounter = 0;		//** modify **
		FBISourceCodeCounter = 0;		//** modify **
		*/
		
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

/********************************************************************************************************************
*												The Main Method														*
********************************************************************************************************************/
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