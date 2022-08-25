# Words

This is a project by Brandon Faunce and Paola Frunzio created as our final project for Topics in Programming in 2022.
About 400 words and their synonyms were scraped from BigHugeThesaurus' API.
The frequency of the words was taken from Rachael Tatman's database: https://www.kaggle.com/datasets/rtatman/english-word-frequency.
Individual words were plotted using an algorithm that clustered them to minimize distance between nodes, so synonyms should (on average) be closer together.

To run .jar, run java -jar Words.jar in the command line.
To run source code, run src/main/Launcher.java in a java JRE.

Words are connected to their immediate synonyms.
