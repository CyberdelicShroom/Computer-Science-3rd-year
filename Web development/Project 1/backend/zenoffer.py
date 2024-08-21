# from crypt import methods
# from re import A
import os
from flask import Flask, render_template, request, redirect, url_for, session
from datetime import timedelta
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from sqlalchemy import ForeignKey
from flask_bcrypt import Bcrypt
from werkzeug.utils import secure_filename

app = Flask(__name__)
app.secret_key = "EldenRing"
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///ZenOffer.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.permanent_session_lifetime = timedelta(minutes=10)

CORS(app)
database = SQLAlchemy(app)
bcrypt = Bcrypt(app)

class developer(database.Model):
    __tablename__='developer'
    id = database.Column("id", database.Integer, primary_key=True)
    username = database.Column(database.String(30), nullable=False)
    email = database.Column(database.String(100), unique=True, nullable=False)
    languages = database.relationship('language', backref='developer', lazy=True)
    password = database.Column(database.String(60), nullable=False)
    experience = database.Column(database.String(100), nullable=False)
    avatarBase64 = database.Column(database.String(1000000))

    def __init__(self, username, email, experience, password, avatarBase64):
        self.username = username
        self.email = email
        self.experience = experience
        self.avatarBase64 = avatarBase64
        self.password = password

class language(database.Model):
    __tablename__='developer languages'
    id = database.Column("id", database.Integer, primary_key=True)
    dev_id = database.Column(database.Integer, ForeignKey('developer.id'), nullable=False)
    language = database.Column(database.String(30), nullable=False)

    def __init__(self, dev_id, language):
        self.dev_id = dev_id
        self.language = language

class company(database.Model):
    __tablename__='company'
    id = database.Column("id", database.Integer, primary_key=True)
    companyname = database.Column(database.String(30), unique=True, nullable=False)
    email = database.Column(database.String(60), unique=True, nullable=False)
    password = database.Column(database.String(60))
    type = database.Column(database.String(100), nullable=False)
    logoBase64 = database.Column(database.String(1000000))

    def __init__(self, companyname, type, email, password, logoBase64):
        self.companyname = companyname
        self.type = type
        self.email = email
        self.password = password
        self.logoBase64 = logoBase64

# @app.route('/uploadImg',methods=['POST','GET'])
# def uploadImg():
#     #avatar = request.form['avatar']
#     response = request.json
#     avatar = response['avatar']

@app.route('/displayAvatar', methods=['POST','GET'])
def displayAvatar():
    dev_id = session["dev_id"]
    dev = developer.query.filter_by(id=dev_id).first()
    return dev.avatarBase64

@app.route('/displayLogo', methods=['POST','GET'])
def displayLogo():
    comp_id = session["comp_id"]
    comp = company.query.filter_by(id=comp_id).first()
    return comp.logoBase64

@app.route('/devSignup',methods=['POST','GET'])
def register_developer():
    response = request.json
    username = response['username']
    email = response['email']
    password = response['password']
    dev_langs = response['languages']
    experience = response['workExperience']
    avatarBase64 = response['avatar']
    
    user_exists = developer.query.filter_by(email=email, username=username).first()

    if not user_exists:
        hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')
        
        dev = developer(username, email, experience, hashed_password, avatarBase64)
        database.session.add(dev)
        database.session.commit()
        dev_id = int(dev.id)

        session["dev_id"] = dev_id

        for i in range(len(dev_langs)):
            dev_lang = language(dev_id, dev_langs[i])
            database.session.add(dev_lang)
        database.session.commit()

        print(f"Registered new user '{dev.username}' to database, email: {dev.email}\n")
        print(f"Password:\n{dev.password}\n")
        print("Known programming languages:")
        for entry in dev.languages:
            print(f"{entry.language}")
        
        print(f"\nWork experience:\n{dev.experience}")
        
        return {"registration_status" : True}
    else:
        register_status = {"registration_status" : False, "details":errors["DevAlreadyExists"]}
        return register_status

@app.route('/devSignin',methods=['POST','GET'])
def developer_signin():
    if request.method == "POST":
        session.permanent = True
        response = request.json
        email = response["email"]
        password = response['password']

        user_exists = developer.query.filter_by(email=email).first()

        if not user_exists:
            login_status = {"login_status": False, "details":errors["DevDoesNotExist"]}
            return login_status
        else:
            dev_hashed_pwd = user_exists.password
            if bcrypt.check_password_hash(dev_hashed_pwd, password):
                print(f"{user_exists.username} logged in!")
                login_status = {"login_status": True}
                session["dev_id"] = user_exists.id
                return login_status
            else:
                login_status = {"login_status": False, "details":errors["PasswordIncorrect"]}
                return login_status
    # else:
    #     if "dev" in session:
    #         return redirect(url_for("dev"))
    #     return render_template("login.html")

@app.route("/companySignUp",methods=['POST','GET'])
def register_company():
    response = request.json
    companyname = response['companyname']
    email = response['email']
    type = response['companyType']
    password = response['password']
    logoBase64 = response['logo']

    user_exists = company.query.filter_by(email=email).first()
    if not user_exists:
        
        hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')

        comp = company(companyname, type, email, hashed_password, logoBase64)
        database.session.add(comp)
        database.session.commit()

        session["comp_id"] = comp.id

        print(f"Registered new company '{comp.companyname}' to database, email: {comp.email}\n")
        print(f"Password:\n{comp.password}\n")
        print(f"Company type:\n{comp.type}")

        return {"registration_status" : True}
    else:
        register_status = {"registration_status" : False, "details":errors["CompanyAlreadyExists"]}
        return register_status

@app.route("/companySignIn",methods=['POST','GET'])
def company_signin():
    session.permanent = True
    response = request.json
    # companyname = response['companyname']
    email = response['email']
    password = response['password']

    user_exists = company.query.filter_by(email=email).first()

    if not user_exists:
        login_status = {"login_status": False, "details":errors["CompanyDoesNotExist"]}
        return login_status
    else:
        company_pwd = user_exists.password
        if(bcrypt.check_password_hash(company_pwd, password)):
            session["comp_id"] = user_exists.id
            print(f"{user_exists.companyname} logged in!")

            login_status = {"login_status": True}
            return login_status
        else:
            login_status = {"login_status": False, "details":errors["PasswordIncorrect"]}
            return login_status


errors = {
    "DevAlreadyExists": "Could not register, this developer is already registered.",
    "CompanyAlreadyExists":"Could not register, this company is already registered.",
    "DevDoesNotExist": "Developer is not registered",
    "CompanyDoesNotExist": "Company is not registered",
    "PasswordIncorrect": "Password incorrect, please try again...",
    "UserCouldNotLogout": "User did not get removed from session, likely because the user did not join a session to begin with or session timed out",
    "UserNotInSession": "User not in session, likely because you are not logged in or got logged out."
}

@app.route("/logout",methods=['POST','GET'])
def logout():
    if "comp_id" in session:
        comp_id = session["comp_id"]
        comp = company.query.filter_by(id=comp_id).first()
        session.pop("comp_id", None)
        print(comp.companyname + " logged out!")
        return {"logout_status": True}
    elif "dev_id" in session:
        dev_id = session["dev_id"]
        dev = developer.query.filter_by(id=dev_id).first()
        session.pop("dev_id", None)
        print(dev.username + " logged out!")
        return {"logout_status": True}
    else:
        return {"logout_status": False,
        "details":errors["UserCouldNotLogout"]}

@app.route("/deleteProfile", methods=['DELETE'])
def delete_profile():
    if "dev_id" in session:
        dev_id = session["dev_id"]
        dev = developer.query.filter_by(id=dev_id).first()
        username = dev.username
        for lang in dev.languages:
            database.session.delete(lang)
        
        developer.query.filter_by(id=dev_id).delete()
        database.session.commit()

        print(f"{username}'s profile has been deleted.")
        return username
    elif "comp_id" in session:
        comp_id = session["comp_id"]
        comp = company.query.filter_by(id=comp_id).first()
        name = comp.companyname

        company.query.filter_by(id=comp_id).delete()
        database.session.commit()

        print(f"{name}'s profile has been deleted.")
        return name

@app.route("/updateCompProfile", methods=['PUT'])
def update_company_profile():
    if "comp_id" in session:
        comp_id = session["comp_id"]
        comp = company.query.filter_by(id=comp_id).first()
        response = request.json
        password = response["password"]

        if response["companyname"] != "":
            user_exists = company.query.filter_by(companyname=response["companyname"]).first()
            if user_exists:
                update_status = {"update_status": False, "details":errors["CompanyAlreadyExists"]}
                return update_status
            comp.companyname = response["companyname"]
        if response["email"] != "":
            user_exists = company.query.filter_by(email=response["email"]).first()
            if user_exists:
                update_status = {"update_status": False, "details":errors["CompanyAlreadyExists"]}
                return update_status
            comp.email = response["email"]
        if response["companyType"] != "":
            comp.type = response["companyType"]
        if response["logo"] != "":
            comp.logoBase64 = response["logo"]
            print(response["logo"])

        if response["companyname"] == "" and response["email"] == "" and response["companyType"] == "" and response["logo"] == "":
            update_status = {"update_status": False, "details":"No changes were made"}
            return update_status

        comp_pwd = comp.password
        if(bcrypt.check_password_hash(comp_pwd, password)):
            database.session.commit()
            print(f"company id: {comp_id}, profile has been updated.")
            update_status = {"update_status": True}
            return update_status
        else:
            update_status = {"update_status": False, "details":errors["PasswordIncorrect"]}
            return update_status
        
@app.route("/updateDevProfile", methods=['PUT'])
def update_dev_profile():
    if "dev_id" in session:
        dev_id = session["dev_id"]
        dev = developer.query.filter_by(id=dev_id).first()
        response = request.json
        password = response["password"]
        
        if response["username"] != "":
            user_exists = developer.query.filter_by(username=response["username"]).first()
            if user_exists:
                update_status = {"update_status": False, "details":errors["DevAlreadyExists"]}
                return update_status
            dev.username = response["username"]
        if response["email"] != "":
            user_exists = developer.query.filter_by(email=response["email"]).first()
            if user_exists:
                update_status = {"update_status": False, "details":errors["DevAlreadyExists"]}
                return update_status
            dev.email = response["email"]
        if response["workExperience"] != "":
            dev.experience = response["workExperience"]
        if response["avatar"] != "":
            dev.avatarBase64 = response["avatar"]

        if response["username"] == "" and response["email"] == "" and response["workExperience"] == "" and response["avatar"] == "":
            update_status = {"update_status": False, "details":"No changes were made"}
            return update_status

        dev_pwd = dev.password
        if(bcrypt.check_password_hash(dev_pwd, password)):
            database.session.commit()
            print(f"developer id: {dev_id}, profile has been updated.")
            update_status = {"update_status": True}
            return update_status
        else:
            update_status = {"update_status": False, "details":errors["PasswordIncorrect"]}
            return update_status

@app.route("/getCompanyInfo", methods=['POST', 'GET'])
def postCompanyInfo():
    if "comp_id" in session:
        comp_id = session["comp_id"]
        comp = company.query.filter_by(id=comp_id).first()
        name = comp.companyname
        type = comp.type
        email = comp.email
        json = {"get_company_info": True, "name": name, "type": type, "email": email}
        return json
    else:
        json = {"get_company_info": False, "details":errors["UserNotInSession"]}
        return json

@app.route("/getDevInfo", methods=['POST', 'GET'])
def postDevInfo():
    if "dev_id" in session:
        dev_id = session["dev_id"]
        dev = developer.query.filter_by(id=dev_id).first()
        username = dev.username
        email = dev.email
        experience = dev.experience
        json = {"get_dev_info": True, "username": username, "email": email, "experience": experience}
        return json
    else:
        json = {"get_dev_info": False, "details":errors["UserNotInSession"]}
        return json

if __name__ == "__main__":
    database.create_all()
    app.run(port=5000, debug=True)
