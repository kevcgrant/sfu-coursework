"""
Routes and views for the flask application.
"""

from datetime import datetime
from flask import request, flash, url_for, redirect, render_template, session, escape
from Demo import app, mongo
from Demo.models import User
import Demo.database as database
import Demo.blocks as blocks

@app.route('/')
@app.route('/home')
def home():
    """Renders the home page."""
    return render_template(
        'index.html',
        show_header=True
    )

@app.route('/debugger')
def debugger():
    """Renders the debugger page."""
    return render_template(
        'debugger.html'
    )

@app.route('/test-debugger')
def test_debugger():  
    """Executes an intentionally invalid statement to demonstrate the Flask debugger.""" 
    bad = 5 + 'hello'
    flash('Issue resolved!')
    return render_template(
        'debugger.html'
    )

@app.route('/databases')
def databases():
    """Renders the users page."""
    return render_template(
        'databases.html',
        users = User.query.all(),
        mongoUsers = mongo.db.users.find(),
        sql_table = blocks.SQL_TABLE,
        mongo_list = blocks.MONGO_LIST,
        route_block = blocks.DATABASES_ROUTE,
        database_init = blocks.DATABASES_INIT,
        user_model = blocks.USER_MODEL
    )

@app.route('/usernew', methods = ['GET', 'POST'])
def new():
    """Renders the new user page."""
    if request.method == 'POST':
        if not request.form['username'] or not request.form['email']:
            flash('Please input all fields', 'error')
        else:
            username = request.form['username']
            email = request.form['email']
            database.create_user(username, email)
            flash('Record was successfully added')
            return redirect(url_for('databases'))
    return render_template(
        'user_new.html',
        route=blocks.NEW_USER_ROUTE,
        template=blocks.NEW_USER_TEMPLATE
    )

@app.route('/session')
def user_session():
    if 'username' in session:
        return redirect(url_for('logout'))
    else:
        return redirect(url_for('login'))

@app.route('/login', methods=['GET', 'POST'])
def login():
    login_error=False
    if request.method == 'POST':
        username = request.form['username']
        user = User.query.filter_by(username=username).first()
        if not user:
            flash('User does not exist')
            login_error=True
        else:
            session['username'] = username
            flash('Logged in successfully')
            return redirect(url_for('logout'))
            
    return render_template(
        'login.html',
        login_error=login_error,
        route_block=blocks.LOGIN
    )

@app.route('/logout', methods=['GET', 'POST'])
def logout():
    if request.method == 'POST':
        session.pop('username', None)
        
        flash('Logged out successfully')
        return redirect(url_for('login'))
    else:
        username = escape(session['username'])
        return render_template(
            "logout.html",
            username=username,
            block=blocks.LOGOUT)


