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
-- Table structure for table `systemconfig`
--


INSERT INTO `systemconfig` (`id`, `deleted`, `version`) VALUES
(1, b'0', 1);

-- --------------------------------------------------------

--
-- Table structure for table `systemconfig_properties`
--

--
-- Dumping data for table `systemconfig_properties`
--

INSERT INTO `systemconfig_properties` (`SystemConfig_id`, `prop_value`, `prop_name`) VALUES
(1, 'Noktiz', 'App_Name'),
(1, '1443280285948927', 'FB_APP_ID'),
(1, '461c40cdde051a1cd8a79aa560b07bea', 'FB_APP_SECRET'),
(1, '10', 'ForgetPasswordTokenAllowdUseTime'),
(1, '1440', 'ForgetPasswordTokenTimeOut'),
(1, 'localhost', 'proxyAddress'),
(1, '8580', 'proxyPort');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `systemconfig`
--
--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `systemconfig`
--
--
-- Constraints for dumped tables
--

--
-- Constraints for table `systemconfig_properties`
--

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
