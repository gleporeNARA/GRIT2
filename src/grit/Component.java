package grit;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/** Component Class
 * Originally codes has redundancy due to increased search elements, the purpose of this wrapper class is
 * to redundancy by grouping related data elements and achieve data persistency for immutable
 * data such as strings and integers, as a result data members of class are meant to be accessed directly
 * without encapsulation implemented.
 * The constructor takes in four arguments to create the object and and initializes all related data members
 * See constructor JavaDoc for specific input parameters.
 */
public class Component {
	final char TYPE;	//C = check box, T = text box
	final String SYM;
	final String LABEL;
	JCheckBox checkBox;
	JTextArea text;
	int counter;
	StringBuilder html;
	StringBuilder csv;

	List<Pattern> regex;
	ArrayList<Match> resultList;
	HashSet<Match> resultListUnique;
	ArrayList <Match> resultListUniqueFinal;

	/** Constructor
	 * This constructor takes in four arguments to create the object and and initializes all related data members
	 * @param type - character that specifies object will be check box or a text area, C = check box, T = text area
	 * @param sym - the symbol use to represent this object, can be use as web links or for web links label
	 * @param label - the label that will be displayed next to the check box in the java GUI application
	 * @param tip - the tool tip text that will be displayed when the user hover mouse cursor over this GUI element
	 */
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

		regex = new ArrayList<>();
		resultList = new ArrayList<>();
		resultListUnique = new HashSet<>();
		resultListUniqueFinal = new ArrayList<>();

		initValues ();
		clrExport ();
	}

	void initValues () {
		counter = 0;
	/*
		// this is handled in the action listener now.  When the run button is hit
		// the buildTextRegexList() function is used to overwrite the regex list of
		// any Components.  If the list is not cleared/overwritten, it keeps old regex patterns

		if (this.TYPE == 'T')		//we only want to clear the user input regex content of the text box,
			regex.clear ();			//all other regex contents should remain intact after each search
	*/
		resultList.clear ();
		resultListUnique.clear ();
		resultListUniqueFinal.clear ();
	}

	void clrExport () {
		html = new StringBuilder ();
		csv = new StringBuilder ();
	}

	boolean isActive () {
		if (TYPE == 'T') {
			return !text.getText().isEmpty();
		}
		else if (TYPE == 'C') {
			return checkBox.isSelected();
		}
		else
			return false;
	}
}