# New York Taxi Services Project README.md
- Name: `IDER Byambadorj`

**Research Goal:** My research goal is to predict yellow taxi demand using public transport data.

**Timeline:** The timeline for the research area is 2022 - 2023.

To run the pipeline, please visit the `scripts` directory and run the files in order:
1. `download.py`: This downloads the raw data into the `data/landing` directory.
2. `public_transport.py`: This downloads the public transport data into the `data/landing` directory.
3. `public_transit_stops.ipynb`: This cleans and aggregates the public transport data.
4. `preprocessing.ipynb`: This notebook details all preprocessing steps and outputs it to the `data/raw` and `data/curated` directory.
5. `visualization.ipynb`: This notebook is used to generate all the plots used for analysis.
6. `modelling.ipynb`: This notebook performs the machine learning modelling and is used for analysing and discussing the model.
7. `linear_regression.Rmd`: This R-markdown performs the linear regression modelling.


Note: taxi_zones data is used as part of the preprocessing step. This data has been uploaded to data/landing directory. Do not delete this.