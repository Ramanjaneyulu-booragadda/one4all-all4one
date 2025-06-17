-- MySQL dump 10.13  Distrib 8.0.40, for macos14 (x86_64)
--
-- Host: 127.0.0.1    Database: one4all
-- ------------------------------------------------------
-- Server version	9.1.0-commercial

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `help_submission`
--

DROP TABLE IF EXISTS `help_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `help_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_member_id` varchar(50) NOT NULL,
  `receiver_member_id` varchar(50) NOT NULL,
  `receiver_mobile` varchar(20) DEFAULT NULL,
  `upliner_level` int DEFAULT NULL,
  `submitted_amount` decimal(10,2) NOT NULL,
  `proof` varchar(255) DEFAULT NULL,
  `comments` text,
  `submission_status` enum('PAID','RECEIVED','UNPAID','PENDING','CANCELLED','NOT_RECEIVED','SUBMITTED') NOT NULL,
  `transaction_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `submission_reference_id` varchar(100) DEFAULT NULL,
  `verified_by` varchar(50) DEFAULT NULL,
  `verification_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_help_submission_sender_receiver_level` (`sender_member_id`,`receiver_member_id`,`upliner_level`),
  KEY `receiver_member_id` (`receiver_member_id`),
  CONSTRAINT `help_submission_ibfk_1` FOREIGN KEY (`sender_member_id`) REFERENCES `ofa_user_reg_details` (`ofa_member_id`),
  CONSTRAINT `help_submission_ibfk_2` FOREIGN KEY (`receiver_member_id`) REFERENCES `ofa_user_reg_details` (`ofa_member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `help_submission`
--

LOCK TABLES `help_submission` WRITE;
/*!40000 ALTER TABLE `help_submission` DISABLE KEYS */;
INSERT INTO `help_submission` VALUES (6,'O4AA4O3998722','SPLNO4AA4O0000010','9703042365',1,2000.00,'/uploads/db60fe60-908d-4b4b-9ec1-cacd92e5609d.pdf','Received and verified on UPI','RECEIVED','2025-04-20 17:25:41','5e66fe79-d613-4b43-8321-b549a665bd22','SPLNO4AA4O0000010','2025-04-21 03:02:24'),(7,'O4AA4O5518741','O4AA4O3998722','8250251231',1,2000.00,'/uploads/bad31525-d391-4780-a20a-cb36e973d2c7.pdf','amount verified','RECEIVED','2025-05-04 07:42:09','24b09feb-a817-42b9-be4b-c1b6920bf6d4','O4AA4O3998722','2025-05-04 07:44:52'),(8,'O4AA4O6532727','O4AA4O3998722','8250251231',1,2000.00,NULL,'amount received','RECEIVED','2025-05-18 02:51:24','7ff62a4f-3598-49a2-b079-e5cd34791515','O4AA4O3998722','2025-05-18 03:31:46'),(10,'O4AA4O6532727','SPLNO4AA4O0000010','9703042364',2,1000.00,NULL,'received amont','RECEIVED','2025-05-18 02:52:48','b5e6aa77-7b62-4e95-9ddf-e2419e663d75','SPLNO4AA4O0000010','2025-05-24 12:18:44'),(11,'O4AA4O3998722','SPLNO4AA4O0000009','9703042364',2,1000.00,NULL,'amount received','RECEIVED','2025-05-18 03:34:59','1427aabe-e187-42fd-8e43-21e01464670b','SPLNO4AA4O0000009','2025-05-24 12:15:46'),(12,'O4AA4O9676328','O4AA4O6759435','7561472032',1,2000.00,'khanna1234',NULL,'SUBMITTED','2025-05-24 15:20:48','7099dbe4-f16b-4776-9055-c7cdc82fe54e',NULL,NULL),(13,'O4AA4O9676328','O4AA4O5518741','9381276617',2,1000.00,'prudhvi123546','amount received 654655','RECEIVED','2025-05-24 15:27:10','b606ef97-f25d-4ac6-bcb1-eb286cfb243c','O4AA4O5518741','2025-05-24 15:52:53'),(14,'O4AA4O5518741','SPLNO4AA4O0000010','9703042365',2,1000.00,'Hey john','hy prudhvi received amout','RECEIVED','2025-05-24 17:31:58','be79bab0-0004-40a8-8ad4-17c2a1425cb7','SPLNO4AA4O0000010','2025-05-24 17:33:24'),(15,'O4AA4O5518741','SPLNO4AA4O0000009','9703042364',3,1000.00,'hey jhon 9','hey prudhvi receive amount ','RECEIVED','2025-05-24 17:59:04','968874f9-b42b-4f2f-a645-0d8780437e75','SPLNO4AA4O0000009','2025-05-24 18:00:12'),(16,'O4AA4O3998722','SPLNO4AA4O0000008','9703042363',3,1000.00,'	John Doe8 123124','amount received','RECEIVED','2025-05-25 06:34:58','8f2fab51-9037-484a-8665-521487e71609','SPLNO4AA4O0000008','2025-05-25 06:36:42'),(17,'O4AA4O3998722','SPLNO4AA4O0000007','9703042362',4,2500.00,'	John Doe7 1231456',NULL,'SUBMITTED','2025-05-25 06:35:32','fe3a6bd1-9b64-4d50-857b-e6e7fc092b16',NULL,NULL);
/*!40000 ALTER TABLE `help_submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `help_submission_audit_log`
--

DROP TABLE IF EXISTS `help_submission_audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `help_submission_audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(50) NOT NULL,
  `payment_id` bigint NOT NULL,
  `performed_by` varchar(50) NOT NULL,
  `remarks` text NOT NULL,
  `timestamp` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_payment_id` (`payment_id`),
  CONSTRAINT `fk_help_submission_audit` FOREIGN KEY (`payment_id`) REFERENCES `help_submission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `help_submission_audit_log`
--

LOCK TABLES `help_submission_audit_log` WRITE;
/*!40000 ALTER TABLE `help_submission_audit_log` DISABLE KEYS */;
INSERT INTO `help_submission_audit_log` VALUES (1,'SUBMITTED',6,'O4AA4O3998722','Help initiated by user O4AA4O3998722','2025-04-20 17:25:41'),(2,'RECEIVED',6,'SPLNO4AA4O0000010','amount received','2025-04-21 02:54:48'),(3,'RECEIVED',6,'SPLNO4AA4O0000010','Received and verified on UPI','2025-04-21 03:02:24'),(4,'SUBMITTED',7,'O4AA4O5518741','Help initiated by user O4AA4O5518741','2025-05-04 07:42:09'),(5,'RECEIVED',7,'O4AA4O3998722','amount verified','2025-05-04 07:44:52'),(6,'SUBMITTED',8,'O4AA4O6532727','Help initiated by user O4AA4O6532727','2025-05-18 02:51:24'),(8,'SUBMITTED',10,'O4AA4O6532727','Help initiated by user O4AA4O6532727','2025-05-18 02:52:48'),(9,'RECEIVED',8,'O4AA4O3998722','amount received','2025-05-18 03:31:46'),(10,'SUBMITTED',11,'O4AA4O3998722','Help initiated by user O4AA4O3998722','2025-05-18 03:34:59'),(11,'RECEIVED',11,'SPLNO4AA4O0000009','amount received','2025-05-24 12:15:46'),(12,'RECEIVED',10,'SPLNO4AA4O0000010','received amont','2025-05-24 12:18:44'),(13,'SUBMITTED',12,'O4AA4O9676328','Help initiated by user O4AA4O9676328','2025-05-24 15:20:48'),(14,'SUBMITTED',13,'O4AA4O9676328','Help initiated by user O4AA4O9676328','2025-05-24 15:27:10'),(15,'RECEIVED',13,'O4AA4O5518741','amount received 654655','2025-05-24 15:52:53'),(16,'SUBMITTED',14,'O4AA4O5518741','Help initiated by user O4AA4O5518741','2025-05-24 17:31:58'),(17,'RECEIVED',14,'SPLNO4AA4O0000010','hy prudhvi received amout','2025-05-24 17:33:24'),(18,'SUBMITTED',15,'O4AA4O5518741','Help initiated by user O4AA4O5518741','2025-05-24 17:59:04'),(19,'RECEIVED',15,'SPLNO4AA4O0000009','hey prudhvi receive amount ','2025-05-24 18:00:12'),(20,'SUBMITTED',16,'O4AA4O3998722','Help initiated by user O4AA4O3998722','2025-05-25 06:34:58'),(21,'SUBMITTED',17,'O4AA4O3998722','Help initiated by user O4AA4O3998722','2025-05-25 06:35:32'),(22,'RECEIVED',16,'SPLNO4AA4O0000008','amount received','2025-05-25 06:36:42');
/*!40000 ALTER TABLE `help_submission_audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_roles`
--

DROP TABLE IF EXISTS `member_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_roles` (
  `ofa_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`ofa_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `member_roles_ibfk_1` FOREIGN KEY (`ofa_id`) REFERENCES `ofa_user_reg_details` (`ofa_id`) ON DELETE CASCADE,
  CONSTRAINT `member_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_roles`
--

LOCK TABLES `member_roles` WRITE;
/*!40000 ALTER TABLE `member_roles` DISABLE KEYS */;
INSERT INTO `member_roles` VALUES (11,2),(12,2),(13,2),(17,2),(18,2),(19,2),(20,2),(21,2),(22,2),(23,2),(24,2),(25,2),(26,2),(27,2),(34,2),(35,2),(1,3),(2,3),(3,3),(4,3),(5,3),(6,3),(7,3),(8,3),(9,3),(10,3),(11,3),(28,3),(29,3),(30,3),(31,3),(32,3),(33,3);
/*!40000 ALTER TABLE `member_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ofa_referral_bonuses`
--

DROP TABLE IF EXISTS `ofa_referral_bonuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ofa_referral_bonuses` (
  `ofa_referral_id` bigint NOT NULL AUTO_INCREMENT,
  `ofa_member_id` bigint NOT NULL,
  `ofa_referrer_id` bigint NOT NULL,
  `ofa_referral_amount` decimal(38,2) NOT NULL,
  `ofa_created_at` datetime NOT NULL,
  PRIMARY KEY (`ofa_referral_id`),
  KEY `fk_member_referral` (`ofa_member_id`),
  KEY `fk_referrer` (`ofa_referrer_id`),
  CONSTRAINT `fk_member_referral` FOREIGN KEY (`ofa_member_id`) REFERENCES `ofa_user_reg_details` (`ofa_id`),
  CONSTRAINT `fk_referrer` FOREIGN KEY (`ofa_referrer_id`) REFERENCES `ofa_user_reg_details` (`ofa_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ofa_referral_bonuses`
--

LOCK TABLES `ofa_referral_bonuses` WRITE;
/*!40000 ALTER TABLE `ofa_referral_bonuses` DISABLE KEYS */;
/*!40000 ALTER TABLE `ofa_referral_bonuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ofa_referrer_details`
--

DROP TABLE IF EXISTS `ofa_referrer_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ofa_referrer_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` varchar(100) NOT NULL,
  `referrer_id` varchar(100) DEFAULT NULL,
  `referral_level` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_referrer_id` (`referrer_id`),
  CONSTRAINT `fk_member_id` FOREIGN KEY (`member_id`) REFERENCES `ofa_user_reg_details` (`ofa_member_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_referrer_id` FOREIGN KEY (`referrer_id`) REFERENCES `ofa_user_reg_details` (`ofa_member_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ofa_referrer_details`
--

LOCK TABLES `ofa_referrer_details` WRITE;
/*!40000 ALTER TABLE `ofa_referrer_details` DISABLE KEYS */;
INSERT INTO `ofa_referrer_details` VALUES (11,'SPLNO4AA4O0000001',NULL,1),(12,'SPLNO4AA4O0000002','SPLNO4AA4O0000001',2),(13,'SPLNO4AA4O0000003','SPLNO4AA4O0000002',3),(14,'SPLNO4AA4O0000004','SPLNO4AA4O0000003',4),(15,'SPLNO4AA4O0000005','SPLNO4AA4O0000004',5),(16,'SPLNO4AA4O0000006','SPLNO4AA4O0000005',6),(17,'SPLNO4AA4O0000007','SPLNO4AA4O0000006',7),(18,'SPLNO4AA4O0000008','SPLNO4AA4O0000007',8),(19,'SPLNO4AA4O0000009','SPLNO4AA4O0000008',9),(20,'SPLNO4AA4O0000010','SPLNO4AA4O0000009',10),(21,'O4AA4O3998722','SPLNO4AA4O0000010',1),(22,'O4AA4O5518741','O4AA4O3998722',1),(23,'O4AA4O6532727','O4AA4O3998722',1),(24,'O4AA4O1761931','O4AA4O6532727',1),(25,'O4AA4O1034720','O4AA4O5518741',1),(26,'O4AA4O6759435','O4AA4O5518741',1),(27,'O4AA4O4058393','O4AA4O6532727',1),(28,'O4AA4O6643413','O4AA4O1034720',1),(29,'O4AA4O8610373','O4AA4O6643413',1),(30,'O4AA4O1283486','O4AA4O1761931',1),(31,'O4AA4O2551041','O4AA4O1761931',1),(32,'O4AA4O2402067','O4AA4O6759435',1),(33,'O4AA4O9442723','O4AA4O1034720',1),(34,'O4AA4O9676328','O4AA4O6759435',1),(35,'O4AA4O5542095','O4AA4O2402067',1);
/*!40000 ALTER TABLE `ofa_referrer_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ofa_upliner_details`
--

DROP TABLE IF EXISTS `ofa_upliner_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ofa_upliner_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` varchar(100) NOT NULL,
  `upliner_id` varchar(100) DEFAULT NULL,
  `upliner_level` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_upliner_id` (`upliner_id`),
  KEY `idx_upliner_level` (`upliner_level`),
  CONSTRAINT `fk_upliner_id` FOREIGN KEY (`upliner_id`) REFERENCES `ofa_user_reg_details` (`ofa_member_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_upliner_member_id` FOREIGN KEY (`member_id`) REFERENCES `ofa_user_reg_details` (`ofa_member_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=241 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ofa_upliner_details`
--

LOCK TABLES `ofa_upliner_details` WRITE;
/*!40000 ALTER TABLE `ofa_upliner_details` DISABLE KEYS */;
INSERT INTO `ofa_upliner_details` VALUES (46,'SPLNO4AA4O0000002','SPLNO4AA4O0000001',1),(47,'SPLNO4AA4O0000003','SPLNO4AA4O0000001',2),(48,'SPLNO4AA4O0000003','SPLNO4AA4O0000002',1),(49,'SPLNO4AA4O0000004','SPLNO4AA4O0000001',3),(50,'SPLNO4AA4O0000004','SPLNO4AA4O0000002',2),(51,'SPLNO4AA4O0000004','SPLNO4AA4O0000003',1),(52,'SPLNO4AA4O0000005','SPLNO4AA4O0000001',4),(53,'SPLNO4AA4O0000005','SPLNO4AA4O0000002',3),(54,'SPLNO4AA4O0000005','SPLNO4AA4O0000003',2),(55,'SPLNO4AA4O0000005','SPLNO4AA4O0000004',1),(56,'SPLNO4AA4O0000006','SPLNO4AA4O0000001',5),(57,'SPLNO4AA4O0000006','SPLNO4AA4O0000002',4),(58,'SPLNO4AA4O0000006','SPLNO4AA4O0000003',3),(59,'SPLNO4AA4O0000006','SPLNO4AA4O0000004',2),(60,'SPLNO4AA4O0000006','SPLNO4AA4O0000005',1),(61,'SPLNO4AA4O0000007','SPLNO4AA4O0000001',6),(62,'SPLNO4AA4O0000007','SPLNO4AA4O0000002',5),(63,'SPLNO4AA4O0000007','SPLNO4AA4O0000003',4),(64,'SPLNO4AA4O0000007','SPLNO4AA4O0000004',3),(65,'SPLNO4AA4O0000007','SPLNO4AA4O0000005',2),(66,'SPLNO4AA4O0000007','SPLNO4AA4O0000006',1),(67,'SPLNO4AA4O0000008','SPLNO4AA4O0000001',7),(68,'SPLNO4AA4O0000008','SPLNO4AA4O0000002',6),(69,'SPLNO4AA4O0000008','SPLNO4AA4O0000003',5),(70,'SPLNO4AA4O0000008','SPLNO4AA4O0000004',4),(71,'SPLNO4AA4O0000008','SPLNO4AA4O0000005',3),(72,'SPLNO4AA4O0000008','SPLNO4AA4O0000006',2),(73,'SPLNO4AA4O0000008','SPLNO4AA4O0000007',1),(74,'SPLNO4AA4O0000009','SPLNO4AA4O0000001',8),(75,'SPLNO4AA4O0000009','SPLNO4AA4O0000002',7),(76,'SPLNO4AA4O0000009','SPLNO4AA4O0000003',6),(77,'SPLNO4AA4O0000009','SPLNO4AA4O0000004',5),(78,'SPLNO4AA4O0000009','SPLNO4AA4O0000005',4),(79,'SPLNO4AA4O0000009','SPLNO4AA4O0000006',3),(80,'SPLNO4AA4O0000009','SPLNO4AA4O0000007',2),(81,'SPLNO4AA4O0000009','SPLNO4AA4O0000008',1),(82,'SPLNO4AA4O0000010','SPLNO4AA4O0000001',9),(83,'SPLNO4AA4O0000010','SPLNO4AA4O0000002',8),(84,'SPLNO4AA4O0000010','SPLNO4AA4O0000003',7),(85,'SPLNO4AA4O0000010','SPLNO4AA4O0000004',6),(86,'SPLNO4AA4O0000010','SPLNO4AA4O0000005',5),(87,'SPLNO4AA4O0000010','SPLNO4AA4O0000006',4),(88,'SPLNO4AA4O0000010','SPLNO4AA4O0000007',3),(89,'SPLNO4AA4O0000010','SPLNO4AA4O0000008',2),(90,'SPLNO4AA4O0000010','SPLNO4AA4O0000009',1),(91,'O4AA4O3998722','SPLNO4AA4O0000010',1),(92,'O4AA4O3998722','SPLNO4AA4O0000009',2),(93,'O4AA4O3998722','SPLNO4AA4O0000008',3),(94,'O4AA4O3998722','SPLNO4AA4O0000007',4),(95,'O4AA4O3998722','SPLNO4AA4O0000006',5),(96,'O4AA4O3998722','SPLNO4AA4O0000005',6),(97,'O4AA4O3998722','SPLNO4AA4O0000004',7),(98,'O4AA4O3998722','SPLNO4AA4O0000003',8),(99,'O4AA4O3998722','SPLNO4AA4O0000002',9),(100,'O4AA4O3998722','SPLNO4AA4O0000001',10),(101,'O4AA4O5518741','O4AA4O3998722',1),(102,'O4AA4O5518741','SPLNO4AA4O0000010',2),(103,'O4AA4O5518741','SPLNO4AA4O0000009',3),(104,'O4AA4O5518741','SPLNO4AA4O0000008',4),(105,'O4AA4O5518741','SPLNO4AA4O0000007',5),(106,'O4AA4O5518741','SPLNO4AA4O0000006',6),(107,'O4AA4O5518741','SPLNO4AA4O0000005',7),(108,'O4AA4O5518741','SPLNO4AA4O0000004',8),(109,'O4AA4O5518741','SPLNO4AA4O0000003',9),(110,'O4AA4O5518741','SPLNO4AA4O0000002',10),(111,'O4AA4O6532727','O4AA4O3998722',1),(112,'O4AA4O6532727','SPLNO4AA4O0000010',2),(113,'O4AA4O6532727','SPLNO4AA4O0000009',3),(114,'O4AA4O6532727','SPLNO4AA4O0000008',4),(115,'O4AA4O6532727','SPLNO4AA4O0000007',5),(116,'O4AA4O6532727','SPLNO4AA4O0000006',6),(117,'O4AA4O6532727','SPLNO4AA4O0000005',7),(118,'O4AA4O6532727','SPLNO4AA4O0000004',8),(119,'O4AA4O6532727','SPLNO4AA4O0000003',9),(120,'O4AA4O6532727','SPLNO4AA4O0000002',10),(121,'O4AA4O1761931','O4AA4O6532727',1),(122,'O4AA4O1761931','O4AA4O3998722',2),(123,'O4AA4O1761931','SPLNO4AA4O0000010',3),(124,'O4AA4O1761931','SPLNO4AA4O0000009',4),(125,'O4AA4O1761931','SPLNO4AA4O0000008',5),(126,'O4AA4O1761931','SPLNO4AA4O0000007',6),(127,'O4AA4O1761931','SPLNO4AA4O0000006',7),(128,'O4AA4O1761931','SPLNO4AA4O0000005',8),(129,'O4AA4O1761931','SPLNO4AA4O0000004',9),(130,'O4AA4O1761931','SPLNO4AA4O0000003',10),(131,'O4AA4O1034720','O4AA4O5518741',1),(132,'O4AA4O1034720','O4AA4O3998722',2),(133,'O4AA4O1034720','SPLNO4AA4O0000010',3),(134,'O4AA4O1034720','SPLNO4AA4O0000009',4),(135,'O4AA4O1034720','SPLNO4AA4O0000008',5),(136,'O4AA4O1034720','SPLNO4AA4O0000007',6),(137,'O4AA4O1034720','SPLNO4AA4O0000006',7),(138,'O4AA4O1034720','SPLNO4AA4O0000005',8),(139,'O4AA4O1034720','SPLNO4AA4O0000004',9),(140,'O4AA4O1034720','SPLNO4AA4O0000003',10),(141,'O4AA4O6759435','O4AA4O5518741',1),(142,'O4AA4O6759435','O4AA4O3998722',2),(143,'O4AA4O6759435','SPLNO4AA4O0000010',3),(144,'O4AA4O6759435','SPLNO4AA4O0000009',4),(145,'O4AA4O6759435','SPLNO4AA4O0000008',5),(146,'O4AA4O6759435','SPLNO4AA4O0000007',6),(147,'O4AA4O6759435','SPLNO4AA4O0000006',7),(148,'O4AA4O6759435','SPLNO4AA4O0000005',8),(149,'O4AA4O6759435','SPLNO4AA4O0000004',9),(150,'O4AA4O6759435','SPLNO4AA4O0000003',10),(151,'O4AA4O4058393','O4AA4O6532727',1),(152,'O4AA4O4058393','O4AA4O3998722',2),(153,'O4AA4O4058393','SPLNO4AA4O0000010',3),(154,'O4AA4O4058393','SPLNO4AA4O0000009',4),(155,'O4AA4O4058393','SPLNO4AA4O0000008',5),(156,'O4AA4O4058393','SPLNO4AA4O0000007',6),(157,'O4AA4O4058393','SPLNO4AA4O0000006',7),(158,'O4AA4O4058393','SPLNO4AA4O0000005',8),(159,'O4AA4O4058393','SPLNO4AA4O0000004',9),(160,'O4AA4O4058393','SPLNO4AA4O0000003',10),(161,'O4AA4O6643413','O4AA4O1034720',1),(162,'O4AA4O6643413','O4AA4O5518741',2),(163,'O4AA4O6643413','O4AA4O3998722',3),(164,'O4AA4O6643413','SPLNO4AA4O0000010',4),(165,'O4AA4O6643413','SPLNO4AA4O0000009',5),(166,'O4AA4O6643413','SPLNO4AA4O0000008',6),(167,'O4AA4O6643413','SPLNO4AA4O0000007',7),(168,'O4AA4O6643413','SPLNO4AA4O0000006',8),(169,'O4AA4O6643413','SPLNO4AA4O0000005',9),(170,'O4AA4O6643413','SPLNO4AA4O0000004',10),(171,'O4AA4O8610373','O4AA4O6643413',1),(172,'O4AA4O8610373','O4AA4O1034720',2),(173,'O4AA4O8610373','O4AA4O5518741',3),(174,'O4AA4O8610373','O4AA4O3998722',4),(175,'O4AA4O8610373','SPLNO4AA4O0000010',5),(176,'O4AA4O8610373','SPLNO4AA4O0000009',6),(177,'O4AA4O8610373','SPLNO4AA4O0000008',7),(178,'O4AA4O8610373','SPLNO4AA4O0000007',8),(179,'O4AA4O8610373','SPLNO4AA4O0000006',9),(180,'O4AA4O8610373','SPLNO4AA4O0000005',10),(181,'O4AA4O1283486','O4AA4O1761931',1),(182,'O4AA4O1283486','O4AA4O6532727',2),(183,'O4AA4O1283486','O4AA4O3998722',3),(184,'O4AA4O1283486','SPLNO4AA4O0000010',4),(185,'O4AA4O1283486','SPLNO4AA4O0000009',5),(186,'O4AA4O1283486','SPLNO4AA4O0000008',6),(187,'O4AA4O1283486','SPLNO4AA4O0000007',7),(188,'O4AA4O1283486','SPLNO4AA4O0000006',8),(189,'O4AA4O1283486','SPLNO4AA4O0000005',9),(190,'O4AA4O1283486','SPLNO4AA4O0000004',10),(191,'O4AA4O2551041','O4AA4O1761931',1),(192,'O4AA4O2551041','O4AA4O6532727',2),(193,'O4AA4O2551041','O4AA4O3998722',3),(194,'O4AA4O2551041','SPLNO4AA4O0000010',4),(195,'O4AA4O2551041','SPLNO4AA4O0000009',5),(196,'O4AA4O2551041','SPLNO4AA4O0000008',6),(197,'O4AA4O2551041','SPLNO4AA4O0000007',7),(198,'O4AA4O2551041','SPLNO4AA4O0000006',8),(199,'O4AA4O2551041','SPLNO4AA4O0000005',9),(200,'O4AA4O2551041','SPLNO4AA4O0000004',10),(201,'O4AA4O2402067','O4AA4O6759435',1),(202,'O4AA4O2402067','O4AA4O5518741',2),(203,'O4AA4O2402067','O4AA4O3998722',3),(204,'O4AA4O2402067','SPLNO4AA4O0000010',4),(205,'O4AA4O2402067','SPLNO4AA4O0000009',5),(206,'O4AA4O2402067','SPLNO4AA4O0000008',6),(207,'O4AA4O2402067','SPLNO4AA4O0000007',7),(208,'O4AA4O2402067','SPLNO4AA4O0000006',8),(209,'O4AA4O2402067','SPLNO4AA4O0000005',9),(210,'O4AA4O2402067','SPLNO4AA4O0000004',10),(211,'O4AA4O9442723','O4AA4O1034720',1),(212,'O4AA4O9442723','O4AA4O5518741',2),(213,'O4AA4O9442723','O4AA4O3998722',3),(214,'O4AA4O9442723','SPLNO4AA4O0000010',4),(215,'O4AA4O9442723','SPLNO4AA4O0000009',5),(216,'O4AA4O9442723','SPLNO4AA4O0000008',6),(217,'O4AA4O9442723','SPLNO4AA4O0000007',7),(218,'O4AA4O9442723','SPLNO4AA4O0000006',8),(219,'O4AA4O9442723','SPLNO4AA4O0000005',9),(220,'O4AA4O9442723','SPLNO4AA4O0000004',10),(221,'O4AA4O9676328','O4AA4O6759435',1),(222,'O4AA4O9676328','O4AA4O5518741',2),(223,'O4AA4O9676328','O4AA4O3998722',3),(224,'O4AA4O9676328','SPLNO4AA4O0000010',4),(225,'O4AA4O9676328','SPLNO4AA4O0000009',5),(226,'O4AA4O9676328','SPLNO4AA4O0000008',6),(227,'O4AA4O9676328','SPLNO4AA4O0000007',7),(228,'O4AA4O9676328','SPLNO4AA4O0000006',8),(229,'O4AA4O9676328','SPLNO4AA4O0000005',9),(230,'O4AA4O9676328','SPLNO4AA4O0000004',10),(231,'O4AA4O5542095','O4AA4O2402067',1),(232,'O4AA4O5542095','O4AA4O6759435',2),(233,'O4AA4O5542095','O4AA4O5518741',3),(234,'O4AA4O5542095','O4AA4O3998722',4),(235,'O4AA4O5542095','SPLNO4AA4O0000010',5),(236,'O4AA4O5542095','SPLNO4AA4O0000009',6),(237,'O4AA4O5542095','SPLNO4AA4O0000008',7),(238,'O4AA4O5542095','SPLNO4AA4O0000007',8),(239,'O4AA4O5542095','SPLNO4AA4O0000006',9),(240,'O4AA4O5542095','SPLNO4AA4O0000005',10);
/*!40000 ALTER TABLE `ofa_upliner_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ofa_user_reg_details`
--

DROP TABLE IF EXISTS `ofa_user_reg_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ofa_user_reg_details` (
  `ofa_id` bigint NOT NULL AUTO_INCREMENT,
  `ofa_address` text NOT NULL,
  `ofa_created_by` varchar(255) NOT NULL,
  `ofa_created_dt` date NOT NULL,
  `ofa_dob` date NOT NULL,
  `ofa_email` varchar(255) NOT NULL,
  `ofa_full_name` varchar(255) NOT NULL,
  `ofa_gender` varchar(10) NOT NULL,
  `ofa_is_deleted` int NOT NULL,
  `ofa_member_id` varchar(100) NOT NULL,
  `ofa_mobile_no` varchar(12) NOT NULL,
  `ofa_nationality` varchar(50) NOT NULL,
  `ofa_password` varchar(255) NOT NULL,
  `ofa_pincode` varchar(6) NOT NULL,
  `ofa_updated_by` varchar(255) NOT NULL,
  `ofa_updated_dt` date NOT NULL,
  `ofa_referrer_id` bigint DEFAULT NULL,
  `referrer_id` bigint DEFAULT NULL,
  PRIMARY KEY (`ofa_id`),
  UNIQUE KEY `ofa_member_id` (`ofa_member_id`),
  KEY `idx_ofa_referrer_id` (`ofa_referrer_id`),
  KEY `idx_ofa_email` (`ofa_email`),
  KEY `idx_ofa_mobile_no` (`ofa_mobile_no`),
  KEY `idx_ofa_member_id` (`ofa_member_id`),
  KEY `idx_ofa_email_mobile` (`ofa_email`,`ofa_mobile_no`),
  KEY `FK8voo0dd9abe92g6rgqak2o1f9` (`referrer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ofa_user_reg_details`
--

LOCK TABLES `ofa_user_reg_details` WRITE;
/*!40000 ALTER TABLE `ofa_user_reg_details` DISABLE KEYS */;
INSERT INTO `ofa_user_reg_details` VALUES (1,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe1@example.com','John Doe1','Male',0,'SPLNO4AA4O0000001','9703042356','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(2,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe2@example.com','John Doe2','Male',0,'SPLNO4AA4O0000002','9703042357','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(3,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe3@example.com','John Doe3','Male',0,'SPLNO4AA4O0000003','9703042358','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(4,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe4@example.com','John Doe4','Male',0,'SPLNO4AA4O0000004','9703042359','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(5,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe5@example.com','John Doe5','Male',0,'SPLNO4AA4O0000005','9703042360','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(6,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe6@example.com','John Doe6','Male',0,'SPLNO4AA4O0000006','9703042361','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(7,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe7@example.com','John Doe7','Male',0,'SPLNO4AA4O0000007','9703042362','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(8,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe8@example.com','John Doe8','Male',0,'SPLNO4AA4O0000008','9703042363','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(9,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe9@example.com','John Doe9','Male',0,'SPLNO4AA4O0000009','9703042364','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(10,'123 Main St, Anytown, USA','ONE4ALL_ADMIN_RW','2024-11-12','1990-01-01','john.doe10@example.com','John Doe10','Male',0,'SPLNO4AA4O0000010','9703042365','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(11,'nellure town','User','2024-09-24','1990-09-30','Ramanjaneyulu@example.com','Ramanjaneyulu borragadda','Male',0,'O4AA4O3998722','8250251231','India','$2a$10$24viiJu.xtSCHm10dYNeEOa27utjuqf0jU3jkdk2cb8TAOGql03l2','500067','ONE4ALL_ADMIN_RW','2024-09-24',NULL,NULL),(12,'godavari home','User','2024-09-24','1990-09-30','Prudhvi@gmail.com','Prudhvi','Male',0,'O4AA4O5518741','9381276617','India','$2a$10$CaWnkTcvWljamt8hx.HPQeegOXHiAxehAJBdm.tOLpr9oF55/qxvi','500067','User','2024-09-24',NULL,NULL),(13,'ayapp nager','User','2024-09-24','1997-09-30','jyo@gmail.com','Jyo','feMale',0,'O4AA4O6532727','9347514206','India','$2a$10$whwOW3180pBpCC8HVn/KTOygXmgyyv11YNDO6U16/y6FmqCqsGJxi','500067','User','2024-09-24',NULL,NULL),(14,'ayapp nager','User','2024-09-24','1967-09-30','prabhavathi@gmail.com','prabhavathi','feMale',0,'O4AA4O1034720','7561472031','India','$2a$10$URAPDT0Q7bUhVMp0acRLNO2auQw.f9oc648rzuNmpAYZR1ytCkGJ2','500067','User','2024-09-24',NULL,NULL),(15,'ayapp nager','User','2024-09-24','1963-09-30','khanna@gmail.com','khanna','Male',0,'O4AA4O6759435','7561472032','India','$2a$10$G3pcgQ4lsrUD4otVY.ZtMODxEMOy9zlOGW7i10PdfV/.wDvRgchXW','500067','User','2024-09-24',NULL,NULL),(16,'ayapp nager','User','2024-09-24','1965-09-30','sarada@gmail.com','sarada','feMale',0,'O4AA4O1761931','9381246614','India','$2a$10$DkNeshwmRggI7Fz3a00jl.GUHRBRt05byf44oEWOJhpo5wD49coma','500067','User','2024-09-24',NULL,NULL),(17,'ayapp nager','User','2024-09-24','1995-09-30','suguna@gmail.com','suguna','Male',0,'O4AA4O4058393','9702015265','India','$2a$10$653J7gOZTOsKv15q6G6DlegpzcI2ckULqD6Io31BmP7wFhTSmPt9i','500067','User','2024-09-24',NULL,NULL),(18,'123 Main Street','user','2025-04-13','1990-01-01','dorema@example.com','dorema','Male',0,'O4AA4O6643413','1234567890','India','$2a$10$Nzr8.fCCT6R1sLnTfda/MehW5xI7FbUT4y2vdNfH2v1gH6g7BMLpW','520025','user','2025-04-13',NULL,NULL),(19,'ayapp nager','ONE4ALL_USER_RO','2024-09-24','1995-09-30','padmaja@gmail.com','padmaja','FeMale',0,'O4AA4O8610373','7455252454','India','$2a$10$CX.3D.suInjsHdOeFANRaufxMFapotBybfky688WeQtuT7m9hCrsi','500067','ONE4ALL_USER_RO','2024-09-24',NULL,NULL),(20,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','Bhaskar@example.com','Bhaskar','Male',0,'O4AA4O1283486','8522542545','India','$2a$10$NHhS1/LkDO.QeY5rSpnRNONLPaRwdFm.kQxzv2vMrh8JuzMO9JJCm','520032','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(21,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','rabada@example.com','rabada','Male',0,'O4AA4O2551041','9855623521','India','$2a$10$I.S6vu99byXpdfA9X29Hle9Z7cXWsZcE3I65OQy62uEfTA58r8pDG','520035','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(22,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','ajar@example.com','Ajar','Male',0,'O4AA4O2402067','7855252121','India','$2a$10$hEWGEuIYqn1yWfffmmYfuOw8ZMBdgIF14e7e6NufCCzGhJPeBrese','520032','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(23,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','uma@example.com','uma shanker','Male',0,'O4AA4O9442723','7400251210','India','$2a$10$T.SUAqz3k1LW./mpoPwZuehVe/7vzrPkCk3d6LKv8vCviroDCBGsa','500035','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(24,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','rachana@example.com','rachana','Female',0,'O4AA4O6780065','7855252121','India','$2a$10$ppsG6vp5N4RGK1dnymzQ6eQXa55lAZaokmldLiWz/pDTfeolBCdWu','500062','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(25,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','pradeep@example.com','pradeep','Male',0,'O4AA4O4183227','8955623121','India','$2a$10$Oi2JwtIHxhN0VFY.S2C6Bu0Z2S.p3zEljqGnMzZuqdJCPBGjuO3n.','520032','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(26,'123 Main Streetnelneeluu','ONE4ALL_USER_RO','2025-05-03','1990-02-13','lavanya@gmail.com','lavanya','female',0,'O4AA4O2429478','8522012454','india','$2a$10$MCDzbchooMPGf.KltpAN9e2ECjEBt5FznpcXJEs91f5lVmiJFTqoi','500023','ONE4ALL_USER_RO','2025-05-03',NULL,NULL),(27,'123 Main Streetass asas','user','2025-05-03','1992-05-12','budhha@gmail.com','budhha','male',0,'O4AA4O1990897','7522542878','india','$2a$10$iN8vuaVLX5eg3iA87qqsreQVJ/c7CM5AJn8S8tsegOdOnQym.wJMW','500025','user','2025-05-03',NULL,NULL),(28,'123 Main Street','admin','2025-05-03','1990-01-01','kling@example.com','kling','male',0,'O4AA4O9295264','7588525454','India','$2a$10$nY3GzFz1zqpVIvkRCjEhXut3Xh8dNyICAC2qLoftaDDk6OPCH8DaK','500025','admin','2025-05-03',NULL,NULL),(29,'123 Main Street','admin','2025-05-03','1990-01-01','yamini@gmail.com','yamini chohan','female',0,'O4AA4O8671522','7400256454','India','$2a$10$plE2pQNRteZ69trTd6zvmOGp6HtfkTt6OL9Hv9ksGVr5D5BEYS0hu','500025','admin','2025-05-03',NULL,NULL),(30,'123 Main Street','admin','2025-05-03','1990-01-01','omkar@example.com','omkar','male',0,'O4AA4O6868935','7855623252','India','$2a$10$zt5GYTVGzIkR1kHQS8PM2OubCfQ4Yj2eVsXkdMOg9R3XllF5kPBcm','500025','admin','2025-05-03',NULL,NULL),(31,'123 Main Street','admin','2025-05-03','1990-01-01','jahapana@example.com','jahapana','male',0,'O4AA4O3025575','8522545254','India','$2a$10$/XllS/0wEpBhH0RYVxM2w.Y1UnKJ7QnxVM4OC.kMBvgGkdSe2ZgsG','500023','admin','2025-05-03',NULL,NULL),(32,'123 Main Street','admin','2025-05-03','1990-01-01','sruthi@example.com','sruthi','female',0,'O4AA4O1643735','7522545854','India','$2a$10$D564jsM1hBE9VUKNrJRaZe1/.2jMQNCoMWGFE8DDnChNtB73Q5lzG','500025','admin','2025-05-03',NULL,NULL),(33,'123 Main Street','admin','2025-05-03','1991-05-12','gavaskar@gmail.com','gavaskar','male',0,'O4AA4O5542095','7855252454','india','$2a$10$3FAi.D.J25wRWL612VtTeuED8dbKsPNtLtjMqzsF3J/SGLKGhWt4y','500025','admin','2025-05-03',NULL,NULL),(34,'123 Main Street','user','2025-05-04','1992-02-12','abhimanyu@gmail.com','abhimanyu','male',0,'O4AA4O1355832','8522545254','india','$2a$10$0JiqyRszHjjVVKshe69H.ua9Zgxj5sIE1ibGdM2XJZ1uDC9OMwrXO','500025','user','2025-05-04',NULL,NULL),(35,'nellure pedda reddy gari taluka','user','2025-05-04','1992-02-12','Tejeswari@gmail.com','Tejeswari','female',0,'O4AA4O9676328','8525021547','india','$2a$10$rDhDBAnghHy0I/4FWKFtzOtcWv/k7ae8Vimzd0jF7LdgiwPvkLage','500023','user','2025-05-04',NULL,NULL);
/*!40000 ALTER TABLE `ofa_user_reg_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `token` varchar(255) NOT NULL,
  `expiry_time` timestamp NOT NULL,
  `used` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_email` (`email`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
INSERT INTO `password_reset_tokens` VALUES (1,'john.doe1@example.com','57ea448e-4361-417f-81dc-6f92b675be99','2025-03-23 21:09:34',0),(2,'john.doe1@example.com','8f031978-858a-4954-ba66-5a49078ed2b4','2025-03-23 21:22:57',0),(3,'john.doe1@example.com','f1d2115b-c780-43a2-8343-9ae453d564b9','2025-03-23 21:32:35',0);
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ONE4ALL_ADMIN_RO'),(3,'ONE4ALL_ADMIN_RW'),(5,'ONE4ALL_ADMIN_W'),(2,'ONE4ALL_USER_RO'),(4,'ONE4ALL_USER_RW'),(6,'ONE4ALL_USER_W');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_help_status_dashboard`
--

DROP TABLE IF EXISTS `v_help_status_dashboard`;
/*!50001 DROP VIEW IF EXISTS `v_help_status_dashboard`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_help_status_dashboard` AS SELECT 
 1 AS `member_id`,
 1 AS `full_name`,
 1 AS `total_help_given`,
 1 AS `total_amount_given`,
 1 AS `total_amount_received_ack`,
 1 AS `total_amount_pending`,
 1 AS `available_balance`,
 1 AS `last_received_date`,
 1 AS `pending_help_count`,
 1 AS `completed_help_count`,
 1 AS `this_month_total`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_help_status_dashboard_bkp`
--

DROP TABLE IF EXISTS `v_help_status_dashboard_bkp`;
/*!50001 DROP VIEW IF EXISTS `v_help_status_dashboard_bkp`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_help_status_dashboard_bkp` AS SELECT 
 1 AS `member_id`,
 1 AS `full_name`,
 1 AS `total_help_given`,
 1 AS `total_amount_given`,
 1 AS `total_amount_received_ack`,
 1 AS `total_amount_pending`,
 1 AS `available_balance`,
 1 AS `last_received_date`,
 1 AS `pending_help_count`,
 1 AS `completed_help_count`,
 1 AS `this_month_total`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_help_summary`
--

DROP TABLE IF EXISTS `v_help_summary`;
/*!50001 DROP VIEW IF EXISTS `v_help_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_help_summary` AS SELECT 
 1 AS `member_id`,
 1 AS `total_submissions`,
 1 AS `total_given`,
 1 AS `total_acknowledged`,
 1 AS `total_pending`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_received_help_summary`
--

DROP TABLE IF EXISTS `v_received_help_summary`;
/*!50001 DROP VIEW IF EXISTS `v_received_help_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_received_help_summary` AS SELECT 
 1 AS `member_id`,
 1 AS `payment_id`,
 1 AS `status`,
 1 AS `transaction_id`,
 1 AS `request_received_at`,
 1 AS `request_modified_at`,
 1 AS `received_amount`,
 1 AS `received_from`,
 1 AS `received_from_name`,
 1 AS `proof_doc`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_received_help_totals`
--

DROP TABLE IF EXISTS `v_received_help_totals`;
/*!50001 DROP VIEW IF EXISTS `v_received_help_totals`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_received_help_totals` AS SELECT 
 1 AS `member_id`,
 1 AS `approved_request_count`,
 1 AS `rejected_request_count`,
 1 AS `total_request_count`,
 1 AS `total_received_amount`,
 1 AS `this_month_received_amount`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `v_help_status_dashboard`
--

/*!50001 DROP VIEW IF EXISTS `v_help_status_dashboard`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_help_status_dashboard` AS select `u`.`ofa_member_id` AS `member_id`,`u`.`ofa_full_name` AS `full_name`,coalesce(`hs`.`total_submissions`,0) AS `total_help_given`,coalesce(`hs`.`total_given`,0.00) AS `total_amount_given`,coalesce(`hs`.`total_acknowledged`,0.00) AS `total_amount_received_ack`,coalesce(`hs`.`total_pending`,0.00) AS `total_amount_pending`,(case when (`u`.`ofa_member_id` like 'SPLNO4AA4O%') then coalesce(`rt`.`total_received_amount`,0.00) else ((3000.00 + coalesce(`rt`.`total_received_amount`,0.00)) - coalesce(`hs`.`total_given`,0.00)) end) AS `available_balance`,`rs`.`last_received_date` AS `last_received_date`,coalesce(`hs`.`pending_help_count`,0) AS `pending_help_count`,coalesce(`hs`.`completed_help_count`,0) AS `completed_help_count`,coalesce(`hs`.`this_month_total`,0.00) AS `this_month_total` from (((`ofa_user_reg_details` `u` left join (select `hs`.`sender_member_id` AS `sender_member_id`,count(0) AS `total_submissions`,sum(`hs`.`submitted_amount`) AS `total_given`,sum((case when (`hs`.`submission_status` = 'RECEIVED') then `hs`.`submitted_amount` else 0 end)) AS `total_acknowledged`,sum((case when (`hs`.`submission_status` = 'SUBMITTED') then `hs`.`submitted_amount` else 0 end)) AS `total_pending`,count((case when (`hs`.`submission_status` = 'SUBMITTED') then 1 end)) AS `pending_help_count`,count((case when (`hs`.`submission_status` = 'RECEIVED') then 1 end)) AS `completed_help_count`,sum((case when ((month(`hs`.`transaction_date`) = month(curdate())) and (year(`hs`.`transaction_date`) = year(curdate()))) then `hs`.`submitted_amount` else 0 end)) AS `this_month_total` from `help_submission` `hs` group by `hs`.`sender_member_id`) `hs` on((`u`.`ofa_member_id` = `hs`.`sender_member_id`))) left join `v_received_help_totals` `rt` on((`u`.`ofa_member_id` = `rt`.`member_id`))) left join (select `help_submission`.`receiver_member_id` AS `receiver_member_id`,max(`help_submission`.`verification_date`) AS `last_received_date` from `help_submission` where (`help_submission`.`submission_status` = 'RECEIVED') group by `help_submission`.`receiver_member_id`) `rs` on((`u`.`ofa_member_id` = `rs`.`receiver_member_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_help_status_dashboard_bkp`
--

/*!50001 DROP VIEW IF EXISTS `v_help_status_dashboard_bkp`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_help_status_dashboard_bkp` AS select `u`.`ofa_member_id` AS `member_id`,`u`.`ofa_full_name` AS `full_name`,coalesce(`hs`.`total_submissions`,0) AS `total_help_given`,coalesce(`hs`.`total_given`,0.00) AS `total_amount_given`,coalesce(`hs`.`total_acknowledged`,0.00) AS `total_amount_received_ack`,coalesce(`hs`.`total_pending`,0.00) AS `total_amount_pending`,(3000.00 - coalesce(`hs`.`total_given`,0.00)) AS `available_balance`,`rs`.`last_received_date` AS `last_received_date`,coalesce(`hs`.`pending_help_count`,0) AS `pending_help_count`,coalesce(`hs`.`completed_help_count`,0) AS `completed_help_count`,coalesce(`hs`.`this_month_total`,0.00) AS `this_month_total` from ((`ofa_user_reg_details` `u` left join (select `help_submission`.`sender_member_id` AS `sender_member_id`,count(0) AS `total_submissions`,sum(`help_submission`.`submitted_amount`) AS `total_given`,sum((case when (`help_submission`.`submission_status` = 'RECEIVED') then `help_submission`.`submitted_amount` else 0 end)) AS `total_acknowledged`,sum((case when (`help_submission`.`submission_status` = 'SUBMITTED') then `help_submission`.`submitted_amount` else 0 end)) AS `total_pending`,count((case when (`help_submission`.`submission_status` = 'SUBMITTED') then 1 end)) AS `pending_help_count`,count((case when (`help_submission`.`submission_status` = 'RECEIVED') then 1 end)) AS `completed_help_count`,sum((case when ((month(`help_submission`.`transaction_date`) = month(curdate())) and (year(`help_submission`.`transaction_date`) = year(curdate()))) then `help_submission`.`submitted_amount` else 0 end)) AS `this_month_total` from `help_submission` group by `help_submission`.`sender_member_id`) `hs` on((`u`.`ofa_member_id` = `hs`.`sender_member_id`))) left join (select `help_submission`.`receiver_member_id` AS `receiver_member_id`,max(`help_submission`.`verification_date`) AS `last_received_date` from `help_submission` where (`help_submission`.`submission_status` = 'RECEIVED') group by `help_submission`.`receiver_member_id`) `rs` on((`u`.`ofa_member_id` = `rs`.`receiver_member_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_help_summary`
--

/*!50001 DROP VIEW IF EXISTS `v_help_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_help_summary` AS select `help_submission`.`sender_member_id` AS `member_id`,count(0) AS `total_submissions`,sum(`help_submission`.`submitted_amount`) AS `total_given`,sum((case when (`help_submission`.`submission_status` = 'RECEIVED') then `help_submission`.`submitted_amount` else 0 end)) AS `total_acknowledged`,sum((case when (`help_submission`.`submission_status` = 'SUBMITTED') then `help_submission`.`submitted_amount` else 0 end)) AS `total_pending` from `help_submission` group by `help_submission`.`sender_member_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_received_help_summary`
--

/*!50001 DROP VIEW IF EXISTS `v_received_help_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_received_help_summary` AS select `hs`.`receiver_member_id` AS `member_id`,`hs`.`id` AS `payment_id`,(case when (`hs`.`submission_status` = 'SUBMITTED') then 'PROCESSING' when (`hs`.`submission_status` = 'RECEIVED') then 'RECEIVED' when (`hs`.`submission_status` = 'REJECTED') then 'REJECTED' else 'UNKNOWN' end) AS `status`,`hs`.`submission_reference_id` AS `transaction_id`,`hs`.`transaction_date` AS `request_received_at`,`hs`.`verification_date` AS `request_modified_at`,`hs`.`submitted_amount` AS `received_amount`,`hs`.`sender_member_id` AS `received_from`,`u`.`ofa_full_name` AS `received_from_name`,`hs`.`proof` AS `proof_doc` from (`help_submission` `hs` left join `ofa_user_reg_details` `u` on((`hs`.`sender_member_id` = `u`.`ofa_member_id`))) where (`hs`.`receiver_member_id` is not null) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_received_help_totals`
--

/*!50001 DROP VIEW IF EXISTS `v_received_help_totals`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_received_help_totals` AS select `hs`.`receiver_member_id` AS `member_id`,count((case when (`hs`.`submission_status` = 'RECEIVED') then 1 end)) AS `approved_request_count`,count((case when (`hs`.`submission_status` = 'REJECTED') then 1 end)) AS `rejected_request_count`,count((case when (`hs`.`submission_status` in ('RECEIVED','REJECTED','SUBMITTED')) then 1 end)) AS `total_request_count`,sum((case when (`hs`.`submission_status` = 'RECEIVED') then `hs`.`submitted_amount` else 0 end)) AS `total_received_amount`,sum((case when ((`hs`.`submission_status` = 'RECEIVED') and (month(`hs`.`transaction_date`) = month(curdate())) and (year(`hs`.`transaction_date`) = year(curdate()))) then `hs`.`submitted_amount` else 0 end)) AS `this_month_received_amount` from `help_submission` `hs` where (`hs`.`receiver_member_id` is not null) group by `hs`.`receiver_member_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-31 20:28:06
