package ru.sfedu.services;



import ru.sfedu.model.*;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Teacher.class, Discipline.class, StudyGroup.class, Schedule.class, Event.class, Student.class, PracticalMaterial.class,LectionMaterial.class, EducationMaterials.class})
public class Wrapper<T extends EntityBean> implements Serializable {
    @XmlElement
    @XmlElementWrapper
    private Map<Long,T> wrapper = new HashMap<>();

    public void setRecords(Map<Long, T> wrapper){
        this.wrapper = wrapper;
    }
    public Map<Long, T> getRecords(){
        return wrapper;
    }

}
