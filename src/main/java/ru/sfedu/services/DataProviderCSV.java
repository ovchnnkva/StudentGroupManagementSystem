package ru.sfedu.services;


import com.opencsv.*;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.model.*;
import ru.sfedu.util.ConfigurationUtil;

import static ru.sfedu.Constants.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class DataProviderCSV implements IDataProvider {
    private static Logger log = LogManager.getLogger(DataProviderCSV.class);
    private ConfigurationUtil config = new ConfigurationUtil();

    private String[] headersDiscipline = {"id", "name", "teacherId"};

    public DataProviderCSV(){
        log.debug("create dataProviderCSV...");
    }


    public <T> List<T> getAllRecords(String[] columns, Class<T> tClass, String urlToCsv)  {
        try {
            log.debug("deserialize object...");
            FileReader reader = new FileReader(urlToCsv);
            ColumnPositionMappingStrategy<T> strat = new ColumnPositionMappingStrategyBuilder<T>().build();
            strat.setType(tClass);
            strat.setColumnMapping(columns);
            CsvToBean csv = new CsvToBeanBuilder(reader).withMappingStrategy(strat).build();
            List list = csv.parse();
            log.debug("records initialization:" + list);
            return list;
        }catch(IOException e){
            log.error("csv file is empty");
        }
        return new ArrayList<>();
    }
    @Override
    public <T extends EntityBean> void deleteRecord(String file, long id, Class<T> tClass, String[] headers) throws Exception {
        List<T> list = getAllRecords(headers,tClass, file);
        list.removeAll(list.stream().filter(t->t.getId()==id).collect(Collectors.toList()));
        log.info("delete record with  id " + id);
        initDataSource(tClass,headers,file, list);
    }
    @Override
    public void deleteRecord(String nameTable, long id) throws Exception{}

    @Override
    public <T extends EntityBean> void deleteRecord(String file, long id, Class<T> tClass) throws Exception{}
    @Override
    public void saveStudyGroupRecord(StudyGroup object) throws Exception {
        List<StudyGroup> list = getAllRecords(STUDY_GROUP_HEADERS,StudyGroup.class, config.getConfigurationEntry(CSV_STUDY_GROUP));
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
        list = getAllRecords(STUDY_GROUP_HEADERS,StudyGroup.class, config.getConfigurationEntry(CSV_STUDY_GROUP));
        list.add(object);
        log.info("save study group: "+object);
        log.debug("study group list"+list);
        object.getDisciplines().stream().forEach(d-> {
            try {
                saveDisciplineInStudyGroup(object.getId(),d.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        initDataSource(StudyGroup.class, STUDY_GROUP_HEADERS,config.getConfigurationEntry(CSV_STUDY_GROUP), list);
    }



    @Override
    public void saveDisciplineInStudyGroup(long groupId, long disciplineId) throws Exception {
        List<String[]> list = getAllDisciplineInStudyGroups();
        new FileOutputStream(config.getConfigurationEntry(CSV_DISCIPLINE_IN_GROUP)).close();
        Writer writer = new FileWriter(config.getConfigurationEntry(CSV_DISCIPLINE_IN_GROUP), true);
        ICSVWriter csvWriter = new CSVWriterBuilder(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .build();
        String[] newStr = {""+groupId,""+disciplineId};
        if(list.stream().noneMatch(mass -> Arrays.equals(mass, newStr))){
            list.add(newStr);
            log.info("save discipline with id "+disciplineId+" in study group with id "+groupId);
        }
        csvWriter.writeNext(DISCIPLINE_IN_GROUP_HEADERS);
        for (String[] str: list) {
            csvWriter.writeNext(str);
        }
        csvWriter.close();
    }

    @Override
    public StudyGroup getStudyGroupRecordById(long id) throws Exception {
        List<StudyGroup> list=getAllRecords(STUDY_GROUP_HEADERS,StudyGroup.class,config.getConfigurationEntry(CSV_STUDY_GROUP));
        log.info("get study group record with id "+id);
        Optional<StudyGroup> studyGroup = list.stream().filter(t->t.getId()==id).findFirst();
        if (studyGroup.isEmpty()){
            log.error("there is no study group record with id = "+id);
            throw new Exception("there is no record with this id");
        }
        try {
            studyGroup.get().setDisciplines(getDisciplinesInStudyGroup(studyGroup.get().getId()));
        }catch (Exception e){
            log.error("this study group hasn't discipline");
        }

        try{
            studyGroup.get().setSessionSchedule(getScheduleRecordByID(studyGroup.get().getSessionScheduleId()));
        }catch(Exception e){
            log.error("no session schedule in study group with id "+studyGroup.get().getId());
        }
        try{
            studyGroup.get().setClassesSchedule(getScheduleRecordByID(studyGroup.get().getClassesScheduleId()));
        }catch(Exception e){
            log.error("no classes schedule in study group with id "+studyGroup.get().getId());
        }
        try{
            studyGroup.get().setEventsSchedule(getScheduleRecordByID(studyGroup.get().getEventsScheduleId()));
        }catch(Exception e){
            log.error("no events schedule in study group with id "+studyGroup.get().getId());
        }

        return studyGroup.get();
    }

    @Override
    public void updateStudyGroup(StudyGroup studyGroup) throws Exception {

    }

    @Override
    public List<Discipline> getDisciplinesInStudyGroup(long idGroups) throws IOException {
        List<Discipline> list = new ArrayList<>();
        FileReader filereader = new FileReader(config.getConfigurationEntry(CSV_DISCIPLINE_IN_GROUP));
        CSVReader csvReader = new CSVReaderBuilder(filereader)
             .withSkipLines(1)
             .build();
        try {
            List<String[]> allData = csvReader.readAll();
            for (String[] row : allData) {
                if (Long.parseLong(row[0]) == idGroups) {
                    log.debug("get discipline with id " + Long.parseLong(row[1]));
                    list.add(getDisciplineRecordById(Long.parseLong(row[1])).get(0));
                }
            }
            log.debug("disciplines in study group with id "+idGroups+" "+list);
        }catch(Exception e){
            log.error(e);
        }
        return list;
    }
    private List<String[]> getAllDisciplineInStudyGroups() throws IOException {
        FileReader filereader = new FileReader(config.getConfigurationEntry(CSV_DISCIPLINE_IN_GROUP));
        List<String[]> allData = new ArrayList<>();
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        try {
            allData = csvReader.readAll();
            log.info("get all discipline in study groups");
        }catch(Exception e){
            log.error(e);
        }
        return allData;
    }
    @Override
    public void saveTeacherRecord(Teacher object) throws Exception {
        List<Teacher> list = getAllRecords(TEACHER_HEADERS,Teacher.class, config.getConfigurationEntry(CSV_TEACHER));
        if(list.stream().noneMatch(s->s.getId() == object.getId())){
            log.info("save teacher: "+object);
            list.add(object);
            object.getDisciplines().stream().forEach(d-> {
                try {
                    saveDisciplineRecord(d);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            initDataSource(Teacher.class, TEACHER_HEADERS,config.getConfigurationEntry(CSV_TEACHER), list);
        }else{
            log.error("there is already teacher record with this id "+object.getId());
            throw new Exception("there is already a record with this id");
        }

    }

    @Override
    public Teacher getTeacherRecordById(long id) throws Exception {
        List<Teacher> list=getAllRecords(TEACHER_HEADERS,Teacher.class,config.getConfigurationEntry(CSV_TEACHER));
        log.info("get teacher record with id "+id);
        Optional<Teacher> teacher = list.stream().filter(t->t.getId()==id).findFirst();
        if (teacher.isEmpty()){
            throw new Exception("there is no record with this id");
        }
        try {
            teacher.get().setDisciplines(getDisciplineRecordByTeacherID(teacher.get().getId()));
        }catch (Exception e){
            log.error("this teacher hasn't discipline");
        }
       return teacher.get();
    }

    @Override
    public void updateTeacherRecord(Teacher teacher) throws Exception {

    }

    @Override
    public void saveDisciplineRecord(Discipline object) throws Exception {
        List<Discipline> list = getAllRecords(DISCIPLINE_HEADERS, Discipline.class, config.getConfigurationEntry(CSV_DISCIPLINE));
        List<Discipline> disciplines = new ArrayList<>(list.stream().filter(d -> d.getId() == object.getId()).collect(Collectors.toList()));
        List<Discipline> findDiscipline =new ArrayList<>(disciplines.stream().filter(d -> d.getTeacherId() != object.getTeacherId()).collect(Collectors.toList()));;
        if (!findDiscipline.isEmpty()){
                log.info("append discipline to teacher with id "+object.getTeacherId());
                deleteRecord(config.getConfigurationEntry(CSV_DISCIPLINE), object.getId(), Discipline.class, DISCIPLINE_HEADERS);
                list = getAllRecords(DISCIPLINE_HEADERS, Discipline.class, config.getConfigurationEntry(CSV_DISCIPLINE));
        }else if(!disciplines.isEmpty() && findDiscipline.isEmpty()){
            log.error("there is already discipline record with id "+object.getId());
            throw new Exception("there is already a record with this id");
        }
        log.debug("save discipline:" + object);
        list.add(object);
        initDataSource(Discipline.class, DISCIPLINE_HEADERS, config.getConfigurationEntry(CSV_DISCIPLINE), list);
    }


    @Override
    public List<Discipline> getDisciplineRecordById(long id) throws Exception {
        List<Discipline> list=getAllRecords(DISCIPLINE_HEADERS,Discipline.class, config.getConfigurationEntry(CSV_DISCIPLINE));
        List<Discipline> disciplines = new ArrayList<>(list.stream().filter(d -> d.getId() == id).collect(Collectors.toList()));
        if(disciplines.isEmpty()){
            log.error("there is no discpline record with id "+id);
            throw new Exception("there is no record with this id");
        }
        log.debug("get discipline with id "+id+" "+disciplines);
        return disciplines;
    }

    @Override
    public List<Discipline> getDisciplineRecordByTeacherID(long id) throws Exception {
        List<Discipline> list=getAllRecords(DISCIPLINE_HEADERS,Discipline.class, config.getConfigurationEntry(CSV_DISCIPLINE));
        List<Discipline> disciplines = new ArrayList<>(list.stream().filter(d -> d.getTeacherId() == id).collect(Collectors.toList()));
        if(disciplines.isEmpty()){
            log.error("there is no discpline record with id "+id);
            throw new Exception("there is no record with this id");
        }
        return disciplines;
    }

    @Override
    public List<Discipline> getDisciplineRecord(String sql) throws Exception {
        return null;
    }

    @Override
    public void updateDisciplineRecord(Discipline object) throws Exception {

    }

    @Override
    public void saveScheduleRecord(Schedule object) throws Exception {
        List<Schedule> list = getAllRecords(SCHEDULE_HEADERS,Schedule.class, config.getConfigurationEntry(CSV_SCHEDULE));
        if(list.stream().noneMatch(s->s.getId() == object.getId())){
            log.info("save schedule: "+object);
            list.add(object);
            object.getSchedule().stream().forEach(e-> {
                try {
                    saveEventRecord(e);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            initDataSource(Schedule.class, SCHEDULE_HEADERS,config.getConfigurationEntry(CSV_SCHEDULE), list);
        }else{
            throw new Exception("there is already a record with this id");
        }
    }

    @Override
    public Schedule getScheduleRecordByID(long id) throws Exception {
        List<Schedule> list = getAllRecords(SCHEDULE_HEADERS, Schedule.class, config.getConfigurationEntry(CSV_SCHEDULE));
        Optional<Schedule> record= list.stream().filter(s->s.getId()==id).findFirst();
        if(record.isEmpty()){
            log.error("there is no schedule record with id "+id);
            throw new Exception("there is no record with this id");
        }
        Schedule schedule = record.get();
        long scheduleId = schedule.getId();
        switch (schedule.getTypeSchedule()){
            case SESSION:schedule = new SessionSchedule();log.debug("get session schedule");break;
            case CLASSES:schedule = new ClassesSchedule();log.debug("get classes schedule");break;
            case EVENTS:schedule = new EventsSchedule();log.debug("get events schedule");break;
        }
        schedule.setId(scheduleId);
        schedule.setSchedule(getEventsRecordByScheduleId(schedule.getId()));
        return schedule;
    }

    @Override
    public void saveEventRecord(Event object) throws Exception {
        List<Event> list = getAllRecords(EVENT_HEADERS,Event.class, config.getConfigurationEntry(CSV_EVENT));
        if(!list.stream().anyMatch(s->s.getId() == object.getId())){
            log.info("save event: "+object);
            list.add(object);
            initDataSource(Event.class, EVENT_HEADERS,config.getConfigurationEntry(CSV_EVENT), list);
        }else{
            log.error("there is already event record with id "+object.getId());
            throw new Exception("there is already a record with this id");
        }
    }

    @Override
    public void updateEventRecord(Event object) throws Exception {

    }

    @Override
    public List<Event> getEventsRecordByScheduleId(long id) throws Exception {
        List<Event> list=getAllRecords(EVENT_HEADERS,Event.class, config.getConfigurationEntry(CSV_EVENT));
        List<Event> events = new ArrayList<>();
        events.addAll(list.stream().filter(e->e.getScheduleId()==id).collect(Collectors.toList()));
        if(events.isEmpty()){
            log.error("no events in schedule with id "+id);
        }
        log.info("get events by schedule with id "+id);
        return events;
    }

    @Override
    public void savePracticalMaterial(PracticalMaterial object) throws Exception {
        List<PracticalMaterial> list = new ArrayList<>();
        try {
            list = getAllRecords(PRACTICAL_HEADERS, PracticalMaterial.class, config.getConfigurationEntry(CSV_PRACTICAL_MATERIAL));
        }catch (Exception ex){
            log.error("no practical materil records");
        }
        if(list.stream().anyMatch(s->s.getId() == object.getId())){
            deleteRecord(config.getConfigurationEntry(CSV_PRACTICAL_MATERIAL), object.getId(), PracticalMaterial.class, PRACTICAL_HEADERS);
            log.info("update practical material with id "+object.getId());
        }
        try {
            list = getAllRecords(PRACTICAL_HEADERS, PracticalMaterial.class, config.getConfigurationEntry(CSV_PRACTICAL_MATERIAL));
        }catch (Exception ex){
            log.error("no practical materil records");
        }
        log.info("save practical material: "+object);
        list.add(object);
        initDataSource(PracticalMaterial.class, PRACTICAL_HEADERS,config.getConfigurationEntry(CSV_PRACTICAL_MATERIAL), list);
    }


    @Override
    public PracticalMaterial getPracticalMaterialRecordById(long id) throws Exception {
        List<PracticalMaterial> list=getAllRecords(PRACTICAL_HEADERS,PracticalMaterial.class, config.getConfigurationEntry(CSV_PRACTICAL_MATERIAL));
        Optional<PracticalMaterial> practicalMaterials = list.stream().filter(t->t.getId()==id).findFirst();
        if(practicalMaterials.isEmpty()){
            log.error("no practical material record with id "+id);
            throw new Exception("there is no record with this id");
        }
        practicalMaterials.get().setDiscipline(getDisciplineRecordById(practicalMaterials.get().getDisciplineID()).get(0));
        log.info("get practical material record by id "+id);
        return practicalMaterials.get();
    }

    @Override
    public void updatePracticalMaterialRecord(PracticalMaterial object) throws Exception {

    }

    @Override
    public void saveLectionMaterialRecord(LectionMaterial object) throws Exception {
        List<LectionMaterial> list = getAllRecords(LECTION_HEADERS,LectionMaterial.class, config.getConfigurationEntry(CSV_LECTION_MATERIAL));
        if(list.stream().anyMatch(s->s.getId() == object.getId())){
            deleteRecord(config.getConfigurationEntry(CSV_LECTION_MATERIAL), object.getId(), LectionMaterial.class,LECTION_HEADERS);
            log.info("update lection material: "+object);
        }else{
            log.error("there is already lection material record with id "+object.getId());
            throw new Exception("there is already a record with this id");
        }
        list = getAllRecords(LECTION_HEADERS,LectionMaterial.class, config.getConfigurationEntry(CSV_LECTION_MATERIAL));
        list.add(object);
        initDataSource(LectionMaterial.class, LECTION_HEADERS,config.getConfigurationEntry(CSV_LECTION_MATERIAL), list);
    }

    @Override
    public LectionMaterial getLectionMaterilById(long id) throws Exception {
        List<LectionMaterial> list=getAllRecords(LECTION_HEADERS,LectionMaterial.class, config.getConfigurationEntry(CSV_LECTION_MATERIAL));
        Optional<LectionMaterial> lectionMaterial = list.stream().filter(t->t.getId()==id).findFirst();
        if(lectionMaterial.isEmpty()){
            log.error("no lection material record with id "+id);
            throw new Exception("there is no record with this id");
        }
        log.info("get lection material with id "+id);
        lectionMaterial.get().setDiscipline(getDisciplineRecordById(lectionMaterial.get().getDisciplineID()).get(0));
        return lectionMaterial.get();
    }

    @Override
    public void updateLectionMaterialRecord(LectionMaterial object) throws Exception {

    }

    @Override
    public void saveStudentRecord(Student object) throws Exception {
        List<Student> list = getAllRecords(STUDENT_HEADERS,Student.class, config.getConfigurationEntry(CSV_STUDENT));
        if(list.stream().noneMatch(s->s.getId() == object.getId())) {
            list.add(object);
            object.getScores().entrySet().stream().forEach(e -> {
                try {
                    saveScoresStudent(object.getId(), e.getKey().getId(), e.getValue());
                } catch (Exception ex) {
                    log.error(ex);
                    throw new RuntimeException(ex);
                }
            });
            log.info("save student: "+object);
            initDataSource(Student.class, STUDENT_HEADERS,config.getConfigurationEntry(CSV_STUDENT), list);
            } else {
                log.error("there is already student record with id " + object.getId());
                throw new Exception("there is already a record with this id");
            }
    }

    @Override
    public void saveScoresStudent(long studentId, long disciplineId, int score) throws Exception {
        List<String[]> list = getAllScores();
        new FileOutputStream(config.getConfigurationEntry(CSV_SCORES)).close();
        Writer writer = new FileWriter(config.getConfigurationEntry(CSV_SCORES), true);
        ICSVWriter csvWriter = new CSVWriterBuilder(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                .build();
       if(getScoresByStudentId(studentId).isEmpty())
               csvWriter.writeNext(SCORES_HEADERS);
        list.forEach(mass-> {
            if ((Long.parseLong(mass[0])==studentId) && Long.parseLong(mass[1])==disciplineId){
                list.remove(mass);
                log.info("update scores student with id "+studentId);
            }
        });
        list.add(new String[]{""+studentId,""+disciplineId,""+score});
        log.info("save scores student with id "+studentId);
        for(String[] str:list ) {
            csvWriter.writeNext(str);
        }
        csvWriter.close();
    }

    @Override
    public Student getStudentRecord(long id) throws Exception {
        List<Student> list=getAllRecords(STUDENT_HEADERS,Student.class,config.getConfigurationEntry(CSV_STUDENT));
        log.info("get srudent record with id "+id);
        Optional<Student> student = list.stream().filter(t->t.getId()==id).findFirst();
        if (student.isEmpty()){
            log.error("there is no student record with id "+id);
            throw new Exception("there is no record with this id");
        }
        student.get().setStudyGroup(getStudyGroupRecordById(student.get().getStudyGroupId()));
        try{
            student.get().setScores(getScoresByStudentId(student.get().getId()));
        }catch (Exception e){
            log.error("this student hasn't score");
        }
        return student.get();
    }

    @Override
    public Map<Discipline, Integer> getScoresByStudentId(long idStudent) throws Exception {
        Map<Discipline, Integer> map = new HashMap<>();
        FileReader filereader = new FileReader(config.getConfigurationEntry(CSV_SCORES));
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        log.info("get scores student with id "+idStudent);
        try {
            List<String[]> allData = csvReader.readAll();
            for (String[] row : allData) {
                if (Long.parseLong(row[0]) == idStudent) {
                    log.info("get scores by discipline with id "+Long.parseLong(row[1]));
                    map.put(getDisciplineRecordById(Long.parseLong(row[1])).get(0), Integer.parseInt(row[2]));
                }
            }
        }catch(Exception e){
            log.error(e);
        }
        return map;
    }
    private List<String[]> getAllScores() throws IOException {
        FileReader filereader = new FileReader(config.getConfigurationEntry(CSV_SCORES));
        List<String[]> allData = new ArrayList<>();
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        try {
            allData = csvReader.readAll();
            log.info("get all scores");
        }catch(Exception e){
            log.error(e);
        }
        return allData;
    }
    @Override
    public void updateStudentRecordById(Student object) throws Exception {

    }

    public <T> void initDataSource(Class<T> tClass, String[] headers, String urlToCsv, List<T> list) throws IOException{
        log.info("update "+urlToCsv);
        new FileOutputStream(urlToCsv).close();
        Writer writer = new FileWriter(new File(urlToCsv), true);
        ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategyBuilder<T>().build();
        strategy.setType(tClass);
        strategy.setColumnMapping(headers);
        StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                .withMappingStrategy(strategy)
                .build();
       list.stream().forEach(obj-> {
           try {
               beanToCsv.write(obj);
           } catch (CsvDataTypeMismatchException e) {
               throw new RuntimeException(e);
           } catch (CsvRequiredFieldEmptyException e) {
               throw new RuntimeException(e);
           }
       });
        writer.close();
    }
}
