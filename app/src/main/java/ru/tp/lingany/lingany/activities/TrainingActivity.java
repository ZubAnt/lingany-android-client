package ru.tp.lingany.lingany.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

import java.util.List;

import ru.tp.lingany.lingany.R;
import ru.tp.lingany.lingany.fragments.FindTranslationFragment;
import ru.tp.lingany.lingany.fragments.LoadingFragment;
import ru.tp.lingany.lingany.fragments.SprintFragment;
import ru.tp.lingany.lingany.fragments.TeachingFragment;
import ru.tp.lingany.lingany.fragments.TypingFragment;
import ru.tp.lingany.lingany.fragments.fragmentData.FragmentData;
import ru.tp.lingany.lingany.fragments.fragmentData.SprintData;
import ru.tp.lingany.lingany.fragments.fragmentData.TeachingData;
import ru.tp.lingany.lingany.fragments.fragmentData.TranslationData;
import ru.tp.lingany.lingany.fragments.fragmentData.TypingData;
import ru.tp.lingany.lingany.sdk.Api;
import ru.tp.lingany.lingany.sdk.api.categories.Category;
import ru.tp.lingany.lingany.sdk.api.trainings.Training;
import ru.tp.lingany.lingany.utils.ListenerHandler;


public class TrainingActivity extends AppCompatActivity implements
        FindTranslationFragment.FindTranslationListener,
        LoadingFragment.RefreshListener,
        SprintFragment.SprintListener,
        TeachingFragment.TeachingListener,
        TypingFragment.TypingListener {

    enum Mode { TEACHING, FIND_TRANSLATION, SPRINT, TYPING }
    private Mode mode;
    private SprintFragment sprintFragment;
    private FindTranslationFragment translationFragment;
    private TeachingFragment teachingFragment;
    private TypingFragment typingFragment;

    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
    public static final String TRAINING_MODE = "TRAINING_MODE";
    public static final String SPRINT_DATA = "SPRINT_DATA";
    public static final String TRAINING_DATA = "TRAINING_DATA";
    public static final String TEACHING_DATA = "TEACHING_DATA";
    public static final String TYPING_DATA = "TYPING_DATA";

    private List<Training> trainings;
    private LoadingFragment loadingFragment;
    private Category category;

    private ListenerHandler getForCategoryListenerHandler;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mode != null) {
            switch (mode) {
                case TEACHING:
                    TeachingData teachingData = teachingFragment.getTeachingData();
                    savedInstanceState.putSerializable(TEACHING_DATA, teachingData);
                    break;
                case FIND_TRANSLATION:
                    TranslationData translationData =  translationFragment.getTranslationData();
                    savedInstanceState.putSerializable(TRAINING_DATA, translationData);
                    break;
                case SPRINT:
                    sprintFragment.stopTimer();
                    SprintData sprintData = sprintFragment.getSprintData();
                    savedInstanceState.putSerializable(SPRINT_DATA, sprintData);
                    break;
                case TYPING:
                    TypingData typingData = typingFragment.getTypingData();
                    savedInstanceState.putSerializable(TYPING_DATA, typingData);
                    break;
                default:
                    break;
            }
            savedInstanceState.putSerializable(TRAINING_MODE, mode);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        loadingFragment = new LoadingFragment();
        Intent intent = getIntent();
        category = (Category) intent.getSerializableExtra(EXTRA_CATEGORY);

        getForCategoryListenerHandler = ListenerHandler.wrap(ParsedRequestListener.class, new ParsedRequestListener<List<Training>>() {
            @Override
            public void onResponse(List<Training> response) {
                trainings = response;
                changeMode(Mode.TEACHING, new TeachingData(trainings));
                loadingFragment.stopLoading();
            }

            @Override
            public void onError(ANError anError) {
                loadingFragment.showRefresh();
            }
        });

        if (savedInstanceState != null) {

            mode = (Mode) savedInstanceState.getSerializable(TRAINING_MODE);
            if (mode == Mode.TEACHING) {
                TeachingData teachingData = (TeachingData) savedInstanceState.getSerializable(TEACHING_DATA);
                changeMode(mode, teachingData);
                return;
            } else if (mode == Mode.SPRINT) {
                SprintData sprintData = (SprintData) savedInstanceState.getSerializable(SPRINT_DATA);
                changeMode(mode, sprintData);
                return;
            } else if (mode == Mode.FIND_TRANSLATION) {
                TranslationData translationData = (TranslationData) savedInstanceState.getSerializable(TRAINING_DATA);
                changeMode(mode, translationData);
                return;
            } else if (mode == Mode.TYPING) {
                TypingData typingData = (TypingData) savedInstanceState.getSerializable(TYPING_DATA);
                changeMode(mode, typingData);
                return;
            }
        }

        inflateLoadingFragment();
        getTrainingsForCategory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getForCategoryListenerHandler != null) {
            getForCategoryListenerHandler.unregister();
        }
    }

    @SuppressWarnings("unchecked")
    private void getTrainingsForCategory() {
        ParsedRequestListener<List<Training>> listener = (ParsedRequestListener<List<Training>>) getForCategoryListenerHandler.asListener();
        Api.getInstance().training().getForCategory(category, listener);
    }

    private void inflateLoadingFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.trainingContainer, loadingFragment)
                .commit();
    }

    private void changeMode(final Mode newMode, final FragmentData data) {
        inflateLoadingFragment();
        mode = newMode;

        Fragment fragment = null;
        if (newMode == Mode.TEACHING) {
            teachingFragment = TeachingFragment.newInstance((TeachingData) data);
            fragment = teachingFragment;
        } else if (newMode == Mode.FIND_TRANSLATION) {
            translationFragment = FindTranslationFragment.newInstance((TranslationData) data);
            fragment = translationFragment;
        } else if (newMode == Mode.SPRINT) {
            sprintFragment = SprintFragment.newInstance((SprintData) data);
            fragment = sprintFragment;
        } else if (newMode == Mode.TYPING) {
            typingFragment = TypingFragment.newInstance((TypingData) data);
            fragment = typingFragment;
        }

        final Handler handler = new Handler();
        final Fragment finalFragment = fragment;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingFragment.startLoading();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.trainingContainer, finalFragment)
                        .commit();
            }
        }, getResources().getInteger(R.integer.delayInflateAfterLoading));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getForCategoryListenerHandler != null) {
            getForCategoryListenerHandler.unregister();
        }
    }

    @Override
    public void onRefresh() {
        loadingFragment.startLoading();
        getTrainingsForCategory();
    }

    @Override
    public void onFindTranslationFinished() {
        changeMode(Mode.SPRINT, new SprintData(trainings));
    }

    @Override
    public void onSprintFinished() {
        finish();
    }

    @Override
    public void onTeachingFinished() {
        changeMode(Mode.TYPING, new TypingData(trainings));
    }

    @Override
    public void onTypingFinished() {
        changeMode(Mode.FIND_TRANSLATION, new TranslationData(trainings));
    }
}
