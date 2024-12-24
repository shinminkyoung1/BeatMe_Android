package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class OnGameActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView myChoiceImage, counterChoiceImage, resultImage;
    private ImageView scissorsButton, rockButton, paperButton;
    private TextView myText, vsText, counterText;
    private int progressStatus = 0;
    private final Handler handler = new Handler();
    private String userChoice = null;
    private String myRandomChoice = null;

    private int correctCount = 0; // Correct 횟수
    private int roundCount = 0; // 진행된 라운드 수

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ongame);

        // ProgressBar 초기화
        progressBar = findViewById(R.id.progressBar);

        // TextView 초기화
        myText = findViewById(R.id.myText);
        vsText = findViewById(R.id.vsText);
        counterText = findViewById(R.id.counterText);

        // ImageView 초기화
        myChoiceImage = findViewById(R.id.myChoiceImage);
        counterChoiceImage = findViewById(R.id.counterChoiceImage);
        resultImage = findViewById(R.id.resultImage);

        // 선택 버튼 초기화
        scissorsButton = findViewById(R.id.scissorsButton);
        rockButton = findViewById(R.id.rockButton);
        paperButton = findViewById(R.id.paperButton);

        // 선택 버튼 클릭 이벤트 설정
        scissorsButton.setOnClickListener(v -> handleUserChoice("scissors", R.drawable.scissors));
        rockButton.setOnClickListener(v -> handleUserChoice("rock", R.drawable.rock));
        paperButton.setOnClickListener(v -> handleUserChoice("paper", R.drawable.paper));

        // 게임 초기화
        startGame();
    }

    private void handleUserChoice(String choice, int drawableResId) {
        userChoice = choice;
        counterChoiceImage.setImageResource(drawableResId); // "상대" 선택 이미지 업데이트
        evaluateResult(); // 결과 평가
    }

    private void startGame() {
        resetGameState(); // 게임 상태 초기화
        updateMyRandomChoice(); // "나"의 랜덤 선택 갱신
        startProgressBar(); // ProgressBar 애니메이션 시작
    }


    private void updateMyRandomChoice() {
        // Drawable 리소스 배열
        int[] imageResources = {
                R.drawable.rock,     // 주먹
                R.drawable.scissors, // 가위
                R.drawable.paper     // 보
        };
        String[] choices = {"rock", "scissors", "paper"};

        // 랜덤으로 선택
        Random random = new Random();
        int randomIndex = random.nextInt(imageResources.length);

        // "나" 이미지 업데이트
        myRandomChoice = choices[randomIndex];
        myChoiceImage.setImageResource(imageResources[randomIndex]);
    }

    private void startProgressBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressStatus < 100) {
                    progressStatus++;
                    progressBar.setProgress(progressStatus); // ProgressBar 업데이트
                    handler.postDelayed(this, 20); // 20ms 후 다시 실행
                } else if (userChoice == null) {
                    // 시간 초과 시 패배 처리
                    resultImage.setImageResource(R.drawable.incorrect); // 패배 이미지 설정
                    resultImage.setVisibility(View.VISIBLE);
                    proceedToNextRound(false);
                }
            }
        }, 20); // 20ms 후 시작
    }

    private void evaluateResult() {
        if (userChoice == null || myRandomChoice == null) return;

        boolean isCorrect = false;

        // 승부 로직: "나"와 "상대" 선택 비교
        if ((myRandomChoice.equals("rock") && userChoice.equals("scissors")) ||
                (myRandomChoice.equals("scissors") && userChoice.equals("paper")) ||
                (myRandomChoice.equals("paper") && userChoice.equals("rock"))) {
            resultImage.setImageResource(R.drawable.correct); // 승리 이미지 설정
            isCorrect = true;
        } else {
            resultImage.setImageResource(R.drawable.incorrect); // 패배 이미지 설정
        }

        // 결과 이미지 표시
        resultImage.setVisibility(View.VISIBLE);

        // 투명도 40%로 설정
        myText.setAlpha(0.4f);
        vsText.setAlpha(0.4f);
        counterText.setAlpha(0.4f);
        myChoiceImage.setAlpha(0.4f);
        counterChoiceImage.setAlpha(0.4f);
        scissorsButton.setAlpha(0.4f);
        rockButton.setAlpha(0.4f);
        paperButton.setAlpha(0.4f);

        // 단계 표시 업데이트
        updateStepIndicator(correctCount + (isCorrect ? 1 : 0));

        // 다음 라운드로 진행
        proceedToNextRound(isCorrect);
    }

    private void resetGameState() {
        progressStatus = 0;
        progressBar.setProgress(0);
        userChoice = null;
        counterChoiceImage.setImageResource(R.drawable.questionmark); // "상대" 이미지를 물음표로 설정
        resultImage.setVisibility(View.INVISIBLE); // 결과 이미지 숨김

        // 투명도 초기화
        FrameLayout gameFrame = findViewById(R.id.gameFrame);
        // 투명도 원래대로 복원
        myText.setAlpha(1.0f);
        vsText.setAlpha(1.0f);
        counterText.setAlpha(1.0f);
        myChoiceImage.setAlpha(1.0f);
        counterChoiceImage.setAlpha(1.0f);
        scissorsButton.setAlpha(1.0f);
        rockButton.setAlpha(1.0f);
        paperButton.setAlpha(1.0f);
    }


    private void proceedToNextRound(boolean isCorrect) {
        // Correct 횟수 증가
        if (isCorrect) correctCount++;

        // 라운드 증가
        roundCount++;

        // 승부 판정
        if (correctCount >= 3) {
            // 승리 화면으로 이동
            Intent intent = new Intent(OnGameActivity.this, EndingWinActivity.class);
            startActivity(intent);
            finish();
        } else if (roundCount >= 5) {
            // 패배 화면으로 이동
            Intent intent = new Intent(OnGameActivity.this, EndingLoseActivity.class);
            startActivity(intent);
            finish();
        } else {
            // 2초 후 다음 라운드 시작
            handler.postDelayed(this::startGame, 2000);
        }
    }

    private void updateStepIndicator(int correctCount) {
        // 단계 표시 View 초기화
        TextView step1 = findViewById(R.id.step1);
        TextView step2 = findViewById(R.id.step2);
        TextView step3 = findViewById(R.id.step3);

        // 모든 단계를 inactive로 초기화
        step1.setBackgroundResource(R.drawable.step_circle_inactive);
        step2.setBackgroundResource(R.drawable.step_circle_inactive);
        step3.setBackgroundResource(R.drawable.step_circle_inactive);

        // 이긴 횟수에 따라 단계 활성화
        if (correctCount >= 1) {
            step1.setBackgroundResource(R.drawable.step_circle_active);
        }
        if (correctCount >= 2) {
            step2.setBackgroundResource(R.drawable.step_circle_active);
        }
        if (correctCount >= 3) {
            step3.setBackgroundResource(R.drawable.step_circle_active);
        }
    }
}
