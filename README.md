Focus stacking using java
======

This is a simple algorithm of focus stacking using java and opencv libraries.
I used [this post](https://stackoverflow.com/questions/15911783/what-are-some-common-focus-stacking-algorithms) and [this repo](https://github.com/cmcguinness/focusstack) to create this algorithm.

How to use it ?
------

Just create a FocusStacking object with the path of the folder wich contains the input images. Then call fill() method to read the images from the folder, and call focus_stack() to launch the algorithm.

```
FocusStacking stack = new FocusStacking("/home/lucas/Images/testStacking/");
stack.fill();
stack.focus_stack();
```
