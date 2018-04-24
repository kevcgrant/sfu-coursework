# demo-website

CMPT 470 tech evaluation demo website; Team Red


## Getting Started 

These are notes for our team, not the markers, on how to easily start running a server and testing.


### Project Structure

The project follows the suggested structure outlined [here](http://exploreflask.com/en/latest/organizing.html#package).

### Prerequisites

#### Mac or Ubuntu

Just run the following script:
```
./setup.sh
```

#### Windows

1. Follow the instructions for installing MongoDB [here](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/)
2. Enter the repository directory and run `pip install -r requirements.txt`

### Running the App

Ensure a mongodb instance is running (e.g. on Linux and Mac):
```
sudo mongod
```


To run the app, enter the repository directory and start the flask server:
```
cd /path/to/demo-website
FLASK_APP=run.py flask run
```

By default, the Flask app will run on `http://127.0.0.1:5000/`. 

