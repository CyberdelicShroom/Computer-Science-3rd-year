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

    async function updateDeveloperInfo(values){
        if (postImage != null){
            values.avatar = postImage;
        }
        const options = { 
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json;charset=utf-8' 
            }, 
            body: JSON.stringify(values)};
        const response = await fetch("/updateDevProfile", options);
        const data = await response.json();
        if (data.update_status){
            console.log(data);
            navigate("/dev_profile");
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
            initialValues={{ username: "", email: "", password: "", workExperience: "", avatar: ""/*, languages: [] */}}
            validationSchema={DisplayingErrorMessagesSchema}
            onSubmit={(values) => {
                updateDeveloperInfo(values);
                //alert(JSON.stringify(values, null, 2));
            }}
        >{props => (<form onSubmit={props.handleSubmit} style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
            <p style={{color: "rosybrown", textAlign: "center"}}>{signUp_info}</p>
            <p style={{color: "blueviolet", textAlign: "center"}}>Fill in the information you wish you change:</p>
            <Field name="username" placeholder="Username" />
            {props.touched.username && props.errors.username && <div>{props.errors.username}</div>}
            <Field name="email" placeholder="Email" />
            {props.touched.email && props.errors.email && <div>{props.errors.email}</div>}
            
            <div>
                {/* <label style={{ marginBottom: "0.6rem" }}>Please select the languages you are proficient in:</label>
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
                </div> */}
                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Update your experience:</label>
                <textarea
                    id="workExperience"
                    name="workExperience"
                    onChange={props.handleChange}
                    value={props.values.workExperience} />
                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Update Avatar:</label>
                <input style={{ marginBottom: "1rem" }}
                type="file"
                name="avatar"
                onChange={(res) => handleFileUpload(res)}/>
            </div>
            <p style={{color: "blueviolet", textAlign: "center"}}>Enter your password to accept changes:</p>
            <Field name="password" placeholder="Password" type="password" />
            {props.touched.password && props.errors.password && <div>{props.errors.password}</div>}
            <button type="submit">Apply changes</button>
        </form>)}
        </Formik></>
    );
};

export default EditProfile;