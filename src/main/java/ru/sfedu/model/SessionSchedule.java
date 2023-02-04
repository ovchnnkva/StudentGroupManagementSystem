package ru.sfedu.model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.Constants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlSeeAlso(Schedule.class)
public class SessionSchedule extends Schedule {
    @XmlTransient
    private static final Logger log = LogManager.getLogger(SessionSchedule.class);
    @XmlElement
    private long id;

    public SessionSchedule(long id){
        super(id,Constants.TypeSchedule.SESSION);
        log.info("create session schedule");
    }
    public SessionSchedule(){
        super(Constants.TypeSchedule.SESSION);
    }
}
