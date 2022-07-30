package com.dahdotech.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.dahdotech.trivia.controller.AppController;
import com.dahdotech.trivia.model.Question;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";
    private ArrayList<Question> questionArrayList = new ArrayList<>();

    public List<Question> getQuestion(final AnswerListAsyncResponse callBack){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    Question question = new Question(response.getJSONArray(i).get(0).toString(), response.getJSONArray(i).getBoolean(1));
                    questionArrayList.add(question);
                    Log.d("hello", "getQuestion: " + questionArrayList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(callBack != null)
                callBack.processFinished(questionArrayList);
        }, error -> {

        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        return questionArrayList;
    }
}
