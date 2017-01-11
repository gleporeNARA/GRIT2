# NARA-GRIT
Application for Generalized Retriever of Information Tool

The tool is designed to search through a large number of
file formats and identify various types of sensitive information.
Currently the program has complete search programming for: Social
Security Numbers, Dates of Birth, Mother's Maiden Name, Place of
Birth, Alien Registration Numbers, and various FBI-specific search terms.

The program allows a user to search through a directory of files, or
just in a single file. By default the program searches through the
following formats: text, docx, doc, xlsx, xls, msg, htm, html, rtf,
mbox, pst, mdb, and pdf. There is a checkbox for "Read Additional
Formats" which allows Apache Tika to try and access whatever it can
from other file types.

The program allows the user to export the results of the search as
either an HTML file or a CSV file.

There is also a free text field, which should allow the user to input
a series of search terms (delimited by commas or the
pipe (|)) character. This functionality is currently mostly
implemented.

The sent_mail.zip file contains sample files which contain instances of all current search terms, including Social Security Numbers, and all FBI search terms.
