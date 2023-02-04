package ru.sfedu.services;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.model.*;
import ru.sfedu.util.ConfigurationUtil;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static ru.sfedu.Constants.*;


public class DataProviderXML implements IDataProvider {

    private static Logger log = LogManager.getLogger(DataProviderXML.class);

    private Wrapper wrapper = new Wrapper<>();
    private JAXBContext context;
    private ConfigurationUtil config = new ConfigurationUtil();

    public DataProviderXML()  {
        log.debug("create dataProviderXML...");
    }

    public <T extends EntityBean> Wrapper<T> getAllRecords(String urlToXml) {
        try{
            log.debug("deserialize object...");
            context = JAXBContext.newInstance(Wrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Wrapper<T> wrap = (Wrapper<T>) unmarshaller.unmarshal(new InputStreamReader(new FileInputStream(urlToXml), StandardCharsets.UTF_8));
            log.debug("records initialization:" + wrapper.getRecords().values());
            return wrap;
        } catch (JAXBException e) {
            log.error(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new Wrapper<>();
    }

    @Override
    public <T extends EntityBean> void deleteRecord(String file, long id, Class<T> tClass, String[] headers) throws Exception {

    }

    @Override
    public <T extends EntityBean> void deleteRecord(String file, long id, Class<T> tClass) throws Exception {
        Wrapper<T> wrapper = getAllRecords(file);
        Optional<T> teacher = wrapper.getRecords().values().stream().filter(t->t.getId()==id).findFirst();
        if (teacher.isEmpty()){
            log.error("there is no record with id "+id+"in "+file);
            throw new Exception("there is no record with this id");
        }
        wrapper.getRecords().remove(teacher.get().getId());

        log.info("delete record with id " + id);
        initDataSource(file, wrapper);
    }
    @Override
    public void deleteRecord(String nameTable, long id) throws Exception {

    }

    @Override
    public void saveTeacherRecord(Teacher object) throws Exception {
        Wrapper<Teacher> wrapper = getAllRecords(config.getConfigurationEntry(XML_TEACHER));
        if(!wrapper.getRecords().values().stream().anyMatch(s->((Teacher)s).getId() ==  object.getId())) {
            wrapper.getRecords().put(object.getId(),object);
            log.debug(wrapper.getRecords());
            object.getDisciplines().stream().forEach(d-> {
                try {
                    saveDisciplineRecord(d);
                } catch (Exception e) {
                    log.error(e);
                    throw new RuntimeException(e);
                }
            });
            log.info("save teacher record with id "+object.getId());
            initDataSource(config.getConfigurationEntry(XML_TEACHER), wrapper);
        }else{
            throw new Exception("there is already a record with this id");
        }
    }
    @Override
    public Teacher getTeacherRecordById(long id) throws Exception {
        Wrapper<Teacher> wrapper = getAllRecords(config.getConfigurationEntry(XML_TEACHER));
        Optional<Teacher> teacher = wrapper.getRecords().values().stream().filter(t->t.getId()==id).findFirst();
        if (teacher.isEmpty()){
            log.error("there is no teacher record with id "+id);
            throw new Exception("there is no record with this id");
        }
        try {
            teacher.get().setDisciplines(getDisciplineRecordByTeacherID(teacher.get().getId()));
        }catch(Exception e){
            log.error("this teacher hasn't discipline");
        }
        return teacher.get();
    }

    @Override
    public void updateTeacherRecord(Teacher teacher) throws Exception {

    }

    @Override
    public void saveDisciplineRecord(Discipline object) throws Exception {
        Wrapper<Discipline> wrapper = getAllRecords(config.getConfigurationEntry(XML_DISCIPLINE));
        List<Discipline> disciplines = wrapper.getRecords().values().stream().filter(s->s.getId() == object.getId()).collect(Collectors.toList());
        if (!disciplines.isEmpty() && disciplines.get(0).getTeacherId()!=object.getTeacherId()){
            deleteRecord(config.getConfigurationEntry(XML_DISCIPLINE), object.getId(), Teacher.class);
        }else if(!disciplines.isEmpty()){
            log.error("there is no discipline record with id "+object.getId());
            throw new Exception("there is already a record with this id");
        }
        wrapper = getAllRecords(config.getConfigurationEntry(XML_DISCIPLINE));
        wrapper.getRecords().put(object.getId(), object);
        initDataSource(config.getConfigurationEntry(XML_DISCIPLINE),wrapper);
        log.info("save discipline record with id "+object.getId());

    }
    @Override
    public List<Discipline> getDisciplineRecordByTeacherID(long teacherId) throws Exception {
        Wrapper<Discipline> wrapper  = getAllRecords(config.getConfigurationEntry(XML_DISCIPLINE));
        List<Discipline> disciplines = new ArrayList<>();
        disciplines.addAll( wrapper.getRecords().values().stream().filter(d->d.getTeacherId()==teacherId).collect(Collectors.toList()));
        if(disciplines.isEmpty()){
            log.error("there is no discipline record with teacher id "+teacherId);
            throw new Exception("there is no record with this id");
        }
        return disciplines;
    }
    @Override
    public List<Discipline> getDisciplineRecordById(long id) throws Exception {
        Wrapper<Discipline> wrapper  = getAllRecords(config.getConfigurationEntry(XML_DISCIPLINE));
        log.debug(wrapper.getRecords().values());
        List<Discipline> disciplines =wrapper.getRecords().values().stream().filter(d -> d.getId() == id).collect(Collectors.toList());
        log.debug(disciplines);
        if(disciplines.isEmpty()){
            log.error("there is no discipline record with id "+id);
            throw new Exception("there is no record with this id");
        }
        return disciplines;
    }

    @Override
    public void saveStudyGroupRecord(StudyGroup object) throws Exception {
        Wrapper<StudyGroup> wrapper = getAllRecords(config.getConfigurationEntry(XML_STUDY_GROUP));
        List<StudyGroup> list =wrapper.getRecords().values().stream().filter(s->s.getId() ==  object.getId()).collect(Collectors.toList());
        List<StudyGroup> disciplinesInXml = list.stream().filter(groups->!groups.getDisciplines().equals(object.getDisciplines())).collect(Collectors.toList());
        if(!disciplinesInXml.isEmpty()){
            log.info("update discipline in study group with id "+object.getId());
            deleteRecord(config.getConfigurationEntry(XML_STUDY_GROUP), object.getId(), StudyGroup.class);
        }
        wrapper = getAllRecords(config.getConfigurationEntry(XML_STUDY_GROUP));
        list =wrapper.getRecords().values().stream().filter(s->s.getId() ==  object.getId()).collect(Collectors.toList());
        Optional<StudyGroup> studyGroup = list.stream().filter(s->s.getId() == object.getId()).findFirst();
        if(!studyGroup.isEmpty()){
            if((studyGroup.get().getSessionScheduleId() == object.getSessionScheduleId()) && (studyGroup.get().getClassesScheduleId() == object.getClassesScheduleId()) && (studyGroup.get().getEventsScheduleId() == object.getEventsScheduleId())) {
                log.error("there is already a study record with id "+object.getId());
                throw new Exception("there is already a record with this id");
            }else {
                log.info("study group with id "+object.getId()+" has new schedule");
                deleteRecord(config.getConfigurationEntry(CSV_STUDY_GROUP), object.getId(), StudyGroup.class, STUDY_GROUP_HEADERS);
            }

        }

        wrapper.getRecords().put(object.getId(),object);
        log.info("save study group: "+object);
        initDataSource(config.getConfigurationEntry(XML_STUDY_GROUP), wrapper);
    }

    @Override
    public void saveDisciplineInStudyGroup(long groupId, long disciplineId) throws Exception {

    }

    @Override
    public StudyGroup getStudyGroupRecordById(long id) throws Exception {
        Wrapper<StudyGroup> wrapper  = getAllRecords(config.getConfigurationEntry(XML_STUDY_GROUP));
        List<StudyGroup> studyGroups = new ArrayList<>();
        studyGroups.addAll(wrapper.getRecords().values().stream().filter(d->d.getId()==id).collect(Collectors.toList()));
        if(studyGroups.isEmpty()){
            log.error("there is ni study group with id "+id);
            throw new Exception("there is no record with this id");
        }
        return studyGroups.get(0);
    }


    @Override
    public void updateStudyGroup(StudyGroup studyGroup) throws Exception {

    }

    @Override
    public List<Discipline> getDisciplinesInStudyGroup(long idGroups) throws Exception {
        return new ArrayList<>();
    }



    @Override
    public List<Discipline> getDisciplineRecord(String sql) throws Exception {
        return new ArrayList<>();
    }

    @Override
    public void updateDisciplineRecord(Discipline object) throws Exception {

    }

    @Override
    public void saveScheduleRecord(Schedule object) throws Exception {
        Wrapper<Schedule> wrapper = getAllRecords(config.getConfigurationEntry(XML_SCHEDULE));
        List<Schedule> list =wrapper.getRecords().values().stream().filter(s->s.getId() ==  object.getId()).collect(Collectors.toList());
        if(list.isEmpty()) {
            wrapper.getRecords().put(object.getId(),object);
            log.info("save schedule record with id "+object.getId());
            initDataSource(config.getConfigurationEntry(XML_SCHEDULE), wrapper);
            object.getSchedule().stream().forEach(e->{
                try {
                    saveEventRecord(e);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }else{
            log.error("there is already schedule record with id "+object.getId());
            throw new Exception("there is already a record with this id");
        }
    }

    @Override
    public Schedule getScheduleRecordByID(long id) throws Exception {
        Wrapper<Schedule> wrapper  = getAllRecords(config.getConfigurationEntry(XML_SCHEDULE));
        List<Schedule> schedules = new ArrayList<>();
        schedules.addAll(wrapper.getRecords().values().stream().filter(d->d.getId()==id).collect(Collectors.toList()));
        if(schedules.isEmpty()){
            log.error("there is no schedule with id "+id);
            throw new Exception("there is no record with this id");
        }
        Schedule schedule = schedules.get(0);
        long scheduleId = schedule.getId();
        switch (schedule.getTypeSchedule()){
            case SESSION:schedule = new SessionSchedule();log.debug("get session schedule");break;
            case CLASSES:schedule = new ClassesSchedule();log.debug("get classes schedule");break;
            case EVENTS:schedule = new EventsSchedule();log.debug("get events schedule");break;
        }
        schedule.setId(scheduleId);
        schedule.setSchedule(getEventsRecordByScheduleId(scheduleId));
        return schedule;
    }

    @Override
    public void saveEventRecord(Event object) throws Exception {
        Wrapper<Event> wrapper = getAllRecords(config.getConfigurationEntry(XML_EVENTS));
        List<Event> list =wrapper.getRecords().values().stream().filter(s->s.getId() ==  object.getId()).collect(Collectors.toList());
        if(list.isEmpty()) {
            wrapper.getRecords().put(object.getId(),object);
            log.info("save event record with id "+object.getId());
            initDataSource(config.getConfigurationEntry(XML_EVENTS), wrapper);
        }else{
            log.error("there is no event record with id "+object.getId());
            throw new Exception("there is already a record with this id");
        }
    }

    @Override
    public void updateEventRecord(Event object) throws Exception {

    }

    @Override
    public List<Event> getEventsRecordByScheduleId(long scheduleId) throws Exception {
        Wrapper<Event> wrapper  = getAllRecords(config.getConfigurationEntry(XML_EVENTS));
        List<Event> events = new ArrayList<>(wrapper.getRecords().values().stream().filter(s -> s.getScheduleId() == scheduleId).collect(Collectors.toList()));
        if(events.isEmpty()){
            log.error("there is no event record with schedule id "+scheduleId);
            throw new Exception("there is no record with this id");
        }
        return events;
    }

    @Override
    public void savePracticalMaterial(PracticalMaterial object) throws Exception {
        Wrapper<PracticalMaterial> wrapper = getAllRecords(config.getConfigurationEntry(XML_PRACTICAL));
        List<PracticalMaterial> list =wrapper.getRecords().values().stream().filter(s->s.getId() ==  object.getId()).collect(Collectors.toList());
        if(!list.isEmpty()) {
            deleteRecord(config.getConfigurationEntry(XML_PRACTICAL),object.getId(),PracticalMaterial.class);
            log.info("update practical material with id "+object.getId());
        }
        wrapper = getAllRecords(config.getConfigurationEntry(XML_PRACTICAL));
        wrapper.getRecords().put(object.getId(),object);
        log.info("save practical material record with id "+object.getId());
        initDataSource(config.getConfigurationEntry(XML_PRACTICAL), wrapper);
    }

    @Override
    public PracticalMaterial getPracticalMaterialRecordById(long id) throws Exception {
        Wrapper<PracticalMaterial> wrapper  = getAllRecords(config.getConfigurationEntry(XML_PRACTICAL));
        List<PracticalMaterial> practicalMaterials = wrapper.getRecords().values().stream().filter(p -> p.getId() == id).collect(Collectors.toList());
        if(practicalMaterials.isEmpty()){
            log.error("there is no practical material record with id"+id);
            throw new Exception("there is no record with this id");
        }
        practicalMaterials.get(0).setDiscipline(getDisciplineRecordById(practicalMaterials.get(0).getDisciplineID()).get(0));
        return practicalMaterials.get(0);
    }

    @Override
    public void updatePracticalMaterialRecord(PracticalMaterial object) throws Exception {

    }

    @Override
    public void saveLectionMaterialRecord(LectionMaterial object) throws Exception {
        Wrapper<LectionMaterial> wrapper = getAllRecords(config.getConfigurationEntry(XML_LECTION));
        List<LectionMaterial> list =wrapper.getRecords().values().stream().filter(s->s.getId() ==  object.getId()).collect(Collectors.toList());
        if(!list.isEmpty()) {
            deleteRecord(config.getConfigurationEntry(XML_LECTION),object.getId(),LectionMaterial.class);
            log.info("update practical material with id "+object.getId());
        }
        wrapper = getAllRecords(config.getConfigurationEntry(XML_LECTION));
        wrapper.getRecords().put(object.getId(),object);
        log.info("save practical material record with id "+object.getId());
        initDataSource(config.getConfigurationEntry(XML_LECTION), wrapper);
    }

    @Override
    public LectionMaterial getLectionMaterilById(long id) throws Exception {
        Wrapper<LectionMaterial> wrapper  = getAllRecords(config.getConfigurationEntry(XML_LECTION));
        List<LectionMaterial> lectionMaterials = wrapper.getRecords().values().stream().filter(p -> p.getId() == id).collect(Collectors.toList());
        if(lectionMaterials.isEmpty()){
            log.error("there is no lection material record with id "+id);
            throw new Exception("there is no record with this id");
        }
        lectionMaterials.get(0).setDiscipline(getDisciplineRecordById(lectionMaterials.get(0).getDisciplineID()).get(0));
        return lectionMaterials.get(0);
    }

    @Override
    public void updateLectionMaterialRecord(LectionMaterial object) throws Exception {

    }

    @Override
    public void saveStudentRecord(Student object) throws Exception {
        Wrapper<Student> wrapper = getAllRecords(config.getConfigurationEntry(XML_STUDENT));
        List<Student> list =wrapper.getRecords().values().stream().filter(s->s.getId() ==  object.getId()).collect(Collectors.toList());
        if((!list.isEmpty())&&!list.get(0).getScores().equals(object.getScores())) {
            deleteRecord(config.getConfigurationEntry(XML_STUDENT), list.get(0).getId(), Student.class);
        } else if(!list.isEmpty()){
            log.error("there is already student record with id " + object.getId());
            throw new Exception("there is already a record with this id");
        }
        wrapper = getAllRecords(config.getConfigurationEntry(XML_STUDENT));
        wrapper.getRecords().put(object.getId(),object);
        log.info("save student with id "+object.getId());
        initDataSource(config.getConfigurationEntry(XML_STUDENT),wrapper);
    }

    @Override
    public void saveScoresStudent(long studentId, long disciplineId, int score) throws Exception {

    }

    @Override
    public Student getStudentRecord(long id) throws Exception {
        Wrapper<Student> wrapper  = getAllRecords(config.getConfigurationEntry(XML_STUDENT));
        List<Student> studentFind = wrapper.getRecords().values().stream().filter(s->s.getId() == id).collect(Collectors.toList());
        if(studentFind.isEmpty()){
            log.error("there is no student record with id "+id);
            throw new Exception("there is no record with this id");
        }
        Student student = studentFind.get(0);
        student.setStudyGroup(getStudyGroupRecordById(student.getStudyGroupId()));
        return student;
    }

    @Override
    public Map<Discipline, Integer> getScoresByStudentId(long id) throws Exception {
        return null;
    }

    @Override
    public void updateStudentRecordById(Student object) throws Exception {

    }

    public <T> void initDataSource(String urlToXml, Wrapper wrapper) throws IOException, JAXBException {
        FileOutputStream file = new FileOutputStream(new File(urlToXml));
        log.debug("update data source "+urlToXml);
        context = JAXBContext.newInstance(Wrapper.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(wrapper,file);
    }

}
