-- phpMyAdmin SQL Dump
-- version 4.2.7.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Nov 20, 2014 at 12:37 PM
-- Server version: 5.5.39
-- PHP Version: 5.4.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `noktiz`
--

-- --------------------------------------------------------

--
-- Table structure for table `SystemConfig`
--

CREATE TABLE IF NOT EXISTS `SystemConfig` (
`id` bigint(20) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `version` bigint(20) DEFAULT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `SystemConfig`
--

INSERT INTO `SystemConfig` (`id`, `deleted`, `version`) VALUES
(1, b'0', 1);

-- --------------------------------------------------------

--
-- Table structure for table `SystemConfig_properties`
--

CREATE TABLE IF NOT EXISTS `SystemConfig_properties` (
  `SystemConfig_id` bigint(20) NOT NULL,
  `prop_value` varchar(255) DEFAULT NULL,
  `prop_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `SystemConfig_properties`
--

INSERT INTO `SystemConfig_properties` (`SystemConfig_id`, `prop_value`, `prop_name`) VALUES
(1, 'Noktiz', 'App_Name'),
(1, '1443280285948927', 'FB_APP_ID'),
(1, '461c40cdde051a1cd8a79aa560b07bea', 'FB_APP_SECRET'),
(1, '10', 'ForgetPasswordTokenAllowdUseTime'),
(1, '1440', 'ForgetPasswordTokenTimeOut');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `SystemConfig`
--
ALTER TABLE `SystemConfig`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `SystemConfig_properties`
--
ALTER TABLE `SystemConfig_properties`
 ADD PRIMARY KEY (`SystemConfig_id`,`prop_name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `SystemConfig`
--
ALTER TABLE `SystemConfig`
MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `SystemConfig_properties`
--
ALTER TABLE `SystemConfig_properties`
ADD CONSTRAINT `FK_djus2jbb9hdge1p3pmt3r3yv3` FOREIGN KEY (`SystemConfig_id`) REFERENCES `SystemConfig` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
