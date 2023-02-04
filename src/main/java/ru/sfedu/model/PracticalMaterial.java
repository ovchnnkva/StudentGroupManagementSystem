package ru.sfedu.model;
import com.opencsv.bean.CsvDate;
import com.sun.xml.bind.annotation.XmlLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.xml.bind.annotation.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class PracticalMaterial extends EducationMaterials implements EntityBean {
	@XmlTransient
	private static final Logger log = LogManager.getLogger(PracticalMaterial.class);
	@XmlElement(name = "teachersFile")
	private String teachersFile="";
	@XmlAttribute(name = "idPractical")
	private long id;
	@XmlElement(name = "teacherComment")
	private String teacherComment = "";
	@XmlElement(name = "name")
	private String name;
	@XmlElement(name = "studentFile")
	private String studentFile ="";
	@XmlElement(name = "maximumScore")
  	private int maximumScore = 0;
	@XmlElement(name = "studentScore")
  	private int studentScore = 0;
	@XmlElement(name = "studentComment")
  	private String studentComment = "";
	@CsvDate(value = "dd-MM-yyyy hh:mm")
	@XmlElement(name = "deadline")
  	private Date deadline;
	@XmlAttribute(name = "disciplineId")
	private long disciplineID;
	@XmlTransient
	private Discipline discipline;
	@XmlTransient
	  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");

	//
	// Constructors
	//
	public PracticalMaterial (long id,String name, Discipline discipline, String deadline, int maximumScore) throws Exception {
		super();
		this.id = id;
		this.name = name;
		this.discipline = discipline;
		disciplineID = discipline.getId();
		this.deadline = dateFormatting(deadline);
		checkExceedingMaximumScore(maximumScore);
		this.maximumScore = maximumScore;
		log.info("create practical material");
	}
	public PracticalMaterial(){log.info("create practical material");}

	//
	// Methods
	//


	//
	// Accessor methods
	//
	@Override
	public long getId(){
		return id;
	}
  	public void setMaximumScore (int maximumScore) {
		  this.maximumScore = maximumScore;
  	}

  	public int getMaximumScore () {
		  return maximumScore;
	}

  	public void setStudentScore(int studentScore) {
  		this.studentScore = studentScore;
  	}

  	public int getStudentScore() {
  		return studentScore;
  	}

  	public void setStudentComment(String comment) throws Exception {
		if(studentComment.length()+comment.length()<255)
  			studentComment += comment;
	  	else
			throw new Exception("comment cannot be more than 255 characters");
  	}

  	public String getStudentComment() {
  		return studentComment;
  	}

	public void setDeadline(Date deadline){
	  this.deadline = deadline;
  }

	public Date getDeadline(){
		return deadline;
  	}
	public String getStudentFile(){
		return studentFile;
	}

	public void setStudentFile(String path){
		studentFile = path;
	}

	public String returnStudentFile(){
		return studentFile;
	}

	@Override
	public long getDisciplineID() {
		return disciplineID;
	}

	@Override
	public void setDisciplineID(long disciplineID) {
		this.disciplineID = disciplineID;
	}

	@Override
	public String getTeachersFile() {
		return teachersFile;
	}

	@Override
	public void setTeachersFile(String teachersFile) {
		this.teachersFile = teachersFile;
	}

	@Override
	public String getTeacherComment() {
		return teacherComment;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public void setTeacherComment(String teacherComment) {
		this.teacherComment = teacherComment;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Discipline getDiscipline() {
		return discipline;
	}

	@Override
	public void setDiscipline(Discipline discipline) {
		this.discipline = discipline;
	}

	//
	//Other methods
	//
	private Date dateFormatting(String dateString) throws Exception {
		try {
			return simpleDateFormat.parse(dateString);
		}catch(ParseException e){
			throw new Exception("invalid date format");
		}

	}
	private void checkExceedingMaximumScore(int maximumScore) throws Exception {
		if(maximumScore>100){
			throw new Exception("maximum score can't be more than 100");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		PracticalMaterial that = (PracticalMaterial) o;
		return id == that.id  ;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id);
	}

	@Override
	public String toString(){
		return discipline+"\ntask: "+ name+"\ndeadline: "+simpleDateFormat.format(deadline)+
				"\nmaximum score: " + maximumScore +"\n student score: "
				+ studentScore +" \nteachers file:" + teachersFile +"\nstudents file:"
				+ studentFile + "\nTeacher comment: "
				+ teacherComment + "\nStudent comment: " + studentComment;
	}

}
