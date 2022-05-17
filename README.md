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
 - git

Running the Project
---
To run the project, run the following commands in a terminal:

    cd .../a_code_folder/
    git clone https://github.com/dschmidt13/merle.git merle
    cd merle
    ./gradlew run

Debugging the Project
---
To debug the project in [Eclipse](https://www.eclipse.org/):

1. Import it into Eclipse: File -> Import... -> Existing Gradle Project -> Finish
2. Use the "application -> run" Gradle Task (requires "Eclipse Buildship" plugin)
3. Wait for the message "Listening for transport dt_socket at address: 9099" in
the Console
4. Run the provided "Run Merle (Remote Debug Required)" launcher to connect to
the application in the Eclipse debugger.

Alternately, if using a different debugger, launch like so:

    gradle run -DDEBUG=true

and then connect to the waiting application, using the remote Java debugger of
your choice, via Port 9099.

Troubleshooting
---
##### Unsupported class file major version 57
If this shows up in the Console or Problems view while building in Eclipse,
first check that you can build and run the project without modifications from
the command line outside Eclipse. If so, this is likely an issue caused by
Buildship, the Eclipse plugin that manages Gradle projects. It occurs when
Eclipse is itself executing in a later JVM than Java SE 11, such as Java SE 13.
To resolve it, close Eclipse, then go to your Eclipse installation directory and
modify the eclipse.ini file to point to the bin folder of a valid Java SE 11
installation, a la:

    ...
    -vm C:/Program Files/Java/openjdk-11/bin
    ...

License: Free-For-All (FFA)
---
This code and codebase may be used and propagated with no restrictions,
constraints, or stipulations. You may even lie and tell people it's your code,
although if they find this repository it's your problem (and may that happen
swiftly if you do)!
