File:   Dev_README.txt
About:  A Readme file meant for developers working on the project.

Note:   Feel free to add to/re-write this file as new info.


Contents
---------
	I.	Java 8 Setup
	II.	Intellij Setup
	III.	Jar / Artifact Configuration
	IV.	Build New Jar
	V.	Update Tika App

I.	Java Setup
----------------
	have java8 jdk 1.8.0_xxx or higher installed.

II.	Intellj Setup
------------------
	1.	Git > checkout from version Control
		-- or --
		File > New > Project from Version Control
		
	2.	Checkout from GitHub
	
	3.	Copy URL
		3.A 	If wanted, rename your "directory name" and "Parent Directory"
			if you don't like the default
			
	4.	Click Clone
	
	5.	Select "Yes" to create an IntelliJ IDEA Project
	
	6.	Select "Next" on the Import Project Window
		6.A	Leave "Create Project From existing sources" checked off
		
	7.	Click "Next" to continue creating project
		7.A 	Modify project name & location if you prefer
		7.B	Leave Project format as ".idea (directory based)"
		
	8.	Leave the box checked to import found source files and click "Next"
			The path should be something like "<directory you chose>\<your project name>\src"

	9.	Click "Next" to import the Tika Jar.
	
	10.	Leave checkbox checked to import found modules, click "Next"
	
	11.	Ensure JDK 1.8 is selected as the project SDK
		NOTE:	Java 8 is required for Tika 1.18, if you don't have it installed
				see section I of this Readme.
				
	12.	"No Frameworks detected" should be displayed, just click "finish"
	
	13.	To run project, right click on "\src\grit\Main.java" and click "Run"
	
III.	Jar / Artifact Configuration
---------------------------------
	1.	File > Project Structure
	2.	Select "Artifacts" in the left side bar
	3.	Click the green plus sign near top left
	4.	Jar > From Modules with dependencies
	5.	Select Main.java as the main class and click "next"
		5.a	The meta-inf should be placed by default in the src folder
	6.	Ensure "Extracted tika-app-1.18.jar" shows up under OutputLayout
		6.a	if not click the green plus sign with drop down arrow
		6.b	select Extracted Directory
		6.c	navigate to "<project source>\dependencies" and select the tika app
	7.	Click OK
	
IV.	Build new Jar
------------------
	1.	with step III completed, click Build > Build Artifacts > Build
	2.	new jar file will be in out/artifacts/<your proj name>/<proj name>.jar
		2.a	default will be something like /out/artifacts/grit/grit.jar
		
V.	Update Tika Dependencies
-----------------------------
	1. 	Navigate to the "dependencies" folder, in the root "NARA-GRIT" folder
	2. 	Copy new .jar files into this folder or delete old ones
		2.B		This allows for Intellij to give you intellesense and compile
				the java app for debugging
	3.	Click File > Project Structure
	4.	On left side menu, click "Artifacts"
	5.	Right click on "Extracted tika app 1.xx" and remove it
	6.	Click above on the green plus sign with a drop down arrow
	7.	Select "extracted directory"
	8.	Navigate to where the Grit\dependencies folder is and select
		the new Tika.jar
