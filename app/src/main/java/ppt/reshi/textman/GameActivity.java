package ppt.reshi.textman;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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


    private static final String TAG = "GameActivity";


    private LinearLayout mLettersContainer;
    private ArrayList<TextView> mLetters;
    private ImageView mGuessesCounter;
    private TextView mScoreLabel;
    private Keypad mKeypad;



    private GamePresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new GamePresenter(this);
        setContentView(R.layout.activity_game);
        mLettersContainer = (LinearLayout) findViewById(R.id.ll_letters);
        mGuessesCounter = (ImageView) findViewById(R.id.iv_hangman);
        mScoreLabel = (TextView) findViewById(R.id.tv_score);
        mScoreLabel.setText(getResources().getQuantityString(R.plurals.score, 0, 0));
        mKeypad = (Keypad) findViewById(R.id.kp_keypad);
        mKeypad.setLetterReceiver(new LetterReceiver() {
            @Override
            public void onLetter(char c) {
                Log.d(TAG, "Clicked: " + c);
                mPresenter.checkLetter(c);
            }
        });

        findViewById(R.id.btn_new_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
        startNewGame();
    }

    private void startNewGame() {
        mPresenter.startNewGame();
        mGuessesCounter.setImageLevel(0);
    }

    public void mark(char letter, boolean isCorrect) {
        if (isCorrect) {
            mKeypad.markCorrect(letter);
        } else {
            mKeypad.markIncorrect(letter);
        }
    }

    public void setGuessesLeft(int left) {
        Log.i(TAG, "new level: " + left);
        mGuessesCounter.setImageLevel(mPresenter.getInitialGuesses() - left);
    }

    public void gameWon() {
        Toast.makeText(this, "You won!", Toast.LENGTH_SHORT).show();
        mKeypad.disable();
    }

    public void gameLost() {
        Toast.makeText(this, "Nie udało się :(", Toast.LENGTH_SHORT).show();
        mKeypad.disable();
    }

    /**
     * Start a fresh game
     */
    public void resetBoard() {
        Resources res = getResources();

        mLettersContainer.removeAllViews();
        mLetters = new ArrayList<>(mPresenter.getLetterCount());
        mKeypad.reset();
        for (int i = 0; i < mPresenter.getLetterCount(); i++) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") // manual attachement
                    TextView letterView = (TextView) inflater.inflate(R.layout.game_letter_view, null);
            mLettersContainer.addView(letterView);
            mLetters.add(letterView);
        }
    }

    public void revealLetter(int pos, char letter) {
        mLetters.get(pos).setText(String.valueOf(letter));
    }


    public void setScore(int score) {
        mScoreLabel.setText(getResources().getQuantityString(R.plurals.score, score, score));
    }

}
