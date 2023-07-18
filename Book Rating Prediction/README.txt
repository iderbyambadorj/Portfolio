============== Code : Instruction on how to run the code ==============

This README.txt file contains instructions on how to run the .ipynb code
submitted to the assignment.

IMPORTANT: Before running the code, make sure all data is in the same folder
as the .ipynb file. This includes book_rating_train.csv, book_rating_test.csv,
train_authors_doc2vec20.csv, train_name_doc2vec100.csv, train_desc_doc2vec100.csv,
test_authors_doc2vec20.csv, test_name_doc2vec100.csv and test_desc_doc2vec100.csv.


The code consists of two main parts: Train and Test phase.

1. Train phase:
    a. Read the training data
    b. Preprocess the training data
    c. Train the models
        i. (optional) optimize the hyperparameters
        ii. Perform cross-validation and evaluate the models
2. Test phase:
    a. Read the test data
    b. Preprocess the test data
    c. Test the model on test data and get the output

To run the code, run all cells in a sequential order. (Run-All)

Optional part of the code, optimization of the hyperparameters, has been 
commented out. This part takes a long time to run and is optional. 
If you want to run this part of the code, uncomment the optimization part.

