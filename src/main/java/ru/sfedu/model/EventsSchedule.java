package ru.sfedu.model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.Constants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlSeeAlso(Schedule.class)
public class EventsSchedule extends Schedule {
    @XmlTransient
    private static final Logger log = LogManager.getLogger(EventsSchedule.class);
    @XmlElement
    private long id;
    //
    // Constructors
    //
    public EventsSchedule() {
        super(Constants.TypeSchedule.EVENTS);
        log.info("create events schedule");
    }

    public EventsSchedule(long id) {
        super(id, Constants.TypeSchedule.EVENTS);
        log.info("create events schedule");
    }
}
