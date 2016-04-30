package pojo.reporting.com.r2m;

import java.util.ArrayList;

public class GameData {
 String name,Sname,GameTitle,ErrorList;
 int TotalQuestionsAsked,TotalRightAnswer;
 boolean Pacer,IsTimed,InstantaneousFeedBack,Watchlist,NewGameButton,ScoreButton;
 ArrayList<Questions> questionList = new ArrayList<Questions>();
}
