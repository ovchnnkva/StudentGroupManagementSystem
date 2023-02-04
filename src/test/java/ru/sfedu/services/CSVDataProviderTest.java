package ru.sfedu.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.sfedu.model.*;
import ru.sfedu.util.ConfigurationUtil;

import static org.junit.jupiter.api.Assertions.*;


public class CSVDataProviderTest {
    private static final ConfigurationUtil config = new ConfigurationUtil();
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
    DataProviderCSV dataProviderCSV = new DataProviderCSV();
    @BeforeAll
    static void init() throws Exception {
        studentPI = new Student(1,"Иванов Иван Иванович", "20-11-2002", studyGroupPI);
        studentPhilosophy = new Student(2,"Орлова Екатерина АНдреевна", "12-01-2002", studyGroupPhilosophy);

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
    public void testSaveTeacherRecord() throws Exception{
        DataProviderCSV dataProviderCSV = new DataProviderCSV();
//        dataProviderCSV.saveTeacherRecord(architectureTeacher);
//        dataProviderCSV.saveTeacherRecord(philosophyTeacher);
//        dataProviderCSV.saveTeacherRecord(systemAnalyzeTeacher);


        assertEquals(architectureTeacher,dataProviderCSV.getTeacherRecordById(architectureTeacher.getId()));
        assertEquals(philosophyTeacher,dataProviderCSV.getTeacherRecordById(philosophyTeacher.getId()));
        assertEquals(systemAnalyzeTeacher,dataProviderCSV.getTeacherRecordById(systemAnalyze.getId()));
    }

    @Test
    public void testSaveExistingTeacherRecord(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.saveTeacherRecord(architectureTeacher);
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }

    @Test
    public void testGetTeacherRecord() throws Exception {
        assertEquals(architectureTeacher, dataProviderCSV.getTeacherRecordById(architectureTeacher.getId()));
    }

    @Test
    public void testGetNonExistingTeacherRecord(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.getTeacherRecordById(5);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }

    @Test
    public void testSaveDisciplineRecord() throws Exception {
        Discipline discipline = new Discipline(4, "Основы программирования");
//        dataProviderCSV.saveDisciplineRecord(discipline);

        assertEquals(discipline, dataProviderCSV.getDisciplineRecordById(discipline.getId()).get(0));
    }

    @Test
    public void testSaveExistingDisciplineRecord(){
        Exception exception =  assertThrows(Exception.class, ()->{
            dataProviderCSV.saveDisciplineRecord(architectureOfInformationSystems);
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }

    @Test
    public void testGetDisciplineRecordById() throws Exception {
        assertEquals(architectureOfInformationSystems, dataProviderCSV.getDisciplineRecordById(architectureOfInformationSystems.getId()).get(0));
    }

    @Test
    public void testGetNonExistingDisciplineRecordById(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.getDisciplineRecordById(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testGetDisciplinesByTeacherId() throws Exception {
        assertEquals(philosophyTeacher.getDisciplines(),  dataProviderCSV.getDisciplineRecordByTeacherID(philosophyTeacher.getId()));
    }

    @Test
    public void testGetNonExistingDisciplineByTeacherId() throws Exception {
        Exception exception = assertThrows(Exception.class, ()->{
            assertTrue(dataProviderCSV.getDisciplineRecordByTeacherID(6).isEmpty());
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testSaveStudyGroupRecord() throws Exception {
//        dataProviderCSV.saveStudyGroupRecord(studyGroupPI);
//        dataProviderCSV.saveStudyGroupRecord(studyGroupPhilosophy);

        assertEquals(studyGroupPI, dataProviderCSV.getStudyGroupRecordById(studyGroupPI.getId()));
        assertEquals(studyGroupPhilosophy, dataProviderCSV.getStudyGroupRecordById(studyGroupPhilosophy.getId()));

    }
    @Test
    public void testSaveExistingStudyGroupRecord(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.saveStudyGroupRecord(studyGroupPI);
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }
    @Test
    public void testGetStudyGroupRecordById() throws Exception {
        assertEquals(studyGroupPI, dataProviderCSV.getStudyGroupRecordById(studyGroupPI.getId()));
    }
    @Test
    public void testGetNonExistingStudyGroupRecordById(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.getStudyGroupRecordById(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testSaveDisciplineInStudyGroup() throws Exception {
        studyGroupPI.appendDisciplines(systemAnalyze);

        dataProviderCSV.saveDisciplineInStudyGroup(studyGroupPI.getId(),systemAnalyze.getId() );

        assertEquals(studyGroupPI.getDisciplines(),dataProviderCSV.getStudyGroupRecordById(studyGroupPI.getId()).getDisciplines());
    }

    @Test
    public void testSaveStudentRecord() throws Exception {
//        dataProviderCSV.saveStudentRecord(studentPI);
//       dataProviderCSV.saveStudentRecord(studentPhilosophy);

        assertEquals(studentPI, dataProviderCSV.getStudentRecord(studentPI.getId()));
        assertEquals(studentPhilosophy, dataProviderCSV.getStudentRecord(studentPhilosophy.getId()));

    }
    @Test
    public void testSaveExistingStudentRecord(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.saveStudentRecord(studentPI);
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }
    @Test
    public void testGetStudentRecordById() throws Exception {
        assertEquals(studentPI, dataProviderCSV.getStudentRecord(studentPI.getId()));
    }
    @Test
    public void testGetNonExistingStudentRecordById(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.getStudentRecord(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testSaveScoreStudent() throws Exception {
        dataProviderCSV.saveScoresStudent(studentPI.getId(), architectureOfInformationSystems.getId(), 10);
        studentPI.appendDisciplineScore(architectureOfInformationSystems,10);

        assertEquals(studentPI.getScores(), dataProviderCSV.getStudentRecord(studentPI.getId()).getScores());
    }
    @Test
    public void testSaveScheduleRecord() throws Exception {
//        dataProviderCSV.saveScheduleRecord(classesSchedule);

        assertEquals(classesSchedule, dataProviderCSV.getScheduleRecordByID(classesSchedule.getId()));
    }
    @Test
    public void testSaveExistingScheduleRecord(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.saveScheduleRecord(classesSchedule);
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }
    @Test
    public void testGetSchedule() throws Exception {
        assertEquals(classesSchedule,dataProviderCSV.getScheduleRecordByID(classesSchedule.getId()));
    }
    @Test
    public void testGetNonExistingSchedule(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.getScheduleRecordByID(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testSaveEvent() throws Exception {
        Event event = new Event(5,classesSchedule.getId(), "Основы системного анализа", "Понедельник", "9:50");
//        dataProviderCSV.saveEventRecord(event);

        assertEquals(event,dataProviderCSV.getEventsRecordByScheduleId(event.getScheduleId()).get(1));
    }
    @Test
    public void testSaveExistingEvent(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.saveEventRecord(classesSchedule.getSchedule().get(0));
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }
    @Test
    public void testSavePracticalMaterial() throws Exception {
        practicalMaterialPI.setStudentScore(5);
        dataProviderCSV.savePracticalMaterial(practicalMaterialPI);

        assertEquals(practicalMaterialPI, dataProviderCSV.getPracticalMaterialRecordById(practicalMaterialPI.getId()));
    }
    @Test
    public void testGetPracticalMaterial() throws Exception {
        assertEquals(practicalMaterialPI, dataProviderCSV.getPracticalMaterialRecordById(practicalMaterialPI.getId()));
    }
    @Test
    public void testGetNonExistingPracticalMaterial() {
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderCSV.getPracticalMaterialRecordById(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testSaveLectionMaterial() throws Exception {
//        dataProviderCSV.saveLectionMaterialRecord(lectionMaterialPI);

        assertEquals(lectionMaterialPI, dataProviderCSV.getLectionMaterilById(lectionMaterialPI.getId()));
    }

    @Test
    public void testDelete() throws Exception {
//        dataProviderCSV.deleteRecord(config.getConfigurationEntry(CSV_TEACHER),3,Teacher.class, TEACHER_HEADERS);
//        dataProviderCSV.deleteRecord(config.getConfigurationEntry(CSV_DISCIPLINE),3, Discipline.class, DISCIPLINE_HEADERS);
//
//        Exception exceptionTeacher = assertThrows(Exception.class, ()->{
//            dataProviderCSV.getTeacherRecordById(systemAnalyze.getId());
//        });
//        Exception exceptionDiscipline = assertThrows(Exception.class, ()->{
//            dataProviderCSV.getDisciplineRecordById(systemAnalyze.getId());
//        });
//
//        assertEquals("there is no record with this id", exceptionTeacher.getMessage());
//        assertEquals("there is no record with this id", exceptionDiscipline.getMessage());

    }
}