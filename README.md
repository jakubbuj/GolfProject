0

 To modify the maze configuration, navigate to the MazeLayout.java file. In this file, you'll find various maze layouts. Simply    uncomment the desired layout and ensure that all other unused layouts are commented out. This will set the active maze configuration  for the game.

0.5  Getting Started 

   open file project-1.2-ken-17-main in ide and then to lunch application run the main class named DesktopLauncher.java

1. Main Menu Screen:

Before pressing "Play", press the "Settings" button at the bottom of the screen.

2. Settings Screen:

In the provided text fields, input the following variables (use a "." for decimal points, not ","):

 Initial Coordinates: Enter the initial X and Y coordinates of the golf ball.
 Friction Values:
    - Kinetic and tatic friction of the sand.
    - Kinetic and static friction of the grass.
 Target Details:
    - Enter the X and Y coordinates of the target.
    - Enter the radius of the target.
 Terrain Function:
    - Enter the function of the terrain. Ensure variables are in alphabetical order (first x, then y), and separate each symbol with a space (except negative values).
    - Example: sqrt ( ( sin ( 0.1 * x ) + cos ( 0.1 * y ) ) ^ 2 ) + 0.5 * sin ( 0.3 * x ) * cos ( 0.3 * y )
Optionally, you can use the "Default" button to set all variables to their default values.

After entering all values, click the "Submit" button. You will return to the Main Menu screen.

3. Options Screen:

Enter your name.
Select the preferred game mode (with or without a maze).
Press the "Submit and Play" button.

4. Game Control Screen:

To apply force to the white golf ball, press, hold, and release the space bar.
Press "Rule Based Bot Game" for the bot to take a shot.
Press "AI Shot" for the bot to take a hole-in-one shot.
Press "Star" for the Astar bot to solve to maze (click it only if the maze is visible)

Correspondences for Inputting Functions and Constants:
Constant e: E
Time: time
Square root: sqrt
Sine: sin
Cosine: cos
Natural log (base e): ln
Logarithm (base 10): log