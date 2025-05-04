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
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ofa_user_reg_details`
--

LOCK TABLES `ofa_user_reg_details` WRITE;
/*!40000 ALTER TABLE `ofa_user_reg_details` DISABLE KEYS */;
INSERT INTO `ofa_user_reg_details` VALUES (1,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe1@example.com','John Doe1','Male',0,'SPLNO4AA4O0000001','9703042356','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(2,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe2@example.com','John Doe2','Male',0,'SPLNO4AA4O0000002','9703042357','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(3,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe3@example.com','John Doe3','Male',0,'SPLNO4AA4O0000003','9703042358','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(4,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe4@example.com','John Doe4','Male',0,'SPLNO4AA4O0000004','9703042359','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(5,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe5@example.com','John Doe5','Male',0,'SPLNO4AA4O0000005','9703042360','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(6,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe6@example.com','John Doe6','Male',0,'SPLNO4AA4O0000006','9703042361','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(7,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe7@example.com','John Doe7','Male',0,'SPLNO4AA4O0000007','9703042362','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(8,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe8@example.com','John Doe8','Male',0,'SPLNO4AA4O0000008','9703042363','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(9,'123 Main St, Anytown, USA','Admin','2024-11-12','1990-01-01','john.doe9@example.com','John Doe9','Male',0,'SPLNO4AA4O0000009','9703042364','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(10,'123 Main St, Anytown, USA','ONE4ALL_ADMIN_RW','2024-11-12','1990-01-01','john.doe10@example.com','John Doe10','Male',0,'SPLNO4AA4O0000010','9703042365','Indian','$2a$10$upoPqZJksNNMyPWbqPsRf.J/fSKCEiI0JiHkFudDx9T2ZPKpsJtCa','500067','Admin','2024-11-12',NULL,NULL),(11,'nellure peddareddy gari gramam','User','2024-09-24','1990-09-30','Ramanjaneyulu@example.com','Ramanjaneyulu borragadda','Male',0,'O4AA4O3998722','8250251231','India','$2a$10$24viiJu.xtSCHm10dYNeEOa27utjuqf0jU3jkdk2cb8TAOGql03l2','500067','ONE4ALL_ADMIN_RW','2024-09-24',NULL,NULL),(12,'ayapp nager','User','2024-09-24','1990-09-30','Prudhvi@gmail.com','Prudhvi','Male',0,'O4AA4O5518741','9381276617','India','$2a$10$CaWnkTcvWljamt8hx.HPQeegOXHiAxehAJBdm.tOLpr9oF55/qxvi','500067','User','2024-09-24',NULL,NULL),(13,'ayapp nager','User','2024-09-24','1997-09-30','jyo@gmail.com','Jyo','feMale',0,'O4AA4O6532727','9347514206','India','$2a$10$whwOW3180pBpCC8HVn/KTOygXmgyyv11YNDO6U16/y6FmqCqsGJxi','500067','User','2024-09-24',NULL,NULL),(14,'ayapp nager','User','2024-09-24','1967-09-30','prabhavathi@gmail.com','prabhavathi','feMale',0,'O4AA4O1034720','7561472031','India','$2a$10$URAPDT0Q7bUhVMp0acRLNO2auQw.f9oc648rzuNmpAYZR1ytCkGJ2','500067','User','2024-09-24',NULL,NULL),(15,'ayapp nager','User','2024-09-24','1963-09-30','khanna@gmail.com','khanna','Male',0,'O4AA4O6759435','7561472032','India','$2a$10$G3pcgQ4lsrUD4otVY.ZtMODxEMOy9zlOGW7i10PdfV/.wDvRgchXW','500067','User','2024-09-24',NULL,NULL),(16,'ayapp nager','User','2024-09-24','1965-09-30','sarada@gmail.com','sarada','feMale',0,'O4AA4O1761931','9381246614','India','$2a$10$DkNeshwmRggI7Fz3a00jl.GUHRBRt05byf44oEWOJhpo5wD49coma','500067','User','2024-09-24',NULL,NULL),(17,'ayapp nager','User','2024-09-24','1995-09-30','suguna@gmail.com','suguna','Male',0,'O4AA4O4058393','9702015265','India','$2a$10$653J7gOZTOsKv15q6G6DlegpzcI2ckULqD6Io31BmP7wFhTSmPt9i','500067','User','2024-09-24',NULL,NULL),(18,'123 Main Street','user','2025-04-13','1990-01-01','dorema@example.com','dorema','Male',0,'O4AA4O6643413','1234567890','India','$2a$10$Nzr8.fCCT6R1sLnTfda/MehW5xI7FbUT4y2vdNfH2v1gH6g7BMLpW','520025','user','2025-04-13',NULL,NULL),(19,'ayapp nager','ONE4ALL_USER_RO','2024-09-24','1995-09-30','padmaja@gmail.com','padmaja','FeMale',0,'O4AA4O8610373','7455252454','India','$2a$10$CX.3D.suInjsHdOeFANRaufxMFapotBybfky688WeQtuT7m9hCrsi','500067','ONE4ALL_USER_RO','2024-09-24',NULL,NULL),(20,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','Bhaskar@example.com','Bhaskar','Male',0,'O4AA4O1283486','8522542545','India','$2a$10$NHhS1/LkDO.QeY5rSpnRNONLPaRwdFm.kQxzv2vMrh8JuzMO9JJCm','520032','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(21,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','rabada@example.com','rabada','Male',0,'O4AA4O2551041','9855623521','India','$2a$10$I.S6vu99byXpdfA9X29Hle9Z7cXWsZcE3I65OQy62uEfTA58r8pDG','520035','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(22,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','ajar@example.com','Ajar','Male',0,'O4AA4O2402067','7855252121','India','$2a$10$hEWGEuIYqn1yWfffmmYfuOw8ZMBdgIF14e7e6NufCCzGhJPeBrese','520032','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(23,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','uma@example.com','uma shanker','Male',0,'O4AA4O9442723','7400251210','India','$2a$10$T.SUAqz3k1LW./mpoPwZuehVe/7vzrPkCk3d6LKv8vCviroDCBGsa','500035','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(24,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','rachana@example.com','rachana','Female',0,'O4AA4O6780065','7855252121','India','$2a$10$ppsG6vp5N4RGK1dnymzQ6eQXa55lAZaokmldLiWz/pDTfeolBCdWu','500062','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(25,'123 Main Street','ONE4ALL_USER_RO','2025-04-13','1990-01-01','pradeep@example.com','pradeep','Male',0,'O4AA4O4183227','8955623121','India','$2a$10$Oi2JwtIHxhN0VFY.S2C6Bu0Z2S.p3zEljqGnMzZuqdJCPBGjuO3n.','520032','ONE4ALL_USER_RO','2025-04-13',NULL,NULL),(26,'123 Main Streetnelneeluu','ONE4ALL_USER_RO','2025-05-03','1990-02-13','lavanya@gmail.com','lavanya','female',0,'O4AA4O2429478','8522012454','india','$2a$10$MCDzbchooMPGf.KltpAN9e2ECjEBt5FznpcXJEs91f5lVmiJFTqoi','500023','ONE4ALL_USER_RO','2025-05-03',NULL,NULL),(27,'123 Main Streetass asas','user','2025-05-03','1992-05-12','budhha@gmail.com','budhha','male',0,'O4AA4O1990897','7522542878','india','$2a$10$iN8vuaVLX5eg3iA87qqsreQVJ/c7CM5AJn8S8tsegOdOnQym.wJMW','500025','user','2025-05-03',NULL,NULL),(28,'123 Main Street','admin','2025-05-03','1990-01-01','kling@example.com','kling','male',0,'O4AA4O9295264','7588525454','India','$2a$10$nY3GzFz1zqpVIvkRCjEhXut3Xh8dNyICAC2qLoftaDDk6OPCH8DaK','500025','admin','2025-05-03',NULL,NULL),(29,'123 Main Street','admin','2025-05-03','1990-01-01','yamini@gmail.com','yamini chohan','female',0,'O4AA4O8671522','7400256454','India','$2a$10$plE2pQNRteZ69trTd6zvmOGp6HtfkTt6OL9Hv9ksGVr5D5BEYS0hu','500025','admin','2025-05-03',NULL,NULL),(30,'123 Main Street','admin','2025-05-03','1990-01-01','omkar@example.com','omkar','male',0,'O4AA4O6868935','7855623252','India','$2a$10$zt5GYTVGzIkR1kHQS8PM2OubCfQ4Yj2eVsXkdMOg9R3XllF5kPBcm','500025','admin','2025-05-03',NULL,NULL),(31,'123 Main Street','admin','2025-05-03','1990-01-01','jahapana@example.com','jahapana','male',0,'O4AA4O3025575','8522545254','India','$2a$10$/XllS/0wEpBhH0RYVxM2w.Y1UnKJ7QnxVM4OC.kMBvgGkdSe2ZgsG','500023','admin','2025-05-03',NULL,NULL),(32,'123 Main Street','admin','2025-05-03','1990-01-01','sruthi@example.com','sruthi','female',0,'O4AA4O1643735','7522545854','India','$2a$10$D564jsM1hBE9VUKNrJRaZe1/.2jMQNCoMWGFE8DDnChNtB73Q5lzG','500025','admin','2025-05-03',NULL,NULL),(33,'123 Main Street','admin','2025-05-03','1991-05-12','gavaskar@gmail.com','gavaskar','male',0,'O4AA4O5542095','7855252454','india','$2a$10$3FAi.D.J25wRWL612VtTeuED8dbKsPNtLtjMqzsF3J/SGLKGhWt4y','500025','admin','2025-05-03',NULL,NULL),(34,'123 Main Street','user','2025-05-04','1992-02-12','abhimanyu@gmail.com','abhimanyu','male',0,'O4AA4O1355832','8522545254','india','$2a$10$0JiqyRszHjjVVKshe69H.ua9Zgxj5sIE1ibGdM2XJZ1uDC9OMwrXO','500025','user','2025-05-04',NULL,NULL);
/*!40000 ALTER TABLE `ofa_user_reg_details` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-04 11:39:17
