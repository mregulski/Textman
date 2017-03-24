package ppt.reshi.textman;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Static keyboard component
 */

public class Keypad extends GridLayout {

    private static final String TAG = "Keypad";

    private LetterReceiver mLetterReceiver;
    private Map<Character, Button> mButtons;

    public Keypad(Context context) {
        super(context);
        initialize(context);
    }

    public Keypad(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public Keypad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public Keypad(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context);
    }

    public void initialize(Context context) {
        mButtons = new HashMap<>();
        // TODO: un-hardcode language
        populate(getAlphabet("pl"));
    }

    public void setLetterReceiver(LetterReceiver receiver) {
        mLetterReceiver = receiver;
    }

    public void markCorrect(char letter) {
        mButtons.get(letter).setBackgroundColor(getContext().getColor(R.color.colorOk));
    }

    public void markIncorrect(char letter) {
        mButtons.get(letter).setBackgroundColor(getContext().getColor(R.color.colorError));
    }

    public Button getButtonForLetter(char c) {
        return mButtons.get(c);
    }

    private void populate(char[] alphabet) {

        for (char c : alphabet) {
            Button button = new Button(getContext());
            mButtons.put(c, button);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = (int) getResources().getDimension(R.dimen.btn_letter_width);
            button.setText(String.valueOf(c));
            button.setTag(c);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLetterReceiver.onLetter((char) v.getTag());
                    v.setEnabled(false);
                }
            });
            addView(button, params);
        }
    }

    public void disable() {
        int keyCount = getChildCount();
        for (int i = 0; i < keyCount; i++) {
            getChildAt(i).setEnabled(false);
        }
    }

    public void reset() {
        removeAllViews();
        populate(getAlphabet("pl"));
    }

    private char[] getAlphabet(String language) {
        if (language.equals("pl")) {
            return "AĄBCĆDEĘFGHIJKLŁMNŃOÓPQRSŚTUVWXYZŹŻ".toCharArray();
        } else {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }


}
