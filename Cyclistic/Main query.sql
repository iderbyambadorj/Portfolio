USE sys;
-- Database Creation
-- CREATE TABLE `trips_2023_03` (
--   `ride_id` varchar(20) NOT NULL,
--   `rideable_type` varchar(45) DEFAULT NULL,
--   `started_at` datetime DEFAULT NULL,
--   `ended_at` varchar(45) DEFAULT NULL,
--   `start_station_name` varchar(100) DEFAULT NULL,
--   `start_station_id` varchar(45) DEFAULT NULL,
--   `end_station_name` varchar(100) DEFAULT NULL,
--   `end_station_id` varchar(45) DEFAULT NULL,
--   `start_lat` float DEFAULT NULL,
--   `start_lng` float DEFAULT NULL,
--   `end_lat` float DEFAULT NULL,
--   `end_lng` float DEFAULT NULL,
--   `member_casual` varchar(45) DEFAULT NULL,
--   PRIMARY KEY (`ride_id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- Aggregate all data into a single table called 'trips'
-- CREATE TABLE IF NOT EXISTS trips AS ( 
-- 	SELECT * FROM trips_2023_03
-- 	UNION ALL 
-- 	SELECT * FROM trips_2023_04
-- 	UNION ALL 
-- 	SELECT * FROM trips_2023_05
-- 	UNION ALL 
-- 	SELECT * FROM trips_2023_06
-- 	UNION ALL 
-- 	SELECT * FROM trips_2023_07
-- 	UNION ALL 
-- 	SELECT * FROM trips_2023_08
-- 	UNION ALL 
-- 	SELECT * FROM trips_2023_09
-- 	UNION ALL 
-- 	SELECT * FROM trips_2023_10
-- 	UNION ALL 
-- 	SELECT * FROM trips_2023_11
-- 	UNION ALL 
-- 	SELECT * FROM trips_2023_12
-- 	UNION ALL 
-- 	SELECT * FROM trips_2024_01
-- 	UNION ALL 
-- 	SELECT * FROM trips_2024_02
-- );

-- Step 1: Data Cleaning
-- 1. Remove duplicates (if any)
SELECT COUNT(ride_id) AS duplicate_ride_count
FROM (
    SELECT ride_id, COUNT(*) AS count
    FROM trips
    GROUP BY ride_id
    HAVING count > 1
) AS duplicate_rides;


-- 2. Handle missing values (if any)
SELECT 'ride_id' AS column_name, COUNT(*) AS null_empty_values FROM trips WHERE ride_id = ''
	UNION ALL
SELECT 'rideable_type', COUNT(*) FROM trips WHERE rideable_type = ''
	UNION ALL
SELECT 'started_at', COUNT(*) FROM trips WHERE started_at IS NULL
	UNION ALL
SELECT 'ended_at', COUNT(*) FROM trips WHERE ended_at IS NULL
	UNION ALL
SELECT 'start_station_name', COUNT(*) FROM trips WHERE start_station_name = ''
	UNION ALL
SELECT 'start_station_id', COUNT(*) FROM trips WHERE start_station_id = ''
	UNION ALL
SELECT 'end_station_name', COUNT(*) FROM trips WHERE end_station_name = ''
	UNION ALL
SELECT 'end_station_id', COUNT(*) FROM trips WHERE end_station_id = ''
	UNION ALL
SELECT 'start_lat', COUNT(*) FROM trips WHERE start_lat = ''
	UNION ALL
SELECT 'start_lng', COUNT(*) FROM trips WHERE start_lng = ''
	UNION ALL
SELECT 'end_lat', COUNT(*) FROM trips WHERE end_lat = ''
	UNION ALL
SELECT 'end_lng', COUNT(*) FROM trips WHERE end_lng = ''
	UNION ALL
SELECT 'member_casual', COUNT(*) FROM trips WHERE member_casual = '';

-- 3. Invalid records

-- Negative duration
SELECT COUNT(*) AS number_of_trips_with_negative_duration 
FROM trips 
WHERE TIMEDIFF(ended_at, started_at) < 0;

-- Duration more than a day
SELECT COUNT(*) AS number_of_trips_with_long_duration 
FROM trips
WHERE TIME_TO_SEC(TIMEDIFF(ended_at, started_at)) > 24*60*60;

-- 4. Remove invalid records
DELETE FROM trips
WHERE start_station_name = '' OR
	end_station_name = '' OR
    end_lat = '' OR
    TIMEDIFF(ended_at, started_at) < 0 OR
    TIME_TO_SEC(TIMEDIFF(ended_at, started_at)) > 24*60*60;

SELECT COUNT(*) AS number_of_trips_after_removal FROM trips;

-- Step 2: Feature Engineering & Feature Selection

-- Calculate ride length
ALTER TABLE trips
MODIFY COLUMN ride_length INT;

UPDATE trips
SET ride_length = ROUND(TIME_TO_SEC(TIMEDIFF(ended_at, started_at)));

-- Extract day of the week
ALTER TABLE trips
ADD COLUMN day_of_week VARCHAR(15);

UPDATE trips
SET day_of_week = DATE_FORMAT(started_at, '%W');

-- Extract time of the day
ALTER TABLE trips
ADD COLUMN time_of_day VARCHAR(12);

UPDATE trips
SET time_of_day = 
    CASE 
        WHEN HOUR(started_at) >= 0 AND HOUR(started_at) <= 5 THEN 'Night'
        WHEN HOUR(started_at) >= 6 AND HOUR(started_at) <= 11 THEN 'Morning'
        WHEN HOUR(started_at) >= 12 AND HOUR(started_at) <= 17 THEN 'Afternoon'
        ELSE 'Evening'
    END;

-- Extract season
ALTER TABLE trips
ADD COLUMN season VARCHAR(8);

UPDATE trips
SET season = 
	CASE 
		WHEN MONTH(started_at) BETWEEN 3 AND 5 THEN 'Spring'
		WHEN MONTH(started_at) BETWEEN 6 AND 8 THEN 'Summer'
		WHEN MONTH(started_at) BETWEEN 9 AND 11 THEN 'Autumn'
		ELSE 'Winter'
	END;

-- Drop unnecessary columns
ALTER TABLE trips
DROP COLUMN start_station_id,
DROP COLUMN end_station_id;

-- Step 3: Data Analysis
-- average ride length by user type and day of the week
SELECT 
    CASE WHEN member_casual = 'member' THEN 'Annual Member' ELSE 'Casual Rider' END AS user_type,
    day_of_week,
    ROUND(AVG(ride_length) / 60, 2) AS avg_ride_length_minutes
FROM trips
GROUP BY user_type, day_of_week
ORDER BY avg_ride_length_minutes DESC;

-- the most popular day of the week (mode)
SELECT 
	CASE WHEN member_casual = 'member' THEN 'Annual Member' ELSE 'Casual Rider' END AS user_type,
    day_of_week,
    COUNT(*) AS number_of_trips
FROM trips
GROUP BY user_type, day_of_week
ORDER BY number_of_trips DESC;
    
-- different seasons
SELECT 
	CASE WHEN member_casual = 'member' THEN 'Annual Member' ELSE 'Casual Rider' END AS user_type,
	season,
	COUNT(*) AS number_of_trips
FROM trips
GROUP BY user_type, season
ORDER BY user_type, number_of_trips DESC;

-- different time periods
SELECT 
	CASE WHEN member_casual = 'member' THEN 'Annual Member' ELSE 'Casual Rider' END AS user_type,
	time_of_day,
	COUNT(*) AS number_of_trips
FROM trips
GROUP BY user_type, time_of_day
ORDER BY user_type, number_of_trips DESC;

-- top 10 start stations
SELECT start_station_name, COUNT(*) AS total_trips
FROM trips
GROUP BY start_station_name
ORDER BY total_trips DESC
LIMIT 10;

-- top 10 destinations
SELECT end_station_name, COUNT(*) AS total_trips
FROM trips
GROUP BY end_station_name
ORDER BY total_trips DESC
LIMIT 10;

-- Step 4: Export Data
-- Export cleaned and analyzed data to a new table / CSV file

-- Export to a new table
DROP TABLE IF EXISTS cleaned_trips;
CREATE TABLE cleaned_trips AS
SELECT *
FROM trips;

-- Export to a CSV file
SELECT *
INTO OUTFILE 'data/trips.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM cleaned_trips;
