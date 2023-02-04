package ru.sfedu.model;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.services.DataBaseProvider;
import ru.sfedu.services.DataProviderCSV;
import ru.sfedu.services.DataProviderXML;
import ru.sfedu.services.IDataProvider;
import ru.sfedu.util.ConfigurationUtil;


import static ru.sfedu.Constants.*;


public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);
    private static IDataProvider dataProvider = new DataProviderCSV();
    private static ConfigurationUtil config = new ConfigurationUtil();
    public static void main(String[] args) throws Exception {
        log.info("Logger configuration");

        Options options = new Options();
        Option optionStudent = new Option("student", true, "create student");
        optionStudent.setArgs(7);
        Option optionStudyGroup = new Option("studygroup", true, "create study group");
        Option optionTeacher = new Option("teacher", true, "create teacher");
        Option optionDiscipline = new Option("discipline", true, "create discipline");
        Option optionAppendDisciplineToTeacher = new Option("appenddisciplinetecher", true, "append discipline to teacher");
        optionAppendDisciplineToTeacher.setArgs(2);
        Option optionAppendDisciplineToGroup = new Option("appenddisciplinegroup", true, "append discipline to study group");
        optionAppendDisciplineToGroup.setArgs(2);
        Option optionSchedule = new Option("schedule", true, "create schedule");
        optionSchedule.setArgs(2);
        Option optionEvent = new Option("event", true, "create event in schedule");
        Option optionSetScheduleToGroup = new Option("groupschedule", true, "set schedule in study group");
        optionSetScheduleToGroup.setArgs(2);
        Option optionLection = new Option("lection", true, "create lection material");
        Option optionPractical = new Option("practical", true, "create practical material");
        Option optionAttachFileStudent = new Option("filestudent", true, "attach student file to practical material");
        optionAttachFileStudent.setArgs(3);
        Option optionAttachFileTeacher = new Option("fileteacher", true, "attach teacher file to education material");
        optionAttachFileTeacher.setArgs(4);
        Option optionTeacherCommenting = new Option("commentteacher", true, "add teacher comment to education material");
        Option optionStudentCommenting = new Option("commentstudent", true, "add student comment to practical material");
        Option optionEvaluate = new Option("evaluate", true, "evaluate practical material");
        optionEvaluate.setArgs(4);
        Option optionDelete = new Option("delete", true, "delete record");
        optionDelete.setArgs(2);

        options.addOption(optionStudent);
        options.addOption(optionStudyGroup);
        options.addOption(optionTeacher);
        options.addOption(optionDiscipline);
        options.addOption(optionAppendDisciplineToTeacher);
        options.addOption(optionAppendDisciplineToGroup);
        options.addOption(optionSchedule);
        options.addOption(optionEvent);
        options.addOption(optionSetScheduleToGroup);
        options.addOption(optionLection);
        options.addOption(optionPractical);
        options.addOption(optionAttachFileStudent);
        options.addOption(optionAttachFileTeacher);
        options.addOption(optionTeacherCommenting);
        options.addOption(optionStudentCommenting);
        options.addOption(optionEvaluate);
        options.addOption(optionDelete);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);


        if (cmd.hasOption("student")) {
            createDataProvider(args[1]);
            StudyGroup studyGroup = dataProvider.getStudyGroupRecordById(Long.parseLong(args[2]));
            Student student = new Student(Long.parseLong(args[3]), args[4] + " " + args[5] + " " + args[6], args[7], studyGroup);
            dataProvider.saveStudentRecord(student);
        }
        if (cmd.hasOption("studygroup")) {
            createDataProvider(args[1]);
            String specialization = "";
            for (int i = 5; i < args.length; i++) {
                specialization += args[i] + " ";
            }
            StudyGroup studyGroup = new StudyGroup(Long.parseLong(args[2]), args[3], specialization, Integer.parseInt(args[4]));
            dataProvider.saveStudyGroupRecord(studyGroup);
        }
        if (cmd.hasOption("teacher")) {
            createDataProvider(args[1]);
            Teacher teacher = new Teacher(Long.parseLong(args[2]), args[3] + " " + args[4] + " " + args[5]);
            dataProvider.saveTeacherRecord(teacher);
        }
        if (cmd.hasOption("discipline")) {
            createDataProvider(args[1]);
            String name = "";
            for (int i = 3; i < args.length; i++) {
                name += args[i] + " ";
            }
            Discipline discipline = new Discipline(Long.parseLong(args[2]), name);
            dataProvider.saveDisciplineRecord(discipline);
        }
        if (cmd.hasOption("appenddisciplinetecher")) {
            createDataProvider(args[1]);
            Discipline discipline = dataProvider.getDisciplineRecordById(Long.parseLong(args[2])).get(0);
            Teacher teacher = dataProvider.getTeacherRecordById(Long.parseLong(args[3]));
            teacher.appendDiscipline(discipline);
            System.out.println(discipline);

            if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider"))
                dataProvider.updateDisciplineRecord(discipline);
            else
                dataProvider.saveDisciplineRecord(discipline);
        }
        if (cmd.hasOption("appenddisciplinegroup")) {
            createDataProvider(args[1]);
            Discipline discipline = dataProvider.getDisciplineRecordById(Long.parseLong(args[2])).get(0);
            StudyGroup studyGroup = dataProvider.getStudyGroupRecordById(Long.parseLong(args[3]));
            studyGroup.appendDisciplines(discipline);
            if (dataProvider.getClass().getSimpleName().equals("DataProviderXML")) {
                dataProvider.saveStudyGroupRecord(studyGroup);
            } else {
                dataProvider.saveDisciplineInStudyGroup(studyGroup.getId(), discipline.getId());
            }
        }
        if (cmd.hasOption("schedule")) {
            createDataProvider(args[1]);
            Schedule schedule = new Schedule();
            switch (args[2]) {
                case "classes":
                    schedule = new ClassesSchedule(Long.parseLong(args[3]));
                    break;
                case "events":
                    schedule = new EventsSchedule(Long.parseLong(args[3]));
                    break;
                case "session":
                    schedule = new SessionSchedule(Long.parseLong(args[3]));
                    break;
            }
            dataProvider.saveScheduleRecord(schedule);
        }
        if (cmd.hasOption("event")) {
            createDataProvider(args[1]);
            Schedule schedule = dataProvider.getScheduleRecordByID(Long.parseLong(args[2]));
            String name = "";
            for (int i = 6; i < args.length; i++) {
                name += args[i] + " ";
            }
            schedule.createEvent(Long.parseLong(args[3]), name, args[4], args[5]);

            dataProvider.saveEventRecord(schedule.getSchedule().get(schedule.getSchedule().size() - 1));
        }
        if (cmd.hasOption("groupschedule")) {
            createDataProvider(args[1]);
            StudyGroup studyGroup = dataProvider.getStudyGroupRecordById(Long.parseLong(args[2]));
            Schedule schedule = dataProvider.getScheduleRecordByID(Long.parseLong(args[3]));
            switch (schedule.getTypeSchedule()) {
                case CLASSES:
                    studyGroup.setClassesSchedule(schedule);
                    break;
                case EVENTS:
                    studyGroup.setEventsSchedule(schedule);
                    break;
                case SESSION:
                    studyGroup.setSessionSchedule(schedule);
                    break;
            }
            if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider"))
                dataProvider.updateStudyGroup(studyGroup);
            else
                dataProvider.saveStudyGroupRecord(studyGroup);
        }
        if (cmd.hasOption("lection")) {
            createDataProvider(args[1]);
            Teacher teacher = dataProvider.getTeacherRecordById(Long.parseLong(args[3]));
            Discipline discipline = dataProvider.getDisciplineRecordById(Long.parseLong(args[4])).get(0);
            String name = "";
            for (int i = 5; i < args.length; i++) {
                name += args[i] + " ";
            }
            LectionMaterial lectionMaterial = teacher.createLectionMaterial(Long.parseLong(args[2]), name, discipline).get();
            dataProvider.saveLectionMaterialRecord(lectionMaterial);
        }
        if (cmd.hasOption("practical")) {
            createDataProvider(args[1]);
            Teacher teacher = dataProvider.getTeacherRecordById(Long.parseLong(args[3]));
            Discipline discipline = dataProvider.getDisciplineRecordById(Long.parseLong(args[4])).get(0);
            String name = "";
            for (int i = 8; i < args.length; i++) {
                name += args[i] + " ";
            }
            PracticalMaterial practicalMaterial = teacher.createPracticalMaterial(Long.parseLong(args[2]), name, discipline, Integer.parseInt(args[5]), args[6] + " " + args[7]).get();
            dataProvider.savePracticalMaterial(practicalMaterial);
        }
        if (cmd.hasOption("filestudent")) {
            createDataProvider(args[1]);
            Student student = dataProvider.getStudentRecord(Long.parseLong(args[2]));
            PracticalMaterial practicalMaterial = dataProvider.getPracticalMaterialRecordById(Long.parseLong(args[3]));
            student.attachFileToPracticalMaterial(practicalMaterial, args[4]);
            if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider"))
                dataProvider.updatePracticalMaterialRecord(practicalMaterial);
            else
                dataProvider.savePracticalMaterial(practicalMaterial);
        }
        if (cmd.hasOption("fileteacher")) {
            createDataProvider(args[1]);
            Teacher teacher = dataProvider.getTeacherRecordById(Long.parseLong(args[3]));
            EducationMaterials educationMaterials;
            switch (args[2]) {
                case "lection":
                    educationMaterials = dataProvider.getLectionMaterilById(Long.parseLong(args[4]));
                    teacher.attachFileToMaterial(educationMaterials, args[5]);
                    if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider"))
                        dataProvider.updateLectionMaterialRecord((LectionMaterial) educationMaterials);
                    else
                        dataProvider.saveLectionMaterialRecord((LectionMaterial) educationMaterials);
                    break;
                case "practical":
                    educationMaterials = dataProvider.getPracticalMaterialRecordById(Long.parseLong(args[4]));
                    teacher.attachFileToMaterial(educationMaterials, args[5]);
                    if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider"))
                        dataProvider.updatePracticalMaterialRecord((PracticalMaterial) educationMaterials);
                    else
                        dataProvider.savePracticalMaterial((PracticalMaterial) educationMaterials);
                    break;
            }
        }
        if (cmd.hasOption("commentteacher")) {
            createDataProvider(args[1]);
            Teacher teacher = dataProvider.getTeacherRecordById(Long.parseLong(args[3]));
            EducationMaterials educationMaterials;
            String comment = "";
            for (int i = 5; i < args.length; i++) {
                comment += args[i] + " ";
            }
            switch (args[2]) {
                case "lection":
                    educationMaterials = dataProvider.getLectionMaterilById(Long.parseLong(args[4]));
                    teacher.commentingMaterial(educationMaterials, comment);
                    if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider"))
                        dataProvider.updateLectionMaterialRecord((LectionMaterial) educationMaterials);
                    else
                        dataProvider.saveLectionMaterialRecord((LectionMaterial) educationMaterials);
                    break;
                case "practical":
                    educationMaterials = dataProvider.getPracticalMaterialRecordById(Long.parseLong(args[4]));
                    teacher.commentingMaterial(educationMaterials, comment);
                    if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider"))
                        dataProvider.updatePracticalMaterialRecord((PracticalMaterial) educationMaterials);
                    else
                        dataProvider.savePracticalMaterial((PracticalMaterial) educationMaterials);
                    break;
            }
        }
        if (cmd.hasOption("commentstudent")) {
            createDataProvider(args[1]);
            Student student = dataProvider.getStudentRecord(Long.parseLong(args[2]));
            PracticalMaterial practicalMaterial = dataProvider.getPracticalMaterialRecordById(Long.parseLong(args[3]));
            String comment = "";
            for (int i = 4; i < args.length; i++) {
                comment += args[i] + " ";
            }
            student.commentingPracticalMaterial(practicalMaterial, comment);
            if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider"))
                dataProvider.updatePracticalMaterialRecord(practicalMaterial);
            else
                dataProvider.savePracticalMaterial(practicalMaterial);
        }
        if (cmd.hasOption("evaluate")) {
            createDataProvider(args[1]);
            Teacher teacher = dataProvider.getTeacherRecordById(Long.parseLong(args[2]));
            PracticalMaterial practicalMaterial = dataProvider.getPracticalMaterialRecordById(Long.parseLong(args[3]));
            Student student = dataProvider.getStudentRecord(Long.parseLong(args[4]));
            teacher.evaluateCompletedPracticalMaterial(practicalMaterial, student, Integer.parseInt(args[5]));
            if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider"))
                dataProvider.updatePracticalMaterialRecord(practicalMaterial);
            else
                dataProvider.savePracticalMaterial(practicalMaterial);
        }
        if (cmd.hasOption("delete")) {
            createDataProvider(args[1]);
            if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider"))
                dataProvider.deleteRecord(args[2], Long.parseLong(args[3]));
            else if (dataProvider.getClass().getSimpleName().equals("DataProviderCSV")) {
                switch (args[2]) {
                    case "student":
                        dataProvider.deleteRecord(config.getConfigurationEntry(CSV_STUDENT), Long.parseLong(args[3]), Student.class, STUDENT_HEADERS);
                        break;
                    case "studygroup":
                        dataProvider.deleteRecord(config.getConfigurationEntry(CSV_STUDY_GROUP), Long.parseLong(args[3]), StudyGroup.class, STUDY_GROUP_HEADERS);
                        break;
                    case "teacher":
                        dataProvider.deleteRecord(config.getConfigurationEntry(CSV_TEACHER), Long.parseLong(args[3]), Teacher.class, TEACHER_HEADERS);
                        break;
                    case "schedule":
                        dataProvider.deleteRecord(config.getConfigurationEntry(CSV_SCHEDULE), Long.parseLong(args[3]), Schedule.class, SCHEDULE_HEADERS);
                        break;
                    case "lection":
                        dataProvider.deleteRecord(config.getConfigurationEntry(CSV_LECTION_MATERIAL), Long.parseLong(args[3]), LectionMaterial.class, LECTION_HEADERS);
                        break;
                    case "practical":
                        dataProvider.deleteRecord(config.getConfigurationEntry(CSV_PRACTICAL_MATERIAL), Long.parseLong(args[3]), PracticalMaterial.class, PRACTICAL_HEADERS);
                        break;
                    case "event":
                        dataProvider.deleteRecord(config.getConfigurationEntry(CSV_EVENT), Long.parseLong(args[3]), Event.class, EVENT_HEADERS);
                        break;
                }
            } else {
                switch (args[2]) {
                    case "student":
                        dataProvider.deleteRecord(config.getConfigurationEntry(XML_STUDENT), Long.parseLong(args[3]), Student.class);
                        break;
                    case "studygroup":
                        dataProvider.deleteRecord(config.getConfigurationEntry(XML_STUDY_GROUP), Long.parseLong(args[3]), StudyGroup.class);
                        break;
                    case "teacher":
                        dataProvider.deleteRecord(config.getConfigurationEntry(XML_TEACHER), Long.parseLong(args[3]), Teacher.class);
                        break;
                    case "schedule":
                        dataProvider.deleteRecord(config.getConfigurationEntry(XML_SCHEDULE), Long.parseLong(args[3]), Schedule.class);
                        break;
                    case "lection":
                        dataProvider.deleteRecord(config.getConfigurationEntry(XML_LECTION), Long.parseLong(args[3]), LectionMaterial.class);
                        break;
                    case "practical":
                        dataProvider.deleteRecord(config.getConfigurationEntry(XML_PRACTICAL), Long.parseLong(args[3]), PracticalMaterial.class);
                        break;
                    case "event":
                        dataProvider.deleteRecord(config.getConfigurationEntry(XML_EVENTS), Long.parseLong(args[3]), Event.class);
                        break;
                }
            }

            if (dataProvider.getClass().getSimpleName().equals("DataBaseProvider")) {
                ((DataBaseProvider) dataProvider).closeConnection();
            }
            ;
        }
    }

    private static void createDataProvider(String dataProviderName) throws Exception {
        switch(dataProviderName){
            case "DB": dataProvider = new DataBaseProvider();break;
            case "CSV": dataProvider = new DataProviderCSV();break;
            case "XML": dataProvider = new DataProviderXML();break;
        }
    }
}
