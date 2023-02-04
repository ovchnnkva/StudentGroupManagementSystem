package ru.sfedu.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.annotation.*;
import java.util.*;


/**
 * Class Teacher
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Teacher implements EntityBean{
	@XmlElement(name = "teacherId")
	private long id;
	@XmlTransient
	private final Logger log = LogManager.getLogger(Teacher.class);
	@XmlElement
	private String name;
	@XmlTransient
  	private List<Discipline> disciplines=new ArrayList<>();
  
	//
	// Constructors
	//
	public Teacher (long id,String name) {
		this.id = id;
		this.name = name;
		log.info("create teacher "+this);
	};
	public Teacher(){}

  	//
	// Methods
	//


	//
	// Accessor methods
	//
	@Override
	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
  		this.name = name;
  }

  	public String getName() {
  		return name;
  }

  	public void setDisciplines(List<Discipline> disciplines) {
  		this.disciplines = disciplines;
  }

  	public List<Discipline> getDisciplines() {
  		return disciplines;
  }

	//
	// Other methods
	//

	/**
	 *Append to disciplines
	 * @param discipline for append to teacher disciplines
	 * @throws Exception
	 */
	public void appendDiscipline(Discipline discipline) throws Exception {
		if (!disciplines.contains(discipline)) {
			disciplines.add(discipline);
			discipline.setTeacherId(id);
			log.info("append " + discipline + " to teacher " + name);
			log.debug("discipline teacher id " + discipline.getTeacherId());
		}else{
			throw new Exception("this discipline has alredy been added");
		}
	}

	/**
	 *Create practical material
	 * @param name the value of name practicalMaterials
	 * @param discipline the value of discipline practicalMaterials
	 * @param maximumScore the value of maximum score practicalMaterials
	 * @param deadline the value of deadline for practicalMaterials
	 * @return Optional<PracticalMaterial>
	 * @throws Exception
	 */
	public Optional<PracticalMaterial> createPracticalMaterial(long id,String name, Discipline discipline, int maximumScore, String deadline ) throws Exception {
		PracticalMaterial practicalMaterial = null;
		checkTeacherContainsDiscipline(discipline);

		try {
			practicalMaterial = new PracticalMaterial(id,name, discipline, deadline, maximumScore);
			log.info("teacher "+name+" create practical material");
		}catch(Exception e){
			e.printStackTrace();
		}

		return Optional.ofNullable(practicalMaterial);
	}

	/**
	 *
	 * @param name the value of name lectionMaterial
	 * @param discipline the value of discipline lectionMaterial
	 * @return Optional<LectionMaterial>
	 * @throws Exception
	 */
	public Optional<LectionMaterial> createLectionMaterial(long id,String name, Discipline discipline) throws Exception {
		LectionMaterial lectionMaterial;
		checkTeacherContainsDiscipline(discipline);

		lectionMaterial = new LectionMaterial(id,name, discipline);
		log.info("teacher "+name+" create lection material");

		return Optional.ofNullable(lectionMaterial);
	}

	/**
	 *Evaluate the completed practical material
	 * @param practicalMaterial the value of practicalMaterial
	 * @param student the value of student
	 * @param studentScore the value of studentScore
	 * @throws Exception
	 */
	public void evaluateCompletedPracticalMaterial(PracticalMaterial practicalMaterial, Student student, int studentScore) throws Exception {
		Discipline practicalMaterialsDiscipline = practicalMaterial.getDiscipline();

		checkExceedingMaximumScore(practicalMaterial.getMaximumScore(),studentScore);
		checkTeacherContainsDiscipline(practicalMaterialsDiscipline);

		try {
			student.appendDisciplineScore(practicalMaterial.getDiscipline(), studentScore);
			practicalMaterial.setStudentScore(studentScore);
			log.info("task evaluating");
			log.debug("practical material student studentScore="+practicalMaterial.getStudentScore());
			log.debug("student studentScore with "+practicalMaterialsDiscipline+" ="+student.getScores().get(practicalMaterialsDiscipline));
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 *Teacher commenting lection or practical material
	 * @param educationMaterials the value of practical or lection material
	 * @param comment the value of comment
	 */
	public void commentingMaterial(EducationMaterials educationMaterials, String comment) throws Exception {
		checkTeacherContainsDiscipline(educationMaterials.getDiscipline());

		educationMaterials.setTeacherComment(comment);
		log.info("teacher "+name+" commenting material "+educationMaterials.getName());
		log.debug("education material teacher comment: "+educationMaterials.getTeacherComment());
	}

	/**
	 * Attach teacher file to lection or practical materil
	 * @param educationMaterials the value of practical or lection material
	 * @param path the value of techerFile for practical or elction material
	 * @throws Exception
	 */
	public void attachFileToMaterial(EducationMaterials educationMaterials, String path) throws Exception {
		checkTeacherContainsDiscipline(educationMaterials.getDiscipline());

		educationMaterials.setTeachersFile(path);
		log.info("teacher "+name+" attach file "+path+"to material "+educationMaterials.getName());
		log.debug("teacher file in material "+educationMaterials.getName()+": "+educationMaterials.getTeachersFile());
	}

	/**
	 * Check the correctness of the student's score
	 * @param practicalMaterialMaximumScore the value of maximum score
	 * @param studentScore the value of student score
	 * @throws Exception
	 */
	private void checkExceedingMaximumScore(int practicalMaterialMaximumScore,int studentScore) throws Exception {
		if(studentScore>practicalMaterialMaximumScore){
			log.error("student score="+studentScore +"cannot exceed practical material maximum score="+practicalMaterialMaximumScore);
			throw new IllegalArgumentException("studentScore more than maximum studentScore");
		}
	}

	/**
	 * Check does the teacher contain discipline
	 * @param discipline the value for check
	 * @throws Exception
	 */
	private void checkTeacherContainsDiscipline(Discipline discipline) throws Exception {
		if(!disciplines.contains(discipline)) {
			log.error("teacher "+name+"not contains "+discipline);
			throw new Exception("The assignment is not available to the teacher");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Teacher teacher = (Teacher) o;
		return id == teacher.id && Objects.equals(name, teacher.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString(){
		return "\nname: "+name+"\ndisciplines: "+disciplines.toString();
	}
}
