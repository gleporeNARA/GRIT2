package grit;

import java.util.ArrayList;

public class ExtensionCounter 
{
    public ArrayList<String> extList;
    public ArrayList<Integer> extCount;
    public String noExt = "No Extension";

    public ExtensionCounter()
    {
        extList = new ArrayList<String>();
        extCount = new ArrayList<Integer>();
    }
	
    public void count(String ext)
    {
        if (!extList.contains(ext))
        {
            extList.add(ext);
            extCount.add(1);
        } else {
            int index = extList.indexOf(ext);
            int count = extCount.get(index);
            count++;
            extCount.remove(index);
            extCount.add(index, count);
        }
    }
	
    public String toString()
    {
        String str = "";
        for (String s : extList)
        {
            int i = extList.indexOf(s);
            int c = extCount.get(i);
            str += "\t" + s + " : " + c + "\n";
        }
        return str;
    }
	
    public String toHTML()
    {
        String str = "";

        for (String s : extList)
        {
            int i = extList.indexOf(s);
            int c = extCount.get(i);
            if (s.isEmpty())
            {
                s = noExt;
            }
            str += "<tr>" +
                        "<td>"+ s + "</td>" + 
                        "<td>"+ c + "</td>" +
                   "</tr>";
        }
        return str;
    }

}
