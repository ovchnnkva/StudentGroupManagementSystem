package ru.sfedu.model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.Constants;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;


@XmlAccessorType(XmlAccessType.FIELD)
public class StudyGroup implements EntityBean{
	@XmlTransient
	private static final Logger log = LogManager.getLogger(StudyGroup.class);
	@XmlElement
  	private String groupCode;
	@XmlElement
  	private long id;
	@XmlElement
  	private long classesScheduleId;
	@XmlElement
  	private long eventsScheduleId;
	@XmlElement
  	private long sessionScheduleId;
	@XmlElement
  	private int course;
	@XmlElement
  	private String specialization;
	@XmlTransient
  	private Schedule classesSchedule = new ClassesSchedule();
	@XmlTransient
  	private Schedule eventsSchedule = new EventsSchedule();
	@XmlTransient
  	private Schedule sessionSchedule=new SessionSchedule();
	@XmlElementWrapper
	@XmlElement(name = "discipline")
  	private List<Discipline> disciplines= new ArrayList<>();

  
	//
	// Constructors
	//
	public StudyGroup (long id,String groupCode, String specialization, int course) {
		this.id = id;
		this.groupCode = groupCode;
		this.specialization = specialization;
		this.course = course;
		log.info("create study group "+this);
	}
	public StudyGroup(){log.info("create study group");}
  
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
  	public void setGroupsCode(String groupCode) {
  			this.groupCode = groupCode;
  	}

  	public String getGroupsCode() {
  			return groupCode;
  	}

	public void setSpecialization(String specialization){
	  this.specialization = specialization;
  	}

  	public String getSpecialization(){
		return specialization;
  	}

	public void setCourse(int course){
		  this.course = course;
	}

	public int getCourse(){
		  return course;
  }

	public void setDisciplines(List<Discipline> disciplines) {
		this.disciplines = disciplines;
	}

	public List<Discipline> getDisciplines() {
		return disciplines;
	}

	public void setClassesSchedule(Schedule classesSchedule) {
		this.classesSchedule = classesSchedule;
		classesScheduleId = classesSchedule.getId();
	}

  	public void setEventsSchedule(Schedule eventsSchedule) {
  		this.eventsSchedule = eventsSchedule;
		  eventsScheduleId = eventsSchedule.getId();
  }

  	public void setSessionSchedule(Schedule sessionSchedule) {
  		this.sessionSchedule = sessionSchedule;
		  sessionScheduleId = sessionSchedule.getId();
  	}

	public long getClassesScheduleId() {
		return classesScheduleId;
	}

	public long getEventsScheduleId() {
		return eventsScheduleId;
	}

	public long getSessionScheduleId() {
		return sessionScheduleId;
	}

	/**
	 * Get the value of schedule by typeSchedule
	 * @param type the enum for type schedule
	 * @return Schedule
	 */
  	public Schedule getSchedule(Constants.TypeSchedule type)  {
	  	switch (type){
			case EVENTS: return eventsSchedule;
			case CLASSES: return classesSchedule;
			case SESSION: return sessionSchedule;

	    }
		return new Schedule();
  	}
  	//
	// Other methods
	//
	/**
	 * Append the value of disciplines
	 * @param discipline the new value for disciplines
	 */
	public void appendDisciplines(Discipline discipline) throws Exception {
		if(!disciplines.contains(discipline)) {
			disciplines.add(discipline);
			log.info("discipline append to "+groupCode);
		}else{
			log.error("discipline has already been added to study group "+groupCode);
			throw new Exception("discipline has already been added");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StudyGroup that = (StudyGroup) o;
		return id == that.id && course == that.course && groupCode.equals(that.groupCode) && specialization.equals(that.specialization);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupCode, id, course, specialization);
	}

	@Override
	public String toString(){
		return "code: "+groupCode+"\nspecialization: "+ specialization +"\ncourse: "+course+"\ndisciplines: "+disciplines.toString();
	}
}
