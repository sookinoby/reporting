package pojo.reporting.com.r2m;

public class Questions {
 String ID,SID,MapRow,MapCol,StudentAnswer;
 float Time;
 boolean Right;
 String Q[],A[],O[];

 public String getID() {
  return ID;
 }

 public void setID(String ID) {
  this.ID = ID;
 }

 public String getSID() {
  return SID;
 }

 public void setSID(String SID) {
  this.SID = SID;
 }

 public String getMapRow() {
  return MapRow;
 }

 public void setMapRow(String mapRow) {
  MapRow = mapRow;
 }

 public String getMapCol() {
  return MapCol;
 }

 public void setMapCol(String mapCol) {
  MapCol = mapCol;
 }

 public float getTime() {
  return Time;
 }

 public void setTime(float time) {
  Time = time;
 }

 public String getStudentAnswer() {
  return StudentAnswer;
 }

 public void setStudentAnswer(String studentAnswer) {
  StudentAnswer = studentAnswer;
 }

 public boolean isRight() {
  return Right;
 }

 public void setRight(boolean right) {
  Right = right;
 }

 public String[] getQ() {
  return Q;
 }

 public void setQ(String[] q) {
  Q = q;
 }

 public String[] getA() {
  return A;
 }

 public void setA(String[] a) {
  A = a;
 }

 public String[] getO() {
  return O;
 }

 public void setO(String[] o) {
  O = o;
 }
}
