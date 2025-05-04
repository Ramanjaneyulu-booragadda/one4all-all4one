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
/*!50001 VIEW `v_received_help_summary` AS select `hs`.`receiver_member_id` AS `member_id`,`hs`.`id` AS `payment_id`,(case when (`hs`.`submission_status` = 'SUBMITTED') then 'PROCESSING' when (`hs`.`submission_status` = 'RECEIVED') then 'RECEIVED' when (`hs`.`submission_status` = 'REJECTED') then 'REJECTED' else 'UNKNOWN' end) AS `status`,`hs`.`submission_reference_id` AS `transaction_id`,`hs`.`transaction_date` AS `request_received_at`,`hs`.`verification_date` AS `request_modified_at`,`hs`.`submitted_amount` AS `received_amount`,`hs`.`sender_member_id` AS `received_from`,`hs`.`proof_url` AS `proof_doc` from `help_submission` `hs` where (`hs`.`receiver_member_id` is not null) */;
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
/*!50001 VIEW `v_help_status_dashboard` AS select `u`.`ofa_member_id` AS `member_id`,`u`.`ofa_full_name` AS `full_name`,coalesce(`hs`.`total_submissions`,0) AS `total_help_given`,coalesce(`hs`.`total_given`,0.00) AS `total_amount_given`,coalesce(`hs`.`total_acknowledged`,0.00) AS `total_amount_received_ack`,coalesce(`hs`.`total_pending`,0.00) AS `total_amount_pending`,(3000.00 - coalesce(`hs`.`total_given`,0.00)) AS `available_balance`,`rs`.`last_received_date` AS `last_received_date`,coalesce(`hs`.`pending_help_count`,0) AS `pending_help_count`,coalesce(`hs`.`completed_help_count`,0) AS `completed_help_count`,coalesce(`hs`.`this_month_total`,0.00) AS `this_month_total` from ((`ofa_user_reg_details` `u` left join (select `help_submission`.`sender_member_id` AS `sender_member_id`,count(0) AS `total_submissions`,sum(`help_submission`.`submitted_amount`) AS `total_given`,sum((case when (`help_submission`.`submission_status` = 'RECEIVED') then `help_submission`.`submitted_amount` else 0 end)) AS `total_acknowledged`,sum((case when (`help_submission`.`submission_status` = 'SUBMITTED') then `help_submission`.`submitted_amount` else 0 end)) AS `total_pending`,count((case when (`help_submission`.`submission_status` = 'SUBMITTED') then 1 end)) AS `pending_help_count`,count((case when (`help_submission`.`submission_status` = 'RECEIVED') then 1 end)) AS `completed_help_count`,sum((case when ((month(`help_submission`.`transaction_date`) = month(curdate())) and (year(`help_submission`.`transaction_date`) = year(curdate()))) then `help_submission`.`submitted_amount` else 0 end)) AS `this_month_total` from `help_submission` group by `help_submission`.`sender_member_id`) `hs` on((`u`.`ofa_member_id` = `hs`.`sender_member_id`))) left join (select `help_submission`.`receiver_member_id` AS `receiver_member_id`,max(`help_submission`.`verification_date`) AS `last_received_date` from `help_submission` where (`help_submission`.`submission_status` = 'RECEIVED') group by `help_submission`.`receiver_member_id`) `rs` on((`u`.`ofa_member_id` = `rs`.`receiver_member_id`))) */;
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

-- Dump completed on 2025-05-04 11:39:17
