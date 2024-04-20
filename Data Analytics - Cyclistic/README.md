# Cyclistic Project

## Overview

Cyclistic is a project aimed at analyzing bike share data to derive insights and trends that can be valuable for decision-making and strategic planning. This project utilizes SQL and Tableau for data analysis, reporting, and visualization to understand user behaviors, usage patterns, and preferences.

### Research Question
How do annual members and casual riders use Cyclistic bikes differently?

## Table of contents

- [Overview](#overview)
- [Dataset](#dataset)
  - [Download the data](#download-the-data)
  - [Database Creation](#database-creation)
- [Data Cleaning](#data-cleaning)
  - [Duplicates](#duplicates)
  - [Missing values](#missing-data)
  - [Invalid instances and outliers](#invalid-records)
  - [Removing invalid instances](#remove-invalid-records)
  - [Feature Engineering](#feature-engineering)
- [Analysis](#analysis)
- [Visualizations](#visualizations---tableau)
- [Results and Recommendations](#results-and-recommendations)
  - [Insights](#insights)
  - [Recommendations](#recommendations)

## Dataset
<!-- - Where is your data located?
- How is the data organized?
- Are there issues with bias or credibility in this data? Does your data ROCCC?
- How are you addressing licensing, privacy, security, and accessibility?
- How did you verify the data’s integrity?
- How does it help you answer your question?
- Are there any problems with the data? -->
### Download the data
The data is publicly available on [Divvy AWS S3 bucket](https://divvy-tripdata.s3.amazonaws.com/index.html). Although Cyclistic is a fictional company, the datasets are appropriate and comes from a real company called Lyft Bikes and Scooters, LLC. The dataset has been made available by Motivate International Inc. under this [license](https://divvybikes.com/data-license-agreement).


The dataset is available month by month. For the sake of data recency (freshness), we will be using the most recent 12 months' data for the project.

### Database Creation
The tables are created for each month's data using the following query:
```SQL
CREATE TABLE `trips_2023_03` (
  `ride_id` varchar(20) NOT NULL,
  `rideable_type` varchar(45) DEFAULT NULL,
  `started_at` datetime DEFAULT NULL,
  `ended_at` varchar(45) DEFAULT NULL,
  `start_station_name` varchar(100) DEFAULT NULL,
  `start_station_id` varchar(45) DEFAULT NULL,
  `end_station_name` varchar(100) DEFAULT NULL,
  `end_station_id` varchar(45) DEFAULT NULL,
  `start_lat` float DEFAULT NULL,
  `start_lng` float DEFAULT NULL,
  `end_lat` float DEFAULT NULL,
  `end_lng` float DEFAULT NULL,
  `member_casual` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ride_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

After importing the data for the past 12 months, the data is then aggregated 
into a single table called trips using the following query:

```SQL
CREATE TABLE IF NOT EXISTS trips AS ( 
	SELECT * FROM trips_2023_03
	UNION ALL 
	SELECT * FROM trips_2023_04
	UNION ALL 
	SELECT * FROM trips_2023_05
	UNION ALL 
	SELECT * FROM trips_2023_06
	UNION ALL 
	SELECT * FROM trips_2023_07
	UNION ALL 
	SELECT * FROM trips_2023_08
	UNION ALL 
	SELECT * FROM trips_2023_09
	UNION ALL 
	SELECT * FROM trips_2023_10
	UNION ALL 
	SELECT * FROM trips_2023_11
	UNION ALL 
	SELECT * FROM trips_2023_12
	UNION ALL 
	SELECT * FROM trips_2024_01
	UNION ALL 
	SELECT * FROM trips_2024_02
);
```

## Data cleaning
<!-- - What tools are you choosing and why?
- Have you ensured your data’s integrity?
- What steps have you taken to ensure that your data is clean?
- How can you verify that your data is clean and ready to analyze?
- Have you documented your cleaning process so you can review and share those
results?

- Check the data for errors.
- Choose your tools.
- Transform the data so you can work with it effectively.
- Document the cleaning process. -->

We use SQL because of its efficiency in handling vast amounts of data (few million instaces). SQL's optimized querying, indexing, and declarative nature make it ideal for these tasks, allowing us to filter, aggregate, and derive features with concise and expressive queries. Its built-in functions and aggregation capabilities further enhance its utility for summarizing data and performing advanced transformations directly within the database, minimizing data movement and improving scalability and performance in the overall data processing pipeline.

#### Duplicates
There are no duplicates in the dataset.
```SQL
SELECT COUNT(ride_id) AS duplicate_ride_count
FROM (
    SELECT ride_id, COUNT(*) AS count
    FROM trips
    GROUP BY ride_id
    HAVING count > 1
) AS duplicate_rides;
```
INSERT duplicate.png

#### Missing data
```SQL
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
```

INSERT missing values.png

### Invalid records

- Trips with negative duration (started before it ended)
```SQL
SELECT COUNT(*) AS number_of_trips_with_negative_duration 
FROM trips 
WHERE TIMEDIFF(ended_at, started_at) < 0;
```

- Trips with duration more than a day
```SQL
SELECT COUNT(*) AS number_of_trips_with_long_duration 
FROM trips
WHERE TIME_TO_SEC(TIMEDIFF(ended_at, started_at)) > 24*60*60;
```

### Remove invalid records
```SQL
DELETE FROM trips
WHERE start_station_name = '' OR
end_station_name = '' OR
end_lat = '' OR
TIMEDIFF(ended_at, started_at) < 0 OR
TIME_TO_SEC(TIMEDIFF(ended_at, started_at)) > 24*60*60;
```

```SQL
SELECT COUNT(*) AS number_of_trips_after_removal FROM trips;
```

After removing the outliers and invalid records, we need to extract some additional features to use in the dashboard that we develop later. The features to be extracted include, Ride Length (seconds), Season, Day of week, Time of day.

### Feature Engineering
Ride length:
```SQL
ALTER TABLE trips
ADD COLUMN ride_length TIME;

UPDATE trips
SET ride_length = ROUND(TIME_TO_SEC(TIMEDIFF(ended_at, started_at)));
```

Day of the week:
```SQL
ALTER TABLE trips
ADD COLUMN day_of_week VARCHAR(15);

UPDATE trips
SET day_of_week = DATE_FORMAT(started_at, '%W');
```

Time of the day:
```SQL
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
```

Season:
```SQL
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
```

Remove unnecessary columns:
```SQL
ALTER TABLE trips
DROP COLUMN start_station_id,
DROP COLUMN end_station_id;
```

## Analysis
<!-- - How should you organize your data to perform analysis on it?
- Has your data been properly formatted?
- What surprises did you discover in the data?
- What trends or relationships did you find in the data?
- How will these insights help answer your business questions?

- Aggregate your data so it’s useful and accessible.
- Organize and format your data.
- Perform calculations.
- Identify trends and relationships.

- Use SQL queries to find out the following results:
    - mean, max of ride_length
    - mode of day_of_week
    - different seasons -->

Since the dataset is now ready for analysis, we perform some queries before developing the dashboard to explore the dataset more. We need to find out the key information about the trips (total number of trips, average ride length, most popular stations, etc.).

#### Average Ride length by users and day of the week
```SQL
SELECT 
    CASE WHEN member_casual = 'member' THEN 'Annual Member' ELSE 'Casual Rider' END AS user_type,
    day_of_week,
    AVG(TIME_TO_SEC(ride_length)) / 60 AS avg_ride_length_minutes
FROM trips
GROUP BY user_type, day_of_week
ORDER BY user_type, FIELD(day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday');
```

#### Trip count by day
```SQL
SELECT 
  CASE WHEN member_casual = 'member' THEN 'Annual Member' ELSE 'Casual Rider' END AS user_type,
  day_of_week,
  COUNT(*) AS number_of_trips
FROM trips
GROUP BY user_type, day_of_week
ORDER BY number_of_trips DESC;
```

#### Trip count by season
```SQL
SELECT 
	CASE WHEN member_casual = 'member' THEN 'Annual Member' ELSE 'Casual Rider' END AS user_type,
	season,
	COUNT(*) AS number_of_trips
FROM trips
GROUP BY user_type, season
ORDER BY user_type, number_of_trips DESC;
```

#### Trip count by time of the day
```SQL
SELECT 
	CASE WHEN member_casual = 'member' THEN 'Annual Member' ELSE 'Casual Rider' END AS user_type,
	time_of_day,
	COUNT(*) AS number_of_trips
FROM trips
GROUP BY user_type, time_of_day
ORDER BY user_type, number_of_trips DESC;
```

#### Top 10 Start Stations by total number of trips
```SQL
SELECT start_station_name, COUNT(*) AS total_trips
FROM trips
GROUP BY start_station_name
ORDER BY total_trips DESC
LIMIT 10;
```

#### Top 10 End Stations by total number of trips
```SQL
SELECT end_station_name, COUNT(*) AS total_trips
FROM trips
GROUP BY end_station_name
ORDER BY total_trips DESC
LIMIT 10;
```
#### Save the table into CSV file

```SQL
CREATE TABLE cleaned_trips AS
SELECT *
FROM trips;

SELECT *
INTO OUTFILE 'data/trips.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM cleaned_trips;
```

## Visualizations - Tableau
<!-- - Were you able to answer the question of how annual members and casual riders use Cyclistic bikes
differently?
- What story does your data tell?
- How do your findings relate to your original question?
- Who is your audience? What is the best way to communicate with them?
- Can data visualization help you share your findings?
- Is your presentation accessible to your audience?
Key tasks
- Determine the best way to share your findings.
- Create effective data visualizations.
- Present your findings.
- Ensure your work is accessible.

- Create interactive dashboards on Tableau -->

We chose Tableau for data visualization primarily due to its robust capabilities in creating a diverse range of visualizations, from basic charts to complex dashboards, which effectively communicate insights from our data. Tableau's flexibility allows us to tailor visualizations to specific stakeholder needs, ensuring that the information presented is relevant and impactful. Additionally, its seamless integration with various data sources ensures that we can leverage our existing datasets efficiently, minimizing additional data preparation efforts. Furthermore, Tableau's sharing and collaboration features facilitate easy dissemination of insights across stakeholders, fostering data-driven decision-making and enhancing overall transparency and alignment within the organization.

INSERT Tableau link here

## Results and Recommendations
<!-- - What is your final conclusion based on your analysis?
- How could your team and business apply your insights?
- What next steps would you or your stakeholders take based on your findings?
- Is there additional data you could use to expand on your findings? -->

### Insights
1. A significant portion of trips, 64.8%, are attributed to members, with casual users making up the remaining 35.2%.
2. Among members, traditional bikes account for two-thirds of trips, while electric bikes make up the remaining third.
3. Summer emerges as the favored season for both member and casual riders.
4. August sees the highest activity for members, whereas casual riders peak in July.
5. Casual riders tend to embark on lengthier bike rides compared to members.
6. The preferred start and end point for members is Streeter Dr & Grand Ave, boasting approximately 60,000 trips over the past year.
7. Weekdays witness a surge in trips during rush hours, namely 7-9 am and 4-6 pm.
8. Trips on weekends are more evenly distributed throughout the day.
9. Both casual riders and members opt for longer bike rides on weekends.

### Recommendations
1. **Summer Membership Promotion**: We recommend launching a summer membership promotion aimed at casual riders. This initiative will highlight the advantages of membership during the peak biking season, with a focus on the convenience and cost-effectiveness it offers. Special emphasis will be placed on access to electric bikes and priority station availability to encourage conversion.
2. **Weekend Exclusive Offers**: Develop targeted promotional offers specifically for weekends to attract casual riders to become members. These offers will leverage the observed trend of increased biking activity during weekends, offering discounts or incentives tailored to this time frame. By tapping into this behavior, we aim to drive membership sign-ups among casual riders.
3. **Weekday Engagement Strategy**: Implement weekday engagement campaigns to promote membership benefits during rush hours. These campaigns will highlight advantages such as quicker access to bikes and priority parking at popular stations, particularly appealing to commuters. Utilizing data-driven insights, we will identify key commuter routes and tailor our messaging accordingly to encourage weekday membership conversions.
4. **Community Building Initiatives**: Launch community events and group rides tailored to casual riders, showcasing the benefits of membership while fostering a sense of community within the biking enthusiast circles. Collaborating with local organizations or businesses, these initiatives will offer exclusive membership incentives to event participants, driving interest and conversion.
