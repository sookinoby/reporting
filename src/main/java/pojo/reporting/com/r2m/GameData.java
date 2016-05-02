package pojo.reporting.com.r2m;

import java.util.ArrayList;

public class GameData {
 String name,Sname,GameTitle,ErrorList;
 int TotalQuestionsAsked,TotalRightAnswer;
 boolean Pacer,IsTimed,InstantaneousFeedBack,Watchlist,NewGameButton,ScoreButton;
 ArrayList<Questions> questionList = new ArrayList<Questions>();

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public String getSname() {
  return Sname;
 }

 public void setSname(String sname) {
  Sname = sname;
 }

 public String getGameTitle() {
  return GameTitle;
 }

 public void setGameTitle(String gameTitle) {
  GameTitle = gameTitle;
 }

 public String getErrorList() {
  return ErrorList;
 }

 public void setErrorList(String errorList) {
  ErrorList = errorList;
 }

 public int getTotalQuestionsAsked() {
  return TotalQuestionsAsked;
 }

 public void setTotalQuestionsAsked(int totalQuestionsAsked) {
  TotalQuestionsAsked = totalQuestionsAsked;
 }

 public int getTotalRightAnswer() {
  return TotalRightAnswer;
 }

 public void setTotalRightAnswer(int totalRightAnswer) {
  TotalRightAnswer = totalRightAnswer;
 }

 public boolean isPacer() {
  return Pacer;
 }

 public void setPacer(boolean pacer) {
  Pacer = pacer;
 }

 public boolean isTimed() {
  return IsTimed;
 }

 public void setTimed(boolean timed) {
  IsTimed = timed;
 }

 public boolean isInstantaneousFeedBack() {
  return InstantaneousFeedBack;
 }

 public void setInstantaneousFeedBack(boolean instantaneousFeedBack) {
  InstantaneousFeedBack = instantaneousFeedBack;
 }

 public boolean isWatchlist() {
  return Watchlist;
 }

 public void setWatchlist(boolean watchlist) {
  Watchlist = watchlist;
 }

 public boolean isNewGameButton() {
  return NewGameButton;
 }

 public void setNewGameButton(boolean newGameButton) {
  NewGameButton = newGameButton;
 }

 public boolean isScoreButton() {
  return ScoreButton;
 }

 public void setScoreButton(boolean scoreButton) {
  ScoreButton = scoreButton;
 }

 public ArrayList<Questions> getQuestionList() {
  return questionList;
 }

 public void setQuestionList(ArrayList<Questions> questionList) {
  this.questionList = questionList;
 }
}
