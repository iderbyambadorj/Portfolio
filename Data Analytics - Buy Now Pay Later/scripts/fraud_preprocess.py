"""
Fraud Model preprocessing script

This script performs preprocessing steps necessary to run the fraud 
classifier models.

Outline:
    1. Perform analysis on the missing data and distinct values for each feature
    2. Perform filtering
    3. Perform feature selection
    4. Output the data
"""


from pyspark.sql import SparkSession
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

data = spark.read.parquet('../data/raw/transactions_clean')

filtered = data.filter(~(col("consumer_fraud_prob").isNull() & col("merchant_fraud_prob").isNull()))
# Distinct values
print('Distinct values in the dataframe')
filtered.agg(*(countDistinct(col(c)).alias(c) for c in filtered.columns)).show()
# Missing values
print('Missing values in the dataframe')
filtered.select([count(when(col(c).isNull(), c)).alias(c) for c in filtered.columns]).show()

# Remove unnecessary columns
df = filtered.drop('merchant_abn', 'order_id', 'consumer_id', 'merchant_name', 
                           'merchant_desc', 'SA2_NAME21', 'sum_income', 'SA2_CODE21', 'postcode')
df.coalesce(1) \
    .write \
    .mode('overwrite') \
    .parquet("../data/raw/fraud_data")