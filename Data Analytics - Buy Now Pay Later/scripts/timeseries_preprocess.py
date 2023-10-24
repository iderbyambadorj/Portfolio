"""
Time Series preprocessing script

This script performs preprocessing steps necessary to run the time series model.

Outline:
    1. Perform feature selection
    2. Impute the first two months data using the average of the next 
        three months
    3. Aggregate the data by their months and return three dataframes
        for each target variables: revenue, consumer count and transactions 
        count.
"""


from pyspark.sql import SparkSession, functions as F
from pyspark.sql.functions import *

spark = (
    SparkSession.builder.appName("MAST30034 Project 2")
    .config("spark.sql.repl.eagerEval.enabled", True)
    .config("spark.sql.parquet.cacheMetadata", "true")
    .config("spark.sql.session.timeZone", "Etc/UTC")
    .config('spark.driver.memory', '4g')
    .config('spark.executor.memory', '2g')
    .getOrCreate()
)

# Read the transactions data
transactions = spark.read.parquet('../data/raw/transactions_clean')
transactions = transactions. \
    withColumn("date_ym", date_format("order_datetime", "yyyyMM"))

# Remove unnecessary columns for time series model
time_series_data = transactions \
    .drop('order_id', 'state', 'postcode', 'gender', 'population', 'popDensity2022', 
          'num_earners', 'medianAge_earners', 'sum_income', 'median_income',
          'consumer_fraud_prob', 'merchant_name', 'merchant_desc', 
          'merchant_revenue_lvl', 'merchant_fraud_prob', 'year', 'month')

# Find the total consumer count for each merchant for each month
user_count_ts = time_series_data \
    .groupBy("merchant_abn") \
    .pivot("date_ym") \
    .agg(countDistinct("consumer_id")).fillna(0)

# Impute the first two months data using the average of the next three months
user_count_ts = user_count_ts \
    .withColumn("202211", F.col("202111")) \
    .withColumn("202212", F.col("202112")) \
    .drop("202102") \
    .withColumn('202101', (F.col('202103') + F.col('202104') + F.col('202105')) / 3)\
    .withColumn('202102', (F.col('202103') + F.col('202104') + F.col('202105')) / 3) \
    .select(["merchant_abn"] + sorted(user_count_ts.columns[1:]))

# Find the total revenue for each merchant for each month
monthly_rev_ts = time_series_data \
    .groupBy("merchant_abn") \
    .pivot("date_ym") \
    .agg(F.sum("dollar_value")).fillna(0)

# Impute the first two months data using the average of the next three months
monthly_rev_ts = monthly_rev_ts \
    .withColumn("202211", F.col("202111")) \
    .withColumn("202212", F.col("202112")) \
    .drop("202102") \
    .withColumn('202101', (F.col('202103') + F.col('202104') + F.col('202105')) / 3) \
    .withColumn('202102', (F.col('202103') + F.col('202104') + F.col('202105')) / 3) \
    .select(["merchant_abn"] + sorted(monthly_rev_ts.columns[1:]))

# Find the total number of transactions made for each merchant for each month
tx_count_ts = time_series_data \
    .groupBy("merchant_abn") \
    .pivot("date_ym") \
    .agg(count("consumer_id")).fillna(0)

# Impute the first two months data using the average of the next three months
tx_count_ts = tx_count_ts \
    .withColumn("202211", F.col("202111")) \
    .withColumn("202212", F.col("202112")) \
    .drop("202102") \
    .withColumn('202101', (F.col('202103') + F.col('202104') + F.col('202105')) / 3)\
    .withColumn('202102', (F.col('202103') + F.col('202104') + F.col('202105')) / 3) \
    .select(["merchant_abn"] + sorted(tx_count_ts.columns[1:]))

# Output the data
user_count_ts.toPandas().to_csv("../data/curated/user_count_ts.csv", index=False)
monthly_rev_ts.toPandas().to_csv("../data/curated/monthly_rev_ts.csv", index=False)
tx_count_ts.toPandas().to_csv("../data/curated/trans_count_ts.csv", index=False)