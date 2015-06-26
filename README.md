# Quality Printer
Quality Printer provides a process to check if the 3D printing is accurate.
# Setup
The setup described is for printing a new object. If you are printing an object you have already printed before, you can skip this section.
### Slic3r
Download the [Printrun: 3D Printing Host Suite](http://www.pronterface.com/). Run Slic3r and load the .stl file you wish to print. Once loaded, navigate to "File->Slice to SVG" then select the .stl file you are printing and press "Open" then "Save" to create the SVG file. Next, to create a G-code file that pronterface will use to print the object, click on the "Export G-code" button. One you have saved the .stl as a SVG file and a G-code file, you are done with Slic3r.
* Open Slic3r and load .stl file
* ```File->Slice to SVG``` and save SVG
* Click the ```Export G-code``` button


### SVG to JPG
In order to see if what we are printing is right, we need to have a 2D image to compare it to. Using the SVG to JPG project included, convert the SVG file you made using Slic3r to a JPG image. Simply put the SVG file in the project path and run the program and the new JPG file will be saved in the project path. This uses [Apache Batik](https://xmlgraphics.apache.org/batik/using/transcoder.html) to convert the file.

### Android App
Once you have the JPG file, you will need to add it to the Android project so that you can select it as the item you are printing. Add the JPG file to the "drawable" folder, and then you will need to make some additions in the code (listed below) in order to be able to choose the item in-app. All of these should be added to the existing control statements that involve other icons. To display the images I used [EcoGallery](https://github.com/falnatsheh/EcoGallery)

In the onCreate method of **ImageDialog.java** add:
```Java
else if(imageNumber.equals("yourIconTextName"))
            Picasso.with(this).load(R.drawable.yourFile).into(mDialog);
```
In the startMethod of **CameraFragment.java** add:
```Java
else if(icon.equals("yourIconTextName")){
    Bitmap layer1 = BitmapFactory.decodeResource(getResources(), R.drawable.yourFile);
    layer = layer1.copy(Bitmap.Config.ARGB_8888, true);
    layer1.recycle();
}
```
This should be added in this method twice; once under
```Java
if(method.equals("subtraction"))
``` 
and once under 
```Java 
else if(method.equals("analysis"))
```
In the getView method of the **ImageAdapter** class at the bottom of the **SetupFragment.java** file add:
```Java
case x: Picasso.with(context).load(R.drawable.yourFile).resize(200,200).centerCrop().into(view);
            break;
```
You will need to update the number of positions in this same class
In the changeText() method of **EcoGallery.java** (in a different folder than the main project files) add:
```Java
else if(getPosition()==x)
            icon.setText("yourIconTextName");
```
In the changeText(int i) method of **EcoGallery.java** add:
```Java
else if(i==x)
            icon.setText("yourIconTextName");
```
In all cases above, "yourIconTextName" is what the icon label will be, "R.drawable.yourFile" is the file you added to the "drawables" folder and "x" is the next position (typically number of icon files you have minus 1 for newly added files)

### Pronterface
Open [Pronterface](www.pronterface.com) and navigate to ```Settings->Macros->New```. Name this first macro "blank_picture". It will be used to move the printer bed into position to take the picture of just the printer bed that you will subtract from the image with the printed object. In this macro put the single line of code found in "blank.py". The escape character "!" is important when writing a macro in python in this program.
```python
!self.onecmd('G0 X200 Y250 Z190 F2000')
```
Next, navigate to ```Settings->Macros->New``` again and create a macro named "quality_print". This macro will be used to detect when the printer has completed a layer and begin to run the analysis. In this macro put the lines of code found in "quality_print.py". The line that begins with "run_script" is a shell command and therefore does not need an "!". For this line you will need to add where the python script that tells the android device to run analysis is (ex: run_script python myDirectory). This python script is explained in the next section.
```python
!notRunBefore = True
!while(self.p.printing and notRunBefore):
     !if (self.curlayer>0.350 and notRunBefore):
          !self.update_pos()
          !currentPositionX = self.current_pos[0]
          !currentPositionY = self.current_pos[1]
          !currentPositionZ = self.current_pos[2]
          !self.pause() 
          !self.onecmd('G0 X200 Y250 Z190 F2000')
          !time.sleep(15)
          run_script python #insert directory where the parse python script is located (parsetest directory)
          !self.pause()
          !notRunBefore = False
!print "Continuing Print"
```
Finally, create two buttons using the "+" sign. Name these "Blank Image Setting" and "Quality Print" and set their command to the two macros you just created (blank_picture and quality_print respectively). The "Quality Print button will not work until you set up the parsetest which is explained next.
### parsetest
The folder named "parsetest" contains the python script necessary to tell the android device to begin analyzing what has been printed so far. To write this program, I used [ParsePy](https://github.com/dgrtwo/ParsePy) to connect with parse. You may need to install this to your Python using the command:

```pip install https://github.com/dgrtwo/ParsePy/archive/master.zip```

Once you have parsetest on your computer, fill in where the directory is in the run_script command mentioned above. This will be something like "C:/Users/parsetest". In order for this script to run, you must tell it that it is a python script (run_script *python*), and you may have to put the [python](https://www.python.org/downloads/release/python-343/) application in the printrun folder where pronterface is located.

##Running the Program
Now that you have everything set up, you can begin printing with Quality Analysis.
* Add the "G-code" file to Pronterface. In the console, the min and max x and y coordinates will appear
* Start the Android app and enter these coordinates in the EditText boxes
* In the Android app, select how many pixels you want to add to the outside of icon (Error Buffer)
* Select the item you are printing. An image is selected when the TextView has its name displayed
* Click on "Subtraction" or "Analysis"
* Press your "Blank Image Setting" button on Proterface
* When the printer is done moving, set up the Android device on the stand such that the printer bed's border matches where the white rectangle is on the Camera View
* If you selected "Subtraction" click "Blank" on the Android screen to take a before picture of the printer bed
* Click "Start" on the Android screen. It is now waiting until the first layer has been printed
* In pronterface, press "Print"
* Once the device has begun printing the first layer, press "Quality Print"
* The printer will now pause after the first layer and analyze what has been printed and report the error back.
