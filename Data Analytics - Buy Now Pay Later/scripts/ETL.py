"""
ETL script

This script extracts and merges the data from the tables and returns a single
dataframe called transactions.

Outline:
    1. Read and merge the transactions dataset.
    2. Read and transform consumer and consumer_fraud datasets. Join this with 
        transactions dataset.
    3. Read and transform merchant and merchant_fraud datasets. Join this with 
        transactions dataset.
    4. Extract the category field from the merchant tags (merchant_desc).
    5. Output the merged dataset into raw folder.
"""

import pandas as pd
from pyspark.sql import SparkSession, functions as F
import re
from pyspark.sql.types import *
from pyspark.sql.functions import *

# Create a spark session (which will run spark jobs)
spark = (
    SparkSession.builder.appName("MAST30034 Project 2")
    .config("spark.sql.repl.eagerEval.enabled", True)
    .config("spark.sql.parquet.cacheMetadata", "true")
    .config("spark.sql.session.timeZone", "Etc/UTC")
    .config('spark.driver.memory', '4g')
    .config('spark.executor.memory', '2g')
    .getOrCreate()
)

# Helper functions
def extract_tags(input_string):
    """
    This function takes the tags input as a string and returns the three
    tags stored in it.
    """
    
    # There are some missing values in the tags field
    # Need to fix it later
    if type(input_string) != str:
        return ['', '', '']
    
    # Define the regular expression pattern
    pattern = r'[(\[].*?[)\]]'

    # Find all matches of the pattern in the input string
    matches = re.findall(pattern, input_string)

    # Extract tags from matches
    tags = []
    for match in matches:
        tag = match.strip('([)]')
        tags.append(tag)

    return tags

def extract_take_rate(tag):
    """
    This function extracts the take rate information from a string and converts
    it into float.
    """
    # Use regular expression to extract the number after 'take rate:'
    take_rate_pattern = r'take rate:\s*([\d.]+)'
    match = re.search(take_rate_pattern, tag)
    if match:
        return float(match.group(1))
    else:
        return None

# Following three functions are used to extract tags information
# from tags field in merchant dataset
extract_desc_UDF = F.udf(lambda x:extract_tags(x)[0].lower(), StringType())
extract_rev_lvl_UDF = F.udf(lambda x:extract_tags(x)[1].lower(), StringType())
extract_take_rate_UDF = F.udf(lambda x:extract_take_rate(extract_tags(x)[2]), FloatType())

# Read Transactions data
trans_1 = spark.read.parquet('../data/tables/transactions_20210228_20210827_snapshot/')
trans_2 = spark.read.parquet('../data/tables/transactions_20210828_20220227_snapshot/')
trans_3 = spark.read.parquet('../data/tables/transactions_20220228_20220828_snapshot/')

transactions = trans_1.union(trans_2).union(trans_3)

# Join the fraud dataset and consumer dataset
consumer_details = spark.read.format("csv") \
                .options(header=True, delimiter="|") \
                .load('../data/tables/tbl_consumer.csv') \
                .withColumnRenamed("name", "consumer_name")

# modified: use consumer dataset combined with external dataset
# consumer_details = spark.read.format("csv") \
#                 .options(header=True, delimiter=",") \
#                 .load('../data/raw/addedInfo_transaction/tbl_consumer_demo_income.csv') \
#                 .withColumnRenamed("name", "consumer_name")

# Read consumer fraud dataset
consumer_fraud = spark.read.format("csv") \
                .options(header=True, delimiter=",") \
                .load('../data/tables/consumer_fraud_probability.csv') \
                .withColumnRenamed('fraud_probability', 'consumer_fraud_prob')
# Remove duplicates
consumer_fraud = consumer_fraud.groupBy("user_id", "order_datetime").agg(max("consumer_fraud_prob").alias("consumer_fraud_prob"))

# Prepare the consumer dataset
consumer_user = spark.read.parquet("../data/tables/consumer_user_details.parquet")
consumer_details = consumer_details.join(consumer_user, "consumer_id", "left")

# Merge the transactions and consumer datasets
merged_data = transactions \
    .join(consumer_details, on='user_id', how='left') \
    .join(consumer_fraud, on=['user_id', 'order_datetime'], how='left')


# Read Merchant data
merchant_details = spark.read.parquet('../data/tables/tbl_merchants.parquet') \
                    .withColumnRenamed("name", "merchant_name")

# Extract tags information from the tags field
merchant_details = merchant_details.withColumn("merchant_desc", extract_desc_UDF(F.col('tags'))) \
                .withColumn("merchant_revenue_lvl", extract_rev_lvl_UDF(F.col('tags'))) \
                .withColumn("merchant_take_rate", extract_take_rate_UDF(F.col('tags')))


merchant_fraud = spark.read.format("csv") \
                .options(header=True, delimiter=",") \
                .load('../data/tables/merchant_fraud_probability.csv') \
                .withColumnRenamed("fraud_probability", "merchant_fraud_prob")
merchant_fraud = merchant_fraud.groupBy("merchant_abn", "order_datetime").agg(max("merchant_fraud_prob").alias("merchant_fraud_prob"))


# Extract merchant description into categories
merchant_details = merchant_details.withColumn("merchant_desc", split(col("merchant_desc"), ","))
merchant_details = merchant_details.withColumn("merchant_desc", col("merchant_desc")[0])
merchant_details = merchant_details.withColumn("merchant_desc", split(col("merchant_desc"), " "))
merchant_details = merchant_details.withColumn("merchant_desc", col("merchant_desc")[0])
merchant_details = merchant_details.withColumn(
    "merchant_desc",
    when(col("merchant_desc") == "computers", "computer")
    .when(col("merchant_desc") == "artist", "artist supply")
    .when(col("merchant_desc") == "art", "art")
    .when(col("merchant_desc") == "digital", "digital")
    .when(col("merchant_desc") == "lawn", "garden")
    .otherwise(col("merchant_desc"))
)
merchant_details.drop('tags')

# Merge the transactions data and merchant datasets
merged_data = merged_data \
    .join(merchant_details, on='merchant_abn', how='outer') \
    .join(merchant_fraud, on=['merchant_abn', 'order_datetime'], how='left')


merged_data \
    .repartition(4) \
    .write \
    .mode('overwrite') \
    .parquet("../data/raw/transactions_data")