package ru.sfedu.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.sfedu.Constants;

import static org.junit.jupiter.api.Assertions.*;

class StudentGroupManagementSystemTest {
        final static StudyGroup studyGroupPI = new StudyGroup(1,"09.03.03", "Прикладная информатика", 3);
        final static StudyGroup studyGroupPhilosophy = new StudyGroup(2,"23.01.01","Философия", 2);
        static Student studentPI ;
        static Student studentPhilosophy;
        final static ClassesSchedule classesSchedulePI = new ClassesSchedule(1);
        final static SessionSchedule sessionSchedulePI = new SessionSchedule(2);
        final static EventsSchedule eventsSchedulePhilosophy = new EventsSchedule(3);
        final static Discipline architectureOfInformationSystems = new Discipline(1,"Архитектура Информационных Систем");
        final static Discipline philosophy = new Discipline(2,"Философия");
        final static Discipline systemAnalyze = new Discipline(3, "Основы системного анализа");
        final static Teacher philosophyTeacher = new Teacher(2,"Давыдов Платон Анатольевич");
         final static Teacher architectureTeacher = new Teacher(1,"Жмайлов Борис Борисович");
         private static PracticalMaterial practicalMaterialPI;
        private static LectionMaterial lectionMaterialPI;
        private static PracticalMaterial practicalMaterialPhilosophy;

    @BeforeAll
    static void init() throws Exception {
        studentPI = new Student(1,"Иванов Иван Иванович", "20-11-2002", studyGroupPI);
        studentPhilosophy = new Student(2,"Орлова Екатерина АНдреевна", "12-01-2002", studyGroupPhilosophy);
        architectureTeacher.appendDiscipline(architectureOfInformationSystems);
        philosophyTeacher.appendDiscipline(philosophy);
        studyGroupPI.appendDisciplines(architectureOfInformationSystems);
        studyGroupPI.appendDisciplines(systemAnalyze);
        studyGroupPhilosophy.appendDisciplines(philosophy);

        practicalMaterialPI =  architectureTeacher.createPracticalMaterial(1,"UML UseCase Diagram", architectureOfInformationSystems, 5, "10-10-2022 23:59").get();
        practicalMaterialPhilosophy = philosophyTeacher.createPracticalMaterial(2,"Тест", philosophy, 10, "21-11-2022 23:59").get();
        lectionMaterialPI = architectureTeacher.createLectionMaterial(1,"Методичка", architectureOfInformationSystems).get();

    }
    @Test
    public void testCreateSchedule() throws Exception{
        classesSchedulePI.createEvent(1,"Архитектура Информационных систем", "Понедельник", "08:00");
        sessionSchedulePI.createEvent(2,"Архитектура Информационных систем", "22.01.2022","10:00");
        eventsSchedulePhilosophy.createEvent(3,"Собрание", "09.01.2022", "09:00");

        studyGroupPI.setSessionSchedule(sessionSchedulePI);
        studyGroupPI.setClassesSchedule(classesSchedulePI);
        studyGroupPhilosophy.setEventsSchedule(eventsSchedulePhilosophy);

        assertNotNull(classesSchedulePI.getSchedule());
        assertNotNull(sessionSchedulePI.getSchedule());
        assertNotNull(eventsSchedulePhilosophy.getSchedule());
        assertNotNull(studentPI.returnInformationAboutSchedule(Constants.TypeSchedule.CLASSES));
        assertNotNull(studentPI.returnInformationAboutSchedule(Constants.TypeSchedule.SESSION));
        assertNotNull(studentPhilosophy.returnInformationAboutSchedule(Constants.TypeSchedule.EVENTS));
    }
    @Test
    public void testCreateEmptyEventInSchedule() throws Exception {
        int expected = sessionSchedulePI.getSchedule().size();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()-> {
                    sessionSchedulePI.createEvent(0,"", "", "");
                }
        );

        assertEquals("Invalid name or time or date", exception.getMessage());
        assertEquals(expected,sessionSchedulePI.getSchedule().size());
    }
    @Test
    public void testCreateIncorrectEventInSchedule() {
        int expected = classesSchedulePI.getSchedule().size();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()-> {
                    classesSchedulePI.createEvent(1,"Архитектура информационных систем", "22.09.2022", "14:00");
                }
        );

        assertEquals(expected, classesSchedulePI.getSchedule().size());
        assertEquals("The date in the class schedule should be the day of the week", exception.getMessage());
    }
    @Test
    public void testStudentGetSchedule() throws Exception {
        studyGroupPI.setClassesSchedule(classesSchedulePI);

        assertEquals(classesSchedulePI, studentPI.returnInformationAboutSchedule(Constants.TypeSchedule.CLASSES));
    }
    @Test
    public void testAttachFileToEducationMaterial() throws Exception {
        architectureTeacher.attachFileToMaterial(practicalMaterialPI,"/arch_pract");
        architectureTeacher.attachFileToMaterial(lectionMaterialPI, "/lection");

        assertEquals("/arch_pract",practicalMaterialPI.getTeachersFile());
        assertEquals("/lection",lectionMaterialPI.getTeachersFile());
    }
    @Test
    public void testWrongAttachFileToEducationMaterial() {
        Exception exception =assertThrows(Exception.class, ()->{
            architectureTeacher.attachFileToMaterial(practicalMaterialPhilosophy,"/...");
        });

        assertEquals("The assignment is not available to the teacher", exception.getMessage());
    }
    @Test
    public void testTurnPracticalMaterials() throws Exception {
        studentPI.attachFileToPracticalMaterial(practicalMaterialPI,"/uml");

        assertEquals("/uml",practicalMaterialPI.returnStudentFile());
    }
    @Test
    public void testWrongTurnPracticalMaterial() {
        Exception exception = assertThrows(Exception.class,()->{
            PracticalMaterial practicalMaterial = philosophyTeacher.createPracticalMaterial(3,"Тест", philosophy, 10, "21-11-2022 23:59").get();
            studentPI.attachFileToPracticalMaterial(practicalMaterial,"/test.txt");
            }
        );

        assertEquals("The assignment is not available to the student", exception.getMessage());
    }
    @Test
    public void testEvaluatePracticalMaterial() throws Exception {
        architectureTeacher.evaluateCompletedPracticalMaterial(practicalMaterialPI, studentPI,1);

        assertTrue(studentPI.getScores().containsKey(practicalMaterialPI.getDiscipline()));
        assertEquals(1,studentPI.getScores().get(practicalMaterialPI.getDiscipline()));
    }
    @Test
    public void testWrongEvaluatePracticalMaterial() {
        Exception exception = assertThrows(Exception.class, ()-> {
                    architectureTeacher.evaluateCompletedPracticalMaterial(practicalMaterialPhilosophy, studentPhilosophy, 10);
                }
        );

        assertEquals("The assignment is not available to the teacher", exception.getMessage());
        assertEquals(0,practicalMaterialPhilosophy.getStudentScore());

    }
    @Test
    public void testScoreMoreMaximumEvaluatePracticalMaterial(){
        Exception exception = assertThrows(Exception.class, ()-> {
                philosophyTeacher.evaluateCompletedPracticalMaterial(practicalMaterialPhilosophy, studentPhilosophy, 20);
            }
        );

        assertEquals("studentScore more than maximum studentScore", exception.getMessage());
        assertEquals(0,practicalMaterialPhilosophy.getStudentScore());
    }
    @Test
    public void testAppendScoreToStudent() throws Exception {
        int expected = studentPI.getScores().get(architectureOfInformationSystems);
        studentPI.appendDisciplineScore(architectureOfInformationSystems,10);
        expected+=10;

        assertEquals(expected, studentPI.getScores().get(architectureOfInformationSystems));
    }
    @Test
    public void testAppendScoreMoreMaximumToStudent(){
        Exception exception = assertThrows(Exception.class, ()-> {
                    studentPI.appendDisciplineScore(architectureOfInformationSystems,110);
                }
        );

        assertEquals("total score cannot exceed 100", exception.getMessage());
    }
    @Test
    public void testReturnInformationAboutPerformance() throws Exception {
        studentPI.appendDisciplineScore(systemAnalyze, 30);

        assertEquals(systemAnalyze+" score:30",studentPI.returnInformationAboutPerformance() );
    }
    @Test
    public void testStudentCommentingPracticalMaterial() throws Exception {
        studentPI.commentingPracticalMaterial(practicalMaterialPI,"I'm stupid");

        assertEquals("I'm stupid",practicalMaterialPI.getStudentComment() );
    }

    @Test
    public void testWrongStudentCommentingPractiicalMaterial() {
        Exception exception = assertThrows(Exception.class,()-> {
            studentPI.commentingPracticalMaterial(practicalMaterialPhilosophy, "hi");
        });
        assertEquals("The assignment is not available to the student", exception.getMessage());
    }
    @Test
    public void testTeacherCommentingEducationMaterial() throws Exception{
        architectureTeacher.commentingMaterial(practicalMaterialPI,"That's horrible...");

        assertEquals("That's horrible...", practicalMaterialPI.getTeacherComment());
    }

    @Test
    public void  testWrongTeacherCommentingEducationMaterial(){
        Exception exception = assertThrows(Exception.class,()-> {
            architectureTeacher.commentingMaterial(practicalMaterialPhilosophy, "hi");
        });
        assertEquals("The assignment is not available to the teacher", exception.getMessage());
    }
}