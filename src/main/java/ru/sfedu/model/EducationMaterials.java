package ru.sfedu.model;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.annotation.*;
import java.util.Objects;

public class EducationMaterials implements EntityBean{

	private static final Logger log = LogManager.getLogger(EducationMaterials.class);

  	protected String teachersFile="";

  	protected String teacherComment = "";

  	protected String name;
	@XmlTransient
  	protected Discipline discipline;

  	protected long id;

  	protected long disciplineID;
		//
		// Constructors
		//
		public EducationMaterials (long id,String name, Discipline discipline) {
			this.id = id;
			this.name = name;
			this.discipline = discipline;
			disciplineID = discipline.getId();
			log.info("create education material");
		}
		public EducationMaterials(){log.info("create education material");}
  
		//
		// Methods
		//


		//
		// Accessor methods
		//

  	public void setTeachersFile (String path) {
  		teachersFile= path;
  }

  	public String getTeachersFile () {
  		return teachersFile;
  }

  	public void setTeacherComment(String comment) throws Exception {
		if((teacherComment.length()+comment.length())<255){
  			teacherComment += comment;
	  	}else{
		  	throw new Exception("comment cannot be more than 255 characters");
		}
  	}
  	public String getTeacherComment() {
  		return teacherComment;
  }

  	public void setName(String name) {
  		this.name = name;
  }

  	public String getName () {
  		return name;
  }
	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDisciplineID() {
		return disciplineID;
	}

	public void setDisciplineID(long disciplineID) {
		this.disciplineID = disciplineID;
	}

  	public void setDiscipline (Discipline discipline) {
  		this.discipline = discipline;
		  disciplineID = discipline.getId();
  	}

  	public Discipline getDiscipline() {
  		return discipline;
  	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EducationMaterials that = (EducationMaterials) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString(){
	  return discipline+" "+ name+" \n"+teachersFile+"\ncomment: "+teacherComment;
  }
}
