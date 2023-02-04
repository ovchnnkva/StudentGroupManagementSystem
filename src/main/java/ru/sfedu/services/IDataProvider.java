package ru.sfedu.services;

import ru.sfedu.model.*;

import java.util.List;
import java.util.Map;

public interface IDataProvider {

    <T extends EntityBean> void deleteRecord(String file, long id, Class<T> tClass, String[] headers) throws Exception;

     <T extends EntityBean> void deleteRecord(String file, long id, Class<T> tClass) throws Exception;
     void deleteRecord(String file, long id) throws Exception;

    void saveStudyGroupRecord(StudyGroup object) throws Exception;

    void saveDisciplineInStudyGroup(long groupId, long disciplineId) throws Exception;

    StudyGroup getStudyGroupRecordById(long id) throws Exception;

    void updateStudyGroup(StudyGroup studyGroup) throws Exception;

    List<Discipline> getDisciplinesInStudyGroup(long idGroups) throws Exception;

    void saveTeacherRecord(Teacher object) throws Exception;

    Teacher getTeacherRecordById(long id) throws Exception;

    void updateTeacherRecord(Teacher teacher) throws Exception;

    void saveDisciplineRecord(Discipline object) throws Exception;

    List<Discipline> getDisciplineRecordById(long id) throws Exception;

    List<Discipline> getDisciplineRecordByTeacherID(long id) throws Exception;

    List<Discipline> getDisciplineRecord(String sql) throws Exception;

    void updateDisciplineRecord(Discipline object) throws Exception;

    void saveScheduleRecord(Schedule object) throws Exception;

    Schedule getScheduleRecordByID(long id) throws Exception;

    void saveEventRecord(Event object) throws Exception;

    void updateEventRecord(Event object) throws Exception;

    List<Event> getEventsRecordByScheduleId(long id) throws Exception;

    void savePracticalMaterial(PracticalMaterial object) throws Exception;

    PracticalMaterial getPracticalMaterialRecordById(long id) throws Exception;

    void updatePracticalMaterialRecord(PracticalMaterial object)throws Exception;

    void saveLectionMaterialRecord(LectionMaterial object) throws Exception;

    LectionMaterial getLectionMaterilById(long id) throws Exception;

    void updateLectionMaterialRecord(LectionMaterial object)throws Exception;

    void saveStudentRecord(Student object) throws Exception;

    void saveScoresStudent(long studentId, long disciplineId, int score) throws Exception;

    Student getStudentRecord(long id) throws Exception;

    Map<Discipline,Integer> getScoresByStudentId(long id) throws Exception;

    void updateStudentRecordById(Student object) throws Exception;
}
