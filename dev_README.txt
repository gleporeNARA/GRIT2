File:   Dev_README.txt
About:  A Readme file meant for developers working on the project.

Note:   Feel free to add to/re-write this file as new info.


/******************* Update Tika / Dependencies ********************
For Intellij IntelliJ IDEA 2017.3.4 (Ultimate Edition) on Windows 7

1. Navigate to the "dependencies" folder, in the root "NARA-GRIT" folder
2. Copy new .jar files into this folder or delete old ones
3. To manage dependencies directly, click "File > Project Structure > Libraries"



/********************* Compile New .jar file **********************
For Intellij IntelliJ IDEA 2017.3.4 (Ultimate Edition) on Windows 7

If previously configured:
1. click Build > "Build Artifacts"

From scratch:
1. Click "File > Project Structure > Artifacts"
2. Click green plus sign near top left
3. Select "JAR > From modules with dependencies..."
4. Select main class (in "NARA-GRIT\src\grit\main")
5. Make sure "Extract to the target .jar" is selected (default)
6. Click OK
7. Check off "Include in project Build" or "Build on Make" to
   automatically make the .jar file when you click "Build Project"
8. Click Build > Build Artifacts to generate the new .jar file.
9. NARA-GRIT.jar is located in NARA-GRIT\out\artifacts\NARA_GRIT_jar\
   unless another output directory was specified in "Project Settings > Artifacts"