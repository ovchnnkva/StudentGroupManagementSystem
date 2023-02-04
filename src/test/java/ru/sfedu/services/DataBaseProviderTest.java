package ru.sfedu.services;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.sfedu.Constants;
import ru.sfedu.model.*;
import ru.sfedu.util.ConfigurationUtil;

import static org.junit.jupiter.api.Assertions.*;

class DataBaseProviderTest {
    private static final ConfigurationUtil config = new ConfigurationUtil();
    private static DataBaseProvider dataBaseProvider ;
    final static StudyGroup studyGroupPI = new StudyGroup(1,"09.03.03", "Прикладная информатика", 3);
     final static StudyGroup studyGroupPhilosophy = new StudyGroup(2,"23.01.01","Философия", 2);
     static Student studentPI ;
     static Student studentPhilosophy ;
    final static ClassesSchedule classesSchedule = new ClassesSchedule(1);
    final static SessionSchedule sessionSchedule = new SessionSchedule(2);
    final static EventsSchedule eventsSchedule = new EventsSchedule(3);
    final static Discipline architectureOfInformationSystems = new Discipline(1,"Архитектура Информационных Систем");
    final static Discipline philosophy = new Discipline(2,"Философия");
    final static Discipline systemAnalyze = new Discipline(3, "Основы системного анализа");
    final static Teacher philosophyTeacher = new Teacher(2,"Давыдов Платон Анатольевич");
    final static Teacher architectureTeacher = new Teacher(1,"Жмайлов Борис Борисович");
    final static Teacher systemAnalyzeTeacher = new Teacher(3,"Троянов Алексей Дмитриевич");
    private static PracticalMaterial practicalMaterialPI;
    private static PracticalMaterial practicalMaterialPhilosophy;
    private static LectionMaterial lectionMaterialPI;

    @BeforeAll
    static void init() throws Exception {
        studentPI = new Student(1,"Иванов Иван Иванович", "20-11-2002", studyGroupPI);
        studentPhilosophy = new Student(2,"Орлова Екатерина АНдреевна", "12-01-2002", studyGroupPhilosophy);

        dataBaseProvider = new DataBaseProvider();
        architectureTeacher.appendDiscipline(architectureOfInformationSystems);
        philosophyTeacher.appendDiscipline(philosophy);
        studyGroupPI.appendDisciplines(architectureOfInformationSystems);
        studyGroupPhilosophy.appendDisciplines(philosophy);
        systemAnalyzeTeacher.appendDiscipline(systemAnalyze);

        practicalMaterialPI =  architectureTeacher.createPracticalMaterial(1,"UML UseCase Diagram", architectureOfInformationSystems, 5, "10-10-2022 23:59").get();
        practicalMaterialPhilosophy = philosophyTeacher.createPracticalMaterial(2,"Тест", philosophy, 10, "21-11-2022 23:59").get();
        lectionMaterialPI = architectureTeacher.createLectionMaterial(1,"Методичка",architectureOfInformationSystems).get();

        classesSchedule.createEvent(1,"Архитектура Информационных систем", "Понедельник", "08:00");
        sessionSchedule.createEvent(2,"Архитектура Информационных систем", "22.01.2022","10:00");
        eventsSchedule.createEvent(3,"Собрание", "09.01.2022", "09:00");
    }
    @Test
    public void testSaveStudentRecord() throws Exception {
//        dataBaseProvider.saveStudentRecord(studentPI);
//        dataBaseProvider.saveStudentRecord(studentPhilosophy);

        assertNotNull(dataBaseProvider.getStudentRecord(studentPI.getId()));
        assertNotNull(dataBaseProvider.getStudentRecord(studentPhilosophy.getId()));
    }
    @Test
    public void testSaveExistingStudentRecord(){
        Exception exception = assertThrows(Exception.class, ()-> {
            dataBaseProvider.saveStudentRecord(studentPhilosophy);
        });

        assertEquals("record with this id has already been saved",exception.getMessage());
    }
    @Test
    public void testSaveScoresRecord() throws Exception {
//        studentPI.appendDisciplineScore(architectureOfInformationSystems,5);
//        dataBaseProvider.saveScoresStudent(studentPI.getId(), architectureOfInformationSystems.getId(), studentPI.getScores().get(architectureOfInformationSystems));

        assertNotNull(dataBaseProvider.getStudentRecord(studentPI.getId()).getScores().get(architectureOfInformationSystems));
    }
    @Test
    public void testSaveExistingScoresRecord() throws Exception {
        studentPI.appendDisciplineScore(architectureOfInformationSystems,5);Exception exception = assertThrows(Exception.class, ()-> {
            dataBaseProvider.saveScoresStudent(studentPI.getId(), architectureOfInformationSystems.getId(), studentPI.getScores().get(architectureOfInformationSystems));
        });
        assertEquals("record has already been saved",exception.getMessage());

    }
    @Test
    public void testGetStudentRecord() throws Exception {
        assertEquals(studentPhilosophy, dataBaseProvider.getStudentRecord(studentPhilosophy.getId()));
    }
    @Test
    public void testGetNonExistentStudentRecord() {
        Exception exception = assertThrows(Exception.class, ()->{
            dataBaseProvider.getStudentRecord(5);
        });
        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testUpdateStudentRecord() throws Exception {
        studentPI.setName("Иванов Андрей Иванович");
        dataBaseProvider.updateStudentRecordById(studentPI);

        assertEquals(studentPI.getName(), dataBaseProvider.getStudentRecord(studentPI.getId()).getName());
    }
    @Test
    void testSaveTeacherRecord() throws Exception {
//        dataBaseProvider.saveTeacherRecord(architectureTeacher);
//        dataBaseProvider.saveTeacherRecord(philosophyTeacher);
//        dataBaseProvider.saveTeacherRecord(systemAnalyzeTeacher);

        assertNotNull(dataBaseProvider.getTeacherRecordById(architectureTeacher.getId()));
        assertNotNull(dataBaseProvider.getTeacherRecordById(philosophyTeacher.getId()));
        assertNotNull(dataBaseProvider.getTeacherRecordById(systemAnalyze.getId()));
    }
    @Test
    void testSaveExistingTeacherRecord(){
       Exception exception = assertThrows(Exception.class, ()-> {
           dataBaseProvider.saveTeacherRecord(architectureTeacher);
       });

       assertEquals("record with this id has already been saved",exception.getMessage());
    }

    @Test
    void testGetTeacherRecordById() throws Exception {
        assertEquals(architectureTeacher,dataBaseProvider.getTeacherRecordById(architectureTeacher.getId()));
    }
    @Test
    void testGetNonExistingTeacherRecordById() {
        Exception exception = assertThrows(Exception.class, ()->{
            dataBaseProvider.getTeacherRecordById(7);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testUpdateTeacherRecord() throws Exception {
        philosophyTeacher.setName("Понтийский Платон Леонидович");
        dataBaseProvider.updateTeacherRecord(philosophyTeacher);

        assertEquals(dataBaseProvider.getTeacherRecordById(philosophyTeacher.getId()).getName(), "Понтийский Платон Леонидович");
    }
    @Test
    void  testSaveStudyGroupRecord() throws Exception {
//        dataBaseProvider.saveStudyGroupRecord(studyGroupPI);
//        dataBaseProvider.saveStudyGroupRecord(studyGroupPhilosophy);

        assertNotNull(dataBaseProvider.getStudyGroupRecordById(studyGroupPI.getId()));
        assertNotNull(dataBaseProvider.getStudentRecord(studyGroupPhilosophy.getId()));
    }
    @Test
    void testSaveExistingStudyGroup(){
        Exception exception = assertThrows(Exception.class, ()-> {
            dataBaseProvider.saveStudyGroupRecord(studyGroupPI);
        });

        assertEquals("record with this id has already been saved",exception.getMessage());
    }
    @Test
    public void testGetStudyGroup() throws Exception {
        System.out.println(dataBaseProvider.getStudyGroupRecordById(studyGroupPI.getId()).getSchedule(Constants.TypeSchedule.SESSION));
       assertEquals(studyGroupPI,dataBaseProvider.getStudyGroupRecordById(studyGroupPI.getId()));
    }
    @Test
    public void testGetNonExistingStudyGroupById(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataBaseProvider.getStudyGroupRecordById(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testUpdateStudyGroup() throws Exception {
        studyGroupPI.setSessionSchedule(sessionSchedule);

        dataBaseProvider.updateStudyGroup(studyGroupPI);

        assertEquals(studyGroupPI.getSchedule(Constants.TypeSchedule.SESSION), dataBaseProvider.getScheduleRecordByID(sessionSchedule.getId()));
    }
    @Test
    public void testSaveDisciplineRecord() throws Exception {
//        dataBaseProvider.saveDisciplineRecord(architectureOfInformationSystems);
//        dataBaseProvider.saveDisciplineRecord(philosophy);
//        dataBaseProvider.saveDisciplineRecord(systemAnalyze);

        dataBaseProvider.getDisciplineRecordById(architectureOfInformationSystems.getId());
        dataBaseProvider.getDisciplineRecordById(philosophy.getId());
        dataBaseProvider.getDisciplineRecordById(systemAnalyze.getId());
    }
    @Test
    public void testSaveExistingDisciplineRecord(){
        Exception exception = assertThrows(Exception.class, ()-> {
            dataBaseProvider.saveDisciplineRecord(architectureTeacher.getDisciplines().get(0));
        });

        assertEquals("record with this id has already been saved",exception.getMessage());
    }
    @Test
    public void testGetDisciplineByTeacherID() throws Exception {
        assertEquals(architectureTeacher.getDisciplines(),dataBaseProvider.getDisciplineRecordByTeacherID(architectureTeacher.getId()));
    }
    @Test
    public void testGetNonExistingDisciplineRecordByTeacherId() throws Exception {
      Exception exception = assertThrows(Exception.class, ()->{
        dataBaseProvider.getDisciplineRecordByTeacherID(7);
       });

       assertEquals("there is no discipline record with this teacher id", exception.getMessage());
    }
    @Test
    public void testGetDisciplineById() throws Exception {
        assertEquals(architectureOfInformationSystems,dataBaseProvider.getDisciplineRecordById(architectureOfInformationSystems.getId()).get(0));
    }
    @Test
    public void testGetNonExistingDisciplineRecordById(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataBaseProvider.getDisciplineRecordById(7);
        });

        assertEquals("there is no discipline record with this id", exception.getMessage());
    }
    @Test
    public void testSaveDisciplineInStudyGroupRecord() throws Exception {
//        dataBaseProvider.saveDisciplineInStudyGroup(studyGroupPI.getId(),systemAnalyze.getId());

        assertTrue(dataBaseProvider.getStudyGroupRecordById(studyGroupPI.getId()).getDisciplines().contains(systemAnalyze));
    }
    @Test
    public void testSaveExistingDisciplineInStudyGroup(){
        Exception exception = assertThrows(Exception.class, ()-> {
            dataBaseProvider.saveDisciplineInStudyGroup(studyGroupPI.getId(),architectureOfInformationSystems.getId());
        });

        assertEquals("record has already been saved",exception.getMessage());
    }
    @Test
    public void testUpdateDisciplineRecord() throws Exception {
        systemAnalyze.setName("Методы системного анализа");

        dataBaseProvider.updateDisciplineRecord(systemAnalyze);

        assertEquals(systemAnalyze,dataBaseProvider.getDisciplineRecordById(systemAnalyze.getId()).get(0));
    }
    @Test
    public void testSaveScheduleRecord() throws Exception {
//        dataBaseProvider.saveScheduleRecord(classesSchedule);
//        dataBaseProvider.saveScheduleRecord(sessionSchedule);
//        dataBaseProvider.saveScheduleRecord(eventsSchedule);

        assertNotNull(dataBaseProvider.getScheduleRecordByID(classesSchedule.getId()));
        assertNotNull(dataBaseProvider.getScheduleRecordByID(sessionSchedule.getId()));
        assertNotNull(dataBaseProvider.getScheduleRecordByID(eventsSchedule.getId()));
    }
    @Test
    public void testSaveExistingScheduleRecord(){
        Exception exception = assertThrows(Exception.class, ()-> {
            dataBaseProvider.saveScheduleRecord(sessionSchedule);
        });

        assertEquals("record with this id has already been saved",exception.getMessage());
    }
    @Test
    public void testGetScheduleRecordById() throws Exception {
        assertEquals(sessionSchedule , dataBaseProvider.getScheduleRecordByID(sessionSchedule.getId()));
    }
    @Test
    public void testSaveEventRecord() throws Exception {
        classesSchedule.createEvent(4,"Основы системного анализа", "Понедельник","9:50");
//        dataBaseProvider.saveEventRecord(classesSchedule.getSchedule().get(1));

        assertNotNull(dataBaseProvider.getScheduleRecordByID(classesSchedule.getId()).getSchedule().get(1));
        //Проверяем элемент под инексом 1, т.к. элемент под индексом 0 уже был инициализирован перед вызовом этого теста
    }
    @Test
    public void testSaveExistingEvent() {
        Exception exception = assertThrows(Exception.class, ()-> {
        dataBaseProvider.saveEventRecord(classesSchedule.getSchedule().get(0));
        });

        assertEquals("record with this id has already been saved",exception.getMessage());
    }
    @Test
    public void testUpdateEvent() throws Exception {
        eventsSchedule.getSchedule().get(0).setTime("9:00");

        dataBaseProvider.updateEventRecord(eventsSchedule.getSchedule().get(0));

        assertEquals(eventsSchedule.getSchedule().get(0).getTime(), dataBaseProvider.getScheduleRecordByID(eventsSchedule.getId()).getSchedule().get(0).getTime());
    }

    @Test
    public void testSavePracticalMaterialRecord() throws Exception {
//        dataBaseProvider.savePracticalMaterial(practicalMaterialPhilosophy);
//        dataBaseProvider.savePracticalMaterial(practicalMaterialPI);

        assertNotNull(dataBaseProvider.getPracticalMaterialRecordById(practicalMaterialPhilosophy.getId()));
        assertNotNull(dataBaseProvider.getPracticalMaterialRecordById(practicalMaterialPI.getId()));
    }
    @Test
    public void testSaveExistingPracticalMaterialRecord() throws Exception{
        Exception exception = assertThrows(Exception.class, ()-> {
            dataBaseProvider.savePracticalMaterial(practicalMaterialPI);
        });
        assertEquals("record with this id has already been saved",exception.getMessage());
    }

    @Test
    public void testGetPracticalMaterialRecordById() throws Exception {
        assertEquals(practicalMaterialPI,dataBaseProvider.getPracticalMaterialRecordById(practicalMaterialPI.getId()));
    }
    @Test
    public void testUpdatePracticalMaterialRecord() throws Exception {
        studentPI.commentingPracticalMaterial(practicalMaterialPI, "I'm stupid");

        dataBaseProvider.updatePracticalMaterialRecord(practicalMaterialPI);

        assertEquals(practicalMaterialPI.getStudentComment().replaceAll("\'|\"",""), dataBaseProvider.getPracticalMaterialRecordById(practicalMaterialPI.getId()).getStudentComment());
    }

    @Test
    public void testSaveLectionMaterialRecord() throws Exception {
//        dataBaseProvider.saveLectionMaterialRecord(lectionMaterialPI);

        assertNotNull(dataBaseProvider.getLectionMaterilById(lectionMaterialPI.getId()));
    }
    @Test
    public void testSaveExistingLectionMaterial(){
        Exception exception = assertThrows(Exception.class, ()-> {
            dataBaseProvider.saveLectionMaterialRecord(lectionMaterialPI);
        });

        assertEquals("record with this id has already been saved",exception.getMessage());
    }
    @Test
    public void testGetLectionMaterialById() throws Exception {
        assertEquals(lectionMaterialPI,dataBaseProvider.getLectionMaterilById(lectionMaterialPI.getId()));
    }
    @Test
    public void getGetNonExistingLectionMaterialById(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataBaseProvider.getLectionMaterilById(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void updateLectionMateriaRecord() throws Exception {
        architectureTeacher.commentingMaterial(lectionMaterialPI,"read carefully!!!");

        dataBaseProvider.updateLectionMaterialRecord(lectionMaterialPI);

        assertEquals(lectionMaterialPI.getTeacherComment(), dataBaseProvider.getLectionMaterilById(lectionMaterialPI.getId()).getTeacherComment());
    }
    @Test
    public void testDeleteRecord() throws Exception {
//        dataBaseProvider.deleteRecord(config.getConfigurationEntry(TEACHER_TABLE_DB), systemAnalyzeTeacher.getId());
//        dataBaseProvider.deleteRecord(config.getConfigurationEntry(LECTION_MATERIAL_DB), lectionMaterialPI.getId());
//
//        Exception exceptionGetTeacher = assertThrows(Exception.class, ()->{
//            dataBaseProvider.getTeacherRecordById(systemAnalyze.getId());
//        });
//        Exception exceptionGetLectionMaterial = assertThrows(Exception.class, ()->{
//            dataBaseProvider.getLectionMaterilById(lectionMaterialPI.getId());
//        });
//
//        assertEquals("there is no record with this id", exceptionGetTeacher.getMessage());
//        assertEquals("there is no record with this id", exceptionGetLectionMaterial.getMessage());

    }

    @AfterAll
    static void closeDataBase() throws Exception {
        dataBaseProvider.closeConnection();
    }
}