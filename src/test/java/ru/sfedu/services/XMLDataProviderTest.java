package ru.sfedu.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.sfedu.model.*;
import ru.sfedu.util.ConfigurationUtil;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XMLDataProviderTest {
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
    private DataProviderXML dataProviderXML= new DataProviderXML();
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
    public void testSaveTeacherRecord() throws Exception {

//        dataProviderXML.saveTeacherRecord(architectureTeacher);
//        dataProviderXML.saveTeacherRecord(philosophyTeacher);
//        dataProviderXML.saveTeacherRecord(systemAnalyzeTeacher);

        assertEquals(architectureTeacher,dataProviderXML.getTeacherRecordById(architectureTeacher.getId()));
        assertEquals(philosophyTeacher,dataProviderXML.getTeacherRecordById(philosophyTeacher.getId()));
        assertEquals(systemAnalyzeTeacher,dataProviderXML.getTeacherRecordById(systemAnalyze.getId()));

    }
    @Test
    public void testSaveExistingTeacherRecords(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderXML.saveTeacherRecord(architectureTeacher);
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }

    @Test
    public void testGetTeacherRecord() throws Exception {
        assertEquals(architectureTeacher, dataProviderXML.getTeacherRecordById(architectureTeacher.getId()));
    }

    @Test
    public void testGetNonExistingTeacherRecord(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderXML.getTeacherRecordById(5);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }

    @Test
    public void testSaveDisciplineRecord() throws Exception {
        Discipline discipline = new Discipline(4, "Основы программирования");

//        dataProviderXML.saveDisciplineRecord(discipline);

        assertEquals(discipline, dataProviderXML.getDisciplineRecordById(discipline.getId()).get(0));
    }

    @Test
    public void testSaveExistingDisciplineRecord(){
        Exception exception =  assertThrows(Exception.class, ()->{
            dataProviderXML.saveDisciplineRecord(architectureOfInformationSystems);
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }

    @Test
    public void testGetDisciplineRecordById() throws Exception {
        assertEquals(architectureOfInformationSystems, dataProviderXML.getDisciplineRecordById(architectureOfInformationSystems.getId()).get(0));
    }

    @Test
    public void testGetNonExistingDisciplineRecordById(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderXML.getDisciplineRecordById(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }

    @Test
    public void testGetDisciplinesByTeacherIdRecord() throws Exception {
        assertEquals(architectureTeacher.getDisciplines(),  dataProviderXML.getDisciplineRecordByTeacherID(architectureTeacher.getId()));
    }

    @Test
    public void testGetNonExistingDisciplineByTeacherIdRecord() throws Exception {
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderXML.getDisciplineRecordByTeacherID(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testSaveStudyGroupRecord() throws Exception {
//        dataProviderXML.saveStudyGroupRecord(studyGroupPI);

        assertEquals(studyGroupPI, dataProviderXML.getStudyGroupRecordById(studyGroupPI.getId()));
    }
    @Test
    public void testSaveExistingStudyGroupRecord(){
        Exception exception =  assertThrows(Exception.class, ()->{
            dataProviderXML.saveStudyGroupRecord(studyGroupPI);
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }
    @Test
    public void testGetStudyGroupRecord() throws Exception {
        assertEquals(studyGroupPI, dataProviderXML.getStudyGroupRecordById(studyGroupPI.getId()));
    }
    @Test
    public void testGetNonExistingStudyGroupRecord(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderXML.getStudyGroupRecordById(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testSaveScheduleRecord() throws Exception {
//        dataProviderXML.saveScheduleRecord(classesSchedule);

       assertEquals(classesSchedule,dataProviderXML.getScheduleRecordByID(classesSchedule.getId()));
    }
    @Test
    public void testSaveExistingScheduleRecord(){
        Exception exception =  assertThrows(Exception.class, ()->{
            dataProviderXML.saveScheduleRecord(classesSchedule);
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }
    @Test
    public void testGetScheduleRecord() throws Exception {
        assertEquals(classesSchedule,dataProviderXML.getScheduleRecordByID(classesSchedule.getId()));
    }
    @Test
    public void testGetNonExistingSchedule(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderXML.getScheduleRecordByID(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testSaveEventRecord() throws Exception {
        classesSchedule.createEvent(2,"Основы системного анализа", "Понедельник", "9:50");

//        dataProviderXML.saveEventRecord(classesSchedule.getSchedule().get(1));

        assertEquals(classesSchedule.getSchedule(), dataProviderXML.getEventsRecordByScheduleId(classesSchedule.getId()));
    }
    @Test
    public void testSaveStudentRecord() throws Exception {
//        dataProviderXML.saveStudentRecord(studentPI);

        assertEquals(studentPI, dataProviderXML.getStudentRecord(studentPI.getId()));
    }
    @Test
    public void testSaveExistingStudentRecord(){
        Exception exception =  assertThrows(Exception.class, ()->{
            dataProviderXML.saveStudentRecord(studentPI);
        });

        assertEquals("there is already a record with this id", exception.getMessage());
    }
    @Test
    public void testGetStudentRecord() throws Exception {
        assertEquals(studentPI,dataProviderXML.getStudentRecord(studentPI.getId()));
    }
    @Test
    public void testGetNonExistingStudentRecord(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderXML.getStudentRecord(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testSavePracticalMaterialRecord() throws Exception{
        dataProviderXML.savePracticalMaterial(practicalMaterialPI);

        assertEquals(practicalMaterialPI,dataProviderXML.getPracticalMaterialRecordById(practicalMaterialPI.getId()));
    }
    @Test
    public void testGetPracticalMaterialRecord() throws Exception {
        assertEquals(practicalMaterialPI,dataProviderXML.getPracticalMaterialRecordById(practicalMaterialPI.getId()));
    }
    @Test
    public void teatGetNonExistingPracticalMaterial(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderXML.getPracticalMaterialRecordById(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testSaveLectionMaterialRecord() throws Exception{
        dataProviderXML.saveLectionMaterialRecord(lectionMaterialPI);

        assertEquals(lectionMaterialPI,dataProviderXML.getLectionMaterilById(lectionMaterialPI.getId()));
    }
    @Test
    public void testGetLectionMaterialRecord() throws Exception {
        assertEquals(lectionMaterialPI,dataProviderXML.getLectionMaterilById(lectionMaterialPI.getId()));
    }
    @Test
    public void teatGetNonExistingLectionMaterial(){
        Exception exception = assertThrows(Exception.class, ()->{
            dataProviderXML.getLectionMaterilById(6);
        });

        assertEquals("there is no record with this id", exception.getMessage());
    }
    @Test
    public void testDeleteRecord() throws Exception {
//        dataProviderXML.deleteRecord(config.getConfigurationEntry(XML_TEACHER), systemAnalyzeTeacher.getId(), Teacher.class);
//
//        Exception exception = assertThrows(Exception.class, ()->{
//            dataProviderXML.getTeacherRecordById(systemAnalyzeTeacher.getId());
//        });
//        assertEquals("there is no record with this id", exception.getMessage());
    }

}
