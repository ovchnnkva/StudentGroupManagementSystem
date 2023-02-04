package ru.sfedu.model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.Constants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlSeeAlso(Schedule.class)
public class ClassesSchedule extends Schedule {
    @XmlTransient
    private static final Logger log = LogManager.getLogger(ClassesSchedule.class);
    @XmlElement
    private long id;
    //
    // Constructors
    //
    public ClassesSchedule(){
        super(Constants.TypeSchedule.CLASSES);
        log.info("create classes schedule");
    }
    public ClassesSchedule(long id){
        super(id,Constants.TypeSchedule.CLASSES);
        log.info("create classes schedule");
    }
}
