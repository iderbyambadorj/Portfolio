### Script for downloading the TLC data

from urllib.request import urlretrieve
import os

URL_TEMPLATE = "https://d37ci6vzurychx.cloudfront.net/trip-data/"#_tripdata_year-month.parquet
URL_SUFFIX = '_tripdata_'

def download_data(timelines, trip_type, output_relative_dir):
    """
    This function downloads all TLC trip record data into data/landing folder.
    """
    for timeline in timelines:
        print(f"Begin {timeline} of {trip_type}")
        
        # generate url
        url = f'{URL_TEMPLATE}{trip_type}{URL_SUFFIX}{timeline}.parquet'
        # generate output location and filename
        output_dir = f"{output_relative_dir}/{trip_type}-{timeline}.parquet"
        # download
        urlretrieve(url, output_dir) 
        
        print(f"Completed {timeline} of {trip_type}")

if __name__ == "__main__":
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    output_relative_dir = '../data/landing/'
    trip_type = 'yellow'

    if not os.path.exists(output_relative_dir):
        os.makedirs(output_relative_dir)

    # Generate timelines
    YEAR = '2022'
    MONTHS = range(1, 13)
    timelines = []
    for month in MONTHS:
        timelines.append(YEAR + "-" + str(month).zfill(2))
    YEAR = '2023'
    MONTHS = range(1, 6)
    for month in MONTHS:
        timelines.append(YEAR + "-" + str(month).zfill(2))
    

    download_data(timelines, trip_type, output_relative_dir)