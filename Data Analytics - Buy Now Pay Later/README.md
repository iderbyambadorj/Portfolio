# Generic Buy Now, Pay Later Project

## Table of Contents

- [Project Overview](#project-overview)
- [Authors](#authors)
- [Table of Contents](#table-of-contents)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Running Code](#running-code)
- [Project Structure](#project-structure)
- [License](#license)

## Project Overview

The Buy Now Pay Later (BNPL) Firm has begun offering a new “Pay in 5 Installments” feature and is going to onboard 100 merchants every year.

This project focuses on these tasks:
1. Assess the given and external datasets
2. Construct models to predict merchant’s features
3. Find best 100 merchants to trade with

## Authors

- Ider Byambadorj
- Keigo Warashina
- Tugsgerel Lkhagvasuren
- Beibei Zhu
- Zihan Xu

## Getting Started
### Prerequisites

All prerequisities needed to run the pipeline are outlined in the requirements.txt file.

### Running Code

To run the pipeline, please visit the directory for each file and run the files in order:

1. **scripts/ETL.py**: Extract, Transform, Load. This script joins all the tables
and returns a dataframe called transactions in data/raw/transactions_data path.

2. **notebooks/external_dataset.ipynb**: This script performs web scraping and 
downloads the SA2 demographics and income datasets.

3. **notebooks/preprocess.ipynb**: This notebook performs all preprocessing steps in 
transactions dataset and returns a clean dataset joined with external datasets.

4. **notebooks/preliminary_analysis.ipynb**: This notebook performs geospatial and
demographic analysis and visualizes the results.

5. **scripts/timeseries_preprocess.py**: This script aggregates the data and 
prepares it for the ARIMA, time series forecasting model.

6. **models/time_series_forecast.Rmd**: This notebook uses the time series model to 
impute the missing data. Before running set working directory as specified in 
the file.

7. **scripts/lm_preprocess.py**: This script aggregates and prepares the data for 
the linear model.

8. **models/linear_model.ipynb**: This notebook runs the linear regression model.

9. **notebooks/LM_analysis.Rmd**: This notebook evaluates the performance of the 
linear regression model. Before running the notebook, set the working directory
to the file location.

10. **scripts/fraud_preprocess.py**: This script prepares the data for fraud 
classification model.

11. **models/fraud_classification.ipynb**: This notebook trains Random Forest 
classifier model to predict the reliability scores for the merchants.

12. **notebooks/ranking_system.ipynb**: This notebook runs the weighted-sum 
ranking system.

13. **notebooks/summary.ipynb**: The summary notebook that outlines the key findings
of the project.

## License

This academic group data science project is not open for external use or distribution.

All rights to this project and its code are reserved solely for educational and assessment purposes within the academic context. The code, data, and any associated materials are not to be shared, distributed, or used for any other purpose without explicit permission from the project authors and the academic institution involved.

Note: Do not delete any of the data provided in the data/tables directory.