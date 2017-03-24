package ppt.reshi.textman;

import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Handles game's logic.
 */

public class GamePresenter {
    private final static String TAG = "GamePresenter";
    private static final int GUESSES_MAX = 11;

    private boolean mInProgress;
    private int mGuessesLeft;
    private int mLettersCorrect;
    private int mRoundScore;
    private int mTotalScore;

    private int mWordCount;
    private Random mRng;

    private CharSequence mWord;

    private GameActivity mGameView;

    public GamePresenter(GameActivity gameView) {
        mGameView = gameView;
        mWordCount = calculateWordCount();
        mRng = new Random();
        mTotalScore = 0;
    }

    public int getLetterCount() {
        return mWord.length();
    }

    public void startNewGame() {
        if (mInProgress) {
            mTotalScore -= 10;
            mGameView.setScore(mTotalScore);
        }
        mLettersCorrect = 0;
        mWord = pickRandomWord();
        Log.d(TAG, "word: " + mWord);
        mRoundScore = mWord.length() * 2;
        mGuessesLeft = getInitialGuesses();
        mInProgress = true;
        mGameView.resetBoard();
    }

    /**
     * Check if letter exists in the selected word and update UI accordingly.
     * @param letter - character to check
     */
    public boolean checkLetter(char letter) {
        boolean isCorrect = false;
        for (int i = 0; i < mWord.length(); i++) {
            if (mWord.charAt(i) == Character.toUpperCase(letter)) {
                mGameView.revealLetter(i, letter);
                isCorrect = true;
                mLettersCorrect++;
            }
        }
        Log.d("checkLetter", "checking " + letter + ": " + (isCorrect ? "correct" : "incorrect"));
        Log.d("checkLetter", "correct: " + mLettersCorrect + "/" + mWord.length());
        mGameView.mark(letter, isCorrect);
        if (isCorrect) {
            if (mLettersCorrect == mWord.length()) {
                mTotalScore += mRoundScore;
                mInProgress = false;
                mGameView.gameWon();
                mGameView.setScore(mTotalScore);
            }
        } else { // incorrect guess
            mGuessesLeft--;
            mRoundScore--;
            mGameView.setGuessesLeft(mGuessesLeft);
            if (mGuessesLeft <= 0) {
                mGameView.gameLost();
                revealSolution();
                mInProgress = false;

            }
        }
        return isCorrect;
    }

    private void revealSolution() {
        for (int i = 0; i < mWord.length(); i++) {
            mGameView.revealLetter(i, mWord.charAt(i));
        }
    }

    public int getInitialGuesses() {
        return GUESSES_MAX;
    }

    private CharSequence pickRandomWord() {
        Resources res = mGameView.getResources();
        CharSequence word;
        try (InputStream wordsFile = res.openRawResource(R.raw.words);
             InputStreamReader wordsReader = new InputStreamReader(wordsFile);
             BufferedReader words = new BufferedReader(wordsReader)) {
            int pick = mRng.nextInt(mWordCount);
            // skip all words until we reach the selected one
            for (int pos = 0; pos < pick; pos++) {
                words.readLine();
            }
            word = words.readLine();
        } catch (IOException ioe) {
            // TODO: deal with it, if it makes sense to do so
            word = null;
        }
        return word;
    }

    private int calculateWordCount() {
        Resources res = mGameView.getResources();
        int wordCount = 0;
        try (InputStream wordsFile = res.openRawResource(R.raw.words);
             InputStreamReader wordsReader = new InputStreamReader(wordsFile);
             BufferedReader words = new BufferedReader(wordsReader))
        {
            while (words.readLine() != null) {
                wordCount++;
            }
            words.close();
            wordsReader.close();
            wordsFile.close();
        } catch (IOException ioe) {
            Log.e("worddcount", ioe.getMessage(), ioe);
        }
        return wordCount;
    }

}
