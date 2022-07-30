package com.dahdotech.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dahdotech.trivia.data.Repository;
import com.dahdotech.trivia.databinding.ActivityMainBinding;
import com.dahdotech.trivia.model.Question;
import com.dahdotech.trivia.model.Score;
import com.dahdotech.trivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        score = new Score();
        prefs = new Prefs(MainActivity.this); // same as simply passing 'this'

        //retrieve the last state
        currentQuestionIndex = prefs.getState();

        binding.highestScore.setText(String.format("Highest: %s", String.valueOf(prefs.getHighestScore())));
        binding.scoreText.setText(String.format("Current: %s", String.valueOf(score.getScore())));

        questionList = new Repository().getQuestion(questionArrayList -> {

            binding.questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                    updateCounter(questionArrayList);
                }
        );
        binding.buttonNext.setOnClickListener(view -> {
            nextQuestion();
            prefs.saveHighestScore(scoreCounter);
        });
        binding.buttonTrue.setOnClickListener(view -> {
            checkAnswer(true);
            nextQuestion();
            //updateQuestion(); // after making use of nextQuestion() function in animation functions.
                                //This is no longer needed to trigger it.
        });
        binding.buttonFalse.setOnClickListener(view -> {
            checkAnswer(false);
            nextQuestion();
            //updateQuestion();
        });
    }

    private void nextQuestion() {
        currentQuestionIndex = ++currentQuestionIndex % questionList.size();
        updateQuestion();
    }

    private void checkAnswer(boolean userChoice) {
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if(userChoice == answer){
            snackMessageId = R.string.correct_answer;
            fadeAnimation();
            addPoints();
        }
        else {
            snackMessageId = R.string.incorrect_answer;
            shakeAnimation();
            deductPoints();
        }
        Snackbar.make(binding.cardView, snackMessageId, Snackbar.LENGTH_SHORT).show();
    }

    private void updateCounter(ArrayList<Question> questionArrayList) {
        binding.textViewOutOf.setText(String.format("Question : %d / %d", currentQuestionIndex + 1, questionArrayList.size() + 1));
    }
    private void fadeAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                //calling nextQuestion() here was causing unwanted functionalities
                //multiple answers were being given before the animation could end
                //moved the function to button event listeners!
                //nextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        binding.questionTextView.setText(question);
        updateCounter((ArrayList<Question>) questionList);
    }
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                //nextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void addPoints(){
        scoreCounter += 100;
        score.setScore(scoreCounter);
        binding.scoreText.setText(String.format("Current: %s", String.valueOf(score.getScore())));
    }

    private void deductPoints(){
        scoreCounter -= 100;
        if(scoreCounter < 0)
            scoreCounter = 0;
        score.setScore(scoreCounter);
        binding.scoreText.setText(String.format("Current: %s", String.valueOf(score.getScore())));
    }

    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        Log.d("state", "onPause saved state: " + prefs.getState());
        super.onPause();
    }
}