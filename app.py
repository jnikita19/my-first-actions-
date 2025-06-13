import boto3
import requests
from flask import Flask

def add(a, b):
    return a + b

if __name__ == "__main__":
    print("The sum is:", add(10, 5))
