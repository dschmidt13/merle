Merle Coat Generation
===

*Experiments on digital recreation of Merle coat patterns.*

This project algorithmically generates image patterns in an attempt to
investigate and simulate the process of Merle coat spotting in dogs. A JavaFX
GUI is provided that allows the user to select colors, tweak and experiment with
algorithm parameters, and track the rendering simulation in real time. Depending
on the settings selected, a wide variety of both Merle-seeming, non-merle, and
even clearly unnatural coat patterns may be generated.

Features
---

 - Simple user-friendly GUI
 - Real-time tracking of rendering algorithm progress
 - Predefined color palette of commonly occurring dog coat colors
 - Options for customizing eye and nose color
 - Ability to save generated image to a PNG file or copy it to the clipboard

Authors
---

 - David Schmidt
 - Johnnalyn Covey

Dependencies (Installed Locally)
---

 - Java SE 11+
 - Gradle
 - git

Running the Project
---
To run the project, run the following commands in a terminal:

    cd .../a_code_folder/
    git clone https://github.com/dschmidt13/merle.git merle
    cd merle
    gradle run

Debugging the Project
---
To debug the project in [Eclipse](https://www.eclipse.org/):

1. Import it into Eclipse
2. Use the "application -> run" Gradle Task (requires "Eclipse Buildship" plugin)
3. Wait for the message "Listening for transport dt_socket at address: 9099" in
the Console
4. Run the provided "Run Merle (Remote Debug Required)" launcher to connect to
the application in the Eclipse debugger.

Alternately, if using a different debugger, launch like so:

    gradle run -DDEBUG=true

and then connect to the waiting application, using the remote Java debugger of
your choice, via Port 9099.

License: Free-For-All (FFA)
---
This code and codebase may be used and propagated with no restrictions,
constraints, or stipulations. You may even lie and tell people it's your code,
although if they find this repository it's your problem (and may that happen
swiftly if you do)!
