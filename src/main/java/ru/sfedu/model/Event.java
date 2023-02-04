package ru.sfedu.model;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Objects;
import java.util.regex.Pattern;


@XmlAccessorType(XmlAccessType.FIELD)
public class Event implements EntityBean {
	@XmlTransient
	private final Logger log = LogManager.getLogger(Event.class);
	@XmlElement
	private long id;
	@XmlElement
	private long scheduleId;
	@XmlElement
	private String name;
	@XmlElement
	private String time;
	@XmlElement
	private String date;
	@XmlTransient
	private Pattern pattern = Pattern.compile("([0-1]?[0-9]|2[0-3]):[0-5][0-9]");
	//Примеры: 2:20, 02:20, 12:00

	//
	// Constructors
	//
	public Event(long id, long scheduleId, String name, String date, String time) throws IllegalArgumentException{
		if ((name != "") && (pattern.matcher(time).find()) && (date != "")) {
			this.id = id;
			this.scheduleId = scheduleId;
			this.name = name;
			this.time = time;
			this.date = date;
			log.info("Create event");
		}else{
			log.error("invalid name or time or date of event");
			throw new IllegalArgumentException("Invalid name or time or date");
		}
	}
	public Event(){log.info("Create event");}


  
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

  	public void setTime(String time) {
  		this.time = time;
  }

  	public String getTime() {
  		return time;
  }

  	public void setDate(String date) {
  		this.date = date;
  }

  	public String getDate() {
  		return date;
  }
	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}

		//
		// Other methods
		//

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Event event = (Event) o;
		return id == event.id && scheduleId == event.scheduleId && name.equals(event.name) && time.equals(event.time) && date.equals(event.date);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, scheduleId, name, time, date);
	}

	public String toString(){
	  return ""+ name +" "+date+" "+time+"\n";
	}
}
