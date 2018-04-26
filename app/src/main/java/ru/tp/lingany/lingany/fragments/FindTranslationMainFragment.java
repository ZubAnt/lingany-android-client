package ru.tp.lingany.lingany.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.tp.lingany.lingany.R;
import ru.tp.lingany.lingany.activities.TrainingActivity;
import ru.tp.lingany.lingany.sdk.trainings.Training;
import ru.tp.lingany.lingany.utils.RandArray;

public class FindTranslationMainFragment extends Fragment {
    private List<TextView> buttons = new ArrayList<>();

    public interface FindTranslationListener {
        void onFindTranslationFinished();
    }

    private FindTranslationListener findTranslationFinished;
    private List<Training> trainings;
    private Training currentTraining;

    private ViewGroup markCrossContainer;
    private TextView wordToTranslate;
    private LayoutInflater inflater;

    public static FindTranslationMainFragment newInstance(List<Training> trainings) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("trainings", (Serializable) trainings);

        FindTranslationMainFragment fragment = new FindTranslationMainFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @SuppressWarnings("unchecked")
    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            trainings = (List<Training>) bundle.getSerializable("trainings");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_translations_main, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        readBundle(getArguments());

        buttons.add((TextView) Objects.requireNonNull(getView()).findViewById(R.id.leftUpperButton));
        buttons.add((TextView) getView().findViewById(R.id.leftDownButton));
        buttons.add((TextView) getView().findViewById(R.id.rightUpperButton));
        buttons.add((TextView) getView().findViewById(R.id.rightDownButton));

        markCrossContainer = Objects.requireNonNull(getView()).findViewById(R.id.containerMarkAndCross);
        wordToTranslate = getView().findViewById(R.id.wordToTranslate);

        for (View button:buttons) {
            button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processAnswer(v);
                    }
                });
        }

        setAll();
    }

    private void setAll() {
        if (trainings.size() < 4) {
            finish();
        }
        Training training = trainings.get(0);
        currentTraining = training;

        clearMarkAndCross();
        setWordToTranslate(training.getForeignWord());
        setTranslationButtons(training);
    }

    private void setTranslationButtons(Training training) {
        List<Integer> indexes = RandArray.getRandIndexes(3, 1, trainings.size() - 1);
        List<String> words = new ArrayList<>();

        for (Integer index: indexes) {
            words.add(trainings.get(index).getNativeWord());
        }
        setWordsOnButtons(training.getNativeWord(), words);
    }

    public void setWordsOnButtons(String translationWord, List<String> words) {
        int translationPosition = (int) (Math.random() * 3);
        for (int i = 0, j = 0; i < buttons.size(); ++i) {
            if (i == translationPosition) {
                buttons.get(i).setText(translationWord);
            } else {
                if (words.size() < 1) {
                    break;
                }
                buttons.get(i).setText(words.get(j));
                ++j;
            }
        }
    }

    private void processAnswer(View view) {
        TextView textView = (TextView) view;
        if (this.currentTraining != null && this.currentTraining.getNativeWord() == textView.getText()) {
            setMark();
        } else {
            setCross();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 2000ms
                trainings.remove(0);
                setAll();
            }
        }, 1000);
    }

    public void setWordToTranslate(String word) {
        wordToTranslate.setText(word);
    }

    public void setMark() {
        clearMarkAndCross();
        final View view = inflater.inflate(R.layout.item_mark, markCrossContainer, false);
        markCrossContainer.addView(view);
    }

    public void setCross() {
        clearMarkAndCross();
        final View view = inflater.inflate(R.layout.item_cross, markCrossContainer, false);
        markCrossContainer.addView(view);
    }

    public void clearMarkAndCross() {
        markCrossContainer.removeAllViews();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        findTranslationFinished = (FindTranslationListener) context;
    }

    private void finish() {
        findTranslationFinished.onFindTranslationFinished();
    }

}
