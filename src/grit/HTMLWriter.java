package grit;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class HTMLWriter 
{
    private static final String NL = "\n";
        
    // default constructor
    public HTMLWriter()
    {

    }

    public String addOpenHTMLTag()
    {
        return "<html>" + NL;
    }

    public String addCloseHTMLTag()
    {
        return "</html>" + NL;
    }

    public String addOpenTableTag(String tableID)
    {
        return "<table id=" + tableID + ">" + NL;
    }

    public String addCloseTableTag()
    {
        return "</table>" + NL;
    }
	
    public String addOpenPanelTag()
    {
        return "<div class='panel'>" + NL;
    }
    public String addClosePanelTag()
    {
        return "</div>" + NL;
    }
    public String addOpenNavTag()
    {
        return "<nav class='main_nav'>" + NL;
    }
    public String addCloseNavTag()
    {
        return "</nav>" + NL;
    }
    public String addOpenNavULTag()
    {
        return "<ul>" + NL;
    }
    public String addCloseNavULTag()
    {
        return "</ul>" + NL;
    }
    public String addOpenNavLITag()
    {
        return "<li>" + NL;
    }
    public String addCloseNavLITag()
    {
        return "</li>" + NL;
    }
    public String addOpenCenterTag()
    {
        return "<center>" + NL;
    }

    public String addCloseCenterTag()
    {
        return "</center>" + NL;
    }
	
    public String addTableHeader()
    {
        String header = "";
        header += "<tr>" + NL;
        header += "	<th>Match</th>" + NL;
        header += "	<th>Confidence</th>" + NL;
        header += "	<th>Text</th>" + NL;
        header += "	<th>Full Line</th>" + NL;
        header += "	<th>Type</th>" + NL;
        header += "	<th>File</th>" + NL;
        header += "	<th>Line</th>" + NL;
        header += "</tr>" + NL;

        return header;
    }
	
    public String addTableRow(int matchNum, String matchMode, String matchText, String fullLine, String fileExtension, File filePath, int lineNum)
    {
        String lineStripped = fullLine.replace("&lt;", " ").replace("&gt;", " ");
        lineStripped = fullLine.replace("<", " ").replace(">", " ");

        String row = "";
        row += "<tr>" + NL;
        row += "	<td>" + matchNum + "</td>" + NL;
        row += "	<td>" + matchMode + "</td>" + NL;
        row += "	<td>" + matchText + "</td>" + NL;
        row += "	<td>" + lineStripped + "</td>" + NL;
        row += "	<td>" + fileExtension + "</td>" + NL;
        row += "	<td> <a href=\"file:///" + filePath + "\">" + filePath + "</a> </td>" + NL;
        row += "	<td>" + lineNum + "</td>" + NL;
        row += "</tr>" + NL;

        return row;
    }
    
    public String addAltTableHeader()
    {
        String header = "";
        header += "<tr>" + NL;
        header += "	<th>File Name</th>" + NL;
        header += "</tr>" + NL;

        return header;
    }
    
    public String addAltTableRow(String filePath)
    {
        String row = "";
        row += "<tr>" + NL;
        row += "	<td> <a href=\"file:///" + filePath + "\">" + filePath + "</a> </td>" + NL;
        row += "</tr>" + NL;

        return row;
    }
	
    public String addResultTitle(Date timeStamp)
    {
        String title = "";
        title += "<center><h3>PII Search Results on " + timeStamp + "</h3></center>";

        return title;
    }
	
    public String addResultNote(int unreadFiles, int fileCounter, int totalFiles, int foundCounter, String elapsedTime)
    {
        String note = "";
        note += "<center>";
        note += "<nav class='sec_nav'>";
        note += "<ul>";
        note += "<li><span class='counter' style='color:red;'>"+unreadFiles +"</span><br/><a href='#skippedResults'>Unreadable files</a></li>";
        note += "<li><span class='counter'>"+fileCounter + " / " + totalFiles+"</span><br/>Readable files</li>";
        note += "<li><span class='counter'>"+foundCounter+"</span><br/>Matches Found</li>";
        note += "<li><span class='counter'>"+elapsedTime+"</span><br/>Elapsed time</li>";
        note += "</ul>";
        note += "</nav>";
        note += "</center>" + NL;
        return note;
    }
    
    public String addExtNote(ExtensionCounter extCounter)
    {
        String note = "";
        note += "<center>";
        note += "<div class='panel_sm'>";
        note += "<h3>File types read<h3>";
        note += "<table>"+
                "<tr>" +
                    "<th>Extension</th>" +
                    "<th>Count</th>" +
                "</tr>"+extCounter.toHTML()+
                "</table>";
        note += "</div>";
        note += "</center>" + NL;

        return note;
    }
	
    public String addAnchorTopLink(String id, String anchorText)
    {
        String anchorTopLink = "";
        anchorTopLink += "<a id='" + id + "'><h1><center>" + anchorText + "</center></h1></a>";

        return anchorTopLink;
    }

    public String addAnchorLink(String id, String anchorText)
    {
        String anchorLink = "";
        anchorLink += "<a id='" + id + "'><h3><center>" + anchorText + "</center></h3></a>"; 

        return anchorLink;
    }
    public String addCounter(int counter)
    {
        String counterText = "";
        counterText += "<span class='counter'>"+counter+"</span><br/>";

        return counterText;
    }
        
    public String addTextLink(String id, String linkText)
    {
        String textLink = "";
        textLink +=  "<a href='#" + id + "'>" + linkText + "</a>";

        return textLink;
    }
        
    public String addBackToTopLink(String id, String linkText)
    {
        String textLink = "";
        textLink +=  "<a class='backToTop' href='#" + id + "'>" + linkText + "</a>";

        return textLink;
    }

    public String addStyleSection()
    {
        String style = "";
        style += "<head>" + NL;
        style += "<style>" + NL;
        style += "html { font-family: Arial; width: 100%; }" + NL;
        style += "a:link { color: #045FB4; }" + NL;
        style += "h1 { color: #2E9AFE; }" + NL;
        style += ".main_nav ul, .sec_nav ul { width: 90%; display: inline-block; list-style: none; }" + NL;
        style += ".main_nav li { width: 12%; display: inline-block; padding: 10px; margin-right: 7px; border: 1px solid #9C9A9A; border-radius: 5px;}" + NL;
        style += ".sec_nav li { width: 20%; display: inline-block; padding: 10px; margin-right: 10px; border: 1px solid #9C9A9A; border-radius: 5px;}" + NL;
        style += ".main_nav li .counter { font-size: 32px; }" + NL;
        style += ".sec_nav li .counter { font-size: 26px; }" + NL;
        style += "table { width: 100%;border-collapse: collapse;}" + NL;
        style += "th, td { text-align: left;padding: 8px; word-wrap: break-word; }" + NL;
        style += "tr:hover, tr:hover:nth-child(even) { background-color: #F7F8E0;}" + NL;
        style += "tr:nth-child(even) { background-color: #f2f2f2;}" + NL;
        style += "th { background-color: #E0F8E6;color: black;}" + NL;
        style += ".panel { display: block;padding: 10px;margin: 10px;border: 1px solid rgba(0,0,0,0.3);border-radius: 5px;}" + NL;
        style += ".panel_sm { width:25%;display: block;padding: 5px;margin: 10px;border: 1px solid rgba(0,0,0,0.3);border-radius: 5px;}" + NL;
        style += ".backToTop { display: block;text-align: right;margin: 10px 0 5px 0; }" + NL;
        style += "</style>" + NL;
        style += "</head>" + NL;

        return style;
    }
}

