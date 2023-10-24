"""
Linear Model preprocessing script

This script performs preprocessing steps necessary to run the linear model.

Outline:
    1. Impute the first two months data using the average of the next 
        three months
    2. Impute the last two months data using the forecasted values from
        the time series model (ARIMA).
    3. Aggregate all the data and separate them into train and test data.
"""

from pyspark.sql import SparkSession, functions as F
from pyspark.sql.functions import *
from pyspark.sql.types import IntegerType, DoubleType, LongType

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

# Aggregate the monthly data for each individual merchant
merged_monthly_data = transactions \
    .groupby('merchant_abn', 'merchant_desc', 'merchant_revenue_lvl', 
             'merchant_take_rate', 'month', 'year') \
    .agg(
        F.countDistinct('consumer_id').alias('total_num_consumer'),
        F.countDistinct('order_id').alias('total_num_trans'),
        F.sum('dollar_value').alias('total_revenue'),
        F.mean('population').alias('zone_mean_population'),
        F.mean('num_earners').alias('zone_avg_num_earners'),
        F.mean('medianAge_earners').alias('zone_avg_age'),
        F.median('sum_income').alias('zone_avg_total_income'),
        F.mean('median_income').alias('zone_avg_income'),
        F.countDistinct('postcode').alias('total_num_pos')
    ).withColumnRenamed('merchant_desc', 'category') \
    .withColumn('total_revenue', round(col('total_revenue'), 2)) \
    .withColumn('zone_mean_population', round(col('zone_mean_population'), 0)) \
    .withColumn('zone_avg_num_earners', round(col('zone_avg_num_earners'), 0)) \
    .withColumn('zone_avg_age', round(col('zone_avg_age'), 2)) \
    .withColumn('zone_avg_income', round(col('zone_avg_income'), 2))


# Months that we use to impute 2021 Jan, Feb data
to_impute_2021 = merged_monthly_data.filter((col('year')==2021) & (col('month').between(3, 5)))


# We impute features by calculating the average for all features
imputed_2021 = to_impute_2021 \
    .groupby('merchant_abn', 'category', 'merchant_revenue_lvl', 
             'merchant_take_rate') \
    .agg(
        F.mean('total_num_consumer').alias('total_num_consumer'),
        F.mean('total_num_trans').alias('total_num_trans'),
        F.mean('total_revenue').alias('total_revenue'),
        F.mean('zone_mean_population').alias('zone_mean_population'),
        F.mean('zone_avg_num_earners').alias('zone_avg_num_earners'),
        F.mean('zone_avg_age').alias('zone_avg_age'),
        F.mean('zone_avg_total_income').alias('zone_avg_total_income'),
        F.mean('zone_avg_income').alias('zone_avg_income'),
        F.mean('total_num_pos').alias('total_num_pos')
    ).withColumn('total_num_consumer', col('total_num_consumer').cast(IntegerType())) \
    .withColumn('total_num_trans', col('total_num_trans').cast(IntegerType())) \
    .withColumn('total_revenue', round(col('total_revenue'), 2)) \
    .withColumn('zone_mean_population', col('zone_mean_population').cast(IntegerType())) \
    .withColumn('zone_avg_num_earners', col('zone_avg_num_earners').cast(IntegerType())) \
    .withColumn('zone_avg_age', round(col('zone_avg_age'), 2)) \
    .withColumn('zone_avg_total_income', round(col('zone_avg_total_income'), 2)) \
    .withColumn('zone_avg_income', round(col('zone_avg_income'), 2)) \
    .withColumn('total_num_pos', col('total_num_pos').cast(IntegerType()))


# Imputed months data
imputed_2021_jan = imputed_2021 \
    .withColumn('year', lit(2021)) \
    .withColumn('month', lit(1)) \
    .withColumn('total_num_consumer', col('total_num_consumer'))
imputed_2021_feb = imputed_2021 \
    .withColumn('year', lit(2021)) \
    .withColumn('month', lit(2))

# Ensure that the dataset merges correctly (in the correct order of collumns)
column_order = merged_monthly_data.columns
imputed_2021_jan = imputed_2021_jan.select(*column_order)
imputed_2021_feb = imputed_2021_feb.select(*column_order)

# Join the imputed data into original dataset
merged_monthly_data = merged_monthly_data \
    .union(imputed_2021_jan) \
    .union(imputed_2021_feb)

# We use time series modelling to impute these values.
imputed_2022 = spark.read.option("header",True).csv('../data/curated/time_series_forecast.csv')

# data types conversions
imputed_2022 = imputed_2022 \
    .withColumn('merchant_abn', col('merchant_abn').cast(LongType())) \
    .withColumn('month', col('month').cast(IntegerType())) \
    .withColumn('year', col('year').cast(IntegerType())) \
    .withColumn('total_revenue', col('total_revenue').cast(DoubleType())) \
    .withColumn('total_num_consumer', col('total_num_consumer').cast(LongType())) \
    .withColumn('total_num_trans', col('total_num_trans').cast(LongType()))

# Use the previous year's data to impute the external dataset
holiday_2021 = merged_monthly_data.filter((col('year') == 2021) & (col('month').between(11, 12)))

# Join the external dataset into the predicted data from the time series model
holiday_2022 = holiday_2021 \
    .drop('total_num_consumer', 'total_num_trans', 'total_revenue', 'year') \
    .join(imputed_2022, ['merchant_abn', 'month'], how='left') \
    .withColumn('year', lit(2022))

# Ensure correct order of columns and join the dataset
holiday_2022 = holiday_2022.select(*column_order)

merged_monthly_data = merged_monthly_data.union(holiday_2022)

# Aggregate the yearly data for each merchant
agg_data = merged_monthly_data \
    .groupby('merchant_abn', 'category', 'merchant_revenue_lvl', 
             'merchant_take_rate', 'year') \
    .agg(
        F.sum('total_num_consumer').alias('total_num_consumer'),
        F.sum('total_num_trans').alias('total_num_trans'),
        F.sum('total_revenue').alias('total_revenue'),
        F.median('zone_mean_population').alias('zone_mean_population'),
        F.median('zone_avg_num_earners').alias('zone_avg_num_earners'),
        F.median('zone_avg_age').alias('zone_avg_age'),
        F.median('zone_avg_total_income').alias('zone_avg_total_income'),
        F.median('zone_avg_income').alias('zone_avg_income'),
        F.mean('total_num_pos').alias('avg_monthly_post')
    )

# We train on 2021 data and use 2022 data to predict 2023 data.
train = agg_data.filter(col('year') == 2021)
test = agg_data.filter(col('year') == 2022)
label_data = test.select('merchant_abn', 'total_num_consumer', 'total_num_trans', 'total_revenue')


label_data = label_data \
    .withColumnRenamed('total_revenue', 'next_total_revenue') \
    .withColumnRenamed('total_num_trans', 'next_total_num_trans') \
    .withColumnRenamed('total_num_consumer', 'next_total_num_consumer')
train_data = train.join(label_data, ['merchant_abn'], how='left')

# Output the data
train_data.write.mode('overwrite').parquet("../data/curated/train_data")
train_data.toPandas().to_csv('../data/curated/lm_train.csv')
test.write.mode('overwrite').parquet("../data/curated/test_data")
test.toPandas().to_csv('../data/curated/lm_test.csv')