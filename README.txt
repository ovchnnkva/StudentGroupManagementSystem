 
 java -jar -Dlog4j2.configurationFile=log4j2.xml StudentGroupManagementSystem.jar
 //передать конфигурацию логгера
 
 --------------------------------------------------------------------------------------------------------------------------------------------------------------  
 Команды для запуска.
 //каждая команда принимает параметр [typeProvider] для выбора провайдера. Допустимые значения: DB; CSV; XML
 
 java -jar java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -delete [nameTable] [id]
 //удаление записи для провайдера к базе данных
 nameTable принимает значения:
 teachers
 disciplines
 schedules
 events
 studyGroups
 disciplinesInStudyGroups
 practicalMaterials
 lectionMaterials
 students
 studentsScores
 
  java -jar java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -delete [typeProvider] [nameClass] [id]
   //удаление записи для файловых провайдеров
   nameClass принимает значения:
   student
   studygroup
   teacher
   schedule
   lection
   practical
   event
 
 -------------------------------------------------------------------------------------------------------------------------------------------------------------- 
  
 java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -student [typeProvider] [idStudyGroup] [idStudent] [firstName] [lastName] [patronymic] [birthday] 
 //создание студента
 студента можно сохранить только при существовании студенческой группы, т.к. она передаётся в конструктор. Дата рождения формата dd-mm-yyyy
 
 Пример:
 java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -student DB 1 5 Овчинникова Анна Владимировна 23-07-2002 
--------------------------------------------------------------------------------------------------------------------------------------------------------------  

  java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -studygroup [typeProvider][idStudyGroup] [groupCode] [course] [specialization] 
  //создание студенческой группы
  
  Пример:
  java -jar -Dpath=config.propertiesStudentGroupManagementSystem.jar -studygroup CSV 4 09.04.04 3 Системный анализ
  
------------------------------------------------------------------------------------------------------------------------------------------------------------  

  java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -teacher [typeProvider] [idTeacher] [name] [firstName] [lastName] [patronymic]
  //создание преподавателя
  
  ПРимер:
  java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -teacher XML 4 Петров Пётр Петрович
  
 -------------------------------------------------------------------------------------------------------------------------------------------------------------- 
   
  java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -discipline [typeProvider] [idDiscipline] [name]
  //создание дисциплины
  
  Пример:
  java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -discipline DB 5 Веб-технологии
  
 --------------------------------------------------------------------------------------------------------------------------------------------------------------  
  
  java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -appenddisciplinetecher [typeProvider] [idDiscipline] [idTeacher]
  //добавление дисциплины преподавателю
  перед добавлением дисциплина должна быть уже создана, как и преподаватель, которому дисциплина добавляется
  Пример:
  java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -appenddisciplinetecher CSV 3 1
  
--------------------------------------------------------------------------------------------------------------------------------------------------------------    
  java -jar -Dpath=config.properties userStudentGroupManagementSystem.jar -appenddisciplinegroup [typeProvider] [idDiscipline] [idGroup]
  //добавление дисциплины группе студентов
  перед добавлением дисциплина должна быть уже создана, как и группа студентов, которой дисциплина добавляется
  
  Пример:
  java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -appenddisciplinegroup XML 2 1
  
--------------------------------------------------------------------------------------------------------------------------------------------------------------    

   java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -schedule [typeProvider] [id] [type]
   //создание рассписания(classes, events или session)
   
   Пример:
   java -jar -Dpath=config.properties userStudentGroupManagementSystem.jar -schedule DB classes 4
   
--------------------------------------------------------------------------------------------------------------------------------------------------------------  
   
   java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -event [typeProvider] [idSchedule] [idEvent] [date] [time] [name]
   //создание мероприятия в расписании
   перед созданием, расписание должно существовать
   дата для classesschedule представляет из себя день недели, для все остальны - обычная дата
   
   ПРимер:
   java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -event CSV 1 6 Понедельник 11:55 Веб-технологии
   
--------------------------------------------------------------------------------------------------------------------------------------------------------------
     
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -groupschedule [typeProvider] [idGroup] [idSchedule] 
//добавление расписания группе студентов
расписание и группа студентов должны существовать перед вызовом 

Пример:
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -groupschedule XML 1 1
--------------------------------------------------------------------------------------------------------------------------------------------------------------  

java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -lection [typeProvider] [idLectionMaterial] [idTeacher] [idDiscipline] [name]
//создание лекционного материала
преподаватель должен существовать перед вызовом. У преподавателя должна быть соответствующая дисциплина

Пример:
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -lection DB 2 2 2 Философия Канта
-------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -practical [typeProvider] [idLectionMaterial] [idTeacher] [idDiscipline] [maxScore] [deadline] [name]
//созданеи практического материала
преподаватель должен существовать перед вызовом. У преподавателя должна быть соответствующая дисциплина
Дедлайн должен передаваться в формате dd-mm-yyyy hh:mm
Пример:
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -practical CSV 3 2 2 45 23-12-2022 23:59 Курсовая

-------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 
java -jar -Dpath=/home/user/config.properties /home/user/StudentGroupManagementSystem.jar -filestudent [typeProvider] [idStudent] [idMaterial] [filePath]
//прикрепление файла студента к практическому материалу
У студента должна быть та же дисциплина, что и у задания

Пример:
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -filestudent XML 1 1 /uml

--------------------------------------------------------------------------------------------------------------------------------------------------------------  

java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -fileteacher [typeProvider] [typeMaterial] [idTeacher] [idMaterial] [filePath]
//прикрепление файла преподавателя к материалу
У преподавателя должна быть та же дисциплина, что и у задания
typeMaterial должен принимать одно из двух значений: lection или  practical

Пример:
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -fileteacher DB practical 1 1 /specificationUml

-------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -commentstudent [typeProvider] [idStudent] [idMaterial] [comment]
//добавление комментария к материала
У студента должна быть та же дисциплина, что и у задания

Пример:
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -commentstudent CSV 1 1 i'm stupid

--------------------------------------------------------------------------------------------------------------------------------------------------------------  

java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -commentteacher [typeProvider] [typeMaterial] [idTeacher] [idMaterial] [comment]
//добавление комментария к материала
У преподавателя должна быть та же дисциплина, что и у задания

Пример:
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -commentteacher XML practical 1 1 it's terribly...

--------------------------------------------------------------------------------------------------------------------------------------------------------------  

java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -evaluate [typeProvider] [idTeacher] [idMaterial] [idStudent] [score]
//проверка выполненного практического материала и выставление балла студенту
у преподавателя и студента должна быть та же дисциплина, что и у практического материала
балл студента не должен превышать максимальный балл задания
общий балл студента не должен превысить 100 баллов

Пример:
java -jar -Dpath=config.properties StudentGroupManagementSystem.jar -evaluate DB 1 1 1 2

--------------------------------------------------------------------------------------------------------------------------------------------------------------  





