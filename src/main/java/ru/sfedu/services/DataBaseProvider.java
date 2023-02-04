package ru.sfedu.services;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.Constants;
import ru.sfedu.util.ConfigurationUtil;
import ru.sfedu.model.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import static ru.sfedu.Constants.*;




public class DataBaseProvider implements IDataProvider{
    private Connection connection;
    private ConfigurationUtil config = new ConfigurationUtil();

    private static final Logger log = LogManager.getLogger(DataBaseProvider.class);

    public DataBaseProvider() throws Exception {
         try {
            connection = DriverManager.getConnection(ConfigurationUtil.getConfigurationEntry(URL_DATA_BASE), ConfigurationUtil.getConfigurationEntry(USER_DATA_BASE), ConfigurationUtil.getConfigurationEntry( PASS_DATA_BASE));
            log.info("connect to dataBase");
        }catch(SQLException e) {
            throw new Exception(e+"\nfailed to connect");
        }
    }

    public void closeConnection() throws Exception {
        try {
            connection.close();
            log.info("connection close");
        }catch(SQLException e){
            log.info("connection already been closed");
            throw new Exception("connection already been closed");
        }
    }
    @Override
    public void deleteRecord(String nameTable, long id) throws Exception{
        String deleteSQL = "DELETE FROM " + nameTable + " WHERE id=" + id;
        try (Statement statement = connection.createStatement()){
            statement.executeUpdate(deleteSQL);
            log.info("record from table" +nameTable+"with id "+id+" delete");
        }
    }
    @Override
    public <T extends EntityBean> void deleteRecord(String file, long id, Class<T> tClass, String[] headers) throws Exception{};
    @Override
    public <T extends EntityBean> void deleteRecord(String file, long id, Class<T> tClass) throws Exception{}
    @Override
    public void saveStudyGroupRecord(StudyGroup object) throws Exception {
        String sql = "INSERT INTO "+ config.getConfigurationEntry(STUDY_GROUP_DB)+" (id, specialization, course, groupsCode, classesScheduleID, sessionScheduleID, eventsScheduleID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, object.getId());
            statement.setString(2, object.getSpecialization());
            statement.setInt(3, object.getCourse());
            statement.setString(4, object.getGroupsCode());
            statement.setLong(5, object.getClassesScheduleId());
            statement.setLong(6,object.getSessionScheduleId());
            statement.setLong(7, object.getEventsScheduleId());
            log.info("save record");
            if(statement.executeUpdate()==0){
                log.error("record can't save");
                throw new Exception();
            }
        }catch(Exception e){
            log.error("studyGroup record with id="+object.getId()+" has already been saved");
            throw new Exception("record with this id has already been saved");
        }

        object.getDisciplines().stream().forEach(d-> {
            try {
                saveDisciplineInStudyGroup(object.getId(),d.getId());
            } catch (Exception e) {
                log.error(e);
            }
        });
    }
    @Override
    public void saveDisciplineInStudyGroup(long groupId, long disciplineId) throws Exception {
        String selectSQL = "SELECT id FROM "+ config.getConfigurationEntry(DISCIPLINE_IN_GROUPS_DB) +" WHERE groupID = "+groupId+" AND disciplineID="+disciplineId;
        try(PreparedStatement statementSelect = connection.prepareStatement(selectSQL)){
            ResultSet resultSet = statementSelect.executeQuery(selectSQL);
            if(resultSet.next()){
                log.error("discipline in study group record has already been saved");
                throw new Exception("record has already been saved");
            }
        }
        String sql = "INSERT INTO "+ ConfigurationUtil.getConfigurationEntry(DISCIPLINE_IN_GROUPS_DB)+" (groupID, disciplineID) VALUES(?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1,groupId);
            statement.setLong(2,disciplineId);
            log.info("save record");
            if(statement.executeUpdate() == 0) {
                log.error("record can't save");
                throw new Exception();
            }
        }catch(Exception e){
            log.error("discipline record with groupIid="+groupId+" has already been saved");
            throw new Exception("record with this id has already been saved");
        }
    }
    @Override
    public StudyGroup getStudyGroupRecordById(long id) throws Exception {
        String selectSQL = "Select * FROM "+ config.getConfigurationEntry(STUDY_GROUP_DB)+" WHERE id = "+id;
        StudyGroup studyGroup = new StudyGroup();

        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(selectSQL);
            if (resultSet.next()) {
                studyGroup.setId(resultSet.getLong("id"));
                studyGroup.setSpecialization(resultSet.getString("specialization"));
                studyGroup.setCourse(resultSet.getInt("course"));
                studyGroup.setGroupsCode(resultSet.getString("groupsCode"));
                studyGroup.setDisciplines(getDisciplinesInStudyGroup(studyGroup.getId()));
                studyGroup.setClassesSchedule(getScheduleRecordByID(resultSet.getLong("classesScheduleID")));
                studyGroup.setSessionSchedule(getScheduleRecordByID(resultSet.getLong("sessionScheduleID")));
                studyGroup.setEventsSchedule(getScheduleRecordByID(resultSet.getLong("eventsScheduleID")));

            }
        }
        if(studyGroup.getId() == 0){
            log.error("there is no study group record with id="+id);
            throw new Exception("there is no record with this id");
        }

        return studyGroup;
    }
    @Override
    public void updateStudyGroup(StudyGroup studyGroup) throws Exception {
        String updateSql = "UPDATE "+ config.getConfigurationEntry(STUDY_GROUP_DB) +" SET specialization = '" + studyGroup.getSpecialization()+
                "',    groupsCode = '"+studyGroup.getGroupsCode()+
                "', classesScheduleID = '"+studyGroup.getClassesScheduleId()+
                "', sessionScheduleID = '" +studyGroup.getSessionScheduleId()+
                "', eventsScheduleID = '"+ studyGroup.getEventsScheduleId()+
                "' WHERE id = "+studyGroup.getId();

        try(PreparedStatement statement = connection.prepareStatement(updateSql);){
            statement.executeUpdate();
            log.info("update study group with id="+studyGroup.getId());
        }
    }
    @Override
    public List<Discipline> getDisciplinesInStudyGroup(long idGroups) throws Exception {
        String selectSQL = "Select * FROM "+ config.getConfigurationEntry(DISCIPLINE_IN_GROUPS_DB)+" WHERE groupID = "+idGroups;
        List<Discipline> disciplines = new ArrayList<>();
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
               disciplines.addAll (getDisciplineRecordById(resultSet.getLong("disciplineID")));
            }
        }
        if(disciplines.size() == 0){
            log.error("there is no discipline in study group records");
            throw new Exception("there is no records");
        }

        return disciplines;
    }
    @Override
    public void saveTeacherRecord(Teacher object) throws Exception {
        String sql = "INSERT INTO "+ config.getConfigurationEntry(TEACHER_TABLE_DB)+" (id, name) VALUES(?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, object.getId());
            statement.setString(2, object.getName());
            log.info("save record");
            if(statement.executeUpdate() == 0) {
                log.error("record can't save");
                throw new Exception();
            }
        }catch(Exception e){
            throw new Exception("record with this id has already been saved");
        }
    }
    @Override
    public Teacher getTeacherRecordById(long id) throws Exception {
        String selectSQL = "Select * FROM "+ config.getConfigurationEntry(TEACHER_TABLE_DB)+" WHERE id = "+id;
        Teacher teacher = new Teacher();
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(selectSQL);
            if (resultSet.next()) {
                teacher.setId(resultSet.getLong("id"));
                teacher.setName(resultSet.getString("name"));
                teacher.setDisciplines(getDisciplineRecordByTeacherID(teacher.getId()));
            }
        }
        if(teacher.getId() == 0){
            log.error("there is no teacher record with id="+id);
            throw new Exception("there is no record with this id");
        }

        return teacher;
    }
    @Override
    public void updateTeacherRecord(Teacher teacher) throws Exception {
        String updateSql = "UPDATE "+ config.getConfigurationEntry(TEACHER_TABLE_DB) +" SET name = '" + teacher.getName()+
                "' WHERE id = "+teacher.getId();
        try(PreparedStatement statement = connection.prepareStatement(updateSql)){
            statement.executeUpdate();
            log.info("update teacher with id="+teacher.getId());
        }
    }
    @Override
    public void saveDisciplineRecord(Discipline object) throws Exception {
        String sql = "INSERT INTO "+ config.getConfigurationEntry(DISCIPLINE_TABLE_DB)+" (id, teacherID, name) VALUES(?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, object.getId());
            statement.setLong(2,object.getTeacherId());
            statement.setString(3, object.getName());
            log.info("save record");
            if(statement.executeUpdate() == 0){
                throw new Exception();
            }
        }catch(Exception e){
            throw new Exception("record with this id has already been saved");
        }
    }
    @Override
    public  List<Discipline> getDisciplineRecordById(long id) throws Exception {
        String selectSQL = "SELECT * FROM "+ config.getConfigurationEntry(DISCIPLINE_TABLE_DB)+" WHERE id = "+id;
        List<Discipline> disciplines = getDisciplineRecord(selectSQL);
        if (disciplines.isEmpty())
            throw new Exception("there is no discipline record with this id");
        log.info("get discipline by id="+id);
        return disciplines;
    }
    @Override
    public List<Discipline> getDisciplineRecordByTeacherID(long id) throws Exception {
        String selectSQL = "SELECT * FROM "+ config.getConfigurationEntry(DISCIPLINE_TABLE_DB)+" WHERE teacherID = "+id;
        List<Discipline> disciplines = getDisciplineRecord(selectSQL);
        if (disciplines.isEmpty())
            throw new Exception("there is no discipline record with this teacher id");
        log.info("get discipline by id="+id);
        return disciplines;
    }
    @Override
    public List<Discipline> getDisciplineRecord(String sql) throws Exception {
        List<Discipline> disciplines = new ArrayList<>();
        Discipline discipline = new Discipline();
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                discipline.setId(resultSet.getInt("id"));
                discipline.setName(resultSet.getString("name"));
                discipline.setTeacherId(resultSet.getLong("teacherID"));
                if(discipline.getId()==0){
                    log.error("there is no record with this id ");
                    throw new Exception();
                }
                disciplines.add(discipline);
            }
        }

        return disciplines;
    }
    @Override
    public void updateDisciplineRecord(Discipline object) throws Exception {
        String updateSql = "UPDATE " + config.getConfigurationEntry( DISCIPLINE_TABLE_DB) + " SET name = '" + object.getName() +
                "', teacherID = '" + object.getTeacherId() +
                "' WHERE id = " + object.getId();
        try(PreparedStatement statement = connection.prepareStatement(updateSql)){
            statement.executeUpdate();
            log.info("update discipline record with id="+object.getId());
        }
    }
    @Override
    public void saveScheduleRecord(Schedule object) throws Exception {
        String sql = "INSERT INTO "+ config.getConfigurationEntry(SCHEDULE_TABLE_DB)+" (id, type) VALUES(?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, object.getId());
            statement.setString(2, object.getTypeSchedule().name());
            log.info("save record");
            if(statement.executeUpdate()==0) {
                log.info("record can't save");
                throw new Exception();
            }
        }catch(Exception e){
            log.error("schedule record with id="+object.getId()+" has already been saved");
            throw new Exception("record with this id has already been saved");
        }
        object.getSchedule().stream().forEach(e-> {
            try {
                saveEventRecord(e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    @Override
    public Schedule getScheduleRecordByID(long id) throws Exception {
        String selectSQL = "Select * FROM "+ config.getConfigurationEntry(SCHEDULE_TABLE_DB)+" WHERE id = "+id;
        Schedule schedule = new Schedule();
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(selectSQL);
            if (resultSet.next()) {
                switch (resultSet.getString("type")){
                case "EVENTS": schedule = new EventsSchedule();break;
                case "SESSION": schedule = new SessionSchedule();break;
                case "CLASSES": schedule = new ClassesSchedule();break;
            }
                schedule.setTypeSchedule( Constants.TypeSchedule.valueOf(resultSet.getString("type")));
                schedule.setId(resultSet.getInt("id"));
                schedule.setSchedule(getEventsRecordByScheduleId(schedule.getId()));
            }
        }
        if (schedule.getId() == 0){
            log.error("there is no schedule record with id "+id);
        }
        return schedule;
    }
    @Override
    public void saveEventRecord(Event object) throws Exception{
        String sql = "INSERT INTO "+ config.getConfigurationEntry(EVENT_TABLE_DB)+" (id, name, time, date, scheduleID) VALUES(?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, object.getId());
            statement.setString(2, object.getName());
            statement.setString(3, object.getTime());
            statement.setString(4, object.getDate());
            statement.setLong(5,object.getScheduleId());
            log.debug("save event record");
            if(statement.executeUpdate()==0){
                log.error("record can't save");
                throw new Exception();
            }
        }catch(Exception e){
            throw new Exception("record with this id has already been saved");
        }
    }
    @Override
    public void updateEventRecord(Event object) throws Exception {
        String updateSql = "UPDATE "+ config.getConfigurationEntry(EVENT_TABLE_DB)+" SET time='"+ object.getTime()+
                "', name = '" + object.getName()+
                "' WHERE id = "+ object.getId();
        try(PreparedStatement statement = connection.prepareStatement(updateSql)){
            statement.executeUpdate();
            log.info("update event record with id="+object.getId());
        }
    }
    @Override
    public List<Event> getEventsRecordByScheduleId(long id) throws Exception {
        String selectSQL = "SELECT * FROM "+ config.getConfigurationEntry(EVENT_TABLE_DB)+" WHERE scheduleID = "+id;
        List<Event> events = new ArrayList<>();
        Event event = new Event();
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
                event.setScheduleId(id);
                event.setName(resultSet.getString("name"));
                event.setDate(resultSet.getString("date"));
                event.setTime(resultSet.getString("time"));
                events.add(event);
            }
        }
        if(events.isEmpty()){
            log.error("there is no event record with this schedule id ="+id);
        }
        return events;
    }
    @Override
    public void savePracticalMaterial(PracticalMaterial object) throws Exception {
        String sql = "INSERT INTO "+ config.getConfigurationEntry(PRACTICAL_MATERIAL_DB)+" (id, teachersFile,teacherComment,name,disciplineID," +
                "studentFile,maximumScore,studentScore,studentComment,deadline) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";
        String teachComment = object.getTeacherComment().replaceAll("\'|\"","");
        String studComment = object.getStudentComment().replaceAll("\'|\"","");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        java.sql.Timestamp sqlDate = new java.sql.Timestamp(object.getDeadline().getTime());
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1,object.getId());
            statement.setString(2, object.getTeachersFile());
            statement.setString(3, teachComment);
            statement.setString(4, object.getName());
            statement.setLong(5, object.getDisciplineID());
            statement.setString(6, object.getStudentFile());
            statement.setInt(7,object.getMaximumScore());
            statement.setInt(8, object.getStudentScore());
            statement.setString(9, studComment);
            statement.setTimestamp(10, sqlDate);

            if(statement.executeUpdate()<0) {
                log.info("save practical material record with id="+object.getId());
                throw new Exception();
            }
            log.info("save record");
        }catch(Exception e){
            throw new Exception("record with this id has already been saved");
        }
    }
    @Override
    public PracticalMaterial getPracticalMaterialRecordById(long id) throws Exception {
        String selectSQL = "Select * FROM "+ config.getConfigurationEntry(PRACTICAL_MATERIAL_DB)+" WHERE id = "+id;
        PracticalMaterial practicalMaterial = new PracticalMaterial();
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(selectSQL);
            if (resultSet.next()) {
                practicalMaterial.setId(resultSet.getLong("id"));
                practicalMaterial.setTeachersFile(resultSet.getString("teachersFile"));
                practicalMaterial.setTeacherComment(resultSet.getString("teacherComment"));
                practicalMaterial.setName(resultSet.getString("name"));
                practicalMaterial.setDisciplineID(resultSet.getLong("disciplineID"));
                practicalMaterial.setDiscipline(getDisciplineRecordById(resultSet.getLong("disciplineID")).get(0));
                practicalMaterial.setStudentFile(resultSet.getString("studentFile"));
                practicalMaterial.setMaximumScore(resultSet.getInt("maximumScore"));
                practicalMaterial.setStudentScore(resultSet.getInt("studentScore"));
                practicalMaterial.setStudentComment(resultSet.getString("studentComment"));
                Timestamp timestamp = resultSet.getTimestamp("deadline");
                java.util.Date date = timestamp;
                practicalMaterial.setDeadline(date);
            }
        }
        if(practicalMaterial.getId() == 0){
            log.error("there is no practical material record with id="+id);
            throw new Exception("there is no record with this id");
        }
        return practicalMaterial;
    }
    @Override
    public void updatePracticalMaterialRecord(PracticalMaterial object)throws Exception{
        String teachComment = object.getTeacherComment().replaceAll("\'|\"","");
        String studComment = object.getStudentComment().replaceAll("\'|\"","");
        java.sql.Timestamp sqlDate = new java.sql.Timestamp(object.getDeadline().getTime());
        String updateSql = "UPDATE "+ config.getConfigurationEntry(PRACTICAL_MATERIAL_DB)+" SET teachersFile='"+object.getTeachersFile()+
                "', teacherComment = '" +teachComment+
                "', name = '" +object.getName()+
                "', disciplineID = " +object.getDisciplineID()+
                ", studentFile = '" +object.getStudentFile()+
                "', maximumScore = " +object.getMaximumScore()+
                ", studentScore = " +object.getStudentScore()+
                ", studentComment = '" +studComment+
                "', deadline = '" +sqlDate+
                "' WHERE id = "+object.getId();

        try(PreparedStatement statement = connection.prepareStatement(updateSql)){
            statement.executeUpdate();
            log.info("update practical material record with id="+object.getId());
        }
    }
    @Override
    public void saveLectionMaterialRecord(LectionMaterial object) throws Exception {
        String sql = "INSERT INTO "+ config.getConfigurationEntry(LECTION_MATERIAL_DB)+" (id, teachersFile,teacherComment,name,disciplineID) VALUES(?,?,?,?,?)";
        String teachComment = object.getTeacherComment().replaceAll("\'|\"","");
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1,object.getId());
            statement.setString(2, object.getTeachersFile());
            statement.setString(3, teachComment);
            statement.setString(4, object.getName());
            statement.setLong(5, object.getDisciplineID());
            if(statement.executeUpdate()<0){
                log.info("record can't save");
                throw new Exception();
            }
            log.debug("save record");
        }catch(Exception e){
            throw new Exception("record with this id has already been saved");
        }
    }
    @Override
    public LectionMaterial getLectionMaterilById(long id) throws Exception {
        String selectSQL = "Select * FROM "+ config.getConfigurationEntry(LECTION_MATERIAL_DB)+" WHERE id = "+id;
        LectionMaterial lectionMaterial = new LectionMaterial();

        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(selectSQL);
            if (resultSet.next()) {
                lectionMaterial.setId(resultSet.getLong("id"));
                lectionMaterial.setTeachersFile(resultSet.getString("teachersFile"));
                lectionMaterial.setTeacherComment(resultSet.getString("teacherComment"));
                lectionMaterial.setName(resultSet.getString("name"));
                lectionMaterial.setDisciplineID(resultSet.getLong("disciplineID"));
                lectionMaterial.setDiscipline(getDisciplineRecordById(resultSet.getLong("disciplineID")).get(0));
            }
        }
        if(lectionMaterial.getId() == 0){
            log.error("there is no record with this id in");
            throw new Exception("there is no record with this id");
        }
        return lectionMaterial;
    }
    @Override
    public void updateLectionMaterialRecord(LectionMaterial object)throws Exception{
        String teachComment = object.getTeacherComment().replaceAll("\'|\"","");
        String updateSql = "UPDATE "+ config.getConfigurationEntry(LECTION_MATERIAL_DB)+" SET teacherComment='"+teachComment+
                "' WHERE id = "+object.getId();
        try(PreparedStatement statement = connection.prepareStatement(updateSql)){
            log.info("update lection material record with id"+object.getId());
            statement.executeUpdate();
        }
    }
    @Override
    public void saveStudentRecord(Student object) throws Exception {
        String sql = "INSERT INTO "+ config.getConfigurationEntry(STUDENT_DB)+" (id, name,birthday,studyGroupID) VALUES(?,?,?,?)";
        java.sql.Date sqlDate = new java.sql.Date( object.getBirthday().getTime() );
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1,object.getId());
            statement.setString(2, object.getName());
            statement.setDate(3, sqlDate);
            statement.setLong(4,object.getStudyGroup().getId());
            if(statement.executeUpdate()==0){
                log.info("record can't save");
                throw new Exception();
            }
            log.info("save record");
        }catch(Exception e){
            throw new Exception("record with this id has already been saved");
        }
        object.getScores().entrySet().stream().forEach(entry-> {
            try {
                saveScoresStudent(object.getId(), entry.getKey().getId(), entry.getValue());
            } catch (Exception e) {
                log.error(e+"student scores already been saved");
            }
        });
    }
    @Override
    public void saveScoresStudent(long studentId, long disciplineId, int score) throws Exception {
        String selectSQL = "SELECT id FROM "+ config.getConfigurationEntry(SCORES_DB) +" WHERE studentID = "+studentId+" AND disciplineID="+disciplineId;
        try(PreparedStatement statementSelect = connection.prepareStatement(selectSQL)) {
            ResultSet resultSet = statementSelect.executeQuery(selectSQL);
            if (resultSet.next()) {
                log.error("scores record has already been saved");
                throw new Exception("record has already been saved");
            }
        }
        String sql = "INSERT INTO "+ config.getConfigurationEntry(SCORES_DB)+" (studentID, disciplineID, score) VALUES(?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, studentId);
            statement.setLong(2, disciplineId);
            statement.setInt(3,score);
            if(statement.executeUpdate()==0) {
                log.error("record can't save");
                throw new Exception("record can't save");
            }
             log.debug("save record");
        }
    }
    @Override
    public Student getStudentRecord(long id) throws Exception {
        String selectSQL = "Select * FROM "+ config.getConfigurationEntry(STUDENT_DB)+" WHERE id = "+id;
        Student student= new Student();
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(selectSQL);
            if (resultSet.next()) {
                student.setId(resultSet.getLong("id"));
                student.setName(resultSet.getString("name"));
                student.setBirthday(resultSet.getDate("birthday"));
                student.setStudyGroup(getStudyGroupRecordById(resultSet.getLong("studyGroupID")));
                try {
                    student.setScores(getScoresByStudentId(student.getId()));
                }catch(Exception e){
                    log.error(e);
                }
            }
        }
        if(student.getId() == 0){
            log.error("there is no record with this id in");
            throw new Exception("there is no record with this id");
        }
        return student;
    }
    @Override
    public Map<Discipline,Integer> getScoresByStudentId(long id) throws Exception {
        String selectSQL = "Select * FROM "+ config.getConfigurationEntry(SCORES_DB)+" WHERE studentID = "+id;
        Map<Discipline,Integer> scores = new HashMap<>();
        Discipline discipline;
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()) {
               discipline = getDisciplineRecordById(resultSet.getLong("disciplineID")).get(0);
               scores.put(discipline,resultSet.getInt("score"));
            }
        }
        if(scores.isEmpty())
            throw new Exception("there is no record with this student id");
        return scores;
    }
    @Override
    public void updateStudentRecordById(Student object) throws Exception {
        java.sql.Date sqlDate = new java.sql.Date(object.getBirthday().getTime());
        String updateSql = "UPDATE "+ config.getConfigurationEntry(STUDENT_DB)+" SET name ='"+object.getName()+
                "', birthday = '"+sqlDate+
                "', studyGroupId= '"+object.getStudyGroup().getId()+
                "' WHERE id = "+object.getId();
        try(PreparedStatement statement = connection.prepareStatement(updateSql)){
            log.info("update student record with id="+object.getId());
            statement.executeUpdate();
        }
    }
}
