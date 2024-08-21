import React, { useState } from "react";
import { Formik, Field } from "formik";
import Layout from "./Layout";
import * as Yup from 'yup';
import { useNavigate } from 'react-router-dom';
import Axios from 'axios';

const ClientSignup = () => {
    return <Layout><SignupForm /></Layout>;
};

const SignupForm = () => {
    const [signUp_info, setSignUp_info] = useState();
    const [file, setFile] = useState();
    let navigate = useNavigate();

    async function getUserInfo(values){

        let filename = "";
        if(file !== null){
            const data = new FormData();
            data.append("file_img", file);
            filename = file.name;
            Axios.post('api/user/register', {
                username: values.username,
                email: values.email,
                password: values.password,
                image: filename
            })
            .then((res) => {
                if(res.data.status !== "success"){
                    console.log("unsuccessful")
                    setSignUp_info(res.data.details);
                } else {
                    console.log(res.data);
                    Axios.post('/upload', data).then(res => console.log({"Image uploaded": res}));
                    navigate("/client_profile");
                }
            })
            .catch(error => console.log(error))
        } else {
            Axios.post('api/user/register', {
                username: values.username,
                email: values.email,
                password: values.password,
                image: filename
            })
            .then((res) => {
                if(res.data.status !== "success"){
                    console.log("unsuccessful")
                    setSignUp_info(res.data.details);
                } else {
                    console.log(res.data);
                    navigate("/client_profile");
                }
            })
            .catch(error => console.log("Error", error))
        }
        
    }

    const DisplayingErrorMessagesSchema = Yup.object().shape({
        username: Yup.string().required('Username required'),
        email: Yup.string().email("Email must be valid").required('Email required'),
        password: Yup.string().required('Password required'),
        verifyPassword: Yup.string().oneOf([Yup.ref('password'), null], 'Passwords must match')

    });
    return (
        <><Formik
            initialValues={{ username: "", email: "", password: "", verifyPassword: ""}}
            validationSchema={DisplayingErrorMessagesSchema}
            onSubmit={(values) => {
                getUserInfo(values);
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
                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Please choose an Avatar:</label>
                <input style={{ marginBottom: "1rem" }}
                    type="file"
                    name="avatar"
                    onChange={event => {
                        const file = event.target.files[0];
                        console.log(file)
                        setFile(file);
                }}/>
            </div>
            
            <button type="submit">Sign Up</button>
        </form>)}
        </Formik></>
    );
};

export default ClientSignup;
