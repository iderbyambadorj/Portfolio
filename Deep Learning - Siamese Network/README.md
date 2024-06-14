# Siamese Network for Person Re-Identification

## Table of Contents

- [Project Overview](#project-overview)
- [Table of Contents](#table-of-contents)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Running Code](#running-code)
- [Result](#result)

## Project Overview

This project implements a Siamese network using PyTorch for person re-identification. The network is designed to distinguish between images of different individuals by learning useful feature representations. It utilizes a triplet loss function to optimize the model for distinguishing between anchor, positive, and negative image pairs.

- *Dataset Preparation*: The project includes steps to download, prepare, and visualize the dataset.
- *Model Architecture*: A ResNet-based Siamese network architecture is employed to learn image embeddings.
- *Training and Evaluation*: The model is trained using a triplet margin loss function and evaluated on validation data.
- *Image Encoding*: The trained model generates encodings for the images, which can be used for comparison.
- *Similarity Calculation*: A custom function calculates the Euclidean distance between image encodings to identify similar images.

## Getting Started
### Prerequisites

All prerequisities needed to train the model are outlined in the requirements.txt file.

### Running Code

Run the notebook to train the model and generate image encodings.
Use the provided functions to calculate similarity between images and visualize the results.

## Result

The project provides a method to re-identify persons by comparing their image encodings, demonstrating the effectiveness of Siamese networks in distinguishing between individuals.

![Result](https://github.com/iderbyambadorj/Portfolio/blob/main/Deep%20Learning%20-%20Siamese%20Network/output.png)