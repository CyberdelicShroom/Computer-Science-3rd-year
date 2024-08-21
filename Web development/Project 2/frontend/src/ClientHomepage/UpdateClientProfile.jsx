import React, { useState } from "react";
import { Formik, Field } from "formik";
import Layout from "../auth/Layout";
import * as Yup from 'yup';
import { useNavigate } from 'react-router-dom';
import Navbar from "../components/navbar/Navbar";
import Axios from 'axios';

const EditProfile = () => {
    return <Layout><ProfileEditForm /></Layout>;
};

const ProfileEditForm = () => {
    const [signUp_info, setSignUp_info] = useState();
    const [file, setFile] = useState();
    let navigate = useNavigate();

    async function updateUserInfo(values) {
        if(file != null){
            const data = new FormData();
            data.append("file_img", file);
            let filename = file.name;
            Axios.put("api/user/update", {
                username: values.username,
                email: values.email,
                password: values.password,
                image: filename
            })
            .then((res) => {
                if(res.data.status != "success"){
                    console.log("unsuccessful")
                    setSignUp_info(res.data.details);
                } else {
                    console.log(res.data);
                    Axios.post('/upload', data).then(res => console.log({"Image uploaded (updated)": res}));
                    navigate("/client_homepage");
                }
            })
            .catch(error => console.log(error));
        } else {
            Axios.put("api/user/update", {
                username: values.username,
                email: values.email,
                password: values.password,
                image: ""
            })
            .then((res) => {
                if(res.data.status != "success"){
                    console.log("unsuccessful")
                    setSignUp_info(res.data.details);
                } else {
                    console.log(res.data);
                    navigate("/client_homepage");
                }
            })
            .catch(error => console.log("Error", error));
        }
        
    }

    const DisplayingErrorMessagesSchema = Yup.object().shape({
        password: Yup.string().required('Password required'),
    });
    return (
        <><Navbar isDev />
        <Formik
            initialValues={{ username: "", email: "", password: "" }}
            validationSchema={DisplayingErrorMessagesSchema}
            onSubmit={(values) => {
                updateUserInfo(values);
            } }
        >{props => (<form onSubmit={props.handleSubmit} style={{ display: "grid", justifyContent: "center", gap: "0.9rem" }}>
            <p style={{ color: "rosybrown", textAlign: "center" }}>{signUp_info}</p>
            <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Fill in the information you wish you change:</label>
            <Field name="username" placeholder="Username" />
            {props.touched.username && props.errors.username && <div>{props.errors.username}</div>}
            <Field name="email" placeholder="Email" />
            {props.touched.email && props.errors.email && <div>{props.errors.email}</div>}

            <div>

                <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Update Avatar:</label>
                <input style={{ marginBottom: "1rem" }}
                    type="file"
                    name="avatar"
                    onChange={event => {
                        const file = event.target.files[0];
                        setFile(file);
                    } } />
            </div>
            <label style={{ marginBottom: "1rem", marginTop: "1rem" }}>Enter your password to accept changes:</label>
            <Field name="password" placeholder="Password" type="password" />
            {props.touched.password && props.errors.password && <div>{props.errors.password}</div>}
            <button type="submit">Apply changes</button>
        </form>)}
        </Formik></>
    );
};

export default EditProfile;