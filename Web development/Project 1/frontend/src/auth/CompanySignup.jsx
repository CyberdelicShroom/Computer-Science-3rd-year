import React, { useState } from "react";
import { Formik, Field } from "formik";
import Layout from "./Layout";
import * as Yup from 'yup';
import { useNavigate } from 'react-router-dom';

const CompanySignup = () => {
    return <Layout><SignupForm /></Layout>;
};

const SignupForm = () => {
    const [signUp_info, setSignUp_info] = useState();
    const [postImage, setPostImage] = useState();
    let navigate = useNavigate();

    async function getCompanyInfo(values){
        if (postImage != null){
            values.logo = postImage;
        } else {
            values.logo = "None";
        }
        
        const options = { 
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8' 
            }, 
            body: JSON.stringify(values)};
        const response = await fetch("/companySignUp", options);
        const data = await response.json();
        if (data.registration_status){
            console.log(data);
            navigate("/company_profile");
            // navigate('/company_homepage');
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
        companyname: Yup.string().required('Company Name required'),
        email: Yup.string().required('Email required'),
        password: Yup.string().required('Password required'),
        verifyPassword: Yup.string().oneOf([Yup.ref('password'), null], 'Passwords must match')
    });
    return (
        <><Formik
            initialValues={{ companyname: "", email: "", password: "", verifyPassword: "", companyType: "", logo: ""}}
            validationSchema={DisplayingErrorMessagesSchema}
            onSubmit={(values) => {
                getCompanyInfo(values);
                //alert(JSON.stringify(values, null, 2));
            }}
        >{props => (<form onSubmit={props.handleSubmit} style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
            <p style={{color: "rosybrown", textAlign: "center"}}>{signUp_info}</p>
            <Field name="companyname" placeholder="Company Name" />
            {props.touched.companyname && props.errors.companyname && <div>{props.errors.companyname}</div>}
            <Field name="email" placeholder="Email" />
            {props.touched.email && props.errors.email && <div>{props.errors.email}</div>}
            <Field name="password" type="password" placeholder="Password" />
            {props.touched.password && props.errors.password && <div>{props.errors.password}</div>}
            <Field name="verifyPassword" type="password" placeholder="Verify Password" />
            {props.touched.verifyPassword && props.errors.verifyPassword && <div>{props.errors.verifyPassword}</div>}
            <div>
                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Please enter your company's general industry:</label>
                <div>
                    <textarea
                    id="companyType"
                    name="companyType"
                    onChange={props.handleChange}
                    value={props.values.companyType} />
                </div>

                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Please choose a Logo:</label>
                
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

export default CompanySignup;
