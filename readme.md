# Readability Score Analysis

## About

Everyone has their own personal reading history, and as we grow up, we are able to comprehend more and more complicated texts. But how do you estimate the level of difficulty of a given text, and how do you teach a computer to do that? In this project, you will find it out: write a program that determines how difficult the text is and for which age it is most suitable.

## Description
In this stage, you should implement various other scientific approaches to calculate a readability score.

Take a look at different ages and corresponding scores in the table in this article. This table is suitable for all the algorithms described in this stage. To calculate the age use the upper bound of the range. For example, if the range is 12-13 year olds then it's upper bound is 13 year olds.

The first algorithm is Flesch–Kincaid readability tests. First, you need to create a method that calculates a number of syllables in a word. The formula is given below. You can find more information here. You can use the second formula to calculate the index; it allows you to easily calculate the age of a person using the same table from the Automated Readability Index.

The second one is SMOG index. It stands for Simple Measure of Gobbledygook. To calculate it, you need to count the number of polysyllables which is the number of words with more than 2 syllables. The formula is shown below. You can find out more here. The Wikipedia page says that at least 30 sentences are required for this index to work properly. Don't pay attention to this, just keep it in mind when you use this index in real life. As in the previous example, the grade level is calculated here, so to get the age of a person you need to use the table from the first link.

The next one is Coleman–Liau index. The formula is given below. For more information read this. L is the average number of characters per 100 words and S is the average number of sentences per 100 words. Like all other indices, the output is a person's grade level. Like all other indices, the result is a minimum grade level required to understand this text.

So, in this stage, you should program all three approaches. Don't forget about the Automated readability index! Also, there should be an option to choose all methods at the same time.

To count the number of syllables you should use letters a, e, i, o, u, y as vowels. You can see here examples and intricacies with determining vowels in a word with 100% accuracy. So, let's use the following 4 rules:

1. Count the number of vowels in the word.
2. Do not count double-vowels (for example, "rain" has 2 vowels but only 1 syllable).
3. If the last letter in the word is 'e' do not count it as a vowel (for example, "side" has 1 syllable).
4. If at the end it turns out that the word contains 0 vowels, then consider this word as a 1-syllable one.
