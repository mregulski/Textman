package ppt.reshi.textman;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final int GUESSES_INITIAL = 11;

    private LinearLayout mLettersContainer;
    private ArrayList<TextView> mLetters;
    private TextView mGuessesCounter;
    private GridLayout mKeypad;

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
        mKeypad = (GridLayout) findViewById(R.id.gl_keypad);
        findViewById(R.id.btn_new_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
        mWordCount = calculateWordCount();
        mRng = new Random();
        createKeypad();
        startNewGame();
    }

    private void createKeypad() {
        char[] alphabet = getAlphabet("pl");
        for (char c : alphabet) {
            Button button = new Button(this);
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) button.getLayoutParams();
            if (params == null) { params = new GridLayout.LayoutParams(); }
            params.width = (int) getResources().getDimension(R.dimen.btn_letter_width);
            button.setOnClickListener(new LetterListener());
            button.setText(String.valueOf(c));
            mKeypad.addView(button, params);
        }
    }

    private char[] getAlphabet(String language) {
        if (language.equals("pl")) {
            return "AĄBCĆDEĘFGHIJKLŁMNŃOÓPQRSŚTUVWXYZŹŻ".toCharArray();
        } else {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }


    private void startNewGame() {
        Resources res = getResources();
        mGuessesLeft = GUESSES_INITIAL;
        mLettersCorrect = 0;
        mWord = pickRandomWord();
        mLettersContainer.removeAllViews();
        mLetters = new ArrayList<>(mWord.length());
        mGuessesCounter.setText(res.getString(R.string.label_guesses, mGuessesLeft));
        resetKeypad();
        for (int i = 0; i < mWord.length(); i++) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            TextView letterView = (TextView) inflater.inflate(R.layout.game_letter_view, null);
            mLettersContainer.addView(letterView);
            mLetters.add(letterView);
        }
    }

    private void resetKeypad() {
        mKeypad.removeAllViews();
        createKeypad();
    }

    /**
     * Check if letter exists in the selected word and update UI accordingly.
     * @param letter - character to check
     */
    private boolean checkLetter(char letter) {
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

        return isCorrect;
    }

    private void guessCorrect(View v) {
        v.setBackgroundColor(getColor(R.color.letter_bg_ok));
        if (mLettersCorrect == mWord.length()) {
            Toast.makeText(this, "Super!", Toast.LENGTH_SHORT).show();
//            startNewGame();
        }
    }

    private void guessIncorect(View v) {
        Resources res = getResources();
        mGuessesLeft --;
        mGuessesCounter.setText(res.getString(R.string.label_guesses, mGuessesLeft));
        v.setBackgroundColor(getColor(R.color.letter_bg_wrong));
        if (mGuessesLeft <= 0) {
            // loss
            Toast.makeText(this, "Nie udało się :(", Toast.LENGTH_SHORT).show();
            disableKeypad();
            revealSolution();

        }
    }

    private void revealSolution() {
        for (int i = 0; i < mWord.length(); i++) {
            mLetters.get(i).setText(String.valueOf(mWord.charAt(i)));
        }
    }

    private void disableKeypad() {
        int keyCount = mKeypad.getChildCount();
        for (int i = 0; i < keyCount; i++) {
            mKeypad.getChildAt(i).setEnabled(false);
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

    private class LetterListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d("Button", "Clicked: " + ((Button) v).getText());
            v.setEnabled(false);

            boolean isOk = checkLetter(((Button) v).getText().charAt(0));
            if (isOk) {
                guessCorrect(v);
            } else {
                guessIncorect(v);
            }
        }
    }
}
