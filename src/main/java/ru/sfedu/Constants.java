package ru.sfedu;

import javax.swing.plaf.PanelUI;

public class Constants {
    public static final String URL_DATA_BASE = "urlDataBase";
    public static final String USER_DATA_BASE = "userDataBase";
    public static final String PASS_DATA_BASE = "passwordDataBase";
    public static final String TEACHER_TABLE_DB = "teachersTable";
    public  static final String DISCIPLINE_TABLE_DB = "disciplineTable";
    public static final String SCHEDULE_TABLE_DB = "schedulesTable";
    public static final String EVENT_TABLE_DB = "eventsTable";
    public static final String STUDY_GROUP_DB = "studyGroupTable";
    public static final String DISCIPLINE_IN_GROUPS_DB="disciplineInStudyGroupTable";
    public static final String PRACTICAL_MATERIAL_DB = "practicalMaterialTable";
    public static final String LECTION_MATERIAL_DB = "lectionMaterialTable";
    public static final String STUDENT_DB = "studentTable";
    public static final String SCORES_DB = "studentsScoreTable";

    public static final String CSV_TEACHER = "teachersCSV";
    public static final String CSV_DISCIPLINE = "disciplineCSV";
    public static final String CSV_SCHEDULE = "schedulesCSV";
    public static final String CSV_EVENT = "eventsCSV";
    public static final String CSV_STUDY_GROUP = "studyGroupCSV";
    public static final String CSV_DISCIPLINE_IN_GROUP="disciplineInStudyGroupCSV";
    public static final String CSV_PRACTICAL_MATERIAL = "practicalMaterialCSV";
    public static final String CSV_LECTION_MATERIAL = "lectionMaterialCSV";
    public static final String CSV_STUDENT = "studentCSV";
    public static final String CSV_SCORES="studentsScoreCSV";
    public static final String[] TEACHER_HEADERS = {"id", "name"};
    public static final String[] DISCIPLINE_HEADERS = {"id", "name", "teacherId"};
    public static final String[] STUDY_GROUP_HEADERS = {"id","groupCode","course","specialization","classesScheduleId","eventsScheduleId","sessionScheduleId"};
    public static final String[] DISCIPLINE_IN_GROUP_HEADERS = {"groupId", "disciplineId"};
    public static final String[] STUDENT_HEADERS = {"id","studyGroupId","name","birthday"};
    public static final String[] SCORES_HEADERS={"studentId", "disciplineId", "score"};
    public static final String[] SCHEDULE_HEADERS ={"id", "typeSchedule"};
    public static final String[] EVENT_HEADERS = {"id","scheduleId","name","time","date"};
    public static final String[] LECTION_HEADERS = {"id","teachersFile","teacherComment", "name","disciplineID"};
    public static final String[] PRACTICAL_HEADERS = {"id","teachersFile","teacherComment", "name","disciplineID","studentFile","maximumScore","studentScore","studentComment","deadline"};

    public static final String XML_DISCIPLINE = "disciplineXML";
    public static final String XML_DISCIPLINE_IN_GROUP = "disciplineInStudyGroup";
    public static final String XML_EVENTS = "eventsXML";
    public static final String XML_LECTION = "lectionXML";
    public static final String XML_PRACTICAL = "practicalXML";
    public static final String XML_SCHEDULE = "scheduleXML";
    public static final String XML_SCORES = "scoresXML";
    public static final String XML_STUDENT = "studentXML";
    public static final String XML_STUDY_GROUP = "studyGroup";
    public static final String XML_TEACHER = "teacherXML";
    public enum TypeSchedule {
        SESSION, EVENTS, CLASSES;
    }

}
