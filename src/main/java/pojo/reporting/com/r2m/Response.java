package pojo.reporting.com.r2m;

public class Response {
	
	String gameToPlay,studentEmail,studentName,studentGrade;
	GameData gameData;

	public String getGameToPlay() {
		return gameToPlay;
	}

	public void setGameToPlay(String gameToPlay) {
		this.gameToPlay = gameToPlay;
	}

	public String getStudentEmail() {
		return studentEmail;
	}

	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentGrade() {
		return studentGrade;
	}

	public void setStudentGrade(String studentGrade) {
		this.studentGrade = studentGrade;
	}

	public GameData getGameData() {
		return gameData;
	}

	public void setGameData(GameData gameData) {
		this.gameData = gameData;
	}
}
