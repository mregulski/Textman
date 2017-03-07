package ppt.reshi.textman;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final int GUESSES_INITIAL = 11;

    private LinearLayout mLettersContainer;
    private EditText mLetterInput;
    private ArrayList<TextView> mLetters;
    private TextView mGuessesCounter;

    private int mWordCount;
    private Random mRng;

    private int mGuessesLeft;
    private int mLettersCorrect;
    private CharSequence mWord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mLettersContainer = (LinearLayout) findViewById(R.id.ll_letters);
        mGuessesCounter = (TextView) findViewById(R.id.tv_guesses_label);
        mLetterInput = (EditText) findViewById(R.id.et_pick);
//        mLetterInput.addTextChangedListener(new LetterChecker());
        mWordCount = calculateWordCount();
        mRng = new Random();
        startNewGame();
    }

    private void startNewGame() {
        Resources res = getResources();
        mGuessesLeft = GUESSES_INITIAL;
        mLettersCorrect = 0;
        mWord = pickRandomWord();
        mLettersContainer.removeAllViews();
        mLetters = new ArrayList<>(mWord.length());
        mGuessesCounter.setText(res.getString(R.string.label_guesses, mGuessesLeft));
        for (int i = 0; i < mWord.length(); i++) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            TextView letterView = (TextView) inflater.inflate(R.layout.game_letter_view, null);
            mLettersContainer.addView(letterView);
            mLetters.add(letterView);
        }
    }

    /**
     * Confirm button's clik handler.
     * @param view
     */
    public void checkLetter(View view) {
        if (mLetterInput.getText().length() > 0) {
            checkLetter(mLetterInput.getText().charAt(0));
            mLetterInput.setText("");
        } else {
            Log.d("checkLetter", "No letter to check.");
        }
    }

    /**
     * Check if letter exists in the selected word and update UI accordingly.
     * @param letter - character to check
     */
    private void checkLetter(char letter) {
        boolean isCorrect = false;
        for (int i = 0; i < mWord.length(); i++) {
            if (mWord.charAt(i) == Character.toUpperCase(letter)) {
                mLetters.get(i).setText(String.valueOf(letter));
                isCorrect = true;
                mLettersCorrect++;
            }
        }
        Log.d("checkLetter", "checking " + letter + ": " + (isCorrect ? "correct" : "incorrect"));
        Log.d("checkLetter", "correct: " + mLettersCorrect + "/" + mWord.length());
        if (!isCorrect) {
            guessIncorect();
        }
        if (mLettersCorrect == mWord.length()) {
            Toast.makeText(this, "You won!", Toast.LENGTH_SHORT).show();
        }
    }

    private void guessIncorect() {
        Resources res = getResources();
        mGuessesLeft --;
        mGuessesCounter.setText(res.getString(R.string.label_guesses, mGuessesLeft));
        if (mGuessesLeft <= 0) {
            // loss
            startNewGame();
        }
    }

    private int calculateWordCount() {
        Resources res = this.getResources();
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
            // TODO: deal with it
        }
        return wordCount;
    }


    private CharSequence pickRandomWord() {
        Resources res = this.getResources();
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
            // TODO: deal with it
            word = null;
        }
        return word;

    }

//    private class LetterChecker implements TextWatcher {
//        private boolean mCleared = false;
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            /* do nothing */
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            /* do nothing */
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            if (s.length() <= 0) {
//                return;
//            }
//            if (mCleared) {
//                mCleared = false;
//                return;
//            }
//            mCleared = true;
//            checkLetter(s.charAt(0));
//            mLetterInput.setText("");
//
//
//        }
//    }
}
