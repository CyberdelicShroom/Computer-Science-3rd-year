import React, { useState, setState } from "react";
import { Formik, Field } from "formik";
import Layout from "./Layout";
import * as Yup from 'yup';
import { useNavigate } from 'react-router-dom';

const ClientSignup = () => {
    return <Layout><SignupForm /></Layout>;
};

const SignupForm = () => {
    const [signUp_info, setSignUp_info] = useState();
    const [postImage, setPostImage] = useState();
    let navigate = useNavigate();

    async function getDeveloperInfo(values){
        if (postImage != null){
            values.avatar = postImage;
        } else {
            values.avatar = "None";
        }
        
        const options = { 
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            }, 
            body: JSON.stringify(values)};
        const response = await fetch("/devSignup", options);
        const data = await response.json();
        
        if (data.registration_status){
            console.log(data);
            navigate("/dev_homepage");
        } else {
            setSignUp_info(data.details);
            console.error(data);
        }
    }
    
    const convertToBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const fileReader = new FileReader();
            fileReader.readAsDataURL(file);
            fileReader.onload = () => {
                resolve(fileReader.result);
            };
            fileReader.onerror = (error) => {
                reject(error);
            };
        });
    };
    const handleFileUpload = async (res) => {
        const file = res.target.files[0];
        const base64 = await convertToBase64(file);
        setPostImage(base64);
    };
    
    const DisplayingErrorMessagesSchema = Yup.object().shape({
        username: Yup.string().required('Username required'),
        email: Yup.string().email("Email must be valid").required('Email required'),
        password: Yup.string().required('Password required'),
        verifyPassword: Yup.string().oneOf([Yup.ref('password'), null], 'Passwords must match')

    });
    return (
        <><Formik
            initialValues={{ username: "", email: "", password: "", workExperience: "", verifyPassword: "", avatar: "", languages: [] }}
            validationSchema={DisplayingErrorMessagesSchema}
            onSubmit={(values) => {
                getDeveloperInfo(values);
                //alert(JSON.stringify(values, null, 2));
            }}
        >{props => (<form onSubmit={props.handleSubmit} style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
            <p style={{color: "rosybrown", textAlign: "center"}}>{signUp_info}</p>
            <Field name="username" placeholder="Username" />
            {props.touched.username && props.errors.username && <div>{props.errors.username}</div>}
            <Field name="email" placeholder="Email" />
            {props.touched.email && props.errors.email && <div>{props.errors.email}</div>}
            <Field name="password" placeholder="Password" type="password" />
            {props.touched.password && props.errors.password && <div>{props.errors.password}</div>}
            <Field name="verifyPassword" placeholder="Verify Password" type="password" />
            {props.touched.verifyPassword && props.errors.verifyPassword && <div>{props.errors.verifyPassword}</div>}
            <div>
                <label style={{ marginBottom: "0.6rem" }}>Please select the languages you are proficient in:</label>
                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr" }}>
                    <label><Field type="checkbox" name="languages" value="Java" />Java</label>
                    <label><Field type="checkbox" name="languages" value="C/C++" />C/C++</label>
                    <label><Field type="checkbox" name="languages" value="Python" />Python</label>
                    <label><Field type="checkbox" name="languages" value="Ruby" />Ruby</label>
                    <label><Field type="checkbox" name="languages" value="Javascript" />Javascript</label>
                    <label><Field type="checkbox" name="languages" value="HTML" />HTML</label>
                    <label><Field type="checkbox" name="languages" value="CSS" />CSS</label>
                    <label><Field type="checkbox" name="languages" value="Typescript" />Typescript</label>
                    <label><Field type="checkbox" name="languages" value="Kotlin" />Kotlin</label>
                    <label><Field type="checkbox" name="languages" value="Go" />Go</label>
                    <label><Field type="checkbox" name="languages" value="PHP" />PHP</label>
                    <label><Field type="checkbox" name="languages" value="Perl" />Perl</label>
                    <label><Field type="checkbox" name="languages" value="COBOL" />COBOL</label>
                    <label><Field type="checkbox" name="languages" value="Swift" />Swift</label>
                </div>
                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Enter your experience:</label>
                <textarea
                    id="workExperience"
                    name="workExperience"
                    onChange={props.handleChange}
                    value={props.values.workExperience} />
                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Please choose an Avatar:</label>
                <input style={{ marginBottom: "1rem" }}
                type="file"
                name="avatar"
                onChange={(res) => handleFileUpload(res)}/>
            </div>
            <button type="submit">Sign Up</button>
        </form>)}
        </Formik></>
    );
};

export default ClientSignup;
