# Words

This is a project by Brandon Faunce and Paola Frunzio created as our final project for Topics in Programming in 2022.
About 400 words and their synonyms were scraped from BigHugeThesaurus' API.
The frequency of the words was taken from Rachael Tatman's database: https://www.kaggle.com/datasets/rtatman/english-word-frequency.
Individual words were plotted using an algorithm that clustered them to minimize distance between nodes, so synonyms should (on average) be closer together.

To run .jar, run java -jar Words.jar in the command line.

To run source code, run src/main/Launcher.java in a java JRE.

Words are connected to their immediate synonyms.
Use the scroll wheel to zoom in and out, and Reset to reset the zoom.
Use the search bar at the top right to find a specific word, more popular words show up first.
Words are colored by their part of speech:
  * Red: Noun
  * Green: Verb
  * Blue: Adjective
  * Yellow: Adverb
