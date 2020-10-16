package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please type the file name in parameters.");
			return;
		}
		Text text = new Text(Paths.get(args[0]));

		System.out.println("The text is:");
		System.out.println(text.getString() + "\n");
		System.out.println("Words: " + text.getWords());
		System.out.println("Sentences: " + text.getSentences());
		System.out.println("Characters: " + text.getChars());
		System.out.println("Syllables: " + text.getSyllables());
		System.out.println("Polysyllables: " + text.getPolysyllables());
		System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");

		String mode = new Scanner(System.in).nextLine();

		System.out.println();
		switch (mode) {
			case "ARI":
				text.printARI();
				break;
			case "FK":
				text.printFK();
				break;
			case "SMOG":
				text.printSMOG();
				break;
			case "CL":
				text.printCL();
				break;
			case "all":
				text.printARI();
				text.printFK();
				text.printSMOG();
				text.printCL();
				System.out.printf("%nThis text should be understood in average by %.2f year olds.",
						(text.getAgeForScore(text.getAriScore()) +
								text.getAgeForScore(text.getFkScore()) +
								text.getAgeForScore(text.getSmogScore()) +
								text.getAgeForScore(text.getClScore())) / 4.0f);
				break;
			default:
				System.out.println("You enter wrong command. Please use: ARI, FK, SMOG, CL, all.");
				return;
		}
	}
}

class Text {
	public static final String VOWEL_CHARS = "aeiouyAEIOUY";
	public static final int[] SCORE_LEVELS = {6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24, 99};

	private String string;
	private int words;
	private int sentences;
	private int chars;
	private int syllables;
	private int polysyllables;
	private float avgCharsPer100Words;
	private float avgSentsPer100Words;
	float ariScore;
	float fkScore;
	float smogScore;
	float clScore;
	private boolean calculated;

	Text(String text) {
		this.string = text;
		calcTextStats();
	}

	Text(Path textPath) {
		try {
			this.string = new String(Files.readAllBytes(textPath));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		calcTextStats();
	}

	public String getString() {
		return string;
	}

	public void setString(String text) {
		this.string = text;
		words = sentences = chars = syllables = polysyllables = 0;
		avgCharsPer100Words = avgSentsPer100Words = 0.0f;
		ariScore = fkScore = smogScore = clScore = 0.0f;
		calculated = false;
		calcTextStats();
	}

	public int getWords() {
		return words;
	}

	public int getSentences() {
		return sentences;
	}

	public int getChars() {
		return chars;
	}

	public int getSyllables() {
		return syllables;
	}

	public int getPolysyllables() {
		return polysyllables;
	}

	public float getAriScore() {
		return ariScore;
	}

	public float getFkScore() {
		return fkScore;
	}

	public float getSmogScore() {
		return smogScore;
	}

	public float getClScore() {
		return clScore;
	}

	private void calcTextStats() {
		if (calculated) {
			return;
		}

		int numOfWords = 0;
		int numOfChars = 0;
		int numOfSent = string.length() > 0 ? 1 : 0;
		int numOfSyllables = 0;
		int numOfPolysyllables = 0;
		boolean firstWord = false;

		for (String word : string.split(" ")) {
			numOfWords++;

			if (word.matches("\\S+")) {
				numOfChars += word.length();
			}

			if (firstWord) {
				numOfSent++;
				firstWord = false;
			}

			if (word.matches("\\w+[.!?](\\s|$)")) {
				firstWord = true;
			}

			numOfSyllables += calcSyllablesInWord(word);
			numOfPolysyllables += calcSyllablesInWord(word) > 2 ? 1 : 0;
		}

		words = numOfWords;
		sentences = numOfSent;
		chars = numOfChars;
		syllables = numOfSyllables;
		polysyllables = numOfPolysyllables;
		avgCharsPer100Words = (float) chars / words * 100;
		avgSentsPer100Words = (float) sentences / words * 100;
		calcARI();
		calcCL();
		calcFK();
		calcSMOG();
		calculated = true;
	}

	/* This method calculates the number of vowel sounds in the word. But there are some rules:
	1. Count the number of vowels in the word.
	2. Do not count double-vowels (for example, "rain" has 2 vowels but only 1 syllable).
	3. If the last letter in the word is 'e' do not count it as a vowel (for example, "side" has 1 syllable).
	4. If at the end it turns out that the word contains 0 vowels, then consider this word as a 1-syllable one.
	 */
	public static int calcSyllablesInWord(String word) {
		int numOfSyllables = 0;
		char c;

		if (word == null) {
			return 0;
		}

		for (int i = 0; i < word.length(); i++) {
			c = word.charAt(i);

			// Count the number of vowels in the word.
			if (isVowel(c)) {
				numOfSyllables++;
			} else {
				continue;
			}

			// If current char is not the last one than check the next char. If it is a vowel too,
			// than decrease number of Syllables. If it's last char in the word, than check it on 'e'.
			if (i != word.length() - 1) {
				char nextC = word.charAt(i + 1);
				if (isVowel(nextC) && nextC != 'e') {
					numOfSyllables--;
				}
			} else {
				if (c == 'e') {
					numOfSyllables--;
				}
			}
		}

		return Math.max(1, numOfSyllables);
	}

	public static boolean isVowel(char c) {
		return String.valueOf(c).matches("[" + VOWEL_CHARS + "]");
	}

	public void printARI() {
		System.out.printf("Automated Readability Index: %.2f (about %d year olds).%n",
				ariScore, getAgeForScore(ariScore));
	}

	public void printFK() {
		System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d year olds).%n",
				fkScore, getAgeForScore(fkScore));
	}

	public void printSMOG() {
		System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d year olds).%n",
				smogScore, getAgeForScore(smogScore));
	}

	public void printCL() {
		System.out.printf("Coleman–Liau index: %.2f (about %d year olds).%n",
				clScore, getAgeForScore(clScore));
	}

	private void calcARI() {
		try {
			ariScore = 4.71f * chars / words + 0.5f * words / sentences - 21.43f;
		} catch (ArithmeticException e) {
			ariScore = 0;
		}
	}

	private void calcFK() {
		try {
			fkScore = 0.39f * words / sentences + 11.8f * syllables / words - 15.59f;
		} catch (ArithmeticException e) {
			fkScore = 0;
		}
	}

	private void calcSMOG() {
		try {
			smogScore =(float) (1.043 * Math.sqrt(polysyllables * 30.0 / sentences) + 3.1291);
		} catch (ArithmeticException e) {
			smogScore = 0;
		}
	}

	private void calcCL() {
		try {
			clScore = 0.0588f * avgCharsPer100Words - 0.296f * avgSentsPer100Words - 15.8f ;
		} catch (ArithmeticException e) {
			clScore = 0;
		}
	}

	public int getAgeForScore(float score) {
		return score > 0 ? SCORE_LEVELS[(int) (Math.min(14, Math.ceil(score)) - 1)] : 0;
	}
}
