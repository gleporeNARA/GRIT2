package grit;

/*
 * this class contains the help information text displayed in the result window
 * when the application started.
 */

public class Help {
	private static final String helpStr;
	
	static {
		helpStr = 
			"THIS PROGRAM MAINLY SUPPORTS THE FOLLOWING FILE FORMATS:\n" +
			"1. TXT: plain text file\n" +
			"2. DOC: old Microsoft Word format\n" +
			"3. DOCX: new Microsoft Word format\n" +
			"4. XLS: old Microsoft Excel format\n" +
			"5. XLSX: new Microsoft Excel format\n" +
			"6. MSG: Microsoft Outlook format\n" +
			"7. HTML: standard web page format\n" +
			"8. XML: extensible web page format\n" +
			"9. RTF: rich text format\n" +
			"10. MBOX: Apple Mail format\n" +
			"11. PST: Microsoft Outlook format\n" +
			"12. MDB: Microsoft Access format\n" +
			"13. PDF: Portable Document format\n" +
            "14. WPD: WordPerfect, versions 5 and 6\n" +
			"\n" +
			"HOW TO USE:\n" +
			"1. Select the appropriate match mode(s). See below for detailed descriptions of matching algorithms.\n" +
			"2. Select the appropriate read mode.\n" +
			"3. Browse to the directory/file you want to search by clicking Input.\n" +
			"4. Hit Run to start the search.\n" +
			"5. Once the search is done, you can save search results by clicking Save Results as HTML or CSV.\n" +
			"     (search results are discarded if they are not saved upon exit for secure reasons.)\n" +
			"\n" +
			"DETAILED DESCRIPTION OF REGULAR EXPRESSIONS:\n" +
			"     (all searches are case insensitive)\n" +
			"\n" +
			"1. High Probability matches match perfectly formatted Social Security Numbers (555-55-5555) with the traditional numbering rules.\n" +
			"In addition, the following formats are matched:\n" +
			"     a. SSN# and SSN # and SS # and SS# followed by a number\n" +
			"     b. a space, the letters SSN, a possible space, and a number\n" +
			"     c. group of 3, 2, 4 numbers separated by a space, bounded by a word boundary (non-alpha character)\n" +
			"     d. the letters SSN or SSA plus the letters NO, plus a number within 5 spaces\n" +
			"\n" +
			"2. Medium probability matches searches for:\n" +
			"     a. # sign, three numbers matching rules, anything, then 6 more numbers\n" +
			"     b. three numbers matching rules, anything, then 6 more numbers WITH word boundaries, started by newline\n" +
			"     c. the phrase 'social security n' plus three numbers\n" +
			"     d. the letters ' SSA ' (with spaces around them) plus a number within 5 spaces\n" +
			"\n" +
			"3. Low probability matches searches for:\n" +
			"     a. all rules but with forward slashes (also matches a lot of dates)\n" +
			"     b. any nine numbers bracketed by something other than a number, hyphen or slash\n" +
			"     c. group of 3, 2, 4 separated by a / or - bounded by something other than a number, hyphen or slash\n" +
			"     d. group of 3, 2, 4 separated by a period, bounded by something other than a number, hyphen or slash\n" +
			"\n" +
			"4. Date of birth search matches:\n" +
			"     a. the words 'birth', 'born', 'DPOB', 'PDOB' or 'DOB' within 6 words of a date.\n" +
			"     b. the word 'Date' within 1 to 6 words of 'Birth' or vice versa.\n" +
			"\n" +
			"5. Maiden name search matches:\n" +
			"     a. The phrase 'Mother's maiden name' with or without the apostrophe and the 's'\n" +
			"     b. The word 'nee'\n" +
			"\n" +
			"6. Alien Registration Number matches:\n" +
			"     a. matches either a word boundary or the start of a line, then a capital A or lowercase a, \n" +
			"     then an optional dash, then either 7, 8, or 9 numbers in a row, with any amount of other dashes, spaces, or dots \n" +
			"     in between, followed by a word boundary or the end of the line.\n" +
			"\n" +
			"7. Grand Jury matches:\n" +
			"     a. matches either a word boundary or the start of a line, then the words grand jury, \n" +
			"     with any capitalization \n" +
			"\n" +

            "8. FBI Information Files matches:\n" +
			"     a. matches the numbers 134, 137, or 170, followed by a dash (-), followed by any amount of numeric \n" +
			"     characters \n" +
			"\n" +

            "9. FBI Sources matches:\n" +
			"     a. matches either a word boundary or the start of a line, then the words protect identity, \n" +
			"     informant, psi, si, reliable, or confidential, followed by a word boundary, \n" +
            "     with any capitalization \n" +
			"\n" +

            "10. FBI Source Codes matches:\n" +
			"     a. matches either a word boundary or the start of a line, then the abbreviations, \n" +
            "     AL, AQ, AX, AN, AT, BA, BH, BS, BQ, BU, BT, CE, CG, CI, CV, CO, DL, DN, DE, EP, HN, HO, IP, JN, JK, KC, \n" +
            "     KX, LV, LR, LA, LS, ME, MM, MI, MP, MO, NK, NH, NO, NR, NY, NF, OC, OM, PH, PX, PG, PD, RH, SC, SL, SU, \n" +
            "     SA, SD, SF, SJ, SV, SE, SI, TP, WFO, BER, BOG, BON, HON, LON, MAN, MEX, OTT, PAN, PAR, ROM, or TOK, \n" +
			"     followed by a space or dash, and between 1 and 5 numbers. All capitalization \n" +
			"\n" +


			"NOTES:\n" +
			"This PIIFinder does not search for Social Security Numbers which follow the new formatting rules introduced in 2011.\n";
	}
	
	public static String showHelp () {
		return helpStr;
	}
}