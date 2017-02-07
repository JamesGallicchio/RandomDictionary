package com.thatredhead.randwords;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class RandomDictionary {

    public static void main(String[] args) {
        List<String> token = null;
        try {
            token = Files.readAllLines(Paths.get("twittertoken.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Twitter t = new TwitterFactory(
                        new ConfigurationBuilder()
                                .setOAuthConsumerKey(token.get(0))
                                .setOAuthConsumerSecret(token.get(1))
                                .setOAuthAccessToken(token.get(2))
                                .setOAuthAccessTokenSecret(token.get(3))
                                .build())
                        .getInstance();

        while(true) {
            try {
                long wait = 3600000 - LocalTime.now().toNanoOfDay()/1000000 % 3600000;
                System.out.println("Waiting for next hour (" + wait/60000.0 + " minutes)");
                Thread.sleep(wait);
                System.out.println("Sending new tweet!");
                System.out.println(token);
                t.updateStatus(WordMaker.getRandomWord());
            } catch (IllegalArgumentException e){

            } catch (TwitterException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class WordMaker {

    static final double FIRST_LETTER_CONSONANT = 0.6;

    static final double SYLL_PREFIX = 0.9;

    static final double SYLL_SUFFIX = 0.3;

    static final double Y_AS_VOWEL = 0.05;

    static final double DOUBLE_VOWELS = 0.1;


    public static void main(String[] args) {
        for (int i = 0; i < 30; i++)
            System.out.println("\t" + getRandomWord());
    }

    public static String getRandomWord() {

        String word = new String();

        word += getRandomSyllable(rand(FIRST_LETTER_CONSONANT), rand(Y_AS_VOWEL), rand(SYLL_SUFFIX));


        for (int i = 0; i < randInt(5); i++)
            word += getRandomSyllable(rand(SYLL_PREFIX), true, rand(SYLL_SUFFIX));

        return word.length() < 4 ? getRandomWord() : word;
    }

    public static String getRandomSyllable(boolean includePrefix, boolean includeY, boolean includeSuffix) {

        String result = new String();

        if (includePrefix) result += getRandomConsonant();

        result += getRandomVowel(includeY);

        if (includeSuffix) result += getRandomConsonant();

        return result;

    }


    private static final String[] consonants = {"b", "c", "ch", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "ph", "qu", "r", "s", "sh", "t", "th", "v", "w", "x", "y", "z"};

    public static String getRandomConsonant() {

        return consonants[randInt(25)];

    }


    private static final String[] vowels = {"a", "e", "i", "o", "u", "ea", "ae", "oo", "ou"};

    public static String getRandomVowel(boolean includeY) {

        return includeY && rand(Y_AS_VOWEL) ? "y" : rand(DOUBLE_VOWELS) ? vowels[randInt(9)] : vowels[randInt(5)];

    }

    public static int randInt(int cap) {
        return (int) (Math.random() * cap);
    }

    public static boolean rand(double chance) {

        return Math.random() < chance;

    }
}
