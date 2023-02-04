-- MySQL dump 10.13  Distrib 8.0.31, for Linux (x86_64)
--
-- Host: localhost    Database: studentDB
-- ------------------------------------------------------
-- Server version	8.0.31-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `disciplines`
--

DROP TABLE IF EXISTS `disciplines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `disciplines` (
  `id` int NOT NULL,
  `teacherID` int NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disciplines`
--

LOCK TABLES `disciplines` WRITE;
/*!40000 ALTER TABLE `disciplines` DISABLE KEYS */;
INSERT INTO `disciplines` VALUES (1,1,'Архитектура Информационных Систем'),(2,2,'Философия'),(3,3,'Методы системного анализа');
/*!40000 ALTER TABLE `disciplines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disciplinesInStudyGroups`
--

DROP TABLE IF EXISTS `disciplinesInStudyGroups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `disciplinesInStudyGroups` (
  `id` int NOT NULL AUTO_INCREMENT,
  `groupID` int NOT NULL,
  `disciplineID` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disciplinesInStudyGroups`
--

LOCK TABLES `disciplinesInStudyGroups` WRITE;
/*!40000 ALTER TABLE `disciplinesInStudyGroups` DISABLE KEYS */;
INSERT INTO `disciplinesInStudyGroups` VALUES (1,1,1),(2,2,2),(7,1,3);
/*!40000 ALTER TABLE `disciplinesInStudyGroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `events` (
  `id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `time` varchar(45) NOT NULL,
  `date` varchar(45) NOT NULL,
  `scheduleID` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,'Архитектура Информационных систем','08:00','Понедельник','1'),(2,'Архитектура Информационных систем','10:00','22.01.2022','2'),(3,'Собрание','9:00','09.01.2022','3'),(4,'Основы системного анализа','9:50','Понедельник','1');
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lectionMaterials`
--

DROP TABLE IF EXISTS `lectionMaterials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lectionMaterials` (
  `id` int NOT NULL,
  `teachersFIle` varchar(255) DEFAULT NULL,
  `teacherComment` varchar(255) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `disciplineID` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lectionMaterials`
--

LOCK TABLES `lectionMaterials` WRITE;
/*!40000 ALTER TABLE `lectionMaterials` DISABLE KEYS */;
INSERT INTO `lectionMaterials` VALUES (1,'','read carefully!!!','Методичка',1),(2,'','','Философия Канта ',2);
/*!40000 ALTER TABLE `lectionMaterials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `practicalMaterials`
--

DROP TABLE IF EXISTS `practicalMaterials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `practicalMaterials` (
  `id` int NOT NULL,
  `teachersFile` varchar(255) DEFAULT NULL,
  `teacherComment` varchar(255) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `disciplineID` int NOT NULL,
  `studentFile` varchar(255) DEFAULT NULL,
  `maximumScore` int NOT NULL,
  `studentScore` int DEFAULT NULL,
  `studentComment` varchar(255) DEFAULT NULL,
  `deadline` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `practicalMaterials`
--

LOCK TABLES `practicalMaterials` WRITE;
/*!40000 ALTER TABLE `practicalMaterials` DISABLE KEYS */;
INSERT INTO `practicalMaterials` VALUES (1,'','','UML UseCase Diagram',1,'',5,0,'Im stupid','2022-10-10 20:59:00'),(2,'','','Тест',2,'',10,0,'its easy ','2022-12-17 15:04:37');
/*!40000 ALTER TABLE `practicalMaterials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedules`
--

DROP TABLE IF EXISTS `schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedules` (
  `id` int NOT NULL,
  `type` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedules`
--

LOCK TABLES `schedules` WRITE;
/*!40000 ALTER TABLE `schedules` DISABLE KEYS */;
INSERT INTO `schedules` VALUES (1,'CLASSES'),(2,'SESSION'),(3,'EVENTS');
/*!40000 ALTER TABLE `schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `birthday` date NOT NULL,
  `studyGroupID` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1,'Иванов Андрей Иванович','2002-11-20',1),(2,'Орлова Екатерина АНдреевна','2002-01-12',2);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studentsScores`
--

DROP TABLE IF EXISTS `studentsScores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `studentsScores` (
  `id` int NOT NULL AUTO_INCREMENT,
  `studentID` int NOT NULL,
  `disciplineID` int NOT NULL,
  `score` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studentsScores`
--

LOCK TABLES `studentsScores` WRITE;
/*!40000 ALTER TABLE `studentsScores` DISABLE KEYS */;
INSERT INTO `studentsScores` VALUES (4,1,1,5);
/*!40000 ALTER TABLE `studentsScores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studyGroups`
--

DROP TABLE IF EXISTS `studyGroups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `studyGroups` (
  `id` int NOT NULL,
  `specialization` varchar(100) NOT NULL,
  `course` int NOT NULL,
  `groupsCode` varchar(45) NOT NULL,
  `classesScheduleID` int DEFAULT NULL,
  `sessionScheduleID` int DEFAULT NULL,
  `eventsScheduleID` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studyGroups`
--

LOCK TABLES `studyGroups` WRITE;
/*!40000 ALTER TABLE `studyGroups` DISABLE KEYS */;
INSERT INTO `studyGroups` VALUES (1,'Прикладная информатика',3,'09.03.03',0,2,0),(2,'Философия',2,'23.01.01',0,0,0);
/*!40000 ALTER TABLE `studyGroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachers` (
  `id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachers`
--

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
INSERT INTO `teachers` VALUES (1,'Жмайлов Борис Борисович'),(2,'Понтийский Платон Леонидович'),(3,'Троянов Алексей Дмитриевич');
/*!40000 ALTER TABLE `teachers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-23  8:57:09
