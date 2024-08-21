import React, { useState } from "react";
import { Formik, Field } from "formik";
import Layout from "../auth/Layout";
import * as Yup from 'yup';
import { useNavigate } from 'react-router-dom';

const EditProfile = () => {
    return <Layout><ProfileEditForm /></Layout>;
};

const ProfileEditForm = () => {
    const [signUp_info, setSignUp_info] = useState();
    const [postImage, setPostImage] = useState();
    let navigate = useNavigate();

    async function updateCompanyInfo(values){
        if (postImage != null){
            values.logo = postImage;
        }
        const options = { 
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json;charset=utf-8' 
            }, 
            body: JSON.stringify(values)};
        const response = await fetch("/updateCompProfile", options);
        const data = await response.json();
        if (data.update_status){
            console.log(data);
            navigate("/company_profile");
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
        password: Yup.string().required('Password required'),
    });
    return (
        <><Formik
            initialValues={{ companyname: "", email: "", password: "", companyType: "", logo: ""}}
            validationSchema={DisplayingErrorMessagesSchema}
            onSubmit={(values) => {
                updateCompanyInfo(values);
                //alert(JSON.stringify(values, null, 2));
            }}
        >{props => (<form onSubmit={props.handleSubmit} style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
            <p style={{color: "rosybrown", textAlign: "center"}}>{signUp_info}</p>
            <p style={{color: "blueviolet", textAlign: "center"}}>Fill in the information you wish you change:</p>
            <Field name="companyname" placeholder="Company Name" />
            {props.touched.companyname && props.errors.companyname && <div>{props.errors.companyname}</div>}
            <Field name="email" placeholder="Email" />
            {props.touched.email && props.errors.email && <div>{props.errors.email}</div>}
            
            <div>
                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Update your company's general industry info:</label>
                <div>
                    <textarea
                    id="companyType"
                    name="companyType"
                    onChange={props.handleChange}
                    value={props.values.companyType} />
                </div>

                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Update Logo:</label>
                <input style={{ marginBottom: "1rem" }}
                type="file"
                name="avatar"
                onChange={(res) => handleFileUpload(res)}/>
                <p style={{color: "blueviolet", textAlign: "center"}}>Enter your password to accept changes:</p>
                <Field name="password" type="password" placeholder="Password" />
                {props.touched.password && props.errors.password && <div>{props.errors.password}</div>}
            </div>
            <button type="submit">Apply changes</button>
        </form>)}
        </Formik></>
    );
};

export default EditProfile;