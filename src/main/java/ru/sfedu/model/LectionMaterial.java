package ru.sfedu.model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.annotation.*;
import java.util.Objects;


@XmlAccessorType(XmlAccessType.FIELD)
public class LectionMaterial extends EducationMaterials implements EntityBean{
	@XmlTransient
	private static final Logger log = LogManager.getLogger(LectionMaterial.class);
	@XmlElement(name = "teacherFile")
	private String teachersFile="";
	@XmlElement(name = "teacherComment")
	private String teacherComment = "";
	@XmlElement(name = "name")
	private String name;
	@XmlElement(name = "idLection")
	private long id;
	@XmlElement(name = "disciplineId")
	private long disciplineID;
	@XmlTransient
	private Discipline discipline;
	//
	// Constructors
	//

	public LectionMaterial(long id,String name, Discipline discipline) {
		super();
		this.id = id;
		this.name = name;
		this.discipline = discipline;
		disciplineID = discipline.getId();
		log.info("create lection material");
	}
	public LectionMaterial(){log.info("create lection material");}
	@Override
	public long getId(){
		return id;
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
	public void setId(long id) {
		this.id = id;
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
	public Discipline getDiscipline() {
		return discipline;
	}

	@Override
	public void setDiscipline(Discipline discipline) {
		this.discipline = discipline;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		LectionMaterial that = (LectionMaterial) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id);
	}
}
