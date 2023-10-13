from urllib.request import urlretrieve
import os
import zipfile



URL = 'https://d37ci6vzurychx.cloudfront.net/misc/taxi_zones.zip'


def download_data(output_relative_dir):
    print("Begin download")
    
    # generate output location and filename
    output_dir = f"{output_relative_dir}"
    # download
    file_dir = output_relative_dir + 'taxi_zones.zip'
    urlretrieve(URL, output_dir) 
    
    zip_ref = zipfile.ZipFile(
        file_dir, "r"
    )
    zip_ref.extractall(
        file_dir
    )
    zip_ref.close()
    

if __name__ == "__main__":
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    output_relative_dir = '../data/landing/'

    if not os.path.exists(output_relative_dir):
        os.makedirs(output_relative_dir)

    download_data(output_relative_dir)