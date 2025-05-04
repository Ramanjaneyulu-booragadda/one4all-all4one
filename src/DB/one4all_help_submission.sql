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
  `proof_url` varchar(255) DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `help_submission`
--

LOCK TABLES `help_submission` WRITE;
/*!40000 ALTER TABLE `help_submission` DISABLE KEYS */;
INSERT INTO `help_submission` VALUES (6,'O4AA4O3998722','SPLNO4AA4O0000010','9703042365',1,2000.00,'/uploads/db60fe60-908d-4b4b-9ec1-cacd92e5609d.pdf','Received and verified on UPI','RECEIVED','2025-04-20 17:25:41','5e66fe79-d613-4b43-8321-b549a665bd22','SPLNO4AA4O0000010','2025-04-21 03:02:24');
/*!40000 ALTER TABLE `help_submission` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-04 11:39:16
