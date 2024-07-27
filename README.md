# Portfolio

Welcome to my data scientist portfolio! This repository showcases a collection of projects and analyses I have worked on to demonstrate my skills and experience in deep learning, data analysis and data engineering. Each project comes with a brief description, dataset source, tools used, and insights gained.

## Projects

### 🚗 [Traffic Network Speed Prediction](https://github.com/iderbyambadorj/Portfolio/tree/main/Deep%20Learning%20-%20U-Net%20Model)
- **Description**: Implemented a U-Net network from scratch using tensorflow and keras.
- **Dataset**: The dataset consists of multiple images showing traffic speed at various time intervals, and the goal is to predict future traffic speed maps.
- **Tools Used**: Python, Tensorflow, Keras, OpenCV
- **Insights Gained**: The model is trained using Binary Cross Entropy loss with masked pixels, meaning that only the relevant pixels (non-zero) contribute to the loss calculation. The model was trained on AWS Sagemaker using ml.m7i.2xlarge instance. The dataset was first stored on S3 bucket then downloaded using boto3 function included in the notebook. The model achieved a validation accuracy of 0.96 and a validation loss of 0.012.

### 🕵️ [Person Re-Identification - Siamese Network](https://github.com/iderbyambadorj/Portfolio/tree/main/Deep%20Learning%20-%20Siamese%20Network)
- **Description**: Trained a Siamese network for person re-identification purposes
- **Dataset Source**: [Market-1501 Dataset](https://www.kaggle.com/pengcw1/market-1501)
- **Tools Used**: Python, pandas, PyTorch
- **Insights Gained**: By leveraging a ResNet-based architecture and optimizing with triplet margin loss, we achieved accurate separation of anchor, positive, and negative image pairs. Data augmentation and deeper network architectures significantly enhanced the model's performance. The use of Euclidean distance effectively identified similar images, demonstrating the practical application of our approach. This project not only showcased the power of deep learning in person re-identification but also provided valuable insights into the impact of model design and optimization techniques.

### 🚴 [Cyclistic](https://github.com/iderbyambadorj/Portfolio/tree/main/Data%20Analytics%20-%20Cyclistic)
- **Description**: Analyzed customer data to understand their behaviour and perform market analysis.
- **Dataset Source**: [Divvy Bicycle Sharing Service data](https://divvy-tripdata.s3.amazonaws.com/index.html)
- **Tools Used**: SQL, Tableau
- **Insights Gained**: Identified key differences between casual and member customers for a bike-sharing service company located in Chicago. 

### 📊 [Buy Now Pay Later](https://github.com/iderbyambadorj/Portfolio/tree/main/Data%20Analytics%20-%20Buy%20Now%20Pay%20Later)
- **Description**: Developed a ranking system to help drive data-driven decisions.
- **Dataset Source**: Internal sales data
- **Tools Used**: Python, pandas, PySpark, scikit-learn, R, ARIMA
- **Insights Gained**: Achieved 99% accuracy in predicting performance measures for each merchant. Recreational and housing segments were the best-performing sectors among merchants. Under our solution, the predicted performance for top 100 merchants are as follows: >$23m in total gain (profit), >24000 customers, >3.7m transactions.

### 🚕 [Predicting Yellow Taxi Demand with Public Transport in New York City: Urban Mobility Insights](https://github.com/iderbyambadorj/Portfolio/tree/main/Data%20Science%20-%20New%20York%20Taxi%20Service)

- **Description**: Analyzed a spatial and temporal relationship between public transport and yellow taxi demands in New York.
- **Dataset Source**: [TLC Trip Record Data](https://www.nyc.gov/site/tlc/about/tlc-trip-record-data.page), [Metropolitan Transportation Authority Data](http://web.mta.info/developers/developer-data-terms.html##data)
- **Tools Used**: Python, pandas, PySpark, matplotlib, seaborn, folium, R, scikit-learn
- **Insights Gained**: According to the result, public transport data, combined with temporal data, can be used to predict yellow taxi demand with high performance. The predictive power of the models not only provided accurate estimations of taxi demand but also highlighted the influence of public transportation services on commuting patterns. This study’s findings can help public transport planners and taxi companies to better allocate their resources.

### Other Projects:
- **[Music Genre](https://github.com/iderbyambadorj/Portfolio/tree/main/Classification%20-%20Music%20Genre)**: ML Classification project implementing Naive Bayes algorithm to predict the music genre of a song. (Data Wrangling, Scikit-learn, Python)
- **[Book Rating Prediction](https://github.com/iderbyambadorj/Portfolio/tree/main/Book%20Rating%20Prediction)**: ML Classification project implementing kNN to predict book ratings. (Data Wrangling, Scikit-learn, Python)
- **[League of Legends](https://github.com/iderbyambadorj/Portfolio/tree/main/Classification%20-%20LoL%20Player%20Roles)**: ML Classification project working on League of Legends players' dataset. Predicts player roles with --- accuracy. (Collaboration, Python)
- **FlowFree**: An AI algorithm that solves the game "Flowfree". ()
- **[ShadowPirate](https://github.com/iderbyambadorj/Portfolio/tree/main/2D%20game%20-%20ShadowPirate)**: 2D role-playing game. (Object-Oriented Software Development, Java)
- **[Checkers](https://github.com/iderbyambadorj/Portfolio/tree/main/Algorithms%20-%20Checkers%20AI)**: An implementation of minmax algorithm on Checkers in C. (Algorithms, C)
- **[Sudoku](https://github.com/iderbyambadorj/Portfolio/tree/main/Algorithms%20-%20Sudoku)**: An implementation of Sudoku solver in C. (Algorithms, C)
- **[NBA Player Stats Explorer](https://github.com/iderbyambadorj/Portfolio/tree/main/NBA%20Player%20Stats%20Explorer)**: EDA project on NBA Player stats. (Web scraping, Data Visualization, Streamlit)



## 👨‍🎓 About Me

I am a passionate data analyst with a strong background in statistics and programming. I specialize in extracting meaningful insights from data to drive business decisions and improve processes. My expertise includes data manipulation, visualization, statistical analysis, and machine learning. 

## 📞 Contact

- **Email**: [ider.byambadorj@gmail.com](mailto:ider.byambadorj@gmail.com)
- **LinkedIn**: [LinkedIn Profile](https://www.linkedin.com/in/iderbyambadorj)
- **GitHub**: [Github Profile](https://github.com/iderbyambadorj)
- **Tableau**: [Tableau Public Profile](https://public.tableau.com/app/profile/ider.byambadorj/vizzes)

Feel free to explore the projects and reach out to me for any inquiries or collaborations!
