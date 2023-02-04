package ru.sfedu.model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.annotation.*;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Discipline implements EntityBean{
	@XmlTransient
	private static final Logger log = LogManager.getLogger(Discipline.class);
	@XmlElement(name = "name")
  	private String name;
	@XmlElement(name = "idTeacher")
	private long teacherId;
	@XmlElement(name = "disciplineId")
	private long id;

	//
	// Constructors
	//
	public Discipline (long id,String name) throws IllegalArgumentException{
		this.id = id;
		if (!name.isEmpty())
			this.name = name;
		else {
			log.error("invalid name");
			throw new IllegalArgumentException("Invalid name");
		}
		log.info("discipline create "+this);
	};
	public Discipline(){log.info("discipline create");}

	//
	// Methods
	//


	//
	// Accessor methods
	//

	public void setName(String name) {
  		this.name = name;
  }

  	public String getName() {
  		return name;
  }

	public long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
	}
	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Discipline that = (Discipline) o;
		return teacherId == that.teacherId && id == that.id && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, teacherId, id);
	}

	@Override
	public String toString(){
	  return "\ndiscipline: "+name;
	}
}
